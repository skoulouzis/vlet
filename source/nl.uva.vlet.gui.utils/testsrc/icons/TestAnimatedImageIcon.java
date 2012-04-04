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
 * $Id: TestAnimatedImageIcon.java,v 1.3 2011-04-18 12:27:15 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:15 $
 */ 
// source: 

//package icons;
//
//import java.awt.BorderLayout;
//import java.net.URL;
//
//import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.border.BevelBorder;
//
//import nl.uva.vlet.gui.icons.AnimatedIcon;
//import nl.uva.vlet.gui.image.AnimatedImage;
//import nl.uva.vlet.gui.icons.IconAnimator;
//import nl.uva.vlet.gui.image.ImageUtil;
//
//public class TestAnimatedImageIcon
//{
//
//    public static void main(String args[])
//    {
//        try
//        {
//            AnimatedImage image=null;
//            ImageIO.read(new URL("file:/home/ptdeboer/gif/test.gif"));
//            
//            image=ImageUtil.loadGifImage(new URL("file:/home/ptdeboer/gif/test.gif"));  
//            
//            // wrape ImageIcon around AnimatedImage 
//            ImageIcon icon =new ImageIcon(image); 
//            
//            //animicon = new AnimatedIcon("file:/home/ptdeboer/gif/test.gif");
//            
//            //animicon.setImageObserver(animicon.new AnimationObserver());
//            
//            JFrame frame=new JFrame();
//            JPanel panel=new JPanel(); 
//            frame.setLayout(new BorderLayout());
//            frame.add(panel,BorderLayout.CENTER);
//            panel.setLayout(new BorderLayout());
//            panel.setBorder(new BevelBorder(BevelBorder.LOWERED)); 
//            JLabel label=new JLabel("test icon"); 
//            label.setIcon(icon); 
//            label.setBorder(new BevelBorder(BevelBorder.LOWERED));
//            
//            // IconAnimator.getDefault().register(label,animicon); 
//            
//            panel.add(label,BorderLayout.CENTER);
//            frame.pack();
//            frame.setVisible(true); 
//            
//            
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        } 
//        
//    }
//}
