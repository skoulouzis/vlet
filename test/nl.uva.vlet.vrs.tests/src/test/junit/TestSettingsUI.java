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
 * $Id: TestSettingsUI.java,v 1.3 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package test.junit;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.jgoodies.forms.layout.FormLayout;

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
public class TestSettingsUI extends javax.swing.JDialog
{
    private JPanel mainPanel;
    private JLabel topLbl;
    private JPanel topPanel;
    private JPanel settingsPanel;

    /**
     * Auto-generated main method to display this JDialog
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        TestSettingsUI inst = new TestSettingsUI(frame);
        inst.setVisible(true);
    }

    public TestSettingsUI(JFrame frame)
    {
        super(frame);
        initGUI();
    }

    private void initGUI()
    {
        try
        {
            {
                mainPanel = new JPanel();
                BorderLayout mainPanelLayout = new BorderLayout();
                mainPanel.setLayout(mainPanelLayout);
                getContentPane().add(mainPanel, BorderLayout.CENTER);
                {
                    settingsPanel = new JPanel();
                    FormLayout settingsPanelLayout = new FormLayout(
                            "max(p;5dlu), 71dlu, 8dlu, 131dlu", 
                            "max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)");
                    settingsPanel.setLayout(settingsPanelLayout);
                    mainPanel.add(settingsPanel, BorderLayout.CENTER);
                }
                {
                    topPanel = new JPanel();
                    FlowLayout topPanelLayout = new FlowLayout();
                    topPanel.setLayout(topPanelLayout);
                    mainPanel.add(topPanel, BorderLayout.NORTH);
                    {
                        topLbl = new JLabel();
                        topPanel.add(topLbl);
                        topLbl.setText("Test Configuration");
                        topLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                        topLbl.setPreferredSize(new java.awt.Dimension(326, 19));
                    }
                }
            }
            setSize(400, 300);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
