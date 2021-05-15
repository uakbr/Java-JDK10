package app;

import awt.*;

/**
 * A static image component. It displays
 * a single centered image.
 *
 * @author Arthur van Hoff
 */
public
class AppImage extends AppComponent {
    Image img;
    
    public AppImage(AppletPanel app, String img) {
	super(app);
	this.img = app.getImage(img);
    }

    public void paint(Graphics g) {
	if (img != null) {
	    g.drawImage(img, (w - img.width)/2, (h - img.height)/2);
	}
    }
    public synchronized AppComponent locate(int x, int y) {
	return null;
    }
}
