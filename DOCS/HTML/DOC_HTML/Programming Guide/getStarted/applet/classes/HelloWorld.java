import browser.Applet;
import awt.Graphics;
class HelloWorld extends Applet {
    public void init() {
	resize(150,25);
    }
    public void paint(Graphics g) {
	g.drawString("Hello world!", 50, 25);
    }
}
