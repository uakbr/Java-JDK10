import java.awt.*;
import java.util.Vector;

public class DiagonalLayout implements LayoutManager {

    private int vgap;
    private int minWidth = 0, minHeight = 0;
    private int preferredWidth = 0, preferredHeight = 0;
    private boolean sizeUnknown = true;
    private boolean DEBUG = false;

    public DiagonalLayout() {
	this(5);
    }

    public DiagonalLayout(int v) {
	vgap = v;
    }

    /* Required by LayoutManager. */
    public void addLayoutComponent(String name, Component comp) {
    }

    /* Required by LayoutManager. */
    public void removeLayoutComponent(Component comp) {
    }

    private void setSizes(Container parent) {
	int nComps = parent.countComponents();
	Dimension d = null;

	if (DEBUG) {
	    System.out.println("");
	    System.out.println("setSizes()");
	}

	//Reset preferred/minimum width and height.
	preferredWidth = 0;
	preferredHeight = 0;
	minWidth = 0;
	minHeight = 0;

	for (int i = 0; i < nComps; i++) {
	    Component c = parent.getComponent(i);
	    if (c.isVisible()) {
		d = c.preferredSize();

		if (i > 0) {
		    preferredWidth += d.width/2; 
		    preferredHeight += vgap;
		} else {
		    preferredWidth = d.width;
		}
		preferredHeight += d.height;

		minWidth = Math.max(c.minimumSize().width, minWidth);
		minHeight = preferredHeight;

		if (DEBUG) {
		    System.out.println("Component["+i+"] preferred size: " + 
			c.preferredSize());
		    System.out.println("Component["+i+"] minimum size: " + 
			c.minimumSize());
	    	    System.out.print("preferred width: " + preferredWidth);
	            System.out.print("; preferred height: " + preferredHeight);
	            System.out.print("; minWidth: " + minWidth);
	            System.out.println("; minHeight: " + minHeight);
		}
	    }
	}
    }


    /* Required by LayoutManager. */
    public Dimension preferredLayoutSize(Container parent) {
	Dimension dim = new Dimension(0, 0);
	int nComps = parent.countComponents();

	if (DEBUG) {
	    System.out.println("");
	    System.out.println("Start of preferredLayoutSize()");
	}

	setSizes(parent);

	//Always add the container's insets!
	Insets insets = parent.insets();
	dim.width = preferredWidth + insets.left + insets.right;
	dim.height = preferredHeight + insets.top + insets.bottom;

	sizeUnknown = false;

	if (DEBUG) {
	    System.out.println("preferred layout size: " + dim);
	    System.out.println("End of preferredLayoutSize()");
	    System.out.println("");
	}
	return dim;
    }

    /* Required by LayoutManager. */
    public Dimension minimumLayoutSize(Container parent) {
	Dimension dim = new Dimension(0, 0);
	int nComps = parent.countComponents();

	if (DEBUG) {
	    System.out.println("");
	    System.out.println("Start of minimumLayoutSize()");
	}

	//Always add the container's insets!
	Insets insets = parent.insets();
	dim.width = minWidth + insets.left + insets.right;
	dim.height = minHeight + insets.top + insets.bottom;

	sizeUnknown = false;

	if (DEBUG) {
	    System.out.println("minimum layout size: " + dim);
	    System.out.println("End of minimumLayoutSize()");
	    System.out.println("");
	}
	return dim;
    }

    /* Required by LayoutManager. */
    /* This is called when the panel is first displayed, 
     * and every time its size changes. 
     * Note: You CAN'T assume preferredLayoutSize() or minimumLayoutSize()
     * will be called -- in the case of applets, at leas, they probably
     * won't be. */
    public void layoutContainer(Container parent) {
	Insets insets = parent.insets();
	int maxWidth = parent.size().width
		       - (insets.left + insets.right);
	int maxHeight = parent.size().height
		        - (insets.top + insets.bottom);
	int nComps = parent.countComponents();
	int previousWidth = 0, previousHeight = 0;
	int x = 0, y = insets.top;
	int rowh = 0, start = 0;
	int xFudge = 0, yFudge = 0;
	boolean oneColumn = false;

	if (DEBUG) {
	    System.out.println("");
	    System.out.println("Start of layoutContainer()");
	    System.out.println("Container size = " + parent.size());
	}

	// Go through the components' sizes, if neither preferredLayoutSize()
	// nor minimumLayoutSize() has been called.
	if (sizeUnknown) {
	    if (DEBUG) {
		System.out.println("Calling preferredLayoutSize()");
	    }
	    setSizes(parent);
	}
	    
	if (maxWidth <= minWidth) {
	    oneColumn = true;
	}

	if (maxWidth != preferredWidth) {
	    xFudge = (maxWidth - preferredWidth)/(nComps - 1);
	    if (DEBUG) {
		System.out.println("horizontal adjustment = " + xFudge);
	    }
	}

	if (maxHeight > preferredHeight) {
	    yFudge = (maxHeight - preferredHeight)/(nComps - 1);
	    if (DEBUG) {
		System.out.println("vertical adjustment = " + yFudge);
	    }
	}

	for (int i = 0 ; i < nComps ; i++) {
	    Component c = parent.getComponent(i);
	    if (c.isVisible()) {
		Dimension d = c.preferredSize();
		
 		// increase x and y, if appropriate
		if (i > 0) { 
		    if (!oneColumn) {
	    	        //x += previousWidth - d.width/2 + xFudge;
			x += previousWidth/2 + xFudge;
		    }
	            y += previousHeight + vgap + yFudge;
		}
		
		if (DEBUG) {
		    System.out.println("Placing component["+i+"] at (" +
			x + "," + y + ")");
		}

		// If x is too large, ...
		if ((!oneColumn) &&
		    (x + d.width) > (parent.size().width - insets.right)) {
		    if (DEBUG) {
		        System.out.println("Eek! x too large: x="+x+"; maxX="+
			    (parent.size().width - insets.right));
		        System.out.println("Component["+i+"] preferred width: "
			    +d.width);
		    }
		    // ... reduce x to a reasonable number.
		    x = parent.size().width - insets.bottom - d.width;
		    if (DEBUG) {
		        System.out.println("Set x to " + x);
		    }
		}

		// If y is too large, ...
		if ((y + d.height) > (parent.size().height - insets.bottom)) {
		    if (DEBUG) {
		        System.out.println("Eek! y too large: y=" + y +
			    "; height=" + d.height +
			    "; maxY="+ (parent.size().height - insets.bottom));
		        System.out.println("Component["+i+"] preferred height: "
			    +d.height);
		    }
		    // ... do nothing.
		    // Another choice would be to do what we do to x.
		}

		// Set the component's size and position.
		c.reshape(x, y, d.width, d.height);

		previousWidth = d.width;
		previousHeight = d.height;
	    }
	}

	if (DEBUG) {
	    System.out.println("End of layoutContainer()");
	    System.out.println("");
	}
    }
    
    public String toString() {
	String str = "";
	return getClass().getName() + "[vgap=" + vgap + str + "]";
    }
}

// TO DO: 
// 1. Come up with a framework for making the Applet just have
//    a button that pops up a window.
// 2. Do event handling for Quit.

