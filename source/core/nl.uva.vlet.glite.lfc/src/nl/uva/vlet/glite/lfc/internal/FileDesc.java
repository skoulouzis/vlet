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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCServer;




/**
 *  Describes files and directories entries.
 *  
 */
public class FileDesc
{
  /**
   * Bitmask for <b>symbolic link</b>
   * PTdB: Beware that the logical operation (0xA000 & 0x8000) == 0x8000  !<br>
   * Use ( val & 0xA000 ) == 0xA000 for link checking ! <br>   
   */
  static final int S_IFLNK = 0xA000;
  /**
   * Bitmask for <b>regular file</b>
   */
  static final int S_IFREG = 0x8000;
  /**
   * Bitmask for <b>directory</b>
   */
  static final int S_IFDIR = 0x4000;
  /**
   * Bitmask for <b>set user ID on execution</b>
   */
  static final int S_ISUID = 0004000;
  /**
   * Bitmask for <b>set group ID on execution</b>
   */
  static final int S_ISGID = 0002000;
  /**
   * Bitmask for <b>sticky bit</b>
   */
   static final int S_ISVTX = 0001000;
  /**
   * Bitmask for <b>read by owner</b>
   */
  static final int S_IRUSR = 0000400;
  /**
   * Bitmask for <b>write by owner</b>
   */
  static final int S_IWUSR = 0000200;
  /**
   * Bitmask for <b>execute/search by owner</b>
   */
  static final int S_IXUSR = 0000100;
  /**
   * Bitmask for <b>read by group</b>
   */
  static final int S_IRGRP = 0000040;
  /**
   * Bitmask for <b>write by group</b>
   */
  static final int S_IWGRP = 0000020;
  /**
   * Bitmask for <b>execute/search by group</b>
   */
  static final int S_IXGRP = 0000010;
  /**
   * Bitmask for <b>read by others</b>
   */
  static final int S_IROTH = 0000004;
  /**
   * Bitmask for <b>write by others</b>
   */
  static final int S_IWOTH = 0000002;
  /**
   * Bitmask for <b>execute/search by others</b>
   */
  static final int S_IXOTH = 0000001;
  
  private String fileName=null;  // name of the file/dir
  private String guid=null;      // global unique id
  private String comment=null;   // user comment of the file
  private String chksumType=null; // checksum type
  private String chksumValue=null; // checksum value
  
  private Date aDate;       
  private Date mDate;
  private Date cDate;
  
  private long fileId;      // unique id
  private long fileSize;    // size of the file (dirs are sized 0)
  private long aTime;       // last access time (in C 64 bit timestamp)
  private long mTime;       // last modification (in C 64 bit timestamp)
  private long cTime;       // last meta-data modification (in C 64 bit timestamp)
  
  private int uLink;        // number of children
  private int uid;          // user id
  private int gid;          // group id

  private int fileMode;   // see description on the end of the file
  private short fileClass;  // 1 = experiment, 2 = user (don't know what is this for)
  private byte status;      // '-' = online, 'm' = migrated (don't know what is this for)
  

  /**
   * @return comment of the file
   */
  public String getComment() {
    return this.comment;
  }

  /**
   * Sets new comment to the file description
   * @param comment new comment
   */
  public void setComment( final String comment ) {
    this.comment = comment;
  }

  /**
   * Sets new checksum type of the file 
   * @param type checksum type
   */
  public void setChkSumType( final String type ) {
    this.chksumType = type;
  }  
  
  /**
   * @return Checksum type of the file
   */
  public String getChkSumType() {
    return this.chksumType;
  }

  /**
   * Sets new checksum value of the file 
   * @param checksum checksum value
   */
  public void setChkSumValue( final String checksum ) {
    this.chksumValue = checksum;
  }

  /**
   * @return file checksum value
   */
  public String getChkSumValue() {
    return this.chksumValue;
  }
  /**
   * @return global unique ID of the file/directory
   */
  public String getGuid() {
    return this.guid;
  }
  
  /**
   * @return fileId of the file/directory
   */
  public long getFileId() {
    return this.fileId;
  }
  
  /**
   * see description at the end of the file
   * @return 16 bit value (as integer!) with permissions and types 
   */
  public int getFileMode() 
  {
    return this.fileMode;
  }

  /**
   * @return user id (this does not have to be remote user id)
   */
  public int getUid() {
    return this.uid;
  }
   
  /**
   * @return group id (this does not have to be remote group id)
   */
  public int getGid() {
    return this.gid;
  }

  /**
   * @return size of the file or 0 if it's a directory
   */
  public long getFileSize() {
    return this.fileSize;
  }

  /**
   * This is not a Java time!
   * To get Java value use {@link #getADate()}
   * @return last access time, C 64 bit timestamp 
   */
  public long getATime() {
    return this.aTime;
  }

  /**
   * This is not a Java time!
   * To get Java value use {@link #getMDate()}
   * @return last modification time, C 64 bit timestamp 
   */
  public long getMTime() {
    return this.mTime;
  }
  
  /**
   * This is not a Java time!
   * To get Java value use {@link #getCDate()}
   * @return last meta-data modification time, C 64 bit timestamp 
   */
  public long getCTime() {
    return this.cTime;
  }
  
  /**
   * @return last access time
   */
  public Date getADate() {
    return this.aDate;
  }
  
  /**
   * @return last modification time
   */
  public Date getMDate() {
    return this.mDate;
  }
  
  /**
   * @return last meta-data modification time
   */
  public Date getCDate() {
    return this.cDate;
  }
  
  /**
   * @return number of children or 0 if it's a file
   */
  public int getULink() {
    return this.uLink;
  }

  /**
   * I have no idea what is this
   * @return 1 = experiment, 2 = user
   */
  public short getFileClass() {
    return this.fileClass;
  }
  
  /**
   * @return '-' = online, 'm' = migrated (don't know what is this for)
   */
  public byte getStatus() {
    return this.status;
  }
  
  /**
   * @return name of the file/directory
   */
  public String getFileName() {
    return this.fileName;
  }
  
  /**
   * @return true if this is a directory, false in other cases
   */
  public boolean isDirectory() {
    return ( ( this.fileMode & 0x4000 ) != 0 );
  }
  /**
   * PTdB: Beware that the logical operation (0xA000 & 0x8000) == 0x8000 0 !<br>
   * Use ( val & 0xA000 ) == 0xA000 for link checking ! <br>   
   * @return true if this is a symbolic link, false in other cases
   */
  public boolean isSymbolicLink() {
    return ( ( this.fileMode & 0xA000 ) == 0xA000);
  }
  /**
   * PTdB: Beware: Since 0xA000 & 0x8000 == 0x8000, this method 
   * will return 'true' for a symbolic link as well. 
   * This is kept so that a link is by default treated as a file. 
   * @return true if this is a file, false in other cases
   */
  public boolean isFile() 
  {
    return ( ( this.fileMode & 0x8000 ) != 0);
  }

  /**
   * @param input data stream where the data is stored
   * @param readName determines if name should be read from stream
   * @param readGuid determines if global unique ID should be read
   * @param readCheckSum determines if checksum details should be read
   * @param readComment determines if file comment should be read
   * @return new FileDesc class
   * @throws IOException
   */
  public static FileDesc getFromStream( final DataInputStream input, 
                                        final boolean readName,
                                        final boolean readGuid,
                                        final boolean readCheckSum, 
                                        final boolean readComment )
    throws IOException {
    
    FileDesc result = new FileDesc();
    int i=5;
    int wait=100; 
    // Receive data from data stream
    try 
    {
      while (i > 0 && input.available() < 50) 
      { 
        LFCServer.staticLogIOMessage( "Waiting for data... " ); //$NON-NLS-1$
        Thread.sleep( wait );
        wait=wait*2; 
        i--;
      }
    }
    catch( InterruptedException exc ) 
    {
      LFCServer.staticLogIOException( exc );
    }
    result.fileId = input.readLong();
    
    if ( readGuid ) 
    {
      result.guid = IOUtil.readString( input );
    }
    
    // PdB changed into readUnsignedShort(): 
    result.fileMode = input.readUnsignedShort();
    
    result.uLink = input.readInt();
    result.uid = input.readInt();
    result.gid = input.readInt();
    result.fileSize = input.readLong();
    result.aTime = input.readLong();
    result.mTime = input.readLong();
    result.cTime = input.readLong();
    
    // convert C/UNIX 64 bit timestamps to Java Date
    result.aDate = new Date( result.getATime()*1000 );
    result.mDate = new Date( result.getMTime()*1000 );
    result.cDate = new Date( result.getCTime()*1000 );
    
    result.fileClass = ( input.readShort() );
    result.status = ( input.readByte() );
    if ( readCheckSum ) {
      result.chksumType = IOUtil.readString( input );
      result.chksumValue = IOUtil.readString( input );
    }
    if( readName ) {
      result.fileName = IOUtil.readString( input );
    }
    if( readComment ) {
      result.comment = IOUtil.readString( input );
    }
    return result;
  }
  
  /**
   * Creates <code>String</code> with linux-like permissions of this file.
   * @return linux-like permission description
   */
  public String getPermissions() {
    StringBuilder result = new StringBuilder( 128 );
    int mode = this.fileMode;
   
    // Added by P.T. de Boer: If mode has 0x0A000 it is a link! 
    if ((mode & S_IFLNK)==S_IFLNK) // use EQUAL for 0x0A000==0x0A000
    {
      	result.append( 'l');
    }
    // orignal code: 
    else if ( ( mode & S_IFDIR ) != 0 ) { result.append( 'd' ); }
    else { result.append( '-' ); }
    
    if ( ( mode & S_IRUSR ) != 0 ) { result.append( 'r' ); }
    else { result.append( '-' ); }
    if ( ( mode & S_IWUSR ) != 0 ) { result.append( 'w' ); }
    else { result.append( '-' ); }
    if ( ( mode & S_IXUSR ) != 0 ) { result.append( 'x' ); }
    else { result.append( '-' ); }

    if ( ( mode & S_IRGRP ) != 0 ) { result.append( 'r' ); }
    else { result.append( '-' ); }
    if ( ( mode & S_IWGRP ) != 0 ) { result.append( 'w' ); }
    else { result.append( '-' ); }
    if ( ( mode & S_IXGRP ) != 0 ) { result.append( 'x' ); }
    else { result.append( '-' ); }
    
    if ( ( mode & S_IROTH ) != 0 ) { result.append( 'r' ); }
    else { result.append( '-' ); }
    if ( ( mode & S_IWOTH ) != 0 ) { result.append( 'w' ); }
    else { result.append( '-' ); }
    if ( ( mode & S_IXOTH ) != 0 ) { result.append( 'x' ); }
    else { result.append( '-' ); }    
    
    return result.toString();
  }
  
  
// /////////////////////////////////////////////////////////////////////////////
//      Copied from Cns_stat manual page (from LFC sources)
//      
//      filemode  is  constructed  by  OR'ing  the bits defined in <sys/stat.h>
//      under Unix or  "statbits.h" under Windows/NT:
//      
//           S_IFLNK   0xA000          symbolic link
//           S_IFREG   0x8000          regular file
//           S_IFDIR   0x4000          directory
//           S_ISUID   0004000         set user ID on execution
//           S_ISGID   0002000         set group ID on execution
//           S_ISVTX   0001000         sticky bit
//           S_IRUSR   0000400         read by owner
//           S_IWUSR   0000200         write by owner
//           S_IXUSR   0000100         execute/search by owner
//           S_IRGRP   0000040         read by group
//           S_IWGRP   0000020         write by group
//           S_IXGRP   0000010         execute/search by group
//           S_IROTH   0000004         read by others
//           S_IWOTH   0000002         write by others
//           S_IXOTH   0000001         execute/search by others
}
