package nl.uva.vlet.gui.vbrowser;

import java.awt.Dimension;
import java.awt.Point;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.BrowserFactory;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.vrl.VRL;

public class VBrowserFactory implements BrowserFactory
{
    private static VBrowserFactory instance=null; 
    
    public static synchronized VBrowserFactory createInstance(UIPlatform platform)
    {
        if (instance==null)
            instance=new VBrowserFactory(platform);
        
        return instance; 
    }
    
    public static VBrowserFactory getInstance()
    {
        return instance;  
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    private UIPlatform uiPlatform=null; 
    
    protected VBrowserFactory(UIPlatform platform)
    {
        this.uiPlatform=platform; 
    }
    
    @Override
    public BrowserController createBrowser()
    {
        return createBrowser((VRL)null); 
    }
    
    // @Override
    public BrowserController createBrowser(String str) throws VlException
    {
        return createBrowser(new VRL(str));
    }
    
    @Override
    public BrowserController createBrowser(VRL vrl)
    {
        VBrowser vb = new VBrowser(this);

        Point p=GuiSettings.getScreenCenter();
        Dimension size=vb.getSize(); 

        vb.setLocation(p.x-size.width/2,p.y-size.height/2);

        vb.setVisible(true);

        BrowserController bc = vb.getBrowserController();

        VRL rootLoc=null; 

        try
        {
            rootLoc = UIGlobal.getVRSContext().getVirtualRootLocation();
        }
        catch (VlException e)
        {
            handle(e); 
        }  

        if (vrl == null)
        {
            vrl = rootLoc; 
        }

        // show it */

        bc.messagePrintln("New browser for:" + vrl);

        // start the populate in a different thread to finish the major task
        // event ask quickly as possible ! 

        bc.asyncOpenLocation(vrl);

        return bc; 
    }


    private void handle(VlException e)
    {
        //          errorMessage("Exception:"+e);
        ExceptionForm.show(e);
        Global.errorPrintf(this,"Exception:%s\n",e); 
    }

    public UIPlatform getUIPlatform()
    {
        return this.uiPlatform;
    }

  


}
