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
 * $Id: GftpFeatureList.java,v 1.2 2011-04-18 12:05:09 ptdeboer Exp $  
 * $Date: 2011-04-18 12:05:09 $
 */
// source: 

package nl.uva.vlet.vfs.gftp;

import java.lang.reflect.Field;

import org.globus.ftp.FeatureList;

/**
 * Util class around Globus GFTP FeatureList.
 * 
 * Sadly org.globus.ftp.FeatureList doesn't parse all Feature List command like
 * MLST.
 * 
 */
public class GftpFeatureList
{
    FeatureList featureList = null;

    public static final String UTF8 = "UTF8";

    public static final String GETPUT = "GETPUT";

    // doesn't get parsed well
    // public static final String MLST="MLST";
    public static final String LANG_EN = "LANG EN";

    public static String gftpFeatureNames[] =
    { FeatureList.ABUF, FeatureList.DCAU, FeatureList.ERET, FeatureList.ESTO, FeatureList.MDTM, FeatureList.PARALLEL,
            FeatureList.PIPE, FeatureList.SBUF, FeatureList.SIZE, UTF8, GETPUT,
            // MLST,
            LANG_EN };

    static
    {
        Field[] fields = FeatureList.class.getFields();

        for (Field f : fields)
        {
            if (f.getName().compareTo("featVector") == 0)
            {
                f.setAccessible(false);
            }
        }
    }

    public GftpFeatureList(FeatureList list)
    {
        this.featureList = list;

    }

    public String toString()
    {
        String str = "{FeatureList:";
        String missing = "";

        for (int i = 0; i < gftpFeatureNames.length; i++)
        {
            String name = gftpFeatureNames[i];
            // last comma:
            String comma = (i + 1 < gftpFeatureNames.length) ? "," : "";

            if (this.featureList.contains(name))
            {
                str += name + comma;
            }
            else
            {
                str += comma;
                missing += name + comma;
            }
        }

        str += ",(missing=" + missing + ")}";

        return str;
    }

    public String toCommaString()
    {
        String str = "";

        for (int i = 0; i < gftpFeatureNames.length; i++)
        {
            String name = gftpFeatureNames[i];
            // last comma:
            String comma = (i + 1 < gftpFeatureNames.length) ? "," : "";

            if (this.featureList.contains(name))
            {
                str += name + comma;
            }

        }

        return str;
    }

    /**
     * Whether Data Channel Authentication (DCAU) is allowed
     */
    public boolean hasDataChannelAuthentication()
    {
        return hasDCAU();
    }

    /** Short for Data Channel Authentication */
    public boolean hasDCAU()
    {
        return contains(FeatureList.DCAU);
    }

    /** MTDM=ModifyDateTime ? */
    public boolean hasMDTM()
    {
        return contains(FeatureList.MDTM);
    }

    public boolean contains(String str)
    {
        return this.featureList.contains(str);
    }

}
