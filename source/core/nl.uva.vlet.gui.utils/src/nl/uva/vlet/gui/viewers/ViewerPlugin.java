/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: ViewerPlugin.java,v 1.12 2011-06-21 10:35:41 ptdeboer Exp $  
 * $Date: 2011-06-21 10:35:41 $
 */
// source: 

package nl.uva.vlet.gui.viewers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.ResourceTypeMismatchException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.HyperLinkListener;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.util.imageutil.ImageCapturer;
import nl.uva.vlet.util.ResourceLoader;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;

//import vls.gui.ActionTask;
//import vls.gui.BrowserController;

/**
 * Super class of all (custom) Viewers for the VBrowser. The ViewerPlugin class
 * is defined outside VBrowser so that custom 3rd party Viewers can be created
 * (plugins). Default the viewer is a JPanel embedded in a JScrollpane which
 * could either be part of the browser tab or part of a stand alone frame.
 * <p>
 * In the latter case the Swing hierarchy is:<br>
 * 
 * <pre>
 * -JFrame - JScrollPane // (haveOwnScrollPane()==false)
 *         - ViewerPlugin // (Subclass of JPanel)
 * 
 * </pre>
 * 
 * Override method haveOwnScrollPane() to control whether this viewer should be
 * embedded in a JScrollPane or not if the Viewer has it's own scrollPane.
 * 
 * @author P.T. de Boer
 */
public abstract class ViewerPlugin extends JPanel implements IMimeViewer
{
    private static final long serialVersionUID = 6820035983193568339L;

    // ========================================================================
    // Private Fields
    // ========================================================================

    private Vector<HyperLinkListener> linkListeners = new Vector<HyperLinkListener>();

    private ViewerInfo viewerInfo;

    private ViewContext viewContext;

    // ========================================================================
    // Protected Fields
    // ========================================================================
    protected String textEncoding = "UTF-8";

    protected VRL location = null;

    protected Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);

    protected Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    private ResourceLoader resourceLoader;

    // ========================================================================
    // Instance
    // ========================================================================

    /**
     * Default public constructor.
     * <p>
     * For the subclasses: leave implementation empty, but allow the
     * construction of an dummy Viewer which the Registry will use to call
     * attribute methods like getName(), getMimetypes(), isTool(), etc. to
     * proper configure the ViewerRegistry.
     */
    public ViewerPlugin()
    {
        // Default constructor.
        // Needed to instantiate a dummy object so
        // ViewerRegistry can invoke getMimeTypes().
    }

    /**
     * Get the Viewer Context which contains information about how the viewer
     * was started.
     */
    public final ViewContext getViewContext()
    {
        return this.viewContext;
    }

    /**
     * Set the Viewer Context which contains information about how the viewer
     * was started. Is called before actually starting the viewer.
     */
    public void setViewContext(ViewContext context)
    {
        this.viewContext = context;
    }

    // ========================================================================
    // Final methods
    // ========================================================================

    /** Returns VRL of viewed Location */
    final public VRL getVRL()
    {
        return location;
    }

    /**
     * Returns URL of viewed location
     * 
     * @throws VlException
     */
    final public URL getURL() throws VlException
    {
        return location.toURL();
    }

    /**
     * Sets currently viewed location. Does not do any updates.
     * <p>
     * For getVNode() and getInputStream()/getOutputStream() to work correctly,
     * a setVRL() has to be done first, before calling one of those methods.
     * 
     */
    final public void setVRL(VRL location)
    {
        this.location = location;
    }

    /** @deprecated use isStandAlone */
    final public boolean getViewStandalone()
    {
        return isStandalone();
    }

    /**
     * Whether the viewer is a stand alone frame and not embedded in the
     * MasterBrowser. if isStandAlone()==true the method getJFrame() will return
     * the Viewer Frame. Method can be called during or after initViewer().
     */
    final public boolean isStandalone()
    {
        if (this.getViewContext() == null)
            return true; // no view context -> start alone

        return this.getViewContext().getStartInStandaloneWindow();
    }

    /** Sets mouse pointer to busy or not busy. */
    final public void setBusy(boolean busy)
    {
        if (busy)
            this.setCursor(busyCursor);
        else
            this.setCursor(defaultCursor);
    }

    /**
     * Returns parent JFrame if contained in one. Might return NULL if parent is
     * not a JFrame! use getTopLevelAncestor() to get the (AWT) toplevel
     * component.
     * 
     * @see javax.swing.JComponent#getTopLevelAncestor()
     * @return the containing JFrame or null.
     */
    final public JFrame getJFrame()
    {
        if (this.getViewContext() == null)
        {
            ClassLogger.getLogger(ViewerPlugin.class).errorPrintf("fixme:NULL ViewContext\n");
            return null;
        }

        // Do not return frame when embedded in VBrowser !!!
        if (this.getViewContext().getStartInStandaloneWindow() == false)
            return null;

        Container topcomp = this.getTopLevelAncestor();
        if (topcomp instanceof Frame)
            return ((JFrame) topcomp);

        return null;
    }

    final protected boolean hasJFrame()
    {
        return (this.getJFrame() != null);
    }

    /** Call this method to dispose the standalone JFrame if is has one */
    final protected boolean disposeJFrame()
    {
        if (hasJFrame())
        {
            this.getJFrame().dispose();
            return true;
        }

        return false;
    }

    /**
     * VBrowser internal: set ViewerInfo object from VBrowser viewer registry.
     * This object does not contain the startup context. Use setViewerContext()
     * for that.
     */
    final public void setViewerInfo(ViewerInfo info)
    {
        this.viewerInfo = info;
    }

    /**
     * VBrowser internal: Returns ViewerInfo object from VBrowser viewer
     * registry. This object does not contain the startup context. Use
     * getViewerContext() for that.
     */
    final public ViewerInfo getViewerInfo()
    {
        return viewerInfo;
    }

    final public void addHyperLinkListener(HyperLinkListener listener)
    {
        synchronized (this.linkListeners)
        {
            // don't duplicate listeners:
            if (this.linkListeners.contains(listener) == false)
                this.linkListeners.add(listener);
            else
                Global.warnPrintf(this, "Warning: HyperlinkListener already added:%s\n", listener);
        }
    }

    final public void removeHyperLinkListener(HyperLinkListener listener)
    {
        synchronized (this.linkListeners)
        {
            this.linkListeners.remove(listener);
        }
    }

    /**
     * Returns array of hyperlink listeners.
     */
    final protected HyperLinkListener[] getHyperLinkListeners()
    {
        synchronized (this.linkListeners)
        {
            HyperLinkListener listeners[] = new HyperLinkListener[linkListeners.size()];
            listeners = linkListeners.toArray(listeners);
            return listeners;
        }
    }

    // ========================================================================
    // Other methods
    // ========================================================================

    /** Perform Dynamic Action Method */
    public void doMethod(String methodName, ActionContext actionContext) throws VlException
    {
        throw new nl.uva.vlet.exception.NotImplementedException("Action method not implemented by Viewer:" + methodName);
    }

    /**
     * If this Viewer can be used as standalone tool, thus without an (default)
     * URL and/or associated mimetype, let this method return 'true'. <br>
     * The VBrowser will add this viewer to the 'Tools' Menu so that users can
     * start this viewer manually.
     * <p>
     * In that case, startViewer() is called with the VRL which the VBrowser
     * currently is viewing. No mimetype checking is done. New: the
     * getClassification() will be used to place the tool in a menu.
     * 
     * @see #getClassification()
     * @return
     */
    public boolean isTool()
    {
        return false;
    }

    /**
     * This method is called by the VBrowser to check whether this viewer wants
     * to be started in a standalone window, or can be embedded in the VBrowser
     * panel.
     * <p>
     * Return true if this viewer always needs to be started in a new window.
     * Return false (=default) for don't care/let the VBrowser choose.
     */

    public boolean getAlwaysStartStandalone()
    {
        return false;
    }

    /**
     * Returns name space or package declaration for this viewer/tool. This part
     * is prepended to the name to form the fully qualified Viewer name. For
     * example name=MyViewer and classificiation=mypackage the full name of this
     * viewer would be = mypackage.MyViewer. This classification might be used
     * by GUI components.
     * 
     * @see #isTool()
     * @see #getName()
     * @return Classification of this viewer.
     */
    public String getClassification()
    {
        return "";// default no classficiation.
    }

    /** Returns VNode of viewedlocation */
    protected VNode getVNode() throws VlException
    {
        return getVNode(this.location);
    }

    /** Returns VNode specified location */
    protected VNode getVNode(VRL vrl) throws VlException
    {
        // use shared UI environment:
        return UIGlobal.getVRSContext().openLocation(vrl);
    }

    public JPanel getViewComponent()
    {
        return this;
    }

    /**
     * Preferably use this method to add childs to the Viewer Panel. Future
     * implements of ViewerPlugin might have a different Component layout.
     */
    public Component addToRootPane(Component comp)
    {
        // For JApplet: return this.getRootPane().add(comp);
        return this.add(comp);
    }

    /**
     * Preferably use this method to add childs to the Viewer Panel. Future
     * implements of IViewer might have a different Component layout.
     */
    public void addToRootPane(Component comp, Object constraints)
    {
        // this.getRootPane().add(comp,constraints);
        this.add(comp, constraints);
    }

    /** Update frame size to preferred size of all components */
    public void requestFrameResizeToPreferred()
    {
        if (getJFrame() != null)
            this.requestFrameResize(getJFrame().getPreferredSize());
    }

    /**
     * Request a resize of the Parent JPanel or JFrame Will also update the
     * parent JFrame if the Viewer is embeded in it's own frame. This is a
     * convenience method for subclasses, when manipulating the gui outside the
     * main GUI Event thread. Calls the SwingUtilites invokeLater method.
     */
    public void requestFrameResize(final Dimension preferredSize)
    {
        final ViewerPlugin viewer = this;

        if (getJFrame() == null)
            return;

        Runnable notifySizeChange = new Runnable()
        {
            public void run()
            {
                // Update Parent Frame (if viewer has one) !
                viewer.setFrameSize(preferredSize);
                getJFrame().validate(); // validate all components
            }
        };

        SwingUtilities.invokeLater(notifySizeChange);
    }

    /**
     * Directly sets the viewer size and the parent Frame if it has one
     */
    public void setFrameSize(Dimension preferredSize)
    {
        this.setSize(preferredSize);

        // invoke parent:
        Container parent = this.getParent();

        if (parent != null)
        {
            parent.getParent().validate();
        }

        if (this.getJFrame() != null)
        {
            // let frame calculate it's preferredSize and
            // update the viewer frame !
            this.getJFrame().validate();
            getJFrame().setSize(getJFrame().getPreferredSize());
        }
    }

    /** Returns mimetype of viewed resource */
    public String getMimeType() throws VlException
    {
        VNode node = getVNode();
        return node.getMimeType();
    }

    /** Returns inputstream to viewed resource */
    public InputStream getInputStream() throws VlException
    {
        VNode node = getVNode();

        if (node instanceof VStreamReadable)
            return ((VStreamReadable) node).getInputStream();
        else
            throw new ResourceTypeMismatchException("VRL is not streamreadable:" + location);
    }

    /** returns outputstream to viewed resource */
    public OutputStream getOutputStream() throws VlException
    {
        VNode node = getVNode();

        if (node instanceof VStreamWritable)
            return ((VStreamWritable) node).getOutputStream();
        else
            throw new ResourceTypeMismatchException("VRL is not stream writable:" + location);
    }

    /**
     * Returns true if this viewer has it's own scrollpane. When false, the
     * ViewrePanel might embed this viewer in a scrollpanel. Override this
     * method and return true if you have your own scrollpanel. or do not want
     * to be embedded in a scrollPane.
     */
    public boolean haveOwnScrollPane()
    {
        return false;
    }

    /**
     * Default Exception handler, notifies the task source, which spawn this
     * viewer. (VBrowser).
     */
    protected void handle(Exception e)
    {
        ViewContext ctx = getViewContext();

        if ((ctx != null) && (ctx.getTaskSource() != null))
        {
            ctx.getTaskSource().handle(e);
        }
        else
        {
            Global.errorPrintf(getName(), "Exception:%s\n", e);
            Global.errorPrintStacktrace(e);

            if ((ctx == null) || (ctx.getShowErrors()))
                ExceptionForm.show(e);
        }
    }

    /**
     * Notify Master Browser (for example the VBrowser) that a link event has
     * happened so it can update it's state. Use this to update the ResourceTree
     * and 'browsed' location in the VBrowser.
     * 
     * @param event
     */
    public void fireHyperLinkEvent(ViewerEvent event)
    {
        for (HyperLinkListener linkl : this.getHyperLinkListeners())
        {
            if (event.isConsumed() == false)
            {
                linkl.notifyHyperLinkEvent(event);
            }
        }
    }

    /**
     * Simple uncached(!) method to get the binary contents of the current
     * resource. Uses getResourceLoader() to read contents.
     * 
     * @throws VlException
     */
    protected byte[] getContents() throws VlException
    {
        return this.getResourceLoader().getBytes(this.getVRL());
    }

    /**
     * When embedded in a ScrollPane, this method will return the actual size
     * which is visble
     */
    public Dimension getViewPortSize()
    {
        Container cont = this.getParent();
        if (cont instanceof javax.swing.JViewport)
        {
            Dimension size = ((javax.swing.JViewport) cont).getSize();
            return size;

        }
        else
        {
            return null;
        }
    }

    /** Set title of master frame or Viewer tab */
    public void setViewerTitle(final String name)
    {
        JFrame frame = getJFrame();
        if (frame != null)
            getJFrame().setTitle(name);

        this.setName(name);
    }

    /**
     * Call updateLocation(VRL)
     * 
     * @see #updateLocation(VRL)
     */
    public void updateLocation(String loc) throws VlException
    {
        updateLocation(new VRL(loc));
    }

    public String toString()
    {
        return "" + getName() + ":" + this.getVRL();
    }

    /**
     * Default "startViewer" without dynamic action method and context. Invokes
     * {@link #startViewer(VRL, String, ActionContext)}. Override note: override
     * this method for non dynamic action viewers.
     * 
     * @see #startViewer(VRL, String, ActionContext)
     */
    public void startViewer(VRL location) throws VlException
    {
        startViewer(location, null, null);
    }

    /**
     * Start Viewer when a <strong>Dynamic Action</strong> is performed. The
     * methods starts the viewer in separate thread. This is NOT the main GUI
     * Event thread, so be careful when manipulating Swing/AWT objects. This
     * method is called after a initViewer().
     * <p>
     * After a stopViewer() this method <i>may</i> be called again.<br>
     * Current order of methods called is: <br>
     * <ul>
     * <li>initViewer() - Initialize Viewer and GUI elements called
     * <i>during</i> Swings Event Thread.
     * <li>startViewer() - Actual start of the viewer in seperate thread.
     * <li>multiple updateLocation() invocations if applicable.
     * <li>stopViewer() - Stop viewing, after which another startViewer
     * <i>may</i> occur.
     * <li>disposeViewer() - Viewer won't be used anymore. Dispose (GUI)
     * resources. </lu>
     * <p>
     * Override note: override this method for dynamic action method viewers. If
     * this viewer was started through a dynamic action optMethodName will
     * contains the actual method name. The ActionContext provides additional
     * information about the selected dynamic action.
     * 
     * @param location
     *            - The actual location to display
     * @param optMethodName
     *            - action name if this viewer was started by a dynamic action.
     * @param actionContext
     *            - optional Dynamic Action Context if applicable.
     * 
     * @throws VlException
     */
    public void startViewer(VRL location, String optMethodName, ActionContext actionContext) throws VlException
    {
        setVRL(location);

        // update location
        if (location != null)
            updateLocation(location);

        // perform action method:
        if (StringUtil.isNonWhiteSpace(optMethodName))
            this.doMethod(optMethodName, actionContext);
    }

    /** Get contents of Viewer in an Image */
    public Image getScreenShot()
    {
        return getScreenShot(null);
    }

    /**
     * Get contents of viewer but render it to the specified size. Size
     * paremeter can null which indicates 'don't care'.
     */
    public Image getScreenShot(Dimension size)
    {
        ImageCapturer capturer = new ImageCapturer(this);
        return capturer.captureContents(size);
    }

    public static void saveProperties(VRL loc, Properties props) throws VlException
    {
        UIGlobal.saveProperties(loc, props);
    }

    /**
     * Custom menu action(s) which will appear in the action menu when right
     * clicking (or alt-mouse clicking) on a resource.
     * <p>
     * If Dynamic Actions are supported override methods
     * {@link #startViewer(VRL, String, ActionContext)} and
     * {@link #doMethod(String, ActionContext)} for full control.
     */
    public Vector<ActionMenuMapping> getActionMappings()
    {
        return null;
    }

    /**
     * Tester method to start this viewer in a standalone context.
     * Implementation might change. Creates a JFrame and embeds this viewer.
     */
    public void startAsStandAloneApplication(VRL location) throws VlException
    {
        if (getViewContext() == null)
        {
            Global.infoPrintf(this, "Warning: Viewer context not set: creating default for: %s\n", this);
            setViewContext(new ViewContext(null));
        }

        // set AFTER creating context !

        getViewContext().setStartInStandaloneWindow(true);
        // update viewer context whith startup VRL
        getViewContext().setStartupLocation(location);
        setVRL(location);

        JFrame frame = new JFrame();
        frame.add(this);
        frame.setSize(800, 600);
        // this.setFrame(frame);
        initViewer();
        frame.pack();
        frame.setVisible(true);
        startViewer(location);

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                Global.infoPrintf(this, "Closing and exitting:%s\n", ViewerPlugin.this);
                System.exit(0);
            }
        });
    }

    /**
     * Fires (VRS) ResourceEvent.
     */
    public void fireEvent(ResourceEvent event)
    {
        UIGlobal.getVRSContext().fireEvent(event);
    }

    /**
     * Returns information about this plugins. Specify Version, Author and other
     * information here.
     */
    public String getInfoText()
    {
        return "No information for:" + this.getName() + " version:" + getVersion()
                + " (Plugin must implement getInfoText()).";
    }

    /**
     * Should return "&lt;Major&gt;.&lt;minor&gt;[.&lt;tiny&gt;]" Version
     * String. Extra version information can be appended after the dotted
     * decimals. For example "1.2.0 Beta Build (C) MyCorp" as long as the
     * Version String starts with dotted separated decimals and the complete
     * string is only one line.
     */
    public String getVersion()
    {
        return "0";
    }

    /**
     * Return HTML formatted information about this plugin.
     */
    public String getAbout()
    {
        return "<html>" + "<table>" + "  <tr bgcolor=#c0c0c0><td><h3>Viewer Plugin Information for:" + getName()
                + "</h3></td><td></td></tr>" + "  <tr bgcolor=#f0f0f0><td>ViewerPlugin Version:</td><td>"
                + this.getVersion() + "</td><tr><br>" + "  <tr bgcolor=#f0f0f0><td>ViewerPlugin class:  </td><td>"
                + this.getClass().getName() + "</td><tr><br>"
                + "  <tr bgcolor=#f0f0f0><td>ViewerPlugin mimeTypes:</td><td>"
                + new StringList(this.getMimeTypes()).toString("<br>") + "<br>" + "</table><br>"
                + " To change this information override the getAbout() method. <br>" + "</html>";
    }

    /**
     * Compares mime type with "this" mimetypes. Uses list from getMimeTypes()
     * to compare with.
     */
    public boolean isMyMimeType(String mimeType)
    {
        if (StringUtil.isEmpty(mimeType))
            return false;

        for (String type : this.getMimeTypes())
            if (StringUtil.compare(type, mimeType) == 0)
                return true;

        return false;
    }

    /**
     * Simple link method to negotiate link handling.
     * 
     * Returns 'true' if the link will be handled outside this plugin, returns
     * false is the plugin should handle it self. For example by calling
     * updateLocation()
     * 
     * @param loc
     * @throws VlException
     */
    public boolean handleLink(VRL loc, boolean openNew) throws VlException
    {
        VNode node = getVNode(loc);
        // check for me:
        if (isMyMimeType(node.getMimeType()) == false)
        {
            if (isStandalone() || openNew)
                fireViewEvent(loc, true);
            else
                fireViewEvent(loc, false);

            return true;
        }
        else
        {
            return false;
        }
    }

    public void fireViewEvent(VRL vrl, boolean openNew)
    {
        if (openNew)
        {
            fireHyperLinkEvent(ViewerEvent.createHyperLinkEvent(this, vrl, ViewerEvent.ViewOpenType.OPEN_NEW));
        }
        else
        {
            fireHyperLinkEvent(ViewerEvent.createHyperLinkEvent(this, vrl));
        }
    }

    public void fireLinkFollowedEvent(VRL vrl)
    {
        fireHyperLinkEvent(ViewerEvent.createLinkFollowedEvent(this, vrl));
    }

    public void fireFrameLinkFollowedEvent(VRL docVrl, VRL link)
    {
        fireHyperLinkEvent(ViewerEvent.createFrameLinkFollowedEvent(this, docVrl, link));
    }

    /**
     * Call this one to exit viewer. If this viewer is a stand alone viewer, the
     * (J)FRame will be closed.
     */
    public void exitViewer()
    {
        this.disposeJFrame();
    }

    /** Returns ResourceLoader for this ViewPlugins classloader context */
    public ResourceLoader getResourceLoader()
    {
        if (this.resourceLoader == null)
            this.resourceLoader = new ResourceLoader(UIGlobal.getVRSContext(), this.getClass().getClassLoader(), null);

        return resourceLoader;
    }

    // ========================================================================
    // Abstract Interface Methods
    // ========================================================================

    /** Returns name of this Viewer which is used in the pop-up menu */
    public abstract String getName();

    /**
     * Returns the mimetypes it can view. Note that this is not a static method.
     * Static methods cannot be abstract !
     */
    public abstract String[] getMimeTypes();

    /**
     * Initialize Viewer. This method is called inside the Main GUI event thread
     * so Swing components can be created in a synchronized way. This method
     * will be called only once during the Viewer's life time. Do not to much
     * heavy resource loading.
     */
    public abstract void initViewer(); // throws VlException;

    /**
     * Informs viewer to update the viewed location. This might be the result of
     * a 'HyperLink' event or the VBrowser might just recycle this viewer
     * without calling disposeViewer/initViewer. Mutiple updateLocations may
     * occur during the life time of the Viewer.
     * <p>
     * Thread handling: this method will always be called in a background
     * thread, so updates to Swing Event must be done using the
     * SwintgUtil.invokeLater() methods.
     */
    public abstract void updateLocation(VRL loc) throws VlException;

    /**
     * When this method is called the viewer should stop all background actions
     * and/or animations etc. It does not mean the viewer will be disposed. The
     * updateLocation() method will not be called again before startViewer()
     * method has been called.
     * <p>
     * However the default implementation of startViewer(VRL) is to call
     * updateLocation(VRL) so after a stopViewer() an updateLocation() may
     * occur. Override both starViewer() and updateLocation() methods for
     * further Viewer Control.
     * <p>
     * Thread handling: This method may be called more then once.
     */
    public abstract void stopViewer();

    /**
     * Method called when really disposing the viewer. After this method the
     * viewer won't be used anymore.
     * <p>
     * To speed up garbage collection please implement this method. In the
     * simplest case, just nullify your (object) fields. This really speeds up
     * garbage collection.
     */
    public abstract void disposeViewer();

}
