import java.awt.Graphics;
import java.awt.TextField;

public class PrintThread extends java.applet.Applet {

    TextField field = new TextField(80);
    //boolean justRepainted = false;

    public void init() {
	field.setEditable(false);
	add(field);
	resize(field.preferredSize());
	show();
        addItem("init:" + Thread.currentThread().getName() + " ");
    }

    public void start() {
        addItem("start:" + Thread.currentThread().getName() + " ");
    }

    public void stop() {
        addItem("stop:" + Thread.currentThread().getName() + " ");
    }

    public void destroy() {
        addItem("destroy:" + Thread.currentThread().getName() + " ");
    }

    public void addItem(String newWord) {
	String t = field.getText();

	System.out.println(newWord);
	field.setText(t + newWord);
	field.repaint();
    }

    //public synchronized void update(Graphics g) {
	//justRepainted = false;
    //}

    public synchronized void paint(Graphics g) {
	//if (!justRepainted) {
            addItem("paint:" + Thread.currentThread().getName() + " ");
	    //justRepainted = true;
	//}
    }
}
