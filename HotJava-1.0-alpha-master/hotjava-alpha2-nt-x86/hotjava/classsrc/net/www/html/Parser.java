/*
 * @(#)Parser.java	1.51 95/03/16 Jonathan Payne
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

import java.util.*;
import java.io.*;

package net.www.html;

/** Net.www.html.Parser takes an input stream and parses it for
    html tags.  It produces an instance of net.www.html.Document
    as a result. */

public class Parser {
    final boolean debug = false;
    static Hashtable	ampChars = new Hashtable();
    static {
	ampChars.put("lt", new Character('<'));
	ampChars.put("gt", new Character('>'));
	ampChars.put("amp", new Character('&'));
	ampChars.put("quot", new Character('"'));
	ampChars.put("nbsp", new Character(' '));   /* remind - incorrect */
	ampChars.put("shy", new Character('-'));    /* remind - incorrect */

	/* NOTE: These are case SENSITIVE, e.g., AElig and aelig. */
	ampChars.put("AElig", new Character(198));
	ampChars.put("Aacute", new Character(193));
	ampChars.put("Acirc", new Character(194));
	ampChars.put("Agrave", new Character(192));
	ampChars.put("Aring", new Character(197));
	ampChars.put("Atilde", new Character(195));
	ampChars.put("Auml", new Character(196));
	ampChars.put("Ccedil", new Character(199));
	ampChars.put("ETH", new Character(208));
	ampChars.put("Eacute", new Character(201));
	ampChars.put("Ecirc", new Character(202));
	ampChars.put("Egrave", new Character(200));
	ampChars.put("Euml", new Character(203));
	ampChars.put("Iacute", new Character(205));
	ampChars.put("Icirc", new Character(206));
	ampChars.put("Igrave", new Character(204));
	ampChars.put("Iuml", new Character(207));
	ampChars.put("Ntilde", new Character(209));
	ampChars.put("Oacute", new Character(211));
	ampChars.put("Ocirc", new Character(212));
	ampChars.put("Ograve", new Character(210));
	ampChars.put("Oslash", new Character(216));
	ampChars.put("Otilde", new Character(213));
	ampChars.put("Ouml", new Character(214));
	ampChars.put("THORN", new Character(222));
	ampChars.put("Uacute", new Character(218));
	ampChars.put("Ucirc", new Character(219));
	ampChars.put("Ugrave", new Character(217));
	ampChars.put("Uuml", new Character(220));
	ampChars.put("Yacute", new Character(221));
	ampChars.put("aacute", new Character(225));
	ampChars.put("acirc", new Character(226));
	ampChars.put("aelig", new Character(230));
	ampChars.put("agrave", new Character(224));
	ampChars.put("aring", new Character(229));
	ampChars.put("atilde", new Character(227));
	ampChars.put("auml", new Character(228));
	ampChars.put("ccedil", new Character(231));
	ampChars.put("eacute", new Character(233));
	ampChars.put("ecirc", new Character(234));
	ampChars.put("egrave", new Character(232));
	ampChars.put("eth", new Character(240));
	ampChars.put("euml", new Character(235));
	ampChars.put("iacute", new Character(237));
	ampChars.put("icirc", new Character(238));
	ampChars.put("igrave", new Character(236));
	ampChars.put("iuml", new Character(239));
	ampChars.put("ntilde", new Character(241));
	ampChars.put("oacute", new Character(243));
	ampChars.put("ocirc", new Character(244));
	ampChars.put("ograve", new Character(242));
	ampChars.put("oslash", new Character(248));
	ampChars.put("otilde", new Character(245));
	ampChars.put("ouml", new Character(246));
	ampChars.put("szlig", new Character(223));
	ampChars.put("thorn", new Character(254));
	ampChars.put("uacute", new Character(250));
	ampChars.put("ucirc", new Character(251));
	ampChars.put("ugrave", new Character(249));
	ampChars.put("uuml", new Character(252));
	ampChars.put("yacute", new Character(253));
	ampChars.put("yuml", new Character(255));

	ampChars.put("copy", new Character(169));
	ampChars.put("reg", new Character(174));
    }

    /** usually the same as input except for ISINDEX (see below) */
    byte    output[];

    /** input string we're parsing */
    byte    input[];

    /** input length */
    int	    inputLength;

    /** current position in input */
    int	    inputSeek;	    

    /** current line number */
    int	    lineCount = 1;  

    /** Document we're building. */
    Document	html;

    /** Read a stream of bytes into a String object as quickly
	as possible. */
    private void readInput(InputStream in) {
	input = new byte[1024*8];
        output = input;

	inputLength = 0;

	int n;
	while ((n = in.read(input, inputLength, input.length - inputLength)) >= 0) {
	    inputLength += n;
	    if (inputLength == input.length) {
		byte newinput[] = new byte[inputLength * 2];
		System.arraycopy(input, 0, newinput, 0, inputLength);
		input = newinput;
	    }
	}
    }

    final int nextChar() {
	if (inputSeek >= inputLength) {
	    return -1;
	}

	int c = input[inputSeek++];
	switch (c) {
	  case '\r':
	    if (inputSeek >= inputLength) {
		lineCount++;
		return '\n';
	    }
	    c = input[inputSeek++];
	    if (c != '\n') {
		inputSeek--;
		c = '\n';
	    }
	    if (c == '\n') {
		lineCount++;
	    }
	    break;
	    
	  case '\n':
	    lineCount++;
	    break;
	}
	return c;
    }

    final int peekChar() {
	return (inputSeek >= inputLength) ? -1 : input[inputSeek];
    }

    final void pushBack() {
	if (input[--inputSeek] == '\n') {
	    lineCount--;
	}
    }

    final void skipWhiteSpace() {
	int c;

	while ((c = nextChar()) == ' ' || c == '\n' || c == '\t')
	    ;
	pushBack();
    }

    final boolean isWhiteSpace(int c) {
	return (c == ' ' || c == '\t' || c == '\n');
    }

    void skipUntil(int what) {
	int c;

	while ((c = nextChar()) != what) {
	    if (c == -1)
		break;
	}
	pushBack();
    }

    final boolean isLetter(int c) {
	return (c >= 'A' && c <= 'Z' ||
		c >= 'a' && c <= 'z');
    }

    final boolean isDigit(int c) {
	return (c >= '0' && c <= '9');
    }

    final boolean isTagChar(int c) {
	return (isLetter(c) || isDigit(c) || c == '.' || c == '-');
    }

    int parseCharacter() {
	int val = 0;
	int c;

	insistThat(nextChar() == '#');
	while (isDigit(c = nextChar())) {
	    val = val * 10 + c - '0';
	}
	if (c != ';') {
	    pushBack();
	}
	return val;
    }

    int parseEntity() {
	Character   ch;
	String	    name;
	int	    start = inputSeek;
	int	    c;

	if (!isLetter(peekChar())) {
	    return '&';
	}
	while ((c = nextChar()) != -1 && isLetter(c))
	    ;

	int lastc = c;
	name = new String(input, 0, start, inputSeek - start - 1);
	if ((ch = (Character) ampChars.get(name)) != null) {
	    c = ch.charValue();
	} else {
	    name = name.toLowerCase();
	    if ((ch = (Character) ampChars.get(name)) != null) {
		c = ch.charValue();
	    } else {
		warning("Warning: failed to find: &" + name); 
		c = -1;
	    }
	}
	if (lastc != ';') {
	    pushBack();
	}
	return c;
    }

    String makeLowerCaseString(byte str[], int start, int len) {
	return new String(str, 0, start, len).toLowerCase();
    }

    String parseTagName() {
	int c;
	int start = inputSeek;

	if ((c = nextChar()) == '!') {
	    /* This is a comment, as far as mosaic is concerned,
	       so we just eat up all the characters until the '>',
	       and return 0 (which means ignore). */
	    skipUntil('>');
	    return "<comment>";
	} else if (!isTagChar(c)) {
	    pushBack();	    /* c */
	    return null;
	}
	    
	/* Read tag name until end.  Don't complain about illegal
	   tag names, because mosaic doesn't. */
	while ((c = nextChar()) != -1 && isTagChar(c))
	    ;
	pushBack();	    /* push back the delimitor */
	if (inputSeek - start == 0) {
	    return null;
	}
	return makeLowerCaseString(input, start, inputSeek - start);
    }	

    final void warning(String msg) {
	if (debug) {
	    System.out.print("Warning (line " + lineCount + "): ");
	    System.out.println(msg);
	}
    }

    private String  delim = "> =";
    void parseAttributes(TagRef ref) {
	int	start;
	int	c;
	String	name;
	String	value;

	while (true) {
	    skipWhiteSpace();
	    start = inputSeek;
	    while ((c = nextChar()) != -1 && c != '>' && c != ' '
		   && c != '=')
		;
	    pushBack();
	    if (start == inputSeek) {
		name = null;
	    } else {
		name = makeLowerCaseString(input, start, inputSeek - start);
	    }
	    skipWhiteSpace();
	    if (name == null) {
		return;
	    }
	    if (peekChar() == '=') {
		nextChar();
		skipWhiteSpace();
		c = nextChar();

		int	match, cnt;

		if (c != '\'' && c != '"') {
		    pushBack();
		    start = inputSeek;
		    cnt = 0;
		    while ((c = nextChar()) != -1 && c != ' ' && c != '\t' && c != '\n' && c != '>')
			;
		} else {
		    match = c;
		    start = inputSeek;
		    cnt = 0;
		    while ((c = nextChar()) != -1 && c != match && c != '>')
			;
		}
		if (c == -1) {
		    warning("unexpected EOF");
		    return;
		}
		value = new String(input, 0, start, inputSeek - start - 1);
		if (c == '>') {
		    pushBack();
		}
	    } else {
		value = new String("true");
	    }
//	    System.out.println(name + ":" + value + ", line = " + lineCount);
	    if (ref != null) {
		ref.addAttribute(name, value);
	    }
	}
    }

    void insistThat(boolean expr) {
	if (!expr) {
	    throw new Exception("assertion failed: " + lineCount);
	}
    }

    Stack   tagStack = new Stack();

    boolean handleTag(Tag tag, boolean isEnd, int offset) {
	if (isEnd) {
	    Tag	tos;
	    try {
		tos = (Tag) tagStack.peek();

		if (tos != tag) {
		    if (tagStack.search(tag) == -1) {
			warning("Ignoring tag: </" + tag.name + ">");
			return false;	    /* ignore this tag completely */
		    } else {
			while (true) {
			    Tag t = (Tag) tagStack.pop();

			    if (t != tag) {
				warning("Missing </" + t.name + "> just noticed by </" + tag.name + ">");
				html.endTag(t, offset);
			    } else {
				break;
			    }
			}
		    }
		} else {
		    if (tag.id == Tag.PRE) {
			preFormatted = false;
		    }
		    tagStack.pop();
		}
	    } catch (EmptyStackException e) {
		warning("Ignoring tag: </" + tag.name + ">");
		return false;
	    }
	} else if (tag.hasEndTag) {
	    if (tag.id == Tag.PRE) {
		preFormatted = true;
	    }
	    tagStack.push(tag);
	}
	return true;
    }

    boolean	preFormatted = false;
    static private Tag	FORMtag = Tag.lookup("form");
    static private Tag	INPUTtag = Tag.lookup("input");
    static private Tag	HRtag = Tag.lookup("hr");

    void parse() {
	int	textIndex = 0;
	int	c;
	Tag	lastTag = null;
	boolean	wasWhite = false;
	boolean	textSinceLastBreak = false;

mainloop:
	while ((c = nextChar()) != -1) {

	    /* In this switch statement, a break means we've read
	       a character suitable for inserting into the document.
	       Continue means we've read some sort of markup. */

	    switch (c) {
	    case '<': {
		boolean	    isEnd;
		String	    tagName;
		TagRef	    ref;
		Tag	    newTag;

		if ((c = peekChar()) == '/') {
		    nextChar();			/* read it */
		    isEnd = true;
		} else {
		    isEnd = false;
		}
		tagName = parseTagName();
		if (tagName == null) {		/* wasn't a real tag */
		    c = '<';
		    break;
		}
		newTag = Tag.lookup(tagName);
		ref = null;
		if (handleTag(newTag, isEnd, textIndex)) {
		    /* Html spec says newline right before and
		       just after a tag is markup and should
		       be deleted. */
		    if (isEnd && newTag.hasEndTag
			&& lastTag == null  /* so we only eat one newline */
			/* && newTag.breaks */) {
			if ((textIndex > 0) && (input[textIndex-1] == '\n')) {
			    textIndex--;
			}
		    }
		    if (!isEnd) {
			ref = html.startTag(newTag, textIndex);
		    } else {
			ref = html.endTag(newTag, textIndex);
		    }
		    lastTag = newTag;
		    if (newTag.breaks) {
			textSinceLastBreak = false;
		    }
		}

		if (!isEnd) {
		    parseAttributes(ref);
		} else {
		    skipUntil('>');
		}
		if (nextChar() != '>') {
		    warning("Malformed tag: " + lastTag);
		}
		if ((ref != null) && (ref.tag.id == Tag.ISINDEX)) {
		    String prompt = ref.getAttribute("prompt");
		    int	   oldTextIndex = textIndex;

		    TagRef fref = html.startTag(FORMtag, textIndex);
		    String action = ref.getAttribute("action");
		    if (action != null) {
			fref.addAttribute("action", action);
		    }
		    html.startTag(HRtag, textIndex);

		    if (prompt == null) {
			// At this point since the prompt wasn't in
			// the input buffer we need to handle the case
			// where we don't have enough space in the
			// buffer so we need to make a larger buffer
			// copy the first part of the input buffer,
			// then the prompt and the rest of the input
			// buffer into it.
			prompt = "This is a searchable index.  Enter search keywords: ";
			output = new byte[input.length + prompt.length() + 1];
			inputLength += prompt.length() + 1;
			System.arraycopy(input, 0,
					 output, 0, textIndex);

			for (int i = 0 ; i < prompt.length() ; i++) {
			    output[textIndex++] = (byte)prompt.charAt(i);
			}

			System.arraycopy(input,
					 oldTextIndex - 1,
					 output, textIndex,
					 input.length - oldTextIndex);
			inputSeek += prompt.length() + 1;
			input = output;
		    } else {
			for (int i = 0 ; i < prompt.length() ; i++) {
			    input[textIndex++] = (byte)prompt.charAt(i);
			}
		    }

		    fref = html.startTag(INPUTtag, textIndex);
		    fref.addAttribute("name", "isindex");
		    html.endTag(HRtag, textIndex);
		    html.endTag(FORMtag, textIndex);
		}
		if (debug) {
		    if (ref != null) {
			System.out.println("Line " + lineCount + ": " + ref.toExternalForm());
		    }
		}
		/* Html spec says newline right before and
		   just after a tag is markup and should
		   be deleted. */
		if (!isEnd && newTag.hasEndTag && newTag.breaks
		    && peekChar() == '\n') {
		    nextChar();
		}

		if (lastTag != null && lastTag.id == Tag.PLAINTEXT) {
		    textIndex = inputLength - inputSeek;
		    System.arraycopy(input, inputSeek, input, 0, textIndex);
		    break mainloop;
		}
		wasWhite = false;
		continue;
	    }

	    case '&':
		if (peekChar() == '#') {
		    c = parseCharacter();
		} else {
		    if ((c = parseEntity()) == -1) {
			continue;
		    }
		}
		wasWhite = false;
		break;

	    case '\n':
	    case '\t':
		if (!preFormatted) {
		    c = ' ';
		}
		/* falls into ... */

	    case ' ':
		if (!preFormatted) {
		    if (!textSinceLastBreak
			/* (lastTag != null && lastTag.breaks) */
			|| wasWhite) {
			continue;
		    }
		}
		wasWhite = true;
		break;

	    default:
		wasWhite = false;
		break;
	    }
	    lastTag = null;
	    textSinceLastBreak = true;
	    //html.addCharacter(c);
	    input[textIndex++] = (byte)c;
	}

	if (tagStack.size() != 0) {
	    String  error = "Missing ";
	    int	    i = tagStack.size();
	    Tag tag;

	    while (--i > 1) {
		tag = (Tag) tagStack.pop();
		html.endTag(tag, textIndex);
		error = error + "</" + tag.name + ">, ";
	    }
	    tag = (Tag) tagStack.pop();
	    html.endTag(tag, textIndex);
	    error = error + "</" + tag.name + ">";

	    warning(error + " at end of document.\n");
	}
	tagStack = null;

	if (input.length != textIndex) {
	    byte newinput[] = new byte[textIndex];
	    System.arraycopy(input, 0, newinput, 0, textIndex);
	    input = newinput;
	}

	html.setText(input);
    }

    public Parser(InputStream is, Document html) {
	readInput(is);
	this.html = html;

	try {
	    parse();
	} catch (Exception e) {
	    warning("Caught exception while parsing\n");
	    e.printStackTrace();
	}
    }

    static public void main(String args[]) {
	URL url = new URL(null, args[0]);
	Parser p = new Parser(url.openStream(), new Document());
    }
}
