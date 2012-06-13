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
 * $Id: ACLPanel.java,v 1.3 2011-04-18 12:27:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:30 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.acldialog;



import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.panels.attribute.AttributePanel;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTable;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.vrl.VRL;

public class ACLPanel extends JPanel
{
    public final static String ACCEPT="Close";
    public final static String DELETE="Delete";
    public final static String ADD="Add";
    public final static String APPLY="Apply";
    public final static String REREAD="Reread";
    public final static String CANCEL="Cancel";
    private JFrame frame;
    
    private JScrollPane tableScrollPane;
    private ResourceTable table;
    private JPanel buttonPanel;
    private JButton acceptButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton applyButton;
    private JButton cancelButton;
    private ACLPanelController panelListener;
    private VAttribute[] aclEntities;
    private BrowserController browserController;
    private JButton rereadButton;
    private JPanel topPanel;
    private AttributePanel attrPanel;
    private JTextPane infoText;
    private ProxyNode proxyNode;
    
    public void initGui()
    {
        setLayout(new BorderLayout());
        Border border = BorderFactory.createEmptyBorder(10,10,10,10); 
        setBorder(border);
        
        // topPanel 
        {
           topPanel=new JPanel(); 
           add(topPanel,BorderLayout.NORTH);
           topPanel.setLayout(new BorderLayout());
           border = BorderFactory.createEmptyBorder(10,10,10,10); 
           topPanel.setBorder(border);
           
           /*// Help/Info textpanel  
           {
               infoText=new JTextPane();
               infoText.setEditable(false); 
               infoText.setText(Messages.I_ACL_INFO_TEXT);
               topPanel.add(infoText,BorderLayout.NORTH);
               border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); 
               infoText.setBorder(border);
            
           }*/
           // attribute Panel 
           {
               attrPanel=new AttributePanel();
               topPanel.add(attrPanel,BorderLayout.CENTER);
               border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); 
               attrPanel.setBorder(border);
               
           }
           
        }
        
        // tableScrollPane 
        {
             tableScrollPane = new JScrollPane();
             add(tableScrollPane,BorderLayout.CENTER);
             border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); 
             tableScrollPane.setBorder(border);
             // tablePanel 
             {
                 table=new ResourceTable(new ACLDataModel()); 
                 tableScrollPane.setViewportView(table);
                 // auto fit columns for the ACL table
                 table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                 table.setEditable(true); 
             }
        }
        // button panel 
        {
            buttonPanel=new JPanel(); 
            add(buttonPanel,BorderLayout.SOUTH);
            
            //buttonPanel.setLayout(new FlowLayout()); //default
            
            // accept Button 
            {
                addButton=new JButton(ADD); 
                buttonPanel.add(addButton); 
                addButton.addActionListener(panelListener); 
            }
            // delete Button 
            {
                deleteButton=new JButton(DELETE); 
                buttonPanel.add(deleteButton);
                deleteButton.addActionListener(panelListener);
                // currenlty the delete button confuses the users:
                deleteButton.setVisible(false); 
            }
            // apply  
            {
                applyButton=new JButton(APPLY); 
                buttonPanel.add(applyButton); 
                applyButton.addActionListener(panelListener); 
            }
         
            // reread 
            {
                rereadButton=new JButton(REREAD); 
                buttonPanel.add(rereadButton); 
                rereadButton.addActionListener(panelListener); 
            }
            // accept&close Button  
            {
                acceptButton=new JButton(ACCEPT); 
                buttonPanel.add(acceptButton); 
                acceptButton.addActionListener(panelListener); 
            }
 
            // cancel Button 
            {
                cancelButton=new JButton(CANCEL); 
                buttonPanel.add(cancelButton);
                cancelButton.addActionListener(panelListener); 
            }
        }
     }
    
    public ACLPanel(BrowserController bc)
    {
        this.browserController=bc; 
        panelListener=new ACLPanelController(this);
        initGui();
    }

    public ResourceTable getTable()
    {
        return table;
    }

    public ACLDataModel getModel() 
    {
        return (ACLDataModel)table.getModel(); 
    }
    
    public void readACLFrom(ProxyNode node) throws VlException
    {
        //Debug("setNode:"+node);
        this.proxyNode=node; 
        
        VAttribute attrs[][]=node.getACL();
        this.getModel().setACL(attrs); 
        this.table.initColumns(); 
        
        aclEntities=node.getACLEntities();
//        if (aclEntities!=null)
//        {
//          for (VAttribute attr:aclEntities)
//          {
//            //String name=attr.getValue(); 
//            //Debug("ACLEntity="+attr);  
//          }
//        }
        
        String names[]=
            {
        		VAttributeConstants.ATTR_HOSTNAME,
        		VAttributeConstants.ATTR_PATH,
            };
        
        VAttributeSet set = node.getAttributeSet(names); 
        this.attrPanel.setAttributes(set); 
    }

    public VAttribute[] getACLEntities()
    {
        return aclEntities;
    }
   
    public void handle(VlException e)
    {
        if (browserController!=null)
        {
           browserController.handle(e);
        }
        else
        {
            // for testing:
            System.out.println("Exception:"+e); 
            e.printStackTrace();
            ExceptionForm.show(e); 
        }
    }
    
    public ACLPanelController getPanelController()
    {
        return this.panelListener; 
    }
    
    public void setFrame(JFrame parentFrame)
    {
        frame=parentFrame;
    }
    
    // ====
   

    public void close()
    {
        if (frame!=null) 
            frame.dispose(); 
        
        dispose(); 
        
    }

    private void dispose()
    {
        this.frame=null;
        this.aclEntities=null;
        // must be double dispose proof;
        if (table!=null)
        	this.table.dispose();
        this.table=null;
    }

    // ========================================================================
    // Static Methods
    // ========================================================================
    
    public static void showEditor(BrowserController bc, VRL loc) throws VlException
    {
        ProxyNode node; 
        node = ProxyNode.getProxyNodeFactory().openLocation(loc,true);
        showEditor(bc,node); 
    }
    
    public static void showEditor(BrowserController bc,ProxyNode node) throws VlException
    {   
         JFrame frame=new JFrame(); // bc.getFrame());
         ACLPanel panel=new ACLPanel(bc);
         panel.setFrame(frame); 
         
         frame.add(panel);
         frame.addWindowListener(panel.getPanelController());
         frame.setTitle("ACL Editor for:"+node.getVRL());
         
         JFrame refFrame=null; 
        
         //add standalone dialog to root frame:
         if (bc!=null)
         {
             refFrame=bc.getFrame(); 
             GuiSettings.setToOptimalWindowSize(refFrame,frame);
         }
         else
         {
             frame.pack(); 
         }
         
         // frame.setSize(800,600); 
         frame.setVisible(true);
         panel.readACLFrom(node); 
         frame.requestFocus(); 
    }

    public ProxyNode getNode()
    {
        return this.proxyNode; 
    }
    

}
