package app;

import awt.*;
import browser.Applet;

/**
 * An applet that can contain simple components.
 * Subclass this class if you want to create an applet
 * that uses the UI components.
 *
 * @author Arthur van Hoff
 */
public
class AppletPanel extends Applet {
    protected AppPanel panel;
    protected AppComponent down;
    protected AppComponent focus;
    public boolean touched;

    public void init() {
	panel = new AppPanel(this);
	panel.reshape(0, 0, width, height);
    }

    public void update(Graphics g) {
	paint(g);
    }
    public void paint(Graphics g) {
	g.setOrigin(g.originX + panel.x, g.originY + panel.y);
	g.clipRect(0, 0, panel.w, panel.h);
	panel.paint(g);
	touched = false;
    }

    public void mouseDown(int x, int y) {
	down = panel.locate(x, y);
	if (down != null) {
	    // global to local coordinates
	    for (AppComponent comp = down ; comp != null ; comp = comp.parent) {
		x -= comp.x;
		y -= comp.y;
	    }
	    down.mouseDown(x, y);
	}
	if (touched) {
	    repaint();
	}
    }
    public void mouseDrag(int x, int y) {
	if (down != null) {
	    // global to local coordinates
	    for (AppComponent comp = down ; comp != null ; comp = comp.parent) {
		x -= comp.x;
		y -= comp.y;
	    }
	    down.mouseDrag(x, y);
	    if (touched) {
		repaint();
	    }
	}
    }
    public void mouseUp(int x, int y) {
	if (down != null) {
	    // global to local coordinates
	    for (AppComponent comp = down ; comp != null ; comp = comp.parent) {
		x -= comp.x;
		y -= comp.y;
	    }
	    down.mouseUp(x, y);
	    if (touched) {
		repaint();
	    }
	}
    }
    public void keyDown(int ch) {
	if (focus != null) {
	    focus.keyDown(ch);
	    if (touched) {
		repaint();
	    }
	}
    }
    public void setFocus(AppComponent comp) {
	focus = comp;
	getFocus();
    }

    public void action(AppButton but) {
	//System.out.println("BUTTON ACTION " + but.label);
    }
    public void action(AppCheckbox check) {
	//System.out.println("CHECKBOX ACTION " + check.value);
    }
    public void action(AppText text) {
	//System.out.println("TEXT ACTION " + text.value);
    }
    public void action(AppSlider slider) {
	//System.out.println("SLIDER ACTION " + slider.value);
    }
    public void preview(AppSlider slider) {
	//System.out.println("SLIDER ACTION " + slider.value);
    }
}
