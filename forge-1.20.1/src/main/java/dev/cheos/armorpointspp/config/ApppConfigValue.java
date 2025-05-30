package dev.cheos.armorpointspp.config;

import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class ApppConfigValue<T, U> {
	protected final String name;
	protected final String[] comments;
	protected final T def;
	protected ForgeConfigSpec.ConfigValue<T> confValue;
//	protected Lazy<T> value;
	
	protected ApppConfigValue(String name, T def, String... comments) {
		this.name = name;
		this.def  = def;
		this.comments = comments;
	}
	
	public void define(ForgeConfigSpec.Builder builder) {
		builder.comment(this.comments);
		this.confValue = builder.define(this.name, this.def);
//		this.value = Lazy.of(this.confValue::get);
	}
	
//	public void invalidate() {
//		this.value = Lazy.of(this.confValue::get);
//	}
	
	@SuppressWarnings("unchecked")
	public U get() { return (U) this.confValue.get(); }
	
	
	public static class HexValue extends ApppConfigValue<String, Integer> {
		public HexValue(String name, Integer def, String... comments) { super(name, hex(def, 6), comments); }
		
		@Override
		public Integer get() { return fromHex(this.confValue.get()); }
		private static String hex(int i, int minlen) { return String.format("0x%0" + minlen + "x", i); }
		private static int fromHex(String hex) { return hex.startsWith("0x")
				? Integer.parseInt(hex.substring(2), 16)
				: hex.startsWith("#")
						? Integer.parseInt(hex.substring(1), 16)
						: Integer.parseInt(hex, 16); }
	}
	
	
	public static class IntValue extends ApppConfigValue<Integer, Integer> {
		private final int min, max;
		
		public IntValue(String name, int def, String... comments) { this(name, def, Integer.MAX_VALUE, comments); }
		public IntValue(String name, int def, int max, String... comments) { this(name, def, 0, max, comments); }
		public IntValue(String name, int def, int min, int max, String... comments) {
			super(name, Mth.clamp(def, min, max), comments);
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void define(ForgeConfigSpec.Builder builder) {
			builder.comment(this.comments);
			this.confValue = builder.defineInRange(this.name, this.def, this.min, this.max);
//			this.value = Lazy.of(this.confValue::get);
		}
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
//			this.value = Lazy.of(this.confValue::get);
		}
		
		@Override
		public Float get() {
			return this.confValue.get().floatValue();
		}
	}
	
	
	public static class EnumValue<T extends Enum<T>> extends ApppConfigValue<T, T> {
//		private final Class<T> type;
		
		public EnumValue(String name, T def, String[] comments) {
			super(name, def, comments);
//			this.type = (Class<T>) def.getClass();
		}
		
		@Override
		public void define(ForgeConfigSpec.Builder builder) {
			builder.comment(this.comments);
			this.confValue = builder.defineEnum(this.name, this.def);
//			this.value = Lazy.of(this.confValue::get);
		}
		
		@Override
		public T get() { return this.confValue.get(); }
		
		public static <T extends Enum<T>> EnumValue<T> of(EnumOption<T> opt) {
			return new EnumValue<>(opt.key(), opt.def(), opt.comments());
		}
	}
}
