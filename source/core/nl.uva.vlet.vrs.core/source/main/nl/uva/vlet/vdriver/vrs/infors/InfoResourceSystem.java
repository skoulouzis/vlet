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
 * $Id: InfoResourceSystem.java,v 1.1 2011-11-25 13:40:48 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:48 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vdriver.vrs.infors.grid.GridNeighbourhood;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VResourceSystem;

public class InfoResourceSystem extends CompositeServiceInfoNode implements VResourceSystem
{
    private GridNeighbourhood gridRoot; 
    private LocalSystem localSystemRoot;  
    
    InfoResourceSystem(VRSContext context) throws VRLSyntaxException
    {
        super(context,new VRL("info:/"));
        initChilds();
        this.setEditable(false); 
    }
    
    private void initChilds()
    {
        gridRoot=new GridNeighbourhood(this.vrsContext);
        
        localSystemRoot=new LocalSystem(this.vrsContext); 
        
        VNode nodes[]=new VNode[2]; 
        nodes[0]=gridRoot;
        nodes[1]=localSystemRoot;
        
        this.setChilds(nodes);  
    }
    
    // IMPORTANT: One ID per context ! (singleton instance) 
    public String getID()
    {
        return InfoConstants.INFO_INSTANCE_ID;    
    }

    public VNode openLocation(VRL vrl) throws VlException
    {
        int len=0;
        
        String[] paths=vrl.getPathElements(); 
        
        if (paths!=null)
            len=paths.length; 
        
        if ((len==0) || (paths[0].equals(""))) 
            return this;
        
        debug("Get info resource from root:"+paths[0]); 
        
        VNode node=null; 
        // len >=1
        if (StringUtil.equals(gridRoot.getVRL().getPathElements()[0],paths[0]))
        {
            if (len==1)
                return gridRoot;
            else
            {
                node=gridRoot.findNode(vrl,true);
                if (node!=null)
                    return node; 
                
                throw new ResourceNotFoundException("No information for grid resource:"+vrl);
            }
        }
        
        if (StringUtil.equals(localSystemRoot.getVRL().getPathElements()[0],paths[0]))
        {
            if (len==1)
                return localSystemRoot;
            else
            {
                node=localSystemRoot.findNode(vrl,true);
                if (node!=null)
                    return node; 
                
                throw new ResourceNotFoundException("No information for local resource:"+vrl);
            }
        }
            
        throw new ResourceNotFoundException("No information for:"+vrl);
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String getType()
    {
        return InfoConstants.INFONODE_TYPE; 
    }

    private void debug(String msg)
    {
        Global.debugPrintf(this,"%s\n",msg); 
    }
    
    @Override
    public void connect()
    {
    }
    
    @Override
    public void disconnect()
    {
    }

    @Override
    public void dispose()
    {
    }
    
}
