/*
 * @(#)StreamTokenizer.java	1.5 95/02/21
 *
 * Copyright (c) 1995 Sun Microsystems, Inc.  All Rights reserved
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file copyright.html
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package java.io;
import java.io.InputStream;

/**
 * A class to turn an input stream into a stream of tokens.
 * There are a number of methods that define the lexical
 * syntax of tokens.
 * @author  James Gosling
 */
public
class StreamTokenizer {
    private InputStream input;
    private char buf[];
    private int peekc = ' ';
    /** The line number of the last token read */
    public int lineno = 1;


    /** Set to true if end-of-lines are significant (TT_EOL will
	be returned by nexttoken).  If false, they will be treated
	as whitespace */
    public boolean eolIsSignificant = false;
    /** Set to true to recognize C++ style // comments */
    public boolean slashSlashComments = false;
    /** Set to true to recognize C style /* comments */
    public boolean slashStarComments = false;

    /** If true, '#' starts a single line comment.  This is
	here solely for compatibility with 1.0a1.  Use
	commentChar('#') instead. */
    public boolean hashComments = false;

    private byte ctype[] = new byte[256];
    private static final byte CT_WHITESPACE = 1;
    private static final byte CT_DIGIT = 2;
    private static final byte CT_ALPHA = 4;
    private static final byte CT_QUOTE = 8;
    private static final byte CT_COMMENT = 16;

    /** The type of the last token returned.  It's value will either
	be one of the following TT_* constants, or a single
	character.  For example, if '+' is encountered and is
	not a valid word character, ttype will be '+' */
    public int ttype;
    /** End of file token */
    public static final int TT_EOF = -1;
    /** End of line token */
    public static final int TT_EOL = '\n';
    /** Number token.  The value is in nval */
    public static final int TT_NUMBER = -2;
    /** Word token.  The value is in sval */
    public static final int TT_WORD = -3;
    public String sval;
    public double nval;

    /** Create a stream tokenizer that parses the given input stream.
	By default, it recognizes numbers, all the alphabetics are
	valid word characters, and strings quoted with single and
	double quote. */
    public StreamTokenizer (InputStream I) {
	input = I;
	buf = new char[20];
	byte ct[] = ctype;
	int i;
	wordChars('a', 'z');
	wordChars('A', 'Z');
	wordChars(128 + 32, 255);
	whitespaceChars(0, ' ');
	commentChar('/');
	quoteChar('"');
	quoteChar('\'');
	parseNumbers();
    }

    /** Reset the syntax table so that all characters are special */
    public void resetSyntax() {
	for (int i = ctype.length; --i >= 0;)
	    ctype[i] = 0;
    }

    /** Specify that characters in this range are word characters */
    public void wordChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi > ctype.length)
	    hi = ctype.length;
	while (low <= hi)
	    ctype[low++] |= CT_ALPHA;
    }

    /** Specify that characters in this range are whitespace characters */
    public void whitespaceChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi > ctype.length)
	    hi = ctype.length;
	while (low <= hi)
	    ctype[low++] = CT_WHITESPACE;
    }

    /** Specify that characters in this range are 'ordinary': it removes any
	significance as a word, comment, string, whitespace or number
	character.  When encountered by the parser, they return a ttype
	equal to the character. */
    public void ordinaryChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi > ctype.length)
	    hi = ctype.length;
	while (low <= hi)
	    ctype[low++] = 0;
    }

    /** Specify that this character is  'ordinary': it removes any
	significance as a word, comment, string, whitespace or number
	character.  When encountered by the parser, it returns a ttype
	equal to the character. */
    public void ordinaryChar(int ch) {
	ctype[ch] = 0;
    }

    /** Specify that this character starts a single line comment. */
    public void commentChar(int ch) {
	ctype[ch] = CT_COMMENT;
    }

    /** Specify that matching pairs of this character delimit string
	constants.  When a string constant is recognized ttype will be
	the character that delimits the string, and sval will have
	the body of the string. */
    public void quoteChar(int ch) {
	ctype[ch] = CT_QUOTE;
    }

    /** Specify that numbers should be parsed.  It accepts double precision
	floating point numbers and returns a ttype of TT_NUMBER with the
	value in nval. */
    public void parseNumbers() {
	for (int i = '0'; i <= '9'; i++)
	    ctype[i] |= CT_DIGIT;
	ctype['.'] |= CT_DIGIT;
	ctype['-'] |= CT_DIGIT;
    }

    /** Parse a token from the input stream.  The return value is
	the same as the value of ttype.  Typical clients of this
	class first set up the syntax tables and then sit in a loop
	calling nextToken to parse successive tokens until TT_EOF
	is returned. */
    public int nextToken() {
	InputStream is = input;
	byte ct[] = ctype;
	int c = peekc;
	sval = null;

        // XXX: this 'if' statement is here for alpha1.0 compatibility
        // only and should be removed post-alpha2.
        if (hashComments) {
            commentChar('#');
        }

	if (c < 0)
	    return ttype = TT_EOF;
	int ctype = c < 256 ? ct[c] : CT_ALPHA;
	while ((ctype & CT_WHITESPACE) != 0) {
	    if (c == '\r') {
		lineno++;
		c = is.read();
		if (c == '\n')
		    c = is.read();
		if (eolIsSignificant) {
		    peekc = c;
		    return ttype = TT_EOL;
		}
	    } else {
		if (c == '\n') {
		    lineno++;
		    if (eolIsSignificant) {
			peekc = ' ';
			return ttype = TT_EOL;
		    }
		}
		c = is.read();
	    }
	    if (c < 0)
		return ttype = TT_EOF;
	    ctype = c < 256 ? ct[c] : CT_ALPHA;
	}
	if ((ctype & CT_DIGIT) != 0) {
	    boolean neg = false;
	    if (c == '-') {
		c = is.read();
		if (c != '.' && (c < '0' || c > '9')) {
		    peekc = c;
		    return ttype = '-';
		}
		neg = true;
	    }
	    double v = 0;
	    int decexp = 0;
	    int seendot = 0;
	    while (true) {
		if (c == '.' && seendot == 0)
		    seendot = 1;
		else if ('0' <= c && c <= '9') {
		    v = v * 10 + (c - '0');
		    decexp += seendot;
		} else
		    break;
		c = is.read();
	    }
	    peekc = c;
	    if (decexp != 0) {
		double denom = 10;
		decexp--;
		while (decexp > 0) {
		    denom *= 10;
		    decexp--;
		}
		/* do one division of a likely-to-be-more-accurate number */
		v = v / denom;
	    }
	    nval = neg ? -v : v;
	    return ttype = TT_NUMBER;
	}
	if ((ctype & CT_ALPHA) != 0) {
	    int i = 0;
	    do {
		if (i >= buf.length) {
		    char nb[] = new char[buf.length * 2];
		    System.arraycopy(buf, 0, nb, 0, buf.length);
		    buf = nb;
		}
		buf[i++] = (char) c;
		c = is.read();
		ctype = c < 0 ? CT_WHITESPACE : c < 256 ? ct[c] : CT_ALPHA;
	    } while ((ctype & (CT_ALPHA | CT_DIGIT)) != 0);
	    peekc = c;
	    sval = String.copyValueOf(buf, 0, i);
	    return ttype = TT_WORD;
	}
	if ((ctype & CT_COMMENT) != 0) {
	    while ((c = is.read()) != '\n' && c != '\r' && c >= 0);
	    peekc = c;
	    return nextToken();
	}
	if ((ctype & CT_QUOTE) != 0) {
	    ttype = c;
	    int i = 0;
	    while ((c = is.read()) >= 0 && c != ttype && c != '\n' && c != '\r') {
		if (c == '\\')
		    switch (c = is.read()) {
		      case '\n':
			c = '\n';
			break;
		      case '\t':
			c = '\t';
			break;
		      case '\b':
			c = '\b';
			break;
		      case '\r':
			c = '\r';
			break;
		      case '0':
			c = 0;
			break;
		    }
		if (i >= buf.length) {
		    char nb[] = new char[buf.length * 2];
		    System.arraycopy(buf, 0, nb, 0, buf.length);
		    buf = nb;
		}
		buf[i++] = (char) c;
	    }
	    peekc = ' ';
	    sval = String.copyValueOf(buf, 0, i);
	    return ttype;
	}
	if (c == '/' && (slashSlashComments || slashStarComments)) {
	    c = is.read();
	    if (c == '*' && slashStarComments) {
		int prevc = 0;
		while ((c = is.read()) != '/' || prevc != '*') {
		    if (c == '\n')
			lineno++;
		    if (c < 0)
			return ttype = TT_EOF;
		    prevc = c;
		}
		peekc = ' ';
		return nextToken();
	    } else if (c == '/' && slashSlashComments) {
		while ((c = is.read()) != '\n' && c != '\r' && c >= 0);
		peekc = c;
		return nextToken();
	    } else {
		peekc = c;
		return ttype = '/';
	    }
	}
	peekc = ' ';
	return ttype = c;
    }

    public String toString() {
	String ret;
	switch (ttype) {
	  case TT_EOF:
	    ret = "EOF";
	    break;
	  case TT_EOL:
	    ret = "EOL";
	    break;
	  case TT_WORD:
	    ret = sval;
	    break;
	  case TT_NUMBER:
	    ret = "n=" + nval;
	    break;
	  default:{
		char s[] = new char[3];
		s[0] = s[2] = '\'';
		s[1] = (char) ttype;
		ret = new String(s);
		break;
	    }
	}
	return "Token[" + ret + "], line " + lineno;
    }
}
