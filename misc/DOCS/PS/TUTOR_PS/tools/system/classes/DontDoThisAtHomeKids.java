/*
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
import java.io.*;

class DontDoThisAtHomeKids {

    public static void main(String args[]) {
	String osname = System.getOSName();
	InputStream is = null;
	StringBuffer buf = new StringBuffer();
	int c;

	if (osname.equals("Solaris")) {
	    is = System.execin("pwd");
	} else if (osname.equals("Win32")) {
	    is = System.execin("cd");
	}
	if (is != null) {
	    while ((c = is.read()) != -1)
	        buf.appendChar((char)c);
	    System.out.print(buf.toString());
	}
    }
}
