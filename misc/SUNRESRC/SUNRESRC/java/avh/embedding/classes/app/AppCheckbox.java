package app;

import awt.*;

/**
 * A simple checkbox. It can be on or off (down or up).
 *
 * @author Arthur van Hoff
 */
public
class AppCheckbox extends AppComponent {
    public boolean value;
    boolean down;
    
    public AppCheckbox(AppletPanel app) {
	super(app);
    }

    public void setValue(boolean value) {
	if (value != this.value) {
	    this.value = value;
	    app.touched = true;;
	}
    }
    public void paint(Graphics g) {
	g.setForeground((value ^ down) ? Color.gray : Color.lightGray);
	g.paint3DRect(0, 0, w, h, true, !(value ^ down));
	g.setForeground(Color.black);
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
	    value = !value;
	    down = false;
	    app.touched = true;;
	    app.action(this);
	}
    }
}
