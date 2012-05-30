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
import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCServer;


/**
 * chmod Request.
 * 
 * @author S. Koulouzis
 */

public class CnsChmodRequest
{
    private int uid = 0;
    private int gid = 0;
    private long cwd = 0;
    private String path = null;
    private int mode;

    /**
     * Creates request for chmod.
     */
    public CnsChmodRequest(final String path, int mode)
    {
        this.path = path;
        this.uid = 0;
        this.gid = 0;
        this.cwd = 0;
        this.mode = mode;
    }

    public CnsChmodResponse sendTo(final DataOutputStream output,
            final DataInputStream input) throws IOException
    {
        LFCServer
                .staticLogIOMessage("Sending chmod request for: "
                        + this.path);
        
        // Build request header
        CnsMessage msg = CnsMessage.createSendMessage(CnsConstants.CNS_MAGIC,
                CnsConstants.CNS_CHMOD);

        DataOutputStream dataOut = msg.createBodyDataOutput(4096);
        mode &= 07777;

        //Build request body
        dataOut.writeInt(this.uid); // +4
        dataOut.writeInt(this.gid); // +4
        dataOut.writeLong(this.cwd); // +8
        IOUtil.writeString(dataOut, path); // +1+length()
        dataOut.writeInt(this.mode);
        // no need to flush databuffer not close it.
        
        // finalize and send !
        int numSend = msg.sendTo(output);
        output.flush(); // sync
        msg.dispose();

        CnsChmodResponse result = new CnsChmodResponse();
        
        result.readFrom( input );
        return result;

    }

}
