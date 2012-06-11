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
 * $Id: GridProxyDialogController.java,v 1.12 2011-05-11 14:34:02 ptdeboer Exp $  
 * $Date: 2011-05-11 14:34:02 $
 */ 
// source: 

package nl.uva.vlet.gui.util.proxy;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.grid.voms.VO;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.dialog.SimpleDialog;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vrs.VRSContext;

/**
 *  Grid Proxy Dialog Controller.
 */
class GridProxyDialogController implements ActionListener, WindowListener, FocusListener
{
    private GridProxyDialog proxyDialog = null;

    GridProxy gridProxy = null;

    /** optionally save new nl.uva.vlet.gui.utils.proxy 
     * @param context */

    public GridProxyDialogController(VRSContext context, GridProxyDialog proxyDialog)
    {
        this.proxyDialog = proxyDialog;

        if (context==null)
        	gridProxy = UIGlobal.getVRSContext().getGridProxy();
        else
        	gridProxy = context.getGridProxy();
    }
    
    ActionTask proxyCreationTask;

    boolean isOk=false;
    
    Runnable runProxyCreated=new Runnable()
        {
            public void run()
            {
                proxyCreated(); // call proxy created;
            }
        };

	private Timer timer=null;
    
    public void createProxy()
    {
    	debugPrintf("createProxy()\n"); 
    	
        final char[] chars = proxyDialog.passwordTextField.getPassword();

        proxyDialog.setCursor(GuiSettings.getBusyCursor());
        
        proxyDialog.createButton.setEnabled(false);
       
        // run creation of proxy in background
        proxyCreationTask = new ActionTask(null, "Proxy Creation Task")
        {
            protected void doTask() 
            {
               try
               {
            	   //Debug("GridProxy.createWithPasswd"); 
                   boolean result=gridProxy.createWithPassword(new String(chars));
                   // clear password chars from memory 
                   for (int i=0;i<chars.length;i++) 
                	   chars[i]=0; 
                   
                   //Debug("result="+result);
                   //Debug("proxy path="+gridProxy.getProxyFilename()); 
                   //Debug("isValid()="+gridProxy.isValid()); 
                   //
                   // When credentials are expired, globus still creates a zero time proxy.
                   // Which was ofcourse discovered after MY credentials expired :-).
                   //
                   if (gridProxy.getTimeLeft()<=0) 
                   {
                	   throw new VlException(
                			   "GlobusCredentialException",
                			   "Zero proxy lifetime detected after succesful proxy creation\n"+
                			   "Are the credentials still valid ?\n"+
                			   "(Proxy Created="+result+",Timeleft="+gridProxy.getTimeLeft()+")");
                   }
                   
               }
               catch (VlException e)
               {
                  this.setException(e); 
                  Global.debugPrintStacktrace(e);
                  debugPrintf("Exception:%s\n",e);  
               }
               // calback:
               SwingUtilities.invokeLater(runProxyCreated);  
            }
            
            @Override
            public void stopTask()
            {
            }
         };
         
         proxyCreationTask.startTask(); 
    }
    
    /** Is called after background task has created the proxy */ 
    protected void proxyCreated()
    {
    	debugPrintf("proxyCreated\n");
        proxyDialog.setCursor(GuiSettings.getDefaultCursor());

        Exception ex=proxyCreationTask.getException();
        VO vo=null;
        
        try 
        {
			vo=this.gridProxy.getProvider().getVO(gridProxy.getDefaultVOName()); 
		} 
        catch (VlException e) 
        {
        	logException("Couldn't get default VO information.",e);
		} 
        
        if (ex!=null)
        {
        	//currently only happens when VOMS server isn't known.  
        	if ((ex instanceof nl.uva.vlet.exception.VlUnknownCAException) && (vo!=null)) 
        	{
        		// ExceptionForm.show(this.proxyDialog,ex,true);
        		// String msg=ex.getMessage(); 
        		
        		Boolean val=SimpleDialog.askConfirmation("The Certificate Authority (CA) doesn't seem to be recognized for:"+vo.getDefaultHost()
        				//+"\nError message:\n-------\n"
        				//+msg+"\n------"
        				+"\nDo you want to import the CA certificate ?\nAfter importing the new certificate, you have to restart the VBrowser.", true);
        	
        		if (val)
        		{
        			checkImportVomsCA(true);
        			this.gridProxy.loadCertificates(); 
        			// still need to restart after loadCertificates. 
        		}
        	}
        	else
        	{
        		// MODALITY CHAINING: when showing a modal dialog DURING another
        		// modal dialog, the parent dialog MUST be supplied :
        		ExceptionForm.show(this.proxyDialog,ex,true);
        		// use JOptionPane during MODAL dialog 
       	 		/* JOptionPane.showMessageDialog(this.proxyDialog,
         		    ex.getMessage(),
         		    ex.getName(),
         		    JOptionPane.ERROR_MESSAGE);*/ 
        	}
        }
        else 
        {
        	try
        	{
        		if (gridProxy.saveProxy() == false)
        			SimpleDialog.displayError(null,"Could not save proxy");
            }
            catch (VlException e2)
            {
               ExceptionForm.show(this.proxyDialog,e2,true);
            }
             
            gridProxy.isValid(); 
         }
              
         proxyDialog.createButton.setEnabled(true);
         this.isOk=true;
         update(); 
    }

    public void checkImportVomsCA(boolean interactive) 
    {
    	String voname=this.gridProxy.getDefaultVOName(); 
		try 
		{
			gridProxy.checkImportVomsServerCertificates(voname);
		}
		catch (VlException e) 
		{
			e.printStackTrace();
		}  
	}

	private void debugPrintf(String format,Object... args) 
    {
    	ClassLogger.getLogger(GridProxyDialogController.class).debugPrintf(format,args);  
    	//System.err.println(msg); 
	}
	
	private void logException(String msg,Throwable e) 
    {
    	ClassLogger.getLogger(GridProxyDialogController.class).logException(Level.WARNING,e,"%s\n",msg); 
	}

	public void update()
    {
		debugPrintf("update()\n");
		// reload:
        gridProxy.reload(); 
        

        if (gridProxy.isValid() == false)
        {
        	debugPrintf("udpate(): GridProxy NOT valid\n");
            proxyDialog.proxyCNTextField.setText("*** Invalid Proxy ***");
            
            if (timer!=null) 
            	timer.stop();   
            this.isOk=false;
        }
        else
        {
        	debugPrintf("update(): GridProxy valid...\n");

            proxyDialog.proxyCNTextField.setText(gridProxy.getSubject());
            // Initiate Countdown to Desctruction...
            if (timer==null)
            {
            	timer = new Timer(1000, this);
            	timer.setInitialDelay(1000);
            }
            timer.start();
            this.isOk=true;
        }
        
        proxyDialog.certificateLocationField.setText(gridProxy.getUserCertificateDirectory());
        proxyDialog.proxyValidityTextField.setText("" + gridProxy.isValid());
        
        // Use path where NEW proxies will be created: 
        proxyDialog.proxyLocationTextField.setText(gridProxy.getDefaultProxyFilename());
        updateTimeLeft(); 
      
        proxyDialog.proxyLifetimeField.setText(""+gridProxy.getDefaultLifeTime()+" (hours)");
        // VO support
        boolean voSup=gridProxy.getEnableVOMS(); 
        proxyDialog.voSupportTB.setSelected(voSup); 
        
        if (voSup)
        {
            proxyDialog.voNameTF.setEditable(true);
            proxyDialog.voNameTF.setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
            proxyDialog.voNameTF.setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
            proxyDialog.voNameTF.setText(notNull(gridProxy.getDefaultVOName()));
            //role
            proxyDialog.voRoleTF.setEditable(true);
            proxyDialog.voRoleTF.setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
            proxyDialog.voRoleTF.setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
            proxyDialog.voRoleTF.setText(notNull(gridProxy.getDefaultVORole()));

        }
        else
        {
            proxyDialog.voNameTF.setEditable(false);
            proxyDialog.voNameTF.setBackground(UIGlobal.getGuiSettings().textfield_non_editable_background_color);
            proxyDialog.voNameTF.setForeground(UIGlobal.getGuiSettings().textfield_non_editable_gray_foreground_color);
            proxyDialog.voNameTF.setText("<disabled>");
            
            // role
            proxyDialog.voRoleTF.setEditable(false);
            proxyDialog.voRoleTF.setBackground(UIGlobal.getGuiSettings().textfield_non_editable_background_color);
            proxyDialog.voRoleTF.setForeground(UIGlobal.getGuiSettings().textfield_non_editable_gray_foreground_color);
            proxyDialog.voRoleTF.setText("");
        }

        proxyDialog.okButton.setEnabled(gridProxy.isValid());
        proxyDialog.deleteButton.setEnabled(gridProxy.isValid());
        proxyDialog.vomsACField.setText(gridProxy.getCredentialVOInfo());
        
        this.proxyDialog.validate();
    }
	
	private String notNull(String name)
	{
		if (name==null) 
				return "";
		return name; 
	}
	
	/** Receives both GUI events as well as the Timer event */
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        {
            if (e.getSource() == this.proxyDialog.passwordTextField)
            {
                createProxy();
            }
            else if (e.getSource()==timer)
            {
            	// ONLY update time left field: 
            	updateTimeLeft(); 
            }
            else if (command==null)
            {
            	// assert command!=null;
            }
            else if (command.compareToIgnoreCase("Cancel") == 0)
            {
                
            	this.isOk=false;
                Exit();
            }
            else if (command.compareToIgnoreCase("Ok") == 0)
            {
                // save();
                Exit();
            }
            else if (command.compareToIgnoreCase("Create") == 0)
            {
                createProxy();
            }
            
            else if (command.compareToIgnoreCase("Destroy") == 0) 
            {
                destroyProxy(); 
            }
            else
            	updateField((Component)e.getSource()); //update check fields 
        }

    }

    // share update method 
    private void updateTimeLeft()
    {
        proxyDialog.proxyTimeleftTextField.setText(gridProxy.getTimeLeftString());
	}

	private void updateField(Component comp)
    {
        if (comp == this.proxyDialog.certificateLocationField) 
        {
            String dir=this.proxyDialog.certificateLocationField.getText();
            debugPrintf("new cert dir=%s\n",dir);
            this.gridProxy.setUserCertificateDirectory(dir); 
        }
        else if (comp == this.proxyDialog.proxyLocationTextField) 
        {
            String dir=this.proxyDialog.proxyLocationTextField.getText();
        	debugPrintf("proxypath=%s\n",dir);
            this.gridProxy.setDefaultProxyLocation(dir);
        }
        else if (comp == this.proxyDialog.proxyLifetimeField) 
        {
            String str=this.proxyDialog.proxyLifetimeField.getText();
            debugPrintf("new lifetime=%s\n",str);
        	// accept SS, MM:SS and HH:MM:SS format:
        	String strs[]=str.split(" "); // filter (hours) part  
        	String parts[]=strs[0].split(":"); // split HH:MM[:SS] (Keep hours)
        	int time=0; 
        	
        	if (StringUtil.isEmpty(str))
        	{
               time=this.gridProxy.getDefaultLifeTime(); // reset 
        	}
            else if (parts.length==1)
        	{
        	        time=Integer.parseInt(parts[0]); 
        	}
        	else if (parts.length==2)
        	{
        		time=Integer.parseInt(parts[0])*60
        			+Integer.parseInt(parts[1]); 
        	}
        	else if (parts.length==3)
        	{
        		time=Integer.parseInt(parts[0])*3600 
        			+ Integer.parseInt(parts[1])*60
        			+Integer.parseInt(parts[2]); 
        	}
        	else
        		return; 
        	debugPrintf("Setting new lifetime:%d\n",time);
        	this.gridProxy.setDefaultProxyLifetime(time); 
             
        }
        else if (comp == this.proxyDialog.voSupportTB) 
        {
        	boolean sel=false; 
        	
        	this.gridProxy.setEnableVOMS(sel=proxyDialog.voSupportTB.isSelected());
 
        	if (sel==true)
        	{ 
        		//  erase <disabled> text and request focus.
        		proxyDialog.voNameTF.setText(gridProxy.getDefaultVOName()); 
        		proxyDialog.voNameTF.requestFocusInWindow();
        	}
        	
        }
        else if (comp == this.proxyDialog.voNameTF) 
        {
            String name=this.proxyDialog.voNameTF.getText();
        	debugPrintf("new vo name=%s\n",name);
            this.gridProxy.setDefaultVOName(name); 
        }
        else if (comp == this.proxyDialog.voRoleTF) 
        {
            String role=this.proxyDialog.voRoleTF.getText();

            debugPrintf("new vo role=%s\n",role);
            this.gridProxy.setDefaultVORole(role); 
        }
        update(); 
        
    }
    /*
     * private void destroyProxy() { this.gridProxy.destroy(); update(); }
     */

    private void destroyProxy()
    {
        this.gridProxy.destroy(); 
        update();
        // after Destroy, must be able to click OK ! 
        proxyDialog.okButton.setEnabled(true); 
        this.isOk=false;
    }

    public void Exit()
    {
    	if (this.timer!=null) 
    		this.timer.stop();
    	
    	this.timer=null;
    	
        proxyDialog.dispose();
        
        synchronized(proxyDialog)
        {
           proxyDialog.notifyAll();
        }
        
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        Exit();
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }

	public void focusGained(FocusEvent e) 
	{
		Component comp=e.getComponent(); 
		
		if (comp == this.proxyDialog.proxyLifetimeField) 
	    {
			// update with integer in hours (remove 'hours' string); 
			
			this.proxyDialog.proxyLifetimeField.setText(""+this.gridProxy.getDefaultLifeTime());
	    }
	}

	public void focusLost(FocusEvent e) 
	{
		updateField((Component)e.getSource());
	}

   

}
