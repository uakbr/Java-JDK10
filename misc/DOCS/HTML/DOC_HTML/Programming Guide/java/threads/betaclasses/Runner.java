class Runner extends Thread {

    public int tick = 1;

    public void run() {
	while (tick < 400000) {
	    tick++;
	}
    }
}
