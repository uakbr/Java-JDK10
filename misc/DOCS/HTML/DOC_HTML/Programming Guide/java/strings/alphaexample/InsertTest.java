class InsertTest {
    public static void main(String args[]) {
	StringBuffer sb = new StringBuffer("Drink Java");
	sb.insert(sb.length(), "Hot ");
	System.out.println(sb.toString());
    }
}
