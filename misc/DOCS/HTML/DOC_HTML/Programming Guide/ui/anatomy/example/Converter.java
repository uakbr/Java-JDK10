/* This program could use some layout work, and the functionality
 * could use some tweaking, but it seems to basically work.
 */
import java.awt.*;
import java.util.*;
import java.applet.Applet;

public class Converter extends Applet {
    Frame window;
    ConversionPanel metricPanel, usaPanel;
    Unit metricDistances[] = new Unit[3];
    Unit usaDistances[] = new Unit[4];

    /** Create the ConversionPanels (one for metric, another for U.S.).
      * I used "U.S." because although Imperial and U.S. distance
      * measurements are the same, this program could be extended to
      * include volume measurements, which aren't the same.
      */
    public void init() {
	setLayout(new GridLayout(2,0,10,10));

	metricDistances[0] = new Unit("Centimeters", 0.01);
	metricDistances[1] = new Unit("Meters", 1.0);
	metricDistances[2] = new Unit("Kilometers", 1000.0);
	metricPanel = new ConversionPanel(this, "Metric System",
				  	  metricDistances);

	usaDistances[0] = new Unit("Inches", 0.0254);
	usaDistances[1] = new Unit("Feet", 0.305);
	usaDistances[2] = new Unit("Yards", 0.914);
	usaDistances[3] = new Unit("Miles", 1613.0);
	usaPanel = new ConversionPanel(this, "U.S. System", usaDistances);

	add(metricPanel);
	add(usaPanel);
    }

    /** Does the conversion from metric to U.S., or vice versa, and
      * updates the appropriate ConversionPanel. */
    void convert(ConversionPanel from) {
	ConversionPanel to;

	if (from == metricPanel)
	    to = usaPanel;
	else
	    to = metricPanel;
	double multiplier = from.getMultiplier() / to.getMultiplier();
	to.setValue(from.getValue() * multiplier);
    }

    /** Draws a box around this panel. */
    public void paint(Graphics g) {
	Dimension d = size();
	g.drawRect(0,0, d.width - 1, d.height - 1);
    }
	
    /** Puts a little breathing space between
      * the panel and its contents, which lets us draw a box
      * in the paint() method.
      */
    public Insets insets() {
	return new Insets(3,3,3,3);
    }

    public static void main(String args[]) {
	Frame f = new Frame("Converter Applet/Application");
	Converter converter = new Converter();

	converter.init();

	f.add("Center", converter);
	f.pack();
	f.resize(250, 165);
	f.show();
    }

}


class ConversionPanel extends Panel {
    String title;
    TextField textField;
    Scrollbar slider;
    Choice unitChooser;
    int min = 0;
    int max = 10000;
    Converter controller;
    Unit units[];

    //TO DO: Should make both panels' choices the same width.
    ConversionPanel(Converter myController, String myTitle, Unit myUnits[]) {
	super();
	controller = myController;
	title = myTitle;
	units = myUnits;

	//Build the left half of this panel.
	textField = new TextField("0", 10);
	slider = new Scrollbar(Scrollbar.HORIZONTAL, 0, 100, min, max);
	Panel leftHalf = new Panel();
	leftHalf.setLayout(new BorderLayout());
	leftHalf.add("Center", textField);
	leftHalf.add("South", slider);

	//Build the right half of this panel.
	unitChooser = new Choice();
	for (int i = 0; i < units.length; i++) {
	    unitChooser.addItem(units[i].description);
	}

	//Put everything in this panel.
	setLayout(new BorderLayout());
	add("North", new Label(title, Label.CENTER));
	add("Center", leftHalf);
	add("East", unitChooser);
    }

    /** Returns the multiplier (units/meter) for the currently
      * selected unit of measurement.
      */
    double getMultiplier() {
	int i = unitChooser.getSelectedIndex();
	return (units[i].multiplier);
    }

    /** Draws a box around this panel. */
    public void paint(Graphics g) {
	Dimension d = size();
	g.drawRect(0,0, d.width - 1, d.height - 1);
    }
	
    /** Puts a little breathing space between
      * the panel and its contents, which lets us draw a box
      * in the paint() method.
      */
    public Insets insets() {
	// Right offset is more than left, due to Choice bug.
	return new Insets(2,10,2,15);
    }

    /** Gets the current value in the text field.
      * That's guaranteed to be the same as the value
      * in the scroller (subject to rounding, of course).
      */
    double getValue() {
	double f;
	try {
	    f = Double.valueOf(textField.getText()).doubleValue(); 
	} catch (java.lang.NumberFormatException e) {
	    f = 0.0;
	}
	return f;
    }

    /** Respond to user actions. */
    public boolean handleEvent(Event e) {
	if (e.target instanceof Scrollbar) {
	    textField.setText(String.valueOf(slider.getValue()));
	    controller.convert(this);
	    return true;
	} else if (e.target instanceof TextField) {
	    setSliderValue(getValue());
	    controller.convert(this);
	    return true;
	} else if (e.target instanceof Choice) {
	    controller.convert(this);
	    return true;
	} 
	return false;
    }

    /** Set the values in the slider and text field. */
    void setValue(double f) {
	setSliderValue(f);
        textField.setText(String.valueOf(f));
    }

    /** Set the slider value. */
    void setSliderValue(double f) {
	int sliderValue = (int)f;

	if (sliderValue > max)
   	    sliderValue = max;
	if (sliderValue < min)
	    sliderValue = min;
        slider.setValue(sliderValue);
    }
}


class Unit {
    String description;
    double multiplier;

    Unit(String description, double multiplier) {
	super();
	this.description = description;
	this.multiplier = multiplier;
    }

    public String toString() {
	String s = "Meters/" + description + " = " + multiplier;
	return s;
    }
}
