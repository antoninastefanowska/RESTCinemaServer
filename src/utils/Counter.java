package utils;

public class Counter {
	private int value;
	
	public Counter() {
		value = 0;
	}
	
	public int nextValue() {
		return value++;
	}
}
