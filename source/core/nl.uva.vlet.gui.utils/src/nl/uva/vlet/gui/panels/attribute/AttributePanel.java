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
 * $Id: AttributePanel.java,v 1.2 2011-04-18 12:27:13 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:13 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.attribute;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.font.FontUtil;
import nl.uva.vlet.gui.panels.fields.AttrEnumField;
import nl.uva.vlet.gui.panels.fields.AttrIntField;
import nl.uva.vlet.gui.panels.fields.AttrParameterField;
import nl.uva.vlet.gui.panels.fields.AttrPortField;

import com.cloudgarden.layout.AnchorLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Attribute Panel. 
 */
public class AttributePanel extends JPanel
{
    // Needed by swing JComponent  
    private static final long serialVersionUID = 6889725168037866258L;
    private static final int default_row_gap = 4; 
    private static final int default_column_gap = 8;
    private static final int default_border_width = 5;
    private static final int default_border_height = 5;

    private static final int default_min_field_width=100;
    private static final int default_max_field_width=300;

    /**
     * Attribute Set. Operation on this set 
     * should reflect Panel and vice versa ! 
     */
    protected VAttributeSet attributes=null;
   
    private boolean isEditable=false;
    private boolean hasChangedAttributes=false; 
    private int maxFieldWidth;
    private JComponent[] jFields;
    private JLabel[] jLabels;
    private boolean useFormLayout=true;
    private int rowOffset;
    private FormLayout formLayout; 

    public void initGui()
    {
        // again the GridBagLayout doesn't work nicely ! 
        //{
        // GridBagLayout thisLayout = new GridBagLayout();
        // this.setLayout(thisLayout);
        // }

        //this.setLayout(null); // no layout for now 

        if (useFormLayout)
        {
            // experimental FormLayout (jgoodies.com)
            formLayout = new FormLayout(
                    "10px,right:pref:grow, 5px, fill:pref:grow,10px",
                    "10px,center:pref:grow, max(p;5px),max(p;5px), max(p;5px), center:pref:grow,10px");

            this.setLayout(formLayout);
        }
        else
        {
            this.setLayout(new AnchorLayout());    
        }

        // this.setPreferredSize(new java.awt.Dimension(400,600));
        // actual constructing is done in setAttributes
    }

    /**
     * Default constructor fo jigloo! 
     * Do not use this one, as default Attributes are created ! 
     * */  
    public AttributePanel()
    {
        //super();
        VAttribute attrs[]=new VAttribute[4];
        String options[]={"Option1","Option2"};
        
        attrs[0]=new VAttribute("textAttribute", "text"); 
        attrs[1]=new VAttribute("booleanAttribute", true); 
        attrs[2]=new VAttribute("numberAttribute", 1);	
        attrs[3]=new VAttribute("selectAttribute",options,0); 
        initGui(); 

        setAttributes(new VAttributeSet(attrs));   
    }

    private void init(VAttributeSet set,boolean editable)
    {
        this.isEditable=editable; 
        initGui();

        if (set==null)
            // allowed: means creat empty window, attribute will follow ! 
            return; 

        setAttributes(set,editable);  
    }

    public AttributePanel(VAttribute attrs[])
    {
        super();
        init(new VAttributeSet(attrs),isEditable); 
    }

    public AttributePanel(VAttribute attrs[],boolean isEditable)
    {
        super();
        init(new VAttributeSet(attrs),isEditable); 
    }

    public AttributePanel(VAttributeSet set,boolean isEditable)
    {
        super();
        init(set,isEditable); 
    }

    public AttributePanel(VAttributeSet set)
	{
    	super(); 
		init(set,true); 
	}

	/** 
     * Creates labels & fields.
     * Robuustness note:  Attributes in the attrs[] array may 
     * be null. They will not be shown:
     * @param editable 
     * @param attrs
     */
    public void setAttributes(VAttributeSet orgSet)
    {
        setAttributes(orgSet,this.isEditable); 
    }

    public void setAttributes(VAttributeSet orgSet, boolean editable)
    {
        this.isEditable=editable; 

        removeAll();
        
        if (orgSet==null)
        {
        	validate(); 
        	repaint(); 
        	return; 
        }
        
        int len=0;  
        len=orgSet.size(); 
        // else this method will remove old attributes 
        
        // new ORDENED Set: 
        attributes=new VAttributeSet(); 
      
        //GridBagConstraints c = new GridBagConstraints();

        // first make left column of fields, 
        // next make right column of values

        // do the layout myself: 

        int xpos=default_border_width; 
        int ypos=default_border_height;

        int ypositions[]=new int[len]; 
        int maxx=0; 
        int maxy=0; 

        jLabels=new JLabel[len]; 

        for (int i=0;i<len;i++)
        { 
            // filter out null Attributes ! 
            if (orgSet.elementAt(i)!=null)
            {
                /* if ( (editable==false) 
                    ||(editable & orgAttrs[i].isEditable()) )*/
            	VAttribute attr = orgSet.elementAt(i);
                attributes.put(attr);                
            }
        }

        // 
        // new attribute size 
        // 

        len=attributes.size();

        //changed.setSize(len); 
        // update form layout columns nr: 

        setFormRows(attributes.size());

        //
        // Field Names
        //

        for (int i=0;i<len;i++)
        { 
        	VAttribute attr=attributes.elementAt(i); 
        	debug("Adding attribute:"+attr); 
            
            String name=attr.getName(); 
            String type=attr.getType().toString();  

            JLabel label=new JLabel(name);
            label.setToolTipText("type:"+type+",name:"+name);

            ypositions[i]=ypos;
            
            if (useFormLayout)
            {
                if (VAttribute.isSectionName(name)) 
                    add(label, new CellConstraints(
                        "2,"+(i+rowOffset)+",1, 1, default, default"));
                else
                    add(label, new CellConstraints(
                            "2,"+(i+rowOffset)+",1, 1, default, default"));
            }
            else
            {
                label.setLocation(xpos,ypos);

                // since I am doing the layout I have to set my childrens size also ! 
                label.setSize(label.getPreferredSize()); 
                this.add(label);
            }

            ypos+=label.getSize().getHeight()+default_row_gap;

            if ((xpos+label.getSize().width)>maxx)
                maxx=xpos+label.getSize().width;

            if ((ypos+label.getSize().height)>maxy)
                maxy=ypos+label.getSize().height; 

        }

        xpos=maxx+default_column_gap;

        ypos=10;

        // === Create Fields === 
        jFields=new JComponent[len]; 

        for (int i=0;i<len;i++)
        {
            VAttribute attr=attributes.elementAt(i);
            String value=attr.getStringValue();  
            String name=attr.getName(); 
            VAttributeType type = attr.getType(); 
            boolean isSection= VAttribute.isSectionName(name); 
            
            if (isSection)
                continue; 
            
            JComponent jField=null; 

            // create field listener for this attribute field 
            AttributeFieldListener attributeListener = new AttributeFieldListener(name);
            // only create combo box for editable attributes:

            if ( (attr.isEditable()) 
                    && (this.isEditable)
                    && ( (type==VAttributeType.ENUM)
                            ||(type==VAttributeType.BOOLEAN)
                    )
            )
            {
                String vals[]=attr.getEnumValues(); 

                if (vals==null)
                {
                    Global.errorPrintln(this,"Error no enumvalues for attribute:"+attr.getName()); 
                    vals=new String[1]; 
                    vals[0]="<NULL>";
                }
                
                AttrEnumField enumField = new AttrEnumField();
                enumField.setValues(vals);
                
                enumField.setSelectedIndex(attr.getEnumIndex());

                jField=enumField; 

                enumField.setFont(FontUtil.createFont("dialog")); 
                enumField.setEditable(this.isEditable && attr.isEditable());
                enumField.setActionCommand(name);
                enumField.addActionListener(attributeListener);
                enumField.addMouseListener(attributeListener);
            }
            else if ( (attr.isEditable()) 
                    && (this.isEditable)
                    && (StringUtil.equals(name,"port") 
                    )
            )
            {
            	 JTextField textField=new AttrPortField(name,value);
                 textField.addActionListener(attributeListener);
                 textField.addMouseListener(attributeListener);
                 textField.setActionCommand(name);
                 textField.setEditable(this.isEditable && attr.isEditable()); 

                 if (textField.isEditable())
                 {
                     textField.setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                     textField.setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);

                 }
                 else
                 {
                     textField.setBackground(UIGlobal.getGuiSettings().textfield_non_editable_background_color);
                     textField.setForeground(UIGlobal.getGuiSettings().textfield_non_editable_foreground_color);
                 }


                 jField=textField;
            }

            else if ( (attr.isEditable()) 
                    && (this.isEditable)
                    && ( (type==VAttributeType.INT)
                            ||(type==VAttributeType.LONG)
                    )
            )
            {
            	 JTextField textField=new AttrIntField(value, attr.getIntValue());
                 textField.addActionListener(attributeListener);
                 textField.addMouseListener(attributeListener);
                 textField.setActionCommand(name);
                 textField.setEditable(this.isEditable && attr.isEditable()); 

                 if (textField.isEditable())
                 {
                     textField.setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                     textField.setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);

                 }
                 else
                 {
                     textField.setBackground(UIGlobal.getGuiSettings().textfield_non_editable_background_color);
                     textField.setForeground(UIGlobal.getGuiSettings().textfield_non_editable_foreground_color);
                 }


                 jField=textField;
            }
            else
            {
                JTextField textField=new AttrParameterField(value);
                textField.addActionListener(attributeListener);
                textField.addMouseListener(attributeListener);
                textField.setActionCommand(name);
                textField.setEditable(this.isEditable && attr.isEditable()); 

                if (textField.isEditable())
                {
                    textField.setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                    textField.setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);

                }
                else
                {
                    textField.setBackground(UIGlobal.getGuiSettings().textfield_non_editable_background_color);
                    textField.setForeground(UIGlobal.getGuiSettings().textfield_non_editable_foreground_color);
                }


                jField=textField;
            }
            //
            // Set optional tooltiptest if attribute specifies 'mini' help text ! 
            //
            String text=attr.getHelpText(); 
            if (text!=null)
            	jField.setToolTipText(text);
          
            
            jField.addFocusListener(attributeListener);
            ypos=ypositions[i]; 
            //jField.setBackground(GuiSettings.current.textfield_non_editable_background_color);
            // since I am doing the layout I have to set my childrens size also ! 


            if (useFormLayout)
            {
                add(jField, new CellConstraints(
                        "4,"+(i+rowOffset)+",1, 1, default, default"));
            }
            else
            {
                jField.setLocation(xpos,ypos);
                jField.setSize(jField.getPreferredSize());
                // since I am doing the layout I have to set my childrens size also ! 
                jField.setSize(jField.getPreferredSize()); 
                this.add(jField);
            }

            if (jField.getSize().width>maxFieldWidth)
                maxFieldWidth=jField.getSize().width;

            jFields[i]=jField; 

            if ((xpos+jField.getSize().width)>maxx)
                maxx=xpos+jField.getSize().width;

            if ((ypos+jField.getSize().height)>maxy)
                maxy=ypos+jField.getSize().height; 
        }

        //if (maxFieldWidth>this.default_max_field_width)
        //    maxFieldWidth=default_max_field_width; 

        // update fields width to maximum field width
        if (useFormLayout==false) 
            for(int i=0;i<len;i++)
            {
                // null fields due to null attributes 
                Dimension size=jFields[i].getSize();

                if (size.width<default_min_field_width)
                {
                    size.width=default_min_field_width;
                }

                if ((jFields[i].getLocation().x+size.width)>maxx)
                    maxx=jFields[i].getLocation().x+size.width;


                //if (size.width>default_max_field_width)
                //    size.width=default_max_field_width; 

                jFields[i].setSize(size);

            }

        Global.debugPrintln("AttributePanel","maxx,maxy="+maxx+","+maxy);

        // add border size 

        maxx+=default_border_width; 
        maxy+=default_border_height;  

        if (maxx>=800)
        	maxx=800; 
        
        if (useFormLayout)
        {
            this.doLayout();
            Dimension prefSize = this.getPreferredSize();
            
            debug("Pre validate: preferred LayoutSize="+this.getLayout().preferredLayoutSize(this));
            debug("Pre validate: preferred size="+getPreferredSize());
            
            this.validate(); 

            debug("Post validate: preferred Panel LayoutSize="+this.getLayout().preferredLayoutSize(this));
            debug("Post validate: preferred Panel size="+getPreferredSize());

            this.setPreferredSize(this.getLayout().preferredLayoutSize(this));
            //if (prefSize.width>800)
           // 	prefSize.width=800;
            
            //this.setSize(prefSize);
            //this.setMaximumSize(prefSize); 
        }
        else
        {
            //this.setSize(maxx,maxy);
            //this.setPreferredSize(getSize());
        }
        // for some reason the FormLayout doesn't update the JPanel's preferredsize: 

        debug("preferred size="+getPreferredSize());  
        debug("preferred LayoutSize="+this.getLayout().preferredLayoutSize(this));  

      // Validate Now
        this.validate();
        //repaint later!: 
        this.repaint(); 
    }


    //@Override 
    /*public Dimension getPreferredSize() 
    {
        // for some reason the FormLayout doesn't update the JPanel's preferredsize:
        return this.getLayout().preferredLayoutSize(this); 
    }*/

    private void setFormRows(int rows)
    {
        // let upper/lower borders grow:
        String defStr="8px,center:pref:grow,";

        rowOffset=3;// first 2 rows are fillers, so 3rd is the first!    

        // nr of rows:
        for (int i=0; i<rows+1;i++)
        {
        	// rows have a minimal 8px in heights
        	defStr+="max(p;8px),";
            //defStr+="100px,";
        }

        // let upper/lower borders grow, but must have minimal 8 pixels
        defStr+="center:pref:grow,8px";

        if (useFormLayout)
        {
            // 4 columns: 2 border and 2 'middle'

            FormLayout formLayout = new FormLayout(
            		//"10px,right:pref:none, 5px, fill:pref:grow,10px",
                    "10px,right:pref:none, 5px, fill:min(800px;p):grow,10px",
                    defStr); 

            this.setLayout(formLayout);
        }
        else
        {
            this.setLayout(new AnchorLayout());    
        }
    }

    /** 
     * Enables all editable attributes (VAttribute.isEditable() still 
     * must return TRUE 
     * @param val
     */
    /*public void setEditable(boolean val)
    {
        this.isEditable=val;

        if (jFields==null)
            return; // not initialized yet

        for (int i=0;i<jFields.length;i++)
        {
            if ((jFields[i]!=null) && (attributes.elementAt(i).isEditable()))
            {
                if (jFields[i] instanceof JTextField)
                {
                    ((JTextField)jFields[i]).setEditable(val);
                }
                jFields[i].setBackground(GuiSettings.current.textfield_editable_background_color);
            }
        }
    }*/

    public class AttributeFieldListener implements ActionListener,FocusListener, MouseListener
    {
        // the attribute name I am listener for: 
        String attributeName=null; 

        public AttributeFieldListener(String attrname)
        {
            attributeName=attrname;    
        }

        public void actionPerformed(ActionEvent e)
        {
            updateComp(e.getSource());  
        }

        public void focusGained(FocusEvent e)
        {
            Global.debugPrintln("FieldListener","focusGained:"+e);
        }

        public void focusLost(FocusEvent e)
        {
        	updateComp(e.getSource());
        }
     
		private void updateComp(Object comp)
        {
            Global.debugPrintln(this,"updateComp:"+comp);
            
            String value=null; 

            if (comp instanceof JTextField)
            {
                JTextField field=(JTextField)comp;
                value=field.getText();
            }
            else if (comp instanceof JComboBox)
            {
                Object val=((JComboBox)comp).getSelectedItem();
                value=(String)val;
            }
            else
            {
                Global.errorPrintln(this,"focusLost:unknown component"+comp);
                return;
            }
            
            // ignore event on non editable attributes 
            if (isEditable(attributeName)) 	
            	setAttribute(attributeName,value);
        }

		public void mouseClicked(MouseEvent e)
		{
			JComponent source=(JComponent)e.getSource(); 
			
			if (e.getClickCount()==2)
			{
				VAttribute attr = getAttribute(this.attributeName); 
				VAttribute newattr=AttributeViewer.editAttribute(attr,attr.isEditable());
			
				Global.debugPrintln(this,"New attribute="+newattr);
				
				if ((newattr!=null) && (source instanceof JTextField))
				{
					String valtxt=newattr.getStringValue(); 
					((JTextField)source).setText(valtxt);
					setAttribute(attributeName,valtxt);
					
				}
				
				/*
				if (newattr!=null)
					setAttribute(newattr.getName(),newattr.getValue());
			 	*/
			}
			
		}

		public void mouseEntered(MouseEvent e)
		{
			// highlight ? ((JComponent)e.getSource()).requestFocusInWindow();
			
		}

		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}
    }
     
    public boolean isEditable(String name)  
    {
        if (name==null)
            return false;   

        if (attributes.containsKey(name))
        	return attributes.get(name).isEditable();
        
        return false;
    }
    
    /** Update Attribute in AttributeSet */ 
    public void setAttribute(String name, String value) 
    {
        Global.debugPrintf(this,"setAttribute %s=%s\n",name,value);

        if (name==null)
        {
            Global.warnPrintf(this,"setAttribute: NULL name!\n"); 
            return;  
        }
        
        if (attributes.containsKey(name))
        {
        	String oldValue=null; 
        	
        	// check for new value: 
        	oldValue = attributes.getStringValue(name); 
				
        	if ((oldValue!=null) && (oldValue.compareTo(value)!=0)) 
        	{
        	 	attributes.set(name,value);
        		// really a new value !
        		// filter out focus and other event which do not 
        		// change the value 
        		this.hasChangedAttributes=true;
        		notifyAttributeChangeEvent(name);
            }
        }
        else
        {
            debug("Attribute not found:"+name); 
        }
       
    }

    private void notifyAttributeChangeEvent(String name)
	{
    	for (AttributePanelListener listener:listeners)
    	{
    		VAttribute attr = attributes.get(name); 
    		listener.notifyAttributeChanged(attr); 
    	}
	}

	private void debug(String msg)
	{
	    //Global.errorPrintln(this,msg); 
		Global.debugPrintln(this,msg); 
	}

	public boolean hasChangedAttributes()
    {
        return this.hasChangedAttributes; 
    }

    /**
     *  return updated attributes.
     *  null Attribute are filtered out ! 
     */
    public VAttribute[] getAttributes()
    {
        if (attributes==null)
            return null; 

        return attributes.toArray(); 
    }

    public VAttribute getAttribute(String name)
    {
        if (attributes==null)
            return null; 

        return attributes.get(name);  
    }

    /** Modal show editor Panel */  
    public static VAttribute[] showEditor(VAttributeSet attrs)
    {
        JDialog dialog=new JDialog(); 
        VAttribute newAttrs[]=null; 

        AttributePanel panel=new AttributePanel(attrs);
        // JFrame frame=new JFrame();
        // frame.add(panel);
        // panel.setAttributes(attrs); 
        // frame.pack(); 
        // GuiSettings.setToOptimalWindowSize(frame); 
        // frame.setVisible(true);

        dialog.add(panel);
        dialog.setModal(true); 
        dialog.setVisible(true); 
        newAttrs=panel.getAttributes();  

        return newAttrs; 
    }

    /**
     * Method to call when attributes are updated outside
     * the main GUI event thread.
     * Uses SwingUtilities.invokeLater() to update the specified attributes.  
     * 
     * @param attrs
     */
    public void asyncSetAttributes(final VAttributeSet set)
    {
        final AttributePanel apanel=this;  

        Runnable runT=new Runnable()
        {
            public void run()
            {
                apanel.setAttributes(set); 
            }
        };

        SwingUtilities.invokeLater(runT); 

    }

    public VAttribute[] getChangedAttributes()
    {
        return this.attributes.getChangedAttributesArray(); 
    }

	public static void showEditor(VAttribute attrs[])
	{
		showEditor(new VAttributeSet(attrs)); 
	}
	
	Vector<AttributePanelListener>listeners=new Vector<AttributePanelListener>();
	
	public void addAttributeListener(AttributePanelListener listener)
	{
		synchronized(listeners)
		{
			listeners.add(listener);
		}
	}
}
