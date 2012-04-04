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
 * $Id: WMSFactory.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import java.util.Vector;

import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuConstants;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vjs.VJS;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VResourceSystem;

public class WMSFactory extends VRSFactory
{
    public static String schemes[] = { VJS.WMS_SCHEME, VJS.LB_SCHEME };

    @Override
    public String getName()
    {
        return "WMS";
    }

    @Override
    public void clear()
    {
        // Clear classes and stop all threads !
        WMSResource.stopAndDisposeAll();
        LBJobCache.stopAndDisposeAll();
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemes;
    }

    protected ServerInfo updateServerInfo(VRSContext context, ServerInfo info, VRL loc) throws VlException
    {
        // create defaults:
        if (info == null)
        {
            info = ServerInfo.createFor(context, loc);
        }
        info.setDefaultHomePath("/"); 
        
        return info;
    }

    @Override
    public Vector<ActionMenuMapping> getActionMenuMappings()
    {
        String[] types = { VJS.TYPE_VJOB };
        
        Vector<ActionMenuMapping> mappings = new Vector<ActionMenuMapping>();

        ActionMenuMapping cancelMapping = new ActionMenuMapping("cancelJob", "Cancel Job", "wms");
        cancelMapping.addResourceMapping(types, schemes, null, null, ActionMenuConstants.SELECTION_NONE);
        mappings.add(cancelMapping);

        ActionMenuMapping menuMapping = new ActionMenuMapping("purgeJob", "Purge Job", "wms");
        menuMapping.addResourceMapping(types, schemes, null, null, ActionMenuConstants.SELECTION_NONE);
        mappings.add(menuMapping);

        menuMapping = new ActionMenuMapping("purgeJobs", "Purge Selection", "wms");
        menuMapping.addSelectionTypeSchemeMapping(types, schemes, ActionMenuConstants.SELECTION_ONE_OR_MORE);
        mappings.add(menuMapping);
        
        menuMapping = new ActionMenuMapping("cancelJobs", "Cancel Selection", "wms");
        menuMapping.addSelectionTypeSchemeMapping(types, schemes, ActionMenuConstants.SELECTION_ONE_OR_MORE);
        mappings.add(menuMapping);

        ActionMenuMapping mapping;

        mapping = new ActionMenuMapping("updateStatuses", "Update Job Statuses", "wms");
        // both "myjobs"  as "LBResource" have child jobs //
        types = new String[] { WMSConstants.TYPE_MYJOBS , WMSConstants.TYPE_LBRESOURCE};
        mapping.addResourceMapping(types, schemes, null, null, ActionMenuConstants.SELECTION_NONE);
        mappings.add(mapping);

        mapping = new ActionMenuMapping("queryJobsFull", "Requery User Jobs", "wms");
        types = new String[] { WMSConstants.TYPE_MYJOBS , WMSConstants.TYPE_LBRESOURCE};
        mapping.addResourceMapping(types, schemes, null, null, ActionMenuConstants.SELECTION_NONE);
        mappings.add(mapping);

        return mappings;
    }

    @Override
    public void performAction(ITaskMonitor monitor, VRSContext vrsContext, String methodName,
            ActionContext actionContext) throws VlException
    {
        
        // The Actual Selection do perform the action with !
        VRL selections[] = actionContext.getSelections();

        // ===
        // The source is NOT part of the selection. It is the origin
        // of the Action Click in the VBrowser.
        // ===
        VRL source = actionContext.getSource();

        System.err.println("method '"+methodName+"' on:"+source); 

        if (methodName == null)
            return;

        WMSResource wms = WMSResource.getFor(vrsContext, source);

        // single selection are right clicks directly on the resource
        if (methodName.compareTo("cancelJob") == 0)
        {
            VNode resource = wms.openLocation(source);
            if (resource instanceof WMSJob)
            {
                WMSJob job = (WMSJob) resource;
                job.cancel();
            }
        }
        else if (methodName.compareTo("purgeJob") == 0)
        {

            VNode resource = wms.openLocation(source);
            if (resource instanceof WMSJob)
            {
                WMSJob job = (WMSJob) resource;
                monitor.startTask("Purging job:" + source, 1);
                job.purge(monitor);
                monitor.updateWorkDone(1);
                monitor.endTask("Purging job:" + source);
            }
        }
        else if (methodName.compareTo("purgeJobs") == 0)
        {
            multiPurgeJobs(vrsContext, monitor, selections);
        }
        else if (methodName.compareTo("getCollectionTemplate") == 0)
        {
            // wms.getCollectionTemplate(jobNumber, requirements, rank);
        }
        else if (methodName.compareTo("updateStatuses") == 0)
        {
            // Full Status Update:
            wms.updateStatuses(monitor, true);
        }
        else if (methodName.compareTo("queryJobsFull") == 0)
        {
            wms.getUserJobs(monitor, true); // Full Query Update
        }
        else
            throw new VlException("No such method","Can't execute method "+methodName+" on :"+source); 

    }

    public static void multiPurgeJobs(VRSContext context, ITaskMonitor monitor, VRL[] selections) throws VlException
    {
        VlException lastEx = null;
        int numPurged = 0;
        int numFailed = 0;
        int index = 0;

        int num = selections.length;
        monitor.startTask("Purging #" + num + " Jobs", num);

        for (VRL jobVrl : selections)
        {
            String subStr = "Purging job:" + jobVrl;

            try
            {
                WMSJob job = null;
                // JobUris might be registered at different WMSs!
                WMSResource wms = WMSResource.getFor(context, jobVrl);

                if (wms == null)
                {
                    monitor.logPrintf("*** Error not a (WMS) Job:" + jobVrl + "\n");
                    continue;
                }

                VNode node = wms.openLocation(jobVrl);

                if (node instanceof WMSJob)
                {
                    job = (WMSJob) node;
                    job.purge(monitor);
                    numPurged++;
                }
                else
                {
                    monitor.logPrintf("*** Error not a (WMS) Job:" + jobVrl + "\n");
                }
            }
            catch (VlException e)
            {
                lastEx = e;
                numFailed++; // but do others first;

            }
            monitor.updateSubTaskDone(1); // update status!
            monitor.endSubTask(subStr);
            monitor.updateWorkDone(++index);
        }

        monitor.endTask("Purging Jobs");

        if (lastEx != null)
        {
            throw new VlException("WMSException", "Failed to purge some jobs.\n" + "jobs purged=" + numPurged
                    + ", jobs failed =" + numFailed, lastEx);
        }

    }

	@Override
	public VResourceSystem createNewResourceSystem(VRSContext context,
			ServerInfo info, VRL location) throws VlException 
	{
		if (location.hasScheme(VJS.LB_SCHEME))
            return LBResource.getFor(context, location);
        else if (location.hasScheme(VJS.WMS_SCHEME))
            return WMSResource.getFor(context, location);
        else
            return WMSResource.getFor(context, location);
	}
}
