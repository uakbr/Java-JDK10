import java.awt.*;
import java.applet.Applet;

public class NoneEx1 extends Applet {

    public void init() {
        Button b1, b2, b3;

	setLayout(null);
	setFont(new Font("Helvetica", Font.PLAIN, 14));

	b1 = new Button("one");
	add(b1);
	b1.reshape(50, 5, 50, 20);

	b2 = new Button("two");
	add(b2);
	b2.reshape(70, 35, 50, 20);

	b3 = new Button("three");
	add(b3);
	b3.reshape(130, 15, 50, 30);
    }

    public static void main(String args[]) {
	Frame f = new Frame("No Layout Manager Example");
	NoneEx1 ex1 = new NoneEx1();

	ex1.init();
        f.add("Center", ex1);
	f.pack();
	f.resize(250, 90);
        f.show();
    }
}
