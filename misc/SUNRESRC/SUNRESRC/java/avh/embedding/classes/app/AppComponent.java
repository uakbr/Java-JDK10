package app;

import awt.*;

/**
 * An applet component. This component (or a subclass of it)
 * can be used inside a AppPanel to create a UI.<p>
 *
 * You can subclass this class to create your own applet
 * component. The coordinates are always relative.
 *
 * @author Arthur van Hoff
 */
public
class AppComponent {
    AppPanel parent;
    public AppletPanel app;
    public int  x, y, w, h;

    protected AppComponent(AppletPanel app) {
	this.app = app;
    }
    public void move(int x, int y) {
	reshape(x, y, w, h);
    }
    public void resize(int w, int h) {
	reshape(x, y, w, h);
    }
    public void reshape(int x, int y, int w, int h) {
	this.x = x;
	this.y = y;
	this.w = w;
	this.h = h;
    }

    public abstract void paint(Graphics g);

    public void mouseDown(int x, int y) {
    }
    public void mouseDrag(int x, int y) {
    }
    public void mouseUp(int x, int y) {
    }
    public void keyDown(int ch) {
    }
    
    public boolean inside(int x, int y) {
	return (x >= 0) && (x < w) && (y >= 0) && (y < h);
    }
    public synchronized AppComponent locate(int x, int y) {
	return inside(x, y) ? this : null;
    }

    public String toString() {
	return getClass().getName();
    }
}
