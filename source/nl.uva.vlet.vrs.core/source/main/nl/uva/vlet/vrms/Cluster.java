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
 * $Id: Cluster.java,v 1.3 2011-04-18 12:00:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:36 $
 */ 
// source: 

package nl.uva.vlet.vrms;

import java.util.Vector;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;


/**
 * Cluster resource VNode interface to the Registry. 
 */
 
public abstract class Cluster extends VNode implements VComposite
{
    public static final String CLUSTER_TYPE="Cluster"; 
    public static final String SERVICE_TYPE="Service";
    
    private final static String[] supportedTypes={CLUSTER_TYPE,SERVICE_TYPE}; 
    
    /** A Cluster can consist of subclusters */ 
    //Vector subclusters[]; => Use Registry !!! 
   
    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#getLocation()
     */
    
    ClusterElement cluster_element=null;
    Cluster parent=null; 
    
    Vector<VNode> subClusterServices=new Vector<VNode>(); 

    public String getType()
    {
        return CLUSTER_TYPE; 
    }
    
    public class ClusterElement
    {
        String name=null;  // das2
        String root_host=null; // fs0, fs1,fs2 
        
        ClusterElement(String name,String root_host)
        {
            this.name=name; 
            this.root_host=root_host; 
        }
    }
    
    protected Cluster(VRSContext vrs,String name,String root_host)
    {
    	super(vrs,null);
        this.cluster_element=new ClusterElement(name,root_host);
    }
    
    /** Default das2 Cluster Factory */ 
    
    public static Cluster createDas2() throws VlException
    {
        Cluster das2=null;
        
        // fill in default clusters
        /*
        Cluster das2=new Cluster("das2","fs0.das2.nikhef.nl");
        
        Cluster dasfs0=new Cluster("fs0","fs0.das2.cs.vu.nl"); 
        Cluster dasfs1=new Cluster("fs1","fs1.das2.liacs.nl"); 
        Cluster dasfs2=new Cluster("fs2","fs2.das2.nikhef.nl"); 
        Cluster dasfs3=new Cluster("fs3","fs3.das2.ewi.tudelft.nl"); 
 
        
        das2.addNode(dasfs0);
        das2.addNode(dasfs1);
        das2.addNode(dasfs2);
        das2.addNode(dasfs3);
        */
        return das2; 
    }
    
   
    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#getTypes()
     */
    public String[] getResourceTypes()
    {
        // TODO Auto-generated method stub
        return supportedTypes;
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#getNrOfChilds()
     */
    public long getNrOfNodes()
    {
        return subClusterServices.size();
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#getChilds()
     */
    public VNode[] getNodes()
    {
        int len=subClusterServices.size(); 
        
        VNode[] nodes=new VNode[len]; 
        
        for (int i=0;i<len;i++)
        {
            nodes[i]=(VNode)subClusterServices.elementAt(i); 
        }
        
        return nodes; 
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#isComposite()
     */
    public boolean isComposite()
    {
        // TODO Auto-generated method stub
        return true;
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#getAttributeNames()
     */
    public String[] getAttributeNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#getAttribute(java.lang.String)
     */
    public VAttribute getAttribute(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#setAttribute
     */
    public boolean setAttribute(VAttribute attr) throws NotImplementedException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Adds (Sub) Cluster to the registry.
     */
    public VNode addNode(VNode node) throws VlException
    {
      
        return null; 
    }

  
    /**
     * Removes (Sub) Cluster/Service to the registry.
     */
    public boolean delNode(VNode node) throws VlException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Removes (Sub) Clusters/Services from the registry.
     */
    public boolean delNodes(VNode[] nodes) throws VlException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean delete(boolean recurse) throws VlException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean delete() throws VlException
    {
        // TODO Auto-generated method stub
        return delete(false); 
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#createChild(java.lang.String)
     */
    public VNode createChild(String type) throws VlException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VNode#createChild(java.lang.String, java.lang.String)
     */
    public VNode createChild(String type, String name) throws VlException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VComposite#addNode(nl.uva.vlet.vrs.i.VNode, boolean)
     */
    public VNode addNode(VNode node, boolean isMove) throws VlException
    {
        this.subClusterServices.add(node);
        
        return node;
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VComposite#addNodes(nl.uva.vlet.vrs.i.VNode[], boolean)
     */
    public VNode[] addNodes(VNode[] nodes, boolean isMove) throws VlException
    {
        for (int i=0;i<nodes.length;i++)
        {
            if (nodes[i] instanceof Cluster)
            {
                Cluster cluster=(Cluster)nodes[i]; 
                cluster.parent=this; 
                this.subClusterServices.add(cluster);
            }
        }
        
        return nodes;
    }

    public VNode createChild(String type, boolean force) throws VlException
    {
        return null;
    }

    public VNode createNode(String type, String name, boolean force) throws VlException
    {
        return null;
    }


}
