import java.awt.Graphics;

public class SimpleClick extends java.applet.Applet {

    StringBuffer buffer = new StringBuffer();

    public void init() {
	resize(500, 20);
        addItem("initializing... ");
    }

    public void start() {
        addItem("starting... ");
    }

    public void stop() {
        addItem("stopping... ");
    }

    public void destroy() {
	addItem("preparing for unloading...");
    }

    public void addItem(String newWord) {
	System.out.println(newWord);
	buffer.append(newWord);
	repaint();
    }

    public void paint(Graphics g) {
	g.drawRect(0, 0, size().width - 1, size().height - 1);
	g.drawString(buffer.toString(), 5, 15);
    }

    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	addItem("click!... ");
	return true;
    }
}
