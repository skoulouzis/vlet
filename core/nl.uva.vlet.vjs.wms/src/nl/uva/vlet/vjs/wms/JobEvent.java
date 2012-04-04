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
 * $Id: JobEvent.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import org.glite.wsdl.types.lb.JobStatus;

public class JobEvent
{
    public static enum JobEventType
    {
        NEW_JOB, UPDATE_STATUS, UPDATE_USERJOBS;
    }

    private long eventTime = -1;

    private JobEventType type;

    private String jobIds[];

    private JobStatus jobStatus;

    private JobEvent(JobEventType type)
    {
        eventTime = System.currentTimeMillis();
        this.type = type;
    }

    public JobEventType getType()
    {
        return type;
    }

    public long getTime()
    {
        return this.eventTime;
    }

    public boolean isType(JobEventType type)
    {
        return (this.type == type);
    }

    public JobStatus getJobStatus()
    {
        return this.jobStatus;
    }

    public String getStatusString()
    {
        if (this.jobStatus == null)
            return null;

        return jobStatus.getState().getValue();
    }

    public String getJobID()
    {
        if (jobIds == null)
            return null;
        return jobIds[0];
    }

    public String[] getUserJobs()
    {
        return jobIds;
    }

    public String toString()
    {
        String id = getJobID();
        String info = getStatusString();

        if (this.type == JobEventType.UPDATE_USERJOBS)
        {
            id = "<first id>" + id;
            info = "#" + this.jobIds.length + " userjobs";
        }

        return "<JobEvent>{" + type + ":" + id + ":" + info + "}";
    }

    public static JobEvent createStatusEvent(JobStatus status)
    {
        if (status == null)
            throw new NullPointerException("JobStatus can not be NULL");

        JobEvent event = new JobEvent(JobEventType.UPDATE_STATUS);
        event.jobIds = new String[] { status.getJobId() };
        event.jobStatus = status;
        return event;
    }

    public static JobEvent createNewJobEvent(String id)
    {
        if (id == null)
            throw new NullPointerException("JobId can not be NULL");

        JobEvent event = new JobEvent(JobEventType.NEW_JOB);
        event.jobStatus = null;
        event.jobIds = new String[] { id };
        return event;
    }

    public static JobEvent createUpdateUserJobs(String[] jobarr)
    {
        if (jobarr == null)
            throw new NullPointerException("JobId array can not be NULL");

        JobEvent event = new JobEvent(JobEventType.UPDATE_USERJOBS);
        event.jobIds = jobarr;
        return event;
    }
}
