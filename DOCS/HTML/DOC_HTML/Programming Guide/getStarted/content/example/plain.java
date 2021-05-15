/*
 * Plain text file handler
 */
package net.www.content.text;
import net.www.html.ContentHandler;
import net.www.html.URL;
import java.io.InputStream;
import awt.GifImage;

public class plain extends ContentHandler {
    public Object getContent(InputStream is, URL u) {
	StringBuffer sb = new StringBuffer();
	int c;

	while ((c = is.read()) >= 0) {
	    sb.appendChar(c);
	}
	sb.append("\n\n-- This closing message brought to you by your plain/text\n");
	sb.append("content handler. To remove this content handler, delete the\n");
	sb.append("net.www.content.text.plain class from your class path.\n");
	is.close();
	return sb.toString();
    }
}
