package dev.cheos.armorpointspp.config;

import java.math.BigDecimal;

import dev.cheos.armorpointspp.Lazy;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.BooleanSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.StringSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import net.minecraft.util.Mth;

public abstract class ApppConfigValue<T, U, X> {
	protected final String name;
	protected final String[] comments;
	protected final T def;
	protected Lazy<X> value;
	
	protected ApppConfigValue(String name, T def, String... comments) {
		this.name = name;
		this.def  = def;
		this.comments = comments;
	}
	
	public void define(ConfigTreeBuilder builder) {
		builder.beginValue(this.name, configType(), this.def)
			   .withComment(String.join("\n", this.comments))
			   .finishValue(leaf -> {
				   this.value = Lazy.of(leaf::getValue);
				   leaf.addChangeListener((old, upd) -> value.invalidate());
			   });
	}
	
	@SuppressWarnings("unchecked")
	public U get() { return (U) this.value.get(); }
	protected abstract ConfigType<T, X, ? extends SerializableType<X>> configType();
	
	
	public static class HexValue extends ApppConfigValue<String, Integer, String> {
		public HexValue(String name, Integer def, String... comments) { super(name, hex(def, 6), comments); }
		
		@Override
		public Integer get() { return fromHex(this.value.get()); }
		private static String hex(int i, int minlen) { return String.format("0x%0" + minlen + "x", i); }
		private static int fromHex(String hex) { return Integer.parseInt(hex.substring(2), 16); }
		
		@Override protected ConfigType<String, String, StringSerializableType> configType() { return ConfigTypes.STRING; }
	}
	
	
	public static class BoolValue extends ApppConfigValue<Boolean, Boolean, Boolean> {
		public BoolValue(String name, Boolean def, String... comments) { super(name, def, comments); }
		
		@Override protected ConfigType<Boolean, Boolean, BooleanSerializableType> configType() { return ConfigTypes.BOOLEAN; }
	}
	
	
	public static class StringValue extends ApppConfigValue<String, String, String> {
		public StringValue(String name, String def, String... comments) { super(name, def, comments); }
		
		@Override protected ConfigType<String, String, StringSerializableType> configType() { return ConfigTypes.STRING; }
	}
	
	
	public static class FloatValue extends ApppConfigValue<Double, Float, BigDecimal> {
		private final float min, max;
		
		public FloatValue(String name, float def, String... comments) { this(name, def, Float.MAX_VALUE, comments); }
		public FloatValue(String name, float def, float max, String... comments) { this(name, def, 0, max, comments); }
		public FloatValue(String name, float def, float min, float max, String... comments) {
			super(name, (double) Mth.clamp(def, min, max), comments);
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void define(ConfigTreeBuilder builder) {
			
			builder.beginValue(this.name, configType(), this.def)
			   .withComment(String.join("\n", this.comments))
			   .finishValue(leaf -> {
				   this.value = Lazy.of(leaf::getValue);
				   leaf.addChangeListener((old, upd) -> value.invalidate());
			   });
		}
		
		@Override public Float get() { return Mth.clamp(this.value.get().floatValue(), this.min, this.max); }
		@Override protected ConfigType<Double, BigDecimal, DecimalSerializableType> configType() { return ConfigTypes.DOUBLE; }
	}
	
	
	public static class EnumValue<T extends Enum<T>> extends ApppConfigValue<String, T, String> {
		private final Class<T> type;
		
		@SuppressWarnings("unchecked")
		public EnumValue(String name, T def, String[] comments) {
			super(name, def.name(), comments);
			this.type = (Class<T>) def.getClass();
		}
		
		@Override public T get() { return Enum.valueOf(this.type, this.value.get()); }
		@Override protected ConfigType<String, String, StringSerializableType> configType() { return ConfigTypes.STRING; }
		
		public static <T extends Enum<T>> EnumValue<T> of(EnumOption<T> opt) {
			return new EnumValue<>(opt.key(), opt.def(), opt.comments());
		}
	}
}
