/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)TextViewLine.java	1.13 94/07/24 Feb 1994
 *
 *	Arthur van Hoff, Nov 1993
 *	Arthur van Hoff, March 1994
 */

package edit;

import awt.Graphics;
import awt.Font;

public class TextViewLine {
    protected TextViewer owner;
    protected int start;
    protected int end;
    protected int tail;
    protected int y;
    protected int oldy;
    protected int height;
    protected boolean updated;
    protected int indent;
    protected int width;
    protected int ascent;
    protected float extra;

    /**
     * Create and format a line of text given a starting
     * offset and a width.
     */
    protected TextViewLine(TextViewer pOwner, int pos, int viewwidth) {
	owner = pOwner;
	updated = true;
	
	Text text = owner.text;
	int stylei = text.indexStyle(pos);
	TextRuler ruler = text.ruler[text.indexRuler(pos)];
	TextStyle style = text.style[stylei++];
	char str[] = text.data;
	Font info = style.font;
	int chwidth[] = info.widths;
	int endPos, x, maxx, ch;
	int breakpos = 0, breakx = 0, breakspaces = 0, spaces = 0, descent;

	start = pos;
	ascent = Math.max(ruler.minAscent, info.ascent);
	descent = Math.max(ruler.minDescent, info.descent);
	
	x = indent = ((pos == 0) || (str[pos - 1] == '\n')) ? ruler.firstIndent : ruler.leftIndent;
	maxx = viewwidth - ruler.rightIndent;
	endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;

      line_break: {
	line_end: {

	    if (pos >= text.length) {
		break line_end;
	    }
	    switch (ruler.format) {
	      case TextRuler.FORMAT_NONE:
		while (true) {
		    breakx = x;
		    
		    switch (ch = str[pos++]) {
		      case '\t':
			x += ruler.tabWidth - ((x  - ruler.leftIndent) % ruler.tabWidth);
			break;
		      case '\n':
			break line_end;
		      default:
			x += chwidth[ch];
			break;
		    }

		    if (x > maxx) {
			breakpos = (pos - 1 == start) ? pos : pos - 1;
			break line_break;
		    }

		    if (pos >= endPos) {
			if (pos >= text.length) {
			    break line_end;
			}
			style = text.style[stylei++];
			info = style.font;
			endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
			chwidth = info.widths;
			ascent = Math.max(info.ascent, ascent);
			descent = Math.max(info.descent, descent);
		    }
		}
		
	      case TextRuler.FORMAT_LEFT:
		while (true) {
		    switch (ch = str[pos++]) {
		      case ' ':
			x += chwidth[ch];
			breakpos = pos;
			breakx = x;
			break;
		      case '\t':
			breakpos = pos;
			breakx = x;
			x += ruler.tabWidth - ((x  - ruler.leftIndent) % ruler.tabWidth);
			break;
		      case '\n':
			break line_end;
		      default:
			x += chwidth[ch];
			break;
		    }

		    if (x > maxx) {
			if (breakpos <= start) {
			    break line_end;
			}
			break line_break;
		    }

		    if (pos >= endPos) {
			if (pos >= text.length) {
			    break line_end;
			}
			style = text.style[stylei++];
			info = style.font;
			endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
			chwidth = info.widths;
			ascent = Math.max(info.ascent, ascent);
			descent = Math.max(info.descent, descent);
		    }
		}
		
	      default:
	      fill_line_next:
		while (true) {
		    switch (ch = str[pos++]) {
		      case ' ':
		      case '\t':
			spaces++;
			x += chwidth[ch];
			if (pos >= endPos) {
			    if (pos >= text.length) {
				break line_end;
			    }
			    style = text.style[stylei++];
			    info = style.font;
			    endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
			    chwidth = info.widths;
			    ascent = Math.max(info.ascent, ascent);
			    descent = Math.max(info.descent, descent);
			}
			continue fill_line_next;
		      case '\n':
			break line_end;
		      default:
			breakx = x;
			breakpos = pos - 1;
			break;
		    }

		    while (true) {
			x += chwidth[ch];
			if (x > maxx) {
			    if (breakpos <= start) {
				break line_end;
			    }
			    break line_break;
			}
			if (pos >= endPos) {
			    if (pos >= text.length) {
				break line_end;
			    }
			    style = text.style[stylei++];
			    info = style.font;
			    endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
			    chwidth = info.widths;
			    ascent = Math.max(info.ascent, ascent);
			    descent = Math.max(info.descent, descent);
			}

			switch (ch = str[pos++]) {
			  case ' ':
			  case '\t':
			    breakspaces = spaces;
			    spaces++;
			    x += chwidth[ch];
			    if (pos >= endPos) {
				if (pos >= text.length) {
				    break line_end;
				}
				style = text.style[stylei++];
				info = style.font;
				endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
				chwidth = info.widths;
				ascent = Math.max(info.ascent, ascent);
				descent = Math.max(info.descent, descent);
			    }
			    continue fill_line_next;
			  case '\n':
			    break line_end;
			}
		    }
		}
	    }
	  }

	  // line_break:
	  breakpos = pos;
	  breakx = x;
	  breakspaces = 0;
        }

        // line_end:
	height = ascent + descent;
	end = breakpos;
	tail = pos;
	width = breakx - indent;

	switch (ruler.format) {
	  case TextRuler.FORMAT_RIGHT:
	    indent = viewwidth - width - ruler.rightIndent;
	    break;
	  case TextRuler.FORMAT_CENTER:
	    indent = ((viewwidth - width) + (ruler.rightIndent - ruler.leftIndent)) / 2;
	    break;
	  case TextRuler.FORMAT_FILL:
	    if (breakspaces > 0) {
		extra = (float)(maxx - breakx) / breakspaces;
		width = maxx - indent;
	    }
	    break;
	}
    }

    /**
     * Paint this line of text on the screen.
     */
    protected void paint(Graphics g, int dx, int dy) {
	Text text = owner.text;
	int pos = start, stylei = text.indexStyle(pos);
	TextRuler ruler = text.ruler[text.indexRuler(pos)];
	TextStyle style = text.style[stylei++];
	char str[] = text.data;
	int startPos;
	int ix = dx + indent;
	int iy = dy + ascent;
	int endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
	int lnEndPos = end;
	
	switch (ruler.format) {
	  case TextRuler.FORMAT_FILL:
	    if (extra > 0.0) {
		float fx = ix;
		while (pos < lnEndPos) {
		    g.setFont(style.font);
		    g.setForeground(style.color);
		    startPos = pos;
		    endPos = Math.min(endPos, lnEndPos);

		    for (; pos < endPos ; pos++)
			switch (str[pos]) {
			  case ' ':
			  case '\t':
			    g.drawChars(str, startPos, pos + 1 - startPos, (int)fx, iy);
			    fx += style.font.stringWidth(new String(str, startPos, pos + 1 - startPos)) + extra;
			    startPos = pos + 1;
			    break;
			}
		    if (pos >= lnEndPos) {
			g.drawString(new String (str, startPos, pos - startPos), (int)fx, iy);
			break;
		    }
		    g.drawChars(str, startPos, pos - startPos, (int)fx, iy);
		    fx += style.font.stringWidth(new String(str, startPos, pos - startPos));
		    startPos = pos;
		    style = text.style[stylei++];
		    endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
		}
		break;
	    }
	  case TextRuler.FORMAT_CENTER:
	  case TextRuler.FORMAT_RIGHT:
	    while (endPos < lnEndPos) {
		g.setFont(style.font);
		g.setForeground(style.color);
		g.drawChars(str, pos, endPos - pos, ix, iy);
		ix += style.font.stringWidth(new String(str, pos, endPos - pos));
		pos = endPos;
		style = text.style[stylei++];
		endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
	    }

	    g.setFont(style.font);
	    g.setForeground(style.color);
	    g.drawString(new String(str, pos, lnEndPos - pos), ix, iy);
	    break;

	  default:
	    while (pos < lnEndPos) {
		g.setFont(style.font);
		g.setForeground(style.color);
		startPos = pos;
		endPos = Math.min(endPos, lnEndPos);

		for (; pos < endPos ; pos++) {
		    if (str[pos] == '\t') {
			g.drawChars(str, startPos, pos - startPos, ix, iy);
			ix += style.font.stringWidth(new String(str, startPos, pos - startPos));
//			g.drawString(">", ix, iy);
			ix += ruler.tabWidth - ((ix  - ruler.leftIndent) % ruler.tabWidth);
			startPos = pos + 1;
		    }
		}
		if (pos >= lnEndPos) {
		    g.drawString(new String(str, startPos, pos - startPos), ix, iy);
		    break;
		}
		g.drawChars(str, startPos, pos - startPos, dx + ix, iy);
		ix += style.font.stringWidth(new String(str, startPos, pos - startPos));
		startPos = pos;
		style = text.style[stylei++];
		endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
	    }
	    break;
	}
    }
    
    /**
     * posToX returns the X position of an offset in the line.
     */
    protected int posToX(int thepos) {
	if (thepos <= start) {
	    return indent;
	}
	if (thepos >= end) {
	    return indent + width;
	}
	    
	Text text = owner.text;
	int pos = start, stylei = text.indexStyle(pos);
	TextRuler ruler = text.ruler[text.indexRuler(pos)];
	char str[] = text.data;
	int chwidth[];
	int x = indent, ch, endPos;

	switch (ruler.format) {
	  case TextRuler.FORMAT_FILL:
	    if (extra > 0.0) {
		float fx = x;
		while (pos < thepos) {
		    chwidth = text.style[stylei++].font.widths;
		    endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
		    if (endPos > thepos) {
			endPos = thepos;
		    }

		    while (pos < endPos)
			switch (ch = str[pos++]) {
			  case ' ':
			    fx += extra;
			  default:
			    fx += chwidth[ch];
			}
		}
		x = (int)fx;
		break;
	    }

	  case TextRuler.FORMAT_CENTER:
	  case TextRuler.FORMAT_RIGHT:
	    while (pos < thepos) {
		chwidth = text.style[stylei++].font.widths;
		endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
		if (endPos > thepos) {
		    endPos = thepos;
		}

		while (pos < endPos) {
		    x += chwidth[str[pos++]];
		}
	    }
	    break;

	  default:
	    while (pos < thepos) {
		chwidth = text.style[stylei++].font.widths;
		endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;
		if (endPos > thepos) {
		    endPos = thepos;
		}

		while (pos < endPos)
		    switch (ch = str[pos++]) {
		      case '\t':
			x += ruler.tabWidth - ((x  - ruler.leftIndent) % ruler.tabWidth);
			break;
		      default:
			x += chwidth[ch];
		    }
	    }
	    break;
	}
	return x;
    }
    
    /**
     * xToPos returns a text offset given an X position
     */
    protected int xToPos(int xpos) {
	Text text = owner.text;
	int pos = start, stylei = text.indexStyle(pos);
	TextRuler ruler = text.ruler[text.indexRuler(pos)];
	char str[] = text.data;
	int chwidth[];
	int x = indent, ch, chw = 0;
	int endPos, lnEndPos = end;

	if ((xpos < -25) && (pos > 0) && (str[pos - 1] == '\n')) {
	    return start - 1;
	}
	
	if (xpos <= indent) {
	    return start;
	}

	if (xpos >= indent + width) {
	    while (lnEndPos > pos)
		switch (str[--lnEndPos]) {
		  case ' ':
		  case '\t':
		  case '\n':
		    break;
		  default:
		    return lnEndPos + 1;
		}
	    return pos;
	}

	switch (ruler.format) {
	  case TextRuler.FORMAT_FILL:
	    if (extra > 0.0) {
		float fx = x, fchw = 0;
		while (pos < lnEndPos) {
		    chwidth = text.style[stylei++].font.widths;
		    endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;

		    for (; pos < endPos ; pos++, fx += fchw) {
			switch (ch = str[pos]) {
			  case ' ':
			    fchw = extra + chwidth[' '];
			    break;
			  default:
			    fchw = chwidth[ch];
			}
			if (xpos <= fx + fchw/2) {
			    return pos;
			}
		    }
		}
		break;
	    }

	  case TextRuler.FORMAT_CENTER:
	  case TextRuler.FORMAT_RIGHT:
	    while (pos < lnEndPos) {
		chwidth = text.style[stylei++].font.widths;
		endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;

		for (; pos < endPos ; pos++, x += chw) {
		    chw = chwidth[str[pos]];
		    if (xpos <= x + chw/2) {
			return pos;
		    }
		}
	    }
	    break;

	  default:
	    while (pos < lnEndPos) {
		chwidth = text.style[stylei++].font.widths;
		endPos = (stylei < text.nstyles) ? text.style[stylei].start : text.length;

		for (; pos < endPos ; pos++, x += chw) {
		    switch (ch = str[pos]) {
		      case '\t':
			chw = ruler.tabWidth - ((x  - ruler.leftIndent) % ruler.tabWidth);
			break;
		      default:
			chw = chwidth[ch];
		    }
		    if (xpos <= x + chw/2) {
			return pos;
		    }
		}
	    }
	    break;
	}
	return end;
    }

    /**
     * Create a copy of a text line, mark it as updated,
     * and set the oldy position.
     */
    public TextViewLine copy() {
	TextViewLine ln = (TextViewLine)clone();
	ln.oldy = y;
	ln.updated = (y < 0) || (y + height > owner.height);
	return ln;
    }

    /**
     * Print the TextViewLine
     */
    public void print() {
	System.out.println("TextViewLine(" + start + "," + end + "," +
			   tail + "," + y + "," + height +  "," + 
			   (updated ? "true" : "false") + ")");
    }
}
