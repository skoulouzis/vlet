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
 * $Id: SRMException.java,v 1.9 2011-04-18 12:21:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:33 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm;

import gov.lbl.srm.v22.stubs.TReturnStatus;
import gov.lbl.srm.v22.stubs.TStatusCode;

public class SRMException extends Exception
{

    private static final long serialVersionUID = -1216958405569924136L;

    // public static enum ErrorType
    // {
    // GENERAL_ERROR,
    // CONNECTION_ERROR,
    // AUTHENTICATION_ERROR,
    // PATH_NOT_FOUND,
    // PATH_ALREADY_EXISTS,
    // PERMISSION_DENIED,
    // URI_SYNTAX_ERROR
    // ;
    // }

    public static SRMException createConnectionException(String message, Throwable cause)
    {
        return new SRMException(message, cause);
    }

    // public static SRMException createURISyntaxException(String message,
    // Exception cause)
    // {
    // return new SRMURIException(message,cause);
    //    
    // }

    // ========================================================================
    // Instance
    // ========================================================================

    // private ErrorType errorType;

    // SRM status code, if provided. Else null.
    private TReturnStatus returnStatus = null;

    // public SRMException(String message,ErrorType type,TStatusCode
    // code,Throwable cause)
    // {
    // super(message,cause);
    // this.errorType=type;
    // this.statusCode=code;
    // }

    public SRMException(String message, TReturnStatus returnStatus, Throwable cause)
    {
        super(message, cause);
        this.returnStatus = returnStatus;
    }
    
    public SRMException(String message)
    {
        super(message); 
    }

    // public SRMException(String message,ErrorType type,Throwable cause)
    // {
    // super(message,cause);
    // this.errorType=type;
    // this.statusCode=null;
    // }

    // public SRMException(String message, ErrorType type)
    // {
    // super(message);
    // this.errorType=type;
    // }

    public SRMException(String message, Throwable cause)
    {
        super(message, cause);
        // this.errorType=ErrorType.GENERAL_ERROR;
    }

    // public ErrorType getErrorType()
    // {
    // return errorType;
    // }


    public TReturnStatus getReturnStatus()
    {
        return this.returnStatus;
    }
    
    /** Return explanation String from return status. */ 
    public String getReturnStatusExplanation()
    {
        if (this.returnStatus==null)
            return "No return status given."; 
        
        String explStr=this.returnStatus.getExplanation();
        
        if (explStr==null)
            return "No explanation given."; 
        
        return explStr; 
    }

    protected void setStatusCode(TReturnStatus status)
    {
        this.returnStatus = status;
    }

    public boolean hasReturnStatusCode(TStatusCode statusCode)
    {
        if (returnStatus==null)
            return false; 
        
        TStatusCode code = returnStatus.getStatusCode(); 
        
        if (code==null)
            return false; 
        
        return (code==statusCode); 
    }

}
