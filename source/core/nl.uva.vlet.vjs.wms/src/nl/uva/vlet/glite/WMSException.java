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
 * $Id: WMSException.java,v 1.3 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package nl.uva.vlet.glite;

/**
 * Generic WMS Exception wrapper. Use getCause() to get nested exception.
 * If an AxisFault was detected, the field {faultCode,faultText,faultDescription} had the fault information.
 */
public class WMSException extends Exception
{
    private static final long serialVersionUID = 6755443017493384972L;
    // === 
    
    protected String faultCode;
    protected String faultText; 
    protected String faultDescription; 
    
    public WMSException(String msg)
    {
        super(msg);
    }

    public WMSException(String msg, Throwable nestedException)
    {
        super(msg + "\n---\n" + nestedException.getMessage(), nestedException);
    }

    public WMSException(Throwable nestedException)
    {
        super(nestedException.getMessage(), nestedException);
    }

    public WMSException(String msgStr, String code, String text, String description, Throwable e)
    {
        super(msgStr,e);
        this.faultCode=code;
        this.faultText=text;
        this.faultDescription=description;
    }
    
    public String getFaultCode()
    {
        return faultCode; 
    }
    
    public String getFaultText()
    {
        return faultText;
    }
    
    public String getFaultDescription()
    {
        return faultDescription;
    }

}
