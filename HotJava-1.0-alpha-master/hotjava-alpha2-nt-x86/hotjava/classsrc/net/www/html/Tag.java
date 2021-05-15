/*
 * @(#)Tag.java	1.23 95/01/31 Jonathan Payne
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

package net.www.html;

import java.util.Hashtable;

public class Tag {
    static Hashtable	tagHashTable = new Hashtable(10);

    public String   name;	/** Name of this tag. */
    public boolean  hasEndTag;	/** Should we expect an end tag? */
    public boolean  breaks;	/** Does this cause a break when formatted? */
    public short    id;		/** id number (below) for this tag */

    public static Tag lookup(String name) {
	Tag tag = (Tag) tagHashTable.get(name);

	if (tag == null) {
	    tag = new Tag(name, false, false, UNKNOWN);
	}
	return tag;
    }

    static public final int UNKNOWN = 0;
    static public final int A = 1;
    static public final int ADDRESS = 2;
    static public final int B = 3;
    static public final int BASE = 4;
    static public final int BODY = 5;
    static public final int BR = 6;
    static public final int CITE = 7;
    static public final int CODE = 8;
    static public final int DL = 9;
    static public final int DD = 10;
    static public final int DFN = 11;
    static public final int DIR = 12;
    static public final int DT = 13;
    static public final int EM = 14;
    static public final int H1 = 15;
    static public final int H2 = 16;
    static public final int H3 = 17;
    static public final int H4 = 18;
    static public final int H5 = 19;
    static public final int H6 = 20;
    static public final int HEAD = 21;
    static public final int HR = 22;
    static public final int HTML = 23;
    static public final int I = 24;
    static public final int IMG = 25;
    static public final int ISINDEX = 26;
    static public final int KBD = 27;
    static public final int LI = 28;
    static public final int MENU = 29;
    static public final int NEXTID = 30;
    static public final int OL = 31;
    static public final int P = 32;
    static public final int PRE = 33;
    static public final int SAMP = 34;
    static public final int STRONG = 35;
    static public final int TITLE = 36;
    static public final int TT = 37;
    static public final int U = 38;
    static public final int UL = 39;
    static public final int BLOCKQUOTE = 40;
    static public final int TEXTAREA = 41;
    static public final int APP = 42;
    static public final int PLAINTEXT = 43;
    static public final int COMMENT = 44;
    static public final int FORM = 45;
    static public final int INPUT = 46;
    static public final int SELECT = 47;
    static public final int VAR = 48;
    static public final int OPTION = 49;
    static public final int CENTER = 50;
    static public final int FONT = 51;
    static public final int BASEFONT = 52;
    static public final int NTAGS = 53;

    static {
	new Tag("a", true, A);
	new Tag("address", true, true, ADDRESS);
	new Tag("b", true, B);
	new Tag("base", false,  BASE);
	new Tag("body", true, true, BODY);
	new Tag("br", false, true, BR);
	new Tag("center", true, false, CENTER);
	new Tag("cite", true, CITE);
	new Tag("code", true, CODE);
	new Tag("dl", true, true, DL);
	new Tag("dd", false, true, DD);
	new Tag("dfn", true, DFN);
	new Tag("dir", true, true, DIR);
	new Tag("dt", false, true, DT);
	new Tag("em", true, EM);
	new Tag("h1", true, true, H1);
	new Tag("h2", true, true, H2);
	new Tag("h3", true, true, H3);
	new Tag("h4", true, true, H4);
	new Tag("h5", true, true, H5);
	new Tag("h6", true, true, H6);
	new Tag("head", true, true, HEAD);
	new Tag("hr", false, true, HR);
	new Tag("html", true, true, HTML);
	new Tag("i", true, I);
	new Tag("isindex", false, ISINDEX);
	new Tag("kbd", true, KBD);
	new Tag("li", false, true, LI);
	new Tag("menu", true, true, MENU);
	new Tag("nextid", false, NEXTID);
	new Tag("ol", true, true, OL);
	new Tag("p", false, true, P);
	new Tag("pre", true, true, PRE);
	new Tag("samp", true, SAMP);
	new Tag("strong", true, STRONG);
	new Tag("title", true, true, TITLE);
	new Tag("tt", true, TT);
	new Tag("u", true, U);
	new Tag("ul", true, true, UL);
	new Tag("blockquote", true, true, BLOCKQUOTE);
	new Tag("plaintext", false, PLAINTEXT);
	new Tag("<comment>", false, true, COMMENT);
	new Tag("form", true, false, FORM);
	new Tag("textarea", true, false, TEXTAREA);
	new Tag("input", false, false, INPUT);
	new Tag("select", true, false, SELECT);
	new Tag("var", true, VAR);
	new Tag("option", false, false, OPTION);

	new Tag("img", false, IMG);
	new Tag("app", false, APP);
    }

    public Tag(String name, boolean hasEndTag, boolean breaks, int id) {
	tagHashTable.put(name, this);
	this.name = name;
	this.hasEndTag = hasEndTag;
	this.breaks = breaks;
	this.id = (short)id;
    }

    public Tag(String name, boolean hasEnd, int id) {
	this(name, hasEnd, false, id);
    }

    public String toString() {
	return getClass().getName() + "[" + name + "]";
    }
}
