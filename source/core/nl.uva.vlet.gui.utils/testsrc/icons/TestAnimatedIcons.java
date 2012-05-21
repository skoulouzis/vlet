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
 * $Id: TestAnimatedIcons.java,v 1.5 2011-04-18 12:27:15 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:15 $
 */ 
// source: 

package icons;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.gui.icons.AnimatedIcon;
import nl.uva.vlet.gui.icons.IconAnimator;
import nl.uva.vlet.gui.image.ImageSequence;
import nl.uva.vlet.gui.image.ImageUtil;

public class TestAnimatedIcons
{

    public static void main(String args[])
    {
        try
        {
        	URL url = Thread.currentThread().getContextClassLoader().getResource("animated/animated_test.gif"); 
        	
            ImageSequence image = ImageUtil.loadAnimatedGif(url); 
            
            int numIcons=6;
            double speeds[]={-2,-1,-0.5,0.5,1,2}; 
            final List<AnimatedIcon> icons=new ArrayList<AnimatedIcon>(numIcons); 
            image.setLoopCount(3); 
            
            //animicon = new AnimatedIcon("file:/home/ptdeboer/gif/test.gif");
            //animicon.setImageObserver(animicon.new AnimationObserver());
            
            JFrame frame=new JFrame() 
                {
                    public void dispose()
                    {
                        super.dispose(); 
                        IconAnimator.getDefault().dispose();
                        //for (AnimatedIcon icon:icons)
                        //    icon.dispose(); 
                    }
                }; 
            
            JPanel panel=new JPanel(); 
            frame.setLayout(new BorderLayout());
            frame.add(panel,BorderLayout.CENTER);
            panel.setLayout(new FlowLayout(FlowLayout.TRAILING));
            panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            
            for (int i=0;i<numIcons;i++)
            {
                AnimatedIcon animIcon=new AnimatedIcon(image);
                double speed=speeds[i];
                animIcon.setAnimationSpeed(speed);
                
                icons.add(animIcon);   
                
                JLabel label=new JLabel("test icon:"+i); 
                label.setIcon(animIcon); 
                label.setBorder(new BevelBorder(BevelBorder.LOWERED));
            
                //IconAnimator.getDefault().register(label,animIcon);
                panel.add(label);
            }
            
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            frame.pack();
            frame.setVisible(true); 
            
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        
    }
}
