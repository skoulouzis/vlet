// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SSLContextHelper.java

package org.globus.gt4_1.axis.transport;

import java.net.Socket;
import org.apache.axis.MessageContext;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.HostAuthorization;
import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.net.GssSocketFactory;
import org.globus.gt4_1.axis.util.Util;
import org.gridforum.jgss.ExtendedGSSContext;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.*;

public class SSLContextHelper
{

    public SSLContextHelper(MessageContext msgContext, String host, int port)
        throws GSSException
    {
        Authorization auth = (Authorization)Util.getProperty(msgContext, "org.globus.gsi.authorization");
        Boolean anonymous = (Boolean)Util.getProperty(msgContext, "org.globus.gsi.anonymous");
        GSSCredential cred = (GSSCredential)Util.getProperty(msgContext, "org.globus.gsi.credentials");
        Integer protection = (Integer)Util.getProperty(msgContext, "org.globus.security.transport.type");
        TrustedCertificates trustedCerts = (TrustedCertificates)Util.getProperty(msgContext, "org.globus.security.trustedCertifictes");
        init(host, port, auth, anonymous, cred, protection, trustedCerts);
    }

    public SSLContextHelper(String host, int port, Authorization auth, Boolean anonymous, GSSCredential cred, Integer protection, TrustedCertificates trustedCerts)
        throws GSSException
    {
        init(host, port, auth, anonymous, cred, protection, trustedCerts);
    }

    protected void init(String host, int port, Authorization auth, Boolean anonymous, GSSCredential cred, Integer protection, TrustedCertificates trustedCerts)
        throws GSSException
    {
        this.host = host;
        this.port = port;
        if(auth == null)
            auth = HostAuthorization.getInstance();
        GSSManager manager = ExtendedGSSManager.getInstance();
        boolean anon = false;
        if(anonymous != null && anonymous.equals(Boolean.TRUE))
            anon = true;
        if(anon)
        {
            org.ietf.jgss.GSSName name = manager.createName((String)null, (Oid)null);
            cred = manager.createCredential(name, 0, (Oid)null, 1);
        }
        ExtendedGSSContext context = (ExtendedGSSContext)manager.createContext(null, GSSConstants.MECH_OID, cred, 0);
        if(anon)
            context.requestAnonymity(true);
        context.setOption(GSSConstants.GSS_MODE, GSIConstants.MODE_SSL);
        if(GSIConstants.ENCRYPTION.equals(protection))
            context.requestConf(true);
        else
            context.requestConf(false);
        if(trustedCerts != null)
            context.setOption(GSSConstants.TRUSTED_CERTIFICATES, trustedCerts);
        myContext = context;
        myAuth = auth;
    }

    public GssSocket wrapSocket(Socket socket)
    {
        GssSocketFactory factory = GssSocketFactory.getDefault();
        GssSocket gsiSocket = (GssSocket)factory.createSocket(socket, host, port, myContext);
        gsiSocket.setAuthorization(myAuth);
        return gsiSocket;
    }

    private String host;
    private int port;
    private Authorization myAuth;
    private ExtendedGSSContext myContext;
}
