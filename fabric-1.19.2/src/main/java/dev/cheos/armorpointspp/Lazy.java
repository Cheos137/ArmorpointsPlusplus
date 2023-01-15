package dev.cheos.armorpointspp;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
	private Supplier<T> supplier;
    private T instance;
    
    private Lazy(Supplier<T> supplier) {
    	if (supplier == null) throw new NullPointerException("supplier is null");
        this.supplier = supplier;
    }
    
    @Override
	public final T get() {
        if (this.instance == null)
            this.instance = supplier.get();
        return this.instance;
    }
    
    public void invalidate() {
    	this.instance = null;
    }
	
	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}
}
