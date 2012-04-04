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
 * $Id: CobraTest.java,v 1.2 2011-04-18 12:27:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:34 $
 */ 
// source: 

package nl.uva.vlet.gui.cobraviewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import nl.uva.vlet.Global;

import org.lobobrowser.html.*;
import org.lobobrowser.html.gui.*;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;

public class CobraTest
{
	public static void main(String[] args) 
	{
		String uri = "http://google.com";
		Global.init(); 
		
		try
		{
			startFor(uri);
			startFor("file://localhost/home/ptdeboer/stuff/bug200/report.html");
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void startFor(String uri) throws IOException, SAXException
	{
		URL url = new URL(uri);
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();

		// A Reader should be created with the correct charset,
		// which may be obtained from the Content-Type header
		// of an HTTP response.
		Reader reader = new InputStreamReader(in);

		// InputSourceImpl constructor with URI recommended
		// so the renderer can resolve page component URLs.
		InputSource is = new InputSourceImpl(reader, uri);
		HtmlPanel htmlPanel = new HtmlPanel();
		HtmlRendererContext rendererContext = new LocalHtmlRendererContext(
				htmlPanel);

		// Set a preferred width for the HtmlPanel,
		// which will allow getPreferredSize() to
		// be calculated according to block content.
		// We do this here to illustrate the 
		// feature, but is generally not
		// recommended for performance reasons.
		htmlPanel.setPreferredWidth(800);

		// This example does not perform incremental
		// rendering. 
		DocumentBuilderImpl builder = new DocumentBuilderImpl(rendererContext
				.getUserAgentContext(), rendererContext);
		Document document = builder.parse(is);
		in.close();

		// Set the document in the HtmlPanel. This
		// is what lets the document render.
		htmlPanel.setDocument(document, rendererContext);

		// Create a JFrame and add the HtmlPanel to it.
		final JFrame frame = new JFrame();
		frame.getContentPane().add(htmlPanel);

		// We pack the JFrame to demonstrate the
		// validity of HtmlPanel's preferred size.
		// Normally you would want to set a specific
		// JFrame size instead.

		// This should be done in the GUI dispatch
		// thread since the document is scheduled to
		// be rendered in that thread.
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	private static class LocalHtmlRendererContext extends
			SimpleHtmlRendererContext
	{
		// Override methods here to implement browser functionality
		public LocalHtmlRendererContext(HtmlPanel contextComponent)
		{
			super(contextComponent);
		}
	}
}
