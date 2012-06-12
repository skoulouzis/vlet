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
 * $Id: ActionCommand.java,v 1.8 2011-04-18 12:27:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:28 $
 */ 
// source: 

package nl.uva.vlet.gui.actions;

import java.util.Hashtable;
import java.util.Map;

import nl.uva.vlet.Global;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.vrs.VRSFactory;

/**
 * Enhanced ActionCommand
 * @author P.T. de Boer
 */

public class ActionCommand  //implements javax.swing.Action// 
{
    public final static String VIEWER_CLASS_NAME="viewerClassName"; 
    public final static String DYNAMIC_METHOD_NAME="methodName"; 
    public final static String VRS_NAME="vrs";   

    // === 

//    public static ActionCommand createSingleArgumentAction(ActionCommandType type, String arg)
//    {
//        ActionCommand cmd=new ActionCommand(type); 
//        cmd.setArgument(arg);
//        return cmd; 
//    }
    
    /** Create VRS Dynamic Action Command */ 
    public static ActionCommand createDynamicActionCommand(VRSFactory vrs,String methodName)
    {
        ActionCommand cmd=new ActionCommand(ActionCommandType.DYNAMIC_ACTION);
        cmd.setDynamicMethodName(methodName); 
        cmd.setVRS(vrs); 
        return cmd;
    }
    
    /** Create ViewerPlugin Dynamic Action Command */  
    public static ActionCommand createDynamicViewerActionCommand(String viewerClassName,String methodName)
    {
        ActionCommand cmd=new ActionCommand(ActionCommandType.DYNAMIC_VIEWER_ACTION);
        cmd.setDynamicMethodName(methodName); 
        cmd.setViewer(viewerClassName); 
        return cmd;
    }
    
    /** Create StartTool (ViewerPlugin) ActionCommand */ 
    public static ActionCommand createStartToolCommand(String toolClassName)
    {
        ActionCommand cmd=new ActionCommand(ActionCommandType.STARTTOOL);
        cmd.setViewer(toolClassName);  
        return cmd;
    }

    /** Create Single Argument Action */ 
    public static ActionCommand createAction(ActionCommandType type,String value)
    {
        ActionCommand cmd=new ActionCommand(type);
        cmd.setArgument(value);   
        return cmd;
    }

    public static ActionCommand createStartViewerAction(String viewerClassName)
    {
        ActionCommand cmd=new ActionCommand(ActionCommandType.STARTVIEWER);
        cmd.setViewer(viewerClassName); 
        return cmd;
    }
    
    // ===
    
    public ActionCommandType actionType=null; 

    public ResourceRef nodeLocations[]=null; // selected,dropped, etc, 
  
    /** Named parameter list */
    private Map<String,String> parameters = new Hashtable<String,String>();
  
    private VRSFactory vrs;
    
    private ActionCommand()
    {
        
    }
    
    /**
     * Enhanced method to parse ActionCommand string.
     * Current syntax:  ActionCommand [ "(", ArgumentList ")" ]  
     * 
     * @param str
     * @return
     */
    
    public ActionCommand(String str)
    {
        parse(str); 
    }
    
    public ActionCommand(ActionCommandType type)
    {
        this.actionType=type; 
    }

    private void parse(String actionStr)
    {
        String strs[]=actionStr.split(":"); 

        if ((strs==null) || strs.length<=0)
        {
            Global.errorPrintf(this,"Warning: Empty or NULL Command:%s\n",actionStr);
            return; 
        }
            
        String cmdStr=strs[0];
        
        this.actionType=ActionCommandType.valueOf(cmdStr);

        if (strs.length>=2)
        {
            parseArguments(strs[1]);
        }
        else
        {
            this.parameters.clear(); 
        }
    }
    
    /** Parse "name=value" argument list string */
    private void parseArguments(String argstr)
    {
        parameters.clear(); 
        
        String args[]=argstr.split(";"); 
        
        for (String arg:args)
        {
            String strs[]=arg.split("=");
            if (strs.length>=2)
            {
                this.set(strs[0],strs[1]); 
            }
        }
    }
   
    public String toString()
    {
       String actionStr=this.actionType.toString(); 
       
       if (this.parameters.size()>0) 
       {
           actionStr+=":";
           for (String arg:this.parameters.keySet())
           {
               String value=this.parameters.get(arg);
               actionStr+=arg+"="+value+";"; 
           }
       }
       
       debug("toString()="+actionStr); 
       return actionStr; 
    }
    
    private void debug(String str)
    {
         Global.debugPrintf(this,"%s\n",str);
    }

    public static ActionCommand fromString(String cmdstr)
    {
        ActionCommand cmd = new ActionCommand();
        cmd.parse(cmdstr); 
        return cmd; 
    }
    
    /** Return human readable action message */  
    public String getActionMessage()
    {
      return this.actionType.getActionMessage();   
    }
    
    /** If this action is a dynamic action, the actual method name is returned */ 
    public String getDynamicActionName()
    {
    	return this.parameters.get(DYNAMIC_METHOD_NAME);  
    }
    
    public void setDynamicMethodName(String methodName)
    {
        this.set(DYNAMIC_METHOD_NAME,methodName);  
    }
  
	protected void setViewer(String className)
	{
		debug(this+"Setting Viewer to:"+className); 
		this.set(VIEWER_CLASS_NAME,className); 
	}

	public VRSFactory getVRS()
	{
		if (vrs==null)
		{
		      String vrsname=this.parameters.get(VRS_NAME); 
		      this.vrs=UIGlobal.getRegistry().getVRSFactoryWithName(vrsname);
					
	          if (this.vrs==null)
	              Global.errorPrintf(this,"*** Internal Error: Could find VRS with name:%s\n",vrsname); 
				
	          debug(this+"getVRS() = "+vrs);
		}
		
		return vrs; 
	}
	
	protected void setVRS(VRSFactory vrs)
	{
		debug(this+"Setting VRS to:"+vrs); 
		
		this.vrs=vrs; 
		this.set(VRS_NAME,vrs.getName());
	}

	
	public String getViewerClassName()
	{
	    String viewerClassName=this.parameters.get(VIEWER_CLASS_NAME); 
		debug(this+VIEWER_CLASS_NAME+"="+viewerClassName);

		return viewerClassName; 
	}

    /** For single argument commands: set first and only argument */ 
    public void setArgument(String value)
    {
        set("arg0",value);  
    }
    
    /** For single argument commands: returns first and only argument */ 
    public String getArgument()
    {
        return this.parameters.get("arg0"); 
    }
    
    protected void set(String name,String value)
    {
        if (value==null)
        {
            this.parameters.remove(name); 
        }
            
        this.parameters.put(name,value); 
    }

    public static ActionCommand createFrom(String cmdstr)
    {
        return new ActionCommand(cmdstr); 
    }


}
