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

package DataObject;

import utils.Enumrations.Language;
import utils.Enumrations.AnalysisJobType;

public class VideoAnalysisJobObject implements JobObject{
	public VideoAnalysisJobObject(){}
	
	/** Overriding Object's equals()
	 * Two videoAnalysisJob object are equal, if they have the same video full name.
	 * This means that a video should not be analyzed by different threads parallelly. 
	 */
	@Override
	public boolean equals(Object o) {
		if ( this == o ) return true;
	    if ( !(o instanceof VideoAnalysisJobObject) ) return false;
	    VideoAnalysisJobObject myO = (VideoAnalysisJobObject)o;
	    return this.getVideoName() == myO.getVideoName();
	}
	
	
	//================getter & setter================//
	/**
	 * @param _JobType the _JobType to set
	 */
	public void set_JobType(AnalysisJobType _JobType) {
		this._JobType = _JobType;
	}


	/**
	 * @return the _JobType
	 */
	public AnalysisJobType get_JobType() {
		return _JobType;
	}


	/**
	 * @param _VideoName the _VideoName to set
	 */
	public void setVideoName(String videoName) {
		this._VideoName = videoName;
	}


	/**
	 * @return the _VideoName
	 */
	public String getVideoName() {
		return _VideoName;
	}


	/**
	 * @param _AnalysisResultPath the _AnalysisResultPath to set
	 */
	public void set_AnalysisResultPath(String _AnalysisResultPath) {
		this._AnalysisResultPath = _AnalysisResultPath;
	}


	/**
	 * @return the _AnalysisResultPath
	 */
	public String get_AnalysisResultPath() {
		return _AnalysisResultPath;
	}


	/**
	 * @param _Language the _Language to set
	 */
	public void set_Language(Language _Language) {
		this._Language = _Language;
	}

	/**
	 * @return the _Language
	 */
	public Language get_Language() {
		return _Language;
	}


	/**
	 * @param _ConfigName the _ConfigName to set
	 */
	public void set_ConfigName(String _ConfigName) {
		this._ConfigName = _ConfigName;
	}

	/**
	 * @return the _ConfigName
	 */
	public String get_ConfigName() {
		return _ConfigName;
	}


	/**
	 * @param _ImageFolderPath the _ImageFolderPath to set
	 */
	public void set_ImagePath(String _ImageFolderPath) {
		this._ImagePath = _ImageFolderPath;
	}

	/**
	 * @return the _ImageFolderPath
	 */
	public String get_ImagePath() {
		return _ImagePath;
	}


	/**
	 * @param _VideoURL the _VideoURL to set
	 */
	public void set_VideoURL(String _VideoURL) {
		this._VideoURL = _VideoURL;
	}

	/**
	 * @return the _VideoURL
	 */
	public String get_VideoURL() {
		return _VideoURL;
	}


	/**
	 * @param _JobId the _JobId to set
	 */
	public void set_JobId(String _JobId) {
		this._JobId = _JobId;
	}

	/**
	 * @return the _JobId
	 */
	public String get_JobId() {
		return _JobId;
	}


	/**
	 * @param _StatusInfoCallbackURL the _StatusInfoCallbackURL to set
	 */
	public void set_StatusInfoCallbackURL(String _StatusInfoCallBackURL) {
		this._StatusInfoCallbackURL = _StatusInfoCallBackURL;
	}

	/**
	 * @return the _StatusInfoCallbackURL
	 */
	public String get_StatusInfoCallbackURL() {
		return _StatusInfoCallbackURL;
	}


	/**
	 * @return the _asrFilePath
	 */
	public String get_asrFilePath() {
		return _asrFilePath;
	}

	/**
	 * @param _asrFilePath the _asrFilePath to set
	 */
	public void set_asrFilePath(String _asrFilePath) {
		this._asrFilePath = _asrFilePath;
	}


	/**
	 * @return the _ocrFilePath
	 */
	public String get_ocrFilePath() {
		return _ocrFilePath;
	}

	/**
	 * @param _ocrFilePath the _ocrFilePath to set
	 */
	public void set_ocrFilePath(String _ocrFilePath) {
		this._ocrFilePath = _ocrFilePath;
	}


	/**
	 * @return the _txtFilePath
	 */
	public String get_txtFilePath() {
		return _txtFilePath;
	}

	/**
	 * @param _txtFilePath the _txtFilePath to set
	 */
	public void set_txtFilePath(String _txtFilePath) {
		this._txtFilePath = _txtFilePath;
	}


	private AnalysisJobType _JobType;
	private String _VideoName;
	private String _AnalysisResultPath;
	//for downloading video file
	private String _VideoURL;
	//for image ocr task
	private String _ImagePath;
	
	
	private Language _Language;
	private String _ConfigName;
	private String _JobId;
	
	private String _StatusInfoCallbackURL;
	
	private String _asrFilePath;
	private String _ocrFilePath;
	private String _txtFilePath;
}
