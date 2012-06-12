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
 * $Id: VRLStreamHandlerFactory.java,v 1.6 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */
// source: 

package nl.uva.vlet.vrl;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import nl.uva.vlet.Global;
import nl.uva.vlet.net.ssl.SslUtil;

/**
 * The VRLStreamHandlerFactory.
 * 
 * It extends URLStreamHandler with the supported protocols from the VRS
 * Registry. Important: At startup this StreamHandleFactory has to be created
 * and set as default in:
 * 
 * <pre>
 * URL.setURLStreamHandlerFactory();
 * </pre>
 * 
 * Currently this is done in the Global class. After this, the URL class can be
 * use to VRS protocols !
 * <p>
 * Examples:
 * <li>URL url=new URL("gftp://fs2.da2.nikhef.nl/");</li>
 * 
 * @author P.T. de Boer
 */

public class VRLStreamHandlerFactory implements URLStreamHandlerFactory
{
    private static VRLStreamHandlerFactory instance = null;

    // no constructor:

    // Whether to use SUN's 'file://' URL reader !
    private static boolean use_sun_file_handler = true;

    public synchronized static VRLStreamHandlerFactory getDefault()
    {
        if (instance == null)
            instance = new VRLStreamHandlerFactory();

        return instance;
    }

    // =========================================================================
    // //
    //
    // =========================================================================
    // //

    private Class<? extends URLStreamHandler> httpsURLHandlerClass;

    private Class<? extends URLStreamHandler> httpgURLHandlerClass;

    private VRLStreamHandlerFactory()
    {
        this.httpsURLHandlerClass = SslUtil.createHttpsHandler().getClass();
    }

    public URLStreamHandler createURLStreamHandler(String protocol)
    {
        Global.debugPrintf(this,"createURLStreamHandler() for:%s\n",protocol);

        if (protocol.compareToIgnoreCase("jar") == 0)
        {
            // return sun's default HTTP handler !
            return new sun.net.www.protocol.jar.Handler();
        }
        // sun http handler !
        else if (protocol.compareToIgnoreCase("mailto") == 0)
        {
            // return sun's default HTTP handler !
            return new sun.net.www.protocol.mailto.Handler();
        }
        // sun http handler !
        else if (protocol.compareToIgnoreCase("ftp") == 0)
        {
            // return sun's default HTTP handler !
            return new sun.net.www.protocol.ftp.Handler();
        }
        else if (protocol.compareToIgnoreCase("gopher") == 0)
        {
            // return sun's default HTTP handler !
            return new sun.net.www.protocol.gopher.Handler();
        }
        // not in java 1.7 :
        /*
         * else if (protocol.compareToIgnoreCase("verbatim")==0) { // return
         * sun's default HTTP handler ! return new
         * sun.net.www.protocol.verbatim.Handler(); } else if
         * (protocol.compareToIgnoreCase("systemresource")==0) { // return sun's
         * default HTTP handler ! return new
         * sun.net.www.protocol.systemresource.Handler(); }
         */
        else if (protocol.compareToIgnoreCase("https") == 0)
        {

            // return sun's default HTTP handler !

            // this.httpsURLHandlerClass=SslUtil.createHttpsHandler().getClass();

            try
            {
                return this.httpsURLHandlerClass.newInstance();
            }
            catch (Exception e)
            {
                throw new Error(e);
            }

        }
        else if ((protocol.compareToIgnoreCase("httpg") == 0) && (httpgURLHandlerClass != null))
        {
            try
            {
                // Use Globus GSS/GSI URL socket handler
                return this.httpgURLHandlerClass.newInstance();
            }
            catch (Exception e)
            {
                throw new Error(e);
            }

        }
        else if (protocol.compareToIgnoreCase("http") == 0)
        {

            String proxyHost = null;
            int proxyPort = 0;

            if (Global.useHttpProxy())
            {

                proxyHost = Global.getProxyHost();
                proxyPort = Global.getProxyPort();

                Global.infoPrintf(this, "Using HTTP Proxy=%s:%d\n",proxyHost,proxyPort);

                // return sun's default HTTP handler !
                return new sun.net.www.protocol.http.Handler(proxyHost, proxyPort);
            }
            else
            {
                //Global.infoPrintln(this, "Using plain HTTP connection");

                // return sun's default HTTP handler !
                return new sun.net.www.protocol.http.Handler();
            }
        }
        //
        // Use own https handler so that server certificates
        // can be added.
        //

        /*
         * // sun https handler ! else if
         * (protocol.compareToIgnoreCase("https")==0) { // return sun's default
         * HTTPS handler ! return new sun.net.www.protocol.https.Handler(); }
         */
        else if (use_sun_file_handler)
        {
            //
            // Following protocols could be handled by the VRS Registry
            // but only AFTER the Reigstry has been initialized.
            // Currently the Registry sets the VRLStreamHandler after
            // initialition
            // so the following code isn't necessary :
            //

            // WINDOWS PATH: [aA]:,[bB]:,[cC]:,[dD]:,... !
            if (protocol.charAt(1) == ':')
            {
                return new sun.net.www.protocol.file.Handler();
            }

            // sun jar handler !
            else if (protocol.compareToIgnoreCase("file") == 0)
            {

                // return sun's default HTTP handler !
                return new sun.net.www.protocol.file.Handler();
            }

        }

        // return VRStreamHandler

        return new VRLStreamHandler();
    }

    public void setHTTPGUrlHandlerClass(Class<? extends URLStreamHandler> handlerClass)
    {
        // Todo: Better GSS/GSI/SSL integration:
        this.httpgURLHandlerClass = handlerClass;
    }

}
