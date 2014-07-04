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

package Recognition;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import utils.FileHelper;
import utils.Enumrations.FileType;
import utils.Enumrations.Language;
import utils.Enumrations.StandardEntities;

import DataObject.NERObject;
import DataObject.TextObject;

import com.apple.dnssd.TXTRecord;
import com.webssky.jcseg.core.ISegment;
import com.webssky.jcseg.core.IWord;
import com.webssky.jcseg.core.SegmentFacotry;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


import org.w3c.dom.*;

public class NerController {
	
	private AbstractSequenceClassifier<CoreLabel> CL_en;
	private AbstractSequenceClassifier<CoreLabel> CL_de;
	private AbstractSequenceClassifier<CoreLabel> CL_zh;
	
	//entity types
	@SuppressWarnings("rawtypes")
	private Map<SimpleEntry, StandardEntities> entity_type_en;
	@SuppressWarnings("rawtypes")
	private Map<SimpleEntry, StandardEntities> entity_type_de;
	@SuppressWarnings("rawtypes")
	private Map<SimpleEntry, StandardEntities> entity_type_zh;
	
	//for xml result file
	private String _NER_XMLRoot = "NerResult";
	private String _NER_XMLEntry = "entity";
	private String _NER_Entry_Id = "id";
	private String _NER_Entry_Text = "text";
	private String _NER_Entry_Type = "type";
	private String _NER_Entry_Start = "start";
	private String _NER_Entry_End = "end";
	private String _NER_Entry_Language = "language";
	private String _NER_JSON_Root = "entities";
	
	//for other result file
	private String _XMLResultFileName = "ner.xml";
	private String _CSVResultFileName = "ner.csv";
	private String _JSONResultFileName = "ner.json";
	private String _TxtResultFileName = "ner.txt";
	
	private int _NER_id_count = 1;
	
	public NerController(){		
		Initialization();
	}
	


	/**
	 * This method serves as a pre-processing for Chinese NER.
	 * Because Chinese text has no word spacing, so we have to
	 * segment text into words before the NER algorithm process
	 * the text. 
	 * @param text
	 * @return
	 */
	public String GetSegmentedChineseText(String text){
		
		ISegment seg = null;
		seg = SegmentFacotry.createSegment("com.webssky.jcseg.ComplexSeg");
		
		StringBuffer segment_result = new StringBuffer();
		
		IWord word = null;
		
		boolean isFirst = true;
		int counter = 0;
		//in the reset the dictionary will be loaded
		try {
			seg.reset(new StringReader(text));
			
			while ( (word = seg.next()) != null ) {
				if ( isFirst ) {
					segment_result.append(word.getValue());
					isFirst = false;
				}
				else {
					segment_result.append(" ");
					segment_result.append(word.getValue());
				}
				//clear the allocations of the word.
				word = null;
				counter++;
			}
		} catch (IOException e) {		
			e.printStackTrace();
		}
		
		//System.out.println("Chinese text segmentation result:"); 
		//System.out.println(segment_result.toString());	
		
		return segment_result.toString();
	}

	/**
	 * Perform ner analysis for multi-languages, provide analysis
	 * result with inline xml format.
	 * @param text: input text to analyze
	 * @param _lang: language
	 * @return: result text with inline xml tagging
	 */
	public String GetNERStringInlineXMLFormat(String text, Language _lang ){
		
		//CheckAndLoadNERClassifier(_lang);
		
		String result = "";
		
		switch (_lang) {
		case EN:
			result = CL_en.classifyWithInlineXML(text);
	        //System.out.println(result);
	        //System.out.println(CL_en.classifyToString(text, "xml", true));	        
			break;
			
		case DE:
			result = CL_de.classifyWithInlineXML(text);
	        //System.out.println(result);
	        //System.out.println(CL_de.classifyToString(text, "xml", true));
			
			//we have to use the common output format according to the result format of EN
			result = result.replace("<I-LOC>", "<LOCATION>");
			result = result.replace("</I-LOC>", "</LOCATION>");
			result = result.replace("<B-LOC>", "<LOCATION>");
			result = result.replace("</B-LOC>", "</LOCATION>");

			result = result.replace("<I-ORG>", "<ORGANIZATION>");
			result = result.replace("</I-ORG>", "</ORGANIZATION>");
			result = result.replace("<B-ORG>", "<ORGANIZATION>");
			result = result.replace("</B-ORG>", "</ORGANIZATION>");

			result = result.replace("<I-MISC>", "<MISC>");
			result = result.replace("</I-MISC>", "</MISC>");
			result = result.replace("<B-MISC>", "<MISC>");
			result = result.replace("</B-MISC>", "</MISC>");

			result = result.replace("<I-PER>", "<PERSON>");
			result = result.replace("</I-PER>", "</PERSON>");	

			break;

		case ZH:
			//segment Chinese text to spaced words			
			text = GetSegmentedChineseText(text);
			
			//here in practice we have found that the chinese model cannot deal with 
			//unsegmented Chinese string. Even already segmented, it is still not robust 
			//with long string containing more words, therefore we do the NER
			//for each segment individually. 
			String[] words = text.split("\\s+");
			StringBuilder s_inline = new StringBuilder();
			for (int i = 0; i < words.length; i++) {
			    // You may want to check for a non-word character before blindly
			    // performing a replacement
			    // It may also be necessary to adjust the character class
			    s_inline.append(CL_zh.classifyWithInlineXML(words[i]));
			    //add empty space as the separator
			    if(i != words.length-1)
			    	s_inline.append(" ");
			}
			
			
			result = s_inline.toString();
	        //System.out.println(result);
	        //System.out.println(CL_zh.classifyToString(text, "xml", true));

			//we have to get the common output format according to the result format of EN
			result = result.replace("<ORG>", "<ORGANIZATION>");
			result = result.replace("</ORG>", "</ORGANIZATION>");
			
			result = result.replace("<LOC>", "<LOCATION>");
			result = result.replace("</LOC>", "</LOCATION>");
			
			result = result.replace("<GPE>", "<LOCATION>");
			result = result.replace("</GPE>", "</LOCATION>");
			
			break;

		default:
			throw new NotImplementedException();			
		}
		
		return result;
	}
	
	/**
	 * Perform NER for each item in text object list, the found 
	 * NER object will be saved in the NERObject list. 
	 * @param tobjectList: text object list to analyze
	 * @param nobjectlist: NER object list to fill.
	 * @return whether the analysis process successfully finished
	 */
	public boolean NERForTextObjectList(List<TextObject> _tobjectList,
			List<NERObject> _nobjectlist) {
		if (_tobjectList == null || _nobjectlist == null) {
			return false;
		}
		
		try {
			for (TextObject to : _tobjectList) {
				
				if (to.get_Text() != null
						&& !to.get_Text().isEmpty()) {
					//perform ner					
					String result = GetNERStringInlineXMLFormat(to.get_Text(), to.get_Language());
					
					ExtractNERObjectFromInLineXMLNERResult(result, to, _nobjectlist);
				}
			}
		} catch (Exception e) {
			//System.out.println("error occurred during ner analysis for text object list.");
			return false;
		}
		return true;
	}
	
	/**
	 * Create a result file for the given ner object list.
	 * Supported file type could be "xml", "json", and "csv" format
	 * @param nobjectlist: ner object list
	 * @param _fildeType: target file format type
	 * @param _saveFolderPath: folder path to save result file
	 * @return: indicates whether the process succussfully finished.
	 */
	public boolean SaveNERObjectToFile(List<NERObject> _nobjectlist, FileType _fileType, String _saveFolderPath) {
		switch (_fileType) {
		case XML:
			if(!SaveFileXML(_nobjectlist, _saveFolderPath)){
				return false;
			}
			break;
		case JSON:	
			if(!SaveFileJSON(_nobjectlist, _saveFolderPath)){
				return false;
			}
			break;
		case CSV:
			throw new NotImplementedException();
			//break;

		default:
			//System.out.println("This output file format is not supported yet.");
			return false;
		}
		return true;
	}
	
	public boolean SaveFileJSON(List<NERObject> _nobjectlist,
			String _saveFolderPath) {		
		if(_nobjectlist == null || _nobjectlist.isEmpty()){
			return false;
		}
		
		String fn = _saveFolderPath + System.getProperty("file.separator") + _JSONResultFileName;
		File fileDir = new File(fn);
		String NL = System.getProperty("line.separator");
		StringBuilder text = new StringBuilder();
		try {			
			
			int i = 0;
			
			//start to write the json file
			text.append("{" + NL);
			text.append('"' + _NER_Entry_Language + '"' + ':' + '"' +  _nobjectlist.get(0).get_Language().toString() + '"' + ',' + NL);
			text.append('"' + _NER_JSON_Root + '"' + ":[" + NL );
			for (NERObject nerObject : _nobjectlist) {
				text.append("{" + NL);
				
				//id
				text.append('"' + _NER_Entry_Id + '"' + ':' + '"' + nerObject.get_Id() + '"' + ',' + NL);
				//type
				text.append('"' + _NER_Entry_Type + '"' + ':' + '"' + nerObject.get_EntityType().toString() + '"' + ',' + NL);
				//start
				text.append('"' + _NER_Entry_Start + '"' + ':' +  nerObject.get_Start() + ',' + NL);
				//end
				text.append('"' + _NER_Entry_End + '"' + ':' +  nerObject.get_End() + ',' + NL);
				//text
				text.append('"' + _NER_Entry_Text + '"' + ':' + '"' + nerObject.get_Text() + '"' + NL);
				
				
				if(i == _nobjectlist.size() - 1)
					text.append("}" + NL);
				else
					text.append("}," + NL);
				i++;
			}
			text.append("]}" + NL);

			//print result to the system output
			//System.out.println(text.toString());
			
			
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileDir), "UTF8"));
			out.append(text.toString());
			 
			out.flush();
			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	/**
	 * this method save the inline-xml based ner result string to a file using 
	 * the given path.
	 * @param result_path
	 * @param result
	 */
	public boolean SaveInlineXMLResult(String result_path, String result) {
		if(result_path != null && FileHelper.IsFolderExist(result_path)){
			String fn = result_path + System.getProperty("file.separator") + _TxtResultFileName;
			try {
				Writer writer = new OutputStreamWriter(
	                       new FileOutputStream(fn), "UTF-8");
	            BufferedWriter fout = new BufferedWriter(writer);
	            
	            fout.write(result);
	            fout.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	
	
	public String GetInlineXMLNERResult(String input_text, Language lang, String result_path){
		String result = GetNERStringInlineXMLFormat(input_text, lang);
		if(result !=null){
		//save result to file
			if(result_path != null){
				SaveInlineXMLResult(result_path, result);
				return "";
			}else
				return result; 
	 
		}
		return "";		
	}
	
	
	private boolean SaveFileXML(List<NERObject> _nolist, String _savePath){
	
		try {
			//System.out.println("ner object list size: " + _nolist.size());
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(_NER_XMLRoot);
			doc.appendChild(rootElement);
			
			for (NERObject nerObject : _nolist) {
				// entity element
				Element entity = doc.createElement(_NER_XMLEntry);
				rootElement.appendChild(entity);
				
				// set attribute to entity element
				Attr attr = doc.createAttribute(_NER_Entry_Id);
				attr.setValue(nerObject.get_Id());
				entity.setAttributeNode(attr);
				
				attr = doc.createAttribute(_NER_Entry_Text);
				attr.setValue(nerObject.get_Text());
				entity.setAttributeNode(attr);
				
				attr = doc.createAttribute(_NER_Entry_Type);
				attr.setValue(nerObject.get_EntityType().toString());
				entity.setAttributeNode(attr);
				
				attr = doc.createAttribute(_NER_Entry_End);
				attr.setValue(String.valueOf(nerObject.get_End()));
				entity.setAttributeNode(attr);
				
				attr = doc.createAttribute(_NER_Entry_Start);
				attr.setValue(String.valueOf(nerObject.get_Start()));
				entity.setAttributeNode(attr);
				
				attr = doc.createAttribute(_NER_Entry_Language);
				attr.setValue(nerObject.get_Language().toString());
				entity.setAttributeNode(attr);				
			}
			 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			String fileName = _savePath + System.getProperty("file.separator") + _XMLResultFileName;
			
			StreamResult result = new StreamResult(new File(fileName));
			 			
			transformer.transform(source, result);
			
			//print result to system out
			String text = FileHelper.ReadFileText(fileName);
//			if(text != null)
//				System.out.println(text);
					
		 
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return false;
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * a recursive function for getting ner object from the given ner result string
	 * in inline xml format.
	 * @param _text: ner string
	 * @param _nolist: ner object list
	 * @param _entry: which contains the begin and end tag of a ner entity  
	 */
	private void GetNERObjectRecursively(String _text, List<NERObject> _nolist, SimpleEntry<String, String> _entry,
			StandardEntities _entityType, TextObject _to){

		int index_start = _text.indexOf(_entry.getKey().toString());
		int index_end = _text.indexOf(_entry.getValue().toString());
		
		
		if (index_start > -1 && index_end > index_start) {
			String entity_text = _text.substring(index_start + _entry.getKey().toString().length(), index_end);
			String remaining_text = _text.substring(index_end + _entry.getValue().toString().length());			
			
			NERObject no = new NERObject();
			no.set_Text(entity_text);
			no.set_Start(_to.get_Start());
			no.set_End(_to.get_End());
			no.set_Language(_to.get_Language());
			no.set_EntityType(_entityType);
			no.set_Id(String.valueOf(_NER_id_count));
			_nolist.add(no);
			_NER_id_count++;
			//recursive 
			GetNERObjectRecursively(remaining_text, _nolist, _entry, _entityType, _to);
		}
	}
	
	/**
	 * Extract ner objects from ner result text in inline-xml format.
	 * @param result: ner result text
	 * @param _lang: language
	 * @param nobjectlist: ner object list
	 */
	private boolean ExtractNERObjectFromInLineXMLNERResult(String _result, TextObject _to,
			List<NERObject> _nobjectlist) {

		for (SimpleEntry<String, String> entry : entity_type_en.keySet()) {
			GetNERObjectRecursively(_result, _nobjectlist, entry, entity_type_en.get(entry), _to);
		}
		return true;
	}


	/**
	 * Check whether the corresponding ner classifier is loaded.
	 * If not then load it.
	 * 
	 */
	private void CheckAndLoadNERClassifier(Language _lang){
				
		//String en = "web/WEB-INF/lib/ner/classifiers/english.conll.4class.distsim.crf.ser.gz";
		String en = "/var/lib/tomcat6/webapps/nerService/WEB-INF/lib/ner/classifiers/english.conll.4class.distsim.crf.ser.gz";
		
		//String de = "web/WEB-INF/lib/ner/classifiers/hgc_175m_600.crf.ser.gz";
		String de = "/var/lib/tomcat6/webapps/nerService/WEB-INF/lib/ner/classifiers/hgc_175m_600.crf.ser.gz";
		
		//String zh = "web/WEB-INF/lib/ner/classifiers/chinese.misc.distsim.crf.ser.gz";
		String zh = "/var/lib/tomcat6/webapps/nerService/WEB-INF/lib/ner/classifiers/chinese.misc.distsim.crf.ser.gz";
		try {
			//init NER models
			switch (_lang) {
			case EN:
				if (CL_en == null) 
					CL_en = CRFClassifier.getClassifier(en);	
				break;
			case DE:
				if(CL_de == null)
					CL_de = CRFClassifier.getClassifier(de);
				break;
			case ZH:
				if(CL_zh == null)
					CL_zh = CRFClassifier.getClassifier(zh);	
				break;
			default:
				//System.out.println("language " + _lang.toString() + " is still not supported yet.");
				break;
			}
			
		} catch (ClassCastException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	


	private void Initialization() {
		//English
		entity_type_en = new HashMap<SimpleEntry, StandardEntities>();
		
		SimpleEntry se_en_l = new SimpleEntry<String, String>("<LOCATION>", "</LOCATION>");
		SimpleEntry se_en_p = new SimpleEntry<String, String>("<PERSON>", "</PERSON>");
		SimpleEntry se_en_o = new SimpleEntry<String, String>("<ORGANIZATION>", "</ORGANIZATION>");
		SimpleEntry se_en_m = new SimpleEntry<String, String>("<MISC>", "</MISC>");
		SimpleEntry se_en_g = new SimpleEntry<String, String>("<GPE>", "</GPE>");
		
		entity_type_en.put(se_en_l, StandardEntities.Location);
		entity_type_en.put(se_en_p, StandardEntities.Person);
		entity_type_en.put(se_en_o, StandardEntities.Organization);
		
		//we only support location, person and organization
		//entity_type_en.put(se_en_m, StandardEntities.MISC);
		//entity_type_en.put(se_en_g, StandardEntities.GPE);
		
		//load NER classifiers
		CheckAndLoadNERClassifier(Language.EN);

		//load NER classifiers
		CheckAndLoadNERClassifier(Language.DE);
		//load NER classifiers
		CheckAndLoadNERClassifier(Language.ZH);
		
	}

	


}
