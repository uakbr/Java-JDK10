/* Generic Applet to Application Frame
 * @(#)AppletFrame.java	1.4  02 Dec 1995 15:28:07
 * @author Kevin A. Smith
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
 *
 * Directions for use: 
 *
 * In order to convert an applet to an application you must provide
 * two things: A main() function in the class body of the applet
 * and an AWT Frame window for the application to exist in.  If your
 * applet requires parameters from the HTML Applet tag, you will need
 * to do additional work.  
 *
 * Here's a sample main() function that you can add to your applet class:
 *
 *  public static void main(String args[])
 *  {
 *     AppletFrame.startApplet( <ClassName>, "Application Title");
 *  }
 *
 * The class AppletFrame provides a simple AWT Frame window for running
 * applications.  
 *
 */

import java.awt.Frame;
import java.awt.Event;
import java.awt.Dimension;
import java.applet.Applet;

// Applet to Application Frame window
class AppletFrame extends Frame
{

    public static void startApplet(String className, 
                                   String title, 
                                   String args[])
    {
       // local variables
       Applet a;
       Dimension appletSize;

       try 
       {
          // create an instance of your applet class
          a = (Applet) Class.forName(className).newInstance();
       }
       catch (ClassNotFoundException e) { return; }
       catch (InstantiationException e) { return; }
       catch (IllegalAccessException e) { return; }

       // initialize the applet
       a.init();
       a.start();
  
       // create new application frame window
       AppletFrame f = new AppletFrame(title);
  
       // add applet to frame window
       f.add("Center", a);
  
       // resize frame window to fit applet
       // assumes that the applet sets its own size
       // otherwise, you should set a specific size here.
       appletSize =  a.size();
       f.pack();
       f.resize(appletSize);  

       // show the window
       f.show();
  
    }  // end startApplet()
  
  
    // constructor needed to pass window title to class Frame
    public AppletFrame(String name)
    {
       // call java.awt.Frame(String) constructor
       super(name);
    }

    // needed to allow window close
    public boolean handleEvent(Event e)
    {
       // Window Destroy event
       if (e.id == Event.WINDOW_DESTROY)
       {
          // exit the program
          System.exit(0);
          return true;
       }
       
       // it's good form to let the super class look at any 
       // unhandled events
       return super.handleEvent(e);

    }  // end handleEvent()

}   // end class AppletFrame






