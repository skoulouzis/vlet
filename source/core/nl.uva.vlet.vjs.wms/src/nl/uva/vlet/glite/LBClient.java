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
 * $Id: LBClient.java,v 1.33 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package nl.uva.vlet.glite;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.glite.WMLBConfig.LBConfig;
import nl.uva.vlet.grid.ssl.SSLHTTPSender;
import nl.uva.vlet.net.ssl.SSLContextManager;
import nl.uva.vlet.vrs.VRSContext;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.http.HTTPSender;
import org.glite.wms.wmproxy.BaseFaultType;
import org.glite.wsdl.services.lb.LoggingAndBookkeepingLocator;
import org.glite.wsdl.services.lb.LoggingAndBookkeepingPortType;
import org.glite.wsdl.types.lb.DoneCode;
import org.glite.wsdl.types.lb.Event;
import org.glite.wsdl.types.lb.GenericFault;
import org.glite.wsdl.types.lb.JobFlags;
import org.glite.wsdl.types.lb.JobFlagsValue;
import org.glite.wsdl.types.lb.JobStatus;
import org.glite.wsdl.types.lb.QueryAttr;
import org.glite.wsdl.types.lb.QueryConditions;
import org.glite.wsdl.types.lb.QueryOp;
import org.glite.wsdl.types.lb.QueryRecValue;
import org.glite.wsdl.types.lb.QueryRecord;
import org.glite.wsdl.types.lb.StatName;
import org.glite.wsdl.types.lb.StateEnterTimesItem;
import org.glite.wsdl.types.lb.Timeval;
import org.glite.wsdl.types.lb.holders.JobStatusArrayHolder;
import org.globus.axis.transport.HTTPSSender;
import org.globus.axis.util.Util;
import org.w3.www._2001.XMLSchema.holders.StringArrayHolder;
import org.w3c.dom.NodeList;

/**
 * Glite Logging and Bookkeeping (L&B) API. 
 *
 * @author Spirous Koulouzis, Piter T. de Boer
 */
public class LBClient
{
    public static class LBJobHolder
    {
        public String jobID;

        public JobStatus jobStatus;
    }

    // 4.1. QUERY LANGUAGE
    // The L&B query language is based on simple value assertions on job and
    // event attributes. There are two
    // types of queries based on the complexity of selection criteria, simple
    // and complex. Simple queries are
    // can be described by the following formula:
    // attr1 OP value1 ∧ · · · ∧ attrn OP valuen
    // where attri is attribute name, OP is one of the =, <, >, = and ∈
    // relational operators and value is single
    // value (or, in the case of ∈ operator, interval) from attribute type.
    // Complex queries can be described using the following formula:

    // (attr1 OP value1,1 ∨ · · · ∨ attr1 OP value1,i1 ) ∧ (attr2 OP value2,1 ∨
    // · · · ∨ attr2 OP value2,i2 )∧.....∧ (attrn OP valuen,1 ∨ · · · ∨ attrn OP
    // valuen,in )

    // The complex query can, in contrast to simple query, contain more
    // assertions on value of single attribute,
    // which are ORed together.
    // The query must always contain at least one attribute indexed on the L&B
    // server; this restriction is nec-
    // essary to avoid matching the selection criteria against all jobs in the
    // L&B database. The list of indexed
    // attributes for given L&B server can be obtained by L&B API call.

    // static imports ?
    public static DoneCode STATUS_DONE_OK = DoneCode.OK;

    public static DoneCode STATUS_DONE_CANCELLED = DoneCode.CANCELLED;

    public static DoneCode STATUS_DONE_FAILED = DoneCode.FAILED;

    public static final QueryAttr[] QueryAttributes = 
        { 
            QueryAttr.CHKPTTAG, 
            QueryAttr.DESTINATION, 
            QueryAttr.DONECODE,
            QueryAttr.EVENTTYPE, 
            QueryAttr.EXITCODE, 
            QueryAttr.HOST, 
            QueryAttr.INSTANCE, 
            QueryAttr.JOBID,
            QueryAttr.LOCATION, 
            QueryAttr.OWNER, 
            QueryAttr.PARENT, 
            QueryAttr.RESUBMITTED, 
            QueryAttr.SOURCE,
            QueryAttr.TIME, 
            QueryAttr.USERTAG 
        };

    private ArrayList<QueryAttr> indexedAttributes = new ArrayList<QueryAttr>();

    private LoggingAndBookkeepingLocator lbLocator;

    private void initTransport() throws Exception
    {
        SimpleProvider provider;

        boolean initMySSL = false;

        if (initMySSL)
        {
        	// ===========================================
        	// Almost works, but still is buggy. 
        	// ===========================================
            Properties sslProps = new Properties();
            sslProps.setProperty(SSLContextManager.PROP_SSL_PROTOCOL, "SSLv3");
            sslProps.setProperty(SSLContextManager.PROP_USE_PROXY_AS_IDENTITY,"true"); 
            
           // sslProps.setProperty(nl.uva.vlet.grid.ssl.SSLContextWrapper.PROP_INIT_PROXY_PRIVATE_KEY, "false");
            // sslProps.setProperty("axis.socketSecureFactory", "org.glite.security.trustmanager.axis.AXISSocketFactory");
            // sslProps.setProperty(nl.uva.vlet.grid.ssl.SSLContextWrapper.PROP_INIT_PROXY_PRIVATE_KEY,
            // "false");

            // sslProps.setProperty("axis.socketSecureFactory",
            // "org.glite.security.trustmanager.axis.AXISSocketFactory");
            if ((lbInfo != null) && (lbInfo.getProxyFilename() != null))
                sslProps.setProperty(SSLContextManager.PROP_CREDENTIALS_PROXY_FILE, lbInfo.getProxyFilename());

            provider = new SimpleProvider();
            org.apache.axis.Handler sslHandler = new SSLHTTPSender(sslProps);
            SimpleTargetedChain chain = new SimpleTargetedChain(sslHandler);
            provider.deployTransport("https", chain);
            chain = new SimpleTargetedChain(new HTTPSender());
            provider.deployTransport("http", chain);
        }
        else
        {
        	// ===================================
        	// Use Legacy Globus 4.1 HTTPSSender !
        	// ===================================
            provider = new SimpleProvider();
            // Use GLOBUS HTTPS !
            SimpleTargetedChain chain = new SimpleTargetedChain(new HTTPSSender());
            provider.deployTransport("https", chain);
            //chain = new SimpleTargetedChain(new HTTPSender());
            //provider.deployTransport("http", chain);
            // 
            Util.registerTransport();
         	// ===================================
        	// ===================================
        }

        lbLocator = new LoggingAndBookkeepingLocator();
        lbLocator.setEngine(new AxisClient(provider));

        AxisEngine engine = lbLocator.getEngine();
        MessageContext messageContext = new MessageContext(engine);
        messageContext.setTimeout(100);

        this.lbService = lbLocator.getLoggingAndBookkeeping(lbInfo.getServiceUri().toURL());

        //Stub stub=(Stub)lbService; 
        
    }

    /**
     * Create LB Service Client using the host+port but with the specified port.
     */
    public static LBClient createServiceFromJobUri(java.net.URI uri) throws Exception
    {
        return createService(uri.getHost(), WMLBConfig.LB_DEFAULT_PORT);
    }

    public static LBClient createService(String hostname, int port) throws Exception
    {
        return new LBClient(WMLBConfig.createLBConfig(hostname, port));
    }

    // ==============================================================
    // Instance
    // ==============================================================

    private String lbVersion;

    private LoggingAndBookkeepingPortType lbService;

    private LBConfig lbInfo;

    private NodeList childs;

    public LBClient(LBConfig lbConfig) throws Exception
    {
        this.lbInfo = lbConfig;

        initTransport();

        debug("New LBClient for:" + lbConfig.getServiceUri());
    }

    public LoggingAndBookkeepingPortType getService()
    {
        return this.lbService;
    }

    public String getVersion() throws GenericFault, RemoteException
    {
        if (lbVersion == null)
        {
            lbVersion = lbService.getVersion(null);
            if (lbVersion == null)
                lbVersion = "";
        }

        return lbVersion;
    }

    public JobStatus getStatus(java.net.URI joburi) throws GenericFault, RemoteException
    {
        JobFlags flags = new JobFlags();

        flags.setFlag(new JobFlagsValue[] { JobFlagsValue.CLASSADS, JobFlagsValue.CHILDREN, JobFlagsValue.CHILDSTAT });

        
        JobStatus status = null;
        int tryCount=2;
        
        while ((status==null) && (tryCount>0)) 
        {
        	tryCount--; 
	        try
	        {
	        	status=lbService.jobStatus(joburi.toString(), flags);
	        }
	        catch (Exception e)
	        {
	        	;; // ignore one exception 
	        }
        }
        
        String lbServiceVersion = getVersion();

        // if LB version < 1.7.1 fix the status names
        String[] versionParts = lbServiceVersion.split("\\.");
        if (Integer.parseInt(versionParts[0]) == 1
                && (Integer.parseInt(versionParts[1]) < 7 || (Integer.parseInt(versionParts[1]) == 7 && Integer
                        .parseInt(versionParts[2]) < 1)))
        {
            // logger.debug("Using fix for LB job status history");
            status = fixJobStatus171(status);
        }

        return status;
    }

    private void debug(String str)
    {
//        Global.errorPrintf(this, "LBClient:%s\n", str);
        Global.debugPrintf(this, "LBClient:%s\n", str);
    }

    public static void printStatus(JobStatus stat, PrintStream out)
    {
        out.println(" --- Job ---");
        out.println(" jobid                     = " + stat.getJobId());
        out.println(" globus id                 = " + stat.getGlobusId());
        out.println(" state                     = " + stat.getState());
        out.println(" DoneCode                  = " + stat.getDoneCode().getValue());
        out.println(" ExitCode                  = " + stat.getExitCode());
        out.println(" Failure Reasons           = " + stat.getFailureReasons());
        out.println(" Owner                     = " + stat.getOwner());
        out.println(" CE Node                   = " + stat.getCeNode());
        out.println(" ACL                       = " + stat.getAcl());
        out.println(" Cancel Reason             = " + stat.getCancelReason());
        out.println(" Children Num              = " + stat.getChildrenNum());
        // out.println(" Condor Dest Host          = " +
        // stat.getCondorDestHost());
        // out.println(" Condor Error Desc         = " +
        // stat.getCondorErrorDesc());
        // out.println(" Condor Id                 = " + stat.getCondorId());
        // out.println(" Condor Jdl                = " + stat.getCondorJdl());
        // out.println(" Condor Job Exit Status    = " +
        // stat.getCondorJobExitStatus());
        // out.println(" Condor Job Pid            = " +
        // stat.getCondorJobPid());
        // out.println(" Condor Owner              = " + stat.getCondorOwner());
        // out.println(" Condor Preempting         = " +
        // stat.getCondorPreempting());
        // out.println(" Condor Reason             = " +
        // stat.getCondorReason());
        // out.println(" Condor Shadow Exit Status = " +
        // stat.getCondorShadowExitStatus());
        // out.println(" Condor Starter Pid        = " +
        // stat.getCondorStarterPid());
        // out.println(" Condor Status             = " +
        // stat.getCondorStatus());
        // out.println(" Condor Universe           = " +
        // stat.getCondorUniverse());
        out.println(" Destination               = " + stat.getDestination());
        out.println(" JDL                       = " + stat.getJdl());
        out.println(" Local Id                  = " + stat.getLocalId());
        out.println(" Location                  = " + stat.getLocation());
        out.println(" Matched                   = " + stat.getMatchedJdl());
        out.println(" Network Server            = " + stat.getNetworkServer());
        out.println(" Parent Job                = " + stat.getParentJob());
        out.println(" [ attributes ] ");
        out.println(" cpu time        = " + stat.getCpuTime());
    }

    public static String jobStatusToString(JobStatus status)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("State:      " + status.getState() + "\n");
        sb.append("Job ID:     " + status.getJobId() + "\n");
        sb.append("Owner:      " + status.getOwner() + "\n");
        sb.append("Job type:   " + status.getJobtype() + "\n");
        sb.append("Destination:   " + status.getLocation() + "\n");
        sb.append("Done code:  " + status.getDoneCode() + "\n");
        sb.append("User tags:  ");
        // if there are some user tags write it out.
        if (status.getUserTags() != null)
        {
            for (int i = 0; i < status.getUserTags().length; i++)
            {
                if (i == 0)
                {
                    sb.append(status.getUserTags()[i].getTag() + " = " + status.getUserTags()[i].getValue() + "\n");
                }
                else
                    sb.append("            " + status.getUserTags()[i].getTag() + " = "
                            + status.getUserTags()[i].getValue() + "\n");
            }
        }
        else
            sb.append("\n");
        // Write the time info in a readable form.
        Calendar calendar = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        calendar.setTimeInMillis(status.getStateEnterTime().getTvSec() * 1000);

        sb.append("Enter time: " + df.format(calendar.getTime()) + "\n");
        calendar.setTimeInMillis(status.getLastUpdateTime().getTvSec() * 1000);
        sb.append("Last update time: " + df.format(calendar.getTime()) + "\n");
        return sb.toString();
    }

    /*
     * Copyright 2008-2009 Oleg Sukhoroslov
     * 
     * Licensed under the Apache License, Version 2.0 (the "License"); you may
     * not use this file except in compliance with the License. You may obtain a
     * copy of the License at
     * 
     * http://www.apache.org/licenses/LICENSE-2.0
     * 
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
     * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
     * License for the specific language governing permissions and limitations
     * under the License.
     */
    public static JobStatus fixJobStatus171(JobStatus status)
    {
        StateEnterTimesItem[] states = status.getStateEnterTimes();
        StatName prevState = StatName.SUBMITTED;
        StatName temp = null;

        for (StateEnterTimesItem state : states)
        {
            temp = state.getState();
            state.setState(prevState);
            prevState = temp;
        }

        JobStatus[] children = status.getChildrenStates();
        if (children != null && children.length > 0)
        {
            JobStatus[] fixedChildren = new JobStatus[children.length];
            for (int i = 0; i < children.length; i++)
            {
                fixedChildren[i] = fixJobStatus171(children[i]);
            }
            status.setChildrenStates(fixedChildren);
        }
        return status;
    }

    /**
     * Get all jobs for authenticated user registered at this LBClient.
     * 
     * @throws Exception
     */
    public LBJobHolder[] getUserJobs() throws Exception
    {
        // Cannot use stub.userJobs() because not yet implemented (version > 2.0
        // needed)
        boolean newVersion = false;

        String strVersion = getVersion();
        String[] strVersionParts = strVersion.split("[.]");

        Integer ver = Integer.valueOf(strVersionParts[0]);

        if (ver >= 2)
        {
            newVersion = true;
        }

        StringArrayHolder jobNativeIdResult = new StringArrayHolder();
        JobStatusArrayHolder jobStatusResult = new JobStatusArrayHolder();

        if (newVersion)
        {
            this.lbService.userJobs(jobNativeIdResult, jobStatusResult);
        }
        else
        {
            // get Jobs Status
            JobFlagsValue[] jobFlagsValue = new JobFlagsValue[1];
            jobFlagsValue[0] = JobFlagsValue.CLASSADS;

            JobFlags jobFlags = new JobFlags(jobFlagsValue, null);

            QueryConditions[] queryConditions = new QueryConditions[2];

            queryConditions[0] = new QueryConditions();
            queryConditions[0].setAttr(QueryAttr.JOBID);
            QueryRecord[] qR0 = new QueryRecord[1];
            QueryRecValue value0 = new QueryRecValue();
            value0.setC("https://" + this.getHostname() + "/");
            qR0[0] = new QueryRecord(QueryOp.UNEQUAL, value0, null);
            queryConditions[0].setRecord(qR0);

            queryConditions[1] = new QueryConditions();
            queryConditions[1].setAttr(QueryAttr.OWNER);
            QueryRecord[] qR1 = new QueryRecord[1];
            QueryRecValue value1 = new QueryRecValue();
            String x509Subject = VRSContext.getDefault().getGridProxy().getSubject();
            value1.setC(x509Subject);
            qR1[0] = new QueryRecord(QueryOp.EQUAL, value1, null);
            queryConditions[1].setRecord(qR1);

            try
            {
                this.lbService.queryJobs(queryConditions, jobFlags, jobNativeIdResult, jobStatusResult);
            }
            catch (RemoteException e)
            {
                // parse exception: 
                //WMSException wmex=WMSUtil.convertException("Querying User Jobs for:"+getHostname()+":"+getPort(), e); 
                
                if (e instanceof AxisFault)
                {
                    AxisFault fault=(AxisFault)e; 
                    Integer code = getErrorCode(fault);
                    debug("ErrorCode: " + code);
                   
                    // what is code 2 ? 
                    if (code != 2)
                    {
                        // todo create better readable exception:
                        throw e; 
                    }

                }
                else
                {
                    // GenericFault
                    throw e; 
                }
            }
        }
        LBJobHolder[] jobHolder = null;
        if (jobNativeIdResult.value != null)
        {
            jobHolder = new LBJobHolder[jobNativeIdResult.value.length];
            for (int i = 0; i < jobHolder.length; i++)
            {
                jobHolder[i] = new LBJobHolder();
                jobHolder[i].jobID = jobNativeIdResult.value[i];
            }

        }
        else if (jobStatusResult.value != null)
        {
            jobHolder = new LBJobHolder[jobStatusResult.value.length];
            for (int i = 0; i < jobHolder.length; i++)
            {
                jobHolder[i] = new LBJobHolder();
                jobHolder[i].jobStatus = jobStatusResult.value[i];
                jobHolder[i].jobID = jobStatusResult.value[i].getJobId();
            }
        }

        return jobHolder;
    }

    public Event[] getAllJobEvents(java.net.URI jobUri) throws Exception
    {

        debug("Events for: " + jobUri);

        QueryRecValue jobIdValue = new QueryRecValue(null, jobUri.toString(), null);
        QueryRecord[] jobIdRec = new QueryRecord[] { new QueryRecord(QueryOp.EQUAL, jobIdValue, null) };
        QueryConditions[] query = new QueryConditions[] { new QueryConditions(QueryAttr.JOBID, null, null, jobIdRec) };

        QueryRecValue levelValue = new QueryRecValue(0, null, null);
        QueryRecord[] levelRec = new QueryRecord[] { new QueryRecord(QueryOp.GREATER, levelValue, null) };
        QueryConditions[] queryEvent = new QueryConditions[] { new QueryConditions(QueryAttr.EVENTTYPE, null, null,
                levelRec) };

        try
        {
            return lbService.queryEvents(query, queryEvent);
        }
        catch (Exception e)
        {
            if (e instanceof AxisFault)
            {
                Integer code = getErrorCode((AxisFault) e);
                // If code == 2 then there are no events.Return Null
                if (code == 2)
                {
                    return null;
                }
                else
                {
                    throw e;
                }
            }

        }
        return null;
    }

    public LBJobHolder[] getUserJobs(Timeval from, Timeval to) throws Exception
    {
        // LBJobHolder[] userJobs = this.getUserJobs();
        //
        // List<QueryRecord> recList = new ArrayList<QueryRecord>();
        // for (int i = 0; i < userJobs.length; i++)
        // {
        // QueryRecValue jobIdValue = new QueryRecValue(null, userJobs[i].jobID,
        // null);
        // debug("jobIdValue: " + jobIdValue.getC());
        // recList.add(new QueryRecord(QueryOp.EQUAL, jobIdValue, null));
        // }
        //        
        // QueryRecord[] jobIdRec = new QueryRecord[] {};
        // jobIdRec = recList.toArray(jobIdRec);
        //
        // QueryConditions condOnJobid = new QueryConditions(QueryAttr.JOBID,
        // null, null, jobIdRec);
        //
        // // create query record for time
        // QueryRecValue timeFrom = new QueryRecValue(null, null, from);
        // QueryRecValue timeTo = new QueryRecValue(null, null, to);
        // QueryRecord[] timeRec = new QueryRecord[] { new
        // QueryRecord(QueryOp.WITHIN, timeFrom, timeTo) };
        // // create QueryConditions instance representing this formula:
        // // (TIME is in <timeFrom, timeTo> interval)
        // QueryConditions condOnTime = new QueryConditions(QueryAttr.TIME,
        // null, null, timeRec);
        //
        // // create QueryConditions list representing this formula:
        // // (JOBID='jobId1' or JOBID='jobId2 or ...) AND (TIME is in
        // <timeFrom,
        // // timeTo> interval)
        // // where jobId1,... are ids of user's jobs
        // List<QueryConditions> condList = new ArrayList<QueryConditions>();
        // condList.add(condOnJobid);
        // condList.add(condOnTime);
        //
        // QueryConditions[] queryconditions = new QueryConditions[] {};
        // queryconditions = condList.toArray(queryconditions);
        //
        // StringArrayHolder stringarrayholder = new StringArrayHolder();
        //
        // JobStatusArrayHolder jobstatusarrayholder = new
        // JobStatusArrayHolder();
        //
        // // get all jobs matching the given conditions
        // lbService.queryJobs(queryconditions, null, stringarrayholder,
        // jobstatusarrayholder);
        //
        // return jobstatusarrayholder.value;

        // get Jobs Status
        JobFlagsValue[] jobFlagsValue = new JobFlagsValue[1];
        jobFlagsValue[0] = JobFlagsValue.CLASSADS;

        JobFlags jobFlags = new JobFlags(jobFlagsValue, null);

        QueryConditions[] queryConditions = new QueryConditions[3];

        queryConditions[0] = new QueryConditions();
        queryConditions[0].setAttr(QueryAttr.JOBID);
        QueryRecord[] qR0 = new QueryRecord[1];
        QueryRecValue value0 = new QueryRecValue();
        value0.setC("https://" + this.getHostname() + "/");
        qR0[0] = new QueryRecord(QueryOp.UNEQUAL, value0, null);
        queryConditions[0].setRecord(qR0);

        queryConditions[1] = new QueryConditions();
        queryConditions[1].setAttr(QueryAttr.TIME);
        QueryRecord[] qR1 = new QueryRecord[1];
        QueryRecValue value1 = new QueryRecValue();
        String x509Subject = VRSContext.getDefault().getGridProxy().getSubject();
        value1.setC(x509Subject);
        qR1[0] = new QueryRecord(QueryOp.WITHIN, value1, null);
        queryConditions[1].setRecord(qR1);

        queryConditions[2] = new QueryConditions();
        queryConditions[2].setAttr(QueryAttr.TIME);
        QueryRecord[] qR2 = new QueryRecord[1];
        QueryRecValue timeFrom = new QueryRecValue();
        timeFrom.setT(from);

        QueryRecValue timeTo = new QueryRecValue();
        timeTo.setT(to);

        qR2[0] = new QueryRecord(QueryOp.WITHIN, timeFrom, timeTo);
        queryConditions[2].setRecord(qR2);

        StringArrayHolder jobNativeIdResult = new StringArrayHolder();
        JobStatusArrayHolder jobStatusResult = new JobStatusArrayHolder();
        try
        {
            lbService.queryJobs(queryConditions, jobFlags, jobNativeIdResult, jobStatusResult);
        }
        catch (RemoteException e)
        {
            if (e instanceof AxisFault)
            {
                Integer code = getErrorCode((AxisFault) e);
                debug("ErrorCode: " + code);

                if (code != 2)
                {
                    throw e;
                }
            }
        }

        LBJobHolder[] jobHolder = null;
        if (jobNativeIdResult.value != null)
        {
            jobHolder = new LBJobHolder[jobNativeIdResult.value.length];
            for (int i = 0; i < jobHolder.length; i++)
            {
                jobHolder[i] = new LBJobHolder();
                jobHolder[i].jobID = jobNativeIdResult.value[i];
            }

        }
        else if (jobStatusResult.value != null)
        {
            jobHolder = new LBJobHolder[jobStatusResult.value.length];
            for (int i = 0; i < jobHolder.length; i++)
            {
                jobHolder[i] = new LBJobHolder();
                jobHolder[i].jobStatus = jobStatusResult.value[i];
                jobHolder[i].jobID = jobStatusResult.value[i].getJobId();
            }
        }

        return jobHolder;
    }

    public QueryAttr[] getIndexedAtrributes() throws Exception
    {

        if (indexedAttributes.isEmpty())
        {
            JobFlagsValue[] jobFlagsValue = new JobFlagsValue[1];
            jobFlagsValue[0] = JobFlagsValue.CLASSADS;

            JobFlags jobFlags = new JobFlags(jobFlagsValue, null);

            QueryConditions[] queryConditions = new QueryConditions[1];

            StringArrayHolder jobNativeIdResult = new StringArrayHolder();
            JobStatusArrayHolder jobStatusResult = new JobStatusArrayHolder();

            for (int i = 0; i < QueryAttributes.length; i++)
            {

                debug("QueryAttributes: " + QueryAttributes[i]);

                queryConditions[0] = new QueryConditions();
                QueryRecord[] qR0 = new QueryRecord[1];
                QueryRecValue value0 = new QueryRecValue();
                value0.setC("");
                qR0[0] = new QueryRecord(QueryOp.UNEQUAL, value0, null);
                queryConditions[0].setRecord(qR0);
                queryConditions[0].setAttr(QueryAttributes[i]);
                try
                {
                    lbService.queryJobs(queryConditions, jobFlags, jobNativeIdResult, jobStatusResult);
                }
                catch (Exception e)
                {
                    if (e instanceof AxisFault)
                    {
                        Integer code = getErrorCode((AxisFault) e);

                        addIndexedAttributes(code, QueryAttributes[i]);
                    }
                    else
                    {
                        throw e;
                    }

                }
            }
        }

        QueryAttr[] indexedAtrr = new QueryAttr[indexedAttributes.size()];
        indexedAtrr = indexedAttributes.toArray(indexedAtrr);

        return indexedAtrr;

    }

    private void addIndexedAttributes(Integer code, QueryAttr queryattributes2)
    {

        switch (code)
        {
            case 1416:
                break;

            case 1415:
                break;

            default:
                if (!indexedAttributes.contains(queryattributes2))
                {
                    indexedAttributes.add(queryattributes2);
                }

                break;
        }

    }

    private Integer getErrorCode(AxisFault axisF) 
    {
    	if ((axisF instanceof BaseFaultType)==false) 
    		return -1; 
    	
    	// Only base fault types contain errorcodes: 
    	BaseFaultType e=(BaseFaultType)axisF; 
    	
    	String intstr=e.getErrorCode();
    	if (StringUtil.isEmpty(intstr))
    		return -1;
    	
    	try
    	{
    		return Integer.parseInt(intstr);	
    	}
    	catch (Exception ex)
    	{
    		return -1; 
    	}
    	
    }

    public JobStatus[] getUserJobsExp() throws Exception
    {
        JobFlagsValue[] jobFlagsValue = new JobFlagsValue[1];
        jobFlagsValue[0] = JobFlagsValue.CLASSADS;

        JobFlags jobFlags = new JobFlags(jobFlagsValue, null);

        QueryConditions[] queryConditions = new QueryConditions[1];

        queryConditions[0] = new QueryConditions();
        queryConditions[0].setAttr(QueryAttr.USERTAG);
        QueryRecord[] qR0 = new QueryRecord[1];
        QueryRecValue value0 = new QueryRecValue();

        value0.setC("/O=dutchgrid/O=users/O=uva/OU=wins/CN=Spyridon Koulouzis");
        qR0[0] = new QueryRecord(QueryOp.EQUAL, value0, null);
        queryConditions[0].setRecord(qR0);

        StringArrayHolder jobNativeIdResult = new StringArrayHolder();
        JobStatusArrayHolder jobStatusResult = new JobStatusArrayHolder();

        try
        {
            lbService.queryJobs(queryConditions, jobFlags, jobNativeIdResult, jobStatusResult);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            if (e instanceof AxisFault)
            {
                Integer code = getErrorCode((AxisFault) e);
                debug("ErrorCode: " + code);

                if (code != 2)
                {
                    throw e;
                }
            }
        }

        return jobStatusResult.value;
    }

    /**
     * @return the serviceURL
     * @throws MalformedURLException
     */
    public java.net.URI getServiceUri()
    {
        return this.lbInfo.getLBUri();
    }

    public String getHostname()
    {
        return this.lbInfo.getHostname();
    }

    public int getPort()
    {
        return this.lbInfo.getPort(); 
    }
    
    public String toString()
    {
        return "lblcient:" + this.getServiceUri();
    }

    public JobStatus[] getUserJobStatusses() throws Exception
    {
        JobStatus[] jobStatus = null;
        LBJobHolder[] jobs = getUserJobs();
        if (jobs != null)
        {
            jobStatus = new JobStatus[jobs.length];
            for (int i = 0; i < jobs.length; i++)
            {
                if (jobs[i].jobStatus == null)
                {
                    jobs[i].jobStatus = getStatus(new URI(jobs[i].jobID));
                }

                jobStatus[i] = jobs[i].jobStatus;
            }
        }

        return jobStatus;
    }
}
