package app;

import awt.*;

/**
 * An sample applet panel.
 *
 * @author Arthur van Hoff
 */

public
class TestPanel3 extends AppletPanel {
    AppSlider celcius;
    AppSlider farenheit;
    
    public void init() {
	panel = new AppPanel(this);
	panel.reshape(0, 0, width, height);
	panel.add(new AppLabel(this, "Celcius:", AppLabel.RIGHT), 5, 5, 70, 20);
	panel.add(new AppLabel(this, "Farenheit:", AppLabel.RIGHT), 5, 30, 70, 20);
	panel.add(celcius = new AppSlider(this, -100, 200, Color.red), 80, 5, 160, 20);
	panel.add(farenheit = new AppSlider(this, -100, 200, Color.pink), 80, 30, 160, 20);
	celcius.setValue(37);
	farenheit.setValue(99);
    }
    public void preview(AppSlider slider) {
	if (slider == celcius) {
	    farenheit.setValue(32 + (celcius.value * 9)/5);
	} else {
	    celcius.setValue(((farenheit.value - 32) * 5) / 9);
	}
    }
}
