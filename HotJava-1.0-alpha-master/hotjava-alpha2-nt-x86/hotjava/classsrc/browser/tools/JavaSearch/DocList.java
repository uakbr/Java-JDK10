/*
 * @(#)DocList.java	1.10 95/03/14 David A. Brown
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
//import Doc;

import java.util.Vector;
import java.io.*;

/**
 * DocList:  a list of Documents 
 *
 * Basically a collection of Doc objects, along with some
 * functionality to manipulate them.
 */
class DocList {

    /** All the Docs in thie DocList.  This is a Vector of Doc objects */
    private Vector entries;

    /** Header ('magic number') at top of docs file */
    static final String docsFileHeader = "JavaSearch-docs";

    /** Basic constructor; create an empty doclist */
    public DocList() {
	//System.out.println("DocList constructor...");
	entries = new Vector();
    }

    /** 
     *  Create a DocList full of Docs, to be loaded from the specified
     *  Database, using the Doc IDs in 'idvector'.
     */
    public DocList(Database db, IDVector idvector) {
	this();
	//System.out.println("DocList constructor, from DB "+db.dbName+
	//	 ".  Creating "+idvector.count+" docs; idvector's capacity is "+idvector.ids.length+"...");

	// Open .docs and .docindex files: 
	RandomAccessFile docsFile =
	    new RandomAccessFile(db.docsFilename,"r");
	RandomAccessFile docindexFile =
	    new RandomAccessFile(db.docindexFilename,"r");

	for (int i=0; i<idvector.count; i++) {
	    char docid = idvector.ids[i];
	    Doc newdoc = new Doc(docid, docsFile, docindexFile);
	    addDoc(newdoc);
	}

	docsFile.close();
	docindexFile.close();
    }

    /** Add a Doc object to the end of our list.  */
    public void addDoc(Doc doc) {
	entries.addElement(doc);
    }

    /** Get a specified Doc out of our list */
    public Doc getDocAt(int i) {
	return (Doc)entries.elementAt(i);
    }

    /** Return the size of (number of Docs in) this DocList */
    public int size() {
	return entries.size();
    }

    /** Dump out the full contents of this DocList */
    public void dump() {
	//System.out.println("DocList.dump():  This doc list contains "+entries.size()+" Doc"+((entries.size()==1)?"":"s")+".");

	for (int i=0; i<entries.size(); i++) {
            Doc d = (Doc)entries.elementAt(i);
	    //System.out.println(" Doc # "+i+"\t"+d);
        }
	System.out.println("-----");
    }

    /** 
     * Save this DocList to disk, as part of the database 'db'.
     */
    public void saveAs(Database db) {
	System.out.println("DocList.saveAs()...");

	// REMIND:  maybe have a sanity check here to ensure
	//   that all the IDs of docs in the list are in
	//   order, with no IDs skipped or duplicated?

	// Open two output streams (docs + docindex).
	// Write each Doc to the .docs file, and for
	//   each one add a position entry to the .docindex file.
	//
	System.out.println("Opening docs file '" + db.docsFilename + "'...");
	FileOutputStream fileout = new FileOutputStream(db.docsFilename);
	DataOutputStream out = new DataOutputStream(fileout);
	//
	System.out.println("Opening docindex file '" + db.docindexFilename + "'...");
	FileOutputStream docfileout = new FileOutputStream(db.docindexFilename);
	DataOutputStream docout = new DataOutputStream(docfileout);

	// Write some header info.
	// Future:  version numbers?  other db or index info?
	out.writeBytes(docsFileHeader);
	out.writeByte('\n');

	// Write a dummy int to the docindex file, since there's
	//    no doc ID zero!
	docout.writeInt(0);

	// Ok, just write every Doc in order...
	for (int i=0; i<entries.size(); i++) {
            int outPos = out.size();
            Doc d = (Doc)entries.elementAt(i);
	    d.writeToStream(out);
	    // Write the docindex record
            docout.writeInt(outPos);
	}
	int docsSize = out.size();
	fileout.close();
	docfileout.close();
	System.out.println("Wrote docs file ("+docsSize+" bytes).");
    }


}
