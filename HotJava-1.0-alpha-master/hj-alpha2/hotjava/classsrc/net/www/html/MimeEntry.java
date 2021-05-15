/*
 * @(#)MimeEntry.java	1.4 95/02/07  
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
import java.io.*;

class MimeEntry {
    String name;
    String command;
    String TempNameTemplate;
    MimeEntry next;
    boolean starred;

    MimeEntry (String nm, String cmd) {
	this(nm, cmd, null);
    }
    MimeEntry (String nm, String cmd, String tnt) {
	name = nm;
	command = cmd;
	TempNameTemplate = tnt;
	if (nm != null && nm.length() > 0 && nm.charAt(nm.length() - 1) == '/')
	    starred = true;
    }

    Object launch(InputStream is, URL u, MimeTable mt) {
	if (command.equalsIgnoreCase("loadtofile"))
		return is;
	if (command.equalsIgnoreCase("plaintext")) {
	    StringBuffer sb = new StringBuffer();
	    int c;
	    while ((c = is.read()) >= 0)
		sb.appendChar((char) c);
	    return sb.toString();
	}
	String message = command;
	int fst = message.indexOf(' ');
	if (fst > 0)
	    message = message.substring(0, fst);
	return new MimeLauncher(this, is, u, mt.TempTemplate(),
				message);
//	new MimeLauncher(this, is, u, mt.TempTemplate()).start();
//	String message = command;
//	int fst = message.indexOf(' ');
//	if (fst > 0)
//	    message = message.substring(0, fst);
//	System.out.print("Launched " + message + "\n");
//	return null;
    }

    boolean matches(String type) {
	if (starred)
	    return type.startsWith(name);
	else
	    return type.equals(name);
    }
}
