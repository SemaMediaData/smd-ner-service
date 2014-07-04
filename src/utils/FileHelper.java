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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


public class FileHelper {
	
	
	/**
	 * This method check whether the given file is empty.
	 * 
	 * @param _filename
	 * @return
	 */
	public static boolean IsFileEmpty (String _filename){
		try {
			FileInputStream fis = new FileInputStream(new File(_filename));  
			
			// read() will return -1 when the 
			// end of the stream has been reached, if the very first read() returns -1 the file must be empty.
			int b = fis.read();  
			if (b == -1)  
			{  
				return true;  
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return false;
	}

	/**
	 * Get the file name after the last '/'
	 * @param realURL
	 * @return
	 */
	public static String GetFileNameFromURL(String _realURL) {
		String fn = "";
		int po = _realURL.lastIndexOf("/");
		if (po != -1) {
			fn = _realURL.substring(po +1);	
		}
		
		return fn;
	}

	public static String GetAudioFileNameFromURL(String _url) {
		String fn = "";
		int po = _url.lastIndexOf("/");
		int popoint = _url.lastIndexOf(".");
		if (po != -1) {
			fn = _url.substring(po + 1, popoint);
			fn += "wav";			
		}
		return fn;
	}

	public static void DeleteFile(String _filePath) {

		if (_filePath == null || _filePath.isEmpty()) {
			return;
		}
		
		File file = new File(_filePath);
		if (file.exists()) {
			try {
				if(!file.delete()){
				}
			} catch (Exception e) {
				e.printStackTrace();				   
			}
		}
	}
	
	public static String GetFileNameWithoutExtension(String _name){
		int pos = _name.lastIndexOf(".");
		if (pos > 0) {
		    _name = _name.substring(0, pos);
		}
		return _name;
	}
	
	 /**
	  Remove a directory and all of its contents.

	  The results of executing File.delete() on a File object
	  that represents a directory seems to be platform
	  dependent. This method removes the directory
	  and all of its contents.

	  @return true if the complete directory was removed, false if it could not be.
	  If false is returned then some of the files in the directory may have been removed.

	*/
	public static boolean DeleteDirectory(String directoryPath) {
		if (directoryPath == null || directoryPath.isEmpty()) {
			return false;
		}
		
		File directory = new File(directoryPath);

	  if (directory == null)
	    return false;
	  if (!directory.exists())
	    return true;
	  if (!directory.isDirectory())
	    return false;

	  String[] list = directory.list();

	  // Some JVMs return null for File.list() when the
	  // directory is empty.
	  if (list != null) {
	    for (int i = 0; i < list.length; i++) {
	      File entry = new File(directory, list[i]);

	      //        System.out.println("\tremoving entry " + entry);

	      if (entry.isDirectory())
	      {
	        if (!DeleteDirectory(entry.getPath()))
	          return false;
	      }
	      else
	      {
	        if (!entry.delete())
	          return false;
	      }
	    }
	  }

	  return directory.delete();
	}
	
	 public static String GetFileExtension(String filename, String extensionSeparator) {
		 int dot = filename.lastIndexOf(extensionSeparator);
	     return filename.substring(dot + 1);
	 }

	public static String GetURLPrefixIfAny(String s) {
		if (s != null && !s.isEmpty()) {
			String head = s.substring(0, 5);
			
			if (head.equalsIgnoreCase("http:") || head.equalsIgnoreCase("rtmp:")) {
				return head;
			}
		}
		return null;
	}

	public static boolean IsFolderExist(String result_path) {
		File f = new File(result_path);
		
		if(f.exists() && f.isDirectory())
			return true;
		return false;
	}
	
	public static boolean IsFileExist(String file_path) {
		File f = new File(file_path);
		
		if(f.exists() && f.isFile())
			return true;
		return false;
	}

	@SuppressWarnings("null")
	public static String ReadFileText(String fname) {
		StringBuilder content = new StringBuilder(); 
		String line;
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF8"));
			while ((line = br.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}

			// Done with the file
			br.close();
			br = null;
			
			return content.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}


}
