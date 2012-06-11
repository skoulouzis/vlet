package nl.uva.vlet.gui.vhtml;


import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

public class VHTMLKit extends HTMLEditorKit
{
    private static final long serialVersionUID = 4777118976470906864L;

    public static class LinkListener implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent e) 
        {
            JEditorPane pane = (JEditorPane) e.getSource();
            try 
            {
                pane.setPage(e.getURL());
            }
            catch (Throwable t) 
            {
                t.printStackTrace();
            }
        }
    }
   
    // ========================================================================
    // ========================================================================
    
    private VHTMLLinkController linkController;
    
    public void install(JEditorPane c) 
    {
        super.install(c);
        replaceLinkController(c);
    }
    
    public void deinstall(JEditorPane c)
    {
        c.removeMouseListener(linkController);
        c.removeMouseMotionListener(linkController);
    }

    void replaceLinkController(JEditorPane c)
    {
        {
            EventListener[] listeners = c.getListeners(MouseListener.class);
            for( int i=0; i<listeners.length; ++i)
            {
                if(listeners[i] instanceof HTMLEditorKit.LinkController)
                {
                    c.removeMouseListener((MouseListener)listeners[i]);
                }
            }
        }
        
        {
            EventListener[] listeners = c.getListeners(MouseMotionListener.class);
            for( int i=0; i<listeners.length; ++i)
            {
                if(listeners[i] instanceof HTMLEditorKit.LinkController)
                {
                    c.removeMouseMotionListener((MouseMotionListener)listeners[i]);
                }
            }
        }
        
        linkController = new VHTMLLinkController(c);
        c.addMouseListener(linkController);
        c.addMouseMotionListener(linkController);
    }

    LinkController createLinkController()
    {
        return new LinkController();
    }
    
}
