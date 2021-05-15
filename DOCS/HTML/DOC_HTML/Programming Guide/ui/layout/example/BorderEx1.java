import java.awt.*;
import java.util.*;
import java.applet.Applet;

public class BorderEx1 extends Applet {
    public void init() {
	setLayout(new BorderLayout());
        setFont(new Font("Helvetica", Font.PLAIN, 14));
   
	add("North", new Button("North"));
	add("South", new Button("South"));
	add("East", new Button("East"));
	add("West", new Button("West"));
	add("Center", new Button("Center"));

        resize(300, 100);
    }

    public static void main(String args[]) {
	Frame f = new Frame("Border Layout Example");
	BorderEx1 ex1 = new BorderEx1();

	ex1.init();

	f.add("Center", ex1);
	f.pack();
	f.resize(f.preferredSize());
	f.show();
    }
}
