// Copyright (c) 2014 SemaMediaData. All Rights Reserved.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//

package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;

import utils.DateTimeHelper;
import utils.Enumrations.Language;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import DataObject.NerJobObject;
import DataObject.TextObject;
import DataObject.VideoAnalysisJobObject;


public class XMLHelper {
	

	public static VideoAnalysisJobObject GetVideoAnalysisObjectFromXMLString(String xml, VideoAnalysisJobObject newjob){
		
		try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        
	        StringReader sr = new StringReader(xml);
	        
	        InputSource is = new InputSource(sr);
	        is.setEncoding("UTF-8");
	        Document doc = db.parse(is);
	        getAnalysisJobObjectFromDocument(doc, newjob);	 
	        
        }catch (SAXParseException err) {

        }catch (SAXException e) {
        	Exception x = e.getException ();
        	((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        	t.printStackTrace ();
        }

		return newjob;
	}
	
	private static void getAnalysisJobObjectFromDocument(Document doc, VideoAnalysisJobObject newjob) {
		if (newjob == null) {
			newjob = new VideoAnalysisJobObject();			
		}
		// normalize text representation
        doc.getDocumentElement().normalize();       
        
        //=========================================================================================
        // This part of codes contains string comparison, it is not a good programming solution
        // but for xml file parsing we have no other choice now, so man should really be careful with 
        // this part!!!!!
        //=========================================================================================
        NodeList nodelist = doc.getDocumentElement().getChildNodes();
			
		for (int j = 0; j < nodelist.getLength(); j++) {
			if (nodelist.item(j).getNodeName().equalsIgnoreCase("job") ) {
				Node jobnode = nodelist.item(j);
				NodeList subsublist = jobnode.getChildNodes();

				for (int k = 0; k < subsublist.getLength(); k++) {
					Node subno = subsublist.item(k);					
					//assigns value for new job object
					if (subno.getNodeName().equalsIgnoreCase("videoFullName")) {
						if (!subno.getTextContent().trim().isEmpty()) {						
							newjob.setVideoName(subno.getTextContent().trim());
						}
					}else if (subno.getNodeName().equalsIgnoreCase("ResultPath")) {
						if (!subno.getTextContent().trim().isEmpty()) {
							newjob.set_AnalysisResultPath(subno.getTextContent().trim());
						}
					}else if (subno.getNodeName().equalsIgnoreCase("Language")) {
						if (subno.getTextContent().trim().equalsIgnoreCase("EN")) {
							newjob.set_Language(Language.EN);
						}else if (subno.getTextContent().trim().equalsIgnoreCase("DE")) {
							newjob.set_Language(Language.DE);
						}else if (subno.getTextContent().trim().equalsIgnoreCase("ZH")) {
							newjob.set_Language(Language.ZH);
						}else if (subno.getTextContent().trim().equalsIgnoreCase("ZH_T")) {
							newjob.set_Language(Language.ZH_T);
						}else if (subno.getTextContent().trim().equalsIgnoreCase("FR")) {
							newjob.set_Language(Language.FR);
						}else
							newjob.set_Language(Language.None);
					}else if (subno.getNodeName().equalsIgnoreCase("videoURL")) {
						if (!subno.getTextContent().trim().isEmpty()) {
							newjob.set_VideoURL(subno.getTextContent().trim());
						}
					}else if (subno.getNodeName().equalsIgnoreCase("Text")) {
						if (!subno.getTextContent().trim().isEmpty() && newjob instanceof NerJobObject) {
							((NerJobObject)newjob).setInput_text(subno.getTextContent().trim());
						}
					}else if (subno.getNodeName().equalsIgnoreCase("asrFile")) {
						if (!subno.getTextContent().trim().isEmpty()) {
							newjob.set_asrFilePath(subno.getTextContent().trim());
						}
					}else if (subno.getNodeName().equalsIgnoreCase("ocrFile")) {
						if (!subno.getTextContent().trim().isEmpty()) {
							newjob.set_ocrFilePath(subno.getTextContent().trim());
						}
					}else if (subno.getNodeName().equalsIgnoreCase("txtFile")) {
						if (!subno.getTextContent().trim().isEmpty()) {
							newjob.set_txtFilePath(subno.getTextContent().trim());
						}
					}
					
				}
			}
		}
	}	
	
	/**
	 * Read the ocr text object from xml result file, fill to the list.
	 * @param ocrFileName: ocr result file name
	 * @param tobjectList: text object list to fill
	 * @param getLanguage: text language
	 * @return: indicates whether the process successfully done.
	 */
	public static boolean GetTextObjectFromOCRFile(String _ocrFileName,
			List<TextObject> _tobjectList, Language _lang) {
		
		try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        	        
	        File f = new File(_ocrFileName);
	        if (!f.exists()) {
				System.out.println("can not find ocr result file:'" + _ocrFileName +"'");
				return false;
			}
	        
	        Document doc = db.parse(f);
	        
	        // normalize text representation
	        doc.getDocumentElement().normalize();  
	        
	        NodeList nodelist = doc.getDocumentElement().getChildNodes();
			
			for (int j = 0; j < nodelist.getLength(); j++) {
				if (nodelist.item(j).getNodeName().equalsIgnoreCase("textObject") ) {
					
					//create new text object
					TextObject to = new TextObject();
					to.set_Language(_lang);
					
					Node jobnode = nodelist.item(j);
					NodeList subsublist = jobnode.getChildNodes();

					for (int k = 0; k < subsublist.getLength(); k++) {
						Node subno = subsublist.item(k);					
						//assigns value for new job object
						if (subno.getNodeName().equalsIgnoreCase("StartSecond")) {
							if (!subno.getTextContent().trim().isEmpty()) {						
								to.set_Start(Double.parseDouble(subno.getTextContent().trim()));
							}
						}else if (subno.getNodeName().equalsIgnoreCase("EndSecond")) {
							if (!subno.getTextContent().trim().isEmpty()) {
								to.set_End(Double.parseDouble(subno.getTextContent().trim()));
							}
						}else if (subno.getNodeName().equalsIgnoreCase("Text")) {
							if (!subno.getTextContent().trim().isEmpty()) {
								to.set_Text(subno.getTextContent().trim());
							}
						}	
					}
					
					_tobjectList.add(to);			
				}
			}
        }catch (SAXParseException err) {
        	System.out.println(err.getMessage());
        	System.out.println(err.getStackTrace());
        	return false;
        }catch (SAXException e) {
        	Exception x = e.getException();
        	((x == null) ? e : x).printStackTrace();
        	
        	System.out.println(e.getMessage());
        	return false;
        }catch (Throwable t) {
        	t.printStackTrace ();
        	System.out.println(t.getMessage());
        	return false;
        }
        
		return true;
	}	


}
