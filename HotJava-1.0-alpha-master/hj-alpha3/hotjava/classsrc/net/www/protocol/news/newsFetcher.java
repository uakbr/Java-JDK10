/*
 * @(#)newsFetcher.java	1.13 95/03/28 James Gosling, Jonathan Payne
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

/*-
 *	news stream opener
 */

package net.www.protocol.news;

import java.io.*;
import java.util.*;
import net.nntp.*;
import net.www.html.MessageHeader;
import net.www.html.WWWClassLoader;
import awt.*;

class newsFetcher extends Thread {
    static Newsgroup Newsgroups[];
    static String newsrcName;

    static NntpClient news;
    String group;
    String article;
    Thread nntpOwner;		/* the nntp connection is inherently single
				 * threaded: we have to lock out multiple
				 * attempts to use it */


    private synchronized void lockServer() {
	while (nntpOwner != null) {
	    if (nntpOwner == Thread.currentThread())
		return;
	    else if (nntpOwner.isAlive())
		wait();
	    else
		break;
	}
	nntpOwner = Thread.currentThread();
    }

    private synchronized void unlockServer() {
	if (nntpOwner == Thread.currentThread())
	    nntpOwner = null;
	notifyAll();
    }

    private synchronized void handoffLock(Thread t) {
	if (nntpOwner == Thread.currentThread())
	    nntpOwner = t;
    }

    static String classLoaderSrc;

    void fireUp(String fn, OutputStream os) {
	try {
	    lockServer();
	    ps = new PrintStream(os);
	    if (news == null) {
		String serverEnvName = System.getenv("NNTPSERVER");
		if (serverEnvName == null)
		    serverEnvName = "newshost";
		news = new NntpClient(serverEnvName);
	    }
	    article = null;
	    group = null;
	    if (fn != null) {
		while (fn.startsWith("/"))
		    fn = fn.substring(1);
		while (fn.endsWith("/"))
		    fn = fn.substring(0, fn.length() - 1);
		if (fn.length() > 0) {
		    int firstc = fn.charAt(0);
		    int seppos = fn.indexOf('/');
		    int atpos = fn.indexOf('@');
		    if (atpos > 0) {
			article = seppos > 0 && atpos > seppos
			    ? fn.substring(seppos + 1)
			    : fn;
		    } else if (seppos > 0) {
			group = fn.substring(0, seppos);
			article = fn.substring(seppos + 1);
		    } else
			group = fn;
		}
	    }
	    start();
	    handoffLock(this);
	} catch(Object e) {
	    unlockServer();
	    throw e;
	}
    }

    static Newsgroup findGroup(String s) {
	if (s != null && Newsgroups != null)
	    for (int i = Newsgroups.length; --i >= 0;) {
		Newsgroup p = Newsgroups[i];
		if (p.group.name.equals(s))
		    return p;
	    }
	return null;
    }

    static Newsgroup addGroup(String s) {
	Newsgroup r = new Newsgroup(s + "!", news);
	Newsgroup ng[];
	if (Newsgroups != null) {
	    ng = new Newsgroup[Newsgroups.length + 1];
	    System.arraycopy(Newsgroups, 0, ng, 0, Newsgroups.length);
	} else
	    ng = new Newsgroup[1];
	ng[ng.length - 1] = r;
	Newsgroups = ng;
	return r;
    }

    PrintStream ps;

    private String makeSafe(String s) {
	int start = 0;
	int pos;
	while ((pos = s.indexOf("&", start)) >= 0) {
	    start = pos + 3;
	    s = s.substring(0, pos) + "&amp;" + s.substring(pos + 1);
	}
	start = 0;
	while ((pos = s.indexOf("<", start)) >= 0) {
	    start = pos + 3;
	    s = s.substring(0, pos) + "&lt;" + s.substring(pos + 1);
	}
	return s;
    }

    private void title(String s) {
	s = makeSafe(s);
	ps.print("<html><title>" + s + "</title>\n<body><H1>" + s + "</H1>\n");
    }

    private void button(String label, String target) {
	ps.print("<app class=net.www.protocol.news.InlineButton label=\""
		 + label + "\" href=" + target + classLoaderSrc + ">\n");
    }

    private void StandardButtons(boolean allgroups) {
	if (allgroups) {
	    button("All Groups", "news:/");
	    button("Catch up", "catchup");
	}
	button("Regenerate page", "regen");
	button("Post", "post");
	button("Mail", "mail");
	button("Save", "saverc");
	button("Rescan", "rescan");
    }

    private void outStr(char s[], int i, int lim, boolean html) {
	while (i < lim) {
	    int c = s[i++];
	    switch (c) {
	      case '<':
		ps.print(html ? "<" : "&lt;");
		break;
	      case '&':
		ps.print(html ? "&" : "&amp;");
		break;
	      default:
		ps.write(c);
		break;
	    }
	}
    }

    private void FormatArticle(InputStream is, int articlenum) {
	boolean html = false;
	Newsgroup thisNG = null;
	MessageHeader mh = new MessageHeader(is);
	String gp = mh.findValue("newsgroups");
	if (gp == null || (gp.indexOf(",") >= 0 && group != null))
	    gp = group;
	String Subject = mh.findValue("subject");
	if (Subject == null) {
	    if (gp == null)
		Subject = "News message";
	    else
		Subject = "Message from " + gp;
	}
	thisNG = findGroup(gp);
	title(Subject);
	ps.print("<blockquote>\n");
	String s;
	if ((s = mh.findValue("from")) != null)
	    ps.print("<i>" + makeSafe(s) + "</i><br>\n");
	if ((s = mh.findValue("organization")) != null)
	    ps.print(makeSafe(s) + "<br>\n");
	if ((s = mh.findValue("date")) != null)
	    ps.print(makeSafe(s) + "<br>\n");
	ps.print("<p>\n");
	if (gp != null)
	    button("Group", "news:/" + gp);
	if (thisNG != null && articlenum > 0) {
	    thisNG.markAsRead(articlenum);
	    if (articlenum > thisNG.group.firstArticle)
		button("Previous", "prev");
	    if (articlenum < thisNG.group.lastArticle)
		button("Next", "next");
	}
	if ((s = mh.findValue("content-type")) != null &&
		s.startsWith("text/html"))
	    html = true;
	StandardButtons(true);
	ps.print("</blockquote>\n");
	ps.print("<hr><pre>");
	int c;
	char line[] = new char[40];
	int len = 0;
	while ((c = is.read()) >= 0) {
	    if (c == '\n') {
		int outed = 0;
		int limit = len - 1;
		if (!html) {
		    for (int i = 3; i < limit; i++) {
			if (line[i] == ':') {
			    int st = -1;
			    if (line[i + 1] == '/' && line[i - 1] == 'p'
				&& line[i - 2] == 't')
			    {
				if (line[i - 3] == 't'
				    && line[i - 4] == 'h')
				{
				    st = 4;	/* seen http: */
				} else if (line[i - 3] == 'f') {
				    st = 3;	/* seen ftp: */
				}
			    } else if (line[i - 1] == 's'
				       && line[i - 2] == 'w'
				       && line[i - 3] == 'e'
				       && line[i - 4] == 'n')
			    {
				st = 4;	/* seen news: */
			    }
			    if (st > 0) {
				outStr(line, outed, i - st, false);
				outed = i - st;
				while (i < len
				       && (c = line[i]) > ' ' && c != '>'
				       && c != '"' && c != '\'' && c != ';'
				       && c != ')' && c != ']' && c != '}')
				{
				    i++;
				}
				while (i > outed && (line[i - 1] == ','
						     || line[i - 1] == '.'))
				{
				    i--;
				}
				ps.print("<a href=\""
					 + new String(line, outed, i - outed)
					 + "\">");
				outStr(line, outed, i, false);
				ps.print("</a>");
				outed = i;
			    }
			}
		    }
		}
		outStr(line, outed, len, html);
		ps.write('\n');
		len = 0;
	    } else {
		if (len >= line.length) {
		    char nln[] = new char[line.length * 2];
		    System.arraycopy(line, 0, nln, 0, line.length);
		    line = nln;
		}
		line[len++] = (char) c;
	    }
	}
	ps.print("</pre>\n");
    }

    private NewsDirectoryEntry dumpRange(Newsgroup ng, NewsDirectoryEntry root,
					 int first, int last)
    {
	for (int j = first; j <= last; j++) {
	    try {
		InputStream is = news.getHeader(j);
		MessageHeader mh = new MessageHeader(is);
		if (is == null)
		    ps.print("Null stream pointer\n");
		else {
		    is.close();
		    String From = mh.findValue("from");
		    String mid = mh.findValue("message-id");
		    if (root != null && root.find(mid))
			continue;
		    if (From != null) {
			int lparen = From.indexOf('(');
			if (lparen >= 0) {
			    int rparen = From.indexOf(')', lparen);
			    if (rparen >= 0)
				From = From.substring(lparen + 1, rparen);
			}
		    }
		    NewsDirectoryEntry n = new NewsDirectoryEntry(
					   mh.findValue("subject"), From, j,
					   mid, mh.findValue("references"));
		    root = n.insert(root, true);
		}
	    } catch(Exception e) {
		if (ng != null)
		    ng.markAsRead(j);
	    }
	}
	return root;
    }

    public void run() {
	try {
	    if (classLoaderSrc == null) {
		classLoaderSrc = " src=news:///";
		try {
		    classLoaderSrc = " src=" + ((WWWClassLoader) this.getClass().getClassLoader()).ctx.toExternalForm();
		} catch(Exception e) {
		}
	    }
	    Newsgroup p = null;
	    if (Newsgroups == null) {
		newsrcName = System.getenv("HOME") + File.separator + ".newsrc";
		Newsgroups = Newsgroup.readNewsrcFile(newsrcName, news);
		if (Newsgroups == null) {
		    newsrcName = System.getenv("HOME") + File.separator + ".hotjava"
			+ File.separator + "newsrc";
		    Newsgroups = Newsgroup.readNewsrcFile(newsrcName, news);
		}
		if (Newsgroups == null)
		    Newsgroups = new Newsgroup[0];
	    }
	    int articlenum = -1;
	    try {
		articlenum = Integer.parseInt(article);
	    } catch(Exception e) {
	    }
	    if (article == null && group == null) {
		title("Newsgroup Directory");
		StandardButtons(false);
		ps.print("<p>\n");
		if (Newsgroups == null || Newsgroups.length == 0) {
		    ps.print("No subscribed newsgroups or .newsrc is unreadable.  If the news: protocol handler was dynamically loaded, it probably doesn't have the necessary authority to read .newsrc.\n<br>Goto the url news:<i>groupname</i> to read a new group");
		} else
		    for (int i = 0; i < Newsgroups.length; i++) {
			p = Newsgroups[i];
			if (p.subscribed) {
			    int unread = p.articles != null ? p.articles.size() : 0;
			    if (unread > 0)
				ps.print("<br><app class=net.www.protocol.news.TotalUnReadIndicator group=" +
					 p.group.name +
					 classLoaderSrc +
					 "><a href=news:/" + p.group.name +
					 "> " + p.group.name
					 + "</a>\n");
			}
		    }
	    } else if (group != null) {
		if (articlenum <= 0) {
		    int i;
		    boolean inConversation = false;
		    title("Newsgroup " + group);
		    StandardButtons(true);
		    boolean groupExists = false;
		    try {
			news.setGroup(group);
			groupExists = true;
		    } catch(Exception e) {
		    }
		    p = findGroup(group);
		    if (p == null) {
			try {
			    p = addGroup(group);
			} catch(Exception e) {
			    p = null;
			}
		    }
		    NumberSet ns = p == null ? null : p.articles;
		    NewsDirectoryEntry root = null;
		    if (p != null && p.subscribed)
			button("Unsubscribe", "unsubscribe");
		    else
			button("Subscribe", "subscribe");
		    boolean readingUnread = true;
		    if (article != null && article.startsWith("upto")) {
			int min = p.group.firstArticle;
			if (min <= 0) {
			    p.group = news.getGroup(p.group.name);
			    min = p.group.firstArticle;
			}
			int max = p.group.lastArticle;
			int hi = max;
			readingUnread = false;
			try {
			    hi = Integer.parseInt(article.substring(4));
			} catch(Exception e) {
			}
			if (hi > max)
			    hi = max;
			int lo = hi - 40;
			if (lo < min)
			    lo = min;
			if (lo > min)
			    button("Earlier",
				   "news:/" + group + "/upto" + (lo + 5));
			if (hi < max)
			    button("Later",
				   "news:/" + group + "/upto" + (hi + 35));
			button("New or unread", "news:/" + group);
			root = dumpRange(p, root, lo, hi);
		    } else {
			button("View All", "news:/" + group + "/upto");
			if (ns != null) {
			    for (i = 0; i < ns.nranges; i++) {
				root = dumpRange(p, root,
						 ns.starts[i], ns.ends[i]);
			    }
			}
		    }
		    // button("Catch up", "catchup");
		    ps.print("<hr>\n");
		    if (root == null) {
			ps.print("No articles found.\n");
			if (readingUnread) {
			    ps.print("Use ");
			    button("View All", "news:/" + group + "/upto");
			    ps.print("to read all messages.\n");
			}
		    }
		    addTree(root, true);
		} else {
		    try {
			news.setGroup(group);
			FormatArticle(news.getArticle(articlenum), articlenum);
		    } catch(Exception e) {
			ps.print("<hr>Error fetching article:<br> <i> " + e + "</i>\n");
		    }
		}
	    } else {
		/* group == null */
		try {
		    FormatArticle(news.getArticle(article), -1);
		} catch(Exception e) {
		    ps.print("<hr><H1>Error fetching article: " + e + "</H1>\n");
		}
	    }
	    ps.print("</body></html>\n");
	    ps.close();
	    unlockServer();
	} catch(Exception e) {
	    try {
		ps.close();
	    } catch(Exception e2) {
	    }
	    try {
		unlockServer();
	    } catch(Exception e3) {
	    }
	}
    }

    void addTree(NewsDirectoryEntry n, boolean toplevel) {
	ps.print("<dl compact>\n");
	while (n != null) {
	    ps.print("<dt><app class=net.www.protocol.news.ReadIndicator"
		     + " group=" + group
		     + " article=" + n.anum
		     + classLoaderSrc
		     + ">\n");
	    ps.print("<dd><a href=news:/" + group + "/" + n.anum + ">\n");
	    if (toplevel) {
		ps.print(makeSafe(n.subject));
	    }
	    if (n.author != null) {
		if (toplevel)
		    ps.print(", ");
		ps.print("<i>" + makeSafe(n.author) + "</i>");
	    }
	    ps.print("</a>\n");
	    if (n.child != null) {
		addTree(n.child, false);
	    }
	    n = n.next;
	}
	ps.print("</dl>\n");
    }
}
