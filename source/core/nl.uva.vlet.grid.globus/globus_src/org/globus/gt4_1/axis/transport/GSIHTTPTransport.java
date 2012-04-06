// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GSIHTTPTransport.java

package org.globus.gt4_1.axis.transport;

import org.apache.axis.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;
import org.globus.gt4_1.axis.gsi.GSIConstants;

public class GSIHTTPTransport extends Transport
    implements GSIConstants
{

    public GSIHTTPTransport()
    {
        transportName = "httpg";
    }

    public GSIHTTPTransport(String url, String action)
    {
        transportName = "httpg";
        this.url = url;
        this.action = action;
    }

    public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine)
        throws AxisFault
    {
        if(action != null)
        {
            mc.setUseSOAPAction(true);
            mc.setSOAPActionURI(action);
        }
        if(cookie != null)
            mc.setProperty("Cookie", cookie);
        if(cookie2 != null)
            mc.setProperty("Cookie2", cookie2);
        if(mc.getService() == null)
            mc.setTargetService(mc.getSOAPActionURI());
    }

    public void processReturnedMessageContext(MessageContext context)
    {
        cookie = context.getStrProp("Cookie");
        cookie2 = context.getStrProp("Cookie2");
    }

    public static final String DEFAULT_TRANSPORT_NAME = "httpg";
    public static final String URL = "transport.url";
    private String cookie;
    private String cookie2;
    private String action;
}
