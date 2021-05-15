/*
 * @(#)Database.java	1.10 95/03/14 David A. Brown
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
 * Database:  Class describing an JavaSearch database.
 *
 * See the detailed comment below for info on the filenames
 * making up a database, and file formats.
 *
 */
public class Database {

    /** Name of this database */
    public String dbName;

    /** Pathname prefix for DOCUMENT files in this database.
     *  An absolute document pathname is constructed by concatenating
     *  docPathPrefix with the "filename" from a Doc object.
     *
     *  docPathPrefix may be an empty string but is never null.
     */
    public String docPathPrefix;

    /** URL prefix for documents in this database.
     *  An absolute document URL is constructed by concatenating
     *  docURLPrefix with the "url" from a Doc object.
     *
     *  If docURLPrefix is null, that means that Documents in
     *  this database do not have meaningful URLs.
     */
    public String docURLPrefix;

    /**
     * Human-readable string describing this database
     */
    public String description;
    
    // FUTURE:
    //  - JavaSearch version info
    //  - "Maintainer" of this DB
    //  - Cost parameters
    //  - List of most frequent words in the index
    //  - Most common file type(s) in this index
    //  - Date this db was (1) created, (2) last re-indexed


    /** 
     *  Filenames of the separate files making up this
     *    JavaSearch database.  These files are the DB info file,
     *    the index and docs files, and *their* indexes.
     *    See the big comment at the end of this file.
     *  These filenames are derived from dbName, and are filled
     *  in by our constructor. */
    public String dbinfoFilename;
    public String indexFilename, qindexFilename;
    public String docsFilename, docindexFilename;

    /**
     *  Database constructor.  Specify the database name,
     *  and we derive the index/docs filenames.
     *
     *  This constructor is private.  Use either
     *  CreateNewDatabase() if you're creating an index,
     *  or OpenDatabase() if you're about to search an already-existing
     *  database.
     */
    private Database(String name) {
        dbName = name;
        dbinfoFilename = dbName + ".dbinfo";
        indexFilename = dbName + ".index";
        qindexFilename = dbName + ".qindex";
        docsFilename = dbName + ".docs";
        docindexFilename = dbName + ".docindex";
    }

    //
    // Static methods returning a new Database object.
    // CreateNewDatabase() is used when indexing (to creating a new DB).
    // OpenDatabase() if used to search an already-existing DB.
    //

    /*
     * Create a brand-new Database object
     */
    public static Database CreateNewDatabase(String name) {
	System.out.println("Creating a new Database ["+name+"]...");
	Database db = new Database(name);
	System.out.println("  "+db);
	return db;
    }

    /*
     * Construct an JavaSearch Database object by reading
     * an already-existing Database from disk.
     *
     * Returns a Database object, with all the info from the
     * ".dbinfo" file pre-loaded.
     *
     * Throws IOException if the database can't be opened.
     */
    public static Database OpenDatabase(String name) {
	//System.out.println("Opening a Database from disk ["+name+"]...");
	Database db = new Database(name);
	db.readInfoFile();
	//System.out.println("  "+db);
	return db;
    }

    // Couple of utility functions to get file sizes

    /** Return the size (in bytes) of this db's on-disk index. */
    public int indexSize() {
	int size = 0;

	RandomAccessFile f = new RandomAccessFile(indexFilename,"r");
	size += f.length();
	f.close();

	f = new RandomAccessFile(qindexFilename,"r");
	size += f.length();
	f.close();

	return size;
    }

    /** Return the total size (in bytes) of this Database.
     *  This includes the index/qindex files, *plus* the dbinfo
     *  and docs files. */
    public int totalDBSize() {

	int size = indexSize();

	RandomAccessFile f = new RandomAccessFile(dbinfoFilename,"r");
        size += f.length();
        f.close();

	f = new RandomAccessFile(docsFilename,"r");
        size += f.length();
        f.close();

        f = new RandomAccessFile(docindexFilename,"r");
        size += f.length();
        f.close();

        return size;
    }

    /**
     * Write this Database's .dbinfo file to disk.
     */
    public void saveInfoFile() {
        System.out.println("Opening DB info file '" + dbinfoFilename + "'...");
        FileOutputStream fileout = new FileOutputStream(dbinfoFilename);
        DataOutputStream out = new DataOutputStream(fileout);

	out.writeBytes("JavaSearch Database info file\n");
        out.writeBytes("dbName:" + dbName + "\n");
        out.writeBytes("docPathPrefix:" + docPathPrefix + "\n");
	if (docURLPrefix != null) {
	    out.writeBytes("docURLPrefix:" + docURLPrefix + "\n");
	}
	else {
	    out.writeBytes("docURLPrefix:\n");
	}
        out.writeBytes("description:" + description + "\n");
 
        fileout.close();
    }

    /**
     * Read this Database's .dbinfo file,
     * and set docPathPrefix / docURLPrefix / description.
     */
    private void readInfoFile() {
	String line;

        //System.out.println("Opening DB info file '" + dbinfoFilename + "'...");
        FileInputStream filein = new FileInputStream(dbinfoFilename);
        DataInputStream in = new DataInputStream(filein);

	// REMIND:  This is real quick & dirty.  Really
	//    should be more flexible...

	line = in.readLine();		// Skip over header line
	line = in.readLine();		// dbName
	String tmpName = line.substring(line.indexOf(':')+1);
/*
	if (!tmpName.equals(dbName)) {
	    throw new Exception("Database.readInfoFile(): dbName from file ("+
				tmpName+") doesn't agree with dbname ("+
				dbName+")!");
	}
*/

	line = in.readLine();
	docPathPrefix = line.substring(line.indexOf(':')+1);

	line = in.readLine();
	docURLPrefix = line.substring(line.indexOf(':')+1);
	if (docURLPrefix.length() == 0) {
	    docURLPrefix = null;
	}

	line = in.readLine();
	description = line.substring(line.indexOf(':')+1);

        filein.close();
    }

    /** Return a simple printed representation of this Database */
    public String toString() {
	return "Database '"+dbName+
	    "': docPathPrefix '"+docPathPrefix+
	    "'; docURLPrefix '"+docURLPrefix+
	    "'; description '"+description+"'.";
    }

}




/*
 *      Files used by the JavaSearch toolkit
 *
 An JavaSearch DATABASE consists consists of 5 files on disk:

   (0) dbName.dbinfo    Human-readable discription of this database

   (1) dbName.index     Inverted word index
   (2) dbName.qindex    List of 4-byte pointers into the .index file

   (3) dbName.docs      The source documents making up this database
   (4) dbName.docindex  List of 4-byte pointers into the .docs file


   The .dbinfo file is intended to be human-readable, and
   contains info describing this database.  It's basically
   the persistent form of a Database object.



   The .qindex and .docindex files are simply a list of integers,
   which can be used as pointers into the .index and
   .docs files respectively (to quickly find the Nth
   word in the index, or the Nth document in the database).
   These files must be used to do a quick lookup in the
   index of docs file (since .index and .docs records are
   variable-length!)
     See Searcher.java for the binary search algorithm
   which uses the .qindex and .index files.

   The .index file contains the following information:
     - Header (see Index.indexFileHeader) followed by newline
     - List of Word entries.

   Each Word entry corresponds to a Word object, and contains
   the following:
     - The word itself, as a string (of bytes) followed by newline
     - A list of Doc IDs, written as chars, terminated by zero.

   The .docs file contains the following information:
     - Header (see DocList.docsFileHeader) followed by newline
     - List of Doc entries.
 
   Each Doc entry corresponds to a Doc object, and contains 
   the following:
     - The ID of this Doc, written as a single char.
       (Note this is redundant, but here for sanity checking). 
     - This Doc's filename, as a string (of bytes) followed by newline
     - This Doc's headline, as a string (of bytes) followed by newline
   See Doc.java for some FUTURE stuff which might eventually
   be added to Doc entries.

   Random NOTES and future feature info:

   - There are several alternate ways to do a fast search
     through the inverted index.  The current .index/.qindex
     file arrangement is straightforward, and is as fast
     as a binary search (with two seeks and reads at each
     step of the search).   A couple of other possible methods
     might involve
       - Having an "index to the index" with pointers
	 at every Nth entry (with n=1000, maybe) in the index file.
	 Then, you do a 2-level binary search (which should perform
	 better since your reads are clustered better than the
	 simple binary search, which reads all over the file...)
       - Having an "index to the index" with a bunch of
         FIXED-LENGTH entries, like maybe the first 4 characters
	 of every word in the index (with duplicates removed).
	 So you binary-search this (relatively small) file
	 to get a pointer into the real file, and then you
	 search the real file *sequentially* till you find your word
	 or determine it's not in the index.
     Also, see the file "DESIGN" in the WAIS docs for a (terse)
     description of WAIS's indexing system.

*/

