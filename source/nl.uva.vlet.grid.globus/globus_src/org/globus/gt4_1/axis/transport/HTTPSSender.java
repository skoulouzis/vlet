// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HTTPSSender.java

package org.globus.gt4_1.axis.transport;

import java.io.IOException;
import org.apache.axis.MessageContext;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.SocketHolder;

// Referenced classes of package org.globus.axis.transport:
//            SSLContextHelper

public class HTTPSSender extends HTTPSender
{

    public HTTPSSender()
    {
    }

    protected void getSocket(SocketHolder sockHolder, MessageContext msgContext, String protocol, String host, int port, int timeout, StringBuffer otherHeaders, 
            BooleanHolder useFullURL)
        throws Exception
    {
        if(!protocol.equalsIgnoreCase("https"))
        {
            throw new IOException("Invalid protocol");
        } else
        {
            int lport = port != -1 ? port : 8443;
            SSLContextHelper helper = new SSLContextHelper(msgContext, host, lport);
            super.getSocket(sockHolder, msgContext, "http", host, lport, timeout, otherHeaders, useFullURL);
            org.globus.gsi.gssapi.net.GssSocket gsiSocket = helper.wrapSocket(sockHolder.getSocket());
            sockHolder.setSocket(gsiSocket);
            return;
        }
    }
}
