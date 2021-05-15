class HelloWorld {
    public native void displayHelloWorld();

    static {
	System.loadLibrary("hello");
    }
}
