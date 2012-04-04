// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HTTPUtils.java

package org.globus.gt4_1.axis.transport;

import java.util.Hashtable;
import org.apache.axis.client.Stub;
import org.apache.axis.transport.http.HTTPConstants;

public class HTTPUtils
{

    public HTTPUtils()
    {
    }

    public static void setTimeout(javax.xml.rpc.Stub stub, int timeout)
    {
        if(stub instanceof Stub)
            ((Stub)stub).setTimeout(timeout);
    }

    public static void setCloseConnection(javax.xml.rpc.Stub stub, boolean close)
    {
        Hashtable headers = getRequestHeaders(stub);
        if(close)
            headers.put("Connection", HTTPConstants.HEADER_CONNECTION_CLOSE);
        else
            headers.remove("Connection");
    }

    public static void setHTTP10Version(javax.xml.rpc.Stub stub, boolean enable)
    {
        setHTTPVersion(stub, enable);
    }

    public static void setHTTPVersion(javax.xml.rpc.Stub stub, boolean http10)
    {
        stub._setProperty("axis.transport.version", http10 ? ((Object) (HTTPConstants.HEADER_PROTOCOL_V10)) : ((Object) (HTTPConstants.HEADER_PROTOCOL_V11)));
    }

    public static void setChunkedEncoding(javax.xml.rpc.Stub stub, boolean enable)
    {
        setDisableChunking(stub, !enable);
    }

    public static void setDisableChunking(javax.xml.rpc.Stub stub, boolean disable)
    {
        stub._setProperty("transport.http.disableChunking", disable ? ((Object) (Boolean.TRUE)) : ((Object) (Boolean.FALSE)));
        Hashtable headers = getRequestHeaders(stub);
        headers.put(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED, disable ? "false" : "true");
    }

    private static Hashtable getRequestHeaders(javax.xml.rpc.Stub stub)
    {
        Hashtable headers = (Hashtable)stub._getProperty("HTTP-Request-Headers");
        if(headers == null)
        {
            headers = new Hashtable();
            stub._setProperty("HTTP-Request-Headers", headers);
        }
        return headers;
    }

    public static final String DISABLE_CHUNKING = "transport.http.disableChunking";
}
