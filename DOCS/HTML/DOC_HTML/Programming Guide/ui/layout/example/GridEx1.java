import java.awt.*;
import java.util.*;
import java.applet.Applet;

public class GridEx1 extends Applet {
    public void init() {
	setLayout(new GridLayout(0,2));
        setFont(new Font("Helvetica", Font.PLAIN, 14));
   
	add(new Button("Button 1"));
	add(new Button("Button 2"));
	add(new Button("Button 3"));
	add(new Button("Button 4"));
	add(new Button("Button 5"));

        resize(300, 100);
    }

    public static void main(String args[]) {
	Frame f = new Frame("Grid Layout Example");
	GridEx1 ex1 = new GridEx1();

	ex1.init();

	f.add("Center", ex1);
	f.pack();
	f.resize(f.preferredSize());
	f.show();
    }
}
