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
 * $Id: CobraHTMLViewer.java,v 1.3 2011-04-18 12:27:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:34 $
 */ 
// source: 

package nl.uva.vlet.gui.cobraviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.HyperLinkListener;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.viewers.IMimeViewer;
import nl.uva.vlet.gui.viewers.ViewerEvent;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;

import org.lobobrowser.html.domimpl.HTMLDocumentImpl;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.xml.sax.InputSource;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/** 
 * First version of Cobra HTML parser/renderer.  
 * 
 * @author Piter T. de Boer
 *
 */
public class CobraHTMLViewer extends ViewerPlugin implements HyperLinkListener, MouseListener
{
	/**
	 * Timer: refresh the view every x secondes.
	 * x is fixed on the top of this page  
	 * 
	 * @author Kamel
	 *
	 */
	public class Refresher
	{

		Timer t;
		String linkToUpdate;

		public Refresher(int delay, String uri)
		{
			t = new Timer();
			t.schedule(new MonAction(), 0, delay);
			linkToUpdate = uri;
		}
			
		class MonAction extends TimerTask
		{

			public void run()
			{
				try
				{
					System.out.println("hello refresh"+linkToUpdate);
					updateLocation(linkToUpdate);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
					t.cancel();
				}
			}
		}

	}
	
	// ======================================================================= 
	// Class Stuff 
	// ======================================================================= 
		
	private class LocalHtmlRendererContext extends
			SimpleHtmlRendererContext
	{
		CobraHTMLViewer viewer=null;

		
		// Override methods here to implement browser functionality
		public LocalHtmlRendererContext(CobraHTMLViewer viewer,HtmlPanel contextComponent)
		{
			super(contextComponent);
			this.viewer=viewer;
		}
		
		@Override
		public void navigate(URL url,String ref)
		{
			Debug("navigate url="+url);
			Debug("navigate ref="+ref);
	
			VRL vrl;
			
			try
			{
				vrl = new VRL(url);
				
				// ref= Frame Name ! (Not #target, etc); 
				//if (ref!=null)
				//		vrl=vrl.resolve(ref);
				
				Debug("navigate:"+url+",ref="+ref); 
				
				// Either I or the MasterBrowser will receice this event. 
				
				fireHyperLinkEvent(ViewerEvent.createHyperLinkEvent(viewer,vrl));
 
			}
			catch (Exception e)
			{
				handle(e); 
			}
		}
		
		@Override
		public void error(String msg)
		{
			Global.errorPrintln(this,"error:"+msg); 
		}
		
		@Override
		public void error(String msg,Throwable e)
		{
			Global.errorPrintln(this,"error:"+msg);
			Global.errorPrintln(this,"exception:"+e);
		}
		@Override
		public void warn(String msg)
		{
			Global.warnPrintln(this,"warn:"+msg); 
		}
		
		@Override
		public void alert(String msg)
		{
		    Global.errorPrintln(this,"alert:"+msg); 
			
		}
		@Override
		public void warn(String msg,Throwable e)
		{
		    Global.warnPrintln(this,"warn:"+msg+"\nException ="+e); 
		}
		
	}
	
	// ======================================================================= 
	// Class Stuff 
	// ======================================================================= 
	
	private static final long serialVersionUID = -8949752327943143789L;

	/** The mimetypes i can view */
	private static String mimeTypes[] =
	{ 
		"text/html", // 
	};

	static void Debug(String str)
	{
		Global.debugPrintln("VHTMLViewer", str);
	}
	
	static 
	{
		// shutup default loggers from Cobra toolkit: 
		LogManager man = LogManager.getLogManager(); 
		Logger root = man.getLogger("");
		root.setLevel(Level.SEVERE);
	
	}
	
	// ========================================================================
	// instance 
	// ========================================================================

	VNode vnode = null;

	HtmlPanel htmlPanel = null;

	private boolean muststop = false;

	private LocalHtmlRendererContext rendererContext;
	private JButton simpleBut;
	private JPanel topPanel;

	DocumentBuilderImpl builder = null;

	VRL prevVRL=null;

	private CobraHTMLViewer.Refresher refreshAction; 
	
	/**
	 * 
	 */
	public void initGui()
	{
		{
			/*JFrame frame=new JFrame();
			 frame.setSize(new Dimension(800,600));
			 JPanel panel=new JPanel(); 
			 frame.add(panel);*/
			//panel.add(scrollPane);
			// I am a JPanel 
			this.setLayout(new BorderLayout()); // JPanel
			{
				topPanel = new JPanel();
				FlowLayout topPanelLayout = new FlowLayout();
				topPanel.setLayout(topPanelLayout);
				this.add(topPanel, BorderLayout.NORTH);
				topPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				{
					//simpleBut = new JButton();
					//topPanel.add(simpleBut);
					//simpleBut.setText("but");
				}
			}

			
			// htmlPane 
			{
				htmlPanel = new HtmlPanel();
				rendererContext = new LocalHtmlRendererContext(this,htmlPanel);
				builder = new DocumentBuilderImpl(rendererContext
						.getUserAgentContext(), rendererContext);

				add(htmlPanel,BorderLayout.CENTER);
				htmlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				htmlPanel.addMouseListener(this); 
			}
		}
	}

	/** htmlPane is embedded in a scrollpane */

	public boolean haveOwnScrollPane()
	{
		return true;
	}

	/**
	 * @param location
	 * @throws VlException 
	 */
	public void updateLocation(VRL vrl) 
	{
		// ARRGGG
		if (htmlPanel == null)
		{
			Global.errorPrintln(this, "Viewer not initialized!:"+this);
			return;
		}

		if (vrl == null)
		{
			Global.errorPrintln(this,"Null location:"+this); 
			return;
		}

		this.setVRL(vrl);

		// this.setSize(this.getViewExtentSize());
		
		
	
		setBusy(true);

		try
		{
			
			URL url = vrl.toURL();
			String uristr=vrl.toURIString();
		
			
			//
			// Extra code added by Kamel Boulebiar: 
			// add Refresher when there is a Refresh tag in the meta header. 
			//
			String cont = UIGlobal.getResourceLoader().getText(vrl);

							
					
				// if it is a new page we cancel the timer anyway or the refresher
				// isn't running. 
				if  ((refreshAction==null) 
					 || ((prevVRL!=null) && (prevVRL.compareTo(vrl) != 0))
					)
				{
					// refesher might not be running. 
					stopRefresher();
					
					// retrigger the timer in the case of html page with refresh tag
					Integer delay = HTMLMetaTagParser.parseHTML_MetaContent(cont);
					if (delay != null)
					{
						startRefresher(delay,uristr); 
					}
				}
				
				prevVRL = vrl;
				
			//
			// Patch: Cobra doens't resolve links from directories correctly 
			// If no extension is given: this is probably a directory, 
			// add '/' to trigger correct subdirectory references!
			//
			
			if (vrl.getBasename(true).compareTo(vrl.getBasename(false))==0)
					uristr=uristr+"/";
			
			URLConnection connection = null;
			InputStream in = null;
			connection = url.openConnection();
			in = connection.getInputStream();
		
			
			// A Reader should be created with the correct charset,
			// which may be obtained from the Content-Type header
			// of an HTTP response.
			Reader reader = new InputStreamReader(in);

			// InputSourceImpl constructor with URI recommended
			// so the renderer can resolve page component URLs.
			InputSource is = new InputSourceImpl(reader, uristr);

			// Set a preferred width for the HtmlPanel,
			// which will allow getPreferredSize() to
			// be calculated according to block content.
			// We do this here to illustrate the 
			// feature, but is generally not
			// recommended for performance reasons.
			//htmlPanel.setPreferredWidth(800);

			// create document but do not parse yet:
			HTMLDocumentImpl document =(HTMLDocumentImpl) builder.createDocument(is);
			// Set the document in the HtmlPanel. This
			// is what lets the document render.
			
			htmlPanel.setDocument(document, rendererContext);
			
			Debug("new document url="+document.getURL()); 
			
			// Does actual incremental loading/parsing: 
			document.load(); 
			in.close();
			
			// request redraw:
			
			this.revalidate();
		}
		
		// catch (VlExecption) -> Let startview handle it 
		catch (VlException e)
		{
			handle(e);  
		} 
		catch (Exception e)
		{
			handle(e); 
		}
		finally
		{
			setBusy(false);
		}
		
		setBusy(false);
	}

	private void startRefresher(Integer delay, String uristr)
	{
		stopRefresher(); 
		//refreshAction = new Refresher(delay, uristr);
	}

	private void stopRefresher()
	{
		if (refreshAction != null)
		{
			refreshAction.t.cancel();
			refreshAction.t.purge();
			refreshAction = null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String args[])
	{
		// Global.setDebug(true);

		try
		{

			// viewStandAlone(new VRL(
			// "file:///home/ptdeboer/workspace/mbuild/dist/doc/api/index-all.html"));
			 
			 viewStandAlone(new VRL("file:///home/ptdeboer/stuff/bug200/report.html"));
			 
			//if (false)  
			{
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				CobraHTMLViewer v = new CobraHTMLViewer();
				// HTML viewer is it's own masterbrowser. 
				v.addHyperLinkListener(v);

				frame.add(v);
				v.initViewer();
				frame.add(v);

				// v.htmlPane.setPreferredSize(v.getViewExtentSize());

				frame.setVisible(true);

				v.updateLocation(new VRL("http://www.piter.nl/")); 
				
				Debug("init::htmlPane.getSize()          ="
						+ v.htmlPanel.getSize());
				Debug("init::htmlPane.getPreferredSize() ="
						+ v.htmlPanel.getPreferredSize());

				// v.updateLocation(new VRL("http://www.piter.nl/index.html"));
			}
			
			
				
			 /*
			 viewStandAlone(new VRL("http://www.piter.nl/index.html"));
			 
			 viewStandAlone(new VRL("file://localhost/home/ptdeboer/vfs2/test21Nov05_results.feat/report.html"));
			 **/

		}
		catch (VlException e)
		{
			System.err.println("***Error: Exception:" + e);
			e.printStackTrace();
		}

	}

	public static void viewStandAlone(VRL loc)
	{
		CobraHTMLViewer tv = new CobraHTMLViewer();
	

		try
		{
			tv.startAsStandAloneApplication(loc);
			tv.addHyperLinkListener(tv);
		}
		catch (VlException e)
		{
			System.out.println("***Error: Exception:" + e);
			e.printStackTrace();
		}
	}

	@Override
	public String[] getMimeTypes()
	{
		return mimeTypes;
	}
	
	@Override
	public void stopViewer()
	{
		this.muststop = true;
		System.out.println("STOP");
		stopRefresher();
	}
	
	
	
	

	@Override
	public void disposeViewer()
	{
		this.htmlPanel = null;
		System.out.println("DISPOSE");
		stopRefresher();
	}

	@Override
    public void initViewer()
	{
		initGui();
		// Recieve my own events when I'm in standalone mode:
		if (this.getViewStandalone())
			this.addHyperLinkListener(this);
			this.setPreferredSize(new java.awt.Dimension(681, 485));

		// if NOT standalone, the MasterBrowser (VBrowser) will 
		// handle links events ! 
	}

	@Override
	public String getName()
	{
		return "CobraViewer";
	}

	// From MasterBrowser interface: 

	public void notifyHyperLinkEvent(ViewerEvent event)
	{
		switch (event.type)
		{
			// hyperlink event from viewer: update viewed location  
			case HYPER_LINK_EVENT:
				// create action command:
				// ActionCommand cmd=new ActionCommand(ActionCommandType.SELECTIONCLICK); 
				// performAction(event.location,cmd);
			    IMimeViewer viewer = event.getViewer();

				try
				{
					setVRL(location);
					updateLocation(event.location);
				}
				catch (Exception e)
				{
					handle(e);
				}
				break;
			default:
				break;
		}
	}

	public String toString()
	{
		return "" + getName() + " viewing:" + this.getVRL();
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}
	/**
	* This method should return an instance of this class which does 
	* NOT initialize it's GUI elements. This method is ONLY required by
	* Jigloo if the superclass of this class is abstract or non-public. It 
	* is not needed in any other situation.
	 */
	public static Object getGUIBuilderInstance() 
	{
		return new CobraHTMLViewer(Boolean.FALSE);
	}
	
	/**
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public CobraHTMLViewer(Boolean initGUI) 
	{
		super();
		
		if (initGUI) 
			initViewer(); 
	}

	public CobraHTMLViewer()
	{
		super(); 
	}
}
