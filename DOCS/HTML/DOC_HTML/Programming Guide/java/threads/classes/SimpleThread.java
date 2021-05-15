class SimpleThread extends Thread {
    public SimpleThread(String str) {
	super(str);
    }
    public void run() {
	for (int i = 0; i < 10; i++) {
	    System.out.println(i + " " + getName());
	    sleep(1000);
	}
	System.out.println("DONE! " + getName());
    }
}
