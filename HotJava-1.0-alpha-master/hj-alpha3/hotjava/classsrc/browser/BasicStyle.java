/*
 * @(#)BasicStyle.java	1.12 95/04/10 Jonathan Payne
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
import net.www.html.*;
import awt.*;

/**
 * The BasicStyle class is used to specify most of the style changes
 * that occur as a result of html tag refs.  Zero or more of the
 * parameters may be specified for change.  BasicStyle operates on
 * instances of FormattingParameters, generating new instances of
 * FormattingParameters by modifying old ones.
 * @see awt.FormattingParameters
 * @version 1.12, 10 Apr 1995
 * @author Jonathan Payne
 */

public class BasicStyle extends Style {
    /** The left margin. */
    int	    leftMargin;

    /** The right margin. */
    int	    rightMargin;

    /** Character attribute to add, e.g., BOLD, ITALIC. */
    int	    fontAttrAdd;

    /** Character attributes to clear, e.g., BOLD, ITALIC. */
    int	    fontAttrClr;

    /** Size of the font to use. */
    int	    fontSize;

    /** Family name for the font, e.g., Times. */
    String  fontName;

    /**
     * The red, green and blue components of the foreground color to
     * use.
     */
    short   r, g, b;

    /** Paragraph alignment: left, right, center, etc. */    
    byte    align;

    /** Wrapping mode: not, word, character. */
    byte    wrap;

    /**
     * Whether or not the left margin setting is to be added to the
     * current one, or whether it is to be used as the new left
     * margin.
     */
    boolean lmarginIncrement;

    /**
     * Whether or not the right margin setting is to be added to the
     * current one, or whether it is to be used as the new right
     * margin.
     */
    boolean rmarginIncrement;

    final int	A_LMARGIN = 1;
    final int	A_ATTR_ADD = 2;
    final int	A_ATTR_CLR = 4;
    final int	A_FONT_SIZE = 8;
    final int	A_FONT_NAME = 16;
    final int	A_COLOR = 32;
    final int	A_WRAP = 64;
    final int	A_ALIGN = 128;
    final int	A_RMARGIN = 256;

    int	    which = 0;

    /**
     * Constructs a new instance of BasicStyle.  Initializes itself
     * based on the attribute=value pairs in the specified
     * specification.  Possible attributes and their values are:
     * <dl compact><dt>size<dd>integer size of font <dt>color<dd>
     * integer:integer:integer red, green and blue components of
     * color<dt>style<dd><dl compact><dt>i<dd>turn on italics
     * mode<dt>b<dd>turn on bold mode<dt>u<dd> turn on underline
     * mode<dt>p<dd>go to plain text (no italics or
     * bold)<dt>f<dd>select fixed width
     * font</dl><dt>[+-]leftMargin<dd> integer new value of left
     * margin - if + or - is specified, the new value is relative to
     * the current value<dt>[+-]rightMargin<dd>integer new value of
     * right margin - if + or - is specified, the new value is
     * relative to the current value<dt>wrap<dd><dl compact><dt>not<dd>wrap
     * only at newlines in the document<dt>word<dd>wrap at word
     * bounderies<dt>char<dd>wrap as soon as a character doesn't fit
     * on the line</dl></dl>.
     */

    public BasicStyle(String spec) {
	super(spec);
    }

    /** Handles the initialization string described in the constructor. */
    protected void handleStyleSpec(String spec) {
	String	attr = specAttribute(spec);
	String	value = specValue(spec);

	if (attr.equals("size")) {
	    setFontSize(Integer.parseInt(value));
	} else if (attr.equals("color")) {
	    StringTokenizer t = new StringTokenizer(value, ":");
	    int	r, g, b;

	    r = Integer.parseInt(t.nextToken());
	    g = Integer.parseInt(t.nextToken());
	    b = Integer.parseInt(t.nextToken());
	    setColor(r, g, b);
	} else if (attr.equals("style")) {
	    int	i = 0;
	    int cnt = value.length();
	    int	c;

	    while (--cnt >= 0) {
		switch ((c = value.charAt(i++))) {
		case 'i':
		    addFontAttribute(FormattingParameters.ITALIC);
		    break;

		case 'b':
		    addFontAttribute(FormattingParameters.BOLD);
		    break;

		case 'u':
		    addFontAttribute(FormattingParameters.UNDERLINE);
		    break;

		case 'p':
		    clearFontAttribute(FormattingParameters.ITALIC | FormattingParameters.BOLD);
		    break;

		case 'f':
		    setFontName("Courier");
		    break;

		default:
		    throw new Exception("Unknown font modifier: " + new Character((char)c));
		}
	    }
	} else if (attr.equals("leftMargin")) {
	    if (value.charAt(0) == '+' || value.charAt(0) == '-') {
		lmarginIncrement = true;
		if (value.charAt(0) == '+') {
		    value = value.substring(1);
		}
	    }		
	    setLeftMargin(Integer.parseInt(value));
	} else if (attr.equals("rightMargin")) {
	    if (value.charAt(0) == '+' || value.charAt(0) == '-') {
		rmarginIncrement = true;
		if (value.charAt(0) == '+') {
		    value = value.substring(1);
		}
	    }		
	    setRightMargin(Integer.parseInt(value));
	} else if (attr.equals("align")) {
	    switch (value.charAt(0)) {
	    case 'l':
		setAlignment(FormattingParameters.ALIGN_LEFT);
		break;

	    case 'r':
		setAlignment(FormattingParameters.ALIGN_RIGHT);
		break;

	    case 'c':
		setAlignment(FormattingParameters.ALIGN_CENTER);
		break;
	    }
	} else if (attr.equals("wrap")) {
	    if (value.equals("not")) {
		setWrap(FormattingParameters.WRAP_NOT);
	    } else if (value.equals("word")) {
		setWrap(FormattingParameters.WRAP_WORD);
	    } else if (value.equals("char")) {
		setWrap(FormattingParameters.WRAP_CHAR);
	    }
	} else {
	    super.handleStyleSpec(spec);
	}
    }

    /**
     * Processes this style in the context provided by a Formatter
     * and a TagRef.  If the specified TagRef has a matching closing
     * tag, then this pushes new parameters onto the formatter's
     * parameter stack.  Otherwise it just applies the style changes
     * implied by this BasicStyle object.
     * @param f	the WRFormatter that's formatting the document
     * that contained this style
     * @param ref the html TagRef that refered to this style
     * @see awt.Formatter
     * @see WRFormatter
     */

    public void start(WRFormatter f, TagRef ref) {
	FormattingParameters	fp;

	super.start(f, ref);
	if (ref.tag.hasEndTag) {
	    fp = new FormattingParameters(f.getParameters());
	} else {
	    fp = f.getParameters();
	}

	if (shouldApply(A_LMARGIN)) {
	    if (lmarginIncrement) {
		fp.leftMargin += (short)leftMargin;
	    } else {
		fp.leftMargin = (short)leftMargin;
	    }
	}
	if (shouldApply(A_RMARGIN)) {
	    if (rmarginIncrement) {
		fp.rightMargin += (short)rightMargin;
	    } else {
		fp.rightMargin = (short)rightMargin;
	    }
	}
	if (shouldApply(A_ATTR_ADD))
	    fp.fontAttr |= fontAttrAdd;
	if (shouldApply(A_ATTR_CLR))
	    fp.fontAttr &= ~fontAttrClr;
	if (shouldApply(A_FONT_SIZE))
	    fp.fontSize = (byte)fontSize;
	if (shouldApply(A_FONT_NAME))
	    fp.fontName = fontName;
	if (shouldApply(A_COLOR)) {
	    fp.r = r;
	    fp.g = g;
	    fp.b = b;
	}
	if (shouldApply(A_WRAP)) {
	    fp.wrapStyle = wrap;
	}
	if (shouldApply(A_ALIGN)) {
	    fp.alignment = align;
	}
	if (ref.tag.hasEndTag) {
//	    System.out.println("Pushing for: " + ref);
	    f.pushParameters(fp);
	} else {
	    f.setParameters(fp);
	}
    }

    /**
     * Processes this style in the context provided by a Formatter
     * and a TagRef.  If the specified TagRef is a closing tag, then
     * pop the formatter's parameter stack.
     * @param f	the WRFormatter that's formatting the document
     * that contained this style
     * @param ref the html TagRef that refered to this style
     * @see awt.Formatter
     * @see WRFormatter
     */
    public void finish(WRFormatter f, TagRef ref) {
	super.finish(f, ref);
	if (ref.tag.hasEndTag) {
//	    System.out.println("Popping for: " + ref);
	    f.popParameters();
	}
    }

    public boolean shouldApply(int bit) {
	return ((which & bit) != 0);
    }

    public BasicStyle setWrap(byte wrap) {
	this.wrap = wrap;
	which |= A_WRAP;
	return this;
    }

    public BasicStyle setAlignment(byte align) {
	this.align = align;
	which |= A_ALIGN;
	return this;
    }	

    public BasicStyle setLeftMargin(int lm) {
	leftMargin = lm;
	which |= A_LMARGIN;
	return this;
    }

    public BasicStyle setRightMargin(int rm) {
	rightMargin = rm;
	which |= A_RMARGIN;
	return this;
    }

    public BasicStyle addFontAttribute(int attr) {
	fontAttrAdd = attr;
	which |= A_ATTR_ADD;
	return this;
    }

    public BasicStyle clearFontAttribute(int attr) {
	fontAttrClr = attr;
	which |= A_ATTR_CLR;
	return this;
    }

    public BasicStyle setFontSize(int size) {
	fontSize = size;
	which |= A_FONT_SIZE;
	return this;
    }

    public BasicStyle setFontName(String name) {
	fontName = name;
	which |= A_FONT_NAME;
	return this;
    }

    public BasicStyle setColor(int r, int g, int b) {
	this.r = (short)r;
	this.g = (short)g;
	this.b = (short)b;
	which |= A_COLOR;
	return this;
    }
}

