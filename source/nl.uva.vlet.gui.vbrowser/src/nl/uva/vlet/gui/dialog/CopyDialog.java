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
 * $Id: CopyDialog.java,v 1.8 2011-06-07 15:15:08 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:08 $
 */ 
// source: 

package nl.uva.vlet.gui.dialog;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.vrl.*; 
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.data.*; 
import nl.uva.vlet.exception.VRLSyntaxException;

import nl.uva.vlet.gui.panels.attribute.AttributePanel;

/**
 * Copy Options Dialog. Asks user what to do when a copying a file to a destination
 * which already ex>ists.  
 */
public class CopyDialog extends javax.swing.JDialog implements ActionListener 
{
    private static final long serialVersionUID = 8405499762660230300L;

    public static enum CopyOption{Overwrite,Skip,Rename,Cancel;} 
	
	private JPanel topPanel;
	private JPanel buttonPanel;
	private JButton overwriteBut;
	private JTextArea messageTA;
	private JPanel optionsPanel;
	private JLabel sourceLbl;
	private JTextField sourceTF;
	private JButton renameBut;
	private JButton skipBut;
	private JLabel destLbl;
	private JTextField destVrl;
	private JPanel borderPanel;
	private JCheckBox alwaysSkipCB;
	private JLabel optionLbl;
	private JCheckBox alwaysOverwriteCB;	
	private JButton cancelBut;
	private AttributePanel sourceAttrPnl;
	private AttributePanel destAttrPnl;
	private CopyOption copyOption=null; //CopyOption.Cancel; 
	
	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) 
	{
		try 
		{
			VRL sourceVrl = new VRL("file:///usr/lib/verylongurl_1234567890_ABCDEFGHIJKLMNOPQRSTUVW_abcdefghijklmnopqrstuvxwz/test.txt");
			VRL destVrl=new VRL("file:///etc/passwd");
			
			VAttributeSet set=new VAttributeSet();
			set.set("Hostname","localhost");
			set.set("Length",12346); 
			set.set("Modification","1984-Jan-01 12:34:56");  
			
			VAttributeSet set2=set.duplicate(); 
			set2.set("Modification","1984-Jan-01 12:34:59");  
			set2.set("Creation","1984-Jan-01 12:34:56");  
			set2.set("Usage","1984-Jan-01 12:34:56");  
			
			CopyDialog dialog=CopyDialog.showCopyDialog(null,
					sourceVrl,
					set,
					destVrl,
					set2,
					true);
			
			System.err.println(">>> DONE <<<"); 
			System.err.println("option       ="+dialog.getCopyOption()); 
			System.err.println("skip all     ="+dialog.getSkipAll()); 
			System.err.println("overwrite all="+dialog.getOverwriteAll()); 

		}
		catch (VRLSyntaxException e1) 
		{
			e1.printStackTrace();
		} 
		
		VRS.exit(); 
	}
	
	/**
	 * Display Copy options dialog when copying from source to destination and the
	 * destination resource already exists. 
	 * @param parentFrame optional parent JFrame (can be null) 
	 * @param sourceVrl source VRL 
	 * @param sourceAttrs optional set of significant attributes from source (can be null).  
	 * @param destVrl destination VRL 
	 * @param destAttrs optional set of significant attributes from destination (can be null). 
	 * @param modal whether dialog should be model. If true this method will only return 
	 *              dialog has closed. 
	 * @return
	 */
	public static CopyDialog showCopyDialog(final JFrame parentFrame,
			final VRL sourceVrl,
			final VAttributeSet sourceAttrs,
			final VRL destVrl, 
			final VAttributeSet destAttrs,
			boolean modal)
	{
		//final JFrame frame = new JFrame();
		final CopyDialog inst = new CopyDialog(parentFrame);

		inst.setSourceVRL(sourceVrl); 
		inst.setDestVRL(destVrl);
		inst.setSourceAttrs(sourceAttrs);
		inst.setDestAttrs(destAttrs);
		
		inst.validate(); 
		inst.setToPreferredSize();
		inst.setLocationRelativeTo(null); // Center!
		
		if (modal)
			inst.setModalityType(ModalityType.APPLICATION_MODAL);
		inst.setVisible(true);

        return inst; 
	}

	
	public CopyDialog(JFrame frame) 
	{
		super(frame);
		initGUI();
	}
	
	public void setSourceVRL(VRL vrl)
	{
		//this.sourceTF.setText("("+StringUtil.noNull(vrl.getHostname())+"):"+vrl.getBasename());
		this.sourceTF.setText(vrl.toString()); 
	}
	
	public void setDestVRL(VRL vrl)
	{
		//this.destVrl.setText("("+StringUtil.noNull(vrl.getHostname())+"):"+vrl.getBasename());
		this.destVrl.setText(vrl.toString());
	}
	
	public void setSourceAttrs(VAttributeSet attrs)
	{
		this.sourceAttrPnl.setAttributes(attrs,false);  
	}
	
	public void setDestAttrs(VAttributeSet attrs)
	{
		this.destAttrPnl.setAttributes(attrs,false);   
	}
	
	private void initGUI() 
	{
		try 
		{

			topPanel = new JPanel();
			BorderLayout topPanelLayout = new BorderLayout();
			topPanel.setLayout(topPanelLayout);
			this.getContentPane().add(topPanel, BorderLayout.CENTER);
			topPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
			{
				borderPanel = new JPanel();
				BorderLayout borderPanelLayout = new BorderLayout();
				borderPanel.setLayout(borderPanelLayout);
				topPanel.add(borderPanel, BorderLayout.NORTH);
				borderPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
				borderPanel.setEnabled(false);
				{
					messageTA = new JTextArea();
					borderPanel.add(messageTA, BorderLayout.CENTER);
					messageTA.setText("Destination already exists. Want do you want to do ?");
					messageTA.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
				}
			}
			
			{
				optionsPanel = new JPanel();
				FormLayout optionsPanelLayout = new FormLayout(
						"max(p;5dlu), max(p;5dlu), 5dlu, 20dlu, 5dlu, max(p;50dlu):grow, 5dlu, max(p;5dlu)", 
						"max(p;5dlu), 5dlu, max(p;5dlu), max(p;15dlu):grow, 5dlu, 5dlu, max(p;5dlu), max(p;15dlu):grow, 5dlu, max(p;15dlu), max(p;15dlu), max(p;15dlu), 5dlu, max(p;5px)");
				optionsPanel.setLayout(optionsPanelLayout);

				topPanel.add(optionsPanel, BorderLayout.CENTER);
				
				optionsPanel.add(createSeperatorLbl(), new CellConstraints("2, 1, 6, 1, default, default"));

				{

					sourceLbl = new JLabel();
					optionsPanel.add(sourceLbl, new CellConstraints("2, 3, 3, 1, default, default"));
					sourceLbl.setText("Source:");
					sourceLbl.setFont(new java.awt.Font("Dialog",1,12));
				}
				{
					sourceTF = new JTextField();
					optionsPanel.add(sourceTF, new CellConstraints("6, 3, 2, 1, default, default"));
					sourceTF.setText("<source>");
				}
				{
					destLbl = new JLabel();
					optionsPanel.add(destLbl, new CellConstraints("2, 7, 3, 1, default, default"));
					destLbl.setText("Destination:");
					destLbl.setFont(new java.awt.Font("Dialog",1,12));
				}
				optionsPanel.add(createSeperatorLbl(), new CellConstraints("2, 5, 6, 1, default, default"));

				{
					destVrl = new JTextField();
					optionsPanel.add(destVrl, new CellConstraints("6, 7, 2, 1, default, default"));
					destVrl.setText("<dest>");
				}
				{
					optionsPanel.add(getSourceAttrsPanel(), new CellConstraints("4, 4, 3, 1, default, default"));
				}
				optionsPanel.add(createSeperatorLbl(), new CellConstraints("2, 9, 6, 1, default, default"));

				{
					optionsPanel.add(getDestAttrsPanel(), new CellConstraints("4, 8, 3, 1, default, default"));
					optionsPanel.add(getAlwaysOverwriteCB(), new CellConstraints("4, 11, 3, 1, default, default"));
					optionsPanel.add(getAlwaysSkipCB(), new CellConstraints("4, 12, 3, 1, default, default"));
					optionsPanel.add(getOptionLbl(), new CellConstraints("2, 10, 3, 1, default, default"));
				}
			}
			
			{
				buttonPanel = new JPanel();
				this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
				buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
				buttonPanel.setPreferredSize(new java.awt.Dimension(468, 39));
				{
					overwriteBut = new JButton();
					buttonPanel.add(overwriteBut);
					overwriteBut.setText("Overwrite");
					overwriteBut.addActionListener(this); 
				}
			}

			{
				cancelBut = new JButton();
				buttonPanel.add(getSkipBut());
				buttonPanel.add(getRenameBut());
				buttonPanel.add(cancelBut);
				cancelBut.setText("Cancel");
				cancelBut.addActionListener(this); 
			}

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void setToPreferredSize()
	{
		this.pack();
		this.setSize(this.getPreferredSize()); 
	}
	
	protected AttributePanel getSourceAttrsPanel() 
	{
		if (this.sourceAttrPnl==null)
		{
			this.sourceAttrPnl=new AttributePanel(); 
			//this.sourceAttrPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		}
		
		return sourceAttrPnl; 
	}
	
	protected AttributePanel getDestAttrsPanel() 
	{
		if (this.destAttrPnl==null)
		{
			this.destAttrPnl=new AttributePanel(); 
			//this.destAttrPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		}
		
		return destAttrPnl; 
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource(); 
		
		if (source==this.cancelBut)
		{
			exit(CopyOption.Cancel); 
		}
		
		if (source==this.overwriteBut)
		{
			exit(CopyOption.Overwrite); 
		}
		
		if (source==this.renameBut)
		{
			exit(CopyOption.Rename); 
		}
		
		if (source==this.skipBut)
		{
			exit(CopyOption.Skip); 
		}
		
		if ((source==this.alwaysOverwriteCB) || (source==this.alwaysSkipCB))
		{
			boolean sourceIsOverwrite=(source==this.alwaysOverwriteCB); 
			
			boolean alwaysOverwrite=this.alwaysOverwriteCB.isSelected();
			boolean alwaysSkip=this.alwaysSkipCB.isSelected();
			
			// mutual exclusive selection: 
			if (sourceIsOverwrite)
			{	
				if (alwaysOverwrite==true)
				{
					alwaysSkip=false; 
					this.alwaysSkipCB.setSelected(false);
				}
			}
			else
			{
				if (alwaysSkip==true) 
				{			
					alwaysOverwrite=false; 
					this.alwaysOverwriteCB.setSelected(false);
				}
			}
			this.overwriteBut.setText(alwaysOverwrite?"Overwrite all":"Overwrite"); 
			this.skipBut.setText(alwaysSkip?"Skip all":"Skip"); 
		}
	}

	public synchronized void exit(CopyOption option)
	{
		this.copyOption=option;
		this.dispose(); 
		this.notifyAll(); 
	}
	
	public synchronized boolean waitForExit()
	{
		try 
		{
			// return directlry !
			if (this.copyOption!=null)
				return true;
			
			this.wait();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		return (this.copyOption!=null); 
	}
	
	public boolean getSkipAll()
	{
		return this.alwaysSkipCB.isSelected(); 
	}
	
	public boolean getOverwriteAll()
	{
		return this.alwaysOverwriteCB.isSelected(); 
	}

	/**
	 * Returns selected copy option or NULL when dialog is still open and no
	 * selection has been made.  
	 */
	public CopyOption getCopyOption()
	{
		return this.copyOption; 
	}
	
	private JButton getSkipBut() 
	{
		if(skipBut == null) 
		{
			skipBut = new JButton();
			skipBut.setText("Skip");
			skipBut.addActionListener(this); 
		}
		return skipBut;
	}
	
	private JButton getRenameBut() 
	{
		if(renameBut == null)
		{
			renameBut = new JButton();
			renameBut.setText("Rename");
			renameBut.addActionListener(this); 
		}
		return renameBut;
	}

	private JCheckBox getAlwaysOverwriteCB() 
	{
		if(alwaysOverwriteCB == null) 
		{
			alwaysOverwriteCB = new JCheckBox();
			alwaysOverwriteCB.setText("Always overwrite");
			alwaysOverwriteCB.addActionListener(this); 
		}
		return alwaysOverwriteCB;
	}
	
	private JCheckBox getAlwaysSkipCB()
	{
		if(alwaysSkipCB == null) 
		{
			alwaysSkipCB = new JCheckBox();
			alwaysSkipCB.setText("Always skip");
			alwaysSkipCB.addActionListener(this); 
		}
		return alwaysSkipCB;
	}
	
	private JLabel getOptionLbl()
	{
		if(optionLbl == null) 
		{
			optionLbl = new JLabel();
			optionLbl.setText("Options");
			optionLbl.setFont(new java.awt.Font("Dialog",1,12));
		}
		return optionLbl;
	}
	
	private JLabel createSeperatorLbl()
	{
		JLabel label = new JLabel();
		label.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		return label;
	}

}
