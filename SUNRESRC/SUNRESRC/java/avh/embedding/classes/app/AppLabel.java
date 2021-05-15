package app;

import awt.*;

/**
 * A text label. It can be LEFT, CENTER or RIGHT
 * justified.
 *
 * @author Arthur van Hoff
 */
public
class AppLabel extends AppComponent {
    public String label;
    public Font font;
    public int align;
    public final static int LEFT = 0;
    public final static int CENTER = 1;
    public final static int RIGHT = 2;
    
    public AppLabel(AppletPanel app, String label) {
	this(app, label, LEFT);
    }
    public AppLabel(AppletPanel app, String label, int align) {
	super(app);
	this.label = label;
	this.align = align;
	this.font = app.getFont("Helvetica", Font.ITALIC, 14);
    }
    public void setValue(String label) {
	this.label = label;
	app.touched = true;
    }

    public void paint(Graphics g) {
	int x = 0;
	int y = ((h + font.height) / 2) - font.descent;

	g.setForeground(Color.lightGray);
	g.fillRect(0, 0, w, h);
	g.setForeground(Color.black);
	g.setFont(font);
	switch (align) {
	  case LEFT:
	    break;
	  case CENTER:
	    x = (w - font.stringWidth(label)) / 2;
	    break;
	  case RIGHT:
	    x = w - font.stringWidth(label);
	    break;
	}
	g.drawString(label, x, y);
    }
    public synchronized AppComponent locate(int x, int y) {
	return null;
    }
}
