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
 * $Id: FontInfo.java,v 1.8 2011-06-07 14:31:58 ptdeboer Exp $  
 * $Date: 2011-06-07 14:31:58 $
 */ 
// source: 

package nl.uva.vlet.gui.font;


import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JComponent;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;

/** 
 * Simple Font Information holder class. 
 *  
 * FontInfo is used by the FontToolbar.   
 * Use createFont() to instantiate a new Font object using the specified Font information.
 */
public class FontInfo
{

    //  ========================================================================
    //  Class Constants: 
    //  ========================================================================
    
    public static final String FONT_ALIAS="fontAlias";
    
    public static final String FONT_TYPE="fontType";
    
    public static final String FONT_FAMILY="fontFamily";
    
    public static final String FONT_STYLE="fontStyle";
    
    public static final String FONT_SIZE="fontSize";
    
    public static final String FONT_ANTI_ALIASING="fontAntiAliasing";
    
    public static final String fontPropertyNames[]=
        {
            FONT_ALIAS,
            FONT_FAMILY,
            FONT_STYLE,
            FONT_SIZE,
            FONT_FAMILY
        };
    
    // some default font types: 
    public static final String FONT_ICON_LABEL   = "iconlabel"; 
    public static final String FONT_MONO_SPACED  = "monospaced"; 
    public static final String FONT_DIALOG       = "dialog";
    public static final String FONT_TERMINAL     = "terminal";
    
    // enable to auto-store create fonts in ~/.vletrc/fonts/<font-alias>.prop
    
    private static boolean autosave = false;
    
    /** Font Style database */ 
    static Hashtable<String,FontInfo> fontStyles= null;  

    //  ========================================================================
    //  Info
    //  ========================================================================
    
    /** Whether to store FontInfo in persistant Font DataBase */ 
    public static void setGlobalAutoSave(boolean value)
    {
        autosave=value; 
    }

    //  ========================================================================
    //  Info
    //  ========================================================================

    /** Font Type or Font Family Name */ 
    String fontFamily="Monospaced";
    
    /** Optional Alias for the GUI (dialog,terminal,label) */ 
    String fontAlias=null; 
    
    /** Font Size */ 
    Integer fontSize=13; 
    
    /** Font Style, 0=non, 0x01=bold,0x02=italic, etc. 
     * @see Font*/
    Integer fontStyle=0; 
    
    /** Extra anti aliasing option (not in the Font class */ 
    private Boolean antiAliasing = null ; // inheret!
    
    /** Optional Foreground color, can be NULL (= use default) */ 
    Color foreground=Color.BLACK;
       
    /** Optional Background color, can be NULL (= use default) */ 
    Color background=Color.WHITE;
    
    /** Optional Highlighted foreground color, can be NULL (= use default) */ 
    Color highlightedForeground=new Color(64,64,240);

    /** For hierarchical Fonts: currently not used */ 
    FontInfo parent=null; 
 
    public FontInfo(Properties props)
    {
        this.setFontProperties(props);
        // backward compatibility: add alias name 
        if (fontAlias==null)
           fontAlias=fontFamily; 
    }

    public FontInfo()
    {
    }

    public FontInfo(Font font) 
    {
        init(font);  
    }
    
    void init(Font font)
    {
        fontSize=font.getSize(); 
        fontStyle=font.getStyle(); 
        fontFamily=font.getFamily();
        // alias default to fontName
        fontAlias=fontFamily;
    }

    /**
     * @deprecated since java 1.6 rendering hints are used which are taken 
     * from the actual Font Implementation. 
     */ 
    public Boolean getAntiAliasing()
    {   
        return getAntiAliasing(true); 
    }
    
    /**
     * @deprecated since java 1.6 rendering hints are used which are taken 
     * from the actual Font Implementation. 
     */ 
    public Boolean getAntiAliasing(boolean defaultValue)
    {   
        if (this.antiAliasing==null)
            return defaultValue;  
        
        return antiAliasing;
    }

    /**
     * @param antiAliasing The antiAliasing to set.
     */
    public void setAntiAliasing(Boolean antiAliasing)
    {
        this.antiAliasing = antiAliasing;
    }

    /**
     * @return Returns the fontSize.
     */
    public int getFontSize()
    {
        return fontSize;
    }

    /**
     * @param fontSize The fontSize to set.
     */
    public void setFontSize(int size)
    {
        //System.err.println("FontInfo.setFontSize="+size);
        this.fontSize = size;
    }

    /**
     * @return Returns the fontStyle.
     */
    public int getFontStyle()
    {
        return fontStyle;
    }

    /**
     * @param fontStyle The fontStyle to set.
     */
    public void setFontStyle(int fontStyle)
    {
        this.fontStyle = fontStyle;
    }

    /**
     * @return Returns the font family, for example "Monospaced"  or "Arial"
     *  
     */
    public String getFontFamily()
    {
        return fontFamily;
    }

    /** @deprecated use getFontFamily */ 
    public String getType()
    {
    	return fontFamily;  
    }
    
    /**
     * @param Set Font Family name. For example "Monospaced" or "Arial". 
     */
    public void setFontFamily(String family)
    {
        this.fontFamily = family;
    }

    /** @deprecated use setFontFamily */
    public void setType(String family)
    {
        this.fontFamily = family;
    }
    
    public Font createFont()
    {
        return new Font(fontFamily,fontStyle,fontSize);
    }    
    
    public boolean isBold()
    {
        return (fontStyle&Font.BOLD)==Font.BOLD; 
    }

    public boolean isItalic()
    {
        return (fontStyle&Font.ITALIC)==Font.ITALIC; 
    }

    public void setBold(boolean val)
    {
        fontStyle=setFlag(fontStyle,Font.BOLD,val); 
    }

    public void setItalic(boolean val)
    {
        fontStyle=setFlag(fontStyle,Font.ITALIC,val); 
        //System.err.println("fontStyle="+fontStyle);
    }
    
    private int setFlag(int orgvalue, int flag, boolean val)
    { 
        if (val==true) 
            orgvalue=orgvalue|flag;  
        else if ((orgvalue & flag) == flag)
            orgvalue-=flag;
        // else val=false and flag not set in the first place
        return orgvalue; 
       // System.err.println("fontstyle="+fontStyle);
    }
    
    // return font properties as property set
    public Properties getFontProperties()
    {
        Properties props=new Properties();
        
        if (fontAlias==null) 
        	fontAlias=fontFamily; 
        
        props.put(FONT_ALIAS,fontAlias); 
        props.put(FONT_FAMILY,fontFamily); 
        props.put(FONT_SIZE,new Integer(fontSize).toString()); 
        props.put(FONT_STYLE,new Integer(fontStyle).toString());
        // if defined
        if (antiAliasing!=null)
            props.put(FONT_ANTI_ALIASING,new Boolean(antiAliasing).toString());
        
        return props; 
    }
    
    /** Uses FONT properties and updates info */ 
    public void setFontProperties(Properties props)
    {
         String valstr=null; 
         
         valstr=(String)props.get(FONT_ALIAS);
         
         if (valstr!=null) 
             this.fontAlias=valstr;
         
         // Old Type name => renamed to FAMILY 
         valstr=(String)props.get(FONT_TYPE);
         
         if (valstr!=null) 
             setFontFamily(valstr); 
         
         // new Correct 'family' i.s.o. generic 'type' 
         valstr=(String)props.get(FONT_FAMILY);
         
         if (valstr!=null) 
             setFontFamily(valstr); 
         
         valstr=(String)props.get(FONT_SIZE);
         
         if (valstr!=null)
              setFontSize(Integer.valueOf(valstr));  
         
         valstr=(String)props.get(FONT_STYLE);
         
         if (valstr!=null) 
             setFontStyle(Integer.valueOf(valstr)); 
         
         valstr=(String)props.get(FONT_ANTI_ALIASING);
         
         if (valstr!=null) 
             setAntiAliasing(Boolean.valueOf(valstr));  
    }

    private void store()
    {
        fontStyles.put(this.fontAlias,this);
        
        if (autosave==true) 
        {
            try
            {
                saveFontStyles();
            }
            catch (VlException e)
            {
                System.out.println("***Error:when saving font styles:"+e); 
                e.printStackTrace();
            }    
        }
    }
    
    
    /** For selected text/icon label text */ 
    
    public Color getHighlightedForeground()
    {
        return this.highlightedForeground; 
    }

    public Color getBackground()
    {
        return this.background; 
    }
    
    public Color getForeground()
    {
        return this.foreground; 
    }   
    
    // ==============================================
    // Static FontInfo Factory 
    // ==============================================
    
    /** Return Aliased Font Style */ 
    
    public static FontInfo getFontInfo(String name)
    {
        // autoinit 
        
        if (fontStyles==null) 
        {
            try
            {
                loadFontStyles();
            }
            catch (VlException e)
            {
                System.out.println("***Error: Exception:"+e); 
                e.printStackTrace();
            }
            
            if (fontStyles==null) 
                fontStyles=new Hashtable<String,FontInfo>();
        }
        
        FontInfo info=fontStyles.get(name);
        
        if (info!=null) 
            return info; 
        
        
        // current hardcoded ones: 
        if (name.compareToIgnoreCase(FONT_ICON_LABEL)==0)
        {
                Font font=new Font("dialog",0,11);
                return store(font,FONT_ICON_LABEL);
                 
        }
        else if (name.compareToIgnoreCase(FONT_DIALOG)==0)
        {
                Font font=new Font("dialog",0,11);
                return store(font,FONT_DIALOG);
                 
        }
        else if (name.compareToIgnoreCase(FONT_MONO_SPACED)==0)
        {
                Font font=new Font("monospaced",0,12);
                return store(font,FONT_MONO_SPACED); 
        }
        else if (name.compareToIgnoreCase(FONT_TERMINAL)==0)
        {
                Font font=new Font("monospaced",0,12);
                return store(font,FONT_MONO_SPACED); 
        }
        return null; 
    }
    
    // Store FontInfo under (new) alias 
    private static FontInfo store(Font font, String alias)
    {
        FontInfo info=new FontInfo(font); 
        info.fontAlias=alias; 
        info.store();
        
        return info;
    }

    /** Store FontInfo */ 
    public static void store(FontInfo info) 
    {
        info.store();  
    }

    private String getAlias()
    {
        return this.fontAlias; 
    }

    /** Update font settings of specified Component with this font */ 
    public void updateComponentFont(JComponent jcomp)
    {
        GuiSettings.setAntiAliasing(jcomp,getAntiAliasing()); 
        jcomp.setFont(createFont());
    }

    private void saveFontStyles() throws VlException
    {
        VRL loc=getUserFontSettingsLocation();
        
        //String filepath=loc.getPath();
        
        for (Enumeration<String> keys =fontStyles.keys();keys.hasMoreElements();) 
        {
            String name=(String)keys.nextElement();
            
            FontInfo info=fontStyles.get(name); 
            
            //Properties fontProps=fontStyles.get(name)filepath
            Properties fontProps=info.getFontProperties(); 
            
            VRL fontLoc=loc.appendPath(name+".prop");
            
            Global.errorPrintf(this,"Saving font:'%s' to file:%s\n",name,fontLoc);
            
            UIGlobal.saveProperties(fontLoc,fontProps);
        }
    }
    
    private static void loadFontStyles() throws VlException
    {
        VRL systemLoc=getSystemFontSettingsLocation();
        loadFontStylesFrom(systemLoc); 
        
        VRL userLoc=getUserFontSettingsLocation();
        loadFontStylesFrom(userLoc);        
    }
    
    private static void loadFontStylesFrom(VRL fontLoc) throws VlException
    {
        if (fontStyles==null) 
            fontStyles=new Hashtable<String,FontInfo>();

        Global.debugPrintf(FontInfo.class,"Loading fonts from:%s\n",fontLoc); 
        VFSClient vfs=new VFSClient();
        
        if (vfs.existsDir(fontLoc)==false)
        {
            // auto create location 
            // vfs.mkdir(fontLoc);
            return; // no fonts to load; 
        }
        
        VDir dir=vfs.getDir(fontLoc); 
        VFSNode nodes[] = dir.list(); 
        
        // scan font directory
        for (VFSNode node:nodes)
        {
            //String name=node.getLocation().getBasename(false); // strip extension;
            
            // load font properties. 
            Properties fontProps=UIGlobal.loadProperties(node.getLocation()); 
            
            FontInfo info=new FontInfo(fontProps);
            fontStyles.put(info.getAlias(),info); 
        }
    }

    private static VRL getUserFontSettingsLocation()
    {
        VRL loc=Global.getUserConfigDir(); 
        
        loc=loc.appendPath("fonts");
        return loc;
    }
    
    private static VRL getSystemFontSettingsLocation()
    {
        VRL loc=Global.getInstallationConfigDir(); 
        
        loc=loc.appendPath("fonts");
        return loc;
    }
   
}
