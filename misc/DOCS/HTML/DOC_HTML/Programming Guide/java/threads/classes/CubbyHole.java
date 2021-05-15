class CubbyHole {
    private int seq;         // this is the condition variable.
    private boolean available = false;

    public synchronized int get() {
	while (available == false) {
	    wait();
	}
	available = false;
	return seq;
    }

    public synchronized void put(int value) {
	seq = value;
	available = true;
	notify();
    }
}
