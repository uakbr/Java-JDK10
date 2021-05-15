/*
 * @(#)PostMessageButton.java	1.7 95/05/12 James Gosling, Jonathan Payne
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
// import browser.Applet;
// import browser.WRWindow;
// import browser.DocumentManager;
import browser.hotjava;
import net.TelnetInputStream;
import net.UnknownHostException;
import awt.*;

/** The "post" button on the mail/news dialog box */
class PostMessageButton extends Button implements Runnable {
    public PostMessageButton (Container w, ArticlePoster ap) {
	super("Send", "", w);
	article = ap;
    }

    private Thread sender;

    public void selected(Component ct, int pos) {
	if (sender != null && sender.isAlive())
	    article.status("Send in progress.");
	else {
	    article.status("Sending...");
	    sender = new Thread(this);
	    sender.start();
	}
    }

    public void run() {
	String user = article.from.getText().trim();
	if (user.length() == 0) {
	    article.status("Please enter a 'from' address");
	    sender = null;
	    return;
	}
	if (hotjava.props != null) {
	    String ouser = (String) hotjava.props.get("usersMailAddress");
	    if (!user.equals(ouser)) {
		hotjava.props.put("usersMailAddress", user);
		hotjava.props.save();
	    }
	}
	if (article.mailing) {
	    try {
		String to = article.group.getText().trim();
		String cc = article.cc != null ? article.cc.getText().trim() : "";
		SmtpClient os = new SmtpClient();
		os.from(user);
		os.to(to);
		os.to(cc);
		PrintStream ms = os.startMessage();
		ms.print("From: " + user + "\nTo: " + to + "\nSubject: " + article.subject.getText() + "\r\n");
		if (cc.length() > 0)
		    ms.print("Cc: " + cc + "\r\n");
		String pd = article.wwwEncodedBody;
		if (article.reference != null)
		    ms.print("In-Reply-To: <" + article.reference + ">\n");
		if (pd != null)
		    ms.print("Content-Type: application/x-www-form-urlencoded\n");
		else if (article.htmlToggle != null && article.htmlToggle.getState())
		    ms.print("Content-type: text/html\n");
		ms.print("x-Mailer: " + hotjava.programName + " " + hotjava.version + " "
			 + System.getOSName() + "\n");
		if (pd != null) {
		    int len = pd.length();
		    int pos = 0;
		    int col = 0;
		    ms.print("Content-Length: "+(len+(len/70+1)*2)+"\r\n\r\n");
		    while (pos < len) {
			char c = pd.charAt(pos++);
			col++;
			if (col > 70) {
			    col = 1;
			    ms.print("\r\n");
			}
			ms.write(c);
		    }
		    ms.print("\r\n");
		} else {
		    ms.print("\r\n");
		    if (article.t != null)
			ms.print(article.t.getText());
		}
		os.closeServer();
		article.unMap();
		article.dispose();
	    } catch(Exception e) {
		String msg = e.getMessage();
		if (msg == null)
		    msg = e.toString();
		article.status("Mail send failure: " + msg);
	    }
	} else {
	    NntpClient ns = newsFetcher.news;
	    PrintStream ps = ns.startPost();
	    if (ps == null)
		article.status("Couldn't start post: " + ns.getResponseString());
	    else {
		ps.print("Subject: " + article.subject.getText() + "\r\n");
		ps.print("From: " + user + "\r\n");
		if (article.htmlToggle != null && article.htmlToggle.getState())
		    ps.print("Content-type: text/html\n");
		if (article.reference != null)
		    ps.print("References: <" + article.reference + ">\n");
		ps.print("Newsgroups: " + article.group.getText() + "\r\n\r\n");
		String body = article.t != null ? article.t.getText() : "";
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
		else {
		    article.unMap();
		    article.dispose();
		}
	    }
	}
	sender = null;
    }

    ArticlePoster article;
}
