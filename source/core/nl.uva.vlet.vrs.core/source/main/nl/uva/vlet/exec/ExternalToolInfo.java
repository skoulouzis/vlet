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
 * $Id: ExternalToolInfo.java,v 1.3 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.exec;

import nl.uva.vlet.vrl.VRL;

public class ExternalToolInfo
{
    private String toolName="echo"; 
    
    private String commandPath="/bin/echo";
    
    private boolean downloadFirst=false;
    
    // whether URI syntax can be used or local path only 
    private boolean useURItrue; 
    
    // StringBuilder.format compatible syntax, first '%s' argument is start VRL 
    private String startupSyntax="%s";

    public static ExternalToolInfo createLocalCommand(String path, 
            boolean downloadFirst, 
            boolean canUseUri)
    {
        ExternalToolInfo info=new ExternalToolInfo(); 
        info.commandPath=path;
        info.toolName=VRL.basename(info.commandPath); 
        info.useURItrue=canUseUri; 
        info.downloadFirst=downloadFirst;  
        
        return info; 
    }
    
    public String getName()
    {
        return toolName; 
    }
    
    public String getCommandPath()
    {
        return this.commandPath; 
    }
    
    public boolean canUseUri()
    {
        return this.useURItrue;
    }
    
    public String getStartSyntax()
    {
        return this.startupSyntax; 
    }
    
    public boolean getDownloadFirst()
    {
        return this.downloadFirst;
    }
}
