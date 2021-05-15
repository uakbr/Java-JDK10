class SelfishRunner extends Thread {

    public int tick = 1;
    public int num;

    SelfishRunner(int num) {
	this.num = num;
    }

    public void run() {
	while (tick < 400000) {
	    tick++;
	    if ((tick % 50000) == 0) {
		System.out.println("Thread #" + num + ", tick = " + tick);
	    }
	}
    }
}
