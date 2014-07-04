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

public class NerJobObject extends VideoAnalysisJobObject{
	public NerJobObject(){}
	
	/**
	 * @param input_text the input_text to set
	 */
	public void setInput_text(String input_text) {
		this.input_text = input_text;
	}

	/**
	 * @return the input_text
	 */
	public String getInput_text() {
		return input_text;
	}

	private String input_text; 
}
