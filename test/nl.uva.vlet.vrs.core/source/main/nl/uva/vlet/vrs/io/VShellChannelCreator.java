package nl.uva.vlet.vrs.io;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

public interface VShellChannelCreator
{
    /** Create Shell Channel */ 
    public VShellChannel createShellChannel(VRL optionalLocation) throws VlException;
    
}
