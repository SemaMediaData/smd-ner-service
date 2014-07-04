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

public class Enumrations {

	/**
	 * any change of this enum may result the defect 
	 * for starting ocr software. Since video ocr software 
	 * only support "DE" and "EN" yet.
	 * @author haojin
	 *
	 */
	public enum Language{
		DE,
		EN,
		ZH,
		ZH_T,//traditional Chinese
		FR,
		None
	}
	
	/**
	 * differs to AnalysisJobType, because, this var was specifically defined for teletask project,
	 * @author haojin
	 *
	 */
	public enum RecognitionType{
		OCR,
		ASR,
		OCR_ASR
	}
	
	
	public enum AnalysisJobType{
		NER_string,
		NER_txtFile,
		NER_ocrFile,
		NER_asrFile,
		None
	}
	
	public enum StandardEntities{
		Person,
		Location,
		Organization,
		MISC,
		GPE
	}
	
	public enum TextLineType{
		Text,
		KeyPoint,
		Title,
		Footline
	}
	
	public enum FileType{
		XML,
		JSON,
		CSV
	}
}
