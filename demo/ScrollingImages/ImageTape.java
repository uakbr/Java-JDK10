
import java.awt.*;

public class ImageTape extends java.applet.Applet implements Runnable {
    Image imgs[];
    int nimgs;
    int imgwidth;
    int x, newx;
    int dist = 5;
    int timeout = 200;
    Thread scroller;
    String dir;

    public void init() {
	String at = getParameter("img");
	dir = (at != null) ? at : "doc:/demo/images/duke";
	at = getParameter("speed");
	timeout = 1000 / ((at == null) ? 4 : Integer.valueOf(at).intValue());
	at = getParameter("dir");
	dist = (at == null) ? 5 : Integer.valueOf(at).intValue();
	at = getParameter("nimgs");
	nimgs = (at == null) ? 16 : Integer.valueOf(at).intValue();
	newx = x = size().width;

	imgs = new Image[nimgs];
	for (int i = 0 ; i < nimgs ; i++) {
	    imgs[i] = getImage(getDocumentBase(), dir + "/T" + (i+1) + ".gif");
	}
    }

    public void start() {
	if (scroller == null) {
	    scroller = new Thread(this);
	    scroller.start();
	}
    }
    public void stop() {
	if (scroller != null) {
	    scroller.stop();
	    scroller = null;
	}
    }

    public void run() {
	while (true) {
	try {Thread.currentThread().sleep(timeout);} catch (InterruptedException e){}
	    scroll(dist);
	}
    }

    synchronized void scroll(int dist) {
	newx += dist;
	repaint();
    }

    public void update(Graphics g) {
	g.setColor(Color.lightGray);

	if (newx != x) {
	    //g.clipRect(1, 1, size().width-2, size().height-2);
	    int dist = newx - x;
	    if (dist > 0) {
		g.copyArea(1, 1, (size().width-2) - dist, size().height-2, dist, 0);
		for (x = newx ; x > size().width ; x -= Math.max(size().width - 2, imgwidth));
		paint(g, 1, dist + 1);
	    } else {
		g.copyArea(1 - dist, 1, (size().width-2) + dist, size().height-2, 1, 1);
		for (x = newx ; x < 0 ; x += Math.max(size().width - 2, imgwidth));
		paint(g, (size().width-1) + dist, size().width-1);
	    }
	} else {
	    paint(g);
	}
    }

    public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
	if ((flags & WIDTH) != 0) {
	    imgwidth += img.getWidth(this);
	}
	return super.imageUpdate(img, flags, x, y, w, h);
    }

    public void paint(Graphics g, int fromx, int tox) {
	int x = this.x;
	newx = x;

	g.setColor(Color.lightGray);
	g.fillRect(fromx, 1, tox - fromx, size().height-2);
	g.clipRect(fromx, 1, tox - fromx, size().height-2);
	g.setColor(Color.black);

	for (int i = 0 ; i < nimgs ; i++) {
	    if (imgs[i] == null) {
		continue;
	    }

	    int w = imgs[i].getWidth(this);
	    int h = imgs[i].getHeight(this);
	    if ((w > 0) && (h > 0)) {
		if ((x + w > fromx) && (x < tox)) {
		    g.drawImage(imgs[i], x, size().height - (h+1), this);
		}
		if ((x + w) > size().width) {
		    x -= Math.max(size().width - 2, imgwidth);
		    if ((x + w > fromx) && (x < tox)) {
			g.drawImage(imgs[i], x, size().height - (h + 1), this);
		    }
		}
		x += w;
	    }
	}
    }
    public void paint(Graphics g) {
	g.draw3DRect(0, 0, size().width, size().height, true);
	paint(g, 1, size().width-1);
    }
}
