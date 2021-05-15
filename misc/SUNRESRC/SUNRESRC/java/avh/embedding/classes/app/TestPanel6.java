package app;

import awt.*;

/**
 * An sample applet panel.
 *
 * @author Arthur van Hoff
 */
public
class TestPanel6 extends AppletPanel {
    AppCheckbox foo;
    AppCheckbox bar;
    AppCheckbox baz;
    AppLabel lbl;

    public void init() {
	panel = new AppPanel(this);
	panel.reshape(0, 0, width, height);
	panel.add(new AppLabel(this, "Foo:", AppLabel.RIGHT), 5, 5, 50, 20);
	panel.add(foo = new AppCheckbox(this), 65, 10, 10, 10);
	panel.add(new AppLabel(this, "Bar:", AppLabel.RIGHT), 80, 5, 50, 20);
	panel.add(bar = new AppCheckbox(this), 140, 10, 10, 10);
	panel.add(new AppLabel(this, "Baz:", AppLabel.RIGHT), 155, 5, 50, 20);
	panel.add(baz = new AppCheckbox(this), 215, 10, 10, 10);

	panel.add(lbl = new AppLabel(this, "- | - | -", AppLabel.CENTER), 0, 40, width, 20);
    }

    public void action(AppCheckbox check) {
	lbl.setValue(
	    (foo.value ? "X" : "-") + " | " +
	    (bar.value ? "X" : "-") + " | " +
	    (baz.value ? "X" : "-"));
    }
}
