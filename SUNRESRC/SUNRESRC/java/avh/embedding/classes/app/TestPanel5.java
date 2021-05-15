package app;

import awt.*;

/**
 * An sample applet panel.
 *
 * @author Arthur van Hoff
 */

public
class TestPanel5 extends AppletPanel {
    AppText name;
    AppText street;
    AppText city;
    AppText zip;
    
    public void init() {
	panel = new AppPanel(this);
	panel.reshape(0, 0, width, height);
	panel.add(new AppLabel(this, "Name:", AppLabel.RIGHT), 5, 5, 50, 20);
	panel.add(new AppLabel(this, "Street:", AppLabel.RIGHT), 5, 30, 50, 20);
	panel.add(new AppLabel(this, "City:", AppLabel.RIGHT), 5, 55, 50, 20);
	panel.add(new AppLabel(this, "Zip:", AppLabel.RIGHT), 245, 55, 40, 20);
	panel.add(name = new AppText(this, ""), 60, 5, 330, 20);
	panel.add(street = new AppText(this, ""), 60, 30, 330, 20);
	panel.add(city = new AppText(this, ""), 60, 55, 180, 20);
	panel.add(zip = new AppText(this, ""), 290, 55, 100, 20);
	panel.add(new AppButton(this, "ok"), 60, 80, 80, 20);
	panel.add(new AppButton(this, "cancel"), 150, 80, 80, 20);
    }

    public void action(AppButton button) {
	play("audio/beep.au");
    }
}
