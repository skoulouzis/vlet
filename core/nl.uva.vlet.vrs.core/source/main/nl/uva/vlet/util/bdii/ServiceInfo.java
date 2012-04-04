/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: ServiceInfo.java,v 1.10 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */ 
// source: 

package nl.uva.vlet.util.bdii;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/**
 * Info object describing BDII Service Element.
 */
public class ServiceInfo
{
    public static enum ServiceInfoType
    {
        NILL, SRMV11, SRMV21, SRMV22, LFC, WMS, LB
    };

    public static Map<ServiceInfoType, String> serviceTypeToScheme = new HashMap<ServiceInfoType, String>();

    static
    {
        serviceTypeToScheme.put(ServiceInfoType.SRMV11, "srm");
        serviceTypeToScheme.put(ServiceInfoType.SRMV21, "srm");
        serviceTypeToScheme.put(ServiceInfoType.SRMV22, "srm");
        serviceTypeToScheme.put(ServiceInfoType.LFC, "lfn");
        serviceTypeToScheme.put(ServiceInfoType.WMS, "wms");
        // LB URIs are handled by the 'wms' implementation!
        serviceTypeToScheme.put(ServiceInfoType.LB, "wms");
    }

    private VAttributeSet infoAttributes = new VAttributeSet(); // default empty

    private String protocol = null;

    private String hostname = null;

    private int port = 0;

    private ServiceInfoType serviceType = ServiceInfoType.NILL;

    protected ServiceInfo()
    {

    }

    public ServiceInfo(ServiceInfoType type, String protocol, String host, int port) throws VlException
    {
        this.serviceType = type;

        this.protocol = protocol;
        if (host == null)
        {
            throw new VlException("Host can't be null. Info details: \n \t Type: " + type + "\n \t protocol: "
                    + protocol + "\n \t port: " + port);
        }
        this.hostname = host;
        this.port = port;
    }

    public String toString()
    {
        return "<StorageServiceType>[" + serviceType + "]" + protocol + "://" + hostname + ":" + port;
    }

    public int getPort()
    {
        return port;
    }

    public String getHost()
    {
        return hostname;
    }

    /**
     * Returns 'logical' scheme for VRL, for
     * "WMS this is "wms" and not "http[s]"
     */
    public String getScheme()
    {
        return protocol;
    }

    public ServiceInfoType getServiceType()
    {
        return serviceType;
    }

    public VRL toVRL()
    {
        // optimalization: NULL ServiceInfo is NILL type but return NULL vrl!
        if (this.serviceType == ServiceInfoType.NILL)
            return null;

        return new VRL(getScheme(), null, getHost(), getPort(), "/");
    }

    public static ServiceInfo createFrom(URI endpointURI, ServiceInfoType type)
    {
        ServiceInfo info = new ServiceInfo();
        info.serviceType = type;

        info.hostname = endpointURI.getHost();
        String scheme = serviceTypeToScheme.get(type);
        if (StringUtil.isEmpty(scheme))
            scheme = endpointURI.getScheme();
        info.protocol = scheme;
        info.port = endpointURI.getPort();

        return info;
    }

    public static ServiceInfo createNill(ServiceInfoType srmType, String host)
    {
        ServiceInfo info = new ServiceInfo();
        info.serviceType=srmType;
        info.hostname=host; 
        return info;
    }

    public static ServiceInfo createWMService(String wmsUri) throws URISyntaxException
    {
        return createFrom(new URI(wmsUri), ServiceInfoType.WMS);
    }

    public static ServiceInfo createLBService(String lbUri) throws URISyntaxException
    {
        return createFrom(new URI(lbUri), ServiceInfoType.LB);
    }

    public boolean isSRMV22()
    {
        return (this.serviceType == ServiceInfoType.SRMV22);
    }

    public boolean isSRMV11()
    {
        return (this.serviceType == ServiceInfoType.SRMV11);
    }

    public boolean isWMS()
    {
        return (this.serviceType == ServiceInfoType.WMS);
    }

    public boolean isLFC()
    {
        return (this.serviceType == ServiceInfoType.LFC);
    }

    public void addInfoAttribute(String name, String value)
    {
        infoAttributes.put(new VAttribute(name, value));
    }

    public void addInfoAttribute(VAttribute attr)
    {
        infoAttributes.put(attr);
    }

    public void addInfoAttributes(VAttribute[] attrs)
    {
        if (attrs != null)
        {
            infoAttributes.add(attrs);
        }

    }

    public VAttributeSet getInfoAttributes()
    {
        return infoAttributes;
    }

    /** Returns Service VRL, for example "srm://host:port/" */
    public VRL getServiceVRL()
    {
        return toVRL();
    }

}