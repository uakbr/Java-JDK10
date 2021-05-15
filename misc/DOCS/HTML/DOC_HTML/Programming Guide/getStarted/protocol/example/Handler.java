
package net.www.protocol.run;

import java.io.*;
import net.www.html.*;

/**
 *
 * Given a URL of the form 
 *
 *           run:classname 
 *
 * run a compiled java class
 * Currently the class must be in the CLASSPATH.
 *
 */

class Handler extends URLStreamHandler {

    public synchronized InputStream openStream(URL u) {

	    /* This function must return an input stream suitable
	     * for reading in the contents of the URL.
	     * So, set up a PipedOutputStream which is connected
	     * to a PipedInputStream. To make it easier to
	     * write to the PipedOutputStream, make it a PrintStream.
	     */
	PipedOutputStream ps = new PipedOutputStream();
	PipedInputStream is = new PipedInputStream(ps);
        PrintStream os = new PrintStream(ps);

	    /* Get the class name from the URL, and
	     * strip off any leading slashes.
	     */
	String className = u.file;
	String args;
	className = className.substring(className.lastIndexOf("/") + 1);

	    /* Get the args -- if any.
	     */
	if (className.indexOf("?") != 0) {
		className.replace(':', ' ');
	}

	    /* Force URL type to be HTML.
	     */
	u.setType(URL.content_html);

	    /* Print appropriate HTML to the PipedOutputStream.
	     */
 	os.println("<HTML>");
	os.print("<TITLE> Running Class: ");
	os.print(className);
	os.println(" </TITLE>");
	os.print("<BODY><APP CLASS=\"");
	os.print(className);
	os.println("\"></BODY>\n</HTML>\n");

	    /* Close the PipedOutputStream, so that the
	     * PipedInputStream can be read by the caller.
	     */
	os.close();

	    /* Return the PipedInputStream.
	     */
	return is;
    }

}
