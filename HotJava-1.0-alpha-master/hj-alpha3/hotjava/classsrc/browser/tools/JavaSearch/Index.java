/*
 * @(#)Index.java	1.15 95/03/14 David A. Brown
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
import java.util.*;
import java.io.*;
//import javaindex;

/**
 * Index: the master word/doc inverted index for an JavaSearch
 *         database.  Basically a collection of index entries, along
 *         with some * functionality to manipulate them.
 *
 *     Important: This class is only used when BUILDING an JavaSearch
 *         index; when doing a search, the Searcher object directly
 *         reads the on-disk .index and .qindex files.
 *
 *   REMIND:  This class needs a couple of serious
 *     optimizations!  (Especially look for "btree"
 *     comments below...)
 *
 */
class Index {

    //
    // Various tweakable indexing parameters
    //
    /** Shortest word we will index */
    static final int MIN_WORD_LENGTH = 2;

    //
    // Misc constants
    //
    /** Header ('magic number') at top of index files */
    static final String indexFileHeader = "JavaSearch-index";


    /** 
     * All the words in this Index.  This is a Vector of Word objects.
     * This is only used 
     */
    Vector entries;
    Hashtable table;    // changed vector to use hashtable instead

    StopList stopList = null;
    
    /** Create a new, empty index */
    public Index(String stopfile) {
	System.out.println("Index constructor...");
	//entries = new Vector();
        table = new Hashtable();

	try {
	    if (stopfile != null) {
		stopList = new StopList(stopfile);
	    }
	} catch (IOException e) {
	    System.err.println("No such stop list file - \""+stopfile+"\"");
	    System.err.println("Ignoring stop list.");
	    stopList = null;
	}
    }


    /**
     *  Add all the words we find in the specified IndexingInputStream
     *  to the Index, associating them with the specified Doc.
     */
    public void addDocToIndex(Doc doc, IndexingInputStream in) {
	String word;
	int startTime = System.nowMillis();

	Hashtable seenInDoc = new Hashtable();
	
	while ((word = in.getWord()) != null) {
	    //System.out.println("  word: '"+word+"'");
	    if ((word.length() >= MIN_WORD_LENGTH) &&
		(!stopList.isStopWord(word))) {
		if (seenInDoc.get(word) == null) {
		    seenInDoc.put(word, word);

		    // It's not a stopword, too short, or already
		    // seen in this document, so add word to index.
		    
		    //System.out.println("adding index entry: '"+word+"'");
		    
		    Word w = getWord(word);
		    if (w == null) {
			w = addWord(word);
		    }
		    w.idvector.appendID(doc.docID);
		}
	    }
	}

	int totalTime = System.nowMillis() - startTime;
	javaindex.parseTime += totalTime;
    }

    /** 
     *  Return the Word in our entries Vector for the
     *  specified word, or null if we don't have an entry for 'word'.
     */
    public Word getWord(String word) {

	// REMIND:  This is terrible!  When we're indexing, we
	//   should store the Words we're accumulating in memory
	//   in a btree, not in an unsorted Vector!!!
	//     (Sorry, I didn't get around to implementing
	//     a btree utility class.  But this only makes
	//     indexing slow, not searching...   - dab )
	
        Word w = (Word)table.get(word);
        return w;
/* change to hashtable
	for (int i=0; i<entries.size(); i++) {
	    Word w = (Word)entries.elementAt(i);
	    if (w.word.equals(word)) {
		return w;
	    }
	}
	return null;
*/
    }

    /**
     * Create a Word object for the specified word,
     * and add it to our entries Vector.  Don't call this
     * unless you're sure we don't already have a Word object
     * for this word!
     */
    public Word addWord(String word) {

	Word w = new Word(word);

        table.put(word, w);
/* change to hashtable
	// REMIND:  We really should be inserting the Word into
	//   a btree here!
	entries.addElement(w);
*/
	return w;
    }


    /**
     *  Sort the 'entries' vector alphabetically by each
     *    entry's word.
     *
     *    REMIND:  This would be unnecessary if we used
     *             a btree to hold our Words...
     */
    public void sort() {
	System.out.print("Sorting index...  ");
	int starttime = System.nowMillis();
	// An entry is    Word w = (Word)entries.elementAt(i);
	// An entry's word is  w.word

        // copy the hashtable into a vector
        int tableSize = table.size();
        entries = new Vector(tableSize);
        Enumeration enumeration = table.elements();
        for (int i=0; i<tableSize; i++) {
            entries.addElement(enumeration.nextElement());
        }

	qsort_entries(0, entries.size()-1);

	int sortTime = System.nowMillis() - starttime;
	System.out.println("Done. Time to sort: " + sortTime + " ms.");
	javaindex.numSorts++;
	javaindex.sortTime += sortTime;
    }

    /** Guts of the qsort algorithm. 
     *  Stolen from jag's QSortAlgorithm.java demo */
    private void qsort_entries(int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        if (lo >= hi)
            return;
        String mid = ((Word)entries.elementAt((lo + hi) / 2)).word;
        while (lo < hi) {
            while (lo<hi && (((Word)entries.elementAt(lo)).word.compareTo(mid) < 0))
		lo++;
            while (lo<hi && (((Word)entries.elementAt(hi)).word.compareTo(mid) > 0))
                hi--;
            if (lo < hi) {
		Word w_lo = (Word)entries.elementAt(lo);
		Word w_hi = (Word)entries.elementAt(hi);
		entries.setElementAt(w_lo,hi);
		entries.setElementAt(w_hi,lo);
            }
        }
        if (hi < lo) {
            int T = hi;
            hi = lo;
            lo = T;
        }
        qsort_entries(lo0, lo);
        qsort_entries(lo == lo0 ? lo+1 : lo, hi0);
    }




    /** Print out the full contents of this index.  For debuggging. */
    public void dump() {
	System.out.println("Index.dump():  This index contains "+entries.size()+" entries.");

	for (int i=0; i<entries.size(); i++) {
            Word w = (Word)entries.elementAt(i);
	    System.out.println("  "+i+"\t"+w);
        }
	System.out.println("-----");
    }




    /**
     * Save this JavaSearch index to disk, as part of the database 'db'.
     * Returns the total size of the .index file.
     */
    public void saveAs(Database db) {
	System.out.println("Index.saveAs()...");

	// Ensure we're sorted.  No big deal if someone already
	//   manually called sort() since qsort is reasonably fast
	//   if we're already sorted.
	sort();

	// Open two output streams (index + qindex).
	// Write each Word to the .index file, and for
	//   each one add a position entry to the .qindex file.
	//
	System.out.println("Opening index file '" + db.indexFilename + "'...");
	FileOutputStream fileout = new FileOutputStream(db.indexFilename);
	DataOutputStream out = new DataOutputStream(fileout);
	//
	System.out.println("Opening qindex file '" + db.qindexFilename + "'...");
	FileOutputStream qfileout = new FileOutputStream(db.qindexFilename);
	DataOutputStream qout = new DataOutputStream(qfileout);

	// Write some header info.
	// Future:  version numbers?  other db or index info?
	out.writeBytes(indexFileHeader);
	out.writeByte('\n');

	// Ok, just write every Word in order...
	for (int i=0; i<entries.size(); i++) {
	    int outPos = out.size();
            Word w = (Word)entries.elementAt(i);
	    w.writeToStream(out);
	    // Write the qindex record
	    qout.writeInt(outPos);
	}
	int indexSize = out.size();
	fileout.close();
	qfileout.close();
	System.out.println("Wrote index file ("+indexSize+" bytes).");
    }




}
