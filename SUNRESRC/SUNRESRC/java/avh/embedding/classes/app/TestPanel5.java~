package app;

import awt.*;

/**
 * An sample applet panel.
 *
 * @author Arthur van Hoff
 */

public
class TestPanel4 extends AppletPanel {
    AppSlider celcius;
    AppSlider farenheit;
    
    public void init() {
	panel = new AppPanel(this);
	panel.reshape(0, 0, width, height);
	panel.add(new AppImage(this, "images/cross.gif"), 5, 5, 50, 50);
	panel.add(new AppImage(this, "images/not.gif"), 60, 5, 50, 50);
	panel.add(new AppButton(this, "beep"), 30, 20, 60, 25);
    }

    public void action(AppButton but) {
	play("audio/beep.au");
    }
}
