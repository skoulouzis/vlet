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
 * $Id: startVBrowser.java,v 1.8 2011-04-18 12:27:31 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:31 $
 */ 
// source: 

package nl.uva.vlet.gui;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.gui.vbrowser.VBrowserFactory;
import nl.uva.vlet.gui.vbrowser.VBrowserInit;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.gui.viewers.ViewerRegistry;
import nl.uva.vlet.vrl.VRL;

/**
 * Simple VBrowser Start Class.
 */

public class startVBrowser
{
  /** 
   * Main  
   *  
   */ 
  public static void main(String args[]) 
  {
      try
      {
          Global.parseArguments(args); 

          // does platform init!
          VBrowserInit.initPlatform();
          VBrowserFactory factory=VBrowserFactory.getInstance(); 
          
        // start browser(s)
      	{
            int urls=0; 
            
            if (args!=null) for (String arg:args)
            {
                Global.debugPrintf(startVBrowser.class,"arg=%s\n",arg);
                
                // assume that every non-option is a VRL:
                
                if (arg.startsWith("-")==false)
                {
                    // urls specfied:
                    urls++; 
                    factory.createBrowser(arg);
                }
                else
                {
                  if (arg.compareTo("-noblock")==0)
                   {
                       BrowserController.setDummyMode(false);
                   }
                   else if (arg.compareTo("-notree")==0)
                   {
                	   GuiSettings.setShowResourceTree(false); 
                   }
                }
            }
            
            // no urls specified, open default window:
            if (urls==0) 
            {
                // get home LOCATION: Can also be gftp/srb/....
                // BrowserController.performNewWindow(TermGlobal.getUserHomeLocation());
                factory.createBrowser(UIGlobal.getVirtualRootLocation());
            }
      	}
    }
    catch (VlException e)
    {
        Global.errorPrintf(startVBrowser.class,"***Error: Exception:%s\n",e); 
        Global.debugPrintStacktrace(e);
        ExceptionForm.show(e); 
         
    }
  }
  
  /** Create VBrowser instance */ 
  public static BrowserController createVBrowser(VRL vrl)
  { 
 	 return VBrowserFactory.getInstance().createBrowser(vrl); 
  }
  
  /** Register viewer */ 
  public static void registerViewer(String clazzname)
  {
	  ViewerRegistry.getRegistry().registerViewer(clazzname); 
  }
  
  /** Register viewer class */ 
  public static void registerViewer(Class<? extends ViewerPlugin> clazz)
  {
	  ViewerRegistry.getRegistry().registerViewer(clazz); 
  }
  

}

  