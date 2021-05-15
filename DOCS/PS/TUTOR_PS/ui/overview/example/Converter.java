/*
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
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
	setLayout(new GridLayout(2,0,5,5));

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
	return new Insets(5,5,5,5);
    }

    public static void main(String args[]) {
	Frame f = new Frame("Converter Applet/Application");
	Converter converter = new Converter();

	converter.init();

	f.add("Center", converter);
	f.pack();
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
	GridBagConstraints c = new GridBagConstraints();
	GridBagLayout gridbag = new GridBagLayout();
	setLayout(gridbag);
	controller = myController;
	title = myTitle;
	units = myUnits;

	//Set up default constraints
	c.fill = GridBagConstraints.HORIZONTAL;

	//Add the label
	Label label = new Label(title, Label.CENTER);
	c.weightx = 0.0;
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(label, c);
	add(label);

	//Add the text field
	textField = new TextField("0", 10);
	c.weightx = 1.0;
	c.gridwidth = GridBagConstraints.RELATIVE;
	gridbag.setConstraints(textField, c);
	add(textField);

	//Add the pop-up list (Choice)
	unitChooser = new Choice();
	for (int i = 0; i < units.length; i++) {
	    unitChooser.addItem(units[i].description);
	}
	c.weightx = 0.0;
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(unitChooser, c);
	add(unitChooser);

	//Add the slider
	slider = new Scrollbar(Scrollbar.HORIZONTAL, 0, 100, min, max);
	c.weightx = 0.0;
	c.gridheight = 1;
	c.gridwidth = GridBagConstraints.RELATIVE;
	gridbag.setConstraints(slider, c);
	add(slider);
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
      * We add more pixels to the right, to work around a
      * Choice bug.
      */
    public Insets insets() {
	return new Insets(5,5,5,8);
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
	} else if ((e.target instanceof TextField) 
		   && (e.id == Event.ACTION_EVENT)) {
	    setSliderValue(getValue());
	    controller.convert(this);
	} else if ((e.target instanceof Choice) 
		   && (e.id == Event.ACTION_EVENT)) {
	    controller.convert(this);
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
