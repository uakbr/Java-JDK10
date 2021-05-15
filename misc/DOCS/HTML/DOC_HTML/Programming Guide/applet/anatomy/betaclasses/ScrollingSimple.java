import java.awt.*;

public class ScrollingSimple extends java.applet.Applet {

    TextField field = new TextField(80);

    public void init() {
        field.setEditable(false);
        add(field);
        resize(field.preferredSize());
        show();
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
        String t = field.getText();

        System.out.println(newWord);
        field.setText(t + newWord);
        repaint();
    }
}
