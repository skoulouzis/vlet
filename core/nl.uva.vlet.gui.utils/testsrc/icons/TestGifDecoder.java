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
 * $Id: TestGifDecoder.java,v 1.3 2011-04-18 12:27:15 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:15 $
 */ 
// source: 

package icons;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import nl.uva.vlet.gui.util.gif.NS2GifDecoder;

public class TestGifDecoder
{
    public static void main(String args[])
    {
        try
        {
            test();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void test() throws MalformedURLException
    {
        NS2GifDecoder gifDec = new NS2GifDecoder(); 
     
        gifDec.read(new URL("file:/home/ptdeboer/gif/test.gif")); 
        Dimension size = gifDec.getFrameSize(); 
        int num=gifDec.getFrameCount(); 
        
        info(" --- gif info ---"
                +"\n - frame count = "+num 
                +"\n - loopcount   = "+gifDec.getLoopCount()
                +"\n - frame size  = "+size.width+"x"+size.height
                +"\n"); 
        
        for (int i=0;i<num;i++)
        {
            BufferedImage frame = gifDec.getFrame(i);
            
            info( " --- frame #"+i+"---"
                 +"\n - delay = "+ gifDec.getDelay(i) 
                 +"\n - frame = "+ frame 
                 +"\n");  
        }   
    }
    
    public static void info(String str)
    {
        System.out.println(str); 
    }
}
