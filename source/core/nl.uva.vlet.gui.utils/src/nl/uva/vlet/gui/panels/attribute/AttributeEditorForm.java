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
 * $Id: AttributeEditorForm.java,v 1.2 2011-04-18 12:27:13 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:13 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.attribute;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.font.FontUtil;

public class AttributeEditorForm extends JDialog
{
    private static final long serialVersionUID = 9136623460001660679L;
    // ---
    AttributePanel infoPanel;
    // package accesable buttons: 
    JButton cancelButton;
    JButton okButton;
    JButton resetButton;
    // data: 
    VAttribute[] originalAttributes;
    // ui stuff 
    private JTextField topLabelTextField;
    private JPanel buttonPanel;
    private AttributeEditorController formController;
    private String titleName;
    //private JFrame frame=null; 
    private JPanel mainPanel;
	private boolean isEditable;
    
    private void initGUI(VAttribute attrs[])
    {
        try
        {
            this.setTitle(this.titleName); 
            
            BorderLayout thisLayout = new BorderLayout();
            this.getContentPane().setLayout(thisLayout); 
            // this.setSize(398, 251);
            
            // mainPanel: 
            /*{
               mainPanel=new JPanel(); 
               mainPanel.setLayout(new BorderLayout()); 
               this.getContentPane().add(mainPanel,BorderLayout.CENTER);
            }*/
            
            Container rootContainer = this.getContentPane(); 
            
            {
                topLabelTextField = new JTextField();
               
                rootContainer.add(topLabelTextField, BorderLayout.NORTH);
                
                topLabelTextField.setText(this.titleName);
                topLabelTextField.setEditable(false);
                topLabelTextField.setFocusable(false);
                topLabelTextField.setBorder(BorderFactory
                        .createEtchedBorder(BevelBorder.RAISED));
                topLabelTextField.setFont(FontUtil.createFont("dialog")); // GuiSettings.current.default_label_font) ; 
                // new java.awt.Font("Lucida Sans", 1,14));
                topLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
                topLabelTextField.setName("huh");
            }
            {
                infoPanel=new AttributePanel(attrs,isEditable);  
                rootContainer.add(infoPanel,BorderLayout.CENTER);
                //infoPanel.setAttributes(attrs,true); 
            }
            {
                buttonPanel = new JPanel();
                rootContainer.add(buttonPanel, BorderLayout.SOUTH);
                {
                    okButton = new JButton();
                    buttonPanel.add(okButton);
                    okButton.setText("Accept");
                    okButton.addActionListener(formController);
                    okButton.setEnabled(this.isEditable);
                }
                {
                    resetButton = new JButton();
                    buttonPanel.add(resetButton);
                    resetButton.setText("Reset");
                    resetButton.addActionListener(formController);
                }
                {
                    cancelButton = new JButton();
                    buttonPanel.add(cancelButton);
                    cancelButton.setText("Cancel");
                    cancelButton.addActionListener(formController);
                }
            }
            validate(); 
            // update size: 
            //infoPanel.setSize(infoPanel.getPreferredSize());
            Dimension size = this.getPreferredSize(); 
            // bug in FormLayout ? Last row is now shown
            // add extra space:
            size.height+=32;
            size.width+=128; // make extra wide
            setSize(size); 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//  ==========================================================================
//  Constructor 
//  ==========================================================================
    
    private void init(String titleName, VAttribute attrs[])
    {
        //frame = new JFrame(); 
        
        this.originalAttributes=attrs;
        this.titleName=titleName; 
        
        attrs=VAttribute.duplicateArray(attrs);  // use duplicate to edit;
        
        // Must first create ActionListener since it is used in initGui...
        this.formController = new AttributeEditorController(this); 
        this.addWindowListener(formController);
        
        // only set to editable if there exists at least one editable attribute
        this.isEditable=false; 
        
        for (VAttribute attr:attrs)
        {
        	if ((attr!=null) && (attr.isEditable()==true))
        		this.isEditable=true; 
        }
        initGUI(attrs);
        formController.update();
        
        //frame.add(this); 
        //frame.pack(); 
        
        Rectangle windowRec=GuiSettings.getOptimalWindow(this); 
        //this.setPreferredSize(windowRec.getSize());
        this.setLocation(windowRec.getLocation());
        this.validate(); 
        // No auto-show: this.setVisible(true);
    }
    
    public AttributeEditorForm(String titleName, VAttribute attrs[]) 
    {
        super();
        UIPlatform.getPlatform().getWindowRegistry().register(this);
        init(titleName,attrs);
    }
    
    public AttributeEditorForm() 
    {
        UIPlatform.getPlatform().getWindowRegistry().register(this);
        //init("Example Attribute Editor",null);
    }
    
//  ==========================================================================
//  
//  ==========================================================================

    public void setAttributes(VAttribute[] attributes)
    {
        this.originalAttributes=attributes;
        
        // use duplicate to edit:
        attributes=VAttribute.duplicateArray(attributes);
        
        this.infoPanel.setAttributes(new VAttributeSet(attributes),true);
        
        validate();
    }

    public synchronized void Exit()
    {
        // notify waiting threads: waitForDialog().
        this.notifyAll();
        
        myDispose();
    }
    
    private void myDispose()
    {
       this.dispose(); 
    }
    
//  ==========================================================================
//  main 
//  ==========================================================================
	
    /**
     * Static method to interactively ask user for attribute settings 
     * Automatically call Swing invoke later if current thread is not the Gui Event thread. 
     * 
     */
    
    public static VAttribute[] editAttributes(final String titleName, final VAttribute[] attrs, 
            final boolean returnChangedAttributesOnly)
    {        
		final AttributeEditorForm dialog = new AttributeEditorForm(); 
		   
    	Runnable formTask=new Runnable()
	    {
    		public void run()
 	        {
    			// perform init during GUI thread 
    			dialog.init(titleName,attrs);
    				
 	            //is now in constructor: dialog.setEditable(true);
 	            // modal=true => after setVisible, dialog will not return until windows closed
 	            dialog.setModal(true);
 	            dialog.setAlwaysOnTop(true); 
 	            dialog.setVisible(true);

 	            synchronized(this)
 	            {
 	            	this.notifyAll();
 	            }	
 	        }
	    };

 	    /** Run during gui thread of use Swing invokeLater() */ 
 	        
 	    if (UIGlobal.isGuiThread())
 	    {
 	    	formTask.run(); // run directly:
 	    }
 	    else
 	    {
 	    	// go background: 
 	    	
 	    	SwingUtilities.invokeLater(formTask);
 	    	
 	    	synchronized(formTask)
 	    	{
 	    		try
				{
 	    			//System.err.println("EditForm.wait()");
					formTask.wait();
				}
				catch (InterruptedException e)
				{
					Global.logException(ClassLogger.ERROR,AttributeEditorForm.class,e,"--- Interupted ---\n");
				}
 	    	}
 	    }
 	    
 	   // wait 
         
         if  (dialog.formController.isOk==true) 
         {
             // boolean update=dialog.hasChangedAttributes(); 
             if (returnChangedAttributesOnly)
                 return dialog.infoPanel.getChangedAttributes();
             else
                 return dialog.infoPanel.getAttributes(); 
         }
         else
         {
             return null;
         }
 	}

    /*private void setEditable(boolean b)
    {
        this.infoPanel.setEditable(b); 
    }*/

    public boolean hasChangedAttributes()
    {
        return this.infoPanel.hasChangedAttributes(); 
    }
}
