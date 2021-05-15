class Producer extends Thread {
    private CubbyHole cubbyhole;
    private int number;

    public Producer(CubbyHole c, int number) {
	cubbyhole = c;
	this.number = number;
    }

    public void run() {
	for (int i = 0; i < 10; i++) {
	    cubbyhole.put(i);
	    System.out.println("Producer #" + this.number + " put: " + i);
	    try {
	        sleep((int)(Math.random() * 100));
	    } catch (InterruptedException e) {
	    }
	}
    }
}
