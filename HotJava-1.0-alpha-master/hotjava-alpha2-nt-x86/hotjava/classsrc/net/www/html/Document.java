/*
 * @(#)Document.java	1.14 95/03/18 Jonathan Payne
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

import java.util.*;
import java.io.*;
import awt.DisplayItem;

package net.www.html;

/** net.ww.html.Document is a class which holds an html document.
    An html document is parsed text, a vector of tags, and
    optional html source text.  Document is used by html.Parser to
    store parsed documents, but it is also used to help
    programatically generate html documents.  E.g., the ftp
    protocol handler might generate an html document out of a
    directory listing. */

public class Document {
    /** The head tag, so we can tell when we've finished parsing
	the head of a document.  The reason we care is that all the
	elements inside a head should really not be rendered.  This
	is where we'll stick the &lt;title> and perhaps other
	extensions such as a digital signature.  The problem is,
	mosaic and other viewers out there don't mind if there is
	a &lt;head> without the corresponding &lt;/head>.  So millions
	of people all over the world write completely incorrect
	html documents AND NEVER EVEN KNOW IT! */
    static Tag	HEADtag = Tag.lookup("head");

    /** If this is non-null, it contains the htmlSource that this
	document was built from. */
    protected String	htmlSource;

    /** This is the text of the html document, with the tags and
	extra white space removed.  */
    protected byte	text[];

    /** This is an array of tag references into the text string. */
    protected Vector	tags;

    /** Title of this html document, or null if not known yet. */
    protected String	title;

    /** This is whether or not we're in a &lt;pre> section.  &lt;pre>
	sections cannot be nested, and we enforce that here, so we
	don't need to keep a count. */
    protected boolean	inPREelement = false;

    public Document() {
	reset();
    }

    public final void setSource(String source) {
	htmlSource = source;
    }

    public void reset() {
	text = null;
	title = null;
	if (tags == null) {
	    tags = new Vector();
	} else {
	    tags.setSize(0);
	}
    }

    /** This adds a pre-made TagRef to the document, and sets
	its position to the current position. */
    protected final TagRef addTagRef(TagRef ref, int offset) {
	if (ref != null) {
	    Tag	theTag = ref.tag;
	    int	tagID = theTag.id;

	    if (inPREelement) {
		if (tagID == Tag.PRE && ref.isEnd) {
		    inPREelement = false;
		} else {
		    if (theTag.breaks && tagID != Tag.BR && tagID != Tag.HR) {
			System.out.println("Warning: Ignoring: " + theTag + " inside <pre> section");
			return null;
		    }
		}
	    } else if (tagID == Tag.PRE) {
		inPREelement = true;
	    }

	    ref.pos = offset;
	    tags.addElement(ref);
	}

	return ref;
    }

    /** Create a new start tag ref at the current position.  This
	creates a generic TagRef object, but can be subclassed to
	create specific TagRef subclasses. */
    public TagRef startTag(Tag t, int offset) {
	//System.out.println("Add <" + t.name + "> at " + offset);
	return addTagRef(new TagRef(t, offset, false), offset);
    }

    /** Create a new end tag ref at the current position.  This
	creates a generic TagRef object, but can be subclassed to
	create specific TagRef subclasses. */
    public TagRef endTag(Tag t, int offset) {
	//System.out.println("Add </" + t.name + "> at " + offset);
	return addTagRef(new TagRef(t, offset, true), offset);
    }

    /** Return the vector of tag refs. */
    public Vector getTags() {
	return tags;
    }

    /** Return the text array. */
    public byte getText()[] {
	return text;
    }

    /** Set the text array. It is not copied. */
    public void setText(byte text[]) {
	this.text = text;
    }

    /** Set the text. */
    public void setText(String str) {
	text = new byte[str.length()];
	for (int i = 0 ; i < text.length ; i++) {
	    text[i] = (byte)str.charAt(i);
	}
    }

    /** This returns the title for this document, as defined by
	the &lt;title> &lt;/title> tag pairs.  If no title is found,
	"" is returned. */
    public String getTitle() {
	if (title != null) {
	    return title;
	}
	if (tags == null) {
	    return "";
	}
	Tag titleTag = Tag.lookup("title");
	int cnt = tags.size();
	int i = 0;
	int pos0 = -1;

	title = "";
	while (--cnt >= 0) {
	    TagRef  ref = (TagRef) tags.elementAt(i++);

	    if (ref.tag == titleTag) {
		if (ref.isEnd) {
		    /* check to make sure we found the <title> begin
		       tag!  If not, we return no title. */
		    if (pos0 == -1) {
			break;
		    }
		    title = new String(text, 0, pos0, ref.pos - pos0);
		    break;
		}
		pos0 = ref.pos;
	    }
	}
	return title;
    }
}
