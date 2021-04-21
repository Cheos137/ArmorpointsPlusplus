package dev.cheos.armorpointspp.config;

import dev.cheos.armorpointspp.Suffix;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class ApppConfigValue<T, U> {
	
	protected final String name;
	protected final String[] comments;
	protected final T def;
	protected ConfigValue<T> value;
	
	protected ApppConfigValue(String name, T def, String... comments) {
		this.name = name;
		this.def  = def;
		this.comments = new String[comments.length];
		
		for (int i = 0; i < comments.length; i++)
			this.comments[i] = String.format(comments[i], def);
	}
	
	public void define(ForgeConfigSpec.Builder builder) {
		builder.comment(this.comments);
		this.value = builder.define(name, def);
	}
	
	@SuppressWarnings("unchecked")
	public U get() { return (U) value.get(); }
	
	
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
	
	public static class SuffixTypeValue extends ApppConfigValue<String, Suffix.Type> {
		protected SuffixTypeValue(String name, Suffix.Type def, String... comments) { super(name, def.name(), comments); }

		@Override
		public Suffix.Type get() { return Suffix.Type.fromName(value.get()); }
	}
}
