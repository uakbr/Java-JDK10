/*
 * @(#)StopList.java	1.5 95/03/14 Herb Jellinek
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */


package browser.tools.JavaSearch;

import java.util.Hashtable;
import java.io.*;

public class StopList {

    /**
      * Constants
      */
    public static final String defaultFile = "real-short-stoplist.txt";

    // We store the stop words here, hashed to themselves.
    protected Hashtable wordHash;

    // True if we want to reject 'words' that are all numeric
    protected boolean stopNumerics;

    public StopList(String stopFile, boolean noNumerics) {
	stopNumerics = noNumerics;
	wordHash = readStopList(stopFile);
	if (wordHash == null) {
	    wordHash = new Hashtable();
	}
    }

    public StopList(String stopFile) {
	this(stopFile, true);
    }

    public int size() {
	return wordHash.size();
    }
    
    public String toString() {
	String result = "StopList[nums "+(stopNumerics ? "no" : "ok");
	result += ", len="+size()+"]";
	return result;
    }
    
    /**
     *  Returns true if the word is a stop word (on the stop list or
     *  all-numeric).
     */
    public boolean isStopWord(String word) {
	return ((stopNumerics && allNumeric(word)) ||
		(wordHash.get(word) != null));
    }

    /**
     *  Returns true if the word is all-numeric.
     */
    public static boolean allNumeric(String s) {
	for (int i = 0; i < s.length(); i++) {
	    if (!Character.isDigit(s.charAt(i))) return false;
	}
	return true;
    }

    /**
     *  Read the contents of the stop list file into
     *  wordHash.  Words in the file are stored one per line.
     *  Skips all blank lines and any line beginning with ';'.
     */
    public Hashtable readStopList(String fileName) {
	Hashtable hash = new Hashtable();
	FileInputStream fileIn = new FileInputStream(fileName);
	DataInputStream in = new DataInputStream(fileIn);

	while (true) {
	    String word = in.readLine();
	    if (word == null) break;
	    if (word.length() == 0 || word.startsWith(";")) continue;
	    hash.put(word, word);
	}

	//System.out.println("Read "+hash.size()+"-word stop list.");
	fileIn.close();
	
	return hash;
    }

}
