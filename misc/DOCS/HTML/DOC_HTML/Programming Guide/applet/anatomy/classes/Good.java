import browser.Applet;
import awt.Graphics;

class Good extends Applet implements Runnable {

    static final int NUMLOOPS = 2000000;
    int loop = 0;
    boolean doneInitializing = false;
    String message = null;
    Thread loopThread = null;

    public void init() {
	resize(500, 20);
    }

    public void start() {
	if (loopThread == null) {
	    loopThread = new Thread(this, "Good thread");
	    loopThread.start();
	}
    }

    public void stop() {
	loopThread.stop();
	loopThread = null;
    }

    public void run() {
	while (loop < NUMLOOPS) {
	    if ((++loop%50000)==0) {
		message = "Good: Initialization loop #"
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
	    g.drawString("Good: ", 5, 15);
	else if (!doneInitializing) 
	    g.drawString(message, 5, 15);
	else 
	    g.drawString("Good: Done initializing.", 5, 15);
    }
}
