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
 * $Id: XMLData.java,v 1.6 2011-04-18 12:00:35 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:35 $
 */ 
// source: 

package nl.uva.vlet.data.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlXMLDataException;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VNode;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Simple XML to/from Object Factory for the data.* packages. 
 * Implementation will change but suffices for now.
 *  
 * Default encoding is UTF-8. 
 * 
 * @author P.T. de Boer
 */

public class XMLData
{
    private static ClassLogger logger;
    
    static
    {
        // check reduce mount of debug statement/profile this class
        logger=ClassLogger.getLogger(XMLData.class); 
    }
    
    public static final String VATTRIBUTE_ELEMENT="vattribute"; 

	public static final String VATTRIBUTESET_ELEMENT="vattributes";


	/*
	public static final String NAME_ELEMENT="name";

	public static final String TYPE_ELEMENT="type";

	public static final String VALUE_ELEMENT="value";

	public static final String ENUM_VALUEST="enumValues";
	 */

	// === 
	// instance 
	// === 

	private String attributeSetElementName=VATTRIBUTESET_ELEMENT; 

	private String attributeElementName=VATTRIBUTE_ELEMENT; 

	private String encoding="UTF-8";

	private String persistanceNodeElementName="persistantnode";

	/** Custom parser/scanner/endcoder/decoder for the data.* package */ 
	public XMLData()
	{

	}

	/**
	 * Specify alternative &lt;VAttributeSet&gt; XML Tag. 
	 * Make sure to use the same VAttributeSet tag names when decoding and when encoding.  
	 */ 
	public void setVAttributeSetElementName(String name)
	{
		this.attributeSetElementName=name;  
	}

	/**
	 * Specify alternative &lt;VAttribute&gt; XML Tag. 
	 * Make sure to use the same VAttribute tag names when decoding and when encoding.
	 */
	public void setVAttributeElementName(String name) 
	{
		this.attributeElementName=name;  
	}
	
	public String getVAtttributeElementName()
	{
		return this.attributeElementName; 
	}

	public String getVAtttributeSetElementName()
	{
		return this.attributeSetElementName; 
	}
	
	public String getPersistanteNodeElementName()
	{
		return this.persistanceNodeElementName; 
	}
	
	public void setPersistanteNodeElementName(String name)
	{
		this.persistanceNodeElementName=name; 
	}
	
	/** Creates new DOM Document */
	public Document createDefaultDocument() throws VlXMLDataException
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance() ;
			DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document domDoc = docBuilder.newDocument();
			return domDoc;
		}
		catch (ParserConfigurationException e)
		{
			throw new VlXMLDataException(e.getMessage(),e); 
		}
	}

	/**
	 * Creates VAttribute (XML) node 
	 */
	public Node createXMLNode(Document domDoc,VAttribute attr)
	{
		String name=attr.getName();
		String value=attr.getStringValue(); // Cast to String representation !  
		VAttributeType type=attr.getType();
		boolean editable=attr.isEditable(); 
		
		// <Attribute name=... type=...>
		Element attrElement = domDoc.createElement(getVAtttributeElementName());
		attrElement.setAttribute("name",name); 
		attrElement.setAttribute("type",type.toString());
		attrElement.setAttribute("editable",(editable==true)?"true":"false");
		//domDoc.appendChild(attrElement); 	

		// <value>...</value> 
		Element valueElement = domDoc.createElement("value");
		attrElement.appendChild(valueElement);

		//actual contents  
		if (value==null)
		{
			logger.warnPrintf("Warning: in VAttribute has NULL value:%s\n",name);
			value=""; // implementation doesn't like NULL values !
		}
		
		Text textElement = domDoc.createTextNode(value); 
		valueElement.appendChild(textElement);

		// enumValues: 

		if (attr.isEnumType())
		{
			// <enumValues>...<enumValues/> 
			Element enumValuesElement = domDoc.createElement("enumValues");
			attrElement.appendChild(enumValuesElement);

			String enumValues[]=attr.getEnumValues();
			if (enumValues!=null)
				for (String enumVal:enumValues)
				{
					// <value>...</value> 
					valueElement = domDoc.createElement("value");
					enumValuesElement.appendChild(valueElement);

					//actual contents  
					textElement = domDoc.createTextNode(enumVal); 
					valueElement.appendChild(textElement);
				}
		}

		return attrElement; 
	}

	/**
	 * Create XML (Dom) node of VAttributeSet 
	 */
	public Node createXMLNode(Document domDoc,VAttributeSet attrSet) 
	{
		VAttribute[] attrs = attrSet.toArray();
		String name=attrSet.getName(); 

		// <Attribute name=... type=...>
		Element attrSetElement = domDoc.createElement(getVAtttributeSetElementName());
		attrSetElement.setAttribute("name",name);
		
		//domDoc.appendChild(attrElement); 	
		if (attrs!=null)
			for (VAttribute attr:attrs)
			{
			    // logger.debugPrintf("Adding VAttribute Node:%s\n",attr);
				Node node=this.createXMLNode(domDoc, attr); 
				attrSetElement.appendChild(node); 
			}
		
		return attrSetElement;
	}

	/**
     * Create XML (Dom) node of VAttributeSet 
     */
    public Node createXMLNode(Document domDoc,String setTag,String setName,Iterable<VAttributeSet> attrSets) 
    {

        // <Attribute name=... type=...>
        Element attrSetElement = domDoc.createElement(setTag);
        attrSetElement.setAttribute("name",setName);
        
        //domDoc.appendChild(attrElement);  
        if (attrSets!=null)
            for (VAttributeSet set:attrSets)
            {
                //logger.debugPrintf("Adding VAttribute Node:%s\n",set);
                Node node=this.createXMLNode(domDoc, set); 
                attrSetElement.appendChild(node); 
            }
        
        return attrSetElement;
    }

	/**
	 * Create complete DOM Document from VAttributeSet. 
	 */ 
	public Document createXMLDocumentFrom(VAttributeSet attrSet) throws VlXMLDataException 
	{
		Document domDoc=createDefaultDocument(); 
		Node node = createXMLNode(domDoc, attrSet);
		domDoc.appendChild(node); 
		return domDoc;   
	}

	
	/**
	 * VAtttributeSet factory method. 
	 * Parses the whole string.  
	 * Assumes one XML document string containing one VAttribute Set.  
	 */ 
	public VAttributeSet parseVAttributeSet(String xmlString) throws VlXMLDataException
	{
		try
		{
			return parseVAttributeSet(StringUtil.createStringInputStream(xmlString,encoding));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new VlXMLDataException(e.getMessage(),e); 
		}  
	}

	/**
	 * VAtttributeSet factory method. 
	 * Reads the whole xml text from the InputStream. 
	 * Assumes one XML document containing one VAttribute Set.  
	 */ 
	public VAttributeSet parseVAttributeSet(InputStream xmlStream) throws VlXMLDataException
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance() ;
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder() ;
			Document domDoc = docBuilder.parse( xmlStream ) ;

			// normalize text representation
			domDoc.getDocumentElement().normalize() ;

			// <VAttributeSet> // 
			Vector<Element> setElements = this.getChildElements(domDoc,getVAtttributeSetElementName()) ;

			int nrOfSets = setElements.size(); 
			

			logger.debugPrintf("Document hastotal nr. of sets:%d\n",nrOfSets);

			if (nrOfSets<=0) 
				throw new VlXMLDataException("XML String doesn't contain any VAttribute Set"); 

			if (nrOfSets>1) 
				logger.warnPrintf("Warning: XML AttributeSet contains more then one 1 set\n"); 

			Element setEl = setElements.elementAt(0); 

			return parseVAttributeSet(setEl); 
		}
		catch (Exception e)
		{
			throw new VlXMLDataException(e.getMessage(),e);
		}
	}
	
	/**
     * VAtttributeSet factory method. 
     * Reads the whole xml text from the InputStream. 
     * Assumes one XML document containing one VAttribute Set.  
	 * @param collectionTag 
     */ 
    public ArrayList<VAttributeSet> parseVAttributeSets(InputStream xmlStream, String collectionTag) throws VlXMLDataException
    {
        ArrayList<VAttributeSet> list=new ArrayList<VAttributeSet>(); 
        
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance() ;
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder() ;
            Document domDoc = docBuilder.parse( xmlStream ) ;

            // normalize text representation
            domDoc.getDocumentElement().normalize() ;
            
            // ===
            // Get <collectionTag>
            // ===
            
            Vector<Element> collectionsElements = this.getChildElements(domDoc,collectionTag) ;
            
            if (collectionsElements.size()<=0) 
            {
                logger.warnPrintf("Warning: Couldn't find CollectionTag:%s\n",collectionTag);
                return list; 
            }
            else
            {
                logger.warnPrintf("Hmm:Found more then one CollectionTag:%s\n",collectionTag); 
            }
            
            // get first collection: 
            Element subEl=collectionsElements.get(0); 
            
            // <VAttributeSet> ... <VAttributeSet> ... // 
            Vector<Element> setElements = this.getChildElements(subEl,getVAtttributeSetElementName()); 
            
            int nrOfSets = setElements.size(); 

            logger.debugPrintf("Document hastotal nr. of sets:%d\n",nrOfSets);

            if (nrOfSets<=0) 
                throw new VlXMLDataException("XML String doesn't contain any VAttribute Set"); 

            for (int i=0;i<nrOfSets;i++)
            {
                Element setEl = setElements.elementAt(i); 

                list.add(parseVAttributeSet(setEl));
            }
            
            return list;
            
        }
        catch (Exception e)
        {
            throw new VlXMLDataException(e.getMessage(),e);
        }
    }
    
    
	public VAttributeSet parseVAttributeSet(Element setElement)
	{
			// name - only one
			String setName = setElement.getAttribute("name");
			//String setEditableStr = setElement.getAttribute("editable");
			
			VAttributeSet attrSet=new VAttributeSet(setName); 
			logger.debugPrintf(" > setName=%s\n",setName);

			NodeList attrNodes = setElement.getElementsByTagName( getVAtttributeElementName()) ;

			for (int i=0;i<attrNodes.getLength();i++)
			{
				Node attrNode = attrNodes.item(i); 
				String attrName = ((Element)attrNode).getAttribute("name");
				String typeStr = ((Element)attrNode).getAttribute("type");
				String editableStr= ((Element)attrNode).getAttribute("editable");
				
				VAttributeType attrType=VAttributeType.valueOf(typeStr);

				logger.debugPrintf(" - new Attribute: {name,type}={%s,%s}\n",attrName,attrType);
				logger.debugPrintf(" - new Attribute editable=%s\n",editableStr);

				// only one <value>CONTENTS</value> allowed 
				Element valueNode = this.getFirstChildElement(attrNode,"value");
				// get text contents between <value>...</value> if any ! 
				String valueStr=getSingleElementContents(valueNode);

				logger.debugPrintf(" - new Attribute value=%s\n",valueStr);
				StringList enumValues=null; 
				// enumValues
				if (attrType==VAttributeType.ENUM)
				{
					Element enumValuesElement=getFirstChildElement(attrNode,"enumValues"); 

					if (enumValuesElement!=null)
					{
						enumValues=new StringList(); 

						NodeList enumValueNodes = enumValuesElement.getElementsByTagName("value"); 
						for (int j=0;j<enumValueNodes.getLength();j++)
						{
							Node enumValueNode = enumValueNodes.item(j); 
							String enumValstr=getSingleElementContents((Element)enumValueNode);
							logger.debugPrintf(" - enumValue =%s\n",enumValstr); 
							enumValues.add(enumValstr); 
						}
						logger.debugPrintf(" - enumValues=%s\n",enumValues);
					}
				}

				VAttribute attr=null;
				// ===
				// Parsed enum value without enum list: add default value 
				// === 
				
				if ((enumValues!=null) && (enumValues.size()>0))
					attr=new VAttribute(attrName,enumValues.toArray(),valueStr);
				else
					attr=VAttribute.createFromString(attrType,attrName,valueStr); //new VAttribute(attrType,attrName,valueStr);

				logger.debugPrintf(" - new Attribute=%s\n",attr); 

				if (StringUtil.isEmpty(editableStr)!=false)
				{
					attr.setEditable(Boolean.parseBoolean(editableStr));
				}
				else
				{
					attr.setEditable(true); // default to editable ! 
				}
				
				attrSet.put(attr); 
			}

			return attrSet;  
	}

	private String getSingleElementContents(Element valueNode)
	{
		if (valueNode==null)
			return null; 

		NodeList textNodes = valueNode.getChildNodes();

		if (textNodes.getLength()<=0) 
			return null;
		// get first: 
		String valuestr= (String)( (Node)textNodes.item(0) ).getNodeValue().trim() ;

		return valuestr; 
	}

	public String getEncoding()
	{
		return this.encoding;
	}

	public String createXMLString(Document doc) throws VlXMLDataException
	{
		java.io.StringWriter sw = new java.io.StringWriter();
		StreamResult sr = new StreamResult(sw);
		writeXML(doc,sr); 
		String xml = sw.toString();
		return xml;
	}

	private void writeXML(Document doc,StreamResult sr) throws VlXMLDataException
	{
		if (doc==null)
			throw new NullPointerException("Received NULL Document");
		
		if (sr==null)
			throw new NullPointerException("Received NULL StreamResult"); 
		
		try
		{
				DOMSource domSource = new DOMSource(doc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = tf.newTransformer();
			//transformer.setOutputProperty (OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING,this.encoding);
			transformer.setOutputProperty
			("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(domSource, sr);
		}
		catch (TransformerException e)
		{
			//Global.errorPrintStacktrace(e);
			throw new VlXMLDataException(e.getMessage(),e); 
		}
		catch (Exception e)
		{
			//Global.errorPrintStacktrace(e);
			throw new VlXMLDataException(e.getMessage(),e); 
		}
		catch (Throwable e)
		{
			//Global.errorPrintStacktrace(e);
			throw new VlXMLDataException(e.getMessage(),e); 
		}
	}

	public void writeAsXML(OutputStream outp, VAttributeSet attrSet, String comments) throws VlXMLDataException
	{
		logger.debugPrintf("writeAsXML(): attrSet=%s\n",attrSet); 
				
		Document domDoc=this.createDefaultDocument();
		domDoc.appendChild(createCommentsNode(domDoc,comments)); 
		domDoc.appendChild(this.createXMLNode(domDoc,attrSet));
		
		StreamResult sr=new StreamResult(outp); 
		this.writeXML(domDoc,sr);
	}
	
	/** Write Iterable Collection of VAttributeSets */  
	public void writeAsXML(OutputStream outp, String configName, Iterable<VAttributeSet> attrSets, String comments) throws VlXMLDataException
    {
	    logger.debugPrintf("writeAsXML(): attrSets\n"); 
        
        Document domDoc=this.createDefaultDocument();
        domDoc.appendChild(createCommentsNode(domDoc,comments)); 
        domDoc.appendChild(this.createXMLNode(domDoc,configName,configName,attrSets));
        
        StreamResult sr=new StreamResult(outp); 
        this.writeXML(domDoc,sr);
    }
	
	public void writeAsXML(OutputStream outp, VPersistance rootNode, String comments) throws DOMException, VlException
	{
		logger.debugPrintf("writeAsXML(): Persistance Node=%s\n",rootNode); 
				
		Document domDoc=this.createDefaultDocument();
		domDoc.appendChild(createCommentsNode(domDoc,comments)); 
		domDoc.appendChild(this.createXMLTree(domDoc,rootNode));
		
		StreamResult sr=new StreamResult(outp); 
		this.writeXML(domDoc,sr);
	}
	
	private Node createCommentsNode(Document domDoc,String comments)
	{
		return domDoc.createComment(comments); 
	}

	/** Does a depth first tree walk and create an Dom Tree 
	 * @throws VlException */
	public Node createXMLTree(Document domDoc,VPersistance rootNode) throws VlException
	{
		{
			// current node :
			Node newXmlNode=domDoc.createElement(getPersistanteNodeElementName()); 
			VAttributeSet attrSet=rootNode.getPersistantAttributes();
			String type=rootNode.getPersistantType();
			((Element)newXmlNode).setAttribute("type",type); 
			
			// attributes if it has them. 
			if (attrSet!=null)
			{
			    // logger.debugPrintf("Adding attributes:%s\n",attrSet); 
				Node attrNode=this.createXMLNode(domDoc,attrSet); 
				newXmlNode.appendChild(attrNode);
			}
			else
			{
				logger.debugPrintf("No attributes for:%s\n",rootNode); 
			}
			
			// add childs, if any: 
			if (rootNode instanceof VCompositePersistance)
			{
				try
				{
					VNode nodes[]=((VCompositePersistance)rootNode).getNodes();
					if (nodes!=null)
					   for (VNode node:nodes)
					   {
						   // only add Persistance nodes: 
						   if (node instanceof VPersistance)
						   {
						       //Info("Adding node:"+node);
							   // go into recursion: 
							   Node childNode=this.createXMLTree(domDoc, (VPersistance)node);
							   newXmlNode.appendChild(childNode); 
					       }
						   else
						   {
							   //Info("Node is not an persistant node:"+node);
						   }
					}
				}
				catch (VlException e)
				{
					Global.logException(ClassLogger.ERROR,this,e,"Failed to create XML Tree\n");
				}
			}
			
			return newXmlNode;
		}
	}

	public VNode parsePersistantNodeTree(XMLtoNodeFactory nodeFactory, InputStream stream) throws VlException
	{
		
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance() ;
			DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document domDoc = docBuilder.parse( stream ) ;

			// normalize text representation
			domDoc.getDocumentElement().normalize() ;

			///First Node must be a persistant type ! 
			Element first=getFirstChildElement(domDoc,this.getPersistanteNodeElementName());
			
			if (first==null)
			{
				throw new VlXMLDataException("InputStream doesn't contain any nodes of type:"+getPersistanteNodeElementName());
			}

			return parsePersistantNodeTree(null,nodeFactory,first);
		}
		catch (ParserConfigurationException e)
		{
			throw new VlException("ParserConfigurationException",e.getMessage(),e); 
		}
		catch (SAXException e)
		{
			throw new VlException("SAXException",e.getMessage(),e);
		}
		catch (IOException e)
		{
			throw new VlException("IOException",e.getMessage(),e);
		}
	}

	public VNode parsePersistantNodeTree(VNode _parent,XMLtoNodeFactory nodeFactory, Element start) throws VlException
	{
		logger.debugPrintf(" parseXMLNodeTree:%s\n",start.getNodeName()); 
		
		Element el=(Element)start; 
		String persistantType=el.getAttribute("type");
		logger.debugPrintf(" - persistant type=%s\n",persistantType); 
		
		// Optional attributeSet: 
		Element attrSetEl=getFirstChildElement(start,this.getVAtttributeSetElementName());
		VAttributeSet attrSet=null; 
		
		if (attrSetEl!=null)
		{
			attrSet=this.parseVAttributeSet(attrSetEl);
		}
			
		// Call Factory: 
		VNode newNode=nodeFactory.createNode(_parent,persistantType,attrSet);
		logger.debugPrintf (" - create VNode: %s\n",newNode); 
		
		if (newNode==null)
			throw new VlXMLDataException("Node Factory returned NULL node for type:"+persistantType);  
			
		// scan childs, filter for persistant node types  
		Vector<Element> childs=getChildElements(start,this.getPersistanteNodeElementName());
		
		if ((childs!=null) && (childs.size()>0))
		{	
			if ((newNode instanceof VComposite)==false) 
				throw new VlXMLDataException("Persistant Node has childs but is NOT of Composite type:"+newNode);
			
			for (Element child:childs)
			{
				VNode subTree=this.parsePersistantNodeTree(newNode,nodeFactory, child); 
				((VComposite)newNode).addNode(subTree,false); 
			}
		}
		
		return newNode; 
	}

	/** Get childs and filter for specific element */ 
	private Vector<Element> getChildElements(Node parent, String elementTag)
	{
		Vector<Element>elements=new Vector<Element>(); 
	
		NodeList childs=parent.getChildNodes(); 
		
		for(int i=0;i<childs.getLength();i++)
		{
			Node item = childs.item(i);
			
			if (item instanceof Element)
			{
				Element el=(Element)childs.item(i); 
				String tagName=el.getTagName(); 
				
				if (StringUtil.equals(tagName,elementTag))
				{
					logger.debugPrintf("getChildElements(): found:%s\n",elementTag); 
					elements.add(el);  
				}
				else
				{
					logger.debugPrintf("getChildElements(): ignoring:%s",tagName);
				}
			}
			else
			{
				// usually text/newlines,etc. 
				logger.debugPrintf("Unknown Node:%s\n",item); 
			}
		}
		
		return elements; 
	}

	private Element getFirstChildElement(Node parent, String elementName) 
	{
		Vector<Element> els = this.getChildElements(parent,elementName);
		
		if ((els==null) ||  (els.size()<=0))
			return null;
		
		return els.elementAt(0); 
	}
	
}
