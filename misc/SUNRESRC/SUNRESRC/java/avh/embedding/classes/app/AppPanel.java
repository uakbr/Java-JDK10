package app;

import awt.*;

/**
 * A container of applet components. It can be
 * used as a component itself (it nests).
 *
 * @author Arthur van Hoff
 */
public
class AppPanel extends AppComponent {
    AppComponent component[] = new AppComponent[4];
    int ncompontents;
    public boolean drawOutline;

    public AppPanel(AppletPanel app) {
	super(app);
    }

    public void paint(Graphics g) {
	int ox = g.originX;
	int oy = g.originY;

	if (drawOutline) {
	    g.setForeground(Color.black);
	    g.drawRect(0, 0, w-1, h-1);
	}
	for (int i = 0 ; i < ncompontents ; i++) {
	    AppComponent comp = component[i];
	    g.setOrigin(ox + comp.x, oy + comp.y);
	    g.clipRect(0, 0, comp.w, comp.h);
	    comp.paint(g);
	}
    }

    public synchronized void add(AppComponent comp) {
	if (ncompontents == component.length) {
	    AppComponent newcomponent[] = new AppComponent[ncompontents*2];
	    System.arraycopy(component, 0, newcomponent, 0, ncompontents);
	    component = newcomponent;
	}
	component[ncompontents++] = comp;
	comp.parent = this;
    }

    public synchronized void add(AppComponent comp, int x, int y, int w, int h) {
	add(comp);
	comp.reshape(x, y, w, h);
    }

    public synchronized AppComponent locate(int x, int y) {
	for (int i = 0 ; i < ncompontents ; i++) {
	    AppComponent comp = component[i];
	    if ((comp = comp.locate(x - comp.x, y - comp.y)) != null) {
		return comp;
	    }
	}
	return super.locate(x, y);
    }
}
