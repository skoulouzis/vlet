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
 * $Id: SRMPutRequest.java,v 1.8 2011-04-18 12:21:32 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:32 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.ArrayOfAnyURI;

import org.apache.axis.types.URI;

/**
 * SRM PutRequest object. Holds SRM Urls and Transport URLs.
 * 
 * @author Piter T. de Boer
 * 
 */
public class SRMPutRequest
{
    private String token;

    ArrayOfAnyURI SURLs;

    ArrayOfAnyURI TURLs;

    /**
     * Gets the SRM URLs
     * 
     * @return the SRM URLs
     */
    public ArrayOfAnyURI getSURLs()
    {
        return this.SURLs;
    }

    /**
     * Sets the SRM URLs
     * 
     * @param surls
     *            the SRM URLs
     */
    public void setSURLs(final ArrayOfAnyURI surls)
    {
        this.SURLs = surls;
    }

    /**
     * Gets an array of transport URLs.
     * 
     * @return the array of the transport URLs
     */
    public ArrayOfAnyURI getTURLs()
    {
        return this.TURLs;
    }

    /**
     * Sets an array of transport URLs.
     * 
     * @param turls
     *            the array of the transport URLs
     */
    public void setTURLs(final ArrayOfAnyURI turls)
    {
        this.TURLs = turls;
    }

    /**
     * Gets the ith element of the array of transport URLs.
     * 
     * @param i
     *            the index
     * @return the transport URL
     */
    public URI getTurl(int i)
    {
        return TURLs.getUrlArray()[i];
    }

    /**
     * Gets the request token for this request. It's a string uniquely
     * identifying each request.
     * 
     * @return the request token.
     */
    public String getToken()
    {
        return this.token;
    }

    /**
     * Sets the request token for this request. It's a string uniquely
     * identifying each request.
     * 
     * @param token
     *            the token to set
     */
    public void setToken(String token)
    {
        this.token = token;
    }

}
