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
 * $Id: ProxyVRSImpl.java,v 1.4 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */
// source: 

package nl.uva.vlet.proxy.vrs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInvokeException;
import nl.uva.vlet.proxy.vrs.Methods.MethodNames;
import nl.uva.vlet.util.ResourceLoader;
import nl.uva.vlet.vfs.VACL;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.VResourceLink;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRenamable;

/**
 * Server/Service side implementation of the ProxyVRS. All methods are entered
 * through the 'invoke' method.
 * Under construction. 
 */
public class ProxyVRSImpl
{
    public static class NodeEntry
    {
        long time = 0;

        VNode node = null;

        int nrHits = 0;

        public NodeEntry(VNode node)
        {
            this.node = node;
            this.time = System.currentTimeMillis();
            nrHits = 1;
        }

        public void increment()
        {
            nrHits++;
        }
    }

    /** Micro cache */
    private Map<String, NodeEntry> nodeCache = new HashMap<String, NodeEntry>();

    VRSContext context;

    private ResourceLoader resourceLoader;

    public ProxyVRSImpl(VRSContext context)
    {
        this.context = context;
    }

    public Object invoke(Class<?> resultClass, String resourceVRL, String methodName, Object[] args) throws VlException
    {
        // debug("===");
        // debug(" Invoke:"+methodName+"() on:"+resourceVRL);

//        if (args != null)
//            for (Object arg : args)
//            {
//                // debug(" --- arg: "+arg.getClass().getCanonicalName()+"="+arg.toString());
//            }

        // ==================
        // UNMarschall: args.
        // ==================
        if (args != null)
            for (Object arg : args)
                if ((arg instanceof Serializable) == false)
                    throw new VlInvokeException("Object class is NOT Serializable:"
                            + args.getClass().getCanonicalName());

        Object result = invokeImpl(resourceVRL, methodName, args);

        // Allowed ?
        if (result == null)
            return null;

        if ((result instanceof Serializable) == false)
            throw new VlInvokeException("After method:" + methodName + ". Result class is NOT Serializable:"
                    + result.getClass().getCanonicalName());

        if (resultClass.isAssignableFrom(result.getClass()) == false)
            throw new VlInvokeException("After method:" + methodName
                    + ". Result class is NOT of expected type. Result=" + result.getClass().getCanonicalName() + "!="
                    + resultClass.getCanonicalName());

        // ================
        // Marshall: result
        // ================
        // debug(" --- result="+result.toString());
        // debug(" ===");
        return result;
    }

    // === ///

    private Object invokeImpl(String resourceVRL, String methodName, Object[] args) throws VlException
    {
        try
        {
            MethodNames method = Methods.MethodNames.valueOf(methodName);

            switch (method)
            {
                case CREATE:
                    checkArgs3(args, String.class, String.class, Boolean.class);
                    return _create(resourceVRL, (String) args[0], (String) args[1], (Boolean) args[2]);
                case GET_ATTRIBUTES:
                    checkArray(args, String.class);
                    return _getVAttributes(resourceVRL, (String[]) args);
                case GET_ATTRIBUTE_NAMES:
                    return _getVAttributeNames(resourceVRL);
                case GET_ACL:
                    return _getACL(resourceVRL);
                case GET_ACL_ENTITIES:
                    return _getACLEntities(resourceVRL);
                case GET_CHILD_VRLS:
                    return _getChildVRLs(resourceVRL);
                case GET_MIMETYPE:
                    return _getMimetype(resourceVRL);
                case GET_NAME:
                    return _getName(resourceVRL);
                case GET_PARENT_VRL:
                    return _getParentVRL(resourceVRL);
                case GET_RESOURCE_TYPE:
                    return _getResourceType(resourceVRL);
                case GET_RESOURCE_CLASS:
                    return _getResourceClass(resourceVRL);
                case GET_LINK_TARGET_VRL:
                    return _getLinkTargetVRL(resourceVRL);
                case GET_TEXT:
                    return _getText(resourceVRL);
                case SET_TEXT:
                    checkArgs1(args, String.class);
                    _setText(resourceVRL, (String) args[0]);
                    return null;
                case SET_ACL:
                    checkArray(args, VAttribute[].class); // array of arrays
                    _setACL(resourceVRL, (VAttribute[][]) args);
                    return null;
                case INSTANCEOF:
                    checkArgs1(args, String.class);
                    return _instanceOf(resourceVRL, args[0].toString());
                case RENAME:
                    checkArgs2(args, String.class, Boolean.class);
                    return _rename(resourceVRL, (String) args[0], (Boolean) args[1]);
                case PING:
                    return new Boolean(true);
                default:
                    throw new VlInvokeException("Unknown method:" + methodName);
            }
        }
        catch (Throwable t)
        {
            if (t instanceof VlInvokeException)
                throw (VlInvokeException) t;

            throw new VlInvokeException("Error when invoking method:" + methodName + ":" + resourceVRL + "\n"
                    + t.getMessage(), t);
        }
    }

    private void _setText(String resourceVRL, String text) throws VRLSyntaxException, VlException
    {
        getResourceLoader().setContents(new VRL(resourceVRL), text);
    }

    private String _getText(String resourceVRL) throws VRLSyntaxException, VlException
    {
        return getResourceLoader().getText(new VRL(resourceVRL));
    }

    private ResourceLoader getResourceLoader()
    {
        if (this.resourceLoader != null)
            this.resourceLoader = new ResourceLoader(this.context);

        return resourceLoader;
    }

    private VRL _create(String resourceVRL, String type, String name, Boolean ignoreExisting) throws VlException
    {
        VNode node = this.getNode(resourceVRL);

        if (ignoreExisting == null)
            ignoreExisting = true;

        if ((node instanceof VComposite) == false)
            throw new VlInvokeException("Resource cannot create (child) resources:" + resourceVRL);

        VComposite comp = (VComposite) node;

        VNode result = comp.createNode(type, name, ignoreExisting);
        cacheStore(result);
        return result.getVRL();
    }

    private VAttribute[] _getVAttributes(String resourceVRL, String[] args) throws VlException
    {
        return this.getNode(resourceVRL).getAttributes(args);
    }

    private String[] _getVAttributeNames(String resourceVRL) throws VlException
    {

        return this.getNode(resourceVRL).getAttributeNames();
    }

    private VAttribute[][] _getACL(String resourceVRL) throws VlException
    {
        VNode node = this.getNode(resourceVRL);
        if (node instanceof VACL)
        {
            return ((VACL) node).getACL();
        }

        return null;
    }

    private VAttribute[] _getACLEntities(String resourceVRL) throws VlException
    {
        VNode node = this.getNode(resourceVRL);
        if (node instanceof VACL)
        {
            return ((VACL) node).getACLEntities();
        }

        return null;
    }

    private void _setACL(String resourceVRL, VAttribute acl[][]) throws VlException
    {
        VNode node = this.getNode(resourceVRL);

        if (node instanceof VACL)
            ((VACL) node).setACL(acl);
        else
            throw new VlInvokeException("Resource doesn't have ACL interface:" + resourceVRL);

        return;
    }

    private VRL[] _getChildVRLs(String resourceVRL) throws VlException
    {
        VNode node = this.getNode(resourceVRL);

        if ((node instanceof VComposite) == false)
            return null;

        VNode nodes[] = _getNodes(resourceVRL);

        if (nodes == null)
            return null;

        VRL vrls[] = new VRL[nodes.length];
        for (int i = 0; i < nodes.length; i++)
        {
            vrls[i] = nodes[i].getVRL();
            cacheStore(nodes[i]);
        }

        return vrls;
    }

    private void cacheStore(VNode node)
    {
        synchronized (this.nodeCache)
        {
            nodeCache.put(node.getVRL().toString(), new NodeEntry(node));
        }
    }

    private void cacheRemove(String vrl)
    {
        synchronized (this.nodeCache)
        {
            nodeCache.remove(vrl);
        }
    }

    private VNode[] _getNodes(String resourceVRL) throws VlException
    {
        VNode node = this.getNode(resourceVRL);

        if ((node instanceof VComposite) == false)
            return null;

        VNode nodes[] = ((VComposite) node).getNodes();
        // put in cache: nodes:
        return nodes;
    }

    protected boolean _instanceOf(String vrl, String className) throws VlException
    {
        VNode vnode = getNode(vrl);

        if (className == null)
            return false;
        Class<?> classObj;
        try
        {
            classObj = getClass(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new VlInvokeException("instanceOf(): Couldn't find class:" + className, e);
        }

        if (classObj == null)
            return false;

        return vnode.getClass().isInstance(classObj);
    }

    protected String _getResourceType(String vrl) throws VlException
    {
        VNode node = getNode(vrl);
        if (node instanceof VResourceLink)
        {
            VRL targetVrl = ((VResourceLink) node).getTargetLocation();
            if (targetVrl != null)
                return targetVrl.toString();
        }
        return null;
    }

    protected String _getResourceClass(String vrl) throws VlException
    {
        VNode node = getNode(vrl);
        return node.getClass().getCanonicalName();
    }

    protected VRL _getParentVRL(String vrl) throws VlException
    {
        return getNode(vrl).getParentLocation();
    }

    protected VRL _getLinkTargetVRL(String vrl) throws VlException
    {
        VNode node = getNode(vrl);
        if (node instanceof VResourceLink)
            return ((VResourceLink) node).getTargetLocation();

        return null;
    }

    protected String _getName(String vrl) throws VlException
    {
        return getNode(vrl).getName();
    }

    protected String _getMimetype(String vrl) throws VlException
    {
        return getNode(vrl).getMimeType();
    }

    private Class<?> getClass(String className) throws ClassNotFoundException
    {
        return this.getClass().getClassLoader().loadClass(className);
    }

    private VNode getNode(String vrl) throws VlException
    {
        synchronized (nodeCache)
        {
            NodeEntry entry = nodeCache.get(vrl);
            if (entry != null)
            {
                entry.increment();
                return entry.node;
            }

            VNode node = this.context.openLocation(vrl);
            cacheStore(node);
            return node;
        }

    }

    private void checkArgs1(Object[] args, Class<?> class1) throws VlInvokeException
    {
        if (args == null)
            throw new VlInvokeException("Received EMPTY (Null) argument list. Expected 1 arguments");

        if (class1.isInstance(args[0]) == false)
            throw new VlInvokeException("Class type mismatch for 1st argument. Got:"
                    + args[0].getClass().getCanonicalName() + ", expected:" + class1.getCanonicalName());
    }

    private void checkArgs2(Object[] args, Class<?> class1, Class<?> class2) throws VlInvokeException
    {
        if (args == null)
            throw new VlInvokeException("Received EMPTY (Null) argument list. Expected 1 arguments");
        if (args.length != 2)
            throw new VlInvokeException("Received wrong nr of arguments. Expected 2 arguments, but got:" + args.length);
        if (class1.isInstance(args[0]) == false)
            throw new VlInvokeException("Class type mismatch for 1st argument. Got:"
                    + args[0].getClass().getCanonicalName() + ", expected:" + class1.getCanonicalName());
        if (class2.isInstance(args[1]) == false)
            throw new VlInvokeException("Class type mismatch for 2d argument. Got:"
                    + args[0].getClass().getCanonicalName() + ", expected:" + class1.getCanonicalName());
    }

    private void checkArgs3(Object[] args, Class<?> class1, Class<?> class2, Class<?> class3) throws VlInvokeException
    {
        if (args == null)
            throw new VlInvokeException("Received EMPTY (Null) argument list. Expected 1 arguments");
        if (args.length != 3)
            throw new VlInvokeException("Received wrong nr of arguments. Expected 3 arguments, but got:" + args.length);
        if (class1.isInstance(args[0]) == false)
            throw new VlInvokeException("Class type mismatch for 1st argument. Got:"
                    + args[0].getClass().getCanonicalName() + ", expected:" + class1.getCanonicalName());
        if (class2.isInstance(args[1]) == false)
            throw new VlInvokeException("Class type mismatch for 2d argument. Got:"
                    + args[0].getClass().getCanonicalName() + ", expected:" + class1.getCanonicalName());
        if (class3.isInstance(args[2]) == false)
            throw new VlInvokeException("Class type mismatch for 3rd argument. Got:"
                    + args[0].getClass().getCanonicalName() + ", expected:" + class1.getCanonicalName());
    }

    private void checkArray(Object[] args, Class<?> class1) throws VlInvokeException
    {
        if (args == null)
            throw new VlInvokeException("Received EMPTY (Null) argument list. Expected Array of:" + class1);

        if (args.length <= 0)
            throw new VlInvokeException("Received 0-size Array of type:" + class1.getCanonicalName());

        for (int i = 0; i < args.length; i++)
            if (class1.isInstance(args[i]) == false)
                throw new VlInvokeException("Class type mismatch for argument #" + i + ". Got:"
                        + args[i].getClass().getCanonicalName() + ", expected:" + class1.getCanonicalName());

    }

    private VRL _rename(String resourceVRL, String nameOrPath, Boolean nameIsPath) throws VlException
    {
        VNode node = this.getNode(resourceVRL);

        if ((node instanceof VRenamable) == false)
            throw new VlInvokeException("Resource is not renameable:" + resourceVRL);
        if (nameIsPath == null)
            nameIsPath = nameOrPath.startsWith("/");

        VRenamable comp = (VRenamable) node;
        VRL result = comp.rename(nameOrPath, nameIsPath);
        return result;
    }

}
