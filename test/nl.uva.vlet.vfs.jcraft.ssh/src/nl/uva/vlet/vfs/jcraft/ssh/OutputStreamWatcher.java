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
 * $Id: OutputStreamWatcher.java,v 1.1 2011-11-25 13:20:39 ptdeboer Exp $  
 * $Date: 2011-11-25 13:20:39 $
 */ 
// source: 

package nl.uva.vlet.vfs.jcraft.ssh;

import java.io.IOException;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelSftp;

public class OutputStreamWatcher extends OutputStream {

	OutputStream outps=null; 
	ChannelSftp channel=null; 
	
	public OutputStreamWatcher(OutputStream outps, ChannelSftp outputChannel) 
	{
		this.outps=outps; 
		this.channel=outputChannel; 
	}

	@Override
	public void write(int b) throws IOException
	{
		outps.write(b); 
	}
	
	@Override
	public void write(byte bytes[]) throws IOException
	{
		outps.write(bytes); 
	}
	
	@Override
	public void write(byte bytes[],int offset,int len) throws IOException
	{
		outps.write(bytes,offset,len); 
	}
	
	public void close() throws IOException
	{
		outps.close(); 
		channel.disconnect(); 
	}
	
	public void flush() throws IOException
	{
		outps.flush();
	}

}
