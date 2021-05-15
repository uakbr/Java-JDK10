/*
 * @(#)javasearch.java	1.11 95/03/14 David A. Brown
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

import java.io.*;

/**
 *  javasearch:  Simple command-line interface to perform searches
 *              on an JavaSearch database.
 *
 *    This is intended to be an example of how to use the
 *    Database, Searcher, DocList and Doc classes in YOUR
 *    application to search an JavaSearch database.
 */
class javasearch {

    public static void main(String args[]) {
        Database db = null;

	if (args.length < 2) {
	    System.out.println("  Usage:   java javasearch database word word ...");
	    return;
	}

	String dbname = args[0];
	String words = args[1];
	for (int i=2; i<args.length; i++) {
	    words = words + " " + args[i];
	}
	//System.out.println("javasearch:  dbname "+dbname+
	//		   "  search string '"+words+"'.");

	//
	// Do a search!
	//

        try {
	    db = Database.OpenDatabase(dbname);
        } catch (IOException e) {
            System.out.println("ERROR: Failed to open " + e.getMessage());
            return;
        }
	Searcher searcher = new Searcher(db);
	DocList resultDL = searcher.doSearch(words);
	
	//
	// Display the results
	//

	System.out.println("\n*** javasearch:  Here's the results: ***\n");
	if (resultDL == null) {
	    System.out.println("  No documents matched this query!");
	    return;
	}

	// Ok!  We got some results!
	resultDL.dump();  	// Just dump out the result list

	// Now let the user look at some documents:
	while (true) {
	    System.out.print("\nEnter a Doc # to view, or 'q' to quit: ");
	    System.out.flush();
	    String s = new DataInputStream(System.in).readLine();
	    if (s.equals("Q") || s.equals("q")) {
		break;
	    }
	    int docNum = Integer.parseInt(s);
	    System.out.println("\nLooking up Doc # "+docNum+"!");
	    
	    Doc doc = resultDL.getDocAt(docNum);
	    
	    // The headline:
	    System.out.println(" - This doc's headline is: "+doc.headline);
	    
	    // The URL.  Only valid if our Database's docURLPrefix != null
	    if (db.docURLPrefix != null) {
		String fullUrl = db.docURLPrefix + doc.filename;
		System.out.println(" - The full URL for this document is: "+
				   fullUrl);
	    }
	    else {
		System.out.println(" - This database does not have URL information.");
	    }
	    
	    // The filename.
	    String fullPath = db.docPathPrefix + doc.filename;
	    System.out.println(" - The full filename for this document is: "+
			       fullPath);
	    
	    // Finally, print out the file!
	    System.out.println("Here's the file!\n-----");
	    try {
		FileInputStream in = new FileInputStream(fullPath);
		int c;
		while((c = in.read()) != -1) {
		    System.out.write(c);
		}
		in.close();
	    }
	    catch (IOException e) {
		System.out.println("[Guess this file doesn't exist...]");
	    }
	    System.out.println("-----");
	}
    }

}
