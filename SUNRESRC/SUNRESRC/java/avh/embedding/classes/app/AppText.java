package app;

import awt.*;

/**
 * A text input item. The applet's action
 * method is called when the user types return.
 *
 * @author Arthur van Hoff
 */
public
class AppText extends AppComponent {
    public String value;
    public Font font;
    public int selStart, selEnd;
    int selPos, upTm;
    
    public AppText(AppletPanel app, String value) {
	super(app);
	this.value = value;
	font = app.getFont("Helvetica", 14);
	if (app.focus == null) {
	    app.setFocus(this);
	}
	selStart = selEnd = value.length();
    }
    public void setValue(String value) {
	this.value = value;
	select(value.length(), value.length());
    }
    public void select(int start, int end) {
	selStart = Math.max(0, Math.min(value.length(), start));
	selEnd = Math.max(0, Math.min(value.length(), end));
	app.touched = true;;
    }
    public void paint(Graphics g) {
	int yoff = ((h + font.height) / 2) - font.descent;
	g.setForeground(Color.lightGray);
	g.fillRect(0, 0, w, h);
	g.setForeground(Color.gray);
	g.drawLine(0, yoff+1, w, yoff+1);
	g.setFont(font);
	if (app.focus == this) {
	    if (selStart == selEnd) {
		int cx = font.stringWidth(value.substring(0, selStart));
		g.setForeground(Color.black);
		g.drawLine(cx - 2, yoff + 4, cx, yoff);
		g.drawLine(cx, yoff, cx + 2, yoff + 4);
	    } else {
		g.setForeground(Color.gray);
		int cx0 = font.stringWidth(value.substring(0, selStart));
		int cx1 = font.stringWidth(value.substring(0, selEnd));
		g.fillRect(cx0, 0, cx1 - cx0, h);
	    }
	}
	g.setForeground(Color.black);
	g.drawString(value, 0, yoff);
    }
    int x2pos(int x) {
	for (int i = 0 ; i < value.length()-1 ; i++) {
	    int cw = font.stringWidth(value.substring(i, i+1));
	    if (x < cw/2) {
		return i;
	    }
	    x -= cw;
	}
	return value.length();
    }
    public void mouseDown(int x, int y) {
	app.setFocus(this);
	selPos = x2pos(x);
	select(selPos, selPos);
    }
    public void mouseDrag(int x, int y) {
	int pos = x2pos(x);
	if (pos < selPos) {
	    select(pos, selPos);
	} else {
	    select(selPos, pos);
	}
    }
    public void mouseUp(int x, int y) {
	if (System.nowMillis() < upTm + 250) {
	    select(0, value.length());
	}
	upTm = System.nowMillis();
    }
    public void keyDown(int ch) {
	switch (ch) {
	  case 'f' & 0x1f:
	    select(selStart+1, selStart+1);
	    break;
	  case 'b' & 0x1f:
	    select(selStart-1, selStart-1);
	    break;
	  case 'e' & 0x1f:
	    select(value.length(), value.length());
	    break;
	  case 'a' & 0x1f:
	    select(0, 0);
	    app.touched = true;;
	    break;

	  case 'h' & 0x1f:
	    if ((selStart > 0) && (selStart == selEnd)) {
		selStart--;
	    }
	    if (selStart < selEnd) {
		value = value.substring(0, selStart) + value.substring(selEnd);
		select(selStart, selStart);
		break;
	    }
	    break;

	  case '\n':
	    app.action(this);
	    break;

	  case '\t':
	    // next field
	    break;

	  default:
	    if (ch >= ' ') {
		if (selStart < selEnd) {
		    value = value.substring(0, selStart) + value.substring(selEnd);
		    selEnd = selStart;
		}
		char str[] = {(char)ch};
		value = value.substring(0, selStart) + new String(str) + value.substring(selEnd);
		select(selStart+1, selStart+1);
	    }
	}
    }
}
