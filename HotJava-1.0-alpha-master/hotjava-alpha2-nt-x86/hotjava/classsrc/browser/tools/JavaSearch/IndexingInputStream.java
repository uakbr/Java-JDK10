/*
 * @(#)IndexingInputStream.java	1.10 95/03/14 David A. Brown
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
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
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;


/** Input stream to parse documents for indexing */
class IndexingInputStream extends BufferedInputStream {

    /** "Document type" of the file we're parsing.
     *  See "Document type" in Doc.java.
     */
    private int docType = Doc.INVALID_TYPE;


    /** Basic constructors; see FileInputStream for details */
    public IndexingInputStream(String name) {
        super(new FileInputStream(name), 2048);
    }

/*
    public IndexingInputStream(File file) {
        super(file, 1024);
    }
*/

    /** Set the Document Type */
    public void setDocType(int type) {
	docType = type;
    }


    /** 
     *  Return the next "word" in the input stream, null on EOF.
     */
    public String getWord() {
	int c;

	// First, skip over any non-alphanumeric characters,
	//   and check for EOF
	while (true) {
	    c = read();

	    if (c == -1) return null;
	    if (isAlphanumeric((char)c)) break;

	    // Doc Type-specific features:
	    if ((docType == Doc.HTML_TYPE) && (c == '<')) {
		// If we detect a '<'...
		while ((c = read()) != '>') {
		    //   skip till the next '>', but bail on EOF
		    if (c == -1) return null;
		}
	    }
	    // Does NEWS need anything special here?  Probably not...

	}

	// Start the input buffer
	StringBuffer input = new StringBuffer();
	input.appendChar((char)c);

	// Process more chars:
	//
	// We're guaranteed to have at least a one-char word,
	//   so we'll return a string either when we hit a
	//   delimiter, OR if we hit EOF.  (Then, the next call to
	//   this method will immediately return null, signaling
	//   the EOF.)
	//
	while (true) {
            c = read();

	    if (isAlphanumeric((char)c)) {
		input.appendChar((char)c);
	    }
	    else {	// c is non-alphanumeric, or could be -1 if EOF
		// If by chance c was '<'...
		if ((docType == Doc.HTML_TYPE) && (c == '<')) {
                    while ((c = read()) != '>') {
                        //   skip till the next '>', but bail on EOF
                        if (c == -1) break;
                    }
                }

		// Ok, process and return the string we've built up
		downcase(input);
		return input.toString();
	    }
	}
    }
	

    /**
     *  Return true if the given character is alphanumeric,
     *  i.e. NOT a word delimiter
     */
    private static boolean isAlphanumeric(char c) {

	if ((c >= 'a') && (c <= 'z')) return true;
	if ((c >= 'A') && (c <= 'Z')) return true;
	if ((c >= '0') && (c <= '9')) return true;

	// REMIND:  Is it always the right thing to have "'"
	//   count as alphanumeric?
	if (c == '\'') return true;

	// FUTURE:  maybe other chars might count as 'alphanumeric',
	//   for example "_" if it's part of any method names...

	return false;
    }


    /**
     *  Convert a StringBuffer to lowercase, in place.
     *  Utility function used by a few JavaSearch classes.
     */
    public static void downcase(StringBuffer buf) {
	for (int i=0; i<buf.length(); i++) {
	    char c = buf.charAt(i);
	    if (c>='A' && c<='Z') {
		c -= 'A'-'a';
		buf.setCharAt(i,c);
	    }
	}
    }

    /**
     *  Return a downcased version of the specified String.
     *  Based on downcase(StringBuffer buf).
     *  Utility function used by a few JavaSearch classes.
     */
    public static String downcase(String s) {
	StringBuffer sb = new StringBuffer().append(s);
	downcase(sb);
	return sb.toString();
    }




    // FUTURE stuff:
    //
    // Method to return the current position in the input file,
    //   or better, the starting position of the last word returned
    //   by getWord().  This will be needed if the indexer ever wants
    //   to keep positional info in the index.
    //

}
