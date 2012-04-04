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
 * $Id: LBGroupNode.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;
///*
// * Copyright 2006-2009 The Virtual Laboratory for e-Science (VL-e) 
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
// * $Id: LBGroupNode.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
// * $Date: 2011-04-18 12:28:50 $
// */
//// source: 
//
//package nl.uva.vlet.vjs.wmsjs;
//
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrs.VCompositeNode;
//
//public class LBGroupNode extends VCompositeNode
//{
//    private WMSResource wms = null;
//
//    /** Cached Node: won't change often! */
//    private LBNode[] lbNodes = null;
//
//    public LBGroupNode(WMSResource wms)
//    {
//        super(wms.getContext(), wms.getVRL().append("lb"));
//        this.wms = wms;
//    }
//
//    @Override
//    public String getType()
//    {
//        return "LBGroup";
//    }
//
//    private Object queryLBMutex = new Object();
//
//    @Override
//    public LBNode[] getNodes() throws VlException
//    {
//        return null;
//        // // synchronize
//        // synchronized (queryLBMutex)
//        // {
//        // if (lbNodes == null)
//        // lbNodes = this.wms.getLBNodes(true);
//        // return lbNodes;
//        // }
//    }
//
//    @Override
//    public String[] getResourceTypes()
//    {
//        return null;
//    }
//
//    public WMSResource getWMS()
//    {
//        return wms;
//    }
//
//    public LBNode findChild(VRL vrl) throws VlException
//    {
//        LBNode nodes[] = getNodes();
//
//        for (LBNode node : nodes)
//            if (node.getVRL().equals(vrl))
//                return node;
//
//        return null;
//    }
//
// }
