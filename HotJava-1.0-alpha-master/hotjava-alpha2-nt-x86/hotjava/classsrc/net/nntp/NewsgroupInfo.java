/*
 * @(#)NewsgroupInfo.java	1.7 95/01/31 Jonathan Payne, James Gosling
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

import java.io.*;
import net.*;
import java.util.*;

package net.nntp;

/**
 * This class manages information related to the current status of 
 * newsgroups. 
 * 
 * @version 	1.5, 12 Dec 1994
 * @author	Jonathan Payne, James Gosling
 * @see		NntpClient
 */
public class NewsgroupInfo {
    /** This newsgroup's name in network form (eg rec.pets) */
    public String   name;

    /** Number of the first and last articles in the group */
    public int	    firstArticle;
    public int	    lastArticle;

    public NewsgroupInfo(String name, int start, int end) {
	this.name = name;
	firstArticle = start;
	lastArticle = end;
    }

    /** convert the information to a printable string of the form:
     *  <p>
     *  NewsgroupInfo[name=<i>name</i>[<i>firstArticle</i>,<i>lastArticle</i>]
     */
    public String toString() {
	return "NewsgroupInfo[name=" + name + "[" + firstArticle + ", "
	    + lastArticle + "]";
    }

    /** Reload news in the group using nntp.getGroup(this.name) */
    public void reload(NntpClient nntp) {
	copy(nntp.getGroup(name));
    }
}
