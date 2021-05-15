/*
 * @(#)FontTable.java	1.11 95/04/10 Sami Shaio
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

import java.lang.*;
import java.util.*;

/**
 * A class that creates font objects. Fonts are addressed by names in
 * the FontTable. These names may not be the actual name of the font.
 * By default, the names "Helvetica", "TimesRoman", "ZapfDingbats",
 * "Dialog", "DialogInput", and "Courier" are always available.
 * "Dialog" is bound to a native font that is appropriate for dialog
 * components such as buttons or menus. In each font category, the
 * sizes 8, 10, 12, 14, 24, 36 are always available. Other sizes may
 * be specified and will work if the requested font is bound to a
 * scalable font on the local platform or if the platform can do
 * font-scaling. If scaling is not available, then the closest match
 * is returned. 
 * 
 * @version 1.11 10 Apr 1995
 * @author Sami Shaio
 */
public class FontTable {
    static Hashtable	fontTable;

    static {
	fontTable = new Hashtable(20);
    }

    /**
     * Return a platform-specific font given a generic name. See Font
     * class for the appropriate values for style.
     */
    public Font getFont(String name, int style, int height) {
	String  fName = compoundName(name, style, height);
	Font	f = (Font)(fontTable.get(fName));

	if (f==null) {
	    try {
		f = new Font(name, style, height);
	    } catch (Exception e) {
		return null;
	    }
	    fontTable.put(fName, f);
	}

	return f;
    }

    /**
     * Return a platform-specific font given a name of the form
     * <generic_name>:style:size where style can be "plain",
     * "bold", "italic" or "bolditalic".
     */
    public Font getFont(String compoundName) {
	Font		f;
	FontSpec	fspec = parseCompoundName(compoundName);

	f = (Font)(fontTable.get(compoundName));
	if (f==null) {
	    try {
		f = new Font(fspec.family, fspec.style, fspec.height);
	    } catch (Exception e) {
		return null;
	    }
	    fontTable.put(compoundName, f);
	}

	return f;	
    }

    /**
     * List the number of fonts on the native platform.
     */
    public int getNativeFontCount() {
	throw new Exception("UNIMPLEMENTED");
    }

    /**
     * Return the name of the native font with the given index.
     */
    public String getNativeFont(int index) {
	throw new Exception("UNIMPLEMENTED");
    }

    /** Returns a string representation of a font-name, style and height.*/
    public String compoundName(String name, int style, int height) {
	String cn = name + ":";

	switch (style & ~Font.UNDERLINE) {
	  case Font.BOLD:
	    cn = cn + "bold";
	    break;
	  case Font.ITALIC:
	    cn = cn + "italic";
	    break;
	  case Font.BOLD+Font.ITALIC:
	    cn = cn + "bolditalic";
	    break;
	  case Font.PLAIN:
	  default:
	    cn = cn + "plain";
	    break;
	}

	cn = cn + ":" + height;

	return cn;
    }

    public static FontSpec	parseCompoundName(String cname) {
	int		separator = cname.indexOf('-');
	int		pseparator;
	FontSpec	fspec = new FontSpec();
	String		field;

	if (separator == -1) {
	    separator = cname.indexOf(':');
	} else {
	    separator = cname.substring(0, separator).indexOf(':');
	}
	if (separator != -1) {
	    fspec.family = cname.substring(0, separator);
	} else {
	    return null;
	}
	pseparator = separator + 1;
	separator = cname.indexOf(':', pseparator);
	if (separator != -1) {
	    field = cname.substring(pseparator, separator);
	    if (field.equals("plain")) {
		fspec.style = Font.PLAIN;
	    } else if (field.equals("bold")) {
		fspec.style = Font.BOLD;
	    } else if (field.equals("italic")) {
		fspec.style = Font.ITALIC;
	    } else if (field.equals("bolditalic")) {
		fspec.style = Font.BOLD+Font.ITALIC;
	    }
	    field = cname.substring(separator + 1);
	} else {
	    fspec.style = Font.PLAIN;
	    field = cname.substring(pseparator);
	}

	fspec.height = Integer.parseInt(field);

	return fspec;
    }
}


