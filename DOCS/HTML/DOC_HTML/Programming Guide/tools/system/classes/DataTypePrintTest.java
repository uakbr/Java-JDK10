class DataTypePrintTest {
    public static void main(String args[]) {

	Thread ObjectData = new Thread();
	String StringData = "Java Mania";
	char CharArrayData[] = { 'a', 'b', 'c' };
	int IntegerData = 4;
	long LongData = Long.MIN_VALUE;
	float FloatData = Float.PI;
	double DoubleData = Double.MAX_VALUE;
	boolean BooleanData = true;

	System.out.println("object = " + ObjectData);
	System.out.println("string = " + StringData);
	System.out.println("character array = " + CharArrayData);
	System.out.println("integer = " + IntegerData);
	System.out.println("long = " + LongData);
	System.out.println("float = " + FloatData);
	System.out.println("double = " + DoubleData);
	System.out.println("boolean = " + BooleanData);
    }
}
