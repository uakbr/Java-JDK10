/*
 * This file defines class OutputFile which implementa a write-only
 * output file abstraction. Like class InputFile, class OutputFile
 * subclasses of class File to obtain simple file and path name
 * functions.
 */

/**
 * This class defines a simple write-only file by extending class File.
 */
public
class OutputFile extends File {

    /**
     * Link in the native library that this class depends on. We
     * bind this class loading to the library loading by making the
     * System.loadLibrary() call part of the static initializer for
     * the class. That is, if the loadLibary() call fails this class
     * fails to load.
     */
    static {
	try {
            System.loadLibrary("file");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("can't find your library");
            System.exit(-1);
        }

    }

    /**
     * Holds the system dependent handle to the file resource.
     */
    protected int fd;

    /**
     * Constructor for the output file object. Initializes the
     * parent class with the path name.
     */
    public OutputFile(String path) {
	super(path);
    }

    /**
     * Attempts to open the file for writing. If the file does not
     * exist one is created. Returns TRUE on success and FALSE on 
     * failure.
     */
    public native boolean open();

    /**
     * Attempts to close the previously opened file. Has
     * no return value.
     */
    public native void close();

    /**
     * Writes some number of bytes to the opened file. Returns
     * the number of bytes written.
     */
    public native int write(byte b[], int len);
}
