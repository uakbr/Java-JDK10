import java.awt.*;
import java.applet.Applet;

public class GUI extends Applet {
    Frame window;

    public void init() {
	Panel bottomPanel = new Panel();
	Panel centerPanel = new Panel();

	setLayout(new BorderLayout());
	add("South", bottomPanel);
	add("Center", centerPanel);

	//Add small things at the bottom.
	bottomPanel.add(new TextField("TextField"));
	bottomPanel.add(new Button("Button"));
	bottomPanel.add(new Checkbox("Checkbox"));
	Choice c = new Choice();
	c.addItem("Choice Item 1");
	c.addItem("Choice Item 2");
	c.addItem("Choice Item 3");
	bottomPanel.add(c);

	//Add big things to the center area.
	centerPanel.setLayout(new GridLayout(1,2));
	//Put a canvas in the left column.
	centerPanel.add(new MyCanvas());
	//Put a label and a text area in the right column.
	Panel p = new Panel();
	p.setLayout(new BorderLayout());
	p.add("North", new Label("Label", Label.CENTER));
	p.add("Center", new TextArea("TextArea", 5, 20));
	centerPanel.add(p);
    }

    public void start(){
	//Create a window with a menu at the top.
	window = new MyWindow("Frame");

	window.pack();
	Rectangle bounds = bounds();
	Rectangle wbounds = window.bounds();
	//window.move(bounds.x + (bounds.width - wbounds.width)/2,
		//bounds.y + (bounds.height - wbounds.height)/2);
	window.move(bounds.x + bounds.width + 15, bounds.y);
	window.show();
    }

    public static void main(String args[]) {
	Frame f = new Frame("GUI Applet/Application");
	GUI gui = new GUI();

	Font oldFont = gui.getFont();
	if (oldFont == null) {
	    System.out.println("Eek! font is null!");
	} else {
	    gui.setFont(new Font(oldFont.getFamily(), oldFont.getStyle(),
	                oldFont.getSize()+2));
	}

	gui.init();

	f.add("Center", gui);
	f.resize(300, 300);
	f.pack();
	f.show();

	gui.start();
    }

}

class MyCanvas extends Canvas {

    public void paint(Graphics g) {
	int w = size().width;
	int h = size().height;
	g.drawRect(0, 0, w - 1, h - 1);
	g.drawString("Canvas", (w - g.getFontMetrics().stringWidth("Canvas"))/2,
		      10);

	g.setFont(new Font("Helvetica", Font.PLAIN, 8));
	g.drawLine(10,10, 100,100);
	g.fillRect(9,9,3,3);
	g.drawString("(10,10)", 13, 10);
	g.fillRect(49,49,3,3);
	g.drawString("(50,50)", 53, 50);
	g.fillRect(99,99,3,3);
	g.drawString("(100,100)", 103, 100);
    }

    public Dimension minimumSize() {
	return new Dimension(120,120);
    }

    public Dimension preferredSize() {
	return minimumSize();
    }
}

class MyWindow extends Frame {
    final String FILEDIALOGMENUITEM = "File dialog...";
    MyWindow(String title) {
	super("Frame");
	MenuBar mb = new MenuBar();
	Menu m = new Menu("Menu");
	m.add(new MenuItem("Menu item 1"));
	m.add(new CheckboxMenuItem("Menu item 2"));
	m.add(new MenuItem("Menu item 3"));
	m.add(new MenuItem("-"));
	m.add(new MenuItem(FILEDIALOGMENUITEM));
	mb.add(m);
	setMenuBar(mb);

	//Put a list in the window.
	List l = new List(3, false);
	l.addItem("List item 1");
	l.addItem("List item 2");
	l.addItem("List item 3");
	l.addItem("List item 4");
	l.addItem("List item 5");
	l.addItem("List item 6");
	l.addItem("List item 7");
	add("Center", l); 
    }

    public boolean action(Event evt, Object obj) {
	if (evt.target instanceof MenuItem) {
	    String label = (String)obj;
	    if (label.equals(FILEDIALOGMENUITEM)) {
		FileDialog fd = new FileDialog(this, "FileDialog");
		//fd bounds are 0,0,0,0! Sometimes it shows up too skinny.
		fd.show();
	    }
	} else if (evt.id == Event.WINDOW_DESTROY) {
	    hide();
	} 
	return true;
    }
}
