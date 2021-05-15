import java.awt.Graphics;
public class HelloWorld extends java.applet.Applet {
    public void init() {
	resize(150,25);
    }
    public void paint(Graphics g) {
	g.drawString("Hello world!", 50, 25);
    }
}
