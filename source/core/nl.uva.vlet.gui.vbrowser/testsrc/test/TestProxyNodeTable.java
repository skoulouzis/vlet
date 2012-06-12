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
 * $Id: TestProxyNodeTable.java,v 1.4 2011-06-07 15:15:07 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:07 $
 */ 
// source: 

package test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import nl.uva.vlet.gui.panels.resourcetable.ResourceTable;
import nl.uva.vlet.gui.proxymodel.ProxyNodeTableModel;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.vbrowser.VBrowserInit;
import nl.uva.vlet.vrl.VRL;

public class TestProxyNodeTable
{
    public static void main(String args[])
    {
        
        ProxyNode pnode=null; 
        
        try
        {
            VBrowserInit.initPlatform(); 
            
            pnode = ProxyNode.getProxyNodeFactory().openLocation(new VRL("file:///home/ptdeboer"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }  
        
        final ProxyNode fnode=pnode; 
        
        Runnable runnable=new Runnable()
        {
            public void run()
            {
                try
                {
                    JFrame frame=new JFrame(); 
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

                    {
                        JPanel panel=new JPanel();
                        panel.setLayout(new BorderLayout());
                        frame.add(panel);
                        
                        {
                            JScrollPane scrollPanel = new JScrollPane();
                            panel.add(scrollPanel, BorderLayout.CENTER);
                            {
                                ResourceTable table=new ResourceTable(new ProxyNodeTableModel(fnode)); 
                                scrollPanel.setViewportView(table);
                            }
                        }
                    }
                    
                    frame.setSize(new Dimension(700,300)); 
                    frame.pack(); 
                    frame.setVisible(true); 
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                } 
               
            }
        };
        
        SwingUtilities.invokeLater(runnable); 
    }
}
