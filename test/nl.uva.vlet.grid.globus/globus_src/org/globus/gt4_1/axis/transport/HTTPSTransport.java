// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HTTPSTransport.java

package org.globus.gt4_1.axis.transport;


// Referenced classes of package org.globus.axis.transport:
//            GSIHTTPTransport

public class HTTPSTransport extends GSIHTTPTransport
{

    public HTTPSTransport()
    {
        transportName = "https";
    }

    public HTTPSTransport(String url, String action)
    {
        super(url, action);
        transportName = "https";
    }

    public static final String DEFAULT_TRANSPORT_NAME = "https";
}
