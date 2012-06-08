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
 * $Id: WMSUtil.java,v 1.15 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package nl.uva.vlet.glite;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.TimeZone;

import nl.uva.vlet.glite.Util.StringHolder;
import nl.uva.vlet.glite.WMLBConfig.LBConfig;

import org.apache.axis.AxisFault;
import org.glite.wms.wmproxy.BaseFaultType;
import org.glite.wms.wmproxy.JobIdStructType;
import org.glite.wsdl.types.lb.JobStatus;
import org.glite.wsdl.types.lb.StatName;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WMSUtil
{
    public static final String JDL_MIMETYPE = "application/glite-jdl";

    private static final String monthStr[] = { "Jan", "Feb", "March", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec" };

    private static final String dayStr[] = { "Sun", "Mon", "Tue", "Wedn", "Thu", "Fri", "Sat" };

    public static boolean equalsIgnoreCase(String val1,String val2)
    {
    	if (val1==null)
    		if (val2==null)
    			return true;
    		else 
    			return false;
    	
    	return (val1.compareToIgnoreCase(val2)==0); 
    }
    
    public static boolean statusIsReady(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.READY.getValue());
    }

    public static boolean statusIsCancelled(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.CANCELLED.getValue());
    }

    public static boolean statusIsDone(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.DONE.getValue());
    }

    public static boolean statusIsAborted(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.ABORTED.getValue());
    }

    public static boolean statusIsCleared(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.CLEARED.getValue());
    }

    public static boolean statusIsSubmitted(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.SUBMITTED.getValue());
    }

    public static boolean statusIsRunning(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.RUNNING.getValue());
    }

    public static boolean statusIsUnknown(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.UNKNOWN.getValue());
    }

    public static boolean statusIsWaiting(StatName status)
    {
        return equalsIgnoreCase(status.getValue(), StatName.WAITING.getValue());
    }

    public static WMSException convertException(String actionStr, Throwable e)
    {
        String exName = "Exception";
        String faultMsg = "";
        
        StringHolder fcodeH=new StringHolder(); 
        StringHolder ftextH=new StringHolder(); 
        StringHolder fdescrH=new StringHolder(); 
       
        // Determine super type !
        if (e instanceof org.apache.axis.AxisFault)
        {
            exName = "AxisFault";
            AxisFault axisF = (AxisFault) e;

            // check super types:
            if (e instanceof org.glite.wms.wmproxy.BaseFaultType)
            {
                // Get WMS Message
                exName = "BaseFault";
                faultMsg = WMSUtil.createBaseFaultMessage((BaseFaultType) e);
            }
            else
            { 
                // parse GenericFault && AxisFault:
                if (e instanceof org.glite.wsdl.types.lb.GenericFault)
                {
                    exName = "GenericFault";
                }
                
                // Create Default Fault Message containing parsed details;
                // Somehow LB Exceptions aren't a proper "FaultType"
                Element[] els = axisF.getFaultDetails();
              
                
                // parse fault: 
                String fullMsg="";
                
                for (Element el : els)
                    fullMsg += parseFaultXML(el,fcodeH,ftextH,fdescrH);
                // if there is a text and a description, use fault information:
                if ( (ftextH.getValue()!=null) && (fdescrH.getValue()!=null))
                {
                    faultMsg+= "FaultCode       :"+fcodeH.getValue()
                            +"\nFaultText       :"+ftextH.getValue()
                            +"\nFaultDescription:"+fdescrH.getValue();
                }
                else
                {
                    // BaseFaultType is subclass of AxisFault.
                    String dump = axisF.dumpToString();
                    if (faultMsg.endsWith("\n")==false)
                        faultMsg+="\n";
                    faultMsg += fullMsg+"[Axis Fault Dump]\n" + dump;
                }
                // +
                // "(<"+"Exception class="+e.getClass().getCanonicalName()+">)\n"+
                // dump;
            }
        }

        // Check some WMS Exceptions. These are subclass of AxisFault!
        if (e instanceof org.glite.wms.wmproxy.ServerOverloadedFaultType)
        {
            exName = "ServerOverloaded";
        }
        else if (e instanceof org.glite.wms.wmproxy.AuthorizationFaultType)
        {
            exName = "AuthorizationFault";
        }
        else if (e instanceof org.glite.wms.wmproxy.AuthenticationFaultType)
        {
            exName = "AuthenticationFault";
        }
        else if (e instanceof org.glite.wms.wmproxy.JobUnknownFaultType)
        {
            exName = "JobUnknown";
        }
        else if (e instanceof org.glite.wms.wmproxy.OperationNotAllowedFaultType)
        {
            exName = "OperationNotAllowed";
        }

        // Full error message text:
        // 
        // "While perform query on ...\n"
        // "--- [Exception] ---\n"
        // "Details...\n"
        String msgStr=actionStr + "\n[" + exName + "]\n" + faultMsg;
        
        return new WMSException(msgStr, fcodeH.getValue(), ftextH.getValue(), fdescrH.getValue(), e);

    }

    // create xml string
    public static String xmltostring(String indent, Node node)
    {
        if (indent == null)
            indent = "";

        String str = "";

        short type = node.getNodeType();
        str += indent + " <" + node.getNodeName() + "(#" + type + ")" + "='" + node.getNodeValue() + "'>\n";

        NamedNodeMap attrs = node.getAttributes();

        if (attrs != null)
            for (int i = 0; i < attrs.getLength(); i++)
            {
                Node attr = attrs.item(i);
                type = attr.getNodeType();
                str += indent + "  + " + attr.getNodeName() + "(#" + type + ")" + "='" + attr.getNodeValue() + "'\n";
            }

        if (node instanceof Element)
        {
            Element el = (Element) node;

            NodeList childs = el.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++)
            {
                str += xmltostring(indent + "   ", childs.item(i));
            }
        }

        return str;
    }

    // Parse Fault XML
    private static String parseFaultXML(Node node, 
            StringHolder codeHolder,
            StringHolder textHolder,
            StringHolder descriptionHolder)
    {
        String str = "";

        String name = node.getNodeName();
        // Spiros:Dead variable
        // short type=node.getNodeType();
        String value ; //= node.getNodeValue();
        value = node.getTextContent();

        if (name.equals("code"))
        {
            str += "code       : " + value + "\n";
            codeHolder.setValue(value); 
        }
        else if (name.equals("text"))
        {
            str += "text       : " + value + "\n";
            textHolder.setValue(value); 
        }
        else if (name.equals("description"))
        {
            str += "description: " + value + "\n";
            descriptionHolder.setValue(value);
        }
        
        NamedNodeMap attrs = node.getAttributes();

        if (attrs != null)
        {
            for (int i = 0; i < attrs.getLength(); i++)
            {
                // check attributes ?
                Node attr = attrs.item(i);
                str += parseFaultXML(attr, codeHolder, textHolder, descriptionHolder);
            }
        }

        // recurse:
        if (node instanceof Element)
        {
            Element el = (Element) node;

            NodeList childs = el.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++)
            {
                str += parseFaultXML(childs.item(i),codeHolder, textHolder, descriptionHolder);
            }
        }

        return str;
    }

    public static String createBaseFaultMessage(BaseFaultType exc)
    {
        String message = "";
        String date = "";
        int hours = 0;
        String ec = exc.getErrorCode();
        String cause[] = (String[]) exc.getFaultCause();
        String desc = exc.getDescription();

        if (desc.length() > 0)
            message = desc + "\n";

        String meth = exc.getMethodName();

        if (meth.length() > 0)
            message = message + "Method: " + meth + "\n";

        Calendar calendar = exc.getTimestamp();
        // Spiros: Dead variable
        // hours = calendar.get(11) - calendar.get(15) / 3600000;
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = dayStr[calendar.get(7)] + " " + monthStr[calendar.get(2)] + " " + twodigits(calendar.get(5)) + " "
                + calendar.get(1) + " ";
        date = date + twodigits(calendar.get(11)) + ":" + twodigits(calendar.get(12)) + ":"
                + twodigits(calendar.get(13));
        date = date + " " + calendar.getTimeZone().getID();

        if (date.length() > 0)
            message = message + "TimeStamp: " + date + "\n";

        if (ec.length() > 0)
            message = message + "ErrorCode: " + ec + "\n";

        for (int i = 0; i < cause.length; i++)
        {
            if (i == 0)
                message = message + "Cause: " + cause[i] + "\n";
            else
                message = message + cause[i] + "\n";
        }

        return message;
    }

    static private String twodigits(int n)
    {
        String td = "";
        if (n >= 0 && n < 10)
            td = "0" + n;
        else
            td = "" + n;
        return td;
    }

    public static void printResult(PrintStream out,JobIdStructType entry)
    {
        JobIdStructType children[] = null;
        int size = 0;
        if (entry != null)
        {
            outPrintln(out,"jobID  = [" + entry.getId() + "]");
            outPrintln(out,"name   = [" + entry.getName() + "]");
            outPrintln(out,"path   = [" + entry.getPath() + "]");

            // children
            children = (JobIdStructType[]) entry.getChildrenJob();
            if (children != null)
            {
                size = children.length;
                outPrintln(out,"number of children = [" + size + "]");
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        outPrintln(out,"child n. " + (i + 1));
                        outPrintln(out,"--------------------------------------------");
                        printResult(out,children[i]);
                    }
                }
            }
            else
                outPrintln(out,"no children");
        }
    }

    private static void outPrintln(PrintStream out,String msg)
    {
    	out.println(msg); 
    }

    /** Either terminated with error, cancelled or aborted */
    public static boolean hasTerminated(StatName state)
    {
        // // check possible termination statussus:
        // StatName state = this.getLBJobStatus(jobId).getState();

        if (WMSUtil.statusIsDone(state))
            return true;

        if (WMSUtil.statusIsAborted(state))
            return true;

        if (WMSUtil.statusIsCancelled(state))
            return true;

        if (WMSUtil.statusIsCleared(state))
            return true;

        // rest are non termination statii :
        //        
        // if (WMSClient.statusIsRunning(this.getLBJobStatus(jobId).getState()))
        // return false;
        //        
        // if (WMSClient.statusIsWaiting(this.getLBJobStatus(jobId).getState()))
        // return false;
        //        
        // if
        // (WMSClient.statusIsSubmitted(this.getLBJobStatus(jobId).getState()))
        // return false;

        return false;
    }

    public static String getStatusErrorText(JobStatus stat)
    {
        if (WMSUtil.statusIsCancelled(stat.getState()))
            return "Job was Cancelled !";

        if (WMSUtil.statusIsAborted(stat.getState()))
            return "Job was Aborted!:";

        return "";

    }

    public static boolean hasError(JobStatus stat)
    {
        if (WMSUtil.statusIsCancelled(stat.getState()))
            return true;

        if (WMSUtil.statusIsAborted(stat.getState()))
            return true;

        return false;
    }

	public static LBClient createLBClient(String lbHostname, int lbPort,
			String proxyFilename) throws Exception 
	{
		LBConfig lbconf = WMLBConfig.createLBConfig(lbHostname,lbPort);
		lbconf.setProxyfilename(proxyFilename);
		return new LBClient(lbconf);
	}
    
    

}
