/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: TestGetPvierV1SEs.java,v 1.1 2010-08-26 12:44:36 ptdeboer Exp $  
 * $Date: 2010-08-26 12:44:36 $
 */ 
// source: 

package nl.uva.vlet.util.bdii;

import java.util.ArrayList;

import nl.uva.vlet.Global;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.bdii.StorageArea;

public class TestGetPvierV1SEs
{


    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Global.getLogger().setLevelToInfo();   
        getSEs();
    }

    public static void getSEs()
    {
        
        try
        {
            BdiiService bdii=new BdiiService("bdii.grid.sara.nl",2170);
            
            ArrayList<StorageArea> sas = bdii.getVOStorageAreas("pvier",null,true);  
            for (StorageArea sa:sas)
            {
                System.out.println(" - Storage Area = "+sa.getVOStorageLocation());
                
                ArrayList<ServiceInfo> ses = sa.getServices(); 
                for (ServiceInfo se:ses)
                    System.out.println(" - - service = "+se.getServiceVRL()+" ["+se.getServiceType()+"]"); 
            }
        }
 
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    
}
