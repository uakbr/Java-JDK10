package app;

/**
 * An sample applet panel.
 *
 * @author Arthur van Hoff
 */

public
class TestPanel1 extends AppletPanel {
    AppText text;
    
    public void init() {
	panel = new AppPanel(this);
	panel.reshape(0, 0, width, height);
	panel.add(new AppButton(this, "ok"), 5, 5, 50, 20);
	panel.add(new AppButton(this, "cancel"), 60, 5, 50, 20);
	panel.add(new AppButton(this, "set"), 115, 5, 50, 20);
	panel.add(new AppLabel(this, "Value:", AppLabel.RIGHT), 5, 30, 50, 20);
	panel.add(text = new AppText(this, ""), 60, 30, 100, 20);
    }
    public void action(AppButton but) {
	text.setValue("button " + but.label);
    }
}
