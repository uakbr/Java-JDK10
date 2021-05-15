import browser.Applet;    
import awt.Color;
import awt.Graphics;

class AppWithAttribute extends Applet {
    Color textColor=null;

    public void init() {
        String color = getAttribute("color");
        if (color != null) {
            System.out.println("The color is: " + color);
            // We only handle white and red, for now.
            if (color.equals("white"))
                textColor = awt.Color.white;
            else if (color.equals("red"))
                textColor = awt.Color.red;
        }
        else
            System.out.println("No valid color was specified");
        
	resize(150, 25);

    }

    public void paint(Graphics g) {
 
        if (textColor != null) {
            g.setForeground(textColor);
        }

        g.drawString("Hello world!", 50, 25);
    }
}
