/*
 * @(#)Searcher.java	1.19 95/03/20 David A. Brown
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
import java.util.Enumeration;
import java.io.*;

/**
 * Searcher:  Interface for *searching* a JavaSearch database.
 *
 */
class Searcher {

    /** The database we're searching */
    Database db;

    /** The window driving us, if any (for messages; can be null). */
    SearchWindow searchWindow;

    /** The stop list, which we use to reject unimportant words. **/
    StopList stopList;

    /** How many words we've read out of the index so far
     *  doing the current search. */
    int indexReadCounter;

    // Modes used internally by doSearch()
    static final int  OR_MODE = 0;
    static final int AND_MODE = 1;
    static final int NOT_MODE = 2;

    /** Searcher constructor.  Specify a Database object and window. */
    public Searcher(Database theDB, SearchWindow sWin) {
	//System.out.println("Searcher [Database "+theDB.dbName+"]...");
	db = theDB;
	searchWindow = sWin;
	stopList = new StopList(System.getenv("HOTJAVA_HOME")+File.separator+
				"doc"+File.separator+
				StopList.defaultFile);
    }

    /** Searcher constructor.  Specify a Database object. */
    public Searcher(Database theDB) {
	this(theDB, null);
    }

    /**
     * Main "Search Engine" function, specifying a search string.
     *
     * Specify a search string, consisting of keywords to search for,
     * along with optional boolean keywords "and", "or" and "not".
     * Words must be separated by spaces!!

     * This method just breaks the search string into a Vector
     * of search words, and calls doSearch(Vector searchWords)
     * to do the actual searching.
     *
     * See below for details.
     */
    public DocList doSearch(String searchString) {
	//System.out.println("doSearch:  searchString '"+searchString+"'...");
	if ((searchString == null) || (searchString.length()==0)) {
	    return null;
	}

	Vector wordVector = new Vector();
	int wordStart = 0;
	// Break apart searchString, adding keywords to wordVector.
	while (wordStart<searchString.length()) {
	    // Skip over any spaces in searchString
	    while (searchString.charAt(wordStart) == ' ') {
		wordStart++;
		if (wordStart >= searchString.length()) break;
	    }
	    if (wordStart >= searchString.length()) break;
	    // Ok, wordStart really points to the start of a word.
	    int wordEnd = searchString.indexOf(' ',wordStart);
	    if (wordEnd == -1) wordEnd = searchString.length();
	    String keyword = searchString.substring(wordStart,wordEnd);
	    wordVector.addElement(keyword);
	    wordStart = wordEnd+1;
	}
	return doSearch(wordVector);
    }

    
    /**
     * Main "Search Engine" function, specifying a Vector of
     * search words.
     *
     * The searchWords parameter is a Vector of words representing
     * a "search string" (ie. keywords to search for, mixed in with
     * the optional boolean keywords "and", "or" and "not".)
     *
     * The search words are processed left to right
     * (i.e. in the order they appear in the Vector), with any
     * boolean keyword affecting how the *subsequent* word's
     * hits are merged into the final result.  Note that
     * "not" means "and not".
     *
     * Example: the Vector for "foo and bar or baz not mumble" is
     * processed as follows:
     *     - Find all documents containing "foo"
     *     - INTERSECT this result with all documents containing "bar"
     *     - Now ADD all documents containing "baz" to the result
     *     - INTERSECT this result with all documents NOT
     *           containing "mumble"
     *
     * Again, remember "not" means "and not".  This means
     * that the results you get from searching for simply "not foo"
     * might not be what you expect! 
     *
     * This method returns a DocList containing all documents
     * which match the search string, in Doc ID order,
     * or null if no documents match the search string.
     */
    public DocList doSearch(Vector searchWords) {

	//System.out.println("doSearch:  Number of searchWords: "+
	//		   searchWords.size()+"...");
	//System.out.println("doSearch:  searchWords "+searchWords);

	// Start of with an empty set of Doc IDs
	IDVector resultVector = new IDVector();

	// The query words we've dropped due to presence on stop list
	Vector stoppedWords = new Vector(2);

	// The "boolean mode" to use when combining
	//   our next set of Doc IDs with the
	//   result set we're accumulating.
	int booleanMode = OR_MODE; // OR is the default
	
	boolean onlyStopWords = true; // so far we've only seen stop words
	Enumeration e = searchWords.elements();
	while (e.hasMoreElements()) {
	    String keyword = (String)e.nextElement();

	    // Make sure keyword is all lower-case
	    keyword = IndexingInputStream.downcase(keyword);
	    //System.out.println("doSearch:  processing keyword '"+keyword+"'.");

	    // If the word is a boolean control keyword,
	    //   update booleanMode and continue
	    //   on to the next word
	    if (keyword.equals("or")) {
		booleanMode = OR_MODE;
		continue;
	    }
	    else if (keyword.equals("and")) {
		booleanMode = AND_MODE;
		continue;
	    }
	    else if (keyword.equals("not")) {
		booleanMode = NOT_MODE;
		continue;
	    }

	    // Is it on our stop list?  If so, skip it.
	    if (stopList.isStopWord(keyword)) {
		stoppedWords.addElement(keyword);
		continue;
	    }

	    // Ok, keyword is a real word.  Get an IDVector for it.
	    Word w = getWordFromIndex(keyword);
	    //System.out.println("doSearch:  getWordFromIndex returned: "+w);

	    // Now we have a bunch of Doc IDs in w.idvector
	    //   (or, w may be null!)
	    // Merge that vector into resultVector, using
	    //   the current booleanMode:
	    switch (booleanMode) {
	    case AND_MODE:
		if (w == null) {
		    // ANDing with a null set gives you a null set!
		    System.out.println("ANDing with a null set: clearing resultVector!");
		    resultVector.clear();
		}
		else {
		    // If we've only seen stopwords so far, don't bother to AND
		    // the null result with anything.
		    if (onlyStopWords) {
			resultVector = w.idvector;
		    } else {
			resultVector.intersectWith(w.idvector);
		    }
		}
		break;
	    case NOT_MODE:
		if (w == null) {
		    // "AND NOT <null set>" doesn't change
		    //    what you started with.
		    //System.out.println("NOT with a null set; resultVector unaffected.");
		}
		else {
		    resultVector.intersectWithNot(w.idvector);
		}
		break;
	    default:		// OR mode
                if (w == null) {
                    // "OR <null set>" doesn't change what you started with.
		    //System.out.println("OR with a null set; resultVector unaffected.");
		}
		else {
		    resultVector.unionWith(w.idvector);
		}
		break;
	    }

	    // Reset booleanMode for the next word (we imply OR
	    //   if there's no boolean control keyword)
	    booleanMode = OR_MODE;

	    // If we've gotten this far, we're seeing real words.
	    onlyStopWords = false;
	}


	// Done with the search!  Results are in resultVector.

	// Display stopped words, if any.
	if (stoppedWords.size() > 0) {
	    if (searchWindow == null) {
		System.out.print("Searcher: Discarded ");
		for (int i = 0; i < stoppedWords.size(); i++) {
		    System.out.print(stoppedWords.elementAt(i)+" ");
		}
		System.out.print("\n");
	    } else {
		searchWindow.displayStoppedWords(stoppedWords);
	    }
	}
	
	if (resultVector.count == 0) {
	    //System.out.println("doSearch:  resultVector.count is 0!");
	    return null;
	}

	//System.out.println("doSearch:  resultVector.count is "+
	//		   resultVector.count);

	// REMIND:  Would be cool to have a MAX_SEARCH_HITS paramater
	//    here, so we don't create a DocList if we have an absurdly
	//    large number of hits.
	//
	//    If (resultVector.count > MAX_SEARCH_HITS), we could either
	//    truncate resultVector to an acceptable length,
	//    or (better) return a special value indicating
	//    we had too many hits (so the user should refine his
	//    search, and try again...)
	//
	//    Actually, it would be cool for this method to
	//    somehow return a number_of_hits *and* a DocList!
	//    So if the DocList comes back null, the
	//    number_of_hits tells you whether your got NO hits,
	//    or too many hits.

	// Generate and return a new DocList based on resultVector --
	//  this involves opening the .docs and .docindex files,
	//  and reading a Doc object for each hit.
	DocList dl = new DocList(db, resultVector);
	return dl;
    }

    /** Look up the specified word in the index, and return a
     *  Word object.  Return null if the word wasn't found. */
    public synchronized Word getWordFromIndex(String aWord) {
	//System.out.println("getWordFromIndex:  finding '"+aWord+"'...");
	indexReadCounter = 0;
	
	// Open the index and qindex files
	RandomAccessFile indexFile =
	    new RandomAccessFile(db.indexFilename,"r");
	RandomAccessFile qindexFile =
	    new RandomAccessFile(db.qindexFilename,"r");

	int numWords = qindexFile.length()/4;
	//System.out.println("getWordFromIndex:  qindexFile has "+
	//		   numWords+" entries.");


	// Binary search the qindex file (each qindex
	//   entry points at a word in the index file)
	Word resultWord = bsearchWord(aWord,
				      0, numWords-1,
				      qindexFile, indexFile);
	//System.out.println("getWordFromIndex:  Got resultWord: "+resultWord);
	//System.out.println("   Did "+indexReadCounter+" read"+
	//		   ((indexReadCounter==1)?"":"s")+
	//		   " from the index/qindex files.");
	return resultWord;
    }

    //
    // These methods are the guts of the getWordFromIndex() functionality
    //

    /**
     * Binary-search index file for the specified word.
     * Returns a Word object if the word is found;
     * Returns null if the word is not in the index.
     * Calls itself recursively.
     */
    private Word bsearchWord(String word, int loPos, int hiPos,
			     RandomAccessFile qindexFile,
			     RandomAccessFile indexFile) {

	//System.out.println("  bsearchWord ("+word+"), range ["+
	//		   loPos+","+hiPos+"]...");

	if (loPos > hiPos) {
	    //System.out.println("  loPos > hiPos.  ["+loPos+","+hiPos+"].  Returning null.");
	    return null;
	}
	else if (loPos == hiPos) {
	    if (word.equals(getWordAtPos(loPos, qindexFile, indexFile))) {
		return new Word(word,indexFile);
	    }
	    else {
		return null;
	    }
	}
	else {
	    // loPos and hiPos describe a range.  Check the
	    //   *middle* of the range, and recurse.
	    int midPos = (loPos+hiPos)/2;
	    String midWord = getWordAtPos(midPos, qindexFile, indexFile);
	    //System.out.println("    midPos "+midPos+
	    //	       ", midWord '"+midWord+"'.");
	    int compareResult = word.compareTo(midWord);
	    if (compareResult == 0) {
		// Direct hit!
		return new Word(word,indexFile);
	    }
	    else if (compareResult < 0) {
		// word < midWord:  Search the lower half
		return bsearchWord(word, loPos, midPos-1,
				   qindexFile, indexFile);
	    }
	    else {
		// word > midWord:  Search the upper half
		return bsearchWord(word, midPos+1, hiPos,
				   qindexFile, indexFile);
	    }
	}
    }

    /** Return the word found at the Nth position in the index file.
     *  Leaves indexFile pointing at the word's doc entries. */
    private String getWordAtPos(int n, RandomAccessFile qindexFile,
				RandomAccessFile indexFile) {
	//System.out.print("    getWordAtPos "+n+"...  ");

	// Read an index file position from the qindex file.
	// qindex entries are ints, which are 4 bytes long.
	qindexFile.seek(n*4);
	int indexPos = qindexFile.readInt();

	// Read just the word (a String) from the index file
	indexFile.seek(indexPos);
	String word = indexFile.readLine();
	//System.out.println("found word '"+word+"'.");

	indexReadCounter++;	// One indexReadCounter increment
				//   means one int from the qindexFile
				//   and one String from the indexFile.

	return word;
    }

}
