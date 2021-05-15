package app;

import awt.*;

/**
 * A simple value slider.
 *
 * @author Arthur van Hoff
 */
public
class AppSlider extends AppComponent {
    public int value;
    public int minValue;
    public int maxValue;
    public Font font;
    public Color color;
    public boolean drawValue = true;
    public boolean drawOutline = true;

    public AppSlider(AppletPanel app, int minValue, int maxValue) {
	this(app, minValue, maxValue, Color.gray);
    }
    public AppSlider(AppletPanel app, int minValue, int maxValue, Color color) {
	super(app);
	this.value = minValue;
	this.minValue = minValue;
	this.maxValue = maxValue;
	this.color = color;
	this.font = app.getFont("Helvetica", 14);
    }

    public void setValue(int value) {
	value = Math.max(minValue, Math.min(maxValue, value));
	if (value != this.value) {
	    this.value = value;
	    app.touched = true;;
	}
    }

    public void paint(Graphics g) {
	g.setForeground(Color.black);
	g.setFont(font);
	if (drawOutline) {
	    g.drawRect(0, 0, w-1, h-1);
	}

	String str = String.valueOf(value);
	int strw = font.stringWidth(str);

	g.setForeground(color);
	if (w > h) {
	    int vx = ((w-2) * (value - minValue)) / (maxValue - minValue);
	    g.fillRect(1, 1, vx, h-2);
	    g.setForeground(Color.lightGray);
	    g.fillRect(vx+1, 1, (w-2) - vx, h-2);
	    if (drawValue) {
		g.setForeground(Color.black);
		if (vx > (w/2)) {
		    g.drawString(str, vx-strw, ((h + font.height) / 2) - font.descent);
		} else {
		    g.drawString(str, vx+1, ((h + font.height) / 2) - font.descent);
		}
	    }
	} else {
	    int vy = (h-2) - ((h-2) * (value - minValue)) / (maxValue - minValue);
	    g.fillRect(1, vy+1, w-2, (h-2) - vy);
	    g.setForeground(Color.lightGray);
	    g.fillRect(1, 1, w-2, vy);
	    if (drawValue) {
		g.setForeground(Color.black);
		if (vy > (h/2)) {
		    g.drawString(str, (w - strw) / 2, vy - font.descent);
		} else {
		    g.drawString(str, (w - strw) / 2, vy + font.ascent);
		}
	    }
	}
    }

    public void mouseDown(int x, int y) {
	if (w > h) {
	    setValue(minValue + (((x-1) * (maxValue - minValue)) / (w-2)));
	} else {
	    y = (h-1) - y;
	    setValue(minValue + (((y-1) * (maxValue - minValue)) / (h-2)));
	}
	app.preview(this);
    }
    public void mouseDrag(int x, int y) {
	mouseDown(x, y);
    }
    public void mouseUp(int x, int y) {
	app.action(this);
    }
}
