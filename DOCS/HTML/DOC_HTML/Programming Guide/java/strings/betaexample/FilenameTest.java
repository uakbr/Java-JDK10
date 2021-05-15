class FilenameTest {
    public static void main(String args[]) {
	Filename myHomePage = new Filename("/home/mem/public_html/index.html", '/');
	System.out.println("Extension = " + myHomePage.extension());
	System.out.println("Filename = " + myHomePage.filename());
	System.out.println("Path = " + myHomePage.path());
    }
}
