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
 * $Id: testURLStreamFactory.java,v 1.3 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package old;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class testURLStreamFactory
{

    public static void main(String args[])
    {
        URL.setURLStreamHandlerFactory(nl.uva.vlet.vrl.VRLStreamHandlerFactory.getDefault());

        URL url = null;

        try
        {
            url = new URL("http://www.piter.nl/index.html");
            InputStream inps = url.openStream();

            Object image = Toolkit.getDefaultToolkit().getImage(url);

        }
        catch (MalformedURLException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

}
