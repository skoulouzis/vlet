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
 * $Id: VBrowserApplet.java,v 1.4 2011-04-18 12:27:23 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:23 $
 */ 
// source: 

package nl.uva.vlet.gui.vbrowser;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.vrl.VRL;

/**
 * Applet to start a VBrowser. 
 * 
 * Under construction 
 * 
 * @author P.T. de Boer
 */

public class VBrowserApplet  extends Applet implements ActionListener 
{
  private JButton startButton;
  String text="";
  private JPanel buttonPanel;
  private JTextArea textArea;
  private JScrollPane textScrollPane; 

  public void initGui()
  {
      this.setLayout(new BorderLayout()); 
      
      {
          textScrollPane=new JScrollPane(); 
          this.add(textScrollPane,BorderLayout.CENTER);
          {
             textArea = new JTextArea(); 
             textScrollPane.setViewportView(textArea); 
             textArea.setText("Applet starting\n");
          }
      }
      
      {
         buttonPanel=new JPanel();
         this.add(buttonPanel,BorderLayout.SOUTH);
         {
            startButton = new JButton();
            buttonPanel.add(startButton);
            startButton.setText("Start VBrowser");
            startButton.setActionCommand("start"); 
            startButton.addActionListener(this); 
         }
      }
  }
  
  private void updateText()
  {
      String txt="--- VBrowser Applet ---"
       +"\nuser      ="+Global.getUsername() 
       +"\nuser home ="+Global.getUserHomeLocation() 
       +"\ncode base ="+Global.getCodeBaseLocation();
      
      /*VRS vrss[]=Registry.getServices();
      txt+="\n --- Registered services ---"; 
      int i=1; 
      
      for (VRS vrs:vrss)
      {
          txt+="\n VRS:["+(i++)+"]="+vrs.getName();
          String schemes[]=vrs.getSchemeNames(); 

          for (String name:schemes)
          {
              txt+="\n - scheme:"+name;
          }
      }*/
      
      textArea.setText(txt); 
  }
  // This applet does not have a run/start method. 
  // The buttons provide interactive start methods and this applet
  // is just a bootstapper/bootloader... 
  // public Applet::run()
  // 
      
  
  /* public VBrowserApplet()
  {
      super(); 
  }*/
  
  public void init()
  { 
      try
      {
        // setup applet environment: 
          
          System.out.println(">>> applet init "); 
          
        GlobalConfig.setIsApplet(true);

        //System.out.println(">>> document base="+this.getDocumentBase());
        
        System.out.println(">>> code base url="+getCodeBase()); 
        System.out.println(">>> code base vrl="+new VRL(getCodeBase()));  
        
        GlobalConfig.setBaseLocation(this.getCodeBase());
        // set user home to installation home for now: 
        GlobalConfig.setUserHomeLocation(this.getCodeBase());
        
        AppletContext context = this.getAppletContext(); 
        String info = this.getAppletInfo(); 

        System.out.println(">>> Global init "); 

        // now call init: 
        Global.init();

        System.out.println(">>> After Global init "); 

        /*Bootstrapper bootstrapper=new Bootstrapper(); 
      
        try
        {
            bootstrapper.setBaseURL(new URL("http://pc-vlab19:8080/vlet/install"));
        }
        catch (MalformedURLException e)
        {
            System.out.println("***Error: Exception:"+e); 
            e.printStackTrace();
         } */
      
         initGui();
      }
      catch (Exception e) 
      {
          e.printStackTrace(); 
          
          ExceptionForm.show(new VlException(e)); 
      }
      
      updateText(); 
      
  }

  public void actionPerformed(ActionEvent e)
  {
     if (e.getSource()==this.startButton)
     {
        try
        {
            // init platform first!!! 
            UIPlatform plat=UIPlatform.getPlatform(); 
            plat.setAppletMode(true); 
            
            // Applet init ! 
            UIGlobal.init(); 
            ProxyVNodeFactory.initPlatform(); 
            VBrowserFactory fac=VBrowserFactory.getInstance(); 
            plat.registerBrowserFactory(fac);
            fac.createBrowser(ProxyVRSClient.getInstance().getVirtualRootLocation());
        }
        catch (VlException ex)
        {
            handle(ex); 
        }
     }
  }

 private void handle(VlException ex)
 {
	 Error("Exception:"+ex);
	 Debug("Exception:"+ex); 
	 Global.errorPrintStacktrace(ex); 
	 Global.debugPrintStacktrace(ex); 
     ExceptionForm.show(ex); 
 }

 private void Debug(String msg) 
 {
	 println("Debug:"+msg); 	 
 }

 private void Error(String msg) 
 {
	 println("Error:"+msg); 
 }

 private void println(String str)
 {
	 this.textArea.setText(textArea.getText()+str); 
 } 
  // ==========================================================================
  // Static methods: 
  // ==========================================================================
}        

