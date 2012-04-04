// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GSIHTTPSender.java

package org.globus.gt4_1.axis.transport;

import java.io.IOException;
import org.apache.axis.MessageContext;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.SocketHolder;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.auth.*;
import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.net.GssSocketFactory;
import org.globus.gt4_1.axis.util.Util;
import org.gridforum.jgss.ExtendedGSSContext;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.*;

public class GSIHTTPSender extends HTTPSender
{
	private static final long serialVersionUID = -5500752736815332654L;

	public GSIHTTPSender()
    {
    }

    protected void getSocket(SocketHolder sockHolder, MessageContext msgContext, String protocol, String host, int port, int timeout, StringBuffer otherHeaders, 
            BooleanHolder useFullURL)
        throws Exception
    {
        if(!protocol.equalsIgnoreCase("httpg"))
            throw new IOException("Invalid protocol");
        GSSCredential cred = null;
        Authorization auth = null;
        String mode = null;
        auth = (Authorization)Util.getProperty(msgContext, "org.globus.gsi.authorization");
        mode = (String)Util.getProperty(msgContext, "org.globus.gsi.mode");
        if(auth == null)
            auth = HostAuthorization.getInstance();
        if(mode == null)
            mode = "gsi";
        GSSManager manager = ExtendedGSSManager.getInstance();
        ExtendedGSSContext context = null;
        Boolean anonymous = (Boolean)Util.getProperty(msgContext, "org.globus.gsi.anonymous");
        if(anonymous != null && anonymous.equals(Boolean.TRUE))
        {
            org.ietf.jgss.GSSName name = manager.createName((String)null, (Oid)null);
            cred = manager.createCredential(name, 0, (Oid)null, 1);
        } else
        {
            cred = (GSSCredential)Util.getProperty(msgContext, "org.globus.gsi.credentials");
        }
        org.ietf.jgss.GSSName expectedName = null;
        if(auth instanceof GSSAuthorization)
        {
            GSSAuthorization gssAuth = (GSSAuthorization)auth;
            expectedName = gssAuth.getExpectedName(cred, host);
        }
        context = (ExtendedGSSContext)manager.createContext(expectedName, GSSConstants.MECH_OID, cred, 0);
        if(mode.equalsIgnoreCase("gsilimited"))
        {
            context.requestCredDeleg(true);
            context.setOption(GSSConstants.DELEGATION_TYPE, GSIConstants.DELEGATION_TYPE_LIMITED);
        } else
        if(mode.equalsIgnoreCase("gsifull"))
        {
            context.requestCredDeleg(true);
            context.setOption(GSSConstants.DELEGATION_TYPE, GSIConstants.DELEGATION_TYPE_FULL);
        } else
        if(mode.equalsIgnoreCase("gsi"))
            context.requestCredDeleg(false);
        else
        if(mode.equalsIgnoreCase("ssl"))
            context.setOption(GSSConstants.GSS_MODE, GSIConstants.MODE_SSL);
        else
            throw new Exception("Invalid GSI MODE: " + mode);
        TrustedCertificates trustedCerts = (TrustedCertificates)Util.getProperty(msgContext, "org.globus.security.trustedCertifictes");
        if(trustedCerts != null)
            context.setOption(GSSConstants.TRUSTED_CERTIFICATES, trustedCerts);
        GssSocketFactory factory = GssSocketFactory.getDefault();
        int lport = port != -1 ? port : 8443;
        super.getSocket(sockHolder, msgContext, "http", host, lport, timeout, otherHeaders, useFullURL);
        GssSocket gsiSocket = (GssSocket)factory.createSocket(sockHolder.getSocket(), host, lport, context);
        gsiSocket.setAuthorization(auth);
        sockHolder.setSocket(gsiSocket);
    }
}
