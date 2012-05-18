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
 * $Id: Messages.java,v 1.2 2011-04-18 12:30:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:40 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc;

/**
 * Returns the localized messages for this package.
 */
public class Messages 
{
  public static String lfc_log_send_header="SEND HEADER: 0x%1$S type: 0x%2$S size: %3$d / 0x%3$X";
  public static String lfc_log_send_body="SEND BODY: size: %1$d / 0x%1$X";

  public static String lfc_log_send_closedir="sending CLOSEDIR request...";
  public static String lfc_log_send_opendir="sending OPENDIR request: %1$s";
  public static String lfc_log_send_readdir="sending READDIR request: 0x%1$X";
  public static String lfc_log_send_lstat="sending LSTAT: %1$s";
  public static String lfc_log_send_statg="sending STATG: %1$s";
  public static String lfc_log_send_token="sending GSS Token, size: %1$d";
  public static String lfc_log_recv_header="RECV HEADER: 0x%1$S type: 0x%2$S size: %3$d / 0x%3$X [%4$s]";
  public static String lfc_log_recv_closedir="receiving CLOSEDIR response...";
  public static String lfc_log_reset_context="RESET CONTEXT: 0x%1$X";
  public static String lfc_log_recv_opendir="receiving OPENDIR response...";
  public static String lfc_log_recv_longvalue="receiving Long Value response...";
		 
  public static String lfc_log_recv_lstat="receiving LSTAT response...";
  public static String lfc_log_recv_statg="receiving STATG response...";
  public static String lfc_log_recv_readdir="receiving READDIR response...";
  public static String lfc_log_recv_readdir_items="\tReceived %1$d item(s)"; 
  public static String lfc_log_recv_readdir_filename="\t  filename = %1$s"; 
  public static String lfc_log_recv_token="received GSS Token, size: %1$d";
  public static String lfc_log_keep_context="KEEP CONTEXT: 0x%1$X";
  public static String lfc_log_connect="Connecting to %1$s:%2$d";
  public static String lfc_log_disconnect="Disconnecting from %1$s:%2$d";
  public static String lfc_log_authenticate="---------------------------------------------\nAuthenticating client";
  public static String lfc_log_authenticated="Client authenticated.\n---------------------------------------------";

  public static String	LFCStore_replica="Replica #";
  public static String	LFCServer_1=" ERROR: RC received during auth.";
  public static String	LFCStore_null_parent="Parent store must not be null";
  public static String	LFCStore_null_fileinfo="FileInfo must not be null";
  public static String	LFCStore_null_uri="URI must not be null";
  public static String	LFCServer_null_URI="You have to specify server URI";
  public static String	LFCServer_null_path="Path must not be null";
  public static String	LFCServer_null_guid="Guid must not be null";
  public static String	LFCServer_empty_path="Path must not be empty";
  public static String	LFCServer_empty_guid="Guid must not be empty";

  public static String LFCStore_wrong_listing="It is possible to list children for directories only!";
  public static String LFCStore_convert_failed="Convert failed.";
  public static String LFCServer_token_canceled="User canceled token creation.";
  public static String LFCServer_token_needed="A VOMS proxy is needed in order to access LFC connections but none could be found. Should a new one be created?";
  public static String	LFCServer_connection="LFC connection";
  public static String Properties_atime="Last access time";
  public static String Properties_ctime="Creation time";
  public static String Properties_mtime="Last modification time";
  public static String Properties_size="Size";
  public static String	Properties_csumType="Checksum type";
  public static String	Properties_csumValue="Checksum value";
  public static String	Properties_comment="Comment";
  public static String Properties_guid="Global Unique ID (guid)";
  public static String	Properties_gid="ID (group)";
  public static String	Properties_uid="ID (user)";
  public static String	Properties_class="File class";
  public static String	Properties_fileid="File local ID";
  public static String	Properties_permissions="File permissions";
  public static String	Properties_status="File status";
  public static String	Properties_permissions_ex="File permissions (linux)";
  public static String Properties_not_available="not available";
  public static String	Properties_experiment="Experiment";
  public static String	Properties_user="User";
  public static String	Properties_online="Online";
  public static String Properties_migrated="Migrated";

//	
//  // LOG MESSAGES
//  public static String lfc_log_send_header;
//  public static String lfc_log_send_closedir;
//  public static String lfc_log_send_opendir;
//  public static String lfc_log_send_readdir;
//  public static String lfc_log_send_lstat;
//  public static String lfc_log_send_statg;
//  public static String lfc_log_send_token;
//  public static String lfc_log_recv_header;
//  public static String lfc_log_recv_closedir;
//  public static String lfc_log_reset_context;
//  public static String lfc_log_recv_opendir;
//  public static String lfc_log_recv_lstat;
//  public static String lfc_log_recv_statg;
//  public static String lfc_log_recv_readdir;
//  public static String lfc_log_recv_readdir_items;
//  public static String lfc_log_recv_readdir_filename;
//  public static String lfc_log_recv_token;
//  public static String lfc_log_keep_context;
//  public static String lfc_log_connect;
//  public static String lfc_log_disconnect;
//  public static String lfc_log_authenticate;
//  public static String lfc_log_authenticated;
//
//
//  public static String LFCStore_replica;
//  public static String LFCServer_1;
//  public static String LFCStore_null_parent;
//  public static String LFCStore_null_fileinfo;
//  public static String LFCStore_null_uri;
//  public static String LFCServer_null_URI;
//  public static String LFCServer_null_path;
//  public static String LFCServer_null_guid;
//  public static String LFCServer_empty_path;
//  public static String LFCServer_empty_guid;
//
//  public static String LFCStore_wrong_listing;
//  public static String LFCStore_convert_failed;
//  public static String LFCServer_token_canceled;
//  public static String LFCServer_token_needed;
//  public static String LFCServer_connection;
//  public static String Properties_atime;
//  public static String Properties_ctime;
//  public static String Properties_mtime;
//  public static String Properties_size;
//  public static String Properties_csumType;
//  public static String Properties_csumValue;
//  public static String Properties_comment;
//  public static String Properties_guid;
//  public static String Properties_gid;
//  public static String Properties_uid;
//  public static String Properties_class;
//  public static String Properties_fileid;
//  public static String Properties_permissions;
//  public static String Properties_status;
//  public static String Properties_permissions_ex;
//  public static String Properties_not_available;
//  public static String Properties_experiment;
//  public static String Properties_user;
//  public static String Properties_online;
//  public static String Properties_migrated;

  private static final String BUNDLE_NAME = "eu.geclipse.lfc.messages"; 
  
  private Messages() 
  {
  
  }
}
