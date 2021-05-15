/*  Daniel Wyszynski 
    Center for Applied Large-Scale Computing (CALC) 
    04-12-95 

    Test of text animation.

    kwalrath: Changed string; added thread suspension. 5-9-95
*/
import java.awt.Graphics;
import java.awt.Font;

public class NervousText extends java.applet.Applet implements Runnable {

	char separated[];
	String s = null;
	Thread killme = null;
	int i;
	int x_coord = 0, y_coord = 0;
	String num;
	int speed=35;
	int counter =0;
	boolean threadSuspended = false; //added by kwalrath

public void init() {
	resize(150,50);
	setFont(new Font("TimesRoman",Font.BOLD,36));
	s = getParameter("text");
	if (s == null) {
	    s = "HotJava";
	}

	separated =  new char [s.length()];
	s.getChars(0,s.length(),separated,0);
 }

public void start() {
	if(killme == null) 
	{
        killme = new Thread(this);
        killme.start();
	}
 }

public void stop() {
	killme = null;
 }

public void run() {
	while (killme != null) {
	try {Thread.sleep(100);} catch (InterruptedException e){}
	repaint();
	}
	killme = null;
 }

public void paint(Graphics g) {
	for(i=0;i<s.length();i++)
	{
	x_coord = (int) (Math.random()*10+15*i);
	y_coord = (int) (Math.random()*10+36);
	g.drawChars(separated, i,1,x_coord,y_coord);
	}
 }
 
/* Added by kwalrath. */
public boolean mouseDown(java.awt.Event evt, int x, int y) {
        if (threadSuspended) {
            killme.resume();
        }
        else {
            killme.suspend();
        }
        threadSuspended = !threadSuspended;
    return true;
    }
}

