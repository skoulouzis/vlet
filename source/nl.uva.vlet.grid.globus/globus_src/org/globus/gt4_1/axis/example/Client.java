// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Client.java

package org.globus.gt4_1.axis.example;

import java.io.PrintStream;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.utils.Options;
import org.globus.gsi.gssapi.auth.SelfAuthorization;
import org.globus.gt4_1.axis.transport.GSIHTTPSender;
import org.globus.gt4_1.axis.transport.HTTPSSender;
import org.globus.gt4_1.axis.util.Util;

public class Client
{

    public Client()
    {
    }

    public static void main(String args[])
    {
        Util.registerTransport();
        try
        {
            Options options = new Options(args);
            String endpointURL = options.getURL();
            args = options.getRemainingArgs();
            String textToSend;
            if(args == null || args.length < 1)
                textToSend = "<nothing>";
            else
                textToSend = args[0];
            SimpleProvider provider = new SimpleProvider();
            SimpleTargetedChain c = null;
            c = new SimpleTargetedChain(new GSIHTTPSender());
            provider.deployTransport("httpg", c);
            c = new SimpleTargetedChain(new HTTPSSender());
            provider.deployTransport("https", c);
            c = new SimpleTargetedChain(new HTTPSender());
            provider.deployTransport("http", c);
            Util.reregisterTransport();
            Service service = new Service(provider);
            Call call = (Call)service.createCall();
            call.setProperty("org.globus.gsi.authorization", SelfAuthorization.getInstance());
            call.setProperty("org.globus.gsi.mode", "gsilimited");
            call.setTargetEndpointAddress(new URL(endpointURL));
            call.setOperationName(new QName("MyService", "serviceMethod"));
            call.addParameter("arg1", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            String ret = (String)call.invoke(new Object[] {
                textToSend
            });
            System.out.println("Service response : " + ret);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
