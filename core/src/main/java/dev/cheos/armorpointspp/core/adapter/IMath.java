package dev.cheos.armorpointspp.core.adapter;

public interface IMath {
	public static final IMath INSTANCE = new IMath() { };
	
	default int ceil(float f) {
		int i = (int) f;
		return f > i ? i + 1 : i;
	}
	
	default long ceil(double d) {
		long l = (long) d;
		return d > l ? l + 1 : l;
	}
	
	default int floor(float f) {
		int i = (int) f;
		return f < i ? i - 1 : i;
	}
	
	default long floor(double d) {
		long l = (long) d;
		return d < l ? l -1 : l;
	}
	
	default int clamp(int i, int min, int max) {
		return i < min ? min : i > max ? max : i;
	}
}
