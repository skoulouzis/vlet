package nl.uva.vlet.gui.lobo;

import java.net.URL;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.vrl.VRL;

import org.lobobrowser.ua.NavigationEvent;
import org.lobobrowser.ua.NavigationVetoException;


public class LoboPanelController implements org.lobobrowser.ua.NavigationListener
{
    private static final ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(LoboPanelController.class); 
        //logger.setLevelToDebug(); 
    }

    private LoboBrowser loboBrowser;
    
    public LoboPanelController(LoboBrowser lobo)
    {
        this.loboBrowser=lobo; 
    }

    public void debugPrintf(String format,Object... args)
    {
        logger.debugPrintf(format,args); 
    }

    void handle(String msg,Throwable e)
    {
        logger.logException(ClassLogger.ERROR,e,"%s\n",msg); 
        ExceptionForm.show(new VlException("Lobo Error",msg+"\n"+e.getMessage(),e));
    }

    public boolean isStandalone()
    {
        return this.loboBrowser.isStandalone();
    }
    
    @Override
    public void beforeLocalNavigate(NavigationEvent event) throws NavigationVetoException 
    {
        java.net.URL url=event.getURL();
        logger.debugPrintf("beforeLocalNavigate:%s\n",url);
        // Local = 'Frame' Event
        //    Object linkObject = event.getLinkObject();

        
        try
        {
            if (redirect(url,false,false))
                throw new NavigationVetoException();
        }
        catch (Exception e)
        {
            handle("Invalid URL:"+url,e); 
            // block:
            throw new NavigationVetoException();
        }
    }


     @Override
     public void beforeNavigate(NavigationEvent event) throws NavigationVetoException
     {
         // Local = 'Frame' Event
         //Object linkObject = event.getLinkObject();
         java.net.URL url=event.getURL();
         VRL vrl=null; 
      
         logger.debugPrintf("beforeNavigate %s\n",url);
         
         try
         {  
             vrl=new VRL(url); 
         }
         catch (Exception e)
         {
             handle("Invalid URL:"+url,e);
             throw new NavigationVetoException();
         }
         
         // ===
         // always forward clicked links to master browser
         // View Event will be called back by updateLocation(). 
         // After calling navigate() the call will pass here again
         // and isFromClick will be false. 
         // This way only view events from VBrowser will be handled. 
         // === 
         if (event.isFromClick()==true)
         {
             debugPrintf("beforeNavigate isFromClick()==true:%s\n",url);
             loboBrowser.fireViewEvent(vrl,false);
             throw new NavigationVetoException();
         }
         else
         {
             debugPrintf("beforeNavigate isFromClick()==false:%s\n",url);
         }
         
         // Only manual navigate calls may proceed here
         // These are called from updateLocation() which contain
         // already 'approved' MasterBrowser VRLs for this viewer. 
      }

     @Override
     public void beforeWindowOpen(NavigationEvent event) throws NavigationVetoException
     {
         // Object linkObject = event.getLinkObject();
         java.net.URL url=event.getURL(); 
         logger.debugPrintf("beforeWindowOpen:%s\n",url);
         
         try
         {
             if (redirect(url,true,false))
                 throw new NavigationVetoException();
         }
         catch (Exception e)
         {
             handle("Invalid URL:"+url,e); 
             // block:
             throw new NavigationVetoException();
         }
     }

     private boolean redirect(URL url,boolean openNew,boolean fireFollowEvents) throws VlException
     {
        boolean handled=loboBrowser.handleLink(new VRL(url),openNew); 
        
        if (handled==true)
            return true;
        
        if (fireFollowEvents)
        {
            loboBrowser.fireLinkFollowedEvent(new VRL(url)); 
        }
        
        return false;
     }

    public boolean handleLink(VRL vrl, boolean standalone) throws VlException
    {
        return this.loboBrowser.handleLink(vrl,standalone); 
    }


}
