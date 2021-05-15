import browser.Applet;
import awt.Graphics;

class PrintThread extends Applet {

    StringBuffer buffer = new StringBuffer();

    public void init() {
	resize(500, 20);
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
	System.out.println(newWord);
	buffer.append(newWord);
	repaint();
    }

    public void paint(Graphics g) {
	g.clearRect(0, 0, width - 1, height - 1);
	g.drawRect(0, 0, width - 1, height - 1);
	g.drawString(buffer.toString(), 5, 15);
        System.out.println("paint:" + Thread.currentThread().getName());
    }
}
