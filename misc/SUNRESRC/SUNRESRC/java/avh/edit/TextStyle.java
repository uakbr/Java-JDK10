/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)TextStyle.java	1.7 94/07/23 Feb 1994
 *
 *	Arthur van Hoff, Nov 1993
 *	Arthur van Hoff, March 1994
 */

package edit;

import awt.WServer;
import awt.Font;
import awt.Color;

/**
 * A text style. It consists currently of a font and
 * a color. This is may be extended in the future to
 * contain other formatting hints such as underlining,
 * superscript, subscript etc.
 *
 * A text style is used to descibe the visual properties
 * of a range of text.
 */

public class TextStyle {
    public static WServer ws;

    /** The start position of this style in the text */
    int start;

    /** The font of this style */
    public Font font;

    /** The color of this style */
    public Color color;
    
    /** Construct a text style */
    public TextStyle(Font pFont, Color pColor) {
	font = pFont;
	color = pColor;
    }

    /** Construct a text style */
    public TextStyle(String fontname, int fontheight, int fontstyle) {
	this(ws.fonts.getFont(fontname, fontstyle, fontheight), Color.black);
    }

    /** Construct a text style */
    public TextStyle(String fontname, int fontheight, int fontstyle, int r, int g, int b) {
	this(ws.fonts.getFont(fontname, fontstyle, fontheight), new Color(ws, r, g, b));
    }

    /** Compare two text styles */
    public boolean equal(TextStyle st) {
	return font.equals(st.font) && (color.r == st.color.r) && (color.g == st.color.g) && (color.b == st.color.b);
    }

    /** Convert to a String */
    public String toString() {
	return font.toString() + "+" + color.toString();
    }

    /** Clone */
    public Object clone() {
	return super.clone();
    }
}
