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
 * $Id: VHTMLViewer.java,v 1.9 2011-06-24 12:06:13 ptdeboer Exp $  
 * $Date: 2011-06-24 12:06:13 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.gui.vhtml.VHTMLEditorPane;
import nl.uva.vlet.net.ssl.SslUtil;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;

/**
 * The (V)HTML Viewer.
 * See VHTMLEDitorPane for the real implemenation of the HTML viewer. 
 * @see VHTMLEditorPane
 * @author Piter T. de Boer
 */
public class VHTMLViewer extends InternalViewer  implements HyperlinkListener
{
    private static ClassLogger logger;
    
    // === Class Stuff === 
    private static final long serialVersionUID = -8949752327943143789L;

    /** The mimetypes i can view */
    private static String mimeTypes[] =
    { 
        "text/html", // 
    };

    static
    {
        logger=ClassLogger.getLogger(VHTMLViewer.class);
        //logger.setLevelToDebug(); 
    }
   
    // ========================================================================
    // instance 
    // ========================================================================
     

    VHTMLEditorPane htmlPane = null;

    VNode vnode = null;

    //private boolean muststop = false;

    private JScrollPane scrollPane;

    public void initGui()
    {
        {
            /*JFrame frame=new JFrame();
            frame.setSize(new Dimension(800,600));
            JPanel panel=new JPanel(); 
            frame.add(panel);*/
            //panel.add(scrollPane);
            
            // I am a JPanel 
            this.setLayout(new BorderLayout());  // JPanel
            this.setPreferredSize(new Dimension(800,600)); 
            
            // scrollPane 
            {
               scrollPane=new JScrollPane();
               add(scrollPane,BorderLayout.CENTER);
            }
            
            // htmlPane 
            {
               htmlPane = new VHTMLEditorPane();
               // Custom Kit !
               // htmlPane.setEditorKit(new VHTMLKit());
               
               htmlPane.setEditorKit(new HTMLEditorKit());
               
               htmlPane.setEditable(false);
                
               htmlPane.addHyperlinkListener(this);
                   
               // embed the htmlPane in a scrollPane for auto-resize !!!
               scrollPane.setViewportView(htmlPane);
 
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
    public void updateLocation(final VRL location) throws VlException
    {
        if (location == null)
        {
            htmlPane.setContentType("text/plain");
            htmlPane.setText("NULL VRL");
            return;
        }
        
        try
        {
            if (location.hasScheme(VRS.HTTPS_SCHEME))
                SslUtil.installCert(location.getHostname(),location.getPort());
        }
        catch (Exception e1)
        {
            throw new VlException("SSLException","SSLException:"+e1.getMessage(),e1);
        } 
        
    	// ARRGGG
    	if (htmlPane==null)
    	{
    		logger.warnPrintf("Viewer not initialized !\n");
    	}
      
        this.setVRL(location);
       
       // this.setSize(this.getViewExtentSize());
        
        setBusy(true);
        
        try
        {
            try
            {
                //htmlPane.setContentType("text/html");
                htmlPane.setPage(location.toString());
            }
            catch (IOException e)
            {
                handle(new VlIOException(e));  
            }
        }
        
        // catch (VlExecption) -> Let startview handle it 
        finally
        {
            setBusy(false);
        }
    }
  
    public static void viewStandAlone(VRL loc)
    {
        VHTMLViewer tv = new VHTMLViewer();
        //tv.setViewStandalone(true);

        try
        {
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
    public void startViewer(VRL loc) throws VlException
    {
        updateLocation(loc);
    }

    @Override
    public void stopViewer()
    {
        //this.muststop = true;
    }

    @Override
    public void disposeViewer()
    {
        this.htmlPane = null;
    }

    @Override
    public void initViewer()
    {
        initGui();
    }

    @Override
    public String getName()
    {
        return "VHTMLViewer";
    }
    
    // From MasterBrowser interface: 
 
//    public void notifyHyperLinkEvent(ViewerEvent event)
//    {
//        switch (event.type)
//        {
//            // hyperlink event from viewer: update viewed location  
//            case HYPER_LINK_EVENT:
//                // create action command:
//                // ActionCommand cmd=new ActionCommand(ActionCommandType.SELECTIONCLICK); 
//                // performAction(event.location,cmd);
//                //IViewer viewer=event.getViewer();
//                
//                try
//                {
//                    setVRL(location); 
//                    updateLocation(event.location);
//                }
//                catch (VlException e)
//                {
//                    handle(e); 
//                }
//                break;
//            default:
//                break; 
//        }   
//    }

    public String toString()
    {
    	return ""+getName()+" viewing:"+this.getVRL(); 
    }
    
    //Link clicked in HTML panel
    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        logger.debugPrintf("hyperlinkUpdate:%s\n",e); 
        
        try
        {
            boolean frameEvent=false;
            VRL link=null; 
            VRL docVrl=null; 
            
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
                // get HTML Pane and stuff:
                
                VHTMLEditorPane pane = (VHTMLEditorPane) e.getSource();
                HTMLDocument doc = (HTMLDocument) pane.getDocument();
                docVrl=new VRL(doc.getBase()); 
                URL url=e.getURL();
                
                if (url!=null) 
                {
                    logger.debugPrintf("hyperlinkUpdate: url=%s\n",url);
                    link=new VRL(url); 
                }
                else
                {
                    // resolve (relative) link 
                   String uriStr=e.getDescription();
                   logger.debugPrintf("hyperlinkUpdate: uristr=%s\n",uriStr);
                   link=new VRL(docVrl,uriStr);

                }
                
                if (e instanceof HTMLFrameHyperlinkEvent)
                    frameEvent=true;
                
                if (super.handleLink(link,false)==false)
                {
                    if (frameEvent==true)
                    {
                        HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                        doc.processHTMLFrameHyperlinkEvent(evt);
                        super.fireFrameLinkFollowedEvent(docVrl,link); 
                    }
                    else
                    {
                        super.fireViewEvent(link,false); 
                    }
                }
            }
        }
        catch(Exception ex)
        {
            this.handle(ex);
        }
    }

    public String getVersion()
    {
        return "1.1 Embedded (Virtual) HTML Viewer (HTML 3.2 compatible)"; 
    }
    
    public String getAbout()
    {
        return "<html><h3> Embedded (Virtual) HTML 3.2 Viewer </h3></html> "; 
    }
    

}
