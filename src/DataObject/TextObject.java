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
import utils.Enumrations.TextLineType;

public class TextObject {
	public TextObject(){}
	
	/**
	 * @param _Id the _Id to set
	 */
	public void set_Id(String _Id) {
		this._Id = _Id;
	}

	/**
	 * @return the _Id
	 */
	public String get_Id() {
		return _Id;
	}

	/**
	 * @param _Text the _Text to set
	 */
	public void set_Text(String _Text) {
		this._Text = _Text;
	}

	/**
	 * @return the _Text
	 */
	public String get_Text() {
		return _Text;
	}

	/**
	 * @param _Start the _Start to set
	 */
	public void set_Start(double _Start) {
		this._Start = _Start;
	}

	/**
	 * @return the _Start
	 */
	public double get_Start() {
		return _Start;
	}

	/**
	 * @param _End the _End to set
	 */
	public void set_End(double _End) {
		this._End = _End;
	}

	/**
	 * @return the _End
	 */
	public double get_End() {
		return _End;
	}

	/**
	 * @param _TextLineType the _TextLineType to set
	 */
	public void set_TextLineType(TextLineType _TextLineType) {
		this._TextLineType = _TextLineType;
	}

	/**
	 * @return the _TextLineType
	 */
	public TextLineType get_TextLineType() {
		return _TextLineType;
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

	private String _Id; 
	private String _Text;
	private double _Start;
	private double _End;
	private TextLineType _TextLineType;
	private Language _Language;
}
