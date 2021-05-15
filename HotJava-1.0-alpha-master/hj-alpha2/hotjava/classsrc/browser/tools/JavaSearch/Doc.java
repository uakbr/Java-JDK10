/*
 * @(#)Doc.java	1.11 95/03/14 David A. Brown
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

/** One JavaSearch Document */
public class Doc {

    //
    // Persistent info about this Document (this is all
    //   saved in the docs file)
    //

    /** Document ID:  NOTE maximum of 65535 docs in one index!! */
    public char docID;

    /**
     *  "Filename" of this document.  This is NOT a full
     *  pathname -- it should be combined with either our Database's
     *  docPathPrefix or docURLPrefix, in order to get a
     *  fully-qualified filename or URL respectively.
     */
    public String filename;

    /**
     *  Document's "headline" or "title".  Should be human-readable
     *  and useful in the context of a list of search hits.
     */
    public String headline;

    // FUTURE stuff:
    //int startChar, endChar; // Character range of this doc in 'filename'
    //int numLines; // # lines in the doc
    //int length;	  // # bytes in the doc
    //String date;  // Human-readable date string, based on last-mod-date
    
    //
    // Other (transient) info about this Document
    //

    /** "Document type":  slightly change the behavior of
     *  getWord() for different input file types.
     */
    public static final int INVALID_TYPE = 0;
    public static final int TEXT_TYPE = 1;
    public static final int HTML_TYPE = 2;
    //public static final int NEWS_TYPE = 3;
    //
    public int type = INVALID_TYPE;


    /** Counter used to generate consecutive doc IDs.  Starts at 1
     *  since doc ID 0 is never used!  (it means the end of an
     *  index entry).
     *    Be sure to call resetDocIDCounter() if a single Java program
     *  is ever used to create more than one JavaSearch index.
     */
    private static int doc_ID_counter = 1;

    /** Maximum length (in chars) of a Doc headline */
    private static final int HEADLINE_MAX_LENGTH = 80;


    /** 
     *  Doc constructor to use when we already know all the Doc's info,
     *  eg. when we're reading it from the doc file.
     *  Used by the Doc.readFromStream() method. 
     */
    private Doc(char id, String fn, String hl) {
	docID = id;
	filename = fn;
	headline = hl;
    }

    /**
     * Generate a new Doc object based on a filename.
     * Doc type is derived from the filename.
     * Headline is computed based on the doc type, and may involve
     *   opening and reading the file.
     *
     * Automatically generates a new sequential docID.
     *
     * This Doc constructor is used when CREATING an index.
     */
    public Doc(String aFilename) {
	filename = aFilename;
	type = docTypeForFilename(filename);
	headline = generateHeadline(filename, type);

	// Generate a unique doc ID.
	docID = (char)(doc_ID_counter++);
	if (docID > 65535 ) {	// REMIND: Is there a more correct way
                   // to express the biggest number you can fit in a char?
	    throw new Exception("Doc ID overflow:  too many documents for one index!");
	}

    }

    /**
     * Create a new Doc object by looking it up, using the
     * specified Doc ID, in the specified .docs and .docindex files.
     *
     * The .docindex files gives us a pointer into the .docs
     * file, from where we read the actual document info.
     *
     * This Doc constructor is used when SEARCHING.
     */
    public Doc(char id, RandomAccessFile docsFile,
	       RandomAccessFile docindexFile) {
	
	// Read an docs file position from the docindex file.
	// docindex entries are ints, which are 4 bytes long.
	docindexFile.seek(4*(int)id);
	int docsPos = docindexFile.readInt();

	// Seek the docsfile to the right place, and read the
	//   Doc info.
	docsFile.seek(docsPos);
	readFromFile(docsFile);

	// Now sanity-check the Doc we read from docsFile:
	//   make sure its ID is what we started off looking for!

	if (id != docID) {
	    System.out.println("  Doc constructor:  tried for ID "+
			       id+", but readFromFile() found "+docID+"!!!");
	    throw new Exception("Doc ID mismatch in .docs/.docindex files!");
	}
    }

    /** Reset the counter used to generate consecutive doc IDs.
     *  This must be called if a single Java program is ever
     *  used to create more than one JavaSearch index.
     */
    public static void resetDocIDCounter() {
	doc_ID_counter = 1;
    }

    /** Generate a simple printed representation of this Doc */
    public String toString() {
        return "[DocID " + docID + "]\t" + filename + 
	    "\t'" + headline + "'";
    }
 
    /** Trim a prefix off this Doc's filename.
     *  Used by the indexer.  If our filename begins with
     *  "prefix", then we replace our filename with everything
     *  that *follows* prefix.
     */
    public void trimFilename(String prefix) {
	//System.out.println("\ntrimFilename:  prefix '"+prefix+
	//     "', filename '"+filename+"'.");
	if (prefix == null) return;
	if (filename.startsWith(prefix)) {
	    filename = filename.substring(prefix.length());
	}
	//System.out.println("  trimmed!  filename is now '"+filename+"'.");
    }
	    
    /** Write this Doc to the specified Output stream */
    void writeToStream(DataOutputStream out) {
	// Our doc ID:  one char
	// Note the ID is redundant when writing the docs file
	//    (since Docs are in sequential ID order!) but we still
	//    save it and do a sanity check when retrieving.
	out.writeChar(docID);

	// Filename and headline, terminated by '\n's:
	out.writeBytes(filename);
        out.writeByte('\n');
	out.writeBytes(headline);
        out.writeByte('\n');
    }

    /**
     *  Read a single Document entry from the specified stream.
     *  Returns a Doc object, or null if stream hits EOF.
     *  The stream must be pointing at the start of a
     *  valid Doc entry!!
     */
    static Doc readFromStream(DataInputStream in) {
	char id = in.readChar();
	System.out.println("  Doc.readFromStream:  got a char: "+id);
	// REMIND:  what does readChar() return if we're at EOF??
	//  Is it OK to just use the readLine() calls (below) to detect EOF?

	String filename = in.readLine();
	String headline = in.readLine();
	if (filename==null || headline==null) {
	    return null;
	}

	Doc doc = new Doc(id,filename,headline);
	return doc;
    }

    /**
     * Read a single Document entry from the specified file.  NOTE that
     * indexFile must be pointing at the start of a valid Doc entry!!
     *
     * This duplicates the functionality of
     * readFromStream(), but unfortunately
     * RandomAccessFiles are not InputStreams...
     */
    public void readFromFile(RandomAccessFile docsFile) {

	docID = docsFile.readChar();
	filename = docsFile.readLine();
	headline = docsFile.readLine();
	if (filename==null || headline==null) {
	    throw new Exception("Doc.readFromFile hit EOF: must have been a bad pointer into docsFile!");
	}
	//System.out.println("  Read Doc from docsFile: "+this);
    }


    /**
     *  Return a Document Type given the specified filename.
     *  Specifically, look for any special file extensions we recognize.
     */
    private static int docTypeForFilename(String filename) {
	// Currently the only special filename we recognize
	//   is "*.html":
	// REMIND: the 'endsWith(".html")' part might be Unix-specific!
	if (filename.endsWith(".html")) {
	    //System.out.println("Got an HTML file: "+filename);
	    return HTML_TYPE;
	}

	// REMIND:  Here's where we would detect a NEWS type file.
	//    But there's no way to do that from a filename, unless
	//    we assume a "numeric" filename (like '469') is a news
	//    article!
	// Do we have to also open the file here to figure out what
	//    it is?  Maybe the functionality of docTypeForFilename()
	//    should be merged with generateHeadline(), which already
	//    has to read the file anyway?

	else {
	    // default to TEXT...
	    return TEXT_TYPE;
	}
    }
    
    /**
     *  Compute a headline for this Doc, given the specified "filename" and
     *  Doc type.  This may involve opening and reading some of the file
     *  for certain doc types!
     */
    private static String generateHeadline(String filename, int docType) {

	String headline = null;

	// Handle all known types
	switch (docType) {

	case TEXT_TYPE:
	    // Headline is simply the filename
	    headline = filename;
	    break;

	case HTML_TYPE:
	    headline = getHTMLHeadline(filename);
	    break;

	//case NEWS_TYPE:
	//    headline = getNewsHeadline(filename);
	//    break;

	default:
	    throw new Exception("Doc.generateHeadline:  invalid docType ("+
				docType+")!");
	}

	// Some final processing:

	if ((headline == null) || (headline.length() == 0)) {
	    headline = "[No Headline]";
	}

	// REMIND:  We should strip out any newlines,
	//   and maybe any other nasty characters here

	// Enforce HEADLINE_MAX_LENGTH:
	if (headline.length() > HEADLINE_MAX_LENGTH) {
	    headline = headline.substring(0,HEADLINE_MAX_LENGTH);
	}

	//System.out.println("generateHeadline("+filename+"): returning '"+
	//		   headline+"'.");
	return headline;
    }

    /**
     * Compute the headline of an HTML file, by reading the file
     * and finding everything between the <title> and </title>.
     * Returns a String, or null if no HTML title was found.
     */
    private static String getHTMLHeadline(String filename) {
	String line,tmpline;
	String result= null;
	int pos;

	// Delimiters of an HTML title (lowercase versions)
	String titleStart = "<title>";
	String titleEnd = "</title>";

	//System.out.println("getHTMLHeadline: opening '" + filename + "'...");
	FileInputStream filein = new FileInputStream(filename);
	DataInputStream in = new DataInputStream(filein);

	while (true) {
	    line = in.readLine();
	    if (line == null) {
		filein.close();
		return null;
	    }
	    tmpline = IndexingInputStream.downcase(line);
	    if ((pos = tmpline.indexOf(titleStart)) != -1) {
		// Got the title!  Save the rest of this line
		result = line.substring(pos+titleStart.length());
		break;
	    }
	}
		
	// If we already have titleEnd, we're done.
	tmpline = IndexingInputStream.downcase(result);
	if ((pos = tmpline.indexOf(titleEnd)) != -1) {
	    filein.close();
	    return result.substring(0,pos);
	}

	// Ok, keep reading more lines, looking for titleEnd
	while ((line = in.readLine()) != null) {
            tmpline = IndexingInputStream.downcase(line);
	    if ((pos = tmpline.indexOf(titleEnd)) != -1) {
		// This line contained titleEnd.

		// Add a space to result unless we're at
		//   the very beginning or end of the title
		if ((result.length() != 0) && (pos != 0)) {
		    result += " ";
		}

		// And append anything *before* titleEnd
		result += line.substring(0,pos);
		break;
	    }
	    else {
		// Add a space to result unless we're at
                //   the very beginning of the title,
		//   or if this line is empty
                if ((result.length() != 0) && (line.length() != 0)) {
                    result += " ";
                }
		
		// Append this whole line to result
		result += line;
	    }
	}

	filein.close();
	return result;
    }


//    /**
//     * Compute the headline of a News article, by reading the file
//     * and looking for the From: and Subject: lines.
//     * Returns a String, or null if no title could be constructed.
//     */
//    private static String getNewsHeadline(String filename) {
//	String line,tmpline;
//	String result = null;;
//	int pos;
//
//	// Header fields we care about
//	String fromHeader = "from:";
//	String subjectHeader = "subject:";
//
//	//System.out.println("getNewsHeadline: opening '" + filename + "'...");
//	FileInputStream filein = new FileInputStream(filename);
//	DataInputStream in = new DataInputStream(filein);
//
//	// Read through the lines of the file
//	while ((line = in.readLine()) != null) {
//
//	    // Search for fromHeader or subjectHeader here
//
//	}
//
//	// Construct a nice-looking headline based on the
//	//   From and Subject fields.  Maybe something like
//	//   "Subject of this Article [fromaddress@machine.sun.com]"
//
//	filein.close();
//	return result;
//    }


}
