/*
 * @(#)plain.java	1.4 95/01/31  
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

/*
 * Plain text file handler
 */
package net.www.content.text;
import net.www.html.ContentHandler;
import net.www.html.URL;
import java.io.InputStream;
import awt.GifImage;

public class plain extends ContentHandler {
    public Object getContent(InputStream is, URL u) {
	StringBuffer sb = new StringBuffer();
	int c;
	while ((c = is.read()) >= 0) {
	    sb.appendChar((char)c);
	}
	is.close();
	return sb.toString();
    }
}
