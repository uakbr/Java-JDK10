
import awt.Graphics;
import browser.Applet;

class GraphApplet extends Applet {
    double f(double x) {
	return (Math.cos(x/5) + Math.sin(x/7) + 2) * height / 4;
    }

    public void paint(Graphics g) {
        for (int x = 0 ; x < width ; x++) {
	    g.drawLine(x, (int)f(x), x + 1, (int)f(x + 1));
        }
    }
}
