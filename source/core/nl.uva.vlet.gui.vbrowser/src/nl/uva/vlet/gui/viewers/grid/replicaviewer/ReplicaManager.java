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
 * $Id: ReplicaManager.java,v 1.2 2011-04-18 12:27:25 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:25 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.replicaviewer;

import java.util.ArrayList;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.util.bdii.StorageArea;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VLogicalFileAlias;
import nl.uva.vlet.vfs.VReplicatable;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.io.VResizable;

public class ReplicaManager
{
    private VRSContext vrsContext; 
    private VFSClient vfsClient; 
    
    private VRL vrl;
    private VNode node; 
    
    public ReplicaManager(VRSContext context)
    {
        this.vrsContext=context;
        this.vfsClient=new VFSClient(vrsContext); //private client 
    }

    public StringList getStorageAreasForVo(String vo) throws VlException 
    {
        StringList seList=new StringList(); 
        
        ArrayList<StorageArea> prefSAs = vrsContext.getBdiiService().getSRMv22SAsforVO(vo); 
        for (StorageArea sa:prefSAs)
        {
            seList.addUnique(sa.getHostname()); 
        }
        
        // sort in memory! ignore case
        seList.sort(true); 
        return seList;
    }
    
    public void setVRL(VRL vrl)
    {
        this.vrl=vrl;
        this.node=null; // clear cached node!
    }
    
    public VRL[] getReplicaVRLs() throws VlException
    {
        this.node=getNode(); 
            
        if ((node instanceof VReplicatable)==false) 
        {   
            return null; 
        }
                
        VRL replicas[]=((VReplicatable)node).getReplicas();
        return replicas;          
    }
    
//    public ArrayList<ReplicaInfo> getReplicaInfos() throws VlException
//    {
//        ArrayList<ReplicaInfo> repInfos=new ArrayList<ReplicaInfo>(); 
//        boolean getTransportURIs=false; 
//        StringList replicaHosts=new StringList();
//        
//        VRL replicas[]=getReplicaVRLs(); 
//                
//        for (VRL replica:replicas)
//        {
//            ReplicaInfo repInfo=new ReplicaInfo(replica); 
//            StringList attrNames=new StringList();
//            attrNames.add(VAttributeConstants.ATTR_LENGTH);
//            
//            if (getTransportURIs)
//            {
//                attrNames.add(VAttributeConstants.ATTR_TRANSPORT_URI); 
//            }
//            // catch IN LOOP exceptions  
//            try
//            {
//                VNode repNode=vfsClient.getNode(replica); 
//                VAttributeSet attrSet=repNode.getAttributeSet(attrNames.toArray());
//                repInfo.setLength(attrSet.getLongValue(VAttributeConstants.ATTR_LENGTH)); 
//                repInfo.setTransportURI(attrSet.getVRLValue(VAttributeConstants.ATTR_TRANSPORT_URI));
//                if (repNode.exists()==false)
//                {
//                    repInfo.setError(true);  
//                    repInfo.setExists(false);   
//                }
//                else
//                {
//                    repInfo.setError(false); 
//                    repInfo.setExists(true);  
//                }
//
//            }
//            catch (Exception e)
//            {
//                repInfo.setError(true); 
//                repInfo.setException(e);   
//            }
//            
//            repInfos.add(repInfo); 
//        }//for
//        
//        return repInfos;        
//    }

    /** Get Default Replica Attributes */ 
    public VAttributeSet getReplicaAttributes(VRL repVRL, boolean checksumInfo) throws VlException
    {
        StringList attrNames=new StringList(); 
        attrNames.add(VAttributeConstants.ATTR_TRANSPORT_URI);
        attrNames.add(VAttributeConstants.ATTR_LENGTH);
        attrNames.add(VAttributeConstants.ATTR_EXISTS);
        
        if (checksumInfo)
        {
            attrNames.add(VAttributeConstants.ATTR_CHECKSUM);
            attrNames.add(VAttributeConstants.ATTR_CHECKSUM_TYPE);
            attrNames.add(VAttributeConstants.ATTR_CHECKSUM_TYPES);
        }
        
        return getReplicaAttributes(repVRL,attrNames); 
    }
    
    public VAttributeSet getReplicaAttributes(VRL repVrl,StringList attrs) throws VlException
    {
        VNode repNode=vrsContext.openLocation(repVrl);  
        VAttributeSet attrSet=repNode.getAttributeSet(attrs.toArray());
        
        if (attrs.contains(VAttributeConstants.ATTR_EXISTS))
        {
            // in case ATTR_EXISTS isn't supported/doesn't work! 
            attrSet.set(VAttributeConstants.ATTR_EXISTS,node.exists());
        }
        
        return attrSet; 
    }

    public void addReplica(ITaskMonitor monitor, String se) throws VlException
    {
        VReplicatable rep=getRepFile();
        rep.replicateTo(monitor,se); 
    }

    private VReplicatable getRepFile() throws VlException
    {
        this.node=getNode(); 
        
        if  ((node instanceof VReplicatable)==false)
            throw new VlException("Resource doesn't have replicas:"+vrl);
        
        return (VReplicatable)node;  
    }
    
    private VNode getNode() throws VlException
    {
        if (this.node==null)
            this.node=vfsClient.getNode(vrl);
        
        return node; 
    }

    public void deleteReplica(ITaskMonitor monitor, String se) throws VlException
    {
        VReplicatable rep=getRepFile();
        rep.deleteReplica(monitor,se); 
    }
    
    public void unregisterReplica(ITaskMonitor monitor, String se) throws VlException
    {
        VReplicatable rep=getRepFile();
        VRL reps[]=rep.getReplicas(); 
        
        for (VRL vrl:reps)
        {
            if (vrl.hasHostname(se))
                rep.unregisterReplicas(new VRL[]{vrl}); 
        }
    }

    public long getFileSize() throws VlException
    {
        this.node=getNode(); 
    
        if (node instanceof VFile) 
            return ((VFile)node).getLength(); 
        return -1; 
    }

    public void updateLFCFileSize(long size) throws VlException
    {
        this.node=getNode(); 
        // Special interface for LFC files. 
        if (node instanceof VLogicalFileAlias) 
            ((VLogicalFileAlias)node).updateFileSize(size); 
        else if (node instanceof VResizable) 
            ((VResizable)node).setLength(size); 
        else
            throw new VlException("Error","File size can't be set for:"+node.getClass()+":"+node); 
    }
    
}
