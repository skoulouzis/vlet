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
 * $Id: ProxyNodeTableModel.java,v 1.3 2011-04-18 12:27:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:27 $
 */ 
// source: 

package nl.uva.vlet.gui.proxymodel;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ICON;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LOCATION;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTableModel;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vrl.VRL;

public class ProxyNodeTableModel extends ResourceTableModel
{
    private static final long serialVersionUID = 8402972068504033387L;
    private ProxyNode pnode;
    
    public ProxyNodeTableModel(ProxyNode node)
    {
        this.pnode=node; 
        init(); 
    }
    
    public void init()
    {
        // dummy tabel: 
        StringList headers=new StringList();
        
        headers.add(ATTR_ICON);
        headers.add(ATTR_TYPE);
        headers.add(ATTR_NAME);
        headers.add(ATTR_LOCATION);
        
        this.setHeaders(headers.toArray()); 
        this.setAllHeaders(new StringList(pnode.getAttributeNames()));
        
        fetchData(); 
    }

    private void fetchData()
    {
        this.clearData();
        
        ActionTask task=new ActionTask(null,"Test get ProxyNode data")
        {
            public void doTask()
            {
                ProxyNode nodes[];
                
                try
                {
                    nodes = pnode.getChilds(null);
                }
                catch (VlException e)
                {
                    handle(e); 
                    return; 
                }
                
                for (ProxyNode node:nodes)
                {
                    VAttributeSet set=new VAttributeSet();
                    set.put(ATTR_NAME,node.getVRL().getBasename()); 
                    set.put(ATTR_TYPE,node.getType()); 
                    set.put(new VAttribute(ATTR_LOCATION,node.getVRL())); 
                    
                    addRow(node.getVRL().toString(),set);  
                }
                
                for (ProxyNode node:nodes)
                {
                    VRL vrl=node.getVRL(); 
                    
                    String hdrs[] = getHeaders();   
                    VAttribute[] attrs;
                    try
                    {
                        attrs = node.getAttributes(hdrs);
                        setValues(vrl.toString(),attrs); 
                    }
                    catch (VlException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            public void stopTask()
            {
                
            }
        };
        
        task.startTask();
    }

    protected void handle(VlException e)
    {
        Global.errorPrintStacktrace(e); 
    }

    public int insertHeader(String headerName, String newName, boolean insertBefore)
    {
        int index=super.insertHeader(headerName, newName, insertBefore); 
        // will update data model, Table View will follow AFTER TableStructureEvent 
        // has been handled. 
        fetchAttribute(newName);
        return index; 
    }

    private void fetchAttribute(String newName)
    {
        ProxyNode nodes[];
        
        StringList attrNames=new StringList(newName); 
        
        try
        {
            // should be cached: 
            nodes = pnode.getChilds(null);
        }
        catch (VlException e)
        {
            handle(e); 
            return; 
        }
        
        for (ProxyNode node:nodes)
        {
            VAttribute[] attrs;
            try
            {
                attrs = node.getAttributes(attrNames.toArray());
                setValues(node.getVRL().toString(),attrs);  
                
            }
            catch (VlException e)
            {
                handle(e); 
            } 
        }
    }
    
    
    
}
