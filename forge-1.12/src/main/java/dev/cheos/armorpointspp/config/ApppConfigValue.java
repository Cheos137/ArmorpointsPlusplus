package dev.cheos.armorpointspp.config;

import java.util.function.Supplier;

import dev.cheos.armorpointspp.Lazy;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public abstract class ApppConfigValue<T, U> {
	protected final String name;
	protected final String category;
	protected final String[] comments;
	protected final T def;
	protected Property confValue;
	protected Lazy<T> value;
	
	protected ApppConfigValue(String name, String category, T def, String... comments) {
		this.name = name;
		this.def  = def;
		this.category = category;
		this.comments = comments;
	}
	
	public abstract void define(Configuration config);
	protected abstract Supplier<T> getter();
	@SuppressWarnings("unchecked")
	public U get() { return (U) this.value.get(); }
	
	public void invalidate() { this.value = Lazy.of(getter()); }
	
	
	public static class HexValue extends ApppConfigValue<String, Integer> {
		public HexValue(String name, String category, Integer def, String... comments) { super(name, category, hex(def, 6), comments); }
		
		@Override
		public void define(Configuration config) {
			this.confValue = config.get(this.category, this.name, this.def, String.join("\n", this.comments));
			this.value = Lazy.of(getter());
		}
		
		@Override
		protected Supplier<String> getter() { return this.confValue::getString; }
		@Override
		public Integer get() { return fromHex(this.value.get()); }
		private static String hex(int i, int minlen) { return String.format("0x%0" + minlen + "x", i); }
		private static int fromHex(String hex) { return Integer.parseInt(hex.substring(2), 16); }
	}
	
	
	public static class BoolValue extends ApppConfigValue<Boolean, Boolean> {
		public BoolValue(String name, String category, Boolean def, String... comments) { super(name, category, def, comments); }
		
		@Override
		public void define(Configuration config) {
			this.confValue = config.get(this.category, this.name, this.def, String.join("\n", this.comments));
			this.value = Lazy.of(getter());
		}
		
		@Override
		protected Supplier<Boolean> getter() { return this.confValue::getBoolean; }
	}
	
	
	public static class StringValue extends ApppConfigValue<String, String> {
		public StringValue(String name, String category, String def, String... comments) { super(name, category, def, comments); }
		
		@Override
		public void define(Configuration config) {
			this.confValue = config.get(this.category, this.name, this.def, String.join("\n", this.comments));
			this.value = Lazy.of(getter());
		}
		
		@Override
		protected Supplier<String> getter() { return this.confValue::getString; }
	}
	
	
	public static class FloatValue extends ApppConfigValue<Double, Float> {
		private final float min, max;
		
		public FloatValue(String name, String category, float def, String... comments) { this(name, category, def, Float.MAX_VALUE, comments); }
		public FloatValue(String name, String category, float def, float max, String... comments) { this(name, category, def, 0, max, comments); }
		public FloatValue(String name, String category, float def, float min, float max, String... comments) {
			super(name, category, (double) MathHelper.clamp(def, min, max), comments);
			this.min = min;
			this.max = max;
		}
		

		@Override
		public void define(Configuration config) {
			this.confValue = config.get(this.category, this.name, this.def, String.join("\n", this.comments), this.min, this.max);
			this.value = Lazy.of(getter());
		}
		
		@Override
		protected Supplier<Double> getter() { return this.confValue::getDouble; }
		@Override
		public Float get() { return this.value.get().floatValue(); }
	}
	
	
	public static class EnumValue<T extends Enum<T>> extends ApppConfigValue<String, T> {
		private final Class<T> type;
		
		@SuppressWarnings("unchecked")
		public EnumValue(String name, String category, T def, String[] comments) {
			super(name, category, def.name(), comments);
			this.type = (Class<T>) def.getClass();
		}
		
		@Override
		public void define(Configuration config) {
			this.confValue = config.get(this.category, this.name, this.def, String.join("\n", this.comments));
			this.value = Lazy.of(getter());
		}
		
		@Override
		protected Supplier<String> getter() { return this.confValue::getString; }
		@Override
		public T get() { return Enum.valueOf(this.type, this.value.get()); }
		
		public static <T extends Enum<T>> EnumValue<T> of(EnumOption<T> opt) {
			return new EnumValue<>(opt.key(), opt.category().getPathJoined(), opt.def(), opt.comments());
		}
	}
}
