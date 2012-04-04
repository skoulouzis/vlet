package test;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.lobo.LoboBrowser;
import nl.uva.vlet.vrl.VRL;

public class startStandalone
{

    public static void main(String args[])
    {
        //Global.setDebug(true); 
        Global.init(); 
        
        try
        {
            //VRL vrl = new VRL("http://www.google.com");
            VRL vrl=new VRL("http://www.vl-e.nl/vbrowser/");
            LoboBrowser.viewStandAlone(vrl);
        } 
        catch (VlException e)
        {
            Global.debugPrintException(LoboBrowser.class, e);
        }
    }
    
}
