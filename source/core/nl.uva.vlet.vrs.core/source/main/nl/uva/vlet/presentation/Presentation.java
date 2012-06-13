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
 * $Id: Presentation.java,v 1.17 2011-06-09 14:30:29 ptdeboer Exp $  
 * $Date: 2011-06-09 14:30:29 $
 */ 
// source: 

package nl.uva.vlet.presentation;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ATTEMPTS;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CREATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_DEST_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_DEST_URL;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_FAULT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ICON;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_INDEX;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MAX_WALL_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MIMETYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MODIFICATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NODE_TEMP_DIR;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PERMISSIONS_STRING;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SOURCE_FILENAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SOURCE_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SOURCE_URL;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_STATUS;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;

import java.awt.Color;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.JTable;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

/**
 * Create custom Presentation how to show a VNode and store settings so it can
 * be restored when browsing locations. This object doesn't know anything about
 * GUI stuff, but is simply a place holder for the VBrowser and the VRS to
 * communicate Presentation Settings.
 * <p>
 * PresentationRegistry :<br>
 * Presentation info is stored per Scheme+Host+ResourceType. Optionally the path
 * could also be used, but then the presentation info has to be stored, in THAT
 * location.
 * <p>
 * 
 * @author P.T. de Boer
 */
public class Presentation
{
    /** Different types of default sorting methods */
    public static enum SortType
    {
        /** Don Not Sort */
        NONE,
        /** Sort on name */
        NAME,
        /** Sort on Name first then Type */
        NAME_TYPE,
        /** Sort on Type first then Name */
        TYPE_NAME,
        /** Custom sort -> needs filter */
        CUSTOM;
    }

    // === class === //s
    public static class PreferredSizes
    {
        int minimum=-1; 
        int preferred=-1; 
        int maximum=-1; 

        public PreferredSizes(int minWidth, int prefWidth, int maxWidth)
        {
            this.minimum=minWidth;
            this.preferred=prefWidth;
            this.maximum=maxWidth;
        }
        
        public int getMinimum()
        {
            return minimum; 
        }
        
        public int getMaximum()
        {
            return maximum; 
        }
        
        public int getPreferred()
        {
            return preferred; 
        }
        
        public int[] getValues()
        {
            return new int[]{minimum,preferred,maximum}; 
        }

        /** Set [minimum,preferred,maximum] values */ 
        public void setValues(int[] values)
        {
            this.minimum   = values[0]; 
            this.preferred = values[1]; 
            this.maximum   = values[2]; 
        }
    }

    public static class AttributePresentation
    {
        PreferredSizes widths = null;

        Color foreground = null;
        
        Color background = null;

        Map<String, Color> colorMap = null;

        boolean attributeFieldResizable=true; 
        
        public PreferredSizes getWidths()
        {   
            return widths; 
        }
        
        public int[] getWidthValues()
        {   
            if (widths==null)
                return null;
            
            return widths.getValues();  
        }
        
        public void setWidthValues(int[] values)
        {
            if (this.widths==null)
                this.widths=new PreferredSizes(values[0],values[1],values[2]); 
            else
                this.widths.setValues(values); 
        }
    }
    

    static Hashtable<String, Presentation> presentationStore = new Hashtable<String, Presentation>();

    /** Default Attribute Name to show for VFSNodes */
    public static String defaultVFSAttributeNames[] = { ATTR_ICON, ATTR_NAME, ATTR_TYPE, ATTR_LENGTH,
    // ATTR_MODIFICATION_TIME_STRING,
            ATTR_MODIFICATION_TIME, ATTR_MIMETYPE, ATTR_PERMISSIONS_STRING,
    // ATTR_ISHIDDEN,
    // ATTR_ISLINK
    // VFS.ATTR_ISFILE,
    // VFS.ATTR_ISDIR
    };

    /** Default Attribute Name to show for VFSNodes */
    public static String defaultSRBAttributeNames[] = { ATTR_ICON, ATTR_NAME, ATTR_TYPE, ATTR_LENGTH, "Resource",
    // ATTR_MODIFICATION_TIME_STRING,
            ATTR_MODIFICATION_TIME, ATTR_MIMETYPE,
    // ATTR_PERMISSIONS_STRING,
    // ATTR_ISHIDDEN,
    // ATTR_ISLINK
    // VFS.ATTR_ISFILE,
    // VFS.ATTR_ISDIR
    };
    
    /** Default Attribute Name to show for VNodes */
    public static String defaultNodeAttributeNames[] = { ATTR_ICON, ATTR_TYPE, ATTR_NAME,
    // ATTR_LENGTH,
            ATTR_MIMETYPE };

    /** Default Attribute Name to show for VNodes */
    public static String myvleAttributeNames[] = { ATTR_ICON, ATTR_TYPE, ATTR_NAME, ATTR_SCHEME, ATTR_HOSTNAME,
            ATTR_PATH,
    // ATTR_LENGTH,
    // ATTR_MIMETYPE
    };

    /** Default Attribute names to show for RTFSJobs */
    public static String defaultRFTJobAttributeNames[] = { ATTR_ICON, ATTR_TYPE, ATTR_NAME, "requestStatus",
            ATTR_FAULT, "transfersFinished", "transfersActive", "transfersRestarted", "transfersFailed",
            "transfersCancelled", "transfersPending",

    };

    /** Default Attribute names to show for RTFSJobs */
    public static String defaultRFSTransferAttributeNames[] = { ATTR_ICON, ATTR_TYPE, ATTR_NAME, ATTR_ATTEMPTS,
            ATTR_STATUS, ATTR_FAULT, ATTR_SOURCE_HOSTNAME, ATTR_SOURCE_FILENAME, ATTR_SOURCE_URL, ATTR_DEST_HOSTNAME,
            ATTR_DEST_URL

    };

//    /** Default Attribute names to show for VQueues */
//    public static String defaultJobManagerAttributeNames[] = { ATTR_ICON, ATTR_TYPE, ATTR_NAME, ATTR_HOSTNAME,
//            ATTR_PATH, ATTR_QUEUE_NAME, ATTR_MAX_WALL_TIME, ATTR_NODE_TEMP_DIR, ATTR_SHELL_PATH };

//    /** Default Attribute names to show for VJobs */
//    public static String defaultJobGroupAttributeNames[] = { ATTR_ICON, ATTR_TYPE, ATTR_NAME, ATTR_JOB_SUBMISSION_TIME,
//            ATTR_STATUS, ATTR_JOB_STATUS_UPDATE_TIME, ATTR_JOB_HAS_TERMINATED, ATTR_WMS_STATUS_TEXT };

    public static String monthNames[] = { "Jan", 
                                            "Feb", 
                                            "Mar", 
                                            "Apr", 
                                            "May", 
                                            "Jun", 
                                            "Jul", 
                                            "Aug", 
                                            "Sep", 
                                            "Oct", 
                                            "Nov",
                                            "Dec" };

    /** format number to 00-99 format */
    public static String to2decimals(long val)
    {
        String str = "";

        if (val < 0)
        {
            str = "-";
            val = -val;
        }

        if (val < 10)
            return str + "0" + val;
        else
            return str + val;
    }

    /** format number to 000-999 format */
    public static String to3decimals(long val)
    {
        String str = "";

        if (val < 0)
        {
            str = "-";
            val = -val;
        }

        if (val < 10)
            return str = "00" + val;
        else if (val < 100)
            return str + "0" + val;
        else
            return str + val;
    }

    /** format number to 0000-9999 format */
    public static String to4decimals(long val)
    {
        String str = "";

        if (val < 0)
        {
            str = "-";
            val = -val;
        }

        if (val < 10)
            return str = "000" + val;
        else if (val < 100)
            return str + "00" + val;
        else if (val < 1000)
            return str + "0" + val;
        else
            return str + val;
    }
    /**
     * Returns time string relative to current time in millis since 'epoch'. If,
     * for example, the date is 'today' it will print 'today hh:mm:ss' if the
     * year is this year, the year will be ommitted.
     * 
     * @param date
     * @return
     */
    public static String relativeTimeString(Date dateTime)
    {
        if (dateTime == null)
            return "?";

        long current = System.currentTimeMillis();
        GregorianCalendar now = new GregorianCalendar();
        now.setTimeInMillis(current);

        // convert to local timezone !
        TimeZone localTZ = now.getTimeZone();
        GregorianCalendar time = new GregorianCalendar();
        time.setTime(dateTime);
        time.setTimeZone(localTZ);

        String tstr = "";

        int y = time.get(GregorianCalendar.YEAR);
        int M = time.get(GregorianCalendar.MONTH);
        int D = time.get(GregorianCalendar.DAY_OF_MONTH);
        int cont = 0;

        if (y != now.get(GregorianCalendar.YEAR))
        {
            tstr = "" + y + " ";
            cont = 1;
        }

        if ((cont == 1) || (M != now.get(GregorianCalendar.MONTH)) || (D != now.get(GregorianCalendar.DAY_OF_MONTH)))
        {
            tstr = tstr + monthNames[M];

            tstr += " " + to2decimals(D);
        }
        else
        {
            tstr += "today ";
        }

        tstr += " " + to2decimals(time.get(GregorianCalendar.HOUR_OF_DAY)) + ":"
                + to2decimals(time.get(GregorianCalendar.MINUTE)) + ":"
                + to2decimals(time.get(GregorianCalendar.SECOND));

        // add timezone string:
        tstr += " (" + localTZ.getDisplayName(true, TimeZone.SHORT) + ")";
        return tstr;
    }

    /** @see getPresentationFor(String, String, String, boolean) */
    public static Presentation getPresentationFor(String scheme, String host, String type)
    {
        return getPresentationFor(scheme, host, type, true);
    }

    /** @see getPresentationFor(String, String, String, boolean) */
    public static Presentation getPresentationFor(VRL vrl, String type, boolean autocreate)
    {
        return getPresentationFor(vrl.getScheme(), vrl.getHostname(), type, autocreate);
    }

    /**
     * Checks the PresentationStore if there is already Presentation information
     * stored. If no presentation can be found and autocreate==true, a default
     * Presentation object will be created.
     * 
     * @param scheme
     *            scheme of resource
     * @param host
     *            hostname of resource
     * @param type
     *            VRS type of resource
     * @param autocreate
     *            whether to initialize a default Presentation object
     * @return
     */
    public static Presentation getPresentationFor(String scheme, String host, String type, boolean autocreate)
    {
        String id = createID(scheme, host, type);
        Presentation pres = null;

        synchronized (presentationStore)
        {
            if ((pres = presentationStore.get(id)) != null)
                return pres;

            if (autocreate == false)
                return null;

            pres = new Presentation();
            presentationStore.put(id, pres);

            // 
            // update default presentation
            //
        }

        //
        // Set defaults:
        //

        if (scheme.compareTo(VRS.MYVLE_SCHEME) == 0)
        {
            pres.setChildAttributeNames(myvleAttributeNames);
            // dont sort MyVle !
            pres.setAutoSort(false);
        }

        // presentation=this.getVRS().getDefaultPresentation();
        // if (this.vnode.getType().compareTo(VRS.RFTS_JOB_TYPE)==0)
        else if (scheme.compareTo(VRS.RFTS_SCHEME) == 0)
        {

            if (type.compareTo(VRS.RFTS_JOB_TYPE) == 0)
            {
                // RFT Job, presentation is about childs: which is RFT
                // Transfers:
                pres.setChildAttributeNames(Presentation.defaultRFSTransferAttributeNames);
            }
            else if (type.compareTo(VRS.RFTS_SERVER_TYPE) == 0)
            {
                pres.setChildAttributeNames(Presentation.defaultRFTJobAttributeNames);// null-
                                                                                      // >
                                                                                      // show
                                                                                      // ALL
            }
            else
            {
                // not possible
                pres.setChildAttributeNames(null);
            }

            // names are numbers in RFT:
            pres.setAttributePreferredWidth(ATTR_NAME, 40);

        }
        else if (scheme.compareTo(VRS.SRB_SCHEME) == 0)
        {
            pres.setChildAttributeNames(Presentation.defaultSRBAttributeNames);
        }
        else if (type.compareTo(VFS.DIR_TYPE) == 0)
        {
            pres.setChildAttributeNames(Presentation.defaultVFSAttributeNames);
        }
        // Handled by WMS and LB implementations: 
//        else if (type.compareTo(VJS.TYPE_VJOBMANAGER) == 0)
//        {
//            pres.setChildAttributeNames(Presentation.defaultJobManagerAttributeNames);
//            // shorter attribute widths:
//            pres.setAttributePreferredWidth(ATTR_NAME, 120);
//            pres.setAttributePreferredWidth(ATTR_PATH, 120);
//        }
//        else if ((type.compareTo(VJS.TYPE_VJOBGROUP) == 0) || (type.compareToIgnoreCase("MyJobs") == 0))
//        {
//            pres.setChildAttributeNames(Presentation.defaultJobGroupAttributeNames);
//            // shorter attribute widths:
//            pres.setAttributePreferredWidth(ATTR_NAME, 160);
//            pres.setAttributePreferredWidth(ATTR_PATH, 180);
//            pres.setAttributePreferredWidth(ATTR_JOB_ERROR_TEXT, 240);
//            pres.setAttributePreferredWidth(ATTR_JOB_STATUS_UPDATE_TIME, 180);
//            pres.setAttributePreferredWidth(ATTR_JOB_SUBMISSION_TIME, 180);
//
//            pres.setAutoSort(false);
//            pres.getAutoSort();
//        }
        else
        {
            pres.setChildAttributeNames(Presentation.defaultNodeAttributeNames);
        }

        return pres;
    }

    public static void store(VRL vrl, String type, Presentation pres)
    {
        if (pres == null)
            return;

        synchronized (presentationStore)
        {
            String id = createID(vrl.getScheme(), vrl.getHostname(), type);
            presentationStore.put(id, pres);
        }
    }

    private static String createID(String scheme, String host, String type)
    {
        return scheme + "-" + host + "-" + type;
    }

    /**
     * Returns size in xxx.yyy[KMGTP] format. argument base1024 specifies wether
     * unit base is 1024 or 1000.
     * 
     * @param size
     *            actual size
     * @param base1024
     *            whether to use unit = base 1024 (false is base 1000)
     * @param unitScaleThreshold
     *            at which 1000 unit to show ISO term (1=K,2=M,3=G,etc)
     * @param nrDecimalsBehindPoint
     *            number of decimals behind the point.
     */
    public static String createSizeString(long size, boolean base1024, int unitScaleThreshold, int nrDecimals)
    {
        // boolean negative;
        String prestr = "";

        if (size < 0)
        {
            size = -size;
            // negative = true;
            prestr = "-";
        }

        long base = 1000;

        if (base1024)
            base = 1024;

        String unitstr = "";
        int scale = 0; // 

        if (size < base)
        {
            unitstr = "";
            scale = 0;
        }

        scale = (int) Math.floor(Math.log(size) / Math.log(base));

        switch ((int) scale)
        {
            default:
            case 0:
                unitstr = "";
                break;
            case 1:
                unitstr = "K";
                break;
            case 2:
                unitstr = "M";
                break;
            case 3:
                unitstr = "G";
                break;
            case 4:
                unitstr = "T";
                break;
            case 5:
                unitstr = "P";
                break;
        }
        if (base1024 == false)
            unitstr += "i"; // ISO Ki = Real Kilo, Mi=Million Gi = real Giga.

        // 1024^5 fits in long !
        double norm = (double) size / (Math.pow(base, scale));

        // unitScaleThreshold = upto '1'

        if (scale < unitScaleThreshold)
            return "" + size;

        // format to xxx.yyy<UNIT>
        double fracNorm = Math.pow(10, nrDecimals);
        norm = Math.floor((norm * fracNorm)) / fracNorm;

        return prestr + norm + unitstr;
    }

    public static Presentation getDefault()
    {
        return new Presentation(); // return default object;
    }

    /**
     * Size of Strings, at which they are consider to be 'big'. Currentlty this
     * value determines when the AttributeViewer pop-ups.
     * 
     * @return
     */
    public static int getBigStringSize()
    {
        return 42;
    }

    /** Convert millis since Epoch to Date object */
    public static Date createDate(long millis)
    {

        if (millis < 0)
            return null;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(millis);
        return cal.getTime();
    }

    /** Convert System millies to Date */
    public static Date now()
    {
        long millis = System.currentTimeMillis();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(millis);
        return cal.getTime();
    }

    /**
     * Create GMT Normalized Date Time String from millis since Epoch.
     * <p>
     * Normalized time = YYYYYY MM DD hh:mm:ss.mss <br>
     * Normalized Timezone is GMT.<br>
     */
    public static String createNormalizedDateTimeString(long millis)
    {
        if (millis < 0)
            return null;

        Date date = createDate(millis);
        return Presentation.createNormalizedDateTimeString(date);
    }

    /**
     * Normalized date time string: "YYYYYY-MM-DD hh:mm:ss.milllis".<br>
     * Normalized Timezone is GMT.<br>
     */
    public static Date createDateFromNormalizedDateTimeString(String value)
    {
        if (value == null)
            return null;

        String strs[] = value.split("[ :-]");

        int year = new Integer(strs[0]);
        int month = new Integer(strs[1]) - 1; // January=0!
        int day = new Integer(strs[2]);
        int hours = new Integer(strs[3]);
        int minutes = new Integer(strs[4]);
        double secondsD = 0;
        if (strs.length > 5)
            secondsD = new Double(strs[5]);

        // String tzStr=null;
        TimeZone storedTimeZone = TimeZone.getTimeZone("GMT");

        /*
         * if (strs.length>6) { tzStr=strs[6];
         * 
         * if (tzStr!=null) storedTimeZone=TimeZone.getTimeZone(tzStr); }
         */
        int seconds = (int) Math.floor(secondsD);
        // Warning: millis is in exact 3 digits, but Double create
        // floating point offsets to approximate 3 digit precizion!
        int millis = (int) Math.round(((secondsD - Math.floor(secondsD)) * 1000));

        GregorianCalendar now = new GregorianCalendar();
        // TimeZone localTMZ=now.getTimeZone();

        now.clear();
        // respect timezone:
        now.setTimeZone(storedTimeZone);
        now.set(year, month, day, hours, minutes, seconds);
        now.set(GregorianCalendar.MILLISECOND, millis); // be precize!
        // convert timezone back to 'local'
        // now.setTimeZone(localTMZ);

        return now.getTime();
    }

    /**
     * Create normalized date time string: [YY]YYYY-DD-MM hh:mm:ss.ms in GMT
     * TimeZone.
     */
    public static String createNormalizedDateTimeString(Date date)
    {
        GregorianCalendar gmtTime = new GregorianCalendar();
        gmtTime.setTime(date);
        // normalize to GMT:
        gmtTime.setTimeZone(TimeZone.getTimeZone("GMT"));

        int year = gmtTime.get(GregorianCalendar.YEAR);
        int month = 1 + gmtTime.get(GregorianCalendar.MONTH); // January=0!
        int day = gmtTime.get(GregorianCalendar.DAY_OF_MONTH);
        int hours = gmtTime.get(GregorianCalendar.HOUR_OF_DAY);
        int minutes = gmtTime.get(GregorianCalendar.MINUTE);
        int seconds = gmtTime.get(GregorianCalendar.SECOND);
        int millies = gmtTime.get(GregorianCalendar.MILLISECOND);

        return to4decimals(year) + "-" 
                + to2decimals(month) + "-" 
                + to2decimals(day) + " " 
                + to2decimals(hours) + ":"
                + to2decimals(minutes) + ":" 
                + to2decimals(seconds) + "." 
                + to3decimals(millies);
    }

    /** Convert Normalized DateTime string to millis since epoch */
    public static long createMillisFromNormalizedDateTimeString(String value)
    {
        if (value == null)
            return -1;

        Date date = Presentation.createDateFromNormalizedDateTimeString(value);

        if (date == null)
            return -1;

        return date.getTime();
    }

    // =============================================================
    // Instance
    // =============================================================

    protected Map<String, AttributePresentation> attributePresentations = new Hashtable<String, AttributePresentation>();

    private Integer defaultUnitScaleThreshold = 2; // skip Kilo byte (1), start
                                                   // at megabytes (2)

    private Integer defaultNrDecimals = 1; // nr of decimals behind point

    private Boolean useBase1024 = true; // KiB/MiB in powers of 1024

    private Boolean allowSort = null;

    private Boolean sortIgnoreCase = true;

    // attribute names from child to show by default:
    protected StringList childAttributeNames = new StringList(defaultNodeAttributeNames);

    protected StringList autoSortFields; 
    
    protected Presentation parent = null; // No hierarchical presentation (yet)

    private Locale locale = null; // Optional Locale, null -> use platform Local
    
    private int columnsAutoResizeMode=JTable.AUTO_RESIZE_ALL_COLUMNS; //.AUTO_RESIZE_OFF;
    
    // default presentation:
    public Presentation()
    {
        initDefaults();
    }

    private void initDefaults()
    {
        setAttributePreferredWidth(ATTR_ICON, 32);
        setAttributePreferredWidth(ATTR_INDEX, 32);
        setAttributePreferredWidth(ATTR_NAME, 200);
        setAttributePreferredWidth(ATTR_TYPE, 90);
        setAttributePreferredWidth(ATTR_SCHEME, 48);
        setAttributePreferredWidth(ATTR_HOSTNAME, 120);
        setAttributePreferredWidth(ATTR_LENGTH, 70);
        setAttributePreferredWidth(ATTR_PATH, 200);
        setAttributePreferredWidth(ATTR_STATUS, 48);
        setAttributePreferredWidth(ATTR_MODIFICATION_TIME, 120);
        setAttributePreferredWidth(ATTR_CREATION_TIME, 120);
        // RFT attributes
        setAttributePreferredWidth(ATTR_SOURCE_URL, 200);
        setAttributePreferredWidth(ATTR_DEST_URL, 200);
        setAttributePreferredWidth(ATTR_DEST_HOSTNAME, 128);
        setAttributePreferredWidth(ATTR_SOURCE_HOSTNAME, 128);
        setAttributePreferredWidth(ATTR_FAULT, 120);
        // VQueues and VJobs:
        setAttributePreferredWidth(ATTR_MAX_WALL_TIME, 100);
        setAttributePreferredWidth(ATTR_NODE_TEMP_DIR, 160);
    }

    /**
     * Get which Child Attribute to show by default. Note that it is the PARENT
     * object which holds the presentation information about the child
     * attributes. For example when opening a Directory in Table view the
     * Presentation of the (parent) directory holds the default file attributes
     * to show.
     */
    public String[] getChildAttributeNames()
    {
        return childAttributeNames.toArray();
    }

    /** Set which child attribute to show */
    public void setChildAttributeNames(String names[])
    {
        childAttributeNames = new StringList(names);
    }


    /** Method will return null if information hasn't been stored ! */
    public Integer getAttributePreferredWidth(String name)
    {
        AttributePresentation attrPres = this.attributePresentations.get(name);
        if (attrPres == null)
            return null;
        
        if (attrPres.widths==null)
            return null; 
        
        if (attrPres.widths.preferred<0)
            return null; 
        
        return new Integer(attrPres.widths.preferred); 
    }
    
    /**
     * Returns Integer[]{<Minimum>,<Preferred>,<Maximum>} Triple. 
     * Integer value is NULL is it isn't defined. 
     * Method will always return an Integer array of size 3, but actual values may be null.  
     * 
     */
    public Integer[] getAttributePreferredWidths(String name)
    {
        Integer vals[]=new Integer[3]; 
        
        AttributePresentation attrPres = this.attributePresentations.get(name);
        if (attrPres == null)
            return vals;
        
        if (attrPres.widths==null)
            return vals; 
        
        if (attrPres.widths.minimum>=0)
            vals[0]=new Integer(attrPres.widths.minimum);
        
        if (attrPres.widths.preferred>=0)
            vals[1]=new Integer(attrPres.widths.preferred);
        
        if (attrPres.widths.maximum>=0)
            vals[2]=new Integer(attrPres.widths.maximum);  
        
        return vals;  
    }

    /** Returns sizeString +"[KMG]B/s" from "size" bytes per second */
    public String speedString(long size)
    {
        return sizeString(size) + "B/s";
    }

    /**
     * Returns size in xxx.yyy[KMGTP] format (base 1024). Uses settings from
     * Presentation instance.
     * 
     * @see #createSizeString(long, boolean, int, int)
     */
    public String sizeString(long size)
    {
        return sizeString(size, useBase1024, this.defaultUnitScaleThreshold, this.defaultNrDecimals);
    }

    /** @see #createSizeString(long, boolean, int, int) */
    public String sizeString(long size, boolean base1024, int unitScaleThreshold, int nrDecimals)
    {
        return createSizeString(size, base1024, unitScaleThreshold, nrDecimals);
    }

    /**
     * Create Relative Time String: "DD (days) hh:mm:ss.ms" time string" from
     * the specified nr of milli seconds.
     * 
     */
    public String timeString(long timeInMillis, boolean showMillis)
    {
        String timestr = "";

        if (timeInMillis > 1000L * 24L * 60L * 60L)
        {
            long days = timeInMillis / (1000L * 24L * 60L * 60L);
            timestr += days + " (days) ";
        }

        if (timeInMillis > 1000 * 60 * 60)
        {
            long hours = (timeInMillis / (1000L * 60 * 60)) % 60;
            timestr += timestr + to2decimals(hours) + ":";
        }
        // show it anyway to always show 00:00s format
        // if (time>1000*60)
        {
            long mins = (timeInMillis / (1000 * 60)) % 60;
            timestr += timestr + to2decimals(mins) + ":";
        }

        long secs = (timeInMillis / 1000L) % 60L;
        timestr += to2decimals(secs) + "s";

        if (showMillis)
            timestr += "." + (timeInMillis % 1000);

        return timestr;
    }


    /** Whether to ignore case when sorting files */
    public boolean getSortIgnoreCase()
    {
        if (sortIgnoreCase == null)
            return false;

        return this.sortIgnoreCase;
    }

    public void setSortIgnoreCase(boolean val)
    {
        this.sortIgnoreCase = val;
    }

    public Locale getLocale()
    {
        if (this.locale != null)
            return this.locale;

        return Locale.getDefault();
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * The first month (Jan) is 0 not 1. 
     * If month is null of len is < 3 will return -1
     * @param month String of len 3 (Jan,Feb..,etc)
     * @return the 0-based month number; 
     */
    public static int getMonthNumber(String month)
    {
        if(month == null || month.length() < 3)
        {
            return -1;
        }
        
        for (int i = 0; i < monthNames.length; i++)
        {
            if (month.substring(0, 3).compareToIgnoreCase(monthNames[i]) == 0)
            {
                return i;
            }
        }
        return -1;

    }

    public void setAttributePreferredWidth(String attrname, int minWidth,int prefWidth,int maxWidth)
    {
        AttributePresentation pres = this.attributePresentations.get(attrname);
        
        if (pres == null)
            pres = new AttributePresentation();
        
        pres.widths=new PreferredSizes(minWidth,prefWidth,maxWidth);  
        this.attributePresentations.put(attrname, pres);
    }

    public void setAttributePreferredWidth(String attrname, int w)
    {
        AttributePresentation pres = this.attributePresentations.get(attrname);
        
        if (pres == null)
            pres = new AttributePresentation();
        
        if (pres.widths==null)
            pres.widths=new PreferredSizes(-1,w,-1); 
        else
            pres.widths.preferred=w;
        
        this.attributePresentations.put(attrname, pres);// update
    }

    public int getColumnsAutoResizeMode()
    {
        return this.columnsAutoResizeMode; 
    }
    
    public void setColumnsAutoResizeMode(int value)
    {
        this.columnsAutoResizeMode=value;  
    }


    public Map<String, AttributePresentation> getAttributePresentations()
    {
        return this.attributePresentations; 
    }
    
    // ==============
    // Sort Options
    // ==============

    /**
     * Returns optional attribute sort fields, by which this contents should be sorted.
     * If set the attribute names will be used to sort the contents of a resource. 
     * If sortFields are NULL, then the default Type+Name sort is used.  
     */ 
    public StringList getAutoSortFields()
    {
        return this.autoSortFields; 
    }

    /**
     * Set optional (Attribute) sort fields. If set the attribute names will be used
     * to sort the contents of a resource. 
     * If sortFields are null, then the default Type+Name sort is used. 
     */
    public void setAutoSortFields(String[] fields)
    {
        this.autoSortFields=new StringList(fields); 
    }
    
    /**
     * Whether automatic sorting is allowed or that the returned order of this node should
     * be kept as-is.
     * Optionally use setAutoSortFields() to specify which fields should be used for auto sorting. 
     */
    public boolean getAutoSort()
    {
        if (this.allowSort == null)
            return true;

        return this.allowSort;
    }

    /** 
     * Specify whether nodes should be sorted automatically when fetched from this
     * resource, or nodes should be displayed 'in order'. 
     */
    public void setAutoSort(boolean newVal)
    {
        this.allowSort = newVal;
    }

}
