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
 * $Id: SimpleDialog.java,v 1.5 2011-04-18 12:27:16 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:16 $
 */ 
// source: 

package nl.uva.vlet.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.gui.GuiSettings;

public class SimpleDialog extends javax.swing.JDialog implements ActionListener
{
    private static final long serialVersionUID = -1700768769430554372L;
    private JPanel buttonPanel;
	private JPanel mainPanel;
	private JButton jButton1;
	private JTextField topTextFieldLabel;
	private JTextArea messageTextArea;
	private JScrollPane textScrollPanel;
	private JButton okButton;
    private JFrame parentFrame=null;

    private boolean okValue=false; 
    
    public void setMessage(String text)
    {
        messageTextArea.setColumns(80); 
        messageTextArea.setLineWrap(true);
        messageTextArea.setText(text);
    }
    
    public void setTopLabel(String text)
    {
        topTextFieldLabel.setText(text); 
    }	
	
	private void setParentFrame(JFrame frame)
    {
        parentFrame=frame;
    }

    public SimpleDialog(JFrame frame) 
    {
		super(frame);
		initGUI();
	}    
	
	private void initGUI() 
	{
		try 
		{
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
            {
                mainPanel = new JPanel();
                BorderLayout mainPanelLayout = new BorderLayout();
                mainPanel.setLayout(mainPanelLayout);
                this.getContentPane().add(mainPanel, BorderLayout.CENTER);
                {
                    textScrollPanel = new JScrollPane();
                    mainPanel.add(textScrollPanel, BorderLayout.CENTER);
                    {
                        messageTextArea = new JTextArea();
                        textScrollPanel.setViewportView(messageTextArea);
                        messageTextArea.setText("<text>");
                        messageTextArea.setEditable(false);
                    }
                }
            }
            {
                buttonPanel = new JPanel();
                this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
                {
                    okButton = new JButton();
                    buttonPanel.add(okButton);
                    okButton.setText("OK");
                    okButton.addActionListener(this);
                }
                {
                    jButton1 = new JButton();
                    buttonPanel.add(jButton1);
                    jButton1.setText("Cancel");
                    jButton1.addActionListener(this);
                    jButton1.setVisible(false); 
                }
            }
            {
                topTextFieldLabel = new JTextField();
                this.getContentPane().add(topTextFieldLabel, BorderLayout.NORTH);
                topTextFieldLabel.setText("Dialog Name");
                topTextFieldLabel.setEditable(false);
                topTextFieldLabel.setFocusable(false);
                topTextFieldLabel.setFont(new java.awt.Font("Dialog",1,14));
                topTextFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
			this.setMinimumSize(new Dimension(300,200)); 
		} 
		catch (Exception e) 
		{
			Global.logException(ClassLogger.ERROR,this,e,"Exception during initGUI!\n");
		}
	}

    public void actionPerformed(ActionEvent e)
    {
        // any action -> dispose
        String cmd=e.getActionCommand();
        
        if (cmd.compareTo("OK")==0)
        {
            okValue=true; 
        }
        else if (cmd.compareTo("Cancel")==0)
        {
            okValue=false; 
        }
        
        this.dispose(); 
        
        // has parent frame ? 
        if (parentFrame!=null)
            parentFrame.dispose(); 
        
    }
    
    public boolean getOkValue()
    {
        return okValue;
    }
    
    public static void displayMessage(JFrame frame,String message)
    {
        displayMessage(frame,"Message",message);
    }
    
    public void setName(String name)
    {
        super.setName(name); 
        this.setTitle(name); 
        this.setTopLabel(name); 
    }
    
    public static void displayMessage(JFrame frame,String name,String message)
    {
        SimpleDialog dialog=new SimpleDialog(frame); 
      
        dialog.setName(name); 
        dialog.setMessage(message); 
        dialog.pack();
        GuiSettings.setToOptimalWindowSize(frame,dialog); 
        dialog.setVisible(true);        
    }
    
    public static void displayError(JFrame frame,String message)
    {
        displayMessage(frame,"Error!",message);    
    }

    public static boolean askConfirmation(String questionStr)
    {
        return askConfirmation(questionStr,false); 
    }
    
    public static boolean askConfirmation(String questionStr,boolean defValue)
    {
        int ret = JOptionPane.showConfirmDialog(null,
                questionStr,"Confirmation Needed", JOptionPane.OK_CANCEL_OPTION);
        // questionStr,"Confirmation Needed", JOptionPane.YES_NO_OPTION);
        
        if ((ret==JOptionPane.YES_OPTION) || (ret==JOptionPane.OK_OPTION))
          return true; 
        
        return false; 
    }
    
    public static String askEnumSelectionDialog(String title, String question,String list[],int defSelection)
    {
        Object ret = JOptionPane.showInputDialog(null, question,title,
                JOptionPane.QUESTION_MESSAGE, null, list,list[defSelection]);
        
        if (ret instanceof String)
            return (String)ret;
        
        return null; 
    }

    public static void displayErrorMessage(String string)
    {
        JOptionPane.showMessageDialog(null,string,"Error",JOptionPane.ERROR_MESSAGE); 
        return;  
    }
    
    //========================================================================
    // Main 
    //========================================================================       
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        SimpleDialog inst = new SimpleDialog(frame);
        
        inst.setParentFrame(frame);
        
        inst.setTopLabel("Simple Dialog Example");
        inst.setMessage("HelloWorld");
        
        inst.setVisible(true);
        
        SimpleDialog.displayMessage(null,"This Is A Massage");
        
        boolean ans=askConfirmation("Everything AOK?");
        System.out.println("Answer="+ans);
    }
}
