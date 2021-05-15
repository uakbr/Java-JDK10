import java.awt.*;
import java.applet.Applet;

public class CustomEx1 extends Applet {
    public void init() {
	setLayout(new DiagonalLayout());
        setFont(new Font("Helvetica", Font.PLAIN, 14));
   
	add(new Button("Button 1"));
	add(new Button("Button 2"));
	add(new Button("Button 3"));
	add(new Button("Button 4"));
	add(new Button("Button 5"));

        resize(199, 145);
    }

    public static void main(String args[]) {
	Frame f = new Frame("Custom Layout Example");
	CustomEx1 ex1 = new CustomEx1();

	ex1.init();

	f.add("Center", ex1);
	f.pack();
	f.resize(f.preferredSize());
	f.show();
    }
}
