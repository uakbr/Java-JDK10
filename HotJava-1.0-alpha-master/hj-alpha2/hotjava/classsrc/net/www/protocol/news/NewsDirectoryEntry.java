/*
 * @(#)NewsDirectoryEntry.java	1.4 95/03/14 James Gosling, Jonathan Payne
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

class NewsDirectoryEntry {
    String subject;
    String author;
    String key;
    String ID;
    String Ref;
    int anum;
    NewsDirectoryEntry next;

    NewsDirectoryEntry(String s, String a, int n, String id, String ref) {
	subject = s;
	author = a;
	ID = id;
	Ref = ref;
	int kst = 0;
	int kend = s.length();
	anum = n;
	while (kst < kend)
	    if (s.charAt(kst) <= ' ')
		kst++;
	    else if (s.startsWith("Re:", kst) || s.startsWith("re:", kst))
		kst += 3;
	    else
		break;
	while (kst < kend)
	    if (s.charAt(kend - 1) <= ' ')
		kend--;
	    else
		break;
	if (kst < kend)
	    key = s.substring(kst, kend);
	else
	    key = s;
    }
    boolean sameConversation(NewsDirectoryEntry n) {
	return (key != null && key.equals(n.key)
		|| ID != null && (ID.equals(n.ID) || ID.equals(n.Ref))
		|| Ref !=null && (Ref.equals(n.ID) || Ref.equals(n.Ref)));
    }
}

