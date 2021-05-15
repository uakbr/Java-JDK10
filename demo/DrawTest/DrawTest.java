/*
 * @(#)DrawTest.java	1.14 95/09/01 Sami Shaio
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */
import java.awt.*;
import java.applet.*;

import java.util.Vector;

public class DrawTest extends Applet {
    public void init() {
	setLayout(new BorderLayout());
	DrawPanel dp = new DrawPanel();
	add("Center", dp);
	add("South",new DrawControls(dp));
    }

    public boolean handleEvent(Event e) {
	switch (e.id) {
	  case Event.WINDOW_DESTROY:
	    System.exit(0);
	    return true;
	  default:
	    return false;
	}
    }

    public static void main(String args[]) {
	Frame f = new Frame("DrawTest");
	DrawTest drawTest = new DrawTest();
	drawTest.init();
	drawTest.start();

	f.add("Center", drawTest);
	f.resize(300, 300);
	f.show();
    }
}

class DrawPanel extends Panel {
    public static final int LINES = 0;
    public static final int POINTS = 1;
    int	   mode = LINES;
    Vector lines = new Vector();
    Vector colors = new Vector();
    int x1,y1;
    int x2,y2;
    int xl, yl;

    public DrawPanel() {
	setBackground(Color.white);
    }

    public void setDrawMode(int mode) {
	switch (mode) {
	  case LINES:
	  case POINTS:
	    this.mode = mode;
	    break;
	  default:
	    throw new IllegalArgumentException();
	}
    }

    public boolean handleEvent(Event e) {
	switch (e.id) {
	  case Event.MOUSE_DOWN:
	    switch (mode) {
	      case LINES:
		x1 = e.x;
		y1 = e.y;
		x2 = -1;
		break;
	      case POINTS:
	      default:
		colors.addElement(getForeground());
		lines.addElement(new Rectangle(e.x, e.y, -1, -1));
		x1 = e.x;
		y1 = e.y;
		repaint();
		break;
	    }
	    return true;
	  case Event.MOUSE_UP:
	    switch (mode) {
	      case LINES:
		colors.addElement(getForeground());
		lines.addElement(new Rectangle(x1, y1, e.x, e.y));
		x2 = xl = -1;
		break;
	      case POINTS:
	      default:
		break;
	    }
	    repaint();
	    return true;
	  case Event.MOUSE_DRAG:
	    switch (mode) {
	      case LINES:
		xl = x2;
		yl = y2;
		x2 = e.x;
		y2 = e.y;
		break;
	      case POINTS:
	      default:
		colors.addElement(getForeground());
		lines.addElement(new Rectangle(x1, y1, e.x, e.y));
		x1 = e.x;
		y1 = e.y;
		break;
	    }
	    repaint();
	    return true;
	  case Event.WINDOW_DESTROY:
	    System.exit(0);
	    return true;
	  default:
	    return false;
	}
    }

    public void paint(Graphics g) {
	int np = lines.size();

	/* draw the current lines */
	g.setColor(getForeground());
	g.setPaintMode();
	for (int i=0; i < np; i++) {
	    Rectangle p = (Rectangle)lines.elementAt(i);
	    g.setColor((Color)colors.elementAt(i));
	    if (p.width != -1) {
		g.drawLine(p.x, p.y, p.width, p.height);
	    } else {
		g.drawLine(p.x, p.y, p.x, p.y);
	    }
	}
	if (mode == LINES) {
	    g.setXORMode(getBackground());
	    if (xl != -1) {
		/* erase the last line. */
		g.drawLine(x1, y1, xl, yl);
	    }
	    g.setColor(getForeground());
	    g.setPaintMode();
	    if (x2 != -1) {
		g.drawLine(x1, y1, x2, y2);
	    }
	}
    }
}


class DrawControls extends Panel {
    DrawPanel target;

    public DrawControls(DrawPanel target) {
	this.target = target;
	setLayout(new FlowLayout());
	setBackground(Color.lightGray);
	target.setForeground(Color.red);
	CheckboxGroup group = new CheckboxGroup();
	Checkbox b;
	add(b = new Checkbox(null, group, false));
	b.setBackground(Color.red);
	add(b = new Checkbox(null, group, false));
	b.setBackground(Color.green);
	add(b = new Checkbox(null, group, false));
	b.setBackground(Color.blue);
	add(b = new Checkbox(null, group, false));
	b.setBackground(Color.pink);
	add(b = new Checkbox(null, group, false));
	b.setBackground(Color.orange);
	add(b = new Checkbox(null, group, true));
	b.setBackground(Color.black);
	target.setForeground(b.getForeground());
	Choice shapes = new Choice();
	shapes.addItem("Lines");
	shapes.addItem("Points");
	shapes.setBackground(Color.lightGray);
	add(shapes);
    }

    public void paint(Graphics g) {
	Rectangle r = bounds();

	g.setColor(Color.lightGray);
	g.draw3DRect(0, 0, r.width, r.height, false);
    }

    public boolean action(Event e, Object arg) {
	if (e.target instanceof Checkbox) {
	    target.setForeground(((Component)e.target).getBackground());
	} else if (e.target instanceof Choice) {
	    String choice = (String)arg;

	    if (choice.equals("Lines")) {
		target.setDrawMode(DrawPanel.LINES);
	    } else if (choice.equals("Points")) {
		target.setDrawMode(DrawPanel.POINTS);
	    }
	}
	return true;
    }
}
	


    
