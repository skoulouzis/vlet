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
 * $Id: ProgresPanel.java,v 1.3 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.monitoring;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import nl.uva.vlet.tasks.ActionTask;

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
public class ProgresPanel extends javax.swing.JPanel 
{
    //private JPanel miniButtonPnl;
    private JTextField progressTF;
    private JProgressBar progressBar;
    private JTextField timeTF;
    private JTextField progresPercTF;
    private long todo=1000000;

    /**
    * Auto-generated main method to display this 
    * JPanel inside a new JFrame.
    */
    public static void main(String[] args) 
    {
        JFrame frame = new JFrame();
        final ProgresPanel panel=new ProgresPanel(); 
        frame.getContentPane().add(panel);
        
        ActionTask task=new ActionTask(null,"StatusPanel tester")
        {
            public void doTask()
            {
                panel.setTotal(1000);
                
                for (int i=0;i<=1000;i++)
                {
                    // panel.setProgress(i); 
                    panel.setProgress((double)i/1000.0); 
                    panel.setProgressText(" "+i+" out of:"+1000); 
                    
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void stopTask()
            {
            }
        };
        
        task.startTask();
        
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }
    
    public ProgresPanel() 
    {
        super();
        initGUI();
 
    }
    
    private void initGUI() 
    {
        try 
        {
            FormLayout thisLayout = new FormLayout(
                    "3dlu, 100dlu:grow, 5dlu, max(p;32dlu), 5dlu, max(p;24dlu), 3dlu", 
                    "3dlu, max(p;8dlu), 3dlu, max(p;8dlu), 5dlu");
            this.setLayout(thisLayout);
            //this.setPreferredSize(new java.awt.Dimension(699, 70));
            {
                progressBar = new JProgressBar();
                this.add(progressBar, new CellConstraints("2, 2, 3, 1, default, default"));
                progressBar.setMinimum(0); 
                progressBar.setMaximum((int)todo); 
            }
//            {
//                miniButtonPnl = new JPanel();
//                this.add(miniButtonPnl, new CellConstraints("6, 1, 1, 1, default, default"));
//            }
            {
                progressTF = new JTextField();
                this.add(progressTF, new CellConstraints("2, 4, 1, 1, default, default"));
                progressTF.setText("?");
            }
            {
                progresPercTF = new JTextField();
                this.add(progresPercTF, new CellConstraints("6, 2, 1, 1, default, default"));
                progresPercTF.setText("100.99%");
            }
            {
                timeTF = new JTextField();
                this.add(timeTF, new CellConstraints("4, 4, 3, 1, default, default"));
                timeTF.setText("99:99:99s (99:99:99s) ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void setTotal(int total)
    {
        this.todo=total; 
        this.progressBar.setMinimum(0); 
        this.progressBar.setMaximum(total);
    }
    
    public void setProgress(long value)
    {
        double perc=((double)value)/((double)todo); 
        setProgress(perc); 
    }
    
    public void setProgress(double value)
    {
        this.progressBar.setValue((int)(value*todo));
        // round to 99.99 
        value=Math.round(value*10000.0)/100.0;  
        this.progresPercTF.setText(""+value+"%  "); 
    }

    public void setProgressText(String txt)
    {
        this.progressTF.setText(txt); 
    }
    
    public void setTimeText(String txt)
    {
        this.timeTF.setText(txt); 
 
    }
   
}
