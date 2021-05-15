/*
 * @(#)ArticlePoster.java	1.11 95/02/16 James Gosling, Jonathan Payne
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
import net.www.html.MessageHeader;
import awt.*;

/** Post an article to either a newsgroup or email */
public class ArticlePoster extends Frame implements Runnable {
    static ArticlePoster postDialog;	// we never have one instance
    InputStream is;
    String reference;
    TextArea t;
    TextField subject;
    TextField group;
    Label statuslb;
    Toggle htmlToggle;
    boolean mailing;

    private ArticlePoster() {
	super(true, 600, 575, Color.lightGray);
	Font defaultFont = wServer.fonts.getFont("TimesRoman", Font.PLAIN, 12);
	setDefaultFont(defaultFont);
	Window w = new Window(this, "Center", background, 700, 200);
	w.setLayout(new ColumnLayout(true));
	t = new TextArea(w, "Center", defaultFont, 80, 24);
	t.setHFill(true);
	t.setVFill(true);
	w = new Window(this, "North", background, 700, 150);
	RowColLayout r = new RowColLayout(0, 2, true);
	w.setLayout(r);
	r.setGaps(0, 0, 0, 0);
	new Label("Subject:", null, w, defaultFont);
	subject = new TextField(null, null, w, true);
	new Label("Destination:", null, w, defaultFont);
	group = new TextField(null, null, w, true);
	w = new Window(this, "South", background, 700, 30);
	w.setLayout(new ColumnLayout(true));
	Row row = new Row(w,null,true);
	new PostMessageButton(row, this);
	new DismissButton(row, this);
	htmlToggle = new Toggle("Formatted in HTML", null, row, false);
	statuslb = new Label("", null, w);
	statuslb.setHFill(true);
    }

    void status(String s) {
	statuslb.setText(s != null ? s : "");
    }

    private static void StartSTComposition(String Subject, String Title,
					   String Destination, boolean ml) {
	if (postDialog == null)
	    postDialog = new ArticlePoster();
	postDialog.StartComposition(Subject, Title, Destination, ml);
    }

    private void StartComposition(String Subject, String Title,
				  String Destination, boolean ml) {
		this.is = null;
	reference = null;
	htmlToggle.setState(false);
	mailing = ml;
	t.setText("");
	status(null);
	subject.setText(Subject);
	group.setText(Destination);
	setTitle(Title);
	map();
	resize();
    }

    /** Pop up a dialog box to post a message to a newsgroup.
	The input stream should contain an article that the one
	being posted is in reply to. */
    static public void PostTo(InputStream is) {
	StartSTComposition("", "Composing News Article",
			 "", false);
	postDialog.is = is;
	new Thread (postDialog).start();
    }

    /** Pop up a dialog box to post a message to a newsgroup.
	Initializes the group name to gn. */
    static public void PostTo(String gn) {
	StartSTComposition("", "Composing article for " + gn,
			 gn, false);
    }

    /** Pop up a dialog box to post a message to a newsgroup.
	No default group */
    static public void PostTo() {
	StartSTComposition("", "Composing News Article",
			 "", false);
    }

    /** Pop up a dialog box to mail a message.  The subject
	and destiniation will start out being blank. */
    static public void MailTo() {
	StartSTComposition("", "Composing Mail Message",
			 "", true);
    }

    /** Pop up a dialog box to mail a message.  The subject
	and destiniation will be initialized from the parameters. */
    static public void MailTo(String to, String subject) {
	StartSTComposition(to, "Composing Mail Message",
			 subject, true);
    }

    /** Pop up a dialog box to mail a message.  The subject
	and destiniation will be initialized from the message being
	replied to which should appear on the input stream. */
    static public void MailTo(InputStream is) {
	StartSTComposition("", "Composing Mail Message",
			 "", true);
	postDialog.is = is;
	new Thread (postDialog).start();
    }

    public void run() {
	MessageHeader mh = new MessageHeader(is);
	String s;
	String messageID = mh.findValue("message-id");
	s = mh.findValue("references");
	if (s == null)
	    s = messageID;
	if (s != null)
	    reference = mh.canonicalID(s);
	if (mailing) {
	    if ((s = mh.findValue("from")) != null)
		group.setText(s);
	} else if ((s = mh.findValue("newsgroups")) != null)
	    group.setText(s);
	s = mh.findValue("subject");
	if (s != null) {
	    if (s.startsWith("Re:"))
		subject.setText(s);
	    else
		subject.setText("Re: " + s);
	}
	if (!mailing) {
	    if (messageID != null)
		t.insertText("In news:" + mh.canonicalID(messageID) + " ", t.endPos());
	    s = mh.findValue("from");
	    if (s != null)
		t.insertText(s + " says:\n", t.endPos());
	}
	if (t.endPos() > 1)
	    t.insertText("\n", t.endPos());
	if (is != null) {
	    char cb[] = new char[200];
	    int cbl = 0;
	    int c;
	    int col = 0;
	    int limit = cb.length - 3;
	    while ((c = is.read()) >= 0) {
		if (cbl >= limit) {
		    t.insertText(new String(cb, 0, cbl), t.endPos());
		    cbl = 0;
		}
		if (c == '\n')
		    col = 0;
		else if (col == 0) {
		    cb[cbl++] = '>';
		    cb[cbl++] = ' ';
		    col++;
		}
		cb[cbl++] = (char) c;
	    }
	    if (cbl > 0)
		t.insertText(new String(cb, 0, cbl), t.endPos());
	    is.close();
	}
    }
}

class DismissButton extends Button {

    public DismissButton(Container w, Frame f) {
	this(w, f, "Cancel");
    }
    
    public DismissButton(Container w, Frame f, String label) {
	super(label, null, w);

	frame = f;
    }
    
    public void selected(Component c, int pos) {
	frame.unMap();
    }

    Frame	frame;
}
