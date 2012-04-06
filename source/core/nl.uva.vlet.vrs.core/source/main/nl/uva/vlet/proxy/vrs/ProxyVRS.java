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
 * $Id: ProxyVRS.java,v 1.3 2011-04-18 12:00:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:38 $
 */ 
// source: 

package nl.uva.vlet.proxy.vrs;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;

/** 
 * Proxy VRS interface for remote invocation of VRS/VFS methods! 
 * Method wrappers call invoke() method of the Proxy Implementation. 
 * Under construction.  
 */
public class ProxyVRS
{
    ProxyVRSImpl invokeImpl; 
    
    public ProxyVRS(VRSContext context)
    {
        invokeImpl=new ProxyVRSImpl(context); 
    }

    /** Actual invoker 
     * @throws VlException */ 
    protected Object invoke(Class<?> resultClass,VRL resourceVRL,String methodName,Object[] args) throws VlException 
    {
        return this.invokeImpl.invoke(resultClass,resourceVRL.toString(),methodName,args);  
    }
    
    protected Object invoke(Class<?> resultClass,VRL resourceVRL,String methodName,String arg1) throws VlException 
    {
        return this.invokeImpl.invoke(resultClass,resourceVRL.toString(),methodName,new Object[]{arg1});  
    }
    
    protected Object invoke(Class<?> resultClass,VRL resourceVRL,String methodName) throws VlException 
    {
        return this.invokeImpl.invoke(resultClass,resourceVRL.toString(),methodName,(Object[])null);  
    }
   
    public Boolean instanceOf(VRL vrl, String className) throws VlException
    {
        return (Boolean)invoke(Boolean.class,vrl,Methods.MethodNames.INSTANCEOF.toString(),className); 
    }
    
    /** Equivalent to VNode.getType() */ 
    public String getResourceType(VRL vrl) throws VlException
    {
        return (String)invoke(String.class,vrl,Methods.MethodNames.GET_RESOURCE_TYPE.toString()); 
    }
    
    public String getMimetype(VRL vrl) throws VlException
    {
        return (String)invoke(String.class,vrl,Methods.MethodNames.GET_MIMETYPE.toString());
    }
    
    public VAttribute[] getAttributes(VRL vrl,String[] attrNames) throws VlException
    {
        return (VAttribute[])invoke(VAttribute[].class,vrl,Methods.MethodNames.GET_ATTRIBUTES.toString(),attrNames); 
    }
    
    public VAttribute getAttribute(VRL vrl,String attrName) throws VlException
    {
        VAttribute[] attrs=(VAttribute[])invoke(VAttribute[].class,vrl,Methods.MethodNames.GET_ATTRIBUTES.toString(),new String[]{attrName});
        if ((attrs==null) || (attrs.length<=0))
            return null; 
        else
            return attrs[0]; 
    }
    
    public VRL[] getChildVRLs(VRL vrl) throws VlException
    {
        return (VRL[])invoke(VRL[].class,vrl,Methods.MethodNames.GET_CHILD_VRLS.toString(),(Object[])null); 
    }

    public String getName(VRL vrl) throws VlException
    {
        return (String)invoke(String.class,vrl,Methods.MethodNames.GET_NAME.toString(),(Object[])null); 
    }
    
    public String[] getAttributeNames(VRL vrl) throws VlException
    {
        return (String[])invoke(String[].class,vrl,Methods.MethodNames.GET_ATTRIBUTE_NAMES.toString(),(Object[])null); 
    }

    public VRL resolveLink(VRL vrl) throws VlException
    {
        return (VRL)invoke(VRL.class,vrl,Methods.MethodNames.GET_LINK_TARGET_VRL.toString(),(Object[])null); 
    }

    public String getClass(VRL vrl) throws VlException
    {
        return (String)invoke(String.class,vrl,Methods.MethodNames.GET_RESOURCE_CLASS.toString(),(Object[])null);
    }

    public VRL getParentVRL(VRL vrl) throws VlException
    {
        return (VRL)invoke(VRL.class,vrl,Methods.MethodNames.GET_PARENT_VRL.toString(),(Object[])null);
    }

    public void setAttributes(VRL vrl, VAttribute[] attrs) throws VlException
    {
        invoke(null,vrl,Methods.MethodNames.SET_ATTRIBUTES.toString(),attrs);
    }
    
    public void delete(VRL vrl, boolean recurse) throws VlException
    {
        invoke(null,vrl,Methods.MethodNames.DELETE.toString(),(Object[])null);
    }

    public VRL rename(VRL vrl, String newName,Boolean nameIsPath) throws VlException
    {
        return (VRL)invoke(VRL.class,vrl,Methods.MethodNames.RENAME.toString(),
                new Object[]{newName,new Boolean(nameIsPath)});
    }
    
    public VRL create(VRL vrl, String type,String name,boolean ignoreExisting) throws VlException
    {
        Object args[]=new Object[]{type,name,new Boolean(ignoreExisting)}; 
        return (VRL)invoke(VRL.class,vrl,Methods.MethodNames.CREATE.toString(),args);
    }

    public void setACL(VRL vrl, VAttribute[][] acl) throws VlException
    {
        invoke(null,vrl,Methods.MethodNames.SET_ACL.toString(),acl);
    }

    public VAttribute[][] getACL(VRL vrl) throws VlException
    {
        return (VAttribute[][])invoke(VAttribute[][].class,vrl,Methods.MethodNames.GET_ACL.toString(),(Object[])null);
    }

    public VAttribute[] getACLEntities(VRL vrl) throws VlException
    {
        return (VAttribute[])invoke(VAttribute[].class,vrl,Methods.MethodNames.GET_ACL_ENTITIES.toString(),(Object[])null);
    }
 
}
