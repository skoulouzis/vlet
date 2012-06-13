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
 * $Id: StatInfo.java,v 1.1 2011-11-25 13:40:45 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:45 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vfs.localfs;

import java.io.File;
import java.util.Properties;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vfs.VFS;

/**
 *  Unix/Linux 'stat' result 
 */ 
public class StatInfo
{
    public static final String FILE_TYPE="File"; 
    public static final String DIR_TYPE="Dir";
    public static final String LINK_TYPE="Link";
    
    public static final String FILENAME="filename"; 
    public static final String TYPE="type"; 
    public static final String GROUPID="groupid";
    public static final String GROUPNAME="groupname";
    public static final String USERID="userid";
    public static final String USERNAME="username";
    public static final String MODE="mode";
    public static final String PERMISSIONS="permissions";
    public static final String MODTIME="modtime";
    public static final String ATIME="atime";
    public static final String ULINK="ulink";
    public static final String SIZE="size";
    public static final String LINK="link";
    
    public static final String[] names=
        {
            FILENAME,
            SIZE,
            USERID,
            USERNAME,
            GROUPID,
            GROUPNAME,
            MODE,
            MODTIME,
            ATIME,
            ULINK,
            LINK
        }; 
    
    public static String getUxStatString()
    {
        String statstr=FILENAME+"=%n;"
            + TYPE+"=%F;"
            + USERID+"=%u;"
            + USERNAME+"=%U;" 
            + GROUPID+"=%g;"
            + GROUPNAME+"=%G;"
            + SIZE+"=%s;"
            + MODTIME+"=%Y;"
            + ATIME+"=%X;"
            + MODE+"=%a;"
            + PERMISSIONS+"=%A;"
            + ULINK+"=%h;"
            + LINK+"=%N;";
        return statstr; 
    }
    
    public static StatInfo parseUxStatResult(String statresult)
    {
        StatInfo inf=new StatInfo(); 
        
        if (StringUtil.isEmpty(statresult))
            return null; 
        
        inf.parse(statresult); 
        return inf; 
    }
    
    // ======================================================================
    // 
    // ======================================================================
    private Properties statProperties=new Properties(); 
    
    private boolean timeIsInMillis=false;
    
    protected StatInfo()
    {
    }
    
    protected StatInfo(String localpath)
    {
        this.statProperties.put(FILENAME,localpath); 
    }
    /**
     * Parse "{NAME=VALUE;}*"  String.  
     * Space and other strange characters between '=' and ';' are considered part of the value.  
     * Newlines will be filtered out. 
     */ 
    protected void parse(String statresult)
    {
        // filter newlines: 
        statresult=statresult.replace("\n",";"); 
        
        String lines[]=statresult.split(";");
        
        if (lines.length<=0) 
            return;  
        
        for (String line:lines)
        {
            String strs[]=line.split("=");
            // skip invalid lines; 
            if ((strs==null) || (strs.length<2))
            {
                Global.warnPrintf(this,"StatInfo.parser(): Couldn't parse line:'%s'\n",line);
                continue; 
            }
 
            String name=strs[0];
            String value=strs[1]; 
            
            if ( (StringUtil.isEmpty(name)==false)
                 && (StringUtil.isEmpty(value)==false) )
            {
                statProperties.put(name,value);
            }
        }
    }
    
    public String toString()
    {
        String str="";
        
        for (String name:names)
        {
            String value=""+this.statProperties.get(name);
            str+=name+"="+value+";\n"; 
        }
        return str;
    }   
    
    public String getUserID()
    {
        return tostring(this.statProperties.get(USERID));  
    }
    
    public String getUserName()
    {
        return tostring(this.statProperties.get(USERNAME));
    }
    
    /** Full path */ 
    public String getType()
    {
        return tostring(this.statProperties.get(TYPE));
    }
    
    public boolean isSoftLink()
    {
        String type=this.getType(); 
        if (type==null)
            return false;
        
        // Ux Stat: 
        if (type.compareToIgnoreCase("symbolic link")==0) 
            return true;
        
        return false; 
    }
    
    /** Full path */ 
    public String getFilename()
    {
        return tostring(this.statProperties.get(FILENAME));
    }
    
    public String getGroupID()
    {
        return tostring(this.statProperties.get(GROUPID));  
    }
    
    public String getGroupName()
    {
        return tostring(this.statProperties.get(GROUPNAME));
    }
    
    public String getPermissions()
    {
        return tostring(this.statProperties.get(PERMISSIONS));
    }
    
    // Time in millies since epoch
    public long getATime()
    {
        return tomillies(this.statProperties.get(ATIME));
    }
    
    public long getSize()
    {
        return tolong(this.statProperties.get(SIZE));
    }
    
    // Time in millies since epoch
    public long getModTime()
    {
        return tomillies(this.statProperties.get(MODTIME));
    }
    
    public int getMode()
    {
        Object octstr=this.statProperties.get(MODE);
        
        if ((octstr==null) || ( octstr==""))
            return -1;
        // prefix with '0' to indicate octal! 
        return Integer.parseInt("0"+octstr,8); 
    }
    
    private String tostring(Object obj)
    {
        if (obj==null)
            return null;
        return obj.toString(); 
    }
    
    private long tolong(Object obj)
    {
        if (obj==null)
            return -1; 
        return Long.parseLong(obj.toString()); 
    }
    
    private long tomillies(Object obj)
    {
        if (obj==null)
            return -1; 
        
        if (obj.equals(""))
            return -1;
        
        long time=Long.parseLong(obj.toString());
        
        if (this.timeIsInMillis==false)
            time=time*1000;
        
        return time; 
    }

    /** Create StatInfo using Java File defaults */ 
    public static StatInfo createFrom(File file)
    {
        StatInfo inf=new StatInfo(file.getAbsolutePath());
        inf.statProperties.put(SIZE,file.length()); 
        inf.statProperties.put(MODTIME, file.lastModified()/1000);
        boolean isDir=false;
        
        if (file.isDirectory())
            isDir=true; 
        
        boolean r=file.canRead(); 
        boolean w=file.canWrite(); 
        boolean x=file.canExecute(); 
        int mode=000; 
        if (r)
            mode+=0444;
        if (w)
            mode+=0222;
        if (x)
            mode+=0111;
        
        inf.statProperties.put(MODE,Integer.toOctalString(mode)); 
        inf.statProperties.put(PERMISSIONS,VFS.modeToString(mode,isDir,false));  
        
        return inf;
    }

    /** Ux Style soft link */ 
    public boolean isUxSofLink()
    {
        String str=this.getPermissions();
        
        if (str==null)
            return false;
        
        if (str.startsWith("l"))
            return true;     
       
        return false;
    }
    
}
