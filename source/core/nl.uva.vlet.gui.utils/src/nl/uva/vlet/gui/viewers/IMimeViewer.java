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
 * $Id: IMimeViewer.java,v 1.4 2011-05-09 14:25:00 ptdeboer Exp $  
 * $Date: 2011-05-09 14:25:00 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import java.awt.Component;

import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/**
 * Interface extraction from the ViewerPanel class to allow
 * for more Viewer Plugins.  
 */
public interface IMimeViewer
{
    public String getName(); 
    
    public String[] getMimeTypes();
       
    public abstract void initViewer(); //throws VlException;

    public void setVRL(VRL location);  
    
    public abstract void startViewer(VRL location, String optionalMethodName, ActionContext actionContext) throws VlException;
    
    public abstract void updateLocation(VRL loc) throws VlException;
    
    public abstract void stopViewer();
    
    public abstract void disposeViewer();

    public ViewContext getViewContext();

    public void setViewContext(ViewContext viewContext);

    public boolean haveOwnScrollPane();
    
    /** 
     * Invoke dynamic menu action. 
     *
     * @param optionalMethodName
     * @param actionContext
     */
    public void doMethod(String optionalMethodName, ActionContext actionContext) throws VlException;

    /**
     * Return Swing/AWT Component of this viewer which will be embedded 
     * in a VBrowser Panel.   
     * If haveOwnScrollPane() returns false this component will
     * be embedded in a JScrollPane(). 
     *  
     */ 
    public Component getViewComponent();
    
    
}
