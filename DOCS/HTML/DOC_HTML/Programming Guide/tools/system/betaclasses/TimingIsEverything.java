import java.awt.Graphics;

public class TimingIsEverything extends java.applet.Applet {

    public long firstClickTime = 0;
    public String displayStr;

    public void init() {
	displayStr = "Double Click Me";
    }
    public void paint(Graphics g) {
	g.drawRect(0, 0, size().width-1, size().height-1);
	g.drawString(displayStr, 40, 30);
    }
    public boolean mouseDown(java.awt.Event evt, int x, int y) {
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
	return true;
    }
}
