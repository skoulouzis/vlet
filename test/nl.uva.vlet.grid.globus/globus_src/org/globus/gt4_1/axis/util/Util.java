// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Util.java

package org.globus.gt4_1.axis.util;

import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.ietf.jgss.GSSCredential;

public class Util
{

    public Util()
    {
    }

    public static GSSCredential getCredentials(MessageContext msgContext)
    {
        return (GSSCredential)msgContext.getProperty("org.globus.gsi.credentials");
    }

    public static synchronized void registerTransport()
    {
        if(transportRegistered)
        {
            return;
        } else
        {
            reregisterTransport();
            transportRegistered = true;
            return;
        }
    }

    public static synchronized void reregisterTransport()
    {
        Call.initialize();
        Call.addTransportPackage("org.globus.net.protocol");
        Call.setTransportForProtocol("httpg", org.globus.gt4_1.axis.transport.GSIHTTPTransport.class);
        Call.setTransportForProtocol("https", org.globus.gt4_1.axis.transport.HTTPSTransport.class);
    }

    public static Object getProperty(MessageContext context, String property)
    {
        Object val = null;
        val = context.getProperty(property);
        if(val != null)
            return val;
        Call call = (Call)context.getProperty("call_object");
        if(call == null)
            return null;
        else
            return call.getProperty(property);
    }

    private static boolean transportRegistered = false;

    static 
    {
        registerTransport();
    }
}
