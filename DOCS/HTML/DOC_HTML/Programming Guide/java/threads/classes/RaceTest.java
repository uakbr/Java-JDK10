class RaceTest {

    final static int NUMRUNNERS = 2;

    public static void main(String args[]) {

        SelfishRunner runners[] = new SelfishRunner[NUMRUNNERS];

	for (int i = 0; i < NUMRUNNERS; i++) {
	    runners[i] = new SelfishRunner(i);
	    runners[i].setPriority(2);
        }
	for (int i = 0; i < NUMRUNNERS; i++) {
	    runners[i].start();
	}
    }
}
