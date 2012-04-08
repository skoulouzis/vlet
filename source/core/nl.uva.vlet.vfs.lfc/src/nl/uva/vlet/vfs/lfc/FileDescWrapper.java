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
 * $Id: FileDescWrapper.java,v 1.2 2011-04-18 12:21:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:27 $
 */ 
// source: 

package nl.uva.vlet.vfs.lfc;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.glite.lfc.internal.FileDesc;
import nl.uva.vlet.glite.lfc.internal.ReplicaDesc;
import nl.uva.vlet.vrl.VRL;


public class FileDescWrapper
{
    private FileDesc fileDesc;
    /** Basename of File or Directory */
    private String fileBasename;  
    /** Keep full path of LFN which was used to create/query this FileDescription */ 
    private String lfnPath;       
    //private int numOfNodes = -1;
    /** Optional cached replica array */ 
    private ReplicaDesc[] replicas;
    //private boolean readOnly;
    // private boolean isLinkStat;
    
    /** Cached value of resolved Link File Description. */ 
    private  FileDesc resolvedLinkFileDesc=null;
	private String resolvedLinkPath; 
 
    public FileDescWrapper()
    {

    }

    public FileDescWrapper(FileDesc fdesc, String path)
    {
        setFileDesc(fdesc); 
        setNameAndPath(path); // use LFN path
    }

    public void setFileName(String fileName)
    {
        this.fileBasename = fileName; // must last part of path (basename) 
    }

    public String getFileName()
    {
        if (getFileDesc().getFileName() != null)
        {
            return getFileDesc().getFileName();
        }
        return fileBasename;
    }

    public void setFileDesc(FileDesc fileDesc)
    {
        this.fileDesc = fileDesc;
    }

    public FileDesc getFileDesc()
    {
        return fileDesc;
    }

    public void setNameAndPath(String path)
    {
        this.lfnPath = path;
        this.fileBasename=VRL.basename(path); // PTdB: use VRL methods 
    }

    /** Returns LNF path that was used to create this File Description ! */ 
    public String getPath()
    {
        return this.lfnPath;
    }

    // public int getNumOfNodes()
    // {
    // return getFileDesc().//numOfNodes;
    // }

//    public void setNumOfNodes(int num)
//    {
//        numOfNodes = num;
//    }

    public boolean isReadableByUser()
    {
        return ((this.fileDesc.getFileMode() & 0000400) != 0);
    }

    public boolean isWritableByUser()
    {
        return ((this.fileDesc.getFileMode() & 0000200) != 0);
    }

    public String getAllAttributesAsString()
    {
        String atrr = "";
        atrr = atrr + " ADate: " + this.getFileDesc().getADate();
        atrr = atrr + "\n ATime: " + this.getFileDesc().getATime();
        atrr = atrr + "\n CDate: " + this.getFileDesc().getCDate();
        atrr = atrr + "\n ChkSumType: " + this.getFileDesc().getChkSumType();
        atrr = atrr + "\n ChkSumValue: " + this.getFileDesc().getChkSumValue();
        atrr = atrr + "\n Comment: " + this.getFileDesc().getComment();
        atrr = atrr + "\n CTime: " + this.getFileDesc().getCTime();
        atrr = atrr + "\n FileClass: " + this.getFileDesc().getFileClass();
        atrr = atrr + "\n FileId: " + this.getFileDesc().getFileId();
        atrr = atrr + "\n FileMode: " + this.getFileDesc().getFileMode();
        atrr = atrr + "\n FileName: " + this.getFileName();
        atrr = atrr + "\n FileSize: " + this.getFileDesc().getFileSize();
        atrr = atrr + "\n Gid: " + this.getFileDesc().getGid();
        atrr = atrr + "\n MDate: " + this.getFileDesc().getMDate();
        atrr = atrr + "\n MTime: " + this.getFileDesc().getMTime();
        atrr = atrr + "\n Permissions: " + this.getFileDesc().getPermissions();
        atrr = atrr + "\n Status: " + this.getFileDesc().getStatus();
        atrr = atrr + "\n Uid: " + this.getFileDesc().getUid();
        atrr = atrr + "\n ULink: " + this.getFileDesc().getULink();
        atrr = atrr + "\n isDirectory: " + this.getFileDesc().isDirectory();
        atrr = atrr + "\n isFile: " + this.getFileDesc().isFile();
        atrr = atrr + "\n isSymbolicLink: "
                + this.getFileDesc().isSymbolicLink();
        return atrr;
    }

    /** Return file property as VAttribute */
    public VAttribute getAttribute(String name)
    {
        // null in => null out
        if (name == null)
            return null;

        VAttribute attr = null;

        if (StringUtil.equals(name, VAttributeConstants.ATTR_GID))
        {
            attr = new VAttribute(name, getFileDesc().getGid());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_UID))
        {
            attr = new VAttribute(name, getFileDesc().getUid());
        }

        // Permission as String
        else if (StringUtil.equals(name,
                VAttributeConstants.ATTR_PERMISSIONS_STRING))
        {
            attr = new VAttribute(name, getFileDesc().getPermissions());
        }
        // Permissions as Unix File Mode (int)
        else if (StringUtil.equals(name,
                VAttributeConstants.ATTR_UNIX_FILE_MODE))
        {
            attr = new VAttribute(name, getFileDesc().getFileMode());
        }
        else if (StringUtil
                .equals(name, VAttributeConstants.ATTR_CREATION_TIME))
        {
            // return unix time but create from milliseconds
            attr = VAttribute.createUnixTimeAttribute(name,
                    1000 * getFileDesc().getCTime());
        }
        else if (StringUtil.equals(name,
                VAttributeConstants.ATTR_MODIFICATION_TIME))
        {
            // return unix time :
            attr = VAttribute.createUnixTimeAttribute(name,
                    1000 * getFileDesc().getMTime());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_ACCESS_TIME))
        {
            // return unix time :
            attr = VAttribute.createUnixTimeAttribute(name,
                    1000 * getFileDesc().getATime());
        }

        // ===
        // LFC Attributes:
        // ===

        else if (StringUtil.equals(name, VAttributeConstants.ATTR_GRIDUID))
        {
            attr = new VAttribute(name, getFileDesc().getGuid());
        }
        else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_FILEID))
        {
            attr = new VAttribute(name, getFileDesc().getFileId());
        }
        else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_FILECLASS))
        {
            attr = new VAttribute(name, getFileDesc().getFileClass());
        }
        else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_ULINK))
        {
            attr = new VAttribute(name, getFileDesc().getULink());
        }
        else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_STATUS))
        {
            attr = new VAttribute(name, getFileDesc().getStatus());
        }
//        else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_COMMENT))
//        {
//            attr = new VAttribute(name, getFileDesc().getComment());
//        }
//        else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_CHECKSUMTYPE))
//        {
//            attr = new VAttribute(name, getFileDesc().getChkSumType());
//        }
//        else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_CHECKSUMVALUE))
//        {
//            attr = new VAttribute(name, getFileDesc().getChkSumValue());
//        }

        return attr;
    }

    /**
     * Set Cached replicas. 
     * May not be null as this retriggers the fetching of the replicas. 
     * If this file doesn't have any replicas use a ZERO sized array.  
     */ 
    public void setReplicas(ReplicaDesc[] replicaDesc)
    {
        this.replicas = replicaDesc;
    }

    /**
     * Returns cached replicas. 
     * If NULL replicas have not been fetched. 
     * If the array is a non null but EMPTY Array, 
     * the file has no replicas
     */ 
    public ReplicaDesc[] getReplicas()
    {
        return this.replicas;
    }

    /**
     * Set's the cached value of a resolved link. 
     */
    public void setResolvedLinkFileDesc(FileDesc ldesc)
    {
       this.resolvedLinkFileDesc=ldesc; 
    }

    public void setResolvedLinkPath(String linkToPath)
    {
    	this.resolvedLinkPath=linkToPath; 
    }
    
    /** Returns cached value of resolved link path (if it has one) */ 
    public String getResolvedLinkPath() 
    {
    	return resolvedLinkPath; 
    }
    
    /**
     * Get Resolved Link GUID. 
     * Returns NULL if this value has not been resolved yet ! 
     * 
     * @return
     */
    public String getResolvedLinkGuid()
    {
    	if (resolvedLinkFileDesc==null)
    		return null; 
    	
    	return this.resolvedLinkFileDesc.getGuid();  
    }
    
    /**
     * Holds the cached value of a resolved link. 
     * If null the link has not been resolved yet.  
     */
    public FileDesc getResolvedLinkFileDesc()
    {
    	return this.resolvedLinkFileDesc; 
    }

    public void clearCachedReplicas()
    {
        this.replicas=null; 
    }
    
    public void clearMetaData()
    {
        this.fileDesc=null; 
    }
 
    
}
