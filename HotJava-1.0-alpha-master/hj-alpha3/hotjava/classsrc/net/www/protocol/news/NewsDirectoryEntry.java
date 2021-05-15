/*
 * @(#)NewsDirectoryEntry.java	1.6 95/03/28 James Gosling, Jonathan Payne
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
    Vector Refs;
    int anum;
    NewsDirectoryEntry next;
    NewsDirectoryEntry child;
    
    NewsDirectoryEntry(String s, String a, int n, String id, String ref) {
	subject = s;
	author = a;
	ID = id;
	Ref = ref;
	int kst = 0;
	int kend = s.length();
	anum = n;
	while (kst < kend) {
	    if (s.charAt(kst) <= ' ')
		kst++;
	    else if (s.startsWith("Re:", kst) || s.startsWith("re:", kst))
		kst += 3;
	    else
		break;
	}
	while (kst < kend) {
	    if (s.charAt(kend - 1) <= ' ')
		kend--;
	    else
		break;
	}
	if (kst < kend)
	    key = s.substring(kst, kend);
	else
	    key = s;
	if (ref != null) {
	    kst = 0;
	    int len = ref.length();
	    Refs = new Vector();
	    while (kst < len) {
		if ((kst = ref.indexOf('<', kst)) < 0)
		    break;
		if ((kend = ref.indexOf('>', kst + 1)) < 0)
		    break;
		Refs.addElement(ref.substring(kst, kend + 1));
		kst = kend + 1;
	    }
	}
    }
    
    /**
     * This procedure inserts this node into a given subtree.
     * The new root of the subtree is returned since this root
     * may become the new root.
     */
    NewsDirectoryEntry insert(NewsDirectoryEntry root, boolean toplevel) {
	if (root == null) {
	    // There is no more subtree.  Terminate recursion.
	    return this;
	}
	if (references(root)) {
	    // We are a descendant of this root.  Recurse.
	    root.child = insert(root.child, false);
	    return root;
	}
	if (root.references(this)) {
	    // We are an ancestor of this subtree.  We become the new root.
	    child = root;
	    // Now we must insert all of the old tree's ancestors either
	    // as our siblings or as our children (the old root's siblings)
	    // deciding based on their Refs vectors.
	    NewsDirectoryEntry n = root.next;
	    NewsDirectoryEntry lastsibling = this;
	    NewsDirectoryEntry lastchild = root;
	    while (n != null) {
		if (n.references(this)) {
		    lastchild.next = n;
		    lastchild = n;
		} else {
		    lastsibling.next = n;
		    lastsibling = n;
		}
		n = n.next;
	    }
	    lastsibling.next = null;
	    lastchild.next = null;
	    return this;
	}
	if (toplevel && key != null && key.equals(root.key)) {
	    // We are in the same conversation as this firstlevel root node.
	    root.child = insert(root.child, false);
	    return root;
	}
	root.next = insert(root.next, toplevel);
	return root;
    }

    boolean references(NewsDirectoryEntry n) {
	return (Refs != null && Refs.contains(n.ID));
    }

    boolean find(String mid) {
	if (mid != null) {
	    NewsDirectoryEntry n = this;
	    while (n != null) {
		if (mid.equalsIgnoreCase(n.ID) || (n.child != null
						   && n.child.find(mid)))
		{
		    return true;
		}
		n = n.next;
	    }
	}
	return false;
    }
}
