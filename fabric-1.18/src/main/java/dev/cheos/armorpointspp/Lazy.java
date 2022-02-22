package dev.cheos.armorpointspp;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
	private Supplier<T> supplier;
    private T instance;
    private boolean set;
    
    private Lazy(Supplier<T> supplier) {
    	if (supplier == null) throw new NullPointerException("supplier is null");
        this.supplier = supplier;
    }
    
    @Override
	public final T get() {
        if (!set) {
            instance = supplier.get();
            set = true;
        }
        return instance;
    }
    
    public void invalidate() {
    	set = false;
    }
	
	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}
}
