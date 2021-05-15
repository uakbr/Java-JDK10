class Filename {
    String fullpath;
    char pathseparator;

    Filename(String str, char sep) {
	fullpath = str;
	pathseparator = sep;
    }

    String extension() {
	int dot = fullpath.lastIndexOf('.');
	return fullpath.substring(dot + 1);
    }

    String filename() {
	int dot = fullpath.lastIndexOf('.');
	int sep = fullpath.lastIndexOf(pathseparator);
	return fullpath.substring(sep + 1, dot);
    }

    String path() {
	int sep = fullpath.lastIndexOf(pathseparator);
	return fullpath.substring(0, sep);
    }
}
