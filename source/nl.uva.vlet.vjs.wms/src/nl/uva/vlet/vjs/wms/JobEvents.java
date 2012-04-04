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
 * $Id: JobEvents.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

import org.glite.wsdl.types.lb.Event;

public class JobEvents extends VCompositeNode
{
    private Event[] events;

    private VRSContext context;

    private VRL vrl;

    private JobEventNode[] eventNodes;

    public JobEvents(VRSContext context, VRL vrl, Event[] events) throws VlException
    {
        super(context, vrl);
        this.events = events;
        this.context = context;
        this.vrl = vrl;
        initEventNodes();

    }

    private void initEventNodes() throws VlException
    {
        // this.eventNodes = new JobEventNode[events.length];
        //
        // for (int i = 0; i < events.length; i++)
        // {
        // eventNodes[i] = new JobEventNode(context, vrl.append("Event" + i),
        // events[i]);
        //
        // debug("Node[" + i + "] " + eventNodes[i].getVRL());
        // }

        this.eventNodes = new JobEventNode[1];
        eventNodes[0] = new JobEventNode(context, vrl.append("Event"), events);
    }

    @Override
    public boolean exists() throws VlException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getType()
    {
        return "JobEvents";
    }

    @Override
    public VNode[] getNodes() throws VlException
    {
        return eventNodes;
    }

    private void debug(String msg)
    {
        System.err.println(this.getClass().getName() + ": " + msg);
    }

    @Override
    public String[] getResourceTypes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JobEventNode findChild(VRL vrl)
    {
        for (int i = 0; i < this.eventNodes.length; i++)
        {
            if (eventNodes[i].getVRL().equals(vrl))
            {
                return eventNodes[i];
            }
        }

        return null;
    }

}
