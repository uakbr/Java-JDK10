
import browser.Applet;
import awt.Graphics;

class TimingIsEverything extends Applet {

    public long firstClickTime = 0;
    public String displayStr;

    public void init() {
	resize (300, 50);
	displayStr = "Double Click Me";
    }
    public void paint(Graphics g) {
	g.drawRect(0, 0, 299, 49);
	g.drawString(displayStr, 40, 30);
    }
    public void mouseDown(int x, int y) {
	long clickTime = System.currentTimeMillis();
	long clickInterval = clickTime - firstClickTime;
	if (clickInterval < 200) {
	    displayStr = "Double Click!! (Interval = " + clickInterval + ")";
	    firstClickTime = 0;
	} else {
	    displayStr = "Single Click!!";
	    firstClickTime = clickTime;
	}
	repaint();
    }
}
