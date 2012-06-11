package nl.uva.vlet.gui;

import nl.uva.vlet.vrl.VRL;

public interface BrowserFactory
{
    MasterBrowser createBrowser(); 
    
    MasterBrowser createBrowser(VRL vrl); 
}
