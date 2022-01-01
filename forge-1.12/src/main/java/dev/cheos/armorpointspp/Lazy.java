package dev.cheos.armorpointspp;

import java.util.function.Supplier;

public class Lazy<T> {
	private T value;
	private final Supplier<T> factory;
	
	private Lazy(Supplier<T> factory) {
		this.factory = factory;
	}
	
	public T get() {
		if (this.value == null)
			this.value = this.factory.get();
		return this.value;
	}
	
	public static <T> Lazy<T> of(Supplier<T> factory) {
		return new Lazy<>(factory);
	}
}
