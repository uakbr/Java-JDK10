import browser.Applet;
import awt.Graphics;

class Bad extends Applet {

    static final int NUMLOOPS = 2000000;
    int loop = 0;
    boolean doneInitializing = false;
    String message = null;
    Thread loopThread = null;

    public void init() {
	resize(500, 20);

	while (loop < NUMLOOPS) {
	    if ((++loop%50000)==0) {
		message = "Bad: Initialization loop #"
			  + loop + " of " + NUMLOOPS;
		showStatus(message);
		repaint();
	    }
	}
	doneInitializing = true;
	repaint();
    }

    /* The paint() method can't be called until init() has exited. */
    public void paint(Graphics g) {
	g.clearRect(0, 0, width - 1, height - 1);
	g.drawRect(0, 0, width - 1, height - 1);
	if (message == null) 
	    g.drawString("Bad: ", 5, 15);
	else if (!doneInitializing) 
	    g.drawString(message, 5, 15);
	else 
	    g.drawString("Bad: Done initializing.", 5, 15);
    }
}
