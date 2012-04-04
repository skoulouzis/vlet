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
 * $Id: ExternalToolStarter.java,v 1.3 2011-04-18 12:27:11 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:11 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.external;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;

/** Wrapper plugin for external tools */ 
public class ExternalToolStarter extends ViewerPlugin implements ActionListener
{
    private static final long serialVersionUID = -8153274632131510572L;
    private JTextPane mainTP;
    private JPanel buttonPanel;
    private JButton okB;

    @Override
    public void disposeViewer()
    {

    }

    public boolean isTool()
    {
        return true; 
    }
    @Override
    public String[] getMimeTypes()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "Tool Starter"; 
    }

    public void initGUI()
    {
        FormLayout thisLayout = new FormLayout(
                "5dlu, max(p;145dlu):grow, 5dlu", 
                "max(p;5dlu), 24dlu, max(p;5dlu), 6dlu, max(p;15dlu), max(p;5dlu)");
        this.setLayout(thisLayout);
        this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        {
            mainTP = new JTextPane();
            this.add(mainTP, new CellConstraints("2, 2, 1, 2, default, default"));
            mainTP.setText("Starting External Tool"); 
            mainTP.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        }
        {
            buttonPanel = new JPanel();
            this.add(buttonPanel, new CellConstraints("2, 5, 1, 1, default, default"));
            buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            {
                okB = new JButton();
                buttonPanel.add(okB);
                okB.setText("OK");
                okB.addActionListener(this); 
            }
        }

    }
    
    @Override
    public void initViewer()
    {
        initGUI(); 
    }

    @Override
    public void stopViewer()
    {
        
    }

    @Override
    public void updateLocation(VRL loc) throws VlException
    {
        openVRL(loc);
    }

    public void openVRL(VRL loc) throws VlException
    {   
        VNode node=this.getVNode(loc);
        String mimeType=node.getMimeType(); 
        
        
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    public static void main(String args[])
    {
        ExternalToolStarter tv=new ExternalToolStarter(); 
        // tv.setViewStandalone(true);
            
        try
        {
            tv.startAsStandAloneApplication(new VRL("file:/etc/passwd"));
        }
        catch (VlException e)
        {
            System.err.println("***Error: Exception:"+e); 
            e.printStackTrace();
        }
        
    }
}
