/*
 * @(#)javaindex.java	1.13 95/03/14 David A. Brown
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

/** javaindex:  the JavaSearch indexer main program */
class javaindex {

    // Bunch of static vars we use to keep track of performance stats
    static int numSorts = 0;    // # times we sorted the index
    static int sortTime = 0;    // how much time spent sorting
    static int parseTime = 0;	// how much time spent parsing doc files
    //
    static int documentsSize = 0; // Total size of Documents in the index

    /** Show a usage message.
     *  But see the file README in this directory for more info!
     */
    static void showUsage() {
	System.out.println("  Usage:  java javaindex -db database_name [-trimprefix path_to_trim]");
	System.out.println("\t [-stoplist stopfile] [-fileprefix path_prefix] [-urlprefix url_prefix]");
	System.out.println("\t [-description \"Description of this database\"]");
	System.out.println("\t filename filename ...");	
    }

    public static void main(String args[]) {
	int startTime = System.nowMillis();

	//
	// Process args
	//

	String arg;
	int argc = 0;

	String dbname = null;
	String trimprefix = null;
	String fileprefix = "";
	String urlprefix = null;
	String stopfile = null;
	String description = "JavaSearch database created with 'javaindex'";

	try {
	    while ((argc < args.length) && args[argc].startsWith("-")) {
		if (args[argc].equals("-db")) {
		    dbname = args[++argc];
		}
		else if (args[argc].equals("-trimprefix")) {
		    trimprefix = args[++argc];
		}
		else if (args[argc].equals("-fileprefix")) {
		    fileprefix = args[++argc];
		}
		else if (args[argc].equals("-urlprefix")) {
		    urlprefix = args[++argc];
		}
		else if (args[argc].equals("-description")) {
		    description = args[++argc];
		}
		else if (args[argc].equals("-stoplist")) {
		    stopfile = args[++argc];
		}
		else {
		    System.out.println("  Unknown argument '"+args[argc]+"'!");
		    showUsage();
		    return;
		}
		argc++;
	    }
	}
	catch (ArrayIndexOutOfBoundsException e) {
	    showUsage();
            return;
	}

	// Make sure we got any required parameters, and that
	//   we have at least one filename to index!
	if (dbname == null) {
	    System.out.println("  No database_name specified!");
	    showUsage();
	    return;
	}
	if (argc >= args.length) {
            System.out.println("  No filenames to index specified!");
	    showUsage();
	    return;
	}

	Database db = Database.CreateNewDatabase(dbname);
	db.docPathPrefix = fileprefix;
	db.docURLPrefix = urlprefix;
	db.description = description;
	System.out.println(db);

	Index index = new Index(stopfile);
	DocList doclist = new DocList();

	// Process each file
	// Future:  feature to recursively index a
	//    whole dir tree would be nice
        do {
	    String filename = args[argc];
	    System.out.print("--> indexing file "+filename);

	    // REMIND:  We need to handle IO Exceptions better
	    //   if they occur here, especially stuff we ought to
	    //   handle cleanly (like "File not found")
	    //   versus bad stuff like "I/O error".
	    // But to do this we need a better IOException model;
	    //   see Article 898 in fp.livejava (from csw, 18 Oct 1994)

	    // Make a Doc out of the file
	    Doc doc = new Doc(filename);
	    doc.trimFilename(trimprefix);
	    doclist.addDoc(doc);
	    System.out.print("  [headline '"+doc.headline+"']");

	    // Make an IndexingInputStream for the file
            long start = System.nowMillis();
	    IndexingInputStream input = new IndexingInputStream(filename);
	    input.setDocType(doc.type);

	    documentsSize += input.available();

	    // Tell the index to associate this Doc with
	    //   all the words in this stream:
	    index.addDocToIndex(doc,input);
            System.out.println(" ("+(System.nowMillis()-start)+"ms)");

	    // Close the file
	    input.close();
        } while (++argc < args.length);

	// Debugging:
	System.out.println("-----");
	doclist.dump();
	index.sort();
	//index.dump();

	index.saveAs(db);
	doclist.saveAs(db);
	db.saveInfoFile();

	// Show some final statistics:
	int totalTime = System.nowMillis() - startTime;
	int indexSize = db.indexSize();
	int totalDBSize = db.totalDBSize();

	System.out.println("\njavaindex finished.  "+
			   doclist.size() + " document" +
			   ((doclist.size()==1?"":"s")) +
			   ", "+index.entries.size()+" words in index.");
	System.out.println("   total time "+totalTime/1000+" sec.");
	System.out.println("   sorted the index " +
			   numSorts + " time"+(numSorts==1?"":"s") +
			   ";  total time " + sortTime + " ms.");
	System.out.println("   time processing documents:  " +
			   parseTime + " ms.");
	System.out.println("   total size of documents: " +
			   documentsSize + " bytes, size of index: " +
			   indexSize + " bytes (" +
			   (indexSize*100/documentsSize) + "% of docs)");
	System.out.println("                total size of this JavaSearch database: " +
                           totalDBSize + " bytes (" +
                           (totalDBSize*100/documentsSize) + "% of docs)");
    }

}
