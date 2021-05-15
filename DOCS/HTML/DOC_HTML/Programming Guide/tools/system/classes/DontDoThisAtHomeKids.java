import java.io.*;

class DontDoThisAtHomeKids {

    public static void main(String args[]) {
	String osname = System.getOSName();
	InputStream is = null;
	StringBuffer buf = new StringBuffer();
	int c;

	if (osname.equals("Solaris")) {
	    is = System.execin("pwd");
	} else if (osname.equals("Win32")) {
	    is = System.execin("cd");
	}
	if (is != null) {
	    while ((c = is.read()) != -1)
	        buf.appendChar((char)c);
	    System.out.print(buf.toString());
	}
    }
}
