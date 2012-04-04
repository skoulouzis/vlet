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
 * $Id: WarningDialog.java,v 1.3 2011-04-18 12:00:41 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:41 $
 */ 
// source: 

package nl.uva.vlet.dialog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class WarningDialog extends javax.swing.JDialog implements ActionListener
{
	private static final long serialVersionUID = -3135290068915968993L;
	private JPanel topPanel;
	private JPanel mainPanel;
	private JLabel warningLabel;
	private JPanel buttonPanel;
	private JTextArea messageTA;
	private JButton[] buttons;
	private String value;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args)
	{
		String buts[]={"yes","no","cancel"};
		
		String value=WarningDialog.showMessage("Warning","This is a Warning",buts);
		
		System.err.println("Value="+value); 
	}
	
	public WarningDialog(JFrame frame) 
	{
		super(frame);
		String buts[]={"Yes","No","Cancel"};
		init("Warning","This is an important Warning!",buts);
	}
	
	public WarningDialog(String title, String messageText, String[] buttons)
	{
		super((JFrame)null);
		init(title,messageText,buttons);
	}

	private void init(String title, String messageText, String[] buts)
	{
		initGUI();
		
		this.setTitle(title);
		this.warningLabel.setText(title); 
		this.messageTA.setText(messageText);
		
		this.setLocationRelativeTo(null);
		initButtons(buts);
	}

	private void initButtons(String names[])
	{
		buttons=new JButton[names.length];
		
		int index=0; 
		
		for (String name:names)
		{
			JButton but=new JButton();
			but.setText(name);
			but.setActionCommand(name);
			but.addActionListener(this); 
			buttonPanel.add(but);
			buttons[index] = but; 
			index++;
		}
		
	}

	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			{
				mainPanel = new JPanel();
				BorderLayout mainPanelLayout = new BorderLayout();
				mainPanel.setLayout(mainPanelLayout);
				getContentPane().add(mainPanel, BorderLayout.CENTER);
				mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				{
					topPanel = new JPanel();
					FlowLayout topPanelLayout = new FlowLayout();
					topPanel.setLayout(topPanelLayout);
					mainPanel.add(topPanel, BorderLayout.NORTH);
					topPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					{
						warningLabel = new JLabel();
						FlowLayout warningLabelLayout = new FlowLayout();
						warningLabel.setLayout(warningLabelLayout);
						topPanel.add(warningLabel);
						warningLabel
							.setText("Please read the following warning");
					}
				}
				{
					messageTA = new JTextArea();
					mainPanel.add(messageTA, BorderLayout.CENTER);
					messageTA.setText("Message Text");
					messageTA.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					messageTA.setEditable(false);
				}
				{
					buttonPanel = new JPanel();
					mainPanel.add(buttonPanel, BorderLayout.SOUTH);
				
				}
			}
			this.setSize(400, 218);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd=e.getActionCommand(); 
	
		this.value=cmd; 
		
		this.setVisible(false); 
	}
	
	public String getValue()
	{
		return this.value; 
	}
	
	public static String showMessage(String title,String text,String buttons[])
	{
		WarningDialog dialog=new WarningDialog(title,text,buttons);
		dialog.setModal(true); 
		dialog.setVisible(true); 
		return dialog.getValue(); 
	}
	
	public static String showWarning(String title,String text,String buttons[])
	{
		WarningDialog dialog=new WarningDialog(title,text,buttons);
		dialog.setModal(true); 
		dialog.setVisible(true); 
		return dialog.getValue(); 
	}
	
}

