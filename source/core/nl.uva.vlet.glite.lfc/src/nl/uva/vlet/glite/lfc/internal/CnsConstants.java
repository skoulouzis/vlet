/*
 * Initial development of the original code was made for the
 * g-Eclipse project founded by European Union
 * project number: FP6-IST-034327  http://www.geclipse.eu/
 *
 * Contributors:
 *    Mateusz Pabis (PSNC) - initial API and implementation
 *    Piter T. de boer - Refactoring to standalone API and bugfixing.  
 *    Spiros Koulouzis - Refactoring to standalone API and bugfixing.  
 */ 
package nl.uva.vlet.glite.lfc.internal;



/**
 *  Binary codes for LFC communication
 */
public final class CnsConstants {

  /**
   * To avoid creating garbage strings, and to enhance code readability 
   */
  public static final String EMPTY_STRING = ""; //$NON-NLS-1$

  /**
   * Magic Number for Castor Security Service
   */
  public static final int CSEC_TOKEN_MAGIC_1  = 0xCA03;
  
  /**
   * Castor Name Service - Magic #01<br>
   * defines name service request
   */
  public static final int CNS_MAGIC       = 0x030e1301;
  /**
   * Castor Name Service - Magic #02<br>
   * defines name service extended request
   */
  public static final int CNS_MAGIC2      = 0x030e1302;
  /**
   * Castor Name Service - Magic #03<br>
   * defines name service more extended request
   */
  public static final int CNS_MAGIC3      = 0x030e1303;
  
  /**
   * Castor Name Service - Magic #04<br>
   * defines name service request for replicating files
   */
  public static final int CNS_MAGIC4      = 0x030E1304;
  
  public static final int CNS_ACCESS      = 0;
  public static final int CNS_CHDIR       = 1;
  public static final int CNS_CHMOD       = 2;
  public static final int CNS_CHOWN       = 3;
  public static final int CNS_CREAT       = 4;
  public static final int CNS_MKDIR       = 5;
  public static final int CNS_RENAME      = 6;
  public static final int CNS_RMDIR       = 7;
  public static final int CNS_STAT        = 8;
  public static final int CNS_UNLINK      = 9;  
  /**
   * Open directory command id
   */
  public static final int CNS_OPENDIR     = 10;
  /**
   * Read directory command id
   */
  public static final int CNS_READDIR     = 11;
  /**
   * Close directory command id
   */
  public static final int CNS_CLOSEDIR    = 12;
  public static final int CNS_OPEN        = 13;
  public static final int CNS_CLOSE       = 14;
  public static final int CNS_SETATIME    = 15;
  public static final int CNS_SETFSIZE    = 16;
  public static final int CNS_SHUTDOWN    = 17;
  public static final int CNS_GETSEGAT    = 18;
  public static final int CNS_SETSEGAT    = 19;
  public static final int CNS_LISTTAPE    = 20;
  public static final int CNS_ENDLIST     = 21;
  public static final int CNS_GETPATH     = 22;
  public static final int CNS_DELETE      = 23;
  public static final int CNS_UNDELETE    = 24;
  public static final int CNS_CHCLASS     = 25;
  public static final int CNS_DELCLASS    = 26;
  public static final int CNS_ENTCLASS    = 27;
  public static final int CNS_MODCLASS    = 28;
  public static final int CNS_QRYCLASS    = 29;
  public static final int CNS_LISTCLASS   = 30;
  public static final int CNS_DELCOMMENT  = 31;
  public static final int CNS_GETCOMMENT  = 32;
  
  /**
   * Set new comment to the file/directory command id
   */
  public static final int CNS_SETCOMMENT  = 33;
  public static final int CNS_UTIME       = 34;
  public static final int CNS_REPLACESEG  = 35;
  public static final int CNS_GETACL      = 37;
  public static final int CNS_SETACL      = 38;
  public static final int CNS_LCHOWN      = 39;
  /**
   * Fetch Link Statistics command id 
   */
  public static final int CNS_LSTAT       = 40; // 0x28
  public static final int CNS_READLINK    = 41;
  public static final int CNS_SYMLINK     = 42;
  /**
   * Add replica to another Storage Element command id 
   */
  public static final int CNS_ADDREPLICA  = 43;  // 0x2B
  
  public static final int CNS_DELREPLICA  = 44; // 0x2C
  /**
   * List replicas information command id
   */
  public static final int CNS_LISTREPLICA = 45;
  public static final int CNS_STARTTRANS  = 46; // 0x2E
  public static final int CNS_ENDTRANS    = 47;
  public static final int CNS_ABORTTRANS  = 48;
  public static final int CNS_LISTLINKS   = 49; // 0x31
  /**
   * Change file size and file checksum command id
   */
  public static final int CNS_SETFSIZEG   = 50;
  /**
   * Fetch Link Statistics with Global Unique ID command id
   */
  public static final int CNS_STATG       = 51; // 0x33
  public static final int CNS_STATR       = 52;
  public static final int CNS_SETPTIME    = 53;
  public static final int CNS_SETRATIME   = 54;
  public static final int CNS_SETRSTATUS  = 55;
  public static final int CNS_ACCESSR     = 56;
  public static final int CNS_LISTREP4GC  = 57;
  public static final int CNS_LISTREPLICAX = 58;
  public static final int CNS_STARTSESS   = 59;
  public static final int CNS_ENDSESS     = 60;
  public static final int CNS_DU          = 61;
  public static final int CNS_GETGRPID    = 62;
  public static final int CNS_GETGRPNAM   = 63;
  public static final int CNS_GETIDMAP    = 64;
  public static final int CNS_GETUSRID    = 65;
  public static final int CNS_GETUSRNAM   = 66;
  public static final int CNS_MODGRPMAP   = 67;
  public static final int CNS_MODUSRMAP   = 68;
  public static final int CNS_RMGRPMAP    = 69;
  public static final int CNS_RMUSRMAP    = 70;
  public static final int CNS_GETLINKS    = 71;
  public static final int CNS_GETREPLICA  = 72;
  public static final int CNS_ENTGRPMAP   = 73;
  public static final int CNS_ENTUSRMAP   = 74;
  public static final int CNS_SETRTYPE    = 75;
  public static final int CNS_MODREPLICA  = 76;
  public static final int CNS_GETREPLICAX = 77;
  public static final int CNS_LISTREPSET  = 78;
  public static final int CNS_SETRLTIME   = 79;
  public static final int CNS_GETREPLICAS = 80;
  public static final int CNS_GETGRPNAMES = 81;
  public static final int CNS_PING        = 82;

              /* name server reply types */

  public static final int MSG_ERR     = 1;
  /**
   * Message contains response data to the request
   */
  public static final int MSG_DATA    = 2;
  /**
   * Reset Security Context command from server
   */
  public static final int CNS_RC      = 3;
  /**
   * Keep Security Context command from server
   */
  public static final int CNS_IRC     = 4;
  public static final int MSG_LINKS   = 5;
  public static final int MSG_REPLIC  = 6;
  public static final int MSG_REPLICP = 7;
  public static final int MSG_REPLICX = 8;
  public static final int MSG_REPLICS = 9;
  public static final int MSG_GROUPS  = 10;
  
  public static final String getResponseType( final int response ) {
    String result;
    switch ( response ) {
      case MSG_ERR:     result = "Response Error!"; break;
      case MSG_DATA:    result = "DATA"; break;
      case CNS_RC:      result = "Reset Security Context"; break;
      case CNS_IRC:     result = "Keep Security Context"; break;
      case MSG_LINKS:   result = "LINKS"; break;
      case MSG_REPLIC:  result = "REPLIC"; break;
      case MSG_REPLICP: result = "REPLIC-P"; break;
      case MSG_REPLICX: result = "REPLIC-X"; break;
      case MSG_REPLICS: result = "REPLIC-S"; break;
      case MSG_GROUPS:  result = "Group"; break;
      default: result = "ERROR: No such type!!!";
    }
    return result;
  }
  
}
