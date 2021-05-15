/*
 * @(#)indexdump.java	1.8 95/03/14 David A. Brown
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

/** indexdump:  utility to view an index file */
class indexdump {

    public static void main(String args[]) {
	if (args.length != 1) {
	    System.out.println("  Usage:  java indexdump dbname");
	    return;
	}
	String filename = args[0] + ".index";
	String qfilename = args[0] + ".qindex";

	System.out.println("Opening index file '" + filename + "'...");
        FileInputStream filein = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(filein);

	System.out.println("Opening qindex file '" + qfilename + "'...");
        FileInputStream qfilein = new FileInputStream(qfilename);
        DataInputStream qin = new DataInputStream(qfilein);

	// Read the header
	String header = in.readLine();
	if (!header.equals(Index.indexFileHeader)) {
	    System.out.println("File "+
			       filename+" is not an JavaSearch Index file!");
	    return;
	}

	// Read all the index entries...
	Word w;
	int entrycount = 0;
	while ((w = Word.readFromStream(in)) != null) {
	    // Read the entry from qindex that *should* agree with this w
	    int pos = qin.readInt();
	    System.out.println("  "+pos+":\t"+w);
	    entrycount++;
	}
	System.out.println("Closing index file.  Total index entries: " +
			   entrycount + ".");
	filein.close();
    }
}
