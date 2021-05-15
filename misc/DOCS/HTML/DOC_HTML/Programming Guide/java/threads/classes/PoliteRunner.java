class PoliteRunner extends Thread {

    public int tick = 1;
    public int num;

    PoliteRunner(int num) {
	this.num = num;
    }

    public void run() {
	while (tick < 400000) {
	    tick++;
	    if ((tick % 50000) == 0) {
		System.out.println("Thread #" + num + ", tick = " + tick);
		yield();
	    }
	}
    }
}
