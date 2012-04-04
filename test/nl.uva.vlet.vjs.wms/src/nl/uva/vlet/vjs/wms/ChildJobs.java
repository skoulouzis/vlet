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
 * $Id: ChildJobs.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vjs.VJS;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

public class ChildJobs extends VCompositeNode // implements VPresentable
{
    private WMSJob[] myNodes;

    public ChildJobs(VRSContext ctx, VRL vrl, WMSJob[] jobs)
    {
        super(ctx, vrl);
        this.myNodes = jobs;
    }

    @Override
    public String getType()
    {
        return "wmsJob";
    }

    @Override
    public VNode[] getNodes() throws VlException
    {
        return myNodes;
    }

    @Override
    public String[] getResourceTypes()
    {
        return new String[] { VJS.TYPE_VJOB };
    }

    // public method to allow invocation from other object!
    public void fireChildAdded(VRL vrl)
    {
        super.fireChildAdded(vrl);
    }

    // public method to allow invocation from other object!
    public void fireChildDeleted(VRL vrl)
    {
        super.fireChildDeleted(vrl);
    }

    // public method to allow invocation from other object!
    public void fireSetChilds(VRL vrls[])
    {
        super.fireSetChilds(vrls);
    }

    public VNode findChild(VRL vrl) throws VlException
    {
        for (int i = 0; i < this.myNodes.length; i++)
        {
            if (myNodes[i].getVRL().equals(vrl))
            {
                return myNodes[i];
            }
        }
        
//        for (int i = 0; i < this.myNodes.length; i++)
//        {
//            if (myNodes[i].getVRL().equals(vrl))
//            {
//                return myNodes[i].findChild(vrl);
//            }
//        }

        return null;
    }

    // public Presentation getPresentation()
    // {
    // if (this.presentation==null)
    // {
    // this.presentation=LBResource.createDefaultPresentation();
    // }
    // return this.presentation;
    // }

}
