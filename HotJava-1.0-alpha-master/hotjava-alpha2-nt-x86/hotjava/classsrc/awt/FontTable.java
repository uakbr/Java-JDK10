/*
 * @(#)FontTable.java	1.10 95/01/31 Sami Shaio
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
 * A class that creates native fonts. Fonts are addressed by names in
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
 * @version 1.10 31 Jan 1995
 * @author Sami Shaio
 */
public class FontTable {
    Hashtable	fontTable;
    Hashtable	fontNames;
    WServer	wServer;

    public FontTable(WServer ws) {
	wServer = ws;
	fontTable = new Hashtable(20);
	fontNames = new Hashtable(20);
	addMapping("Helvetica",	  ws.fontMapName("Helvetica"));
	addMapping("TimesRoman",  ws.fontMapName("TimesRoman"));
	addMapping("Courier",	  ws.fontMapName("Courier"));
	addMapping("Dialog",      ws.fontMapName("Dialog"));
	addMapping("DialogInput", ws.fontMapName("DialogInput"));
	addMapping("ZapfDingbats", ws.fontMapName("ZapfDingbats"));
	addMapping("times", mapName("TimesRoman"));
	addMapping("helvetica", mapName("Helvetica"));
	addMapping("courier", mapName("Courier"));
    }

    /**
     * Return a platform-specific font given a generic name. See Font
     * class for the appropriate values for style.
     */
    public Font getFont(String name, int style, int height) {
	String  mName = mapName(name);
	String  fName = compoundName(mName, style, height);
	Font	f = (Font)(fontTable.get(fName));

	if (f==null) {
	    try {
		f = new Font(wServer, mName, name, style, height);
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
	int		dash = compoundName.indexOf(':');
	String  	mapped;

	mapped = mapName(fspec.family);
	compoundName = mapped + compoundName.substring(dash); 
	f = (Font)(fontTable.get(compoundName));
	if (f==null) {
	    try {
		f = new Font(wServer, mapped, fspec.family,
			     fspec.style, fspec.height);
	    } catch (Exception e) {
		return null;
	    }
	    fontTable.put(compoundName, f);
	}

	return f;	
    }

    /**
     * Map a generic font name to the native font it represents.
     */
    public synchronized String mapName(String genName) {
	String pName = (String)(fontNames.get(genName));

	if (pName == null) {
	    /* native platform gets to map the default names */
	    return wServer.fontMapName(genName);
	} else {
	    return pName;
	}
    }


    /**
     * Add a mapping from a generic name to a native fontname. Passing
     * in null for platformName clears the mapping.
     */
    public synchronized void addMapping(String genName, String platformName) {
	fontNames.remove(genName);
	if (platformName != null) {
	    fontNames.put(genName, platformName);
	}
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

	switch (style) {
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


