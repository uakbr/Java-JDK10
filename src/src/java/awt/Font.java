/*
 * @(#)Font.java	1.13 95/12/14 Sami Shaio
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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
package java.awt;

/** 
 * A class that produces font objects. 
 *
 * @version 	1.13, 12/14/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @author 	Jim Graham
 */
public class Font {

    /* 
     * Constants to be used for styles. Can be combined to mix
     * styles. 
     */

    /**
     * The plain style constant.  This can be combined with the other style
     * constants for mixed styles.
     */
    public static final int PLAIN	= 0;

    /**
     * The bold style constant.  This can be combined with the other style
     * constants for mixed styles.
     */
    public static final int BOLD	= 1;

    /**
     * The italicized style constant.  This can be combined with the other
     * style constants for mixed styles.
     */
    public static final int ITALIC	= 2;

    /**
     * Private data.
     */
    private int pData;

    /** 
     * The platform specific family name of this font. 
     */
    private String family;

    /** 
     * The logical name of this font. 
     */
    protected String name;

    /** 
     * The style of the font. This is the sum of the
     * constants PLAIN, BOLD, or ITALIC. 
     */
    protected int style;

    /** 
     * The point size of this font. 
     */
    protected int size;

    /**
     * Creates a new font with the specified name, style and point size.
     * @param name the font name
     * @param style the constant style used
     * @param size the point size of the font
     * @see Toolkit#getFontList
     */
    public Font(String name, int style, int size) {
	this.family = System.getProperty("awt.font." + name.toLowerCase(), name);
	this.name = name;
	this.style = style;
	this.size = size;
    }

    /**
     * Gets the platform specific family name of the font.
     * Use getName to get the logical name of the font.
     * @see #getName
     */
    public String getFamily() {
	return family;
    }

    /**
     * Gets the logical name of the font.
     * @see #getFamily
     */
    public String getName() {
	return name;
    }

    /**
     * Gets the style of the font.
     * @see #isPlain
     * @see #isBold
     * @see #isItalic
     */
    public int getStyle() {
	return style;
    }

    /**
     * Gets the point size of the font.
     */
    public int getSize() {
	return size;
    }

    /**
     * Returns true if the font is plain.
     * @see #getStyle
     */
    public boolean isPlain() {
	return style == 0;
    }

    /**
     * Returns true if the font is bold.
     * @see #getStyle
     */
    public boolean isBold() {
	return (style & BOLD) != 0;
    }

    /**
     * Returns true if the font is italic.
     * @see #getStyle
     */
    public boolean isItalic() {
	return (style & ITALIC) != 0;
    }

    /**
     * Gets a font from the system properties list.
     * @param nm the property name
     */
    public static Font getFont(String nm) {
	return getFont(nm, null);
    }

    /**
     * Gets the specified font from the system properties list.
     * @param nm the property name
     * @param font a default font to return if property 'nm' is not defined
     */
    public static Font getFont(String nm, Font font) {
	String str = System.getProperty(nm);
	if (str == null) {
	    return font;
	}
	String fontName = str;
	int fontSize = 12;
	int fontStyle = Font.PLAIN;

	int i = str.indexOf('-');
	if (i >= 0) {
	    fontName = str.substring(0, i);
	    str = str.substring(i+1);
	    if ((i = str.indexOf('-')) >= 0) {
		if (str.startsWith("bold-")) {
		    fontStyle = Font.BOLD;
		} else if (str.startsWith("italic-")) {
		    fontStyle = Font.ITALIC;
		} else if (str.startsWith("bolditalic-")) {
		    fontStyle = Font.BOLD | Font.ITALIC;
		}
		str = str.substring(i + 1);
	    }
	    try {
		fontSize = Integer.valueOf(str).intValue();
	    } catch (NumberFormatException e) {
	    }
	}
	return new Font(fontName, fontStyle, fontSize);
    }

    /**
     * Returns a hashcode for this font.
     */
    public int hashCode() {
	return name.hashCode() ^ style ^ size;
    }
    
    /**
     * Compares this object to the specifed object.
     * @param obj the object to compare with
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Font) {
	    Font font = (Font)obj;
	    return (size == font.size) && (style == font.style) && name.equals(font.name);
	}
	return false;
    }

    /** 
     * Converts this object to a String representation. 
     */
    public String toString() {
	String	strStyle;

	if (isBold()) {
	    strStyle = isItalic() ? "bolditalic" : "bold";
	} else {
	    strStyle = isItalic() ? "italic" : "plain";
	}

	return getClass().getName() + "[family=" + family + ",name=" + name + ",style=" +
	    strStyle + ",size=" + size + "]";
    }
}
