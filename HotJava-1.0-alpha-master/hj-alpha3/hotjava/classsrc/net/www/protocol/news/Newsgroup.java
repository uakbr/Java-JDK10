/*
 * @(#)Newsgroup.java	1.7 95/03/28 James Gosling, Jonathan Payne
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

/** This news group object maintains a pointer to the newsgroup
    info object, which is obtained from the nntp server, a set of
    integers representing the group of articles that are unread,
    and a pointer to the "current" article in the group.
    currentArticle can be either (1) -1, meaning it is pointing to
    before the first article to read, (2) an unread article (i.e.,
    articles.contains(currentArticle) => true), or (3) 1
    greater than the maximum unread article.

    Therefore, when a newsgroup is first opened for reading,
    currentArticle is -1.  After the first article is opened,
    currentArticle is the article that was last requested and
    displayed.  If currentArticle is > the maximum unread article,
    then we are finished with the newsgroup. */

class Newsgroup {
    /** Groupinfo as returned from the NNTP server for this group. */
    NewsgroupInfo group;

    /** Unread articles in this group, OR, if !subscribed, the
        read articles (so we can save back to newsrc file). */
    NumberSet articles;

    /** Current article we're looking at. */
    int currentArticle;

    /** Whether or not we're subscribed to this group.  See above
	for how this affects how articles is interpreted. */
    boolean subscribed;

    static Newsgroup readNewsrcFile(String name, NntpClient news)[] {
	DataInputStream in;
	Vector groupVector = new Vector(10);
	FileInputStream file = null;

	try {
	    file = new FileInputStream(name);
	    in = new DataInputStream(new BufferedInputStream(file));

	    String data;
	    /* unsubscribed newsgroups are skipped to save space in
	       internal data structures.  To preserve compatibility
	       with other browsers, writeNewsrcFile compensates for
	       this by copying the unsubscribed entries in the original
	       .newsrc into the new .newsrc that do not occur in
	       the internal database. */
	    while ((data = in.readLine()) != null) {
		if (data.indexOf('!') < 0)
		try {
		    Newsgroup n = new Newsgroup(data, news);

		    groupVector.addElement(n);
		} catch(UnknownNewsgroupException e) {
		}
	    }
	} catch(FileNotFoundException e) {
	} catch(IOException e) {
	    if (file != null)
		file.close();
	}
	if (groupVector.size() > 0) {
	    Newsgroup groups[] = new Newsgroup[groupVector.size()];

	    groupVector.copyInto(groups);
	    return groups;
	}
	return null;
    }

    public static void writeNewsrcFile(String name, Newsgroup groups[]) {
	PrintStream out;
	int i;
	int cnt;
	String tempname = name+".tmp";
	out = new PrintStream(new BufferedOutputStream
			      (new FileOutputStream(tempname)));
	for (i = 0, cnt = groups.length; --cnt >= 0; i++) {
	    out.print(groups[i].newsrcString());
	    out.print("\n");
	}
	DataInputStream in;
	FileInputStream file = null;

	try {
	    file = new FileInputStream(name);
	    in = new DataInputStream(new BufferedInputStream(file));
	    String data;
	    while ((data = in.readLine()) != null) {
		int uns = data.indexOf('!');
		if (uns > 0) {
		    for (i = 0, cnt=groups.length; --cnt>=0; i++) {
			String n = groups[i].group.name;
			if (n.length() == uns && data.startsWith(n))
			    break;
		    }
		    if (cnt < 0) {
			out.print(data);
			out.print("\n");
		    }
		}
	    }
	} catch(Exception e){};
	if (file != null)
	   file.close();
	out.flush();
	out.close();
	try {
	    if (!new File(tempname).renameTo(new File(name)))
		System.out.print("Rename failed\n");
	} catch(Exception e) {
	}
    }

    public String newsrcString() {
	NumberSet set;
	String result = group.name + (subscribed ? ":" : "!");
	if (articles != null) {
	    set = (subscribed ? articles.invert(1, group.lastArticle) : articles);
	    if (!set.isEmpty())
		result = result + " " + set.rangesString();
	} else if (subscribed)
	    result = result + " 1-" + group.lastArticle;
	return result;
    }

    public Newsgroup(String input, NntpClient news) {
	parseNewsrcString(input, news);
	currentArticle = -1;
    }

    public void parseNewsrcString(String input, NntpClient news) {
	NumberSet read = null;
	int nameEndIndex;

	if ((nameEndIndex = input.indexOf('!')) != -1)
	    subscribed = false;
	else if ((nameEndIndex = input.indexOf(':')) == -1)
	    throw new Exception("Malformed newsgroup specification: " + input);
	else
	    subscribed = true;
	if (subscribed)
	    group = news.getGroup(input.substring(0, nameEndIndex));
	else
	    group = new NewsgroupInfo(input.substring(0, nameEndIndex), -1, -1);
	if (nameEndIndex + 1 < input.length()) {
	    StringTokenizer t;
	    t = new StringTokenizer(input.substring(nameEndIndex + 1), ", ");

	    try {
		do {
		    String spec = t.nextToken();
		    if (read == null)
			read = new NumberSet();
		    read.add(spec);
		} while (t.hasMoreTokens());
	    } catch(NoSuchElementException e) {
	    }
	}
	if (subscribed) {
	    if (group.firstArticle == 0 && group.lastArticle == 0) {
		// This group is empty, read.invert will do the wrong thing.
		articles = new NumberSet();
	    } else {
		if (read == null)
		    read = new NumberSet();
		articles = read.invert(group.firstArticle, group.lastArticle);
	    }
	} else
	    articles = read;
    }

    /** open prepares this newsgroup for reading.  It resets the
        current article to the first unread article in the group.
	It returns false if there are no articles for reading. */

    public boolean open(NntpClient news) {
	group.reload(news);
	return !articles.isEmpty();
    }

    public void close() {
	currentArticle = -1;
    }

    int firstArticle() {
	return articles.isEmpty() ? -1 : articles.smallest();
    }

    int lastArticle() {
	return articles.isEmpty() ? -1 : articles.largest();
    }

    /** findNextArticle returns the next article number for
	reading, OR, -1 if there are no more articles. */
    public int findNextArticle() {
	int upper;

	if (!subscribed || articles.isEmpty()
		|| currentArticle >= lastArticle())
	    return -1;

	if (currentArticle == -1)
	    currentArticle = firstArticle();
	else
	    currentArticle += 1;

	upper = articles.largest();
	while (currentArticle <= upper) {
	    if (articles.contains(currentArticle))
		break;
	    currentArticle += 1;
	}
	return currentArticle;
    }

    public boolean contains(int articleNumber) {
	return articles != null && articles.contains(articleNumber);
    }

    public void markAsRead(int articleNumber) {
	if (articles == null)
	    articles = new NumberSet();
	if (articles.contains(articleNumber))
	    articles.delete(articleNumber);
    }

    public void markAsUnread(int articleNumber) {
	if (articles == null)
	    articles = new NumberSet();
	if (!articles.contains(articleNumber))
	    articles.add(articleNumber);
    }

    public int unreadArticleCount() {
	return articles.size();
    }

    public String toString() {
	return "Newsgroup[info=" + group + ", unread=" + articles + "]";
    }

    private String spaces = "                              ";

    String padded(String arg, int size) {
	int spacesSize = spaces.length();

	size -= arg.length();

	while (size > 0) {
	    if (size < spacesSize) {
		arg = arg + spaces.substring(0, size);
		break;
	    } else {
		arg = arg + spaces;
		size -= spacesSize;
	    }
	}
	return arg;
    }

    public String headerString() {
	return padded(group.name, 40) + articles.size()
	    + " total unread articles";
    }
}
