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
 * $Id: JPedalPDFViewer.java,v 1.2 2011-04-18 12:27:19 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:19 $
 */ 
// source: 

package nl.uva.vlet.gui.pedal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.HyperLinkListener;
import nl.uva.vlet.gui.image.ImagePane;
import nl.uva.vlet.gui.viewers.ViewerEvent;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.vrl.VRL;

import org.jpedal.PdfDecoder;

/** 
 * 
 *Pedal PDF Viewer. 
 * 
 * @author Piter T. de Boer
 *
 */
public class JPedalPDFViewer extends ViewerPlugin implements HyperLinkListener, 
	ActionListener,	Printable
{
	// === Class Stuff === 
	private static final long serialVersionUID = 1231233422343143789L;

	private static final String PAGE_WIDTH = "Page width";

	private static final String PAGE_HEIGHT = "Page heigth";

	/** The mimetypes i can view */
	private static String mimeTypes[] =
	{ 
		"application/pdf", // 
	};

	static void Debug(String str)
	{
		Global.debugPrintln("PedalPDFViewer", str);
	}
	
    
	public static float zoomList[]={0.25f,0.33f,0.5f,0.75f,1f,1.25f,1.5f,1.75f,2.0f,2.5f,3.0f,4.0f,6.0f,8.0f};
	

	public class ResizeWatcher implements ComponentListener
	{
		private JPedalPDFViewer viewer;

		public ResizeWatcher(JPedalPDFViewer viewer)
		{
			this.viewer=viewer;
		}

		public void componentHidden(ComponentEvent e) {}
		public void componentMoved(ComponentEvent e) {}
		public void componentShown(ComponentEvent e){}
		
		public void componentResized(ComponentEvent e)
		{
			viewer.notifyResize(); 
		}
	}

	// ========================================================================
	// instance 
	// ========================================================================
	private int zoomIndex=5; 
	
	// private boolean muststop = false;

	private PdfDecoder pdfDecoder;

	private JPanel toolPanel;

	private JButton pageUpBut;
	private JButton pageDownBut;

	int currentPage=1;

	private JToolBar toolBar;

	private float currentScaling=zoomList[zoomIndex];

	private JTextField versionField;

	private JTextField pageField;

	private JButton pageUp10But;

	private JButton pageDown10But;

	private JButton pageEndBut;

	private JButton pageBeginBut;

	private JScrollPane scrollPane;

	private JComboBox zoomBox;

	private JTextField pageCountField;

	private boolean fitWidth=false;// =true doesn't work at startup! 

	private boolean fitHeight=false;

	/**
	 * 
	 */
	public void initGui()
	{
		{
			this.setLayout(new BorderLayout()); // JPanel
			this.setPreferredSize(new Dimension(800,600)); 
			{
				toolPanel=new JPanel(); 
				toolPanel.setLayout(new FlowLayout()); 

				add(toolPanel,BorderLayout.NORTH);

				{
					toolBar=new JToolBar(); 
					toolPanel.add(toolBar); 
					{
						JLabel label=new JLabel("Navigate:"); 
						toolBar.add(label);
					}
					{
						pageBeginBut=new JButton("|<-");
						toolBar.add(pageBeginBut);
						pageBeginBut.addActionListener(this);
						pageBeginBut.setToolTipText("Go to first page");
					}
					{
						pageUp10But=new JButton("<<");
						toolBar.add(pageUp10But);
						pageUp10But.addActionListener(this);
						pageUp10But.setToolTipText("Go back 10 pages"); 
					}
					{
						pageUpBut=new JButton("<");
						toolBar.add(pageUpBut);
						pageUpBut.addActionListener(this); 
						pageUpBut.setToolTipText("Previous page"); 
					}
					{
						pageDownBut=new JButton(">");
						toolBar.add(pageDownBut);
						pageDownBut.addActionListener(this); 
						pageDownBut.setToolTipText("Next page"); 
					}
					{
						pageDown10But=new JButton(">>");
						toolBar.add(pageDown10But);
						pageDown10But.addActionListener(this); 
						pageDownBut.setToolTipText("Go 10 pages forward"); 
					}
					{
						pageEndBut=new JButton("->|");
						toolBar.add(pageEndBut);
						pageEndBut.addActionListener(this);
						pageEndBut.setToolTipText("Go to last page");
					}
					{
						JLabel label=new JLabel(" Page:"); 
						toolBar.add(label);
					}
					
					{
						pageField=new JTextField("000"); 
						toolBar.add(pageField);
						pageField.addActionListener(this);  
					}
					{
						JLabel label=new JLabel(" of:"); 
						toolBar.add(label);
					}
					{
						pageCountField=new JTextField("000"); 
						toolBar.add(pageCountField);
						pageCountField.setEditable(false);   
					}
					{
						JLabel zoomLabel=new JLabel(" Zoom:"); 
						toolBar.add(zoomLabel);
					}
					{
						 zoomBox=new JComboBox();
			             toolBar.add(zoomBox);
			             // add Float objects + 2 string objects
						 for (Float f:zoomList)
		                    zoomBox.addItem(new Float(f));
						 zoomBox.addItem(PAGE_WIDTH); 
						 zoomBox.addItem(PAGE_HEIGHT); 
			             zoomBox.setSelectedIndex(zoomIndex);
 
			             zoomBox.addActionListener(this);
					}
					{
						JLabel versionLabel=new JLabel("PDF version:"); 
						toolBar.add(versionLabel);
					}
					{
						versionField=new JTextField("0.0"); 
						toolBar.add(versionField);
						versionField.setEditable(false); 
					}
				}
			}

			{
				scrollPane = new JScrollPane(); 
				this.add(scrollPane,BorderLayout.CENTER);
				scrollPane.addComponentListener(new ResizeWatcher(this)); 
				// pdfDecoder is a JPanel():
				{
					pdfDecoder=new PdfDecoder(); 
					scrollPane.setViewportView(pdfDecoder); 
				}
				
				scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			}


		}

	}

	/** htmlPane is embedded in a scrollpane */

	public boolean haveOwnScrollPane()
	{
		return true;
	}

	public String getVersion()
    {
        return Global.getVersion()+" Internal ViewerPlugin";  
    }
    
    public String getAbout()
    {
        return "<html><body>Internal ViewerPlugin</body><.html>"; 
    }

	/**
	 * @param location
	 * @throws VlException 
	 */
	public void updateLocation(VRL vrl) 
	{
		
		if (vrl == null)
		{
			Global.errorPrintln(this,"Null location:"+this); 
			return;
		}

		this.setVRL(vrl);
		setBusy(true);

		try
		{
			String uristr=vrl.toURIString();
			loadFile(uristr);
		}


		// catch (VlExecption) -> Let startview handle it 
		//catch (VlException e)
		//{
		//	handle(e);  
		//} 
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
	
	

	private void loadFile(String uristr) throws Exception
	{
		// these 2 lines opens page 1 at 100% scaling
		pdfDecoder.openPdfFileFromURL(uristr);
		pdfDecoder.setPageParameters(this.currentScaling,this.currentPage);
		pdfDecoder.decodePage(currentPage);
		
		versionField.setText(pdfDecoder.getPDFVersion());
		pageCountField.setText(""+pdfDecoder.getPageCount());
		pdfDecoder.invalidate();
	
		revalidate(); 
		repaint();
	}

	public static void viewStandAlone(VRL loc)
	{
		JPedalPDFViewer tv = new JPedalPDFViewer();

		try
		{
		    tv.addHyperLinkListener(tv);
			tv.startAsStandAloneApplication(loc);
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
		//this.muststop = true;
		this.pdfDecoder.stopDecoding();
		this.pdfDecoder.closePdfFile();
	}

	@Override
	public void disposeViewer()
	{
		this.pdfDecoder.stopDecoding();
		this.pdfDecoder.closePdfFile();
	}

	@Override
    public void initViewer()
	{
		initGui();
		// Recieve my own events when I'm in standalone mode:
		if (this.getViewStandalone())
			this.addHyperLinkListener(this);

		// if NOT standalone, the MasterBrowser (VBrowser) will 
		// handle links events ! 
	}

	@Override
	public String getName()
	{
		return "jPedalViewer";
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
				//IViewer viewer = event.getViewer();

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



	public void actionPerformed(ActionEvent e)
	{
		Component source=(Component)e.getSource();

		try
		{
			if (source==this.pageField)
			{		
				String str=pageField.getText(); 
				int p=new Integer(str); 
				setPage(p); 
			}
			else if (source==this.pageBeginBut)
			{		
				setPage(1);
			}
			else if (source==this.pageUp10But)
			{		
				if (currentPage>10)
				{
					setPage(currentPage-10);
				}
			}
			else if (source==this.pageUpBut)
			{		
				if (currentPage>1)
				{
					setPage(currentPage-1);
				}
			}
			else if (source==this.pageDownBut)
			{
				if (currentPage+1<=pdfDecoder.getPageCount())
				{
					setPage(currentPage+1);
				}
			}
			else if (source==this.pageDown10But)
			{
				if (currentPage+10<=pdfDecoder.getPageCount())
				{
					setPage(currentPage+10);
				}
			}
			else if (source==this.pageEndBut)
			{
				setPage(pdfDecoder.getPageCount()); 
			}
			else if (source==this.zoomBox)
			{
				Object val=zoomBox.getSelectedItem();
				System.err.println("zoom="+val); 
				
				if (val instanceof Float)
				//if (zoomIndex+1<zoomList.length)
					setScaling((Float)val,false,false); 
				else if (val instanceof String)
				{
					String strval=(String)val;
					// object compare! 
					if (strval==PAGE_WIDTH) 
						setScaling(0,true,false); 
					else if (strval==PAGE_HEIGHT) 
						setScaling(0,false,true); 
					 
				}
				
			}
		}
		catch (Exception ex)
		{
			handle(ex);
		}

	}


	private void setPage(int p) throws Exception
	{
		if ((p<1) || p> pdfDecoder.getPageCount())
		{
			Debug("Illegal page nr:"+p);
			return;
		}
		this.currentPage=p;
		pdfDecoder.decodePage(p);
		pageField.setText(""+p);
		this.revalidate();
		repaint();
	}

	private void setScaling(float f,boolean fitWidth,boolean fitHeight)
	{
		// This DOES NOT work if the page hasn't been rendered yet/.
		
		Dimension curSize=this.pdfDecoder.getPreferredSize(); 
		Dimension viewSize = this.scrollPane.getViewport().getSize(); 
		Debug("preferred size="+curSize); 
		Debug("view port size="+viewSize);
		this.fitWidth=fitWidth; 
		this.fitHeight=fitHeight; 
		
		// adjust to relative size 
		if (fitWidth==true)
			this.currentScaling*=(float)viewSize.width/(float)curSize.width;
		else if (fitHeight==true)
			this.currentScaling*=(float)viewSize.height/(float)curSize.height;
		else
			this.currentScaling=f; 
		
		f=Math.round(currentScaling*100)/100f;  
		
		// this.currentScaling=f; 
		//this.zoomField.setText("zoom:"+f+"x"); 
		pdfDecoder.setPageParameters(this.currentScaling,this.currentPage);
		pdfDecoder.invalidate();
		
		
		scrollPane.getVerticalScrollBar().setUnitIncrement((int)(16*f));
		
		 
		revalidate(); 
		repaint();
	}

	/**
	 * @param args
	 */
	public static void main(String args[])
	{
		Global.setDebug(true);

		try
		{

			{
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				JPedalPDFViewer v = new JPedalPDFViewer();
				// HTML viewer is it's own masterbrowser. 
				v.addHyperLinkListener(v);

				frame.add(v);
				v.initViewer();
				frame.add(v);

				// v.htmlPane.setPreferredSize(v.getViewExtentSize());

                v.initViewer();
                
				frame.setVisible(true);
				
				//v.updateLocation(new VRL("file:///home/ptdeboer/pdf/paper1.pdf")); 
				v.startViewer(new VRL("file:/opt/vlet/doc/UserGuide.pdf")); 

				try
				{
					// v.updateLocation(new VRL("http://www.piter.nl/index.html"));
					Thread.sleep(4000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				java.awt.Image image=v.getScreenShot();
//				JFrame imageFrame=new JFrame(); 
//				imageFrame.add(new ImagePane(image)); 
//				imageFrame.pack();
//				imageFrame.setVisible(true); 
//				
			}

			// viewStandAlone(new VRL(
			// "file:///home/ptdeboer/workspace/mbuild/dist/doc/api/index-all.html"));

			/*
			 viewStandAlone(new VRL("http://www.piter.nl/index.html"));

			 viewStandAlone(new VRL("file://localhost/home/ptdeboer/vfs2/test21Nov05_results.feat/report.html"));
			 **/
			

		}
		catch (VlException e)
		{
			System.out.println("***Error: Exception:" + e);
			e.printStackTrace();
		}
		
	}

	public void notifyResize()
	{
		Debug("Resize!"); 
		
		// update scaling!
		if ((this.fitWidth==true) || (this.fitHeight==true))
			this.setScaling(0,fitWidth, fitHeight);
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
	{
		return this.pdfDecoder.print(graphics,pageFormat,pageIndex); 
	}
	
	public Vector<ActionMenuMapping> getActionMappings()
	{
		ActionMenuMapping mapping=new ActionMenuMapping("viewPDF", "View PDF");
		// '/' is not a RE character
		mapping.addMimeTypeMapping(Pattern.compile("application/pdf"));
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
		mappings.add(mapping); 
		return mappings; 
	}
	
	public void doMethod(String methodName, ActionContext actionContext) throws VlException
	{
		
		if (actionContext.getSource()!=null)
			this.updateLocation(actionContext.getSource());
		
	}

}
