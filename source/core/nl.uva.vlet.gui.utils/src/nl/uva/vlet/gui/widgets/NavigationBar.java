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
 * $Id: NavigationBar.java,v 1.9 2011-04-18 12:27:13 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:13 $
 */ 
// source: 

package nl.uva.vlet.gui.widgets;

import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vrl.VRL;

public class NavigationBar extends JToolBar 
{
    private static final long serialVersionUID = -7147394442677763506L;
    
    public static final int LOCATION_ONLY=1; 
    public static final int LOCATION_AND_NAVIGATION=2; 
    
    public static enum NavigationAction
    {
        BROWSE_BACK,
        BROWSE_UP,
        BROWSE_FORWARD,
        REFRESH,
        LOCATION_EDITED,
        LOCATION_CHANGED;
        
//        public boolean has(String str)
//        {
//            for (NavigationAction value:values())
//            {
//                if (StringUtil.equals(value.toString(),str))
//                    return true; 
//            }
//            
//            return false; 
//        }
        
        public static NavigationAction valueOfOrNull(String str)
        {
            for (NavigationAction value:values())
            {
                if (StringUtil.equals(value.toString(),str))
                    return value;  
            }
            
            return null; 
        }
        
//        public static NavigationAction valueOf(String str)
//        {
//            return valueOfOrNull(str); 
//        }
    }
    
    
    // ===
    //
    // ===
    
    private JLabel locationLabel;
    
    //private Container locationToolBar;
    
    private IconTextField locationTextField;

    //private Vector<NavigationBarListener> listeners=new Vector<NavigationBarListener>();

    private JButton browseForward;

    private JButton browseUp;

    private JButton refreshButton;

    private JButton browseBack; 
    
    private int barType=LOCATION_AND_NAVIGATION; 
    
    public NavigationBar()
    {
    	super(HORIZONTAL);
    	init(); 
    }
    
    public NavigationBar(int type)
    {
    	super(HORIZONTAL); 
    	this.barType=type; 
    	init(); 
    }

    private void init()
    {
        initGui();
        this.setEnableNagivationButtons(false); 
    }
  
    /** Add listener to text field only */ 
    public void addTextFieldListener(ActionListener listener)
    {
        this.locationTextField.setTextActionListener(listener);
    }
    
    /**
     * Add listener for navigation button if Enabled. 
     * If navigation buttons are not enabled before calling this method
     * the refresh button will only be added to the refresh button.
     */ 
    public void addNavigationButtonsListener(ActionListener listener)
    {
        if (refreshButton!=null)
            refreshButton.addActionListener(listener);
        
    	if (this.getShowNavigationButtons()==false)
    		return ; // throw new Error("Navigation Buttons not created");
    	
        browseUp.addActionListener(listener);
        browseForward.addActionListener(listener);
        browseBack.addActionListener(listener);
    }
    
    public void initGui()
    {
        JToolBar locationToolBar=this;
        JToolBar navigationToolBar=this;
        
        locationToolBar.setLayout(new BoxLayout(this,BoxLayout.X_AXIS)); 
        
        // ==================
        // Navigation Buttons
        // ==================
        if (this.getShowNavigationButtons())
        {
	        {
	            browseBack = new JButton();
	            navigationToolBar.add(browseBack);
	            browseBack.setIcon(loadIcon("menu/back.gif"));
	            browseBack.setActionCommand(NavigationAction.BROWSE_BACK.toString());
	           
	        }
	        {
	            browseForward = new JButton();
	            navigationToolBar.add(browseForward);
	            browseForward.setIcon(loadIcon("menu/forward.gif"));
	            browseForward.setActionCommand(NavigationAction.BROWSE_FORWARD.toString());
	          
	        }
	        {
	            browseUp = new JButton();
	            navigationToolBar.add(browseUp);
	            browseUp.setIcon(loadIcon("menu/up.gif"));
	            browseUp.setActionCommand(NavigationAction.BROWSE_UP.toString());
	        }
        }	
        // Refresh 
        {
        	refreshButton = new JButton();
        	navigationToolBar.add(refreshButton);
        	refreshButton.setIcon(loadIcon("menu/refresh.gif"));
        	refreshButton.setActionCommand(NavigationAction.REFRESH.toString());
        }
        
        // ========
        // Location
        // ========
        {
            locationLabel=new JLabel("Location:"); 
            locationToolBar.add(locationLabel);         

        }
        {
            locationTextField = new IconTextField(); 
            locationToolBar.add(locationTextField);
            locationTextField.setText("...");
            locationTextField.setComboActionCommand(NavigationAction.LOCATION_EDITED.toString());
            locationTextField.setComboEditedCommand(NavigationAction.LOCATION_CHANGED.toString());
              // set Preferred Width for the GTK/Window LAF! 
            locationTextField.setMinimumSize(new java.awt.Dimension(300,28));
        }
        
    }
    
 

    public void updateLocation(VRL loc,boolean addToHistory)
    {
        this.locationTextField.setText(loc.toString());
    }

    /** Enabled/disabled the navigation buttons.
     * Disabled buttons appeares grey */ 
    public void setEnableNagivationButtons(boolean enable)
    {
    	if (this.getShowNavigationButtons()==false)
    		return; 
    	
        this.browseBack.setEnabled(enable); 
        this.browseForward.setEnabled(enable); 
        this.browseUp.setEnabled(enable); 
    }
    
    public boolean getShowNavigationButtons()
    {
    	if (this.barType==NavigationBar.LOCATION_ONLY)
    		return false;
    	
    	if (this.barType==NavigationBar.LOCATION_AND_NAVIGATION)
    		return true;
    	
    	return false; 
    	
    }
    public void setLocationText(String txt)
    {
        this.locationTextField.setText(txt);
    }

    public static NavigationAction getNavigationCommand(String cmdStr)
    {
        return NavigationAction.valueOfOrNull(cmdStr);
    }

    public String getLocationText()
    {
         return locationTextField.getText(); 
    }
    
    public void setIcon(Icon icon)
    {
    	this.locationTextField.setIcon(icon); 
    }
  
    public Icon loadIcon(String str)
    {
    	return new ImageIcon(getClass().getClassLoader().getResource(str)); 
    }

    /** Parse ActionCommand or return null */ 
    public static NavigationAction parseActionCommand(String cmdstr)
    {
        return NavigationAction.valueOfOrNull(cmdstr); 
    }
    
    public IconTextField getTextField()
    {
        return this.locationTextField; 
    }
}
