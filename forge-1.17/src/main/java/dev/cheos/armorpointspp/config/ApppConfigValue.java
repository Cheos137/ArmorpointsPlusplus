package dev.cheos.armorpointspp.config;

import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.util.Lazy;

public abstract class ApppConfigValue<T, U> {
	protected final String name;
	protected final String[] comments;
	protected final T def;
	protected ConfigValue<T> confValue;
	protected Lazy<T> value;
	
	protected ApppConfigValue(String name, T def, String... comments) {
		this.name = name;
		this.def  = def;
		this.comments = comments;
	}
	
	public void define(ForgeConfigSpec.Builder builder) {
		builder.comment(this.comments);
		this.confValue = builder.define(this.name, this.def);
		this.value = Lazy.of(this.confValue::get);
	}
	
	@SuppressWarnings("unchecked")
	public U get() { return (U) this.value.get(); }
	
	
	public static class HexValue extends ApppConfigValue<String, Integer> {
		public HexValue(String name, Integer def, String... comments) { super(name, hex(def, 6), comments); }
		
		@Override
		public Integer get() { return fromHex(this.value.get()); }
		private static String hex(int i, int minlen) { return String.format("0x%0" + minlen + "x", i); }
		private static int fromHex(String hex) { return Integer.parseInt(hex.substring(2), 16); }
	}
	
	
	public static class BoolValue extends ApppConfigValue<Boolean, Boolean> {
		public BoolValue(String name, Boolean def, String... comments) { super(name, def, comments); }
	}
	
	
	public static class StringValue extends ApppConfigValue<String, String> {
		public StringValue(String name, String def, String... comments) { super(name, def, comments); }
	}
	
	
	public static class FloatValue extends ApppConfigValue<Double, Float> {
		private final float min, max;
		
		public FloatValue(String name, float def, String... comments) { this(name, def, Float.MAX_VALUE, comments); }
		public FloatValue(String name, float def, float max, String... comments) { this(name, def, 0, max, comments); }
		public FloatValue(String name, float def, float min, float max, String... comments) {
			super(name, (double) Mth.clamp(def, min, max), comments);
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void define(ForgeConfigSpec.Builder builder) {
			builder.comment(this.comments);
			this.confValue = builder.defineInRange(this.name, this.def, this.min, this.max);
			this.value = Lazy.of(this.confValue::get);
		}
		
		@Override
		public Float get() {
			return this.value.get().floatValue();
		}
	}
	
	
	public static class EnumValue<T extends Enum<T>> extends ApppConfigValue<String, T> {
		private final Class<T> type;
		
		@SuppressWarnings("unchecked")
		public EnumValue(String name, T def, String[] comments) {
			super(name, def.name(), comments);
			this.type = (Class<T>) def.getClass();
		}
		
		@Override
		public void define(ForgeConfigSpec.Builder builder) {
			builder.comment(this.comments);
			this.confValue = builder.define(this.name, this.def);
			this.value = Lazy.of(this.confValue::get);
		}
		
		@Override
		public T get() { return Enum.valueOf(this.type, this.value.get()); }
	}
}
