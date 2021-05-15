/*
 * @(#)Document.java	1.50 95/05/10 Jonathan Payne
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

package browser;

import java.util.*;
import java.io.*;
import awt.*;
import net.*;
import net.www.http.*;
import net.www.html.*;

/**
 * Class Document extends the generic html.Document class to use
 * TagRef subclasses that contain information about formatting the
 * document.  This is the class which associates particular style
 * changes with particular html tags.  Many of the html tags are
 * associated with a single TagRef subclass called StyleTagRef,
 * which knows how to apply a style to the formatter when the
 * formatter encounters that tag.  But many other tags have their
 * own special subclass of TagRef, and it's this class which
 * associates those TagRef subclasses with the html tag.<p> This
 * also handles creation, starting, stopping and destruction of
 * hotjava applets.
 * @see Applet
 * @see Style
 * @see net.www.html.Document
 * @version 1.50, 10 May 1995
 * @author Jonathan Payne
 */

public class Document extends net.www.html.Document {
    URL	    thisURL;			/* url for this document */

    /** If an html document is transformed into a DisplayItem
	(rather than being a normal text document) then this points
	to that DisplayItem and the other parts of the document are
	ignored */
    public DisplayItem displayItem;

    /** This is the actual object we got back if it wasn't an
	html document.  Obviously, this shouldn't be an instance
	variable of document ... */
    Object	    content;   /* set if this document is a general Object */

    public String toString() {
	return "Document[url=" + thisURL + ", title = " + getTitle() + "]";
    }

    Vector applets = new Vector();

    static Style	    styleIndex[] = new Style[Tag.NTAGS];
    static void defineTag(String name, Style s) {
	Tag t = Tag.lookup(name);

	styleIndex[t.id] = s;
    }

    static {
	defineTag("a", new AnchorStyle("style=u"));
	defineTag("br", new BreakingStyle("break=0"));
	defineTag("p", new BreakingStyle(null));
	defineTag("center", new BreakingStyle("align=c, break=0"));
	defineTag("h1", new BreakingStyle("size=24,style=b"));
	defineTag("h2", new BreakingStyle("size=18,style=b"));
	defineTag("h3", new BreakingStyle("size=17,style=b"));
	defineTag("h4", new BreakingStyle("size=14,style=b"));
	defineTag("h5", new BreakingStyle("size=12,style=b"));
	defineTag("h6", new BreakingStyle("size=10,style=b"));

	defineTag("title", new Style("renders=false"));
	defineTag("head", new Style(null));

	defineTag("address", new BreakingStyle("break=0,size=14,style=i"));
	defineTag("b", new BasicStyle("style=b"));
	defineTag("body", new BasicStyle("size=14, style=p"));
	defineTag("cite", new BasicStyle("style=i"));
	defineTag("var", new BasicStyle("style=i"));
	defineTag("code", new BasicStyle("style=pf, size=14"));
	defineTag("em", new BasicStyle("style=i"));
	defineTag("i", new BasicStyle("style=i"));
	defineTag("pre", new BreakingStyle("style=pf, size=14, align=l, wrap=not"));
	defineTag("samp", new BasicStyle("style=pf, size=14"));
	defineTag("strong", new BasicStyle("style=b"));
	defineTag("tt", new BasicStyle("style=pf, size=14"));
	defineTag("u", new BasicStyle("style=u"));
	defineTag("blockquote", new BreakingStyle("leftMargin=+35, rightMargin=+35"));
	defineTag("plaintext", new BasicStyle("style=pf, size=14, wrap=not"));
    }

    /** Creates and returns a new TagRef for the specified html start tag. */
    public TagRef startTag(Tag t, int offset) {
	return addTagRef(newTagAt(t, offset, false), offset);
    }

    /** Creates and returns a new TagRef for the specified html end tag. */
    public TagRef endTag(Tag t, int offset) {
	return addTagRef(newTagAt(t, offset, true), offset);
    }

    private TagRef newTagAt(Tag t, int pos, boolean isEnd) {
	TagRef	ref = null;

	switch (t.id) {
	case Tag.ADDRESS:
	case Tag.B:
	case Tag.BODY:
	case Tag.CITE:
	case Tag.CODE:
	case Tag.EM:
	case Tag.I:
	case Tag.KBD:
	case Tag.SAMP:
	case Tag.STRONG:
	case Tag.TT:
	case Tag.U:
	case Tag.BLOCKQUOTE:
	case Tag.H1:
	case Tag.H2:
	case Tag.H3:
	case Tag.H4:
	case Tag.H5:
	case Tag.H6:
	case Tag.PRE:
	case Tag.HEAD:
	case Tag.TITLE:
	case Tag.P:
	case Tag.BR:
	case Tag.CENTER:
	default:
	    ref = new StyleTagRef(t, pos, isEnd, styleIndex[t.id]);
	    break;

	case Tag.A:
	    ref = new AnchorTagRef(t, pos, isEnd, styleIndex[t.id]);
	    break;

	case Tag.UNKNOWN:
	    break;

	case Tag.DL:
	    ref = new DLTagRef(t, pos, isEnd);
	    break;

	    /* REMIND: these two can be styles ... */
	case Tag.DD:
	    ref = new DDTagRef(t, pos, isEnd);
	    break;

	case Tag.DT:
	    ref = new DTTagRef(t, pos, isEnd);
	    break;

	    /* These are not styles because they potentially create
	       display items, which should not be created repeatedly
	       each time the document is layed out. */
	case Tag.LI:
	    ref = new LITagRef(t, pos, isEnd);
	    break;

	case Tag.OL:
	    ref = new OLTagRef(t, pos, isEnd);
	    break;

	case Tag.HR:
	    ref = new HRTagRef(t, pos, isEnd);
	    break;

	case Tag.IMG:
	    ref = new ImgTagRef(t, pos, isEnd);
	    break;

	case Tag.MENU:
	case Tag.DIR:
	    /* REMIND: DIR and MENU should be its own kind of tag
	       list, but for now is a UnorderedList. */

	case Tag.UL:
	    ref = new ULTagRef(t, pos, isEnd);
	    break;

	case Tag.APP:
	    ref = new AppTagRef(t, pos, isEnd);
	    break;

	case Tag.FORM:
	    ref = new FormTagRef(t, pos, isEnd);
	    break;

	case Tag.INPUT:
	    ref = new InputTagRef(t, pos, isEnd);
	    break;

	case Tag.SELECT:
	    ref = new SelectTagRef(t, pos, isEnd);
	    break;

	case Tag.OPTION:
	    ref = new OptionTagRef(t, pos, isEnd);
	    break;

	case Tag.TEXTAREA:
	    ref = new TextAreaTagRef(t, pos, isEnd);
	    break;
	}
	return ref;
    }

    /** Returns the URL that is associated with this document. */
    public URL url() {
	return thisURL;
    }

    /** Sets the URL that is associated with this document. */
    public void setURL(URL u) {
	thisURL = u;
    }

    /**
     * Creates a new Document with the specified URL and InputStream.
     */
    public browser.Document(URL url, InputStream is) {
	thisURL = url;

	try {

	    if (url.content_type != URL.content_html) {
		setText(url.content_type + ": expected html");
		return;
	    }
	    try {
		Parser parser = new Parser(is, this);
	    } catch (Exception e) {
		setText("I can't read your data\n" + e);
	    }

	} finally {
	    is.close();
	}
    }

    /**
     * Creates a new Document with the specified URL with its
     * contents initialized by the specified String.  Unless the
     * string starts with "<html>", the document
     * is a plain text document, which means it contains no html
     * tags in it, exception for the &lt;plaintext&gt; tag at the very
     * beginning.
     */
    public browser.Document(URL u, String s) {
	thisURL = u;
	if (s.startsWith("<html>")){
	    try {
		new Parser(new StringInputStream(s), this);
	    } catch(Exception e) {
		setText("Error in error message:\n"+s);
		startTag(Tag.lookup("plaintext"), 0);
	    }
	} else {
	    setText(s);
	    startTag(Tag.lookup("plaintext"), 0);
	}
    }

    /** Add an applet to this document */
    public synchronized void addApplet(AppletDisplayItem item) {
	if (!applets.contains(item)) {
	    applets.addElement(item);
	}
    }

    /** Send start()s to all the Applets in our hashtable. */
    public synchronized void startApplets() {
	for (Enumeration e = applets.elements() ; e.hasMoreElements() ;) {
	    ((AppletDisplayItem)e.nextElement()).start();
	}
    }

    /** Send stop()s to all the Applets in our hashtable. */
    public synchronized void stopApplets() {
	for (Enumeration e = applets.elements() ; e.hasMoreElements() ;) {
	    ((AppletDisplayItem)e.nextElement()).stop();
	}
    }

    /** Send destroy()s to all the Applets in our hashtable. */
    public synchronized void destroyApplets() {
	for (Enumeration e = applets.elements() ; e.hasMoreElements() ;) {
	    ((AppletDisplayItem)e.nextElement()).destroy();
	}
    }

    /** Nuke all the images in a document */
    public synchronized void flushImages() {
	Vector tagList = getTags();

	/* now, walk through all the tag refs, and start fetching images */
	int i = 0;
	int cnt = tagList.size();
	while (--cnt >= 0) {
	    TagRef  ref = (TagRef)tagList.elementAt(i++);

	    if (ref instanceof ImgTagRef) {
		((ImgTagRef)ref).nuke();
	    }
	}
    }
}


