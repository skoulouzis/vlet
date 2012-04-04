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
 * $Id: CaCertDialog.java,v 1.3 2011-04-18 12:00:41 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:41 $
 */ 
// source: 

package nl.uva.vlet.dialog;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
public class CaCertDialog extends VletDialog
{
	private static final long serialVersionUID = -8943578879979815967L;

	private JButton temporaryButton;
	private JLabel certInfoLabel;
	private JPanel topBorderPanel;
	private JPanel topPanel;

	public static final int CANCEL = -1;
	
	public static final int OK = 0;
	
	public static final int TEMPORARY = 1;
	
	public static final int NO  = 2;
		
	private JTextArea upperText;
	private JPanel buttonPanel;
	private JScrollPane scrollPane;
	private JButton cancelButton;
	private JButton yesButton;
	private JPanel borderPanel;
	private JTextArea middleText;
	private int value=CANCEL; 
	
	
	public CaCertDialog(JFrame frame)
	{
		super(frame);
		initGUI();
		this.setTitle("Certificate Dialog");
		// center on screen 
		this.setLocationRelativeTo(null); 
	}
	
	public void exit(int val)
	{
		this.value=val; 
		this.setVisible(false); 
	}
	
	private void initGUI() 
	{
		try 
		{
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			{
				topPanel = new JPanel();
				BorderLayout topPanelLayout = new BorderLayout();
				topPanel.setLayout(topPanelLayout);
				getContentPane().add(topPanel, BorderLayout.NORTH);
				topPanel.setPreferredSize(new java.awt.Dimension(478, 84));
				topPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
				{
					topBorderPanel = new JPanel();
					BorderLayout topBorderPanelLayout = new BorderLayout();
					topBorderPanel.setLayout(topBorderPanelLayout);
					topPanel.add(topBorderPanel, BorderLayout.CENTER);
					topBorderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
					{
						upperText = new JTextArea();
						topBorderPanel.add(upperText, BorderLayout.CENTER);
						upperText.setText("text");
						upperText.setBorder(BorderFactory
							.createEtchedBorder(BevelBorder.LOWERED));
						upperText.setFont(new java.awt.Font("Dialog",1,14));
						upperText.setEditable(false);
					}
				}
			}
			{
				borderPanel = new JPanel();
				BorderLayout borderPanelLayout = new BorderLayout();
				borderPanel.setLayout(borderPanelLayout);
				getContentPane().add(borderPanel, BorderLayout.CENTER);
				borderPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
				{
					scrollPane = new JScrollPane();
					borderPanel.add(scrollPane, BorderLayout.CENTER);
					scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
					{
						middleText = new JTextArea();
						scrollPane.setViewportView(middleText);
						middleText.setText("Certificate Text");
						middleText.setPreferredSize(new java.awt.Dimension(454, 53));
						middleText.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
						middleText.setFont(new java.awt.Font("DialogInput",0,11));
						middleText.setEditable(false);
					}
				}
				{
					buttonPanel = new JPanel();
					borderPanel.add(buttonPanel, BorderLayout.SOUTH);
					buttonPanel.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
					{
						yesButton = new JButton();
						buttonPanel.add(yesButton);
						yesButton.setText("yes");
						yesButton.addActionListener(new ActionListener() {
							

							public void actionPerformed(ActionEvent evt) 
							{
								exit(OK); 
							}
						});
					}
					{
						temporaryButton = new JButton();
						buttonPanel.add(temporaryButton);
						temporaryButton.setText("Temporary");
						temporaryButton.addActionListener(new ActionListener() {
						
							public void actionPerformed(ActionEvent evt) 
							{
								exit(TEMPORARY);  
 							}
						});
					}
					{
						cancelButton = new JButton();
						buttonPanel.add(cancelButton);
						cancelButton.setText("cancel");
						cancelButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) 
							{
								exit(CANCEL); 
							}
						});
					}
				}
				{
					certInfoLabel = new JLabel();
					borderPanel.add(certInfoLabel, BorderLayout.NORTH);
					certInfoLabel.setText("Certificate Information");
					certInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
				}
			}
			this.setSize(578, 322);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setMessageText(String text)
	{
		this.middleText.setText(text); 
	}
	
	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) 
	{
		JFrame frame = new JFrame();
		CaCertDialog inst = new CaCertDialog(frame);
		inst.setVisible(true);
		
	}
	
	public static int showDialog(String text, String chainMessage)
	{
		JFrame frame = new JFrame();
		CaCertDialog inst = new CaCertDialog(frame);
		inst.setQuestion(text);
		inst.setMessageText(chainMessage); 
		inst.setModal(true); 
		inst.setVisible(true);
		
		return inst.value;
	}

	private void setQuestion(String text)
	{
		this.upperText.setText(text);
	}


}
