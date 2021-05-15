/*
 * The File class defined here is loosely based on the File classes
 * provided as part of the Java base io classes. This File class is
 * an abstract superclass that provides basic file and path manipulation
 * with the expectation that subclasses will provide the actual file
 * management code depending on the file semantics they want to present.
 * For example, read-only input files and read and write output files.
 */

/**
 * The File superclass defines an interface for manipulating path
 * and file names.  
 *
 */
public
class File {

    /**
     * The file path.
     */
    protected String path;

    /**
     * The class File's notion of a path separator character. This
     * will be the Java path separator. Note that this will be
     * converted to the system dependent path separator at runtime
     * by code in the native library.
     */
    public static final char separatorChar = ':';

    /**
     * The constructor initializes the class with the given path. Note
     * that we use the String class found in the Java core classes.
     *
     */
    public File(String path) {
	if (path == null) {
	    throw new NullPointerException();
	}
	this.path = path;
    }	

    /**
     * Get the name of the file, not including the directory path.
     */
    public String getFileName() {
	int index = path.lastIndexOf(separatorChar);
	return (index < 0) ? path : path.substring(index + 1);
    }

    /**
     * Get the name of the file including the full directory path.
     */
    public String getPath() {
	return path;
    }
}
