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
 * $Id: HTMLMetaTagParser.java,v 1.2 2011-04-18 12:27:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:34 $
 */ 
// source: 

package nl.uva.vlet.gui.cobraviewer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/** 
* 
* @author  Boulebiar Kamel
*/ 
public class HTMLMetaTagParser extends DefaultHandler{ 
   
	
	//-----------------------------
	//-----------------------------
	//    Donnees membres
	//-----------------------------
	//-----------------------------

  /**c'est l'objet resultat de notre parsing*/ 
   String refreshTag=null;   
   int delay=2000;
   
   /**flags nous indiquant la position du parseur
    * (dans <meta > ?)*/ 
   private boolean  inMetaTag; 
   
   
   static final private String MetaAttribHttpEquiv="http-equiv";
   static final private String MetaAttribRefreshDelay="content";	   
	   
    //-----------------------------
	//-----------------------------
	//    Fonctions membres
	//-----------------------------
	//-----------------------------

   
   /** simple constructeur*/ 
   public HTMLMetaTagParser(){ 
      super(); 
      
   }
   
   //-------------------------------------------
   /**dtection d'ouverture de balise*/ 
   public void startElement(String uri,String localName, 
                         String qName,Attributes attributes)throws SAXException
   { 
       
      if(qName.toLowerCase().equals("meta")){   
    	 inMetaTag = true;
    	 refreshTag=attributes.getValue(MetaAttribHttpEquiv);
    	 if(refreshTag!=null){
    		 if(refreshTag.compareTo("refresh")==0){
    			 try{
    				 delay=Integer.parseInt(attributes.getValue(MetaAttribRefreshDelay))*1000;
    			 }catch(Exception e){
    				 delay=2000;
    			 } 
    		 }
    		 System.out.println("tag="+refreshTag+"  delay="+delay); 	 	  
    	 }
      }
   } 
// -------------------------------------------
   /**dtection fermeture de balise*/ 
   public void endElement(String uri,String localName, 
           String qName) throws SAXException {
   	// TODO Auto-generated method stub
   	super.endElement(uri, localName, qName);
    if(qName.toLowerCase().equals("meta")){          
    	inMetaTag = false;         
     }    
   }
// -------------------------------------------
   
   /** lecture d'un lment de balise*/
   public void characters(char [] ch,int start,int lg)
   {
	    
   }
// -------------------------------------------
   
   /**dbut du parsing*/ 
   public void startDocument() throws SAXException { 
       
   }
   
// -------------------------------------------
   /**fin du parsing*/ 
   public void endDocument() throws SAXException { 
         
        
   }
// -------------------------------------------
   public String getRefreshTag() {
		return refreshTag;
	}
// -------------------------------------------
   
	public void setRefreshTag(String refreshTag) {
		this.refreshTag = refreshTag;
	}
	// -------------------------------------------
	 
	public int getDelay() {
		return delay;
	}
	// -------------------------------------------
	 
	public void setDelay(int delay) {
		this.delay = delay;
	}
	// -------------------------------------------
	 
   static public Integer parseHTML_MetaContent(String xmlcontent)
	throws ParserConfigurationException, IOException {		
	
	// run parser
	HTMLMetaTagParser h = new HTMLMetaTagParser();
	SAXParserFactory p = SAXParserFactory.newInstance();
	p.setValidating(false);	
	
	try{
	javax.xml.parsers.SAXParser parser = p.newSAXParser();
	InputStream in = new ByteArrayInputStream(xmlcontent.getBytes());
	parser.parse(in, h);
	}catch(SAXException s){
		
	}

	// get parse result
	if(h.getRefreshTag()==null)
		return null;
	else
		return h.getDelay();
}



    
}
