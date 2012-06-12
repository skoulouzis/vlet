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
 * $Id: SimpleNButtonDialog.java,v 1.4 2011-04-18 12:27:15 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:15 $
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

import nl.uva.vlet.gui.GuiSettings;

/**
 * 
 */
public class SimpleNButtonDialog extends javax.swing.JDialog implements ActionListener
{
    private static final long serialVersionUID = -1700768769430554372L;
    private JPanel buttonPanel;
	private JPanel mainPanel;
	private JButton jbut;
	private JTextField topTextFieldLabel;
	private JTextArea messageTextArea;
	private JScrollPane textScrollPanel;
	private JFrame parentFrame=null;
	//private int option;
	
	private String options[]; 
	private String messageString;
	private String selection;
    
    public void setMessage(String text)
    {
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

    public SimpleNButtonDialog(JFrame frame) 
    {
		super(frame);
		initGUI();
	}    
	
	public SimpleNButtonDialog(JFrame frame, String title, String message, String opt1, String opt2) 
	{
		super(frame);
		String options[]=new String[2];
		options[0]=opt1; 
		options[1]=opt2; 
		init(message,title,options); 
	}
	
	public SimpleNButtonDialog(JFrame frame, String title, String message, String opt1, String opt2, String opt3) 
	{
		super(frame);
		String options[]=new String[3];
		options[0]=opt1; 
		options[1]=opt2; 
		options[2]=opt3;
		init(message,title,options); 
	}
	
	public SimpleNButtonDialog(JFrame frame, String title, String message, String options[]) 
	{
		super(frame);
		init(message,title,options); 
	}
	
	private void init(String message,String title,String options[])
	{
		setTitle(title);
		this.options=options; 
		messageString=message; 
		initGUI();
		this.topTextFieldLabel.setText(title); 
	}

	private void initGUI()
	{
		int butwidth=0;
		
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
                        messageTextArea.setText(messageString);
                        messageTextArea.setEditable(false);
                    }
                }
            }
            {
                buttonPanel = new JPanel();
                this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
                for (String optStr:options)
                {
                	if (optStr!=null)
                	{
                		jbut = new JButton();
                    	buttonPanel.add(jbut);
                    	jbut.setText(optStr);
                    	jbut.setActionCommand(optStr); 
                    	jbut.addActionListener(this);
                	}
                	butwidth+=jbut.getPreferredSize().width; 
                }
            }
            {
                topTextFieldLabel = new JTextField();
                this.getContentPane()
                    .add(topTextFieldLabel, BorderLayout.NORTH);
                topTextFieldLabel.setText("Dialog Name");
                topTextFieldLabel.setEditable(false);
                topTextFieldLabel.setFocusable(false);
                topTextFieldLabel.setFont(new java.awt.Font("Dialog",1,14));
                topTextFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
            } 
            
            Dimension prefSize = messageTextArea.getPreferredSize();
            
            if (butwidth>prefSize.width) 
            	prefSize.width=butwidth; 
            
            // dymanic width: 
			this.setSize(prefSize.width+100,prefSize.height+100);       
			
		}
		catch (Exception e) 
		{ 
			e.printStackTrace();
		}
		
		this.setModal(true); 
	}

    public void actionPerformed(ActionEvent e)
    {
        // any action -> dispose
        String cmd=e.getActionCommand();
        
        selection=cmd; 
        
//        for (int i=0;i<options.length;i++) 
//        	if ((options[i]!=null) && (selection.compareTo(options[i])==0))
//        		option=i; 
        
        this.dispose(); 
        
        // has parent frame ? 
        if (parentFrame!=null)
            parentFrame.dispose();         
    }
    
    public static void displayMessage(JFrame frame,String message)
    {
        displayMessage(frame,"Message",message);
    }
    
    public static void displayMessage(JFrame frame,String name,String message)
    {
        SimpleNButtonDialog dialog=new SimpleNButtonDialog(frame); 
        dialog.setName(name); 
        dialog.setMessage(message); 
        dialog.pack(); 
        dialog.setVisible(true);
    }
  
    public static void showErrorMessage(String string)
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
        
        SimpleNButtonDialog inst = new SimpleNButtonDialog(frame,
        		"3button dialog","Three button dailog text. Please push any button",
        		"No","Yes","Any");
        inst.setParentFrame(frame); 
        inst.setVisible(true);
        
        System.out.println("Result="+inst.getOptionString());
        
        
    }

	public static String showDialog(JFrame frame,String title, String message,
			String opt1, String opt2, String opt3) 
	{
		SimpleNButtonDialog inst = new SimpleNButtonDialog(frame,
				title,message,
				opt1,opt2,opt3);
        
		if (frame==null) 
            GuiSettings.placeToCenter(inst);
		
		inst.setVisible(true);
		return inst.getOptionString();  
	}
	
	public static String showDialog(JFrame frame,String title, String message,
			String options[]) 
	{
		SimpleNButtonDialog inst = new SimpleNButtonDialog(frame,
				title,message,
				options);
        
		if (frame==null) 
            GuiSettings.placeToCenter(inst);
		
		inst.setVisible(true);
		return inst.getOptionString();  
	}
	
	
	private String getOptionString()
	{
		 return selection; 
	}
}
