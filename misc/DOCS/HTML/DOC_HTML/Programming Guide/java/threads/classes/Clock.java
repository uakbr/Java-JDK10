
import browser.Applet;
import awt.Graphics;
import java.util.Date;

class Clock extends Applet implements Runnable {

    Thread clockThread;

    public void start() {
	if (clockThread == null) {
	    clockThread = new Thread(this, "Clock");
	    clockThread.start();
	}
    }
    public void run() {
	while (clockThread != null) {
	    repaint();
	    clockThread.sleep(1000);
	}
    }
    public void paint(Graphics g) {
	Date now = new Date();
	g.drawString(now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds(), 5, 10);
    }
    public void stop() {
	clockThread.stop();
	clockThread = null;
    }
}
