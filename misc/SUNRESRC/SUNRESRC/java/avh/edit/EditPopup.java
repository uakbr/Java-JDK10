/*
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 */

package edit;

/*
 * A simple pop up window example
 * @author Arthur van Hoff
 */

import browser.Applet;
import awt.WServer;
import awt.Graphics;
import awt.Frame;
import awt.Color;
import awt.Font;
import awt.Menu;
import awt.MenuBar;
import awt.MenuItem;
import awt.Layoutable;

/**
 * Edit menu
 */
class EditMenu extends Menu {
    TextEditor ed;
    
    EditMenu(MenuBar mbar, TextEditor ed) {
	super("Edit", mbar);
	this.ed = ed;
	
	MenuItem item;
	item = new MenuItem("Top", this);
	item = new MenuItem("Bottom", this);
	item = new MenuItem("Cut", this);
	item.disable();
	item = new MenuItem("Copy", this);
	item.disable();
	item = new MenuItem("Paste", this);
	item.disable();
	item = new MenuItem("Clear", this);
	item = new MenuItem("Select All", this);
    }

    public void selected(int index) {
	switch (index) {
	  case 0:
	    ed.goTop();
	    break;
	  case 1:
	    ed.goBottom();
	    break;
	  case 5:
	    ed.clear();
	    break;
	  case 6:
	    ed.selectAll();
	    break;
	}
    }
}

class StyleMenu extends Menu {
    TextEditor ed;

    static TextStyle styles[];

    StyleMenu(MenuBar mbar, TextEditor ed) {
	super("Style", mbar);
	this.ed = ed;
	
	MenuItem item;
	item = new MenuItem("Courier", this);
	item = new MenuItem("Helvetica Plain", this);
	item = new MenuItem("Helvetica Bold", this);
	item = new MenuItem("Helvetica Italic", this);
	item = new MenuItem("Helvetica BoldItalic", this);
	item = new MenuItem("Helvetica Green", this);
	item = new MenuItem("TimesRoman Plain", this);
	item = new MenuItem("TimesRoman Bold", this);
	item = new MenuItem("TimesRoman Italic", this);
	item = new MenuItem("TimesRoman BoldItalic", this);
	item = new MenuItem("TimesRoman Red", this);
    }

    public void selected(int index) {
	ed.setStyle(styles[index]);
    }
}

class FormatMenu extends Menu {
    TextEditor ed;

    static TextRuler rulers[];

    FormatMenu(MenuBar mbar, TextEditor ed) {
	super("Format", mbar);
	this.ed = ed;
	
	MenuItem item;
	item = new MenuItem("None", this);
	item = new MenuItem("Left", this);
	item = new MenuItem("Center", this);
	item = new MenuItem("Right", this);
	item = new MenuItem("Fill", this);
    }

    public void selected(int index) {
	ed.setRuler(rulers[index]);
    }
}

class MyEditor extends Frame {
    MenuBar mb;
    TextEditor ed;
    Text txt;

    public MyEditor(String title, WServer ws, Frame parent) {
	super(ws, true, parent, 500, 300, Color.lightGray);
	resize(500, 300);
	TextStyle.ws = ws;
	setTitle(title);

	ed = new TextEditor(this, "Center", true);

	// fix some bugs
	boolean b = ed instanceof Runnable;
	b = !b;

	mb = new MenuBar(this);
	new EditMenu(mb, ed);
	new FormatMenu(mb, ed);
	new StyleMenu(mb, ed);
	map();
    }
}

/**
 * Popup class.
 */
class EditPopup extends Applet implements Runnable {
    static final String data = "\
On my first day in Edinburgh I was walking up to the mossy fortress \
that would contain my office.  Suddenly a completely silent vehicle \
lurched out at me.  I had to jump to avoid it (tricky, as I was \
carrying all my worldly possessions).  There was a long-haired old \
guy with wild eyes driving, looking a little like Manson does these \
days.  That was John Smith.  He was driving a \"milk float\", an \
electric-powered flatbed truck, that he got after developing a \
violent distaste for internal combustion engines.  They guy was a \
terror, having plowed into several cars that I know of just in that \
one parking lot.\n\n\
He also was known to suddenly get up during 1-on-1 \
talks with students, walk over to a cot hidden behind filing cabinets \
in his office, and go to sleep -- or if the student was female, to \
ask her to join him.  When he met his wife-to-be (a prof at Maryland, \
where he is now), she refused to live with him as he was living in a \
boat made of concrete that had never left its Leith drydock.  He was \
subsequently seen sneaking into the AI Dept. late at night carrying \
plumbing supplies.  It turned out that was using the Dept's robots to \
assemble a shower, to entice her to his boat.\n\n\
He literally lost the only draft of his thesis in the North Sea when \
he sailed up to join the department at Edinburgh.  A storm came up \
and he was rescued by a passing ship, but his boat sunk with his \
thesis.  So he is still Mr. John Smith, not Dr.  His protege was Mark Smith, \
a great Pop implementor on PDP-11s, who under John's care also \
never quite became a Dr.  Mr. Mark Smith was my thesis advisor, and I continue \
the proud tradition of failing to get a PhD by doing too much language \
hacking.";

   String label = "Create Editor";
    Color bgColor;
    MyEditor f1;
    MyEditor f2;
    Thread loader;

    public void init() {
	bgColor = getColor(220, 220, 220);
	font = getFont("Helvetica", Font.BOLD, 24);

	TextStyle styles[] = {
	    new TextStyle(getFont("Courier", Font.PLAIN, 14), Color.black),
	    new TextStyle(getFont("Helvetica", Font.PLAIN, 18), Color.black),
	    new TextStyle(getFont("Helvetica", Font.BOLD, 19), Color.black),
	    new TextStyle(getFont("Helvetica", Font.ITALIC, 18), Color.black),
	    new TextStyle(getFont("Helvetica", Font.BOLD | Font.ITALIC, 18), Color.black),
	    new TextStyle(getFont("Helvetica", Font.PLAIN, 18), getColor(100, 200, 100)),
	    new TextStyle(getFont("TimesRoman", Font.PLAIN, 24), Color.black),
	    new TextStyle(getFont("TimesRoman", Font.BOLD, 24), Color.black),
	    new TextStyle(getFont("TimesRoman", Font.ITALIC, 24), Color.black),
	    new TextStyle(getFont("TimesRoman", Font.BOLD | Font.ITALIC, 24), Color.black),
	    new TextStyle(getFont("TimesRoman", Font.PLAIN, 24), getColor(200, 100, 100))
	};
	StyleMenu.styles = styles;
    
	TextRuler rulers[] = {
	    new TextRuler(TextRuler.FORMAT_NONE, styles[0]),
	    new TextRuler(TextRuler.FORMAT_LEFT, styles[0]),
	    new TextRuler(TextRuler.FORMAT_CENTER, styles[0]),
	    new TextRuler(TextRuler.FORMAT_RIGHT, styles[0]),
	    new TextRuler(TextRuler.FORMAT_FILL, styles[0])
	};
	FormatMenu.rulers = rulers;
    }

    public void start() {
	if (f1 == null) {
	    loader = new Thread(this);
	    loader.start();
	} else {
	    f1.resize(500, 300);
	    f1.map();
	    f2.resize(500, 300);
	    f2.map();
	}
	repaint();
    }

    public void stop() {
	if (loader != null) {
	    loader.stop();
	    loader = null;
	} else if (f1 != null) {
	    f1.unMap();
	    f2.unMap();
	}
    }

    public void run() {
	f1 = new MyEditor("view 1", item.parent.wServer, (Frame)item.parent.parent);
	f2 = new MyEditor("view 2", item.parent.wServer, (Frame)item.parent.parent);
	Text t = new Text(data);
	f1.ed.setText(t);
	f2.ed.setText(t);
	loader = null;
	repaint();
    }

    public void mouseUp(int x, int y) {
	if (f1 != null) {
	    f1.resize(500, 300);
	    f1.map();
	    f2.resize(500, 300);
	    f2.map();
	}
    }

    /**
     * Paint the applet.
     */
    public void paint(Graphics g) {
	g.setForeground(bgColor);
	g.fillRect(1, 1, width - 2, height -2);
	g.setForeground(fgColor);
	g.drawRect(0, 0, width - 1, height - 1);
	String str = (loader != null) ? "Loading" : ((f1 != null) ? "Editor" : "Initialized");
	g.drawString(str, (width - font.stringWidth(str)) / 2,
		     ((height - font.height) / 2) + font.ascent);
    }
}
