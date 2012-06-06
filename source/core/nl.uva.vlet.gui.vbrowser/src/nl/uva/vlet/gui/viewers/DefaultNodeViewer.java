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
 * $Id: DefaultNodeViewer.java,v 1.5 2011-06-10 10:18:00 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:00 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.panels.attribute.AttributePanel;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;

import nl.uva.vlet.vrl.VRL;


public class DefaultNodeViewer extends ViewerPlugin implements VComponent
{
    public class ActionListener implements MouseListener //,MouseWheelListener, FocusListener
    {
        public void mouseClicked(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {} 
        public void mouseExited(MouseEvent e) {}

        // Canvas Click: 
        public void mousePressed(MouseEvent e)
        {
            // DefaultNodeViewer.this.requestFocus();
            // boolean combine=((e.getModifiersEx()&MouseEvent.CTRL_DOWN_MASK)!=0);
            
            if (GuiSettings.isPopupTrigger(e))
            {
                MasterBrowser mb = getViewContext().getMasterBrowser();
                if (mb!=null)
                {
                    JPopupMenu menu=mb.getActionMenuFor(DefaultNodeViewer.this);
                    menu.show((Component)e.getSource(),e.getX(),e.getY());
                }
            }
        }

    }
    
    /** Needed by Swing */
    private static final long serialVersionUID = -2866218889160789305L;

    /** The mimetypes i can view */
    private static String mimeTypes[]=
        {
        "application/octet-stream",
        }; 

    private AttributePanel aPanel=null;

    private ProxyNode pnode=null; // use ProxyNode for cached attributes ! 

    public void initGui()
    {
        {
            // JPanel has aldready a layout
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout(thisLayout);
            //this.setPreferredSize(new java.awt.Dimension(400, 400));
        }
        {
            aPanel = new AttributePanel((VAttributeSet)null); // !EMPTY window 
            this.add(aPanel, BorderLayout.CENTER);
            aPanel.setPreferredSize(new java.awt.Dimension(534, 186));
        }
        
        // Listeners; 
        this.addMouseListener(new ActionListener()); 
    }
    
    public void initViewer() 
    {
        initGui(); //initViewer is called during GUI event thread
    }
    
        
    public void updateLocation(VRL location)
    {
        setBusy(true);
        
        ProxyNodeFactory factory=ProxyNode.getProxyNodeFactory();
        
        if (location == null)
        {
            return;
        }

        // this.locationTextField.setText(location.toString());

        try
        {
            this.pnode = factory.openLocation(location,true);

            final VAttributeSet set = pnode.getAttributeSet();
            aPanel.setAttributes(set);
            
            
            // update size from attribute panel ! 
            // this.setPreferredSize(aPanel.getPreferredSize());
            
            // update Swing during the GUI event thread ! 
            this.requestFrameResize(aPanel.getPreferredSize()); 
        }
        catch (VlException e)
        {
            handle(e); 
            
        }
        finally
        {
            setBusy(false);
        }
    }

    public static void main(String args[])
    {
        try
        {
            viewStandAlone(new VRL("file:///etc/passwd"));
            viewStandAlone(null);
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

    public static void viewStandAlone(VRL loc) throws VlException
    {
        DefaultNodeViewer nv=new DefaultNodeViewer();
        nv.startAsStandAloneApplication(loc); 
    }

    @Override
    public String[] getMimeTypes()
    {
        return mimeTypes;
    }


    @Override
    public void stopViewer()
    {
    }

    @Override
    public void disposeViewer()
    {
    }
    
    @Override
    public String getName()
    {
        return "PropertyViewer";
    }
	/**
	* This method should return an instance of this class which does 
	* NOT initialize it's GUI elements. This method is ONLY required by
	* Jigloo if the superclass of this class is abstract or non-public. It 
	* is not needed in any other situation.
	 */
	public static Object getGUIBuilderInstance() {
		return new DefaultNodeViewer(Boolean.FALSE);
	}
	
	/**
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public DefaultNodeViewer(Boolean initGUI) {
		super();
	}

    public DefaultNodeViewer()
    {
        
    }

    @Override
    public MasterBrowser getMasterBrowser()
    {
        return this.getViewContext().getMasterBrowser(); 
    }

    @Override
    public ResourceRef getResourceRef()
    {
        return pnode.getResourceRef();  
    }

    @Override
    public VContainer getVContainer()
    {
        return null;
    }
}
