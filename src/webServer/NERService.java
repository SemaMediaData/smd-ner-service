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

package webServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.annotation.Resource;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import utils.Enumrations.FileType;
import utils.XMLHelper;
import utils.Enumrations.Language;
import utils.Enumrations.AnalysisJobType;
import DataObject.NERObject;
import DataObject.NerJobObject;
import DataObject.TextObject;
import DataObject.VideoAnalysisJobObject;
import Recognition.NerController;
import utils.FileHelper;

@SuppressWarnings("restriction")
@WebService(endpointInterface="webServer.NERService", name = "NERWebService", serviceName = "NERService")
@SOAPBinding(style = SOAPBinding.Style.RPC, use=SOAPBinding.Use.ENCODED)

public class NERService {

	
	private NerController _Ner_controller;
	
	///hard coded user and pw for accessing the ner soap service
	String _DB_url = "jdbc:mysql://127.0.0.1:3306/DB?useUnicode=true&amp;characterEncoding=UTF-8";
	private UserManager _UserMan;
	
	
	public NERService() {
		initializeSystem();		
	}
	

	@Resource
	WebServiceContext wsContext;
	
	
	/**
	 * This web method is created for ner live demonstration.
	 *  
	 * @param data: Texts to analyze
	 * @return xml encoded analysis result.
	 */
	@WebMethod
	public String ner_string(String data){
		String encoded = null;
		try {
			//authenticate user
			//- check user:pwd .....
			//if user:pwd check failed, response should be "Error: Authentication failed!"
			if (!authenticateUser())
				return authenticationFailureMessage();			
			
			String data_decoded = decodeRequest(data);
			//System.out.println("data:" + data_decoded);
			NerJobObject job = new NerJobObject(); 
			XMLHelper.GetVideoAnalysisObjectFromXMLString(data_decoded, job);
			
			String response = "";
			
			if(job.get_Language() == Language.None){
				response = "NER_RESPONSE_Error: language not supported yet!";
			}else{
				if ( job.getInput_text()!= null && !job.getInput_text().isEmpty()) {
					job.set_JobType(AnalysisJobType.NER_string);											
					response = _Ner_controller.GetInlineXMLNERResult(job.getInput_text(), job.get_Language(), null);
				}else{
					response = "NER_RESPONSE_Error: no input text content:";
				}				
			}

			encoded = new BASE64Encoder().encode(response.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return encoded;
	}
	
	
	/**
	 * Thie method performs the ner analysis for a asr result file 
	 */
	@WebMethod
	public String ner_asr(String data){
		String encoded = null;
		try {
			//authenticate user
			if (!authenticateUser())
				return authenticationFailureMessage();			
			
			String data_decoded = decodeRequest(data);
			
			VideoAnalysisJobObject job = new VideoAnalysisJobObject();
			XMLHelper.GetVideoAnalysisObjectFromXMLString(data_decoded, job);
			
			String response = "Info: ner result file for ASR saved.";
			
	
			File f = new File(job.get_asrFilePath());
			if ( f!= null && f.exists()) {
			
					
				job.set_JobType(AnalysisJobType.NER_asrFile);
				
				
				List<TextObject> tobjectList = new ArrayList<TextObject>();
				if (GetTextObjectFromASRTranscript(job.get_asrFilePath(), tobjectList, job.get_Language())){
					List<NERObject> nobjectlist = new ArrayList<NERObject>();
					
					if (_Ner_controller.NERForTextObjectList(tobjectList, nobjectlist)){
						if(job.get_AnalysisResultPath() != null && FileHelper.IsFolderExist(job.get_AnalysisResultPath())){
							//save ner object in the result file
							//the result will also be printed within this method
							if (!_Ner_controller.SaveNERObjectToFile(nobjectlist, FileType.XML, job.get_AnalysisResultPath())){
								response = "NER_RESPONSE_Error: Save NER object to file failed!";				
							}
						}else{
							response = "NER_RESPONSE_Error: Save NER object to file failed, wrong result path!";
						}
						
					}else{
						response = "NER_RESPONSE_Error: NER analysis for text object list failed.";
					}
						
				}else{
					response = "NER_RESPONSE_Error: Get text object from asr result file failed.";
				}
				
			}else{
				//NOTE: this string will also be used in portal software, if we change it, the corresponding code in portal has to be changed too.
				System.out.println("asr file:" + job.get_ocrFilePath() + " not found!");
				response = "NER_RESPONSE_Error: can not find asr result file.";
			}
			
			
			encoded = new BASE64Encoder().encode(response.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return encoded;
	}
	
	
	/**
	 * Thie method performs the ner analysis for a OCR result file 
	 */
	@WebMethod
	public String ner_ocr(String data){
		String encoded = null;
		try {
			//authenticate user
			if (!authenticateUser())
				return authenticationFailureMessage();			
			
			String data_decoded = decodeRequest(data);
			
			VideoAnalysisJobObject job = new VideoAnalysisJobObject();
			XMLHelper.GetVideoAnalysisObjectFromXMLString(data_decoded, job);
			
			String response = "Info: ner result file for OCR saved.";
			
	
			File f = new File(job.get_ocrFilePath());
			if ( f!= null && f.exists()) {								
				job.set_JobType(AnalysisJobType.NER_ocrFile );


				List<TextObject> tobjectList = new ArrayList<TextObject>();
				if (GetTextObjectFromOCRResult(job.get_ocrFilePath(), tobjectList, job.get_Language())){
					List<NERObject> nobjectlist = new ArrayList<NERObject>();
					
					if (_Ner_controller.NERForTextObjectList(tobjectList, nobjectlist)){
						if(job.get_AnalysisResultPath() != null && FileHelper.IsFolderExist(job.get_AnalysisResultPath())){
							//save ner object in the result file
							//the result will also be printed within this method
							if (!_Ner_controller.SaveNERObjectToFile(nobjectlist, FileType.XML, job.get_AnalysisResultPath())){
								response = "NER_RESPONSE_Error: Save NER object to file failed!";												
							}
						}else{
							response = "NER_RESPONSE_Error: Save NER object to file failed, wrong result path!";
						}
						
					}else{
						response = "NER_RESPONSE_Error: NER analysis for text object list failed.";
					}
						
				}else{
					response = "NER_RESPONSE_Error: Get text object from ocr result file failed.";
				}

			}else{
				//NOTE: this string will also be used in portal software, if we change it, the corresponding code in portal has to be changed too.
				System.out.println("ocr file:" + job.get_ocrFilePath() + " not found!");
				response = "NER_RESPONSE_Error: can not find ocr result file.";
			}
			
			
			encoded = new BASE64Encoder().encode(response.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return encoded;
	}
	
	/**
	 * Thie method performs the ner analysis for a OCR result file 
	 */
	@WebMethod
	public String ner_txt(String data){
		String encoded = null;
		try {
			//authenticate user
			if (!authenticateUser())
				return authenticationFailureMessage();			
			
			String data_decoded = decodeRequest(data);
			
			VideoAnalysisJobObject job = new VideoAnalysisJobObject();
			XMLHelper.GetVideoAnalysisObjectFromXMLString(data_decoded, job);
			
			String response = "Info: ner result file for text file saved.";
			
	
			File f = new File(job.get_txtFilePath());
			if ( f!= null && f.exists()) {								
				job.set_JobType(AnalysisJobType.NER_txtFile );

				String file_text = FileHelper.ReadFileText(job.get_txtFilePath());
				if(file_text != null){
					_Ner_controller.GetInlineXMLNERResult(file_text, job.get_Language(), job.get_AnalysisResultPath());
				}									
			}else{
				//System.out.println("text file:" + job.get_ocrFilePath() + " not found!");
				response = "NER_RESPONSE_Error: can not find input text file.";
			}			
			
			encoded = new BASE64Encoder().encode(response.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return encoded;
	}
	
	//////////////////////////////////////////////////////////////
	//                   private methods						//
	//////////////////////////////////////////////////////////////
	
	/**
	 * Initialize the web service framework:
	 * all configuration files are read, 
	 * a analysis job scheduler will be initialized
	 * in which a thread pool will be created for 
	 * processing each job.
	 */
	private void initializeSystem() {
		//initilaize NER controller
		_Ner_controller = new NerController();						
	}
	
	/**
	 * returns a message that contains authentication failure information. 
	 * @return
	 */
	private String authenticationFailureMessage() {
		String response = "NER_RESPONSE_Error: Authentication failed!";
		return new BASE64Encoder().encode(response.getBytes());
	}
	
	
	
	/**
	 * This method handles the user authentication.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean authenticateUser() {
		
		MessageContext context = wsContext.getMessageContext();
		
		Map http_headers = (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS);
		ArrayList userList = (ArrayList) http_headers.get("Username");
		ArrayList passList = (ArrayList) http_headers.get("Password");
		
		String username = "";
		String password = "";
		
		if (userList != null)
			username = userList.get(0).toString();
		
		if (passList != null)
			password = passList.get(0).toString();
		
		_UserMan = new UserManager(_DB_url);
		boolean userValid = _UserMan.check(username, password);
		_UserMan.closeConnection();
		
		return userValid;
	}
	
	/**
	 * Decode web service resuqest data.
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private String decodeRequest(String data) throws IOException {
		byte[] tmp = new BASE64Decoder().decodeBuffer(data);
		return new String(tmp, "UTF-8");
	}
	
	
	/**
	 * Read ocr text objects from the result file.
	 * @param ocrFileName: file name to parse
	 * @param tobjectList: list to fill
	 * @param getLanguage: text language
	 * @return: indicates whether the process is successfully finished.
	 */
	private boolean GetTextObjectFromOCRResult(String _ocrFileName,
			List<TextObject> _tobjectList, Language _lang) {
		if (_ocrFileName == null || _ocrFileName.isEmpty()) {
			//System.out.println("Ocr result file can not be found.");
			return false;
		}
		
		if (XMLHelper.GetTextObjectFromOCRFile(_ocrFileName, _tobjectList, _lang)){
			//System.out.println("Ocr text object size: " + _tobjectList.size());
		}else{
			//System.out.println("Read text object from ocr file failed.");
			return false;
		}
		
		return true;
	}

	/**
	 * parse the asr transcript file, create for each line a text object.
	 * @param _fileName: asr transcript file
	 * @param _objectList: list to fill
	 * @param _lang: text language
	 * @return: whether parse process successfully finished 
	 */
	private boolean GetTextObjectFromASRTranscript(String _fileName, List<TextObject> _objectList, Language _lang){
		if (_objectList == null) {
			return false;
		}
		
		File f = new File(_fileName);
		try {
			if (f != null && f.exists()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
		        String line;
		        while((line = br.readLine()) != null) {
		        	String [] words = line.split(";");
					TextObject to = new TextObject();
					if (words.length >= 4) {
						to.set_Text(words[1]);
						double start = Double.parseDouble(words[2]);
						double end = Double.parseDouble(words[3]);
						to.set_Start(Math.round(start));
						to.set_End(Math.round(end));
						to.set_Language(_lang);
						_objectList.add(to);
					}
		        }
		        		        
			}else {
				//System.out.println("ASR file can not be found, ner analysis will thus not start");
				return false;
			}
	
		} catch (IOException ioe) {
			//System.out.println("Error occurred while reading asr transcript for further ner analysis");
			return false;
		}
		return true;			
	}
}