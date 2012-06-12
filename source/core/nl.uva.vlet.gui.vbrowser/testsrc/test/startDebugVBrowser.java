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
 * $Id: startDebugVBrowser.java,v 1.2 2011-04-18 12:27:24 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:24 $
 */ 
// source: 

package test;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.vbrowser.VBrowserFactory;
import nl.uva.vlet.gui.vbrowser.VBrowserInit;

/**
 * 
 * Simple VBrowser Start Class.
 *  
 * Will be called by the 'startVBrowser' in bootstrapper.
 *  
 *  
 */

public class startDebugVBrowser
{

  public static void main(String args[])
  {
      try
      {
        ClassLogger.getRootLogger().setLevelToDebug();
        args=Global.parseArguments(args); 

        UIPlatform plat = VBrowserInit.initPlatform(); 

        // Option --native ? :
  		//GuiSettings.setNativeLookAndFeel();
        
        // shiny swing metal look:
  		plat.startCustomLAF(); 
  		
        // Filter out property arguments like -Duser=jan
  		 
        // start browser(s)
      	{
            int urls=0; 
            
            for (String arg:args)
            {
                Global.debugPrintf(startDebugVBrowser.class,"arg=%s\n",arg);
                
                // assume that every non-option is a VRL:
                
                if (arg.startsWith("-")==false)
                {
                    // urls specified:
                    urls++; 
                    VBrowserFactory.getInstance().createBrowser(arg);
                }
                else
                {
                   if (arg.compareTo("-debug")==0)
                       ClassLogger.getRootLogger().setLevelToDebug(); 
                }
            }
            
            // no urls specified, open default window:
            if (urls==0) 
            {
                // get home LOCATION: Can also be gftp/srb/....
                // BrowserController.performNewWindow(TermGlobal.getUserHomeLocation());
                
                VBrowserFactory.getInstance().createBrowser((UIGlobal.getVRSContext().getVirtualRootLocation())); 
            }
 
      	}
      	
    }
    catch (VlException e)
    {
        Global.logException(ClassLogger.FATAL,startDebugVBrowser.class,e,"Exception:%s\n",e);  
        ExceptionForm.show(e); 
         
    }

  }

}

  