import java.awt.*;

public class RaceApplet extends java.applet.Applet implements Runnable {

    final static int NUMRUNNERS = 2;
    final static int SPACING = 20;

    Runner runners[] = new Runner[NUMRUNNERS];

    Thread updateThread;

    public void init() {
	String raceType = getParameter("type");
	for (int i = 0; i < NUMRUNNERS; i++) {
	    runners[i] = new Runner();
	    if (raceType.compareTo("unfair") == 0)
	    	runners[i].setPriority(i+1);
	    else
	    	runners[i].setPriority(2);
        }
        if (updateThread == null) {
            updateThread = new Thread(this, "Thread Race");
            updateThread.setPriority(NUMRUNNERS+1);
        }
    }

    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	if (!updateThread.isAlive())
            updateThread.start();
	for (int i = 0; i < NUMRUNNERS; i++) {
	    if (!runners[i].isAlive())
	        runners[i].start();
	}
	return true;
    }

    public void paint(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, size().width, size().height);
        g.setColor(Color.black);
        for (int i = 0; i < NUMRUNNERS; i++) {
	    int pri = runners[i].getPriority();
	    g.drawString(new Integer(pri).toString(), 0, (i+1)*SPACING);
	}
        update(g);
    }

    public void update(Graphics g) {
        for (int i = 0; i < NUMRUNNERS; i++) {
	    g.drawLine(SPACING, (i+1)*SPACING, SPACING + (runners[i].tick)/1000, (i+1)*SPACING);
	}
    }

    public void run() {
        while (updateThread != null) {
            repaint();
            try {
		updateThread.sleep(10);
	    } catch (InterruptedException e) {
	    }
        }
    }    

    public void stop() {
	for (int i = 0; i < NUMRUNNERS; i++) {
	    if (runners[i].isAlive()) {
	        runners[i].stop();
	        runners[i] = null;
	    }
        }
	if (updateThread.isAlive()) {
            updateThread.stop();
            updateThread = null;
	}
    }
}
