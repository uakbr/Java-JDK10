/*
 * @(#)Text.java	1.5 95/01/31 Jonathan Payne
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

import java.util.*;
import java.io.*;

/**
 * An object that holds lines of text.
 *
 * @version 1.5 31 Jan 1995
 * @author Jonathan Payne
 */
public class Text {
    /** list of lines in this text object */
    Vector  lines = new Vector();

    public Text() {}

    public Text(String data) {
	appendUnformattedString(data);
    }

    public int lineCount() {
	return lines.size();
    }

    public void appendUnformattedString(String input) {
	int from, to;

	from = 0;
	while ((to = input.indexOf('\n', from)) != -1) {
	    addLine(input.substring(from, to));
	    from = to + 1;
	}
	if (from < input.length())
	    addLine(input.substring(from, input.length()));
    }

    public void addLine(String line) {
	lines.addElement(line);
    }

    public String lineAt(int index) {
	return (String) lines.elementAt(index);
    }

    public void clear() {
	lines = new Vector();
    }
}
