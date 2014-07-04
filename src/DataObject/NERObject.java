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

import utils.Enumrations.StandardEntities;

public class NERObject extends TextObject{
	public NERObject(){}
	
	/**
	 * @param _EntityType the _EntityType to set
	 */
	public void set_EntityType(StandardEntities _EntityType) {
		this._EntityType = _EntityType;
	}

	/**
	 * @return the _EntityType
	 */
	public StandardEntities get_EntityType() {
		return _EntityType;
	}

	private StandardEntities _EntityType;
}
