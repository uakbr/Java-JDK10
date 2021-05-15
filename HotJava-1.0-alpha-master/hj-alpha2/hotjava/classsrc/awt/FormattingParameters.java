/*
 * @(#)FormattingParameters.java	1.8 95/02/10 Jonathan Payne
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

package awt;

/**
 * A class that encapsulates the state parameters relevant for the
 * Formatter.
 *
 * @version 1.8 10 Feb 1995
 * @author Jonathan Payne
 */
public class FormattingParameters {
    public static final byte	PLAIN = 0;
    public static final byte	BOLD = 1;
    public static final byte	ITALIC = 2;
    public static final byte	UNDERLINE = 4;

    public static final byte	WRAP_NOT = 0;	    /* wrap at newline */
    public static final byte	WRAP_WORD = 1;	    /* wrap at word bounderies */
    public static final byte	WRAP_CHAR = 2;	    /* wrap when reach end of line */

    public static final byte	ALIGN_LEFT = 0;	    /* align to left margin */
    public static final byte	ALIGN_CENTER = 1;   /* center text */
    public static final byte	ALIGN_RIGHT = 2;    /* align to right margin */

    public byte    wrapStyle = WRAP_WORD;
    public byte    alignment = ALIGN_LEFT;
    public byte    fontAttr = PLAIN;	    /* bits (PLAIN, BOLD, ITALIC, UNDERLINE) */
    public byte    fontSize = 17;
    public short   leftMargin = 20;
    public short   rightMargin = 20;
    public short   r, g, b;
    public String  fontName = "times";

    public FormattingParameters(FormattingParameters parent) {
	if (parent != null) {
	    copy(parent);
	}
    }

    public String toString() {
	return "FP[" + buildFontName() + ": color = (" + r + ", " + g + ", " + b + ")]";
    }

    public String buildFontName() {
	String name = fontName;

	/* REMIND: Kludged for times-roman right now. */
	switch (fontAttr & (BOLD | ITALIC)) {
	case 0:
	    if (fontName.equals("times")) {
		name = "TimesRoman";
	    }
	    break;

	case BOLD:
	    name = name + ":bold";
	    break;

	case (BOLD | ITALIC):
	    name = name + ":bolditalic";
	    break;

	case ITALIC:
	    name = name + ":italic";
	    break;
	}
	name = name + ":" + fontSize;

	return name;
    }
}
    
