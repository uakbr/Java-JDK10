/*
 * @(#)GraphicsTest.java	1.4  04 Dec 1995 Sami Shaio, Kevin A. Smith
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */
import java.awt.*;
import java.applet.Applet;

public class GraphicsTest extends Applet 
{
    // class Variables
    GraphicsCards cards;

    public void init() 
    {
       setLayout(new BorderLayout());
       add("Center", cards = new GraphicsCards());
       Panel p = new Panel();
       p.add(new Button("next"));
       p.add(new Button("previous"));
       p.add(new Label("go to:", Label.RIGHT));

       Choice c;

       p.add(c = new Choice());
       c.addItem("Arc");
       c.addItem("Oval");
       c.addItem("Polygon");
       c.addItem("Rect");
       c.addItem("RoundRect");
       add("North", p);

       resize(400, 400);
    }  // end init()

    public boolean action(Event evt, Object arg) 
    {
       if (evt.target instanceof Choice) 
       {
          ( (CardLayout) cards.getLayout() ).show(cards,(String) arg );
       }
       else 
       {
          if ("next".equals(arg)) 
          {
             ( (CardLayout) cards.getLayout() ).next(cards);
          } 
          else if ("previous".equals(arg)) 
          {
             ( (CardLayout) cards.getLayout() ).previous(cards);
          }
       }

       return true;
    }  // end action()

    public static void main(String args[]) 
    {
       AppletFrame.startApplet("GraphicsTest", "Graphics Test", args);
    }

}   // end class GraphicsTest

    
class GraphicsCards extends Panel 
{
    public GraphicsCards() 
    {
       setLayout(new CardLayout());
       add("Arc", new ArcCard());
       add("Oval", new ShapeTest( new OvalShape() ) );
       add("Polygon", new ShapeTest( new PolygonShape() ) );
       add("Rect", new ShapeTest( new RectShape() ) );
       add("RoundRect", new ShapeTest( new RoundRectShape() ) );
    }
}   // end class GraphicsCards


class ArcCard extends Panel 
{
    public ArcCard() 
    {
       setLayout(new GridLayout(0, 2));
       add(new ArcPanel(true));
       add(new ArcPanel(false));
       add(new ArcDegreePanel(true));
       add(new ArcDegreePanel(false));
    }
}   // end class ArcCard


class ArcDegreePanel extends Panel 
{
    // class variables
    boolean filled;

    public ArcDegreePanel(boolean filled) 
    {
       this.filled = filled;
    }

    void arcSteps(Graphics g, 
                  int step, 
                  int x, 
                  int y, 
                  int w, 
                  int h, 
                  Color c1,  
                  Color c2) 
    {
       int a1 = 0;
       int a2 = step;
       int progress = 0;
       g.setColor(c1);
       for (; (a1+a2) <= 360; a1 = a1+a2, a2 += 1 ) 
       {
          if (g.getColor() == c1) 
          {
             g.setColor(c2);
          } 
          else 
          {
             g.setColor(c1);
          }
       
          if (filled) 
          {
             g.fillArc(x, y, w, h, a1, a2);
          } 
          else 
          {
             g.drawArc(x, y, w, h, a1, a2);
          }
       
          progress = a1+a2;
       }  // end for

       if (progress != 360) 
       {
          if (filled) 
          {
             g.fillArc(x, y, w, h, a1, 360 - progress);
          } 
          else 
          {
             g.drawArc(x, y, w, h, a1, 360 - progress);
          }
       }  // end if
    }  // end arcSteps()

    public void paint(Graphics g)
    {
       Rectangle r = bounds();

       arcSteps(g, 3, 0, 0, r.width, r.height, Color.orange, Color.blue);

       arcSteps(g, 
                2, 
                r.width / 4, 
                r.height / 4, 
                r.width / 2, 
                r.height / 2, 
                Color.yellow, 
                Color.green);

       arcSteps(g, 
                1, 
                (r.width  * 3) / 8, 
                (r.height * 3) / 8, 
                r.width / 4, 
                r.height / 4, 
                Color.magenta, 
                Color.white);

    }  // end paint()
}   // end class ArcDegreePanel


class ArcPanel extends Panel 
{
    // class variables
    boolean filled;

    public ArcPanel(boolean filled) 
    {
       this.filled = filled;
    }

    public void paint(Graphics g) 
    {
       Rectangle r = bounds();

       g.setColor(Color.yellow);
       if (filled) 
       {
          g.fillArc(0, 0, r.width, r.height, 0, 45);
       } 
       else 
       {
          g.drawArc(0, 0, r.width, r.height, 0, 45);
       }

       g.setColor(Color.green);
       if (filled) 
       {
          g.fillArc(0, 0, r.width, r.height, 90, -45);
       } 
       else 
       {
          g.drawArc(0, 0, r.width, r.height, 90, -45);
       }
    
       g.setColor(Color.orange);
       if (filled) 
       {
          g.fillArc(0, 0, r.width, r.height, 135, -45);
       } 
       else 
       {
          g.drawArc(0, 0, r.width, r.height, 135, -45);
       }

       g.setColor(Color.magenta);

       if (filled) 
       {
          g.fillArc(0, 0, r.width, r.height, -225, 45);
       } 
       else 
       {
          g.drawArc(0, 0, r.width, r.height, -225, 45);
       }

       g.setColor(Color.yellow);
       if (filled) 
       {
          g.fillArc(0, 0, r.width, r.height, 225, -45);
       } 
       else  
       {
          g.drawArc(0, 0, r.width, r.height, 225, -45);
       }

       g.setColor(Color.green);
       if (filled) 
       {
          g.fillArc(0, 0, r.width, r.height, -135, 45);
       } 
       else 
       {
          g.drawArc(0, 0, r.width, r.height, -135, 45);
       }

       g.setColor(Color.orange);
       if (filled) 
       {
          g.fillArc(0, 0, r.width, r.height, -45, -45);
       } 
       else 
       {
          g.drawArc(0, 0, r.width, r.height, -45, -45);
       }

       g.setColor(Color.magenta);
       if (filled) 
       {
          g.fillArc(0, 0, r.width, r.height, 315, 45);
       } 
       else 
       {
          g.drawArc(0, 0, r.width, r.height, 315, 45);
       }

    }  // end paint()

}   // end class ArcPanel


abstract class Shape 
{
    abstract void draw(Graphics g, int x, int y, int w, int h);
    abstract void fill(Graphics g, int x, int y, int w, int h);
}


class RectShape extends Shape 
{
    void draw(Graphics g, int x, int y, int w, int h) 
    {
       g.drawRect(x, y, w, h);
    }
    
    void fill(Graphics g, int x, int y, int w, int h) 
    {
       g.fillRect(x, y, w, h);
    }
}


class OvalShape extends Shape 
{
    void draw(Graphics g, int x, int y, int w, int h) 
    {
       g.drawOval(x, y, w, h);
    }
    
    void fill(Graphics g, int x, int y, int w, int h) 
    {
       g.fillOval(x, y, w, h);
    }
}


class RoundRectShape extends Shape 
{
    void draw(Graphics g, int x, int y, int w, int h) 
    {
       g.drawRoundRect(x, y, w, h, 10, 10);
    }

    void fill(Graphics g, int x, int y, int w, int h) 
    {
       g.fillRoundRect(x, y, w, h, 10, 10);
    }
}

class PolygonShape extends Shape 
{
    // class variables
    Polygon p;
    Polygon pBase;

    public PolygonShape() 
    {
       pBase = new Polygon();
       pBase.addPoint(0, 0);
       pBase.addPoint(10, 0);
       pBase.addPoint(5, 15);
       pBase.addPoint(10, 20);
       pBase.addPoint(5, 20);
       pBase.addPoint(0, 10);
       pBase.addPoint(0, 0);
    }

    void scalePolygon(float w, float h)
    {
       p = new Polygon();
       for (int i = 0; i < pBase.npoints; ++i)
       {
          p.addPoint( (int) (pBase.xpoints[i] * w), 
                      (int) (pBase.ypoints[i] * h) );
       }

    }

    void draw(Graphics g, int x, int y, int w, int h) 
    {
       Graphics ng = g.create();
       ng.translate(x, y);
       scalePolygon( (float) ( (float) w / (float) 10 ), 
                     (float) ( (float) h / (float) 20 ) );
       ng.drawPolygon(p);
    }

    void fill(Graphics g, int x, int y, int w, int h) 
    {
       Graphics ng = g.create();
       ng.translate(x, y);
       scalePolygon( (float) ( (float) w / (float) 10 ), 
                     (float) ( (float) h / (float) 20 ) );
       ng.fillPolygon(p);
    }
}


class ShapeTest extends Panel 
{
    Shape   shape;
    int     step;

    public ShapeTest(Shape shape, int step) 
    {
       this.shape = shape;
       this.step = step;
    }

    public ShapeTest(Shape shape) 
    {
       this(shape, 10);
    }

    public void paint(Graphics g) 
    {
       Rectangle bounds = bounds();

       int cx, cy, cw, ch;

       Color color;
   
       for (color=Color.red, 
            cx=bounds.x, 
            cy=bounds.y, 
            cw=bounds.width / 2, 
            ch=bounds.height ; 

            cw > 0 && ch > 0 ;

            cx+=step, 
            cy += step, 
            cw -= (step * 2), 
            ch -= (step * 2),
            color=ColorUtils.darker(color, 0.9) ) 
       {
          g.setColor(color);
          shape.draw(g, cx, cy, cw, ch);
       }

       for ( cx=bounds.x + bounds.width / 2, 
             cy=bounds.y,
             cw=bounds.width / 2, ch=bounds.height ; 

             cw > 0 && ch > 0;
          
             cx+=step, 
             cy += step, 
             cw -= (step * 2),
             ch -= (step * 2) ) 
       {
          if (g.getColor() == Color.red) 
          {
             g.setColor(Color.blue);
          } 
          else 
          {
             g.setColor(Color.red);
          }

          shape.fill(g, cx, cy, cw, ch);
       }  // end for

    }  // end paint()

}   // end class ShapeTest


class ColorUtils 
{
    static Color brighter(Color c, double factor) 
    {
       return new Color( Math.min((int)(c.getRed()  *(1/factor)), 255), 
                         Math.min((int)(c.getGreen()*(1/factor)), 255),
                         Math.min((int)(c.getBlue() *(1/factor)), 255) );
    }

    static Color darker(Color c, double factor) 
    {
       return new Color( Math.max((int)(c.getRed()  *factor), 0), 
                         Math.max((int)(c.getGreen()*factor), 0),
                         Math.max((int)(c.getBlue() *factor), 0) );
    }
}
