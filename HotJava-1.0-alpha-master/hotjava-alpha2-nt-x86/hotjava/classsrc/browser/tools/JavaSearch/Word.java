/*
 * @(#)Word.java	1.9 95/03/14 David A. Brown
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

import java.util.Vector;
import java.io.*;
import javaindex;

/** One entry in an Index */
class Word {
    
    /** The word this index entry is for */
    String word;

    /** Doc IDs containing this word */
    IDVector idvector;

    /** Basic Word constructor */
    Word() {
	idvector = new IDVector();
    }

    /** Create a new Word for the specified word */
    Word(String aWord) {
	this();
	word = aWord;
    }

    /**
     * Create a Word object for the specified (String) word,
     * reading doc entries out of indexFile.  NOTE that
     * indexFile must point to the word's doc entries!
     */
    public Word(String wordStr, RandomAccessFile indexFile) {
	this(wordStr);
	readDocEntries(indexFile);
    }

    /** Generate a simple printed representation of this Word */
    public String toString() {
	String s = "Word '" + word + "'\tdocs";
	for (int i=0; i<idvector.count; i++) {
	    s += " " + (int)idvector.ids[i];
	}
	return s;
    }

    /** Write this Word to the specified Output stream */
    void writeToStream(DataOutputStream out) {
	// Our word, followed by newline
	out.writeBytes(word);
	out.writeByte('\n');

	// All our doc refs.  These are chars, terminated by a 0.
	for (int i=0; i<idvector.count; i++) {
	    out.writeChar(idvector.ids[i]);
        }
	out.writeChar((char)0);
    }

    /**
     *  Read a single index entry from the specified stream.
     *  Returns a Word object, or null if stream hits EOF.
     *  The stream must be pointing at the start of a
     *  valid index entry!!
     */
    static Word readFromStream(DataInputStream in) {
	String word = in.readLine();
	//System.out.println("  readFromStream:  got a word: "+word);
	if (word == null) {
	    return null;
	}
	Word w = new Word(word);

	// Read doc ids, appending them to w, until we get a 0.
	char c;
	while ((c = in.readChar()) != 0) {
	    //System.out.println("    got a docID: "+c);
	    w.idvector.appendID(c);
	}
	return w;
    }

    /**
     * Read a Word's doc entries out of indexFile.  NOTE that
     * indexFile must point to the word's doc entries!!
     *
     * This reads chars from indexFile, till we get a 0.
     * This duplicates a bit of the functionality
     * in readFromStream(), but unfortunately
     * RandomAccessFiles are not InputStreams...
     */
    public void readDocEntries(RandomAccessFile indexFile) {

	// Read doc ids, appending them to ie, until we get a 0.
        char c;
        while ((c = indexFile.readChar()) != 0) {
            idvector.appendID(c);
        }
	//System.out.println("  Read doc entries for "+this);
    }




}
