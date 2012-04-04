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
 * $Id: SRMFile.java,v 1.4 2011-09-26 14:07:41 ptdeboer Exp $  
 * $Date: 2011-09-26 14:07:41 $
 */ 
// source: 

package nl.uva.vlet.vfs.srm;

import gov.lbl.srm.v22.stubs.TFileStorageType;
import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;
import gov.lbl.srm.v22.stubs.TRetentionPolicyInfo;

import java.io.InputStream;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vfs.VChecksum;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VThirdPartyTransferable;
import nl.uva.vlet.vfs.VTransportable;
import nl.uva.vlet.vfs.VUnixFileMode;
import nl.uva.vlet.vrl.VRL;

import org.apache.axis.types.UnsignedLong;

/**
 * SRM File 
 * 
 * @author Piter T. de Boer
 */
public class SRMFile extends VFile implements VThirdPartyTransferable,
        VUnixFileMode, VChecksum, VTransportable 
{
    // private PathDetail srmDetails;
    private TMetaDataPathDetail srmDetails;
    
    private SRMFileSystem srmfs;
    
    private boolean detailsFetched=false; 

    public SRMFile(SRMFileSystem srmfs, TMetaDataPathDetail info)
            throws VRLSyntaxException
    {
        super(srmfs, (VRL) null);
        this.srmDetails = info;
        this.srmfs = srmfs;
        this.detailsFetched=(srmDetails!=null); 
        
        VRL vrl = srmfs.createPathVRL(info.getPath(),null);
        this.setLocation(vrl);
    }

    public SRMFile(SRMFileSystem client, String path)
            throws VRLSyntaxException
    {
        super(client, (VRL) null);
        this.detailsFetched=(srmDetails!=null);
        this.srmfs=client; 
        
        VRL vrl = srmfs.createPathVRL(path,null);
                this.srmfs = client;
        this.setLocation(vrl);
    }
    
    @Override
    public boolean create(boolean force) throws VlException
    {
        VFile file = srmfs.createFile(getVRL(), force);
        return (file != null);
    }
    
    @Override
    public String[] getAttributeNames()
    {
        String superList[] = super.getAttributeNames();
        StringList attrList = new StringList(superList);

        attrList.add(SRMConstants.ATTR_SRM_RETENTION_POLICY);
        attrList.add(SRMConstants.ATTR_SRM_STORAGE_TYPE); 
        
        attrList.add(VAttributeConstants.ATTR_TRANSPORT_URI);

        return attrList.toArray();
    }

    @Override
    public VAttribute[] getAttributes(String names[]) throws VlException
    {
        VAttribute[] attrs = new VAttribute[names.length];

        // optional caching:
        for (int i = 0; i < names.length; i++)
        {
            if (names[i] != null)
                attrs[i] = getAttribute(names[i]);
            else
                attrs[i] = null;
        }

        return attrs;
    }

    @Override
    public boolean setAttribute(VAttribute attr) throws VlException
    {
        String name = attr.getName();

        if (name.compareTo(SRMConstants.ATTR_SRM_RETENTION_POLICY) == 0)
        {
            // update policy
        }

        return false;
    }
    
    @Override
    public VAttribute getAttribute(String name) throws VlException
    {
        VAttribute attr = null;

        if (name.compareTo(SRMConstants.ATTR_SRM_RETENTION_POLICY) == 0)
        {
            attr = new VAttribute(name, this.getRetentionPolicy());
            //Not Now: it is possible to set this attribute using setAttribute() method.
           // attr.setEditable(true);
        }
        else if (name.compareTo(SRMConstants.ATTR_SRM_STORAGE_TYPE) == 0)
        {
            attr = new VAttribute(name, this.getFileStorageType());
        }
        else if (name.compareTo(VAttributeConstants.ATTR_TRANSPORT_URI) == 0)
        {
            // A VRL is an URI.
            attr = new VAttribute(name, this.getTransportVRL());
        }
        // else if (name.startsWith(VAttributeConstants.ATTR_CHECKSUM))
        // {
        // attr = new VAttribute(name, this.getChecksum(getChecksumTypes()[0]));
        // }
        else
        {
            // call super:
            attr = super.getAttribute(name);
        }

        return attr;
    }

    private String getRetentionPolicy() throws VlException
    {
        fetchFullDetails();

        TRetentionPolicyInfo info = this.srmDetails.getRetentionPolicyInfo();
        
        if (info == null)
            return "";

        return info.getRetentionPolicy().getValue();

    }

    private String getFileStorageType() throws VlException
    {
       
        fetchFullDetails();

        TFileStorageType storageType = this.srmDetails.getFileStorageType();
        
        if (storageType == null)
        {
            Global.warnPrintln(this,"StorageTYpe==null => Requery Type for:"+this);
            // requery: ls doesn't always return storage type 
            this.srmDetails=null; 
            this.fetchFullDetails();
        }
        
        storageType = this.srmDetails.getFileStorageType();
        if (storageType == null)
        {
            Global.warnPrintln(this,"Really no StorageType for"+this);
            return "";
        }
        
        String valstr=storageType.getValue();
        Global.debugPrintln(this,"StorageType for"+this+"="+valstr); 
        return valstr; 
    }
    
    @Override
    public long getLength() throws VlException
    {
        fetchFullDetails();

        UnsignedLong value = this.srmDetails.getSize();

        if (value == null)
            return -1;

        return value.longValue();
    }

    private TMetaDataPathDetail fetchFullDetails() throws VlException
    {
        if (srmDetails==null)
        {
            srmDetails = srmfs.queryPath(getPath());
        }
        return srmDetails; 
    }

    @Override
    public boolean exists() throws VlException
    {
    	if (srmfs.pathExists(getPath())==false)
    		return false;
    	
    	// path exists, now check for file type ! 	
    	return this.srmfs.isFile(this.fetchFullDetails().getType());  
    }

    @Override
    public boolean isReadable() throws VlException
    {

        if (getPermissionsString().charAt(1) == 'R')
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean isWritable() throws VlException
    {
        if (getPermissionsString().charAt(2) == 'W')
        {
            return true;
        }
        return false;
    }
    
    @Override
    public InputStream getInputStream() throws VlException
    {
        ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor(
                "getInputStream:" + this, -1);
        return srmfs.createInputStream(monitor, this.getPath());
    }

    public VRL getTransportVRL() throws VlException
    {
        ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor(
                "getInputStream:" + this, -1);
        return srmfs.getTransportVRL(monitor, this.getPath());
    }
    
    @Override
    public SRMOutputStream getOutputStream() throws VlException
    {
        ITaskMonitor minitor = ActionTask.getCurrentThreadTaskMonitor(
                "getOutputStream:" + this, -1);

        String orgPath = getPath();
        long val = SRMFileSystem.fileRandomizer.nextLong();
        String randStr = null;
        // Beautification: avoid '-' before number
        if (val < 0)
            randStr = "9" + (-val);
        else
            randStr = "" + val;

        String tmpPath = orgPath + "." + randStr;

        SRMOutputStream outps = srmfs.createNewOutputStream(minitor,
                tmpPath, true);
        // replace current file after closing of outputstream with new
        // created file
        outps.setFinalPathAfterClose(tmpPath, orgPath);
        
        //the file size will change. Get rid of the cache 
        detailsFetched = false;
        srmDetails = null;

        return outps;
    }
    
    @Override
    public VRL rename(String newName, boolean renameFullPath)
            throws VlException
    {
        if (renameFullPath)
        {
            return srmfs.mv(getPath(), newName);
        }
        return srmfs.mv(getPath(), getVRL().getParent().getPath() + "/"
                + newName);
    }
    
    @Override
    public boolean delete() throws VlException
    {
        return this.srmfs.deleteFile(this.getPath());
    }

    @Override
    public long getModificationTime() throws VlException
    {
        // no full details needed since the default details
        // already contain the modification time.
        return this.srmfs.createModTime(srmDetails);

    }

    private void Debug(String msg)
    {
        Global.debugPrintln(this, msg);
        // Global.errorPrintln(this, msg);
    }

    @Override
    public VFile activePartyTransferFrom(ITaskMonitor monitor,
            VRL remoteSourceLocation) throws VlException
    {
        return this.srmfs.doActiveTransfer(monitor, remoteSourceLocation, this);
    }

    @Override
    public VFile activePartyTransferTo(ITaskMonitor monitor,
            VRL remoteTargetLocation) throws VlException
    {
        return this.srmfs.doTransfer(monitor, this, remoteTargetLocation);
    }

    @Override
    public boolean canTransferFrom(VRL remoteLocation, StringHolder explanation)
            throws VlException
    {
        return this.srmfs.checkTransferLocation(remoteLocation,
                explanation, false);
    }

    @Override
    public boolean canTransferTo(VRL remoteLocation, StringHolder explanation)
            throws VlException
    {
        return this.srmfs.checkTransferLocation(remoteLocation,
                explanation, true);
    }
    
    @Override
    public SRMFileSystem getFileSystem()
    {
        return (SRMFileSystem) super.getFileSystem();
    }
    
    @Override
    public int getMode() throws VlException
    {
        fetchFullDetails();
        return this.srmfs.getUnixMode(srmDetails);
    }
    
    @Override
    public void setMode(int mode) throws VlException
    {
        fetchFullDetails();
        this.srmfs.setUnixMode(getLocation(), this.srmDetails, mode);
    }

    @Override
    public String getChecksum(String algorithm) throws VlException
    {
        fetchFullDetails();
        String checksum = null;
        String srmChecksumType = srmDetails.getCheckSumType();
        
        // can we get the requested algorithm??
        if (srmChecksumType!=null && srmChecksumType.equalsIgnoreCase(algorithm))
        {
            Debug("Got checksum from SRM service");
            checksum = srmDetails.getCheckSumValue();
        }
        if (checksum == null)
        {            
            Debug("Gouldn't get checksum from SRM service, trying from transfer VRL.");

            VRL tVRL = getTransportVRL();
            Debug("Got transfer VRL: " + tVRL);
            VFile file = srmfs.getVFSClient().newFile(tVRL);
            Debug("Got VFile: ");
            if (file instanceof VChecksum)
            {
                checksum = ((VChecksum) file).getChecksum(algorithm);
            }
        }
        return checksum;
    }

    @Override
    public String[] getChecksumTypes() throws VlException
    {
        fetchFullDetails();
        
        //add srm service types 
        StringList types = new StringList();  
        String type =  srmDetails.getCheckSumType();
                
        if(type!=null){
            types.add(type);
        }
//        //and tVRL types 
//        VRL tVRL = getTransportVRL();
//        VFile file = srmfs.getVFSClient().newFile(tVRL);
//        if (file instanceof VChecksum)
//        {
//            types.add(((VChecksum) file).getChecksumTypes());
//        }
        
        return types.toArray();
    }

}
