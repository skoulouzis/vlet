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
 * $Id: JobEventNode.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.TreeMap;
import java.util.Map.Entry;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.io.VStreamReadable;

import org.apache.axis.encoding.AnyContentType;
import org.glite.wsdl.types.lb.Event;
import org.glite.wsdl.types.lb.Timeval;

public class JobEventNode extends VNode implements VStreamReadable
{

    private static ClassLogger logger;

    private Event event;

    private TreeMap<Date, String> map = new TreeMap<Date, String>();

    // private ArrayList<AnyContentType> events = new
    // ArrayList<AnyContentType>();
    private Event[] events;

    private String allReports = "";
    
    static
    {
        logger = ClassLogger.getLogger(JobEventNode.class);
        logger.setLevelToDebug();
    }

    public JobEventNode(VRSContext context, VRL vrl, Event event) throws VlException
    {
        super(context, vrl);
        this.event = event;

        try
        {
            init();
        }
        catch (Exception e)
        {
            throw new VlException(e);
        }
        
        logger.debugPrintf("New Events node: %s\n", vrl);
        
    }

    public JobEventNode(VRSContext context, VRL vrl, Event[] events) throws VlException
    {
        super(context, vrl);
        this.events = events;

        try
        {
            init2();
        }
        catch (Exception e)
        {
            throw new VlException(e);
        }
        
//        logger.debugPrintf(">>>>>>>>>>>>>>>>>>New Event node: %s\n", vrl);
//        logger.debugPrintf("---------------%s\n", allReports);
    }

    private void init2() throws Exception
    {
        for (int i = 0; i < events.length; i++)
        {
            addEvent(events[i].getAbort());

            addEvent(events[i].getAccepted());

            addEvent(events[i].getCancel());

            addEvent(events[i].getChangeACL());

            addEvent(events[i].getChkpt());

            addEvent(events[i].getClear());

            addEvent(events[i].getCollectionState());

            addEvent(events[i].getCondorError());

            addEvent(events[i].getCondorMatch());

            addEvent(events[i].getCondorReject());

            addEvent(events[i].getCondorResourceUsage());

            addEvent(events[i].getCondorShadowExited());

            addEvent(events[i].getCondorShadowStarted());

            addEvent(events[i].getCurDescr());

            addEvent(events[i].getDeQueued());

            addEvent(events[i].getDone());

            addEvent(events[i].getEnQueued());

            addEvent(events[i].getHelperCall());

            addEvent(events[i].getHelperReturn());

            addEvent(events[i].getListener());

            addEvent(events[i].getMatch());

            addEvent(events[i].getNotification());

            addEvent(events[i].getPBSDequeued());

            addEvent(events[i].getPBSDone());

            addEvent(events[i].getPBSError());

            addEvent(events[i].getPBSMatch());

            addEvent(events[i].getPBSPending());

            addEvent(events[i].getPBSQueued());

            addEvent(events[i].getPBSRerun());

            addEvent(events[i].getPBSResourceUsage());

            addEvent(events[i].getPBSRun());

            addEvent(events[i].getPending());

            addEvent(events[i].getPurge());

            addEvent(events[i].getReallyRunning());

            addEvent(events[i].getRefused());

            addEvent(events[i].getRegJob());

            addEvent(events[i].getResourceUsage());

            addEvent(events[i].getResubmission());

            addEvent(events[i].getResume());

            addEvent(events[i].getRunning());

            addEvent(events[i].getSuspend());

            addEvent(events[i].getTransfer());

            addEvent(events[i].getUserTag());
        }

    }

    private void printReport()
    {
        TreeMap<Date, String> tmpMap = map;
        Entry<Date, String> entry;
        Date date;
        String report = "";
        while (!tmpMap.isEmpty())
        {
            entry = tmpMap.pollFirstEntry();
            date = entry.getKey();
            report = "--------------" + date.toString() + "--------------\n" + entry.getValue() + "\n";
        }

        
        logger.debugPrintf("%s\n", report);
        
    }

    private void init() throws Exception
    {

        addEvent(event.getAbort());

        addEvent(event.getAccepted());

        addEvent(event.getCancel());

        addEvent(event.getChangeACL());

        addEvent(event.getChkpt());

        addEvent(event.getClear());

        addEvent(event.getCollectionState());

        addEvent(event.getCondorError());

        addEvent(event.getCondorMatch());

        addEvent(event.getCondorReject());

        addEvent(event.getCondorResourceUsage());

        addEvent(event.getCondorShadowExited());

        addEvent(event.getCondorShadowStarted());

        addEvent(event.getCurDescr());

        addEvent(event.getDeQueued());

        addEvent(event.getDone());

        addEvent(event.getEnQueued());

        addEvent(event.getHelperCall());

        addEvent(event.getHelperReturn());

        addEvent(event.getListener());

        addEvent(event.getMatch());

        addEvent(event.getNotification());

        addEvent(event.getPBSDequeued());

        addEvent(event.getPBSDone());

        addEvent(event.getPBSError());

        addEvent(event.getPBSMatch());

        addEvent(event.getPBSPending());

        addEvent(event.getPBSQueued());

        addEvent(event.getPBSRerun());

        addEvent(event.getPBSResourceUsage());

        addEvent(event.getPBSRun());

        addEvent(event.getPending());

        addEvent(event.getPurge());

        addEvent(event.getReallyRunning());

        addEvent(event.getRefused());

        addEvent(event.getRegJob());

        addEvent(event.getResourceUsage());

        addEvent(event.getResubmission());

        addEvent(event.getResume());

        addEvent(event.getRunning());

        addEvent(event.getSuspend());

        addEvent(event.getTransfer());

        addEvent(event.getUserTag());

    }

    private void addEvent(AnyContentType any) throws Exception
    {
        if (any != null)
        {
            // debug("Adding " + any.getClass().getName());

            Class c = (any.getClass());

            // Spiros: Dead variables
            // Object inst = c.newInstance();
            // debug("New instance " + inst + "class name:"
            // + inst.getClass().getName());

            Method m[] = c.getMethods();
            String strEvents = "";
            String strHeaderEvent;
            Timeval timeStamp = null;
            for (int i = 0; i < m.length; i++)
            {
                // debug("All methods : " + m[i].toGenericString());
                if (m[i].getName().startsWith("get"))
                {
                    // debug("geters : " + m[i].toGenericString());
                    // debug("\t getReturnType: " +
                    // m[i].getReturnType().getName());

                    if (m[i].getReturnType().getName().equals("java.lang.String"))
                    {
                        // debug("---\t will call string : "+
                        // m[i].toGenericString());

                        String result = (String) m[i].invoke(any);
                        String[] tmp = m[i].getName().split("get");

                        if (result != null)
                        {
                            strEvents = strEvents + tmp[tmp.length - 1] + ":\t\t" + result + "\n";
                        }

                    }
                    else if (m[i].getReturnType().getName().equals("org.glite.wsdl.types.lb.Timeval")
                            && m[i].getName().equals("getTimestamp"))
                    {
                        timeStamp = (org.glite.wsdl.types.lb.Timeval) m[i].invoke(any);
                    }
                }
            }

            long time = timeStamp.getTvSec() * 1000 + timeStamp.getTvUsec() / 1000;
            Date date = Presentation.createDate(time);
            strHeaderEvent = "-------------" + any.getClass().getName() + "-------------\n";
            if (date == null)
            {
                date = Presentation.now();
            }
            map.put(date, (strHeaderEvent + strEvents));

            this.allReports = this.allReports + (date.toString() + strHeaderEvent + strEvents);
            // debug((strHeaderEvent+strEvents));
        }

    }
    
    
    @Override
    public boolean exists() throws VlException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getType()
    {
        return "WMSJobEvent";
    }

    @Override
    public InputStream getInputStream() throws VlException
    {

        // TreeMap<Date, String> tmpMap = map;
        // Entry<Date, String> entry;
        // Date date;
        // String report = "\t\t JOB EVENTS\n";
        // while (!tmpMap.isEmpty())
        // {
        // entry = tmpMap.pollFirstEntry();
        // if(entry != null){
        // date = entry.getKey();
        // report = report + "--------------" + date.toString()+
        // "--------------\n" + entry.getValue() + "\n";
        // this.allReports = this.allReports + "--------------"
        // +date.toString()+ "--------------\n" + entry.getValue() + "\n";
        // debug("Key: "+date);
        // debug("Value: "+entry.getValue());
        //                
        // }else{
        // continue;
        // }
        // }
        // if (StringUtil.isEmpty(report))
        // {
        // report = "No  Events here!!";
        // }

//        logger.debugPrintf("---------------%s\n", allReports);
        
        InputStream sis = null;
        try
        {
            sis = new ByteArrayInputStream(allReports.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new VlException("UnsupportedEncoding", e.getMessage(), e);
        }
        return sis;
    }
}
