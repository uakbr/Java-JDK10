/*
 * @(#)PostMessageButton.java	1.6 95/03/17 James Gosling, Jonathan Payne
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

/** The "post" button on the mail/news dialog box */
class PostMessageButton extends Button implements Runnable {
    public PostMessageButton(Container w, ArticlePoster ap) {
	super("Send", "", w);
	article = ap;
    }

    private Thread sender;

    public void selected(Component ct, int pos) {
	if (sender != null)
	    article.status("Send in progress.");
	else {
	    article.status("Sending...");
	    sender = new Thread (this);
	    sender.start();
	}
    }

    public void run() {
	if (article.mailing) {
	    try {
		String user = System.getenv("USER");
		String to = article.group.getText();
		SmtpClient os = new SmtpClient();
		os.from(user);
		os.to(to);
		PrintStream ms = os.startMessage();
		ms.print("From: " + user + "\nTo: " + to + "\nSubject: " + article.subject.getText() + "\r\n");
		if (article.reference != null)
		    ms.print("In-Reply-To: <" + article.reference + ">\n");
		if (article.htmlToggle.getState())
		    ms.print("Content-type: text/html\n");
		ms.write('\n');
		ms.print(article.t.getText());
		os.closeServer();
		article.unMap();
	    } catch(Exception e) {
		article.status("Mail send failure: " + e.toString());
	    }
	} else {
	    NntpClient ns = newsFetcher.news;
	    PrintStream ps = ns.startPost();
	    if (ps == null)
		article.status("Couldn't start post: " + ns.getResponseString());
	    else {
		ps.print("Subject: " + article.subject.getText() + "\r\n");
		ps.print("From: " + System.getenv("USER") + "\r\n");
		if (article.htmlToggle.getState())
		    ps.print("Content-type: text/html\n");
		if (article.reference != null)
		    ps.print("References: <" + article.reference + ">\n");
		ps.print("Newsgroups: " + article.group.getText() + "\r\n\r\n");
		String body = article.t.getText();
		int limit = body.length();
		int c = 0;
		for (int i = 0; i < limit; i++) {
		    c = body.charAt(i);
		    if (c == '\n')
			ps.print("\r\n");
		    else
			ps.write(c);
		}
		if (c != '\n')
		    ps.print("\r\n");
		if (!ns.finishPost())
		    article.status("Couldn't finish post: " + ns.getResponseString());
		else
		    article.unMap();
	    }
	}
	sender = null;
    }

    ArticlePoster article;
}
