import java.awt.*;
import java.util.*;
import java.applet.Applet;

public class CardEx1 extends Applet {
    Panel cards;
    final static String BUTTONPANEL = "Panel with Buttons";
    final static String TEXTPANEL = "Panel with TextField";

    public void init() {
	setLayout(new BorderLayout());
        setFont(new Font("Helvetica", Font.PLAIN, 14));

	//Put the Choice in a Panel to get a nicer look.
	Panel cp = new Panel();
	Choice c = new Choice();
	c.addItem(BUTTONPANEL);
	c.addItem(TEXTPANEL);
	cp.add(c);
	add("North", cp);

	cards = new Panel();
	cards.setLayout(new CardLayout());
   
	Panel p1 = new Panel();
	p1.add(new Button("Button 1"));
	p1.add(new Button("Button 2"));
	p1.add(new Button("Button 3"));

	Panel p2 = new Panel();
	p2.add(new TextField("TextField", 20));

	cards.add(BUTTONPANEL, p1);
	cards.add(TEXTPANEL, p2);
	add("Center", cards);
        resize(200, 150); 
    }

    public boolean action(Event evt, Object arg) {
	if (evt.target instanceof Choice) {
	    ((CardLayout)cards.getLayout()).show(cards,(String)arg);
	    return true;
	}
	return false;
    }

    public static void main(String args[]) {
	Frame f = new Frame("Card Layout Example");
	CardEx1 ex1 = new CardEx1();

	ex1.init();

	f.add("Center", ex1);
	f.pack();
	f.resize(f.preferredSize());
	f.show();
    }
}
