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
 * $Id: ResourceMappingPatterns.java,v 1.3 2011-04-18 12:00:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:27 $
 */
// source: 

package nl.uva.vlet.actions;

import static nl.uva.vlet.actions.ActionMenuConstants.SELECTION_DONT_CARE;
import static nl.uva.vlet.actions.ActionMenuConstants.SELECTION_NONE;
import static nl.uva.vlet.actions.ActionMenuConstants.SELECTION_ONE;
import static nl.uva.vlet.actions.ActionMenuConstants.SELECTION_ONE_OR_MORE;
import static nl.uva.vlet.actions.ActionMenuConstants.SELECTION_TWO;
import static nl.uva.vlet.actions.ActionMenuConstants.SELECTION_TWO_OR_MORE;
import static nl.uva.vlet.actions.ActionMenuConstants.SELECTION_TYPE_CLIPBOARD;
import static nl.uva.vlet.actions.ActionMenuConstants.SELECTION_TYPE_DRAG_AND_DROP;

import java.util.regex.Pattern;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vrl.VRL;

/**
 * ResourceMappingPatterns.<br>
 * <strong> This Class is package protected !</strong><br>
 * The Implementation will definitaly change in the future. Use
 * ActionMenuMapping object to specify (and add) action mappings. That interface
 * will remain stable.
 * <p>
 * Matches Resource Type (+optional Scheme) with selected ressourceType
 * (+optional) resourceScheme. Resource(Type) is the resource which has the
 * 'Focus', that is the resource which has been right clicked (or alt mouse
 * button clicked) on. <br>
 * It May or May not be selected itself!
 * <p>
 * The Selected Resources are the resources either in the clipboard
 * (copy,cut,CTRL-C,CTRL-V) or explicit selected holding CTRL down in the gui
 * or, in the case of an Drag and Drop, the resources being dragged onto the
 * node matching 'resourceType' . The SelectionMode determines whether the
 * Action is for one, more or no selected items in either the clipboard or
 * active selected on the canvas.
 * <p>
 * Notes: <br>
 * <li>
 * <li>To create multiple actions for the <i>same</i> submenu, just specify more
 * actions which have the same submenu name! (The gui will merge the entries).
 * <li>A Drag and Drop action cannot be empty and thus cannot hase NONE Selected
 * items.
 */
class ResourceMappingPatterns
{

    /** Simple Resource Description */
    static abstract class ResourcePattern
    {
        protected static int instanceCounter = 0;

        protected int id = instanceCounter++;

        /**
         * Matches Resource Triple: {Type,Scheme,MimeType} It is up to the
         * implemenation to ignore either type, scheme and/or mimetype.
         */
        abstract boolean matches(String typeVal, String schemeVal, String mimeType);

        /**
         * Needed in super type so it can be used as first HashEntry ! Returns
         * list of allowed type or NULL if don't care.
         */
        abstract public StringList getAllowedTypes();

        /** Type Scheme pattern matches of on the supplied types,schemes tuple */
        public boolean matchesOnOf(StringList types, StringList schemes, StringList mimeTypes)
        {
            for (int i = 0; i < types.size(); i++)
            {
                if (matches(types.get(i), schemes.get(i), mimeTypes.get(i)))
                    return true;
            }

            return false;
        }

        public String toString()
        {
            StringList types = getAllowedTypes();

            if (types != null)
                return "ResourcePattern.types=" + getAllowedTypes().toString(",");

            return "ResourcePattern(...)";
        }

    };

    /** Simple Array of ResourceTypes */
    static class ResourceTypeSchemePattern extends ResourcePattern
    {
        StringList allowedTypes = null;

        StringList allowedSchemes = null;

        public StringList getAllowedTypes()
        {
            return allowedTypes;
        }

        ResourceTypeSchemePattern(String types[], String schemes[])
        {
            init(types, schemes);
        }

        /** Math type+scheme, ignore mimetype */
        ResourceTypeSchemePattern(String type, String scheme)
        {
            String types[] = null;
            String schemes[] = null;

            if (type != null)
            {
                types = new String[1];
                types[0] = type;
            }

            if (scheme != null)
            {
                schemes = new String[1];
                schemes[0] = scheme;
            }

            init(types, schemes);
        }

        /*
         * public ResourceTypeSchemePattern(Pattern[] sourceTypes, Pattern[]
         * sourceSchemes) { // TODO Auto-generated constructor stub }
         */

        private void init(String types[], String schemes[])
        {
            Debug("[" + id + "] new ResourcePattern()");

            if (types != null)
                this.allowedTypes = new StringList(types);

            if (schemes != null)
                this.allowedSchemes = new StringList(schemes);

            Debug("[" + id + "] types=" + allowedTypes + ",schemes=" + allowedSchemes);
        }

        /**
         * Checks whether type and scheme are in the allowed values, ignores
         * mimeType
         */
        boolean matches(String type, String scheme, String mimeType)
        {
            Debug("[" + id + "] Matching:" + type + "," + scheme);

            // check type:
            if (allowedTypes != null)
                if (allowedTypes.contains(type) == false)
                    return false;

            // check scheme:
            if (allowedSchemes != null)
                if (allowedSchemes.contains(scheme) == false)
                    return false;

            Debug("[" + id + "] Matches for:" + type + "," + scheme + "");

            // entries allowed
            return true;
        }

        private void Debug(String msg)
        {
            // Global.errorPrintln(this,msg);
            Global.debugPrintf(this, "%s\n", msg);
        }
    }

    /** Simple Array of ResourceTypes */
    static class ResourceTypeSchemeREPattern extends ResourcePattern
    {
        Pattern[] allowedTypes = null;

        Pattern[] allowedSchemes = null;

        public StringList getAllowedTypes()
        {
            return null;
        }

        ResourceTypeSchemeREPattern(Pattern types[], Pattern schemes[])
        {
            init(types, schemes);
        }

        ResourceTypeSchemeREPattern(Pattern type, Pattern scheme)
        {
            Pattern types[] = null;
            Pattern schemes[] = null;

            if (type != null)
            {
                types = new Pattern[1];
                types[0] = type;
            }

            if (scheme != null)
            {
                schemes = new Pattern[1];
                schemes[0] = scheme;
            }

            init(types, schemes);
        }

        private void init(Pattern types[], Pattern schemes[])
        {
            Debug("[" + id + "] new ResourcePattern()");

            if (types != null)
                this.allowedTypes = types;

            if (schemes != null)
                this.allowedSchemes = schemes;

            Debug("[" + id + "] types=" + allowedTypes + ",schemes=" + allowedSchemes);
        }

        /**
         * Checks whether type and scheme are in the allowed values ignores
         * mimeType
         */
        boolean matches(String type, String scheme, String mimeType)
        {
            Debug("[" + id + "] Matching:" + type + "," + scheme + "," + mimeType);

            if (StringUtil.isEmpty(mimeType))
                return false; // null not allowed must use RE ".*" for
                              // wildcards...

            // check type:
            if (allowedTypes != null)
            {
                boolean matches = false;

                for (Pattern typePattern : allowedTypes)
                {
                    if (typePattern.matcher(type).matches())
                    {
                        matches = true;
                        break;
                    }
                }

                if (matches == false)
                    return false;
            }

            // check scheme:
            if (allowedSchemes != null)
            {
                boolean matches = false;

                for (Pattern schemePattern : allowedSchemes)
                {
                    if (schemePattern.matcher(scheme).matches())
                    {
                        matches = true;
                        break;
                    }
                }

                if (matches == false)
                    return false;
            }

            Debug("[" + id + "] RE Matches for:" + type + "," + scheme + "");

            // entries allowed
            return true;
        }

        private void Debug(String msg)
        {
            // Global.errorPrintln(this,msg);
            Global.debugPrintf(this, "%s\n", msg);
        }

        public String toString()
        {
            String str = "ResourceTypeSchemeREPattern:";

            if (allowedTypes != null)
            {
                str += "types={";
                for (Pattern pat : this.allowedTypes)
                    str += pat + ";";

                str += "}";
            }

            if (allowedSchemes != null)
            {
                str += "schemes={";

                for (Pattern pat : this.allowedSchemes)
                    str += pat + ";";

                str += "}";
            }

            return str;
        }

    }

    /** MAtches against MimeType REs */
    static class ResourceMimeTypeREPattern extends ResourcePattern
    {
        Pattern[] mimeTypes = null;

        public StringList getAllowedTypes()
        {
            return null;
        }

        ResourceMimeTypeREPattern(Pattern mineTypes[])
        {
            init(mineTypes);
        }

        ResourceMimeTypeREPattern(Pattern type)
        {
            Pattern types[] = null;

            if (type != null)
            {
                types = new Pattern[1];
                types[0] = type;
            }

            init(types);
        }

        private void init(Pattern types[])
        {
            if (types == null)
                throw new NullPointerException("MimeTypes *only* pattern can not have NULL mimetypes list.");

            Debug("[" + id + "] new MimeType ResourcePattern()");

            if (types != null)
                this.mimeTypes = types;

            Debug("[" + id + "] types=" + mimeTypes);
        }

        /**
         * Checks whether type and scheme are in the allowed values ignores
         * mimeType
         */
        boolean matches(String type, String scheme, String mimeType)
        {
            Debug("[" + id + "] Matching:" + type + "," + scheme + "," + mimeType);

            if (mimeType == null)
                return false; // null not allowed. use "*" for wildcarding.

            // check type:
            if (mimeTypes != null)
            {
                boolean matches = false;

                for (Pattern typePattern : mimeTypes)
                {
                    if (typePattern.matcher(mimeType).matches())
                    {
                        matches = true;
                        break;
                    }
                }

                if (matches == false)
                    return false;
            }

            Debug("[" + id + "] RE Matches for:" + type + "," + scheme + "," + mimeType);

            // entries allowed
            return true;
        }

        private void Debug(String msg)
        {
            // Global.errorPrintln(this,msg);
            Global.debugPrintf(this, "%s\n", msg);
        }

        public String toString()
        {
            String str = "ResourceMimeTypePattern.mimeTypes=";

            for (Pattern pat : mimeTypes)
                str += pat;

            return str;
        }
    }

    // ========================================================================
    // instance
    // ========================================================================

    // current pattern are Type+Scheme based !
    ResourcePattern allowedSources = null;

    ResourcePattern allowedSelections = null;

    /**
     * SelectionMode. Default: ignore nr of selected times Can be ONE, TWO,
     * ONE_OR_MORE,etc.
     */

    int selectionMode = (SELECTION_DONT_CARE); // & SELECTION_TYPE_ALL) ;

    private ActionMenuMapping parentActionMenuMapping;

    protected ResourceMappingPatterns(ActionMenuMapping owner)
    {
        this.parentActionMenuMapping = owner;
    }

    public ResourceMappingPatterns(ActionMenuMapping owner, String sourceTypes[], String sourceSchemes[],
            String selectionTypes[], String selectionSchemes[], int selectionMod)
    {
        this.parentActionMenuMapping = owner;
        allowedSources = new ResourceTypeSchemePattern(sourceTypes, sourceSchemes);
        allowedSelections = new ResourceTypeSchemePattern(selectionTypes, selectionSchemes);
        this.selectionMode = selectionMod;
    }

    public ResourceMappingPatterns(ActionMenuMapping owner, Pattern sourceTypes[], Pattern sourceSchemes[],
            Pattern[] selectionTypes, Pattern selectionSchemes[], int selectionMod)
    {
        this.parentActionMenuMapping = owner;
        allowedSources = new ResourceTypeSchemeREPattern(sourceTypes, sourceSchemes);
        allowedSelections = new ResourceTypeSchemeREPattern(selectionTypes, selectionSchemes);
        this.selectionMode = selectionMod;
    }

    ActionMenuMapping getActionMenuMapping()
    {
        return this.parentActionMenuMapping;
    }

    // Has selection mode enabled
    boolean hasSelectionMode(int mode)
    {
        return ((this.selectionMode & mode) == mode);
    }

    /* *
     * Match source+selections as follows:
     * 
     * (I) Check source type+scheme Pre check selections: (IIa) Selection Mode =
     * DONT_CARE -> return true; (IIb) Selection Mode = NONE and no selections
     * -> return true; (IIc) Selection Mode = other combinations
     * 
     * (III) Post check
     */
    public boolean matches(ActionContext selContext)
    {
        if (selContext == null)
            return false;

        //
        VRL source = selContext.source;
        String sourceType = selContext.type;
        String mimeType = selContext.mimeType;

        //
        // (I) First check source, if it doesn't match, the selections don't
        // need to be matched.
        //
        String sourceScheme = null;

        if (source != null)
            sourceScheme = source.getScheme();

        if (allowedSources.matches(sourceType, sourceScheme, mimeType) == false)
            return false;

        int numSelections = 0;
        if (selContext.selectionTypes != null)
            numSelections = selContext.selectionTypes.length;

        // (IIa) SelectionMode= Don't care: return true !
        if (this.selectionMode == SELECTION_DONT_CARE)
            return true;

        if (matchesSelectionType(selContext) == false)
        {
            Debug("matchesSelectionType return false");
            return false;
        }

        Debug("matchesSelectionType return true");

        // (IIb) SelectionMode=NONE check whether there are null selections
        if ((this.hasSelectionMode(SELECTION_NONE)) && (numSelections <= 0))
            return true;

        // int numMatches=0;
        boolean matchall = true; // For All:(match==true)
        @SuppressWarnings("unused")
        boolean matchany = false; // Exists: (match==true)

        // optimilization: Check numbers of selection with selection mode
        // and filter out impossible combinations which can be detected before
        // doing the match
        //

        // (IIc) pre check whether nr of selections are possible
        if (exactNumberOfSelectionsPossible(numSelections) == false)
            return false;

        for (int i = 0; i < numSelections; i++)
        {
            String selectionScheme = selContext.selections[i].getScheme();
            String selectionType = selContext.selectionTypes[i];
            String selectionMimeType = selContext.selectionMimeTypes[i];

            if (allowedSelections.matches(selectionType, selectionScheme, selectionMimeType))
            {
                // matches;
                matchany = true;
                // numMatches++;
            }
            else
            {
                matchall = false;
            }
        }

        // current default is that all selected resource must match except if
        // mode is DON'T CARE
        if (matchall == false)
            return false;

        // SELECTION_NONE and DONT_CARE already checked !

        if (this.hasSelectionMode(SELECTION_ONE) && numSelections == 1)
            return true;

        if (this.hasSelectionMode(SELECTION_TWO) && numSelections == 2)
            return true;

        // ONE ore MORE = (ONE or TWO_OR_MORE)

        if (this.hasSelectionMode(SELECTION_TWO_OR_MORE) && numSelections >= 2)
            return true;

        // Allow sloppy checking ?

        // ============================================
        // check nr of selections with selections mode:
        // ============================================

        // current default is that all selected resource must match except if
        // mode is DON'T CARE
        // if (matchall==false)
        // return false;

        // default: don't match

        return false;
    }

    private boolean matchesSelectionType(ActionContext selContext)
    {
        Debug("matchesSelectionType:" + this.selectionMode);

        boolean wantClipboard = this.hasSelectionMode(SELECTION_TYPE_CLIPBOARD);
        boolean wantDnDMiniMenu = this.hasSelectionMode(SELECTION_TYPE_DRAG_AND_DROP);
        boolean wantCanvasSelection = ((wantClipboard == false) && (wantDnDMiniMenu == false));

        Debug("matchesSelectionType wantClipboard       =" + wantClipboard);
        Debug("matchesSelectionType wantDnDMiniMenu     =" + wantDnDMiniMenu);
        Debug("matchesSelectionType wantCanvasSelection =" + wantCanvasSelection);

        // clipboard (Copy, Paste)
        if (selContext.isClipboardSelection)
            if (wantClipboard == true)
                return true;

        // Drag And Drop:
        if (selContext.isMiniDnDMenu)
            if (wantDnDMiniMenu == true)
                return true;

        // Canvas Selection:
        if ((selContext.isClipboardSelection == false) && (selContext.isMiniDnDMenu == false))
            if (wantCanvasSelection)
                return true;

        return false;
    }

    private boolean exactNumberOfSelectionsPossible(int num)
    {
        // Note: the actual numerical value is checked ('==' operator)
        // So this method checks explicit modes (one explicit set Selection
        // Flag).
        //

        // none selections are wanted
        if (this.selectionMode == SELECTION_NONE)
            if (num != 0)
                return false;

        // exactly one selection is needed.
        if (this.selectionMode == SELECTION_ONE)
            if (num != 1)
                return false;

        // exactly two selection are needed.
        if (this.selectionMode == SELECTION_TWO)
            if (num != 2)
                return false;

        if (this.selectionMode == SELECTION_ONE_OR_MORE)
            if (num <= 0)
                return false;

        return true;
    }

    public ResourcePattern getAllowedSources()
    {
        return this.allowedSources;
    }

    public ResourcePattern getAllowedSelections()
    {
        return this.allowedSelections;
    }

    private void Debug(String msg)
    {
        // Global.errorPrintln(this,msg);
        Global.debugPrintf(this, "%s\n", msg);
    }

    /** Factory method for MimeType patterns */
    public static ResourceMappingPatterns createMimeTypeMappings(ActionMenuMapping owner, Pattern[] sourceMimeTypes,
            Pattern[] selectionMimeTypes, int selectionMod)
    {
        ResourceMappingPatterns mappingPattern = new ResourceMappingPatterns(owner);
        mappingPattern.allowedSources = new ResourceMimeTypeREPattern(sourceMimeTypes);

        if (selectionMimeTypes != null)
            mappingPattern.allowedSelections = new ResourceMimeTypeREPattern(selectionMimeTypes);

        return mappingPattern;

    }

    public String toString()
    {
        return "ResourceMappingPatterns:" + allowedSources + " / " + allowedSelections;
    }

}