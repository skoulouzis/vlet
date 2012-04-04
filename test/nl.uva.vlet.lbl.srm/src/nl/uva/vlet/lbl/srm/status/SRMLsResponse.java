///*
// * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
// * 
// * Licensed under the Apache License, Version 2.0 (the "License").  
// * You may not use this file except in compliance with the License. 
// * For details, see the LICENCE.txt file location in the root directory of this 
// * distribution or obtain the Apache Licence at the following location: 
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the License is distributed on an "AS IS" BASIS, 
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// * See the License for the specific language governing permissions and 
// * limitations under the License.
// * 
// * See: http://www.vl-e.nl/ 
// * See: LICENCE.txt (located in the root folder of this distribution). 
// * ---
// * $Id: SRMLsResponse.java,v 1.1 2011-09-21 11:14:21 ptdeboer Exp $  
// * $Date: 2011-09-21 11:14:21 $
// */ 
//// source: 
//
//package nl.uva.vlet.lbl.srm.status;
//
//import gov.lbl.srm.v22.stubs.SrmLsResponse;
//import gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse;
//import gov.lbl.srm.v22.stubs.TReturnStatus;
//
//import org.apache.axis.types.URI;
//
///**
// * Wraps the <code>gov.lbl.srm.v22.SrmLsResponse</code>. 
// * 
// * @author Piter T. de Boer
// * 
// */
//public class SRMLsResponse implements ISRMResponse
//{
//    private SrmLsResponse response;
//
//    private URI[] suris;
//
//    /**
//     * Creates an instance of an
//     * <code>nl.uva.vlet.lbl.srm.status.SRMGetResponse</code>.
//     * 
//     * @param responce
//     *            the get response obtained from from
//     *            <coed>srmPrepareToGet</code> in <code>ISRM</code>
//     * @param suris
//     */
//    public SRMLsResponse(SrmLsResponse responce, org.apache.axis.types.URI[] suris)
//    {
//        this.response = responce;
//        this.suris = suris;
//    }
//
//    @Override
//    public String getRequestToken()
//    {
//        return response.getRequestToken();
//    }
//
//    @Override
//    public TReturnStatus getReturnStatus()
//    {
//        return response.getReturnStatus();
//    }
//
//    @Override
//    public URI[] getSURIs()
//    {
//        return this.suris;
//    }
//
//    // @Override
//    // public void setSURLs(URI[] suris)
//    // {
//    // this.suris = suris;
//    //        
//    // }
//
//}
