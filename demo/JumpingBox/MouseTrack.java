import java.awt.Graphics;
import java.lang.Math;

public class MouseTrack extends java.applet.Applet {

    int mx, my;
    int onaroll;

    public void init() {
	onaroll = 0;
	resize(500, 500);
    }

    public void paint(Graphics g) {
	g.drawRect(0, 0, size().width - 1, size().height - 1);
	mx = (int)(Math.random()*1000) % (size().width - (size().width/10));
	my = (int)(Math.random()*1000) % (size().height - (size().height/10));
	g.drawRect(mx, my, (size().width/10) - 1, (size().height/10) - 1);
    }

    /*
     * Mouse methods
     */
    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	requestFocus();
	if((mx < x && x < mx+size().width/10-1) && (my < y && y < my+size().height/10-1)) {
	    if(onaroll > 0) {
		switch(onaroll%4) {
		case 0:
		    play(getCodeBase(), "sounds/tiptoe.thru.the.tulips.au");
		    break;
		case 1:
		    play(getCodeBase(), "sounds/danger,danger...!.au");
		    break;
		case 2:
		    play(getCodeBase(), "sounds/adapt-or-die.au");
		    break;
		case 3:
		    play(getCodeBase(), "sounds/cannot.be.completed.au");
		    break;
		}
		onaroll++;
		if(onaroll > 5)
		    getAppletContext().showStatus("You're on your way to THE HALL OF FAME:"
			+ onaroll + "Hits!");
		else
		    getAppletContext().showStatus("YOU'RE ON A ROLL:" + onaroll + "Hits!");
	    }
	    else {
		getAppletContext().showStatus("HIT IT AGAIN! AGAIN!");
		play(getCodeBase(), "sounds/that.hurts.au");
		onaroll = 1;
	    }
	}
	else {
	    getAppletContext().showStatus("You hit nothing at (" + x + ", " + y + "), exactly\n");
	    play(getCodeBase(), "sounds/thin.bell.au");
	    onaroll = 0;
	}
	repaint();
	return true;
    }

    public boolean mouseMove(java.awt.Event evt, int x, int y) {
	if((x % 3 == 0) && (y % 3 == 0))
	    repaint();
	return true;
    }

    public void mouseEnter() {
	repaint();
    }

    public void mouseExit() {
	onaroll = 0;
	repaint();
    }

    /**
     * Focus methods
     */
    public void keyDown(int key) {
	requestFocus();
	onaroll = 0;
	play(getCodeBase(), "sounds/ip.au");
    }
}
