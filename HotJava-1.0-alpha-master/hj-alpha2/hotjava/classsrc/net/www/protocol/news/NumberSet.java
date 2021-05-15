/*
 * @(#)NumberSet.java	1.4 95/03/14 James Gosling, Jonathan Payne
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies. Please
 * refer to the file "copyright.html" for further important copyright and
 * licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 */

package net.www.protocol.news;

import java.io.*;
import java.util.*;
import net.nntp.*;
import net.smtp.SmtpClient;
import browser.Applet;
import browser.WRWindow;
import browser.DocumentManager;
import net.TelnetInputStream;
import net.UnknownHostException;
import awt.*;

class NumberSet {
    int starts[];
    int ends[];
    int nranges = 0;

    public NumberSet() {
    }

    public String toString() {
	StringBuffer buf = new StringBuffer(10);

	buf.append("NumberSet[");
	buf.append(rangesString());
	buf.append("]");

	return buf.toString();
    }

    public String rangesString() {
	StringBuffer buf = new StringBuffer();
	int i;

	for (i = 0; i < nranges; i++) {
	    if (starts[i] == ends[i])
		buf.append(starts[i]);
	    else {
		buf.append(starts[i]);
		buf.append("-");
		buf.append(ends[i]);
	    }
	    if (i < nranges - 1)
		buf.append(",");
	}
	return buf.toString();
    }

    private int growArray(int array[])[] {
	int narray[] = new int[array.length + 10];
	int i;

	for (i = array.length; --i >= 0;)
	    narray[i] = array[i];

	return narray;
    }

    private void makeCanonical() {
	int i;

	for (i = nranges - 1; --i >= 0;) {
	    if (ends[i] + 1 == starts[i + 1]) {
		ends[i] = ends[i + 1];
		delete(i + 1, 1);
	    }
	}
    }

    private void insert(int where, int s, int e) {
	int i = where;

	if (i < nranges && e == starts[i] - 1) {
	    starts[i] = s;
	    makeCanonical();
	} else if (i > 0 && s == ends[i - 1] + 1) {
	    ends[i - 1] = e;
	    makeCanonical();
	} else {
	    for (i = nranges; --i >= where;) {
		starts[i + 1] = starts[i];
		ends[i + 1] = ends[i];
	    }
	    starts[where] = s;
	    ends[where] = e;
	    nranges += 1;
	}
    }

    private void delete(int where, int count) {
	int i;
	int cnt = nranges - (where + count);

	for (i = where; --cnt >= 0; i++) {
	    starts[i] = starts[i + count];
	    ends[i] = ends[i + count];
	}
	nranges -= count;
    }

    private int findClosest(int n) {
	int i = nranges - 1;

	while (i > 0 && starts[i] > n)
	    i -= 1;
	return i;
    }

    public void add(int s, int e) {
	int i;
	int startIndex;
	int endIndex;

	if (s > e) {
//	    System.out.print("Invalid range: [" + s + "-" + e + "]\n");
	    e = s;
	}
	if (starts == null) {
	    starts = new int[10];
	    ends = new int[10];
	} else if (nranges >= starts.length) {
	    starts = growArray(starts);
	    ends = growArray(ends);
	}
	if (nranges == 0 || e < starts[0])
	    insert(0, s, e);
	else if (s > ends[nranges - 1])
	    insert(nranges, s, e);
	else {
	    startIndex = findClosest(s);
	    endIndex = findClosest(e);
	    if (startIndex == endIndex && !contains(s))
		insert(startIndex + 1, s, e);
	    else {
		starts[startIndex] = Math.min(s, starts[startIndex]);
		ends[startIndex] = Math.max(e, ends[endIndex]);
		if (endIndex != startIndex)
		    delete(startIndex + 1, endIndex - startIndex + 1);
	    }
	}
    }

    public void delete(int item) {
	if (contains(item)) {
	    int which = findClosest(item);
	    int s, e;

	    s = starts[which];
	    e = ends[which];
	    delete(which, 1);

	    if (s <= item - 1)
		add(s, item - 1);
	    if (item + 1 <= e)
		add(item + 1, e);
	    makeCanonical();
	}
    }

    public void add(String r) {
	int n;

	if (r.indexOf('-') != -1) {
	    StringTokenizer t = new StringTokenizer(r, "-");

	    add(Integer.parseInt(t.nextToken()), Integer.parseInt(t.nextToken()));
	} else if ((n = Integer.parseInt(r)) > 0)
	    add(n);
    }

    public void add(int i) {
	if (!contains(i))
	    add(i, i);
    }

    public boolean contains(int n) {
	int i;

	for (i = nranges; --i >= 0;)
	    if (n >= starts[i] && n <= ends[i])
		return true;
	return false;
    }

    public int smallest() {
	if (nranges == 0)
	    throw new Exception("Empty Number Set");
	return starts[0];
    }

    public int largest() {
	if (nranges == 0)
	    throw new Exception("Empty Number Set");
	return ends[nranges - 1];
    }

    public boolean isEmpty() {
	return nranges == 0;
    }

    public NumberSet invert(int min, int max) {
	NumberSet r = new NumberSet();
	int i;

	if (nranges == 0) {
	    if (min <= max)
		r.add(min, max);
	} else {
	    if (min < starts[0])
		r.add(min, starts[0] - 1);
	    for (i = 0; i < nranges - 1; i++) {
		if (ends[i] + 1 <= starts[i + 1] - 1)
		    r.add(ends[i] + 1, starts[i + 1] - 1);
	    }
	    if (ends[i] + 1 <= max)
		r.add(ends[i] + 1, max);
	}
	return r;
    }

    int size() {
	int cnt;
	int total = 0;

	for (cnt = nranges; --cnt >= 0;)
	    total += ends[cnt] - starts[cnt] + 1;
	return total;
    }

    static public void main(String args[]) {
	NumberSet set = new NumberSet();

	set.add("312-312");
    }
}
