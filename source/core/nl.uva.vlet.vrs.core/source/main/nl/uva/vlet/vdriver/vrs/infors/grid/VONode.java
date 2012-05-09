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
 * $Id: VONode.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.grid;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vdriver.vrs.infors.CompositeServiceInfoNode;
import nl.uva.vlet.vdriver.vrs.infors.InfoConstants;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.ResourceFolder;
import nl.uva.vlet.vrs.VCompositeDeletable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

/**
 *  VO Resource Folder 
 */
public class VONode extends CompositeServiceInfoNode<VNode> implements VCompositeDeletable
{
    // ===  Class === // 
    private ResourceFolder seLocations; 
    private String vo=null;
    private VOGroupsNode parentServiceNode=null;
    private ResourceFolder lfcLocations;
    private ResourceFolder wmsLocations;
    private ResourceFolder lbLocations; 
    
    boolean showWms=true; 
    boolean showLbs=true; 
    
    public static VONode createVOGroup(VOGroupsNode groupsParentNode, String vo)
    {
        VRL vrl=groupsParentNode.getVRL().append(vo); 
         
        VONode vogrp=new VONode(groupsParentNode.getVRSContext(),vrl);
        vogrp.vo=vo; 
        vogrp.parentServiceNode=groupsParentNode;
      
        return vogrp;
    }
   
    // === Instance === //
    
    public VONode(VRSContext context, VRL vrl)
    {
        super(context, vrl);
        this.setEditable(false);
    }

    @Override
    public String getType()
    {
       return InfoConstants.VO_TYPE;  
    }

   
    
    public String getIconURL(int prefSize)
    {
        String vostr=vrsContext.getGridProxy().getVOName();
        
        if (StringUtil.compareIgnoreCase(vo,vostr)==0)
            if (prefSize>32)
                return "triperson-check-128.png";
            else
                return "triperson-check-32.png";
        
        return "triperson-128.png";
    }
    
    public synchronized VNode[] getNodes() throws VlException
    {
        initChilds(); 
        
        int numN=2+(showWms?1:0)+(showLbs?1:0); 
       
        VNode nodes[]=new VNode[numN]; 
        int index=0; 
        
        nodes[index++]=lfcLocations;
        nodes[index++]=seLocations;
        if (showWms) 
            nodes[index++]=wmsLocations; 
        if (showLbs) 
            nodes[index++]=lbLocations; 
        
    	// update super class internal array! 
		setChilds(nodes); 
		
        return nodes; 
    }
    
    private void initChilds() throws VlException
    {
        if (lfcLocations==null)
            initLFCs(); 

        if (seLocations==null) 
            initSEs();
        
        if ( (wmsLocations==null) && (showWms))
            initWMSs();
        
        if ( (this.lbLocations==null) && (showLbs))
            initLBs(); 
    }

    public String[] getResourceTypes()
    {
        return null;
    }

    private void initSEs() throws VlException
    {
        this.seLocations=parentServiceNode.createSEFolderForVO(this.getVRL(),vo); 
    }

    private void initLFCs() throws VlException
    {
        this.lfcLocations=parentServiceNode.createLFCFolderForVO(this.getVRL(),vo); 
    }
    
    private void initWMSs() throws VlException
    {
        this.wmsLocations=parentServiceNode.createWMSFolderForVO(this.getVRL(),vo); 
    }
    
    private void initLBs() throws VlException
    {
        this.lbLocations=parentServiceNode.createLBFolderForVO(this.getVRL(),vo); 
    }

    public boolean delete()
    {
    	return this.parentServiceNode.deleteVOGroup(this); 
    }
    
    public boolean delete(boolean recurse)
    {
    	return delete(); 
    }
}
