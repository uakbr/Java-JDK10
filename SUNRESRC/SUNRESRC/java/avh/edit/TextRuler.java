/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)TextRuler.java	1.7 94/07/23 Feb 1994
 *
 *	Arthur van Hoff, Nov 1993
 *	Arthur van Hoff, March 1994
 */

package edit;

/**
 * A text ruler specifies the paragraph formatting parameters
 * of a piece of text. A ruler  can only span complete paragraphs.
 * This is enforced by the text editing methods.
 */

public class TextRuler {
    /** Start position in the text */
    int start;

    /** The format of this Ruler */
    public int format;

    /** The indentation of the first line relative to the left margin */
    public int firstIndent;

    /** The indentation of all but the first line relative to the left margin */
    public int leftIndent;

    /** The indentation of all lines relative to the right margin */
    public int rightIndent;

    /** Space above this paragraph */
    public int spaceAbove;

    /** Space below this paragraph */
    public int spaceBelow;

    /** Minimal font ascent */
    public int minAscent;

    /** Minimal font descent */
    public int minDescent;

    /** The width of a tab in pixels measured from the left margin */
    public int tabWidth;

    /** Don't format the text, lines are broken at character boundaries */
    public final static int FORMAT_NONE	= 0;

    /** Format left indented, lines are broken at word boundaries. */
    public final static int FORMAT_LEFT	= 1;

    /** Format right indented, lines are broken at word boundaries. */
    public final static int FORMAT_RIGHT	= 2;

    /** Format centered, lines are broken at word boundaries. */
    public final static int FORMAT_CENTER	= 3;

    /** Format filled lines (insert spaces until the lines fit exactly),
     * lines are broken at word boundaries. */
    public final static int FORMAT_FILL	= 4;

    /** Create a ruler, all parameters will be assigned default values */
    public TextRuler() {
	format = FORMAT_LEFT;
	firstIndent = leftIndent = rightIndent = 5;
	minAscent = 12;
	minDescent = 4;
	tabWidth = 40;
	spaceAbove = spaceBelow = 2;
    }

    /** Create a ruler, all parameters will be assigned reasonable values for
     * the given style */
    public TextRuler(int pFormat, TextStyle pStyle) {
	format = pFormat;
	firstIndent = leftIndent = rightIndent = 5;
	minAscent = pStyle.font.ascent;
	minDescent = pStyle.font.descent;
	tabWidth = pStyle.font.stringWidth("        ");
	spaceAbove = spaceBelow = 2;
    }

    /** Convert to a String */
    public String toString() {
	return "ruler(" + firstIndent + "," + leftIndent + "," + rightIndent + ")";
    }

    /** Clone */
    public Object clone() {
	return super.clone();
    }
}
