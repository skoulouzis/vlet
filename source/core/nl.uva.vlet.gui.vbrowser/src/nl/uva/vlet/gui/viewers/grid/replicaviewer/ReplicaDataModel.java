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
 * $Id: ReplicaDataModel.java,v 1.4 2011-06-07 15:15:08 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:08 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.replicaviewer;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM_TYPES;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ERROR_TEXT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_INDEX;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LOCATION;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PERMISSIONS_STRING;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_STATUS;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_STORAGE_ELEMENT;

import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTableModel;
import nl.uva.vlet.vrl.VRL;

public class ReplicaDataModel extends ResourceTableModel
{
    public static enum ReplicaStatus {UNKNOWN,OK,DELETE,UNREGISTER,NEW,ERROR}; 
    
    private static final long serialVersionUID = -4130154452077132616L;

    // Cache of actual replica information, the data model is created using 
    // this information !  
    private Vector<ReplicaInfo> infos=new Vector<ReplicaInfo>(); 
    
    public static final String replicaAttrNames[]=
        {
            ATTR_PATH,
            ATTR_LENGTH, 
            ATTR_LOCATION,
            ATTR_CHECKSUM,
            ATTR_CHECKSUM_TYPE,
            ATTR_CHECKSUM_TYPES
        };
    
    public static final String tableAttrNames[]=
        {
            ATTR_INDEX,  
            ATTR_STORAGE_ELEMENT,
            ATTR_STATUS, 
            ATTR_ERROR_TEXT
        };
    
    
    public ReplicaDataModel()
    {
        super(); 
        init(); 
    }
    
    public void init()
    {
        StringList headers=new StringList(); 
        
        headers.add(ATTR_INDEX);
        headers.add(ATTR_STORAGE_ELEMENT);
        headers.add(ATTR_PATH);
        headers.add(ATTR_LENGTH);
        headers.add(ATTR_STATUS);
        headers.add(ATTR_ERROR_TEXT);
        
        // Update header to be viewed only: Presentation is part of "View" 
        this.setHeaders(headers.toArray()); 
        
        // Add extra header to data model:
        headers.add(ATTR_LOCATION);
        headers.add(ATTR_PERMISSIONS_STRING);
        headers.add(ATTR_CHECKSUM);
        headers.add(ATTR_CHECKSUM_TYPE);
        headers.add(ATTR_CHECKSUM_TYPES);

        // Set potential headers: 
        this.setAllHeaders(headers);
        // clear dummy data
        this.clearData(); 
    }

    public int addReplica(ReplicaInfo rep)
    {
        Global.debugPrintf(this,"Adding replica:%s\n",rep);
        int index=infos.size(); 
        
        this.infos.add(rep); 
        
        update();
        return index; 
    }
    
    public void clearData()
    {
        super.clearData(); // clear resource table 
        this.infos.clear(); // clear cached replica infos; 
    }
    
    /**
     * Update data from ReplicaInfo into TableModel
     * This will reset the "Status" if marked to be Deleted or Unregisterd!  
     */
    public void update()
    {
        for (ReplicaInfo info:infos)
        {
         // New Replica info 
            VRL vrl=info.getVRL(); 
            String host=vrl.getHostname(); 
            
            if (this.getRow(info.getHostname())==null)
            {
                // create new row: 
                VAttributeSet attrs=new VAttributeSet();
                int index=this.addRow(host, attrs);
                // update index FIRST  
                this.setValue(host,ATTR_INDEX,""+index); 
            }
            // Update row attributes  
            VAttributeSet attrs=new VAttributeSet();
            
            Global.debugPrintf(this,"Adding replica:%s\n",info.getVRL());     
                
            attrs.put(ATTR_STORAGE_ELEMENT,info.getVRL().getHostname());
            attrs.put(new VAttribute(ATTR_LOCATION,info.getVRL())); 
            attrs.put(ATTR_PATH,info.getVRL().getPath());
                
                    
            if ((info.hasError()==false) && (info.getException()==null))
            {
                attrs.put(ATTR_STATUS,""+ReplicaStatus.OK);
                attrs.put(ATTR_ERROR_TEXT,"");
                if (info.getLength()>=0) 
                    attrs.put(new VAttribute(ATTR_LENGTH,info.getLength()));
            }
            else
            {
                attrs.put(ATTR_STATUS,""+ReplicaStatus.ERROR);
                attrs.put(ATTR_ERROR_TEXT,info.getException().getMessage());
                attrs.put(new VAttribute(ATTR_LENGTH,"?")); 
            }
            
            this.setValues(host,attrs.toArray()); 
        }
    }
    
    public int addReplica(VRL replicaVRL)
    {
        return addReplica(new ReplicaInfo(replicaVRL)); 
    }
    
    public int addNewReplicaToBe(String host)
    {
        int index= this.getRowCount();
        String key=host;   
        VAttributeSet attrs=new VAttributeSet();
        attrs.put(ATTR_INDEX,index); 
        attrs.put(ATTR_STORAGE_ELEMENT,host); 
        attrs.put(ATTR_PATH,"?"); 
        attrs.put(ATTR_STATUS,""+ReplicaStatus.NEW);
        attrs.put(ATTR_ERROR_TEXT,"");
        
        return this.addRow(key, attrs);
    }
    
    public void markToBeDeleted(String host, boolean delete)
    {
        String status=this.getAttrStringValue(host,ATTR_STATUS);
        Global.debugPrintf(this,"Status of replica:%s=%s\n",host,status); 
        
        if (status!=null)
        {
            if (delete)
            {
                if (StringUtil.equalsIgnoreCase(status,ReplicaStatus.NEW))
                {
                    // remove new entry:
                    //int index=this.delRow(host);
                    //Global.debugPrintln(this,"Warning: row deleted:"+index);  
                }
                else
                {
                    // mark delete
                    this.setValue(host,ATTR_STATUS,""+ReplicaStatus.DELETE);
                }
            }
            else if (StringUtil.equalsIgnoreCase(status,ReplicaStatus.DELETE))          
            {
                // undelete
                this.setValue(host,ATTR_STATUS,""+ReplicaStatus.OK);
            }
            
        }
    }
    
    public void markToBeUnregistered(String host, boolean unregister)
    {
        String status=this.getAttrStringValue(host,ATTR_STATUS);
        Global.debugPrintf(this,"Status of replica:%s=%s\n",host,status); 
        
        if (status!=null)
        {
            // Only Error  Replica's may be Unregistered !  
            if ((unregister) && (StringUtil.equalsIgnoreCase(status,ReplicaStatus.ERROR)))
            {
                // mark unregister
                this.setValue(host,ATTR_STATUS,""+ReplicaStatus.UNREGISTER);
            }
            else if ((!unregister) && (StringUtil.equalsIgnoreCase(status,ReplicaStatus.DELETE)))          
            {
                // ununregister: unknown ? 
                this.setValue(host,ATTR_STATUS,""+ReplicaStatus.UNKNOWN);
            }
        }
    }

    public String[] getNewSEs()
    {
        StringList ses=new StringList();
        
        for (int i=0;i<this.getRowCount();i++)
        {
            VAttribute statAttr=this.getAttribute(i,ATTR_STATUS);
            VAttribute seAttr=this.getAttribute(i,ATTR_STORAGE_ELEMENT); 
            if ((statAttr==null) || (seAttr==null))
                continue; 
            String statStr=statAttr.getStringValue(); 
            if (statStr.equals(ReplicaStatus.NEW.toString()))
                    ses.add(seAttr.getStringValue());
        }
        
        return ses.toArray(); 
    }
    
    public String[] getDeletedSEs()
    {
        StringList ses=new StringList();
        
        for (int i=0;i<this.getRowCount();i++)
        {
            VAttribute statAttr=this.getAttribute(i,ATTR_STATUS);
            VAttribute seAttr=this.getAttribute(i,ATTR_STORAGE_ELEMENT); 
            if ((statAttr==null) || (seAttr==null))
                continue; 
            String statStr=statAttr.getStringValue(); 
            
            if (statStr.equals(ReplicaStatus.DELETE.toString()))
                ses.add(seAttr.getStringValue());
        }
        
        return ses.toArray(); 
    }
    
    public String[] getUnregisteredSEs()
    {
        StringList ses=new StringList();
        
        for (int i=0;i<this.getRowCount();i++)
        {
            VAttribute statAttr=this.getAttribute(i,ATTR_STATUS);
            VAttribute seAttr=this.getAttribute(i,ATTR_STORAGE_ELEMENT); 
            if ((statAttr==null) || (seAttr==null))
                continue; 
            String statStr=statAttr.getStringValue(); 
            
            if (statStr.equals(ReplicaStatus.UNREGISTER.toString()))
                ses.add(seAttr.getStringValue());
        }
        
        return ses.toArray(); 
    }

    /** Return VRLs of registered replicas */ 
    public VRL[] getReplicaVRLs() throws VRLSyntaxException
    {
        VRL vrls[]=new VRL[infos.size()];  
        for (int i=0;i<infos.size();i++)
            vrls[i]=infos.get(i).getVRL();
        return vrls; 
    }

    public boolean setReplicaAttributes(VRL repVrl, VAttributeSet attrs)
    {
        ReplicaInfo repInfo = this.getReplicaInfo(repVrl);
        repInfo.setAttributes(attrs);
        update(); 
        // update table model 
        //this.setValues(repVrl.getHostname(),attrs.toArray());
      
        return true;
    }

    private ReplicaInfo getReplicaInfo(VRL repVrl)
    {
        for (ReplicaInfo info:this.infos)
            if (info.getVRL().equals(repVrl))
                return info;
        
        return null; 
    }

    public void setReplicaException(VRL repVrl, Exception e)
    {
        ReplicaInfo repInfo = this.getReplicaInfo(repVrl);
        repInfo.setException(e); 
        repInfo.setError(true); 
        repInfo.setLength(-1); 
        
        update();
        
//        RowData row = this.getRow(repVrl.getHostname());
//        row.removeValue(ATTR_LENGTH); 
//        row.setValue(ATTR_STATUS,""+ReplicaStatus.ERROR); 
//        row.setValue(ATTR_ERROR_TEXT,""+e.getMessage());
//        int index=row.getIndex(); 
//        this.fireTableRowsUpdated(index,index); 
    }

    public VRL getReplicaVRLofSE(String hostname)
    {
        for (ReplicaInfo info:infos)
            if (info.getVRL().hasHostname(hostname))
                return info.getVRL(); 
        return null; 
    }

    public long[] getReplicaLengths()
    {
        if (infos==null)
            return null; 
        
        long sizes[]=new long[infos.size()];
        
        for (int i=0;i<infos.size();i++)
            sizes[i]=infos.get(i).getLength();
        
        return sizes; 
    }

    // Return private array  
    public ReplicaInfo[] getReplicaInfos()
    {
        ReplicaInfo infos[]=new ReplicaInfo[this.infos.size()];
        return this.infos.toArray(infos); 
    }

    /**
     * Check whether all replicas, which don't have an error, match the specified size
     */  
    public boolean replicaLengthsEqual(long size)
    {
        for (ReplicaInfo info:infos)
        {
            if (info.hasError()==false)
                if (info.getLength()!=size)
                    return false;
        }
        
        return true;
    }

    public long getMaxReplicaLength()
    {
        long len=0; 
        
        for (ReplicaInfo info:infos)
        {
            if (info.hasError()==false)
                if (info.getLength()>len)
                    len=info.getLength(); 
        }
        return len;
    }
    
    
}
