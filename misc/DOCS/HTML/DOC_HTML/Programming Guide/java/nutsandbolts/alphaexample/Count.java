class Count {
    public static void main(String args[]) {
        int count = 0;

        while (System.in.read() != -1)
            count++;
        System.out.println("Input has " + count + " chars.");
    }
}
