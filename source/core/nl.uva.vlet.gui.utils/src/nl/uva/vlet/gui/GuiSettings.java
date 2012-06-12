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
 * $Id: GuiSettings.java,v 1.9 2011-06-07 14:31:59 ptdeboer Exp $  
 * $Date: 2011-06-07 14:31:59 $
 */
// source: 

package nl.uva.vlet.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.font.FontInfo;
import nl.uva.vlet.gui.font.FontUtil;
import nl.uva.vlet.vrl.VRL;

/**
 * Gui Settings. Contains Global settings for the UI Environment.
 */
public class GuiSettings
{
    // =======================================================================
    // Class stuff
    // =======================================================================

    /**
     * Default Gui Settings: will be created upon class initialization !
     */
    private static GuiSettings defaultSettings = null;

    private static int windowCounter = 0;

    /** Gui settings file stored in users .vletrc: */
    public static final String GUISETTINGS_FILE_NAME = "guisettings.prop";

    // === Current hardcoded defaults === //

    private static int maxWindowWidth = 800;
    private static int maxWindowHeight = 600;
    private static boolean autosave = true;

    static
    {
        classInit();
    }

    private static void classInit()
    {
        // explicit call Global init !
        Global.init();
        defaultSettings = getDefault();
        // System.setProperty("swing.aatext","false");
        // System.setProperty("java.awt.RenderingHint","false");
    }

    // ========================================================================
    // Instance
    // ========================================================================

    public Color textfield_non_editable_background_color = new Color(240, 240, 240); 
    public Color textfield_non_editable_foreground_color = Color.BLACK;
    public Color textfield_non_editable_gray_foreground_color = new Color(32, 32, 32);
    public Color textfield_editable_background_color = new Color(255, 255, 255);
    public Color textfield_editable_foreground_color = Color.BLACK;
    public Color label_default_background_color = new Color(255, 255, 255);
    public Color label_selected_background_color = new Color(184, 207, 229);
    public Color label_default_foreground_color = new Color(0, 0, 0);
    public long viewer_file_size_warn_limit = (long) 100 * 1024 * 1024;
    public int max_dialog_text_width = 800;
    public int default_max_iconlabel_width = 100;
    
    // ===
    // Private fields
    // ===

    // private boolean singleClickAction=true;

    private Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);
    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * Property set which contains Gui Settings which can be set&saved by the
     * user
     */
    private Properties guiProperties = new Properties(); // empty property set!

    // === Constructor ===

    protected GuiSettings()
    {
        initProperties();
    }

    /** Class initializer object */
    private void initProperties()
    {
        Properties props = null;
        VRL loc = getGuiSettingsLocation();
        try
        {
            props = GlobalUtil.staticLoadProperties(loc);
        }
        catch (VlException e)
        {
            Global.logException(ClassLogger.WARN, this, e, "Warning. Error when loading guisettings:%s\n", loc);
        }

        // init to defaults:
        if (props != null)
        {
            for (Enumeration<Object> keys = props.keys(); keys.hasMoreElements();)
            {
                String key = (String) keys.nextElement();
                String valStr = props.getProperty(key);

                if (key.startsWith("gui."))
                    key = key.substring("gui.".length());

                GuiPropertyName[] eValues = GuiPropertyName.values();
                GuiPropertyName name = null;

                // check proper name !
                for (GuiPropertyName eValue : eValues)
                {
                    if (eValue.getName().compareTo(key) == 0)
                        name = eValue;
                }

                if (name == null)
                {
                    Global.warnPrintf(this, "Warning: unknown gui property:%s\n", key);
                    continue; // continue for loop
                }

                Global.debugPrintf(this, "Setting GUI property name:%s->%s\n", name, valStr);

                _setProperty(name, valStr, false);
            }
        }
        else
        {
            guiProperties = new Properties(); // empty property set!
        }
        // setMouseSettings();
        // guiProperties=new Properties(props);
    }

    private void _setProperty(GuiPropertyName name, String valstr, boolean save)
    {
        this.guiProperties.setProperty(name.getName(), valstr);

        if (save == true)
        {
            try
            {
                save();
            }
            catch (VlException e)
            {
                Global.logException(ClassLogger.ERROR, this, e, "Could save properties to:%s\n",
                        getGuiSettingsLocation());

                Global.errorPrintStacktrace(e);
            }
        }
    }

    private String _getProperty(GuiPropertyName name)
    {
        Object val = this.guiProperties.get(name.getName());

        if (val == null)
            return name.getDefault();

        return (String) val;
    }

    /**
     * saveProperties will only save properties in the Propery set. They will
     * only appear in the property set if: the property was defined in the user
     * settings file, or it has been changed by the preferences menu
     * 
     * @throws VlException
     */
    private void save() throws VlException
    {
        VRL loc = getGuiSettingsLocation();

        UIGlobal.saveProperties(loc, guiProperties);
    }

    // === Propery Interface ===

    /** Returns named boolean value */
    public boolean getBoolProperty(GuiPropertyName name)
    {
        return new Boolean(defaultSettings._getProperty(name));
    }

    public boolean getBooleanProperty(GuiPropertyName name)
    {
        return new Boolean(defaultSettings._getProperty(name));
    }

    public int getIntProperty(GuiPropertyName name)
    {
        return new Integer(defaultSettings._getProperty(name));
    }

    // setters:

    public void setProperty(GuiPropertyName name, int val)
    {
        _setProperty(name, "" + val, autosave);
    }

    public void setProperty(GuiPropertyName name, boolean val)
    {
        _setProperty(name, "" + val, autosave);
    }

    public void setProperty(GuiPropertyName name, String val)
    {
        _setProperty(name, val, autosave);
    }

    public String getProperty(GuiPropertyName name)
    {
        return _getProperty(name);
    }

    // ========================================================================
    // Class Stuff
    // ========================================================================

    public static enum LookAndFeelType
    {
        NATIVE, DEFAULT, WINDOWS, METAL, GTK, KDEQT, PLASTIC_3D, PLASTIC_XP
    };

    public static VRL getGuiSettingsLocation()
    {
        VRL confLoc = Global.getUserConfigDir();
        return confLoc.appendPath(GUISETTINGS_FILE_NAME);
    }

    /**
     * This method exists because the e.isPopupTrigger() doesn't work under
     * windows
     */
    public static boolean isPopupTrigger(MouseEvent e)
    {
        if (e.isPopupTrigger())
            return true;

        if (e.getButton() == getMousePopupButton())

            return true;

        return false;
    }

    public static void switchLookAndFeelType(String lafstr)
    {
        switchLookAndFeelType(LookAndFeelType.valueOf(lafstr));
    }

    public static void switchLookAndFeelType(LookAndFeelType lafType)
    {
        // Set Look & Feel
        try
        {

            switch (lafType)
            {
                case NATIVE:
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                case DEFAULT:
                case METAL:
                    javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    break;
                case WINDOWS:
                    javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    break;
                case GTK:
                    javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    break;
                case KDEQT:
                    // org.freeasinspeech.kdelaf.KdeLAF
                    break;
                case PLASTIC_3D:
                    javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
                    break;
                case PLASTIC_XP:
                    javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
                    break;
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            Global.errorPrintStacktrace(e);
        }
        // load()/save()
    }

    public static int getMousePopupButton()
    {
        return defaultSettings.getIntProperty(GuiPropertyName.MOUSE_POPUP_BUTTON);
    }

    public static int getMouseAltButton()
    {
        return defaultSettings.getIntProperty(GuiPropertyName.MOUSE_ALT_BUTTON);
    }

    public static int getMouseSelectionButton()
    {
        return defaultSettings.getIntProperty(GuiPropertyName.MOUSE_SELECTION_BUTTON);
    }

    public static int getMouseActionButton()
    {
        return defaultSettings.getIntProperty(GuiPropertyName.MOUSE_SELECTION_BUTTON);
    }

    public static boolean getSingleClickAction()
    {
        return defaultSettings.getBooleanProperty(GuiPropertyName.SINGLE_CLICK_ACTION);
    }

    /**
     * Wrapper to detection 'Action Events' since the PLAF way to detect event
     * doesn't always work. Typically this is a single mouse click or a double
     * mouse click.
     * 
     * @param e
     * @return
     */
    public static boolean isAction(MouseEvent e)
    {
        int mask = e.getModifiersEx();

        if ((mask & MouseEvent.CTRL_DOWN_MASK) > 0)
        {
            // CONTROL DOWN, not an action, but a selection !
            return false;
        }

        if ((mask & MouseEvent.SHIFT_DOWN_MASK) > 0)
        {
            // SHIFT DOWN, not an action, but a selection !
            return false;
        }

        if (e.getButton() != getMouseActionButton())
            return false;

        if (getSingleClickAction() && (e.getClickCount() == 1))
            return true;

        if ((getSingleClickAction() == false) && (e.getClickCount() == 2))
            return true;

        return false;
    }

    public static boolean isSelection(MouseEvent e)
    {
        if (e.getButton() == getMouseSelectionButton())
            return true;
        else
            return false;
    }

    public static Cursor getBusyCursor()
    {
        return defaultSettings.busyCursor;
    }

    public static Cursor getDefaultCursor()
    {
        return defaultSettings.defaultCursor;
    }

    public static Dimension getScreenSize()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.getScreenSize();
    }

    public static Point getScreenCenter()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        return new Point(dim.width / 2, dim.height / 2);
    }

    /** calculate optimal new window size for new Frame/Window/Dialog */
    public static Rectangle getOptimalWindow(Component comp)
    {
        return getOptimalWindow(null, comp);
    }

    /**
     * Calculates optimal window size for the component and optionally uses
     * frame information from the parent frame to position the new component in
     * front of the parent frame
     * 
     * @param parent
     *            optional Parent frame to position component.
     * @param comp
     *            the component to calculate the size.
     */

    public static Rectangle getOptimalWindow(JFrame parentFrame, Component comp)
    {
        Dimension prefSize = comp.getPreferredSize();
        Point center = null;

        // use parent frame ONLY if given AND is visible on the screen.
        // for other cases, use screen center:

        if ((parentFrame != null) && (parentFrame.isVisible() == true))
        {

            center = parentFrame.getLocationOnScreen();
            Dimension size = parentFrame.getSize();
            center.x += size.width / 2;
            center.y += size.height / 2;
        }
        else
        {
            center = getScreenCenter();
        }

        // auto place new windows with a slight ofset to each other !
        windowCounter++;

        center.x += (windowCounter * 3) % 100;
        center.y += (windowCounter * 3) % 110;

        return new Rectangle(center.x - prefSize.width / 2, center.y - prefSize.height / 2, prefSize.width,
                prefSize.height);
    }

    public static void setToOptimalWindowSize(Component comp)
    {
        setToOptimalWindowSize(null, comp);

    }

    /** @see getOptimalWindowSize */
    public static void setToOptimalWindowSize(JFrame parent, Component comp)
    {
        Rectangle windowRec = GuiSettings.getOptimalWindow(parent, comp);
        Dimension size = windowRec.getSize();

        if (size.width > maxWindowWidth)
            size.width = maxWindowWidth;

        if (size.height > maxWindowHeight)
            size.height = maxWindowHeight;

        comp.setSize(size);

        // this.setLocation((int)windowRec.getCenterX(),(int)windowRec.getCenterY());
        comp.setLocation(windowRec.getLocation());

    }

    public static void saveProperties() throws VlException
    {
        defaultSettings.save();
    }

    public static void placeToCenter(Component inst)
    {
        Dimension size = getScreenSize();
        inst.setLocation(size.width / 2 - inst.getWidth() / 2, size.height / 2 - inst.getHeight() / 2);
    }

    /**
     * Return font alias/style Uses FontInfo database to lookup font.
     * 
     * @param name
     * @return
     */
    public static FontInfo getFontInfo(String name)
    {
        return FontInfo.getFontInfo(name);
    }

    /**
     * Return font alias/style Uses FontInfo database to lookup font.
     * 
     * @param name
     * @return
     */
    public static Font getFont(String name)
    {
        return FontUtil.createFont(name);
    }

    public Color getDefaultPanelBGColor()
    {
        return Color.WHITE;
    }

    public Color getDefaultPanelFGColor()
    {
        return Color.BLACK;
    }

    // static Object AA_TEXT_PROPERTY_KEY = new String("AATextPropertyKey");

    // In Java 1.6 this should be done automatically bases upon the Font
    // properties.
    public static void setAntiAliasing(JComponent comp, Boolean useAA)
    {
        // boolean gotAA=false;

        // try
        // {
        // // =================================
        // // Java 1.5 Way:
        // //
        // comp.putClientProperty(com.sun.java.swing.SwingUtilities2.AA_TEXT_PROPERTY_KEY,
        // useAA);
        // // =================================
        //
        // // Use Reflection to get Java 1.5 object to avoid compilation/runtime
        // errors
        // // when using java 1.6(+)
        // Class
        // cls=GuiSettings.class.getClassLoader().loadClass("com.sun.java.swing.SwingUtilities2");
        //
        //
        // if (cls!=null)
        // {
        // Field field = cls.getField("AA_TEXT_PROPERTY_KEY");
        // Object obj=new Object();
        // // get field value:
        // obj=field.get(obj);
        //
        // /* Check:
        // if
        // (obj.equals(com.sun.java.swing.SwingUtilities2.AA_TEXT_PROPERTY_KEY))
        // {
        // System.err.println(">>> Got object!! <<<");
        // }
        // else
        // {
        // System.err.println(">>> Got wrong object :-/ <<<");
        // }
        // */
        // if (obj instanceof StringBuffer)
        // {
        // Global.debugPrintln(GuiSettings.class,"Got StringBuffer object:"+field);
        // //gotAA=true;
        // comp.putClientProperty(obj, useAA);
        // return;
        // }
        // }
        //
        // }
        // catch (Throwable t){};

        // =========================
        // Java. 1.6 stuff:
        // =========================

        // MAP<OBJECT, OBJECT> DESKTOPHINTS=NULL ;
        //
        // IF (DESKTOPHINTS == NULL)
        // {
        // TOOLKIT TK = TOOLKIT.GETDEFAULTTOOLKIT();
        // DESKTOPHINTS = (MAP<OBJECT, OBJECT>)
        // (TK.GETDESKTOPPROPERTY("AWT.FONT.DESKTOPHINTS"));
        // }

        Graphics graph = comp.getGraphics();
        Graphics2D g2d = (Graphics2D) graph;

        if (g2d == null)
            return;
        //
        // if (desktopHints != null)
        // {
        // if (g2d!=null)
        // g2d.addRenderingHints(desktopHints);
        // }

        if (useAA)
        {
            // g2d.setRenderingHint(
            // RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            // );
            // g2d.setRenderingHint(
            // RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY
            // );
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        }
        else
        {
            // g2d.setRenderingHint(
            // RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
            // );
            // g2d.setRenderingHint(
            // RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_DEFAULT);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }

    public static VRL getUserIconsDir()
    {
        VRL vrl = Global.getUserConfigDir().appendPath("icons");
        return vrl;
    }

    public static VRL getInstallationIconsDir()
    {
        VRL vrl = Global.getInstallationLibDir().appendPath("icons");
        return vrl;
    }

    public static synchronized GuiSettings getDefault()
    {
        if (defaultSettings == null)
            defaultSettings = new GuiSettings();

        return defaultSettings;
    }

    public static void setShowResourceTree(boolean val)
    {
        defaultSettings.setProperty(GuiPropertyName.GLOBAL_SHOW_RESOURCE_TREE, val);
    }

    public static boolean isAltMouseButton(MouseEvent e)
    {
        if (e.getButton() == getMouseAltButton())
            return true;

        return false;
    }

}
