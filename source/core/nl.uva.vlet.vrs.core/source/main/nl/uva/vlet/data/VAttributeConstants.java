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
 * $Id: VAttributeConstants.java,v 1.6 2011-06-01 12:04:23 ptdeboer Exp $  
 * $Date: 2011-06-01 12:04:23 $
 */ 
// source: 

package nl.uva.vlet.data;

import nl.uva.vlet.GlobalConfig;

public class VAttributeConstants
{
	/**
	 * Resource's last access time (if supported). 
	 * The type of returned attribute is compatible with the Date class. 
	 * milliseconds or "time since epoch" is converted to allow all Date types.*/ 
	public static final String ATTR_ACCESS_TIME = "accessTime";

	public static final String ATTR_ALLOW_3RD_PARTY = "allow3rdParty";

	public static final String ATTR_ATTEMPTS = "attempts";

	/** Comma seperated attribute names which the resource supports */ 
	public static final String ATTR_ATTRIBUTE_NAMES = "attributeNames";

	public static final String ATTR_BROKER_NAME = "brokerName";

	/** Character set used by the resource if the content contains text */ 
	public static final String ATTR_CHARSET="charSet";
    
	/** Return checksum value if supported */ 
    public static final String ATTR_CHECKSUM="checksum";

    /**
     * Default Checksum type used for the Checksum value. 
     * Is first checksum type as returned by getChecksumTypes  
     * if the resource implements the VChecksum interface. 
     * @see nl.uva.vlet.vrs.VChecksum
     */
    public static final String ATTR_CHECKSUM_TYPE = "checksumType";

	/**
     * Comma separated list of checksum types, if the resource implements the 
     * VChecksum interface.  
     */
    public static final String ATTR_CHECKSUM_TYPES = "checksumTypes";
    
    /** Resource's creation time
     * The type of returned attribute is compatible with the Date class. 
	 * milliseconds or "time since epoch" is converted to allow all Date types.*/ 
    public static final String ATTR_CREATION_TIME = "creationTime";

	public static final String ATTR_DATA_TYPE = "dataType";
	/** Destination directory used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_DEST_DIRNAME = "destDirname";

	/** Destination file name used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_DEST_FILENAME = "destFilename";

	/** Destination hostname name used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_DEST_HOSTNAME = "destHostname";

	/** Destination path used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_DEST_PATH = "destPath";

	public static final String ATTR_DEST_URL = "destinationUrl";

	/**
	 * Normally for files this attribute means the parent directory, but for 
	 * some implementation for directory resources the actual directory is returned
	 * and NOT the "parent" directory name. Use parentDirname to unambiguously get
	 * the real parent directory.
	 */ 
	public static final String ATTR_DIRNAME = "dirname";
	
	/** Extra error text if the resource has an error or an exceptio has been thrown. */ 
	public static final String ATTR_ERROR_TEXT = "errorText";

	/** Value of exists() method if implemented. */ 
    public static final String ATTR_EXISTS = "exists"; 
	
	public static final String ATTR_FAULT = "fault";
	    
	public static final String ATTR_GID = "groupID";

	/** Spell out as Grid UID instead of confusinf GUID */
	public static final String ATTR_GRIDUID = "gridUniqueID";

	public static final String ATTR_GROUP = "group";
	
	/** Hostname of location URI.*/ 
	public static final String ATTR_HOSTNAME = "hostname";

	/** Binary icons types currently not supported, only ICONURL.*/ 
	public static final String ATTR_ICON = "icon";

	/** Icon URL, can be relative or point to remote location on the web. */ 
	public static final String ATTR_ICONURL = "iconURL";

	public static final String ATTR_ID = "id";

	public static final String ATTR_INDEX = "index";

	public static final String ATTR_ISCOMPOSITE = "isComposite";

	/** Whether the resource is a "Directory" type. */  
    public static final String ATTR_ISDIR = "isDir";

	/** Whether the resource properties are 'editable'.*/  
	public static final String ATTR_ISEDITABLE = "isEditable";

	/** Whether the resource is a "File" type. */  
    public static final String ATTR_ISFILE = "isFile";

	/** 
	 * Whether the resource is considered "hidden" on the (remote) filesystem.  
	 */   
    public static final String ATTR_ISHIDDEN = "isHidden";

    /** 
	 * Whether the resource is readable using the current user's credentials 
	 * (or file permissions). 
	 */
	public static final String ATTR_ISREADABLE = "isReadable";

	/** 
	 * Whether resource is a symbolic link on for example a unix file system. 
	 * Symbolic links are file system depended and not all file systems might support 
	 * this. 
	 */
	public static final String ATTR_ISSYMBOLICLINK = "isSymbolicLink";

	/** Whether this node is a VLink */
	public static final String ATTR_ISVLINK = "isVLink";

	/** 
	 * Whether the resource is writable using the current user's credentials 
	 * (or file permissions). 
	 */
	public static final String ATTR_ISWRITABLE = "isWritable";

	/** Optional error text explaining the (error) status of a resource */ 
	public static final String ATTR_JOB_ERROR_TEXT = "errorText";

	/** Whether the job resource has encountered an error and has stopped executing */ 
	public static final String ATTR_JOB_HAS_ERROR = "jobHasError";

	/** 
	 * Whether Job has terminated or not. A failed or canceled job will be considered
	 * 'terminated' as well.
	 */  
	public static final String ATTR_JOB_HAS_TERMINATED = "jobHasTerminated";

	/** Whether Job is running or not. */ 
	public static final String ATTR_JOB_IS_RUNNING = "jobIsRunning";

	///** Time of last status update */ 
    //public static final String ATTR_JOB_ENTERED_TIME = "jobEnteredTime";

	/** Time of last status update */ 
    public static final String ATTR_JOB_STATUS_UPDATE_TIME = "jobStatusUpdateTime";

    public static final String ATTR_JOB_SUBMISSION_TIME = "jobSubmissionTime";
    
	//public static final String ATTR_WMS_STATUS_TEXT = "wmsStatusText";
	
	/** Job ID as returned by the Job Manager */ 
	public static final String ATTR_JOBID = "jobId";

	/** Job URI as returned by the Job Manager */ 
	public static final String ATTR_JOBURI = "jobUri";

	/** 
	 * Length or size of resource if applicable. 
	 * @see nl.uva.vlet.vfs.VFile#getLength() VFile.getLenght()
	 */
	public static final String ATTR_LENGTH = "length";
	
	/** 
	 * Virtual Resource Location URI or VRL. 
     * @see nl.uva.vlet.vrs.VNode#getVRL() VNode.getVRL() 
     */  
	public static final String ATTR_LOCATION = "location";

	public static final String ATTR_MAX_WALL_TIME = "maxWallTime";

	/** Content 'mime' type if it has one. */ 
	public static final String ATTR_MIMETYPE = "mimeType";

	public static final String ATTR_MODIFICATION_TIME = "modificationTime";

	/** 
	 * Logical Resource name. For files and directories this is the basename
	 * of the path.  
	 */ 
	public static final String ATTR_NAME = "name";

	public static final String ATTR_NODE_TEMP_DIR = "nodeTempDir";

	public static final String ATTR_NRACLENTRIES = "nrACLEntries";

	/** VAttribute name for the nr of childs nodes (Only for VComposite and subclasses) */
	public static final String ATTR_NRCHILDS = "nrChilds";

	public static final String ATTR_OWNER = "owner";

	/** 
	 * Unambiguous Parent directory attribute for both files and 
	 * directories. 
	 * @see #ATTR_DIRNAME 
	 */
	public static final String ATTR_PARENT_DIRNAME = "parentDirname";

	/** Make sure Global.PASSIVE_MODE and ATTR_PASSIVE_MODE are the same !*/
	public static final String ATTR_PASSIVE_MODE = GlobalConfig.PROP_PASSIVE_MODE;
	
	public static final String ATTR_PASSPHRASE = "passphrase";

	/** Password part in location URI */ 
	public static final String ATTR_PASSWORD = "password";

	/** Path part of location URI.*/ 
    public static final String ATTR_PATH = "path";

	/** Unix style permissions string: drwxr-xr-x+ etc, enhanced with '[]' attributes*/
	public static final String ATTR_PERMISSIONS_STRING = "permissionsString";

	public static final String ATTR_PERSISTANT = "persistant";

	/** Port of location URI.*/ 
	public static final String ATTR_PORT = "port"; 

	public static final String ATTR_QUEUE_NAME = "queueName";

	public static final String ATTR_RECURSIVE = "recursive";

	public static final String ATTR_REQUEST_STATUS= "requestStatus";

	public static final String ATTR_RESOURCE_CLASS="resourceClass";

	public static final String ATTR_RESOURCE_TYPES="resourceTypes";

	/** Scheme part of protocol part of location URI. */
	public static final String ATTR_SCHEME = "scheme";

	public static final String ATTR_SHELL_PATH = "shellPath";

	public static final String ATTR_SHOW_SHORTCUT_ICON = "showShortCutIcon";

	/** Source directory attribute used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_SOURCE_DIRNAME = "sourceDirname";

	/** Source filename attribute used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_SOURCE_FILENAME = "sourceFilename";
	
	/** Source hostname attribute used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_SOURCE_HOSTNAME = "sourceHostname";

	/** Source path attribute used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_SOURCE_PATH = "sourcePath";
	
	/** Complete source URL attribute used by Reliable File Transfers (RFT) */ 
	public static final String ATTR_SOURCE_URL = "sourceUrl";

	/** Status attribute, if implemented by resource */  
    public static final String ATTR_STATUS = "status";
    
    /**
     * Extra status information. Human readable text/explanation about current job status. 
     * For example WMS "Reason". 
     */   
    public static final String ATTR_JOB_STATUS_INFORMATION = "jobStatusInformation";
    
    /** Storage element used by LFC and SRM resources */ 
	public static final String ATTR_STORAGE_ELEMENT = "storageElement";

	/**
	 * If the resource is a symbolic link, this attribute return the target path 
	 * on the filesystem. 
	 * @see #ATTR_ISSYMBOLICLINK 
	 */
	public static final String ATTR_SYMBOLICLINKTARGET = "symbolicLinkTarget" ;
	
	/** Hidden attribute to quickly know whether target is composite. */ 
    public static final String ATTR_TARGET_IS_COMPOSITE = "targetIsComposite";

	/** Hidden attribute: mimetype of linkTarget. */ 
    public static final String ATTR_TARGET_MIMETYPE = "targetMimetype";
	
	public static final String ATTR_TRANSFERS_ACTIVE = "transfersActive";
	  
    public static final String ATTR_TRANSFERS_CANCELLED = "transfersCancelled";
    
	public static final String ATTR_TRANSFERS_FAILED = "transfersFailed";

	public static final String ATTR_TRANSFERS_FINISHED = "transfersFinished";

    public static final String ATTR_TRANSFERS_PENDING = "transfersPending";

    public static final String ATTR_TRANSFERS_RESTARTED = "transfersRestarted"; 
    
    public static final String ATTR_TRANSPORT_URI = "transportUri" ;
    
    /** 
     * Resource Type. For example "File" or "Directory". No abstract type 
     * are allow here. 
     * @see nl.uva.vlet.vrs.VNode#getType() VNode.getType() 
     */ 
	public static final String ATTR_TYPE = "type";

    public static final String ATTR_UID = "userID";
    
    public static final String ATTR_UNIQUE = "unique";

    /** Unix style octal file mode value. For example "0755" */ 
    public static final String ATTR_UNIX_FILE_MODE="unixFileMode";
    
    /** Fragment part, part after a hash '#', in the location URI. */
	public static final String ATTR_URI_FRAGMENT = "fragment";
    
    // public static final String ATTR_HOME_PATH = "homePath";
    /** Query part, part after a question mark '?', in the location URI. */
	public static final String ATTR_URI_QUERY = "query"; 
    
    public static final String ATTR_USERNAME = "username";

    public static final String ATTR_VO_NAME = "voName"; 

    /**
     * Used Authentication Scheme. 
     * @see nl.uva.vlet.vrs.ServerInfo ServerInfo
     * @see nl.uva.vlet.vrs.ServerInfo#GSI_AUTH GSI_AUTH
     * @see nl.uva.vlet.vrs.ServerInfo#NO_AUTH NO_AUTH
     */
    public static final String AUTH_SCHEME = "AUTH_SCHEME";

    
}
