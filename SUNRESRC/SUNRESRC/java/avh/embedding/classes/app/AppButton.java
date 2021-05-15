package app;

import awt.*;

/**
 * An applet button. You can set the label. When the
 * button is pressed it calls the action() method in
 * the AppletPanel.
 *
 * @author Arthur van Hoff
 */
public
class AppButton extends AppComponent {
    public String label;
    public Font font;
    boolean down;
    
    public AppButton(AppletPanel app, String label) {
	super(app);
	this.label = label;
	this.font = app.getFont("Helvetica", 14);
    }
    public void setLabel(String label) {
	this.label = label;
	app.touched = true;;
    }
    public void paint(Graphics g) {
	g.setForeground(down ? Color.gray : Color.lightGray);
	g.paint3DRect(0, 0, w, h, true, !down);
	g.setFont(font);
	g.setForeground(Color.black);
	g.drawString(label,
		     (w - font.stringWidth(label)) / 2,
		     ((h + font.height) / 2) - font.descent);
    }
    public void setDown(boolean down) {
	if (down != this.down) {
	    app.touched = true;;
	    this.down = down;
	}
    }
    public void mouseDown(int x, int y) {
	setDown(inside(x, y));
    }
    public void mouseDrag(int x, int y) {
	setDown(inside(x, y));
    }
    public void mouseUp(int x, int y) {
	if (down) {
	    app.action(this);
	}
	setDown(false);
    }
}
