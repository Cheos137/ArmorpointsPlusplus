package dev.cheos.armorpointspp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import dev.cheos.armorpointspp.config.ApppConfigValue.BoolValue;
import dev.cheos.armorpointspp.config.ApppConfigValue.EnumValue;
import dev.cheos.armorpointspp.config.ApppConfigValue.FloatValue;
import dev.cheos.armorpointspp.config.ApppConfigValue.HexValue;
import dev.cheos.armorpointspp.core.adapter.IConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ApppConfig implements IConfig {
	private static ApppConfig INSTANCE;
	private static final Map<String, BoolValue>    boolConfigs   = new HashMap<>();
	private static final Map<String, HexValue>     hexConfigs    = new HashMap<>();
	private static final Map<String, FloatValue>   floatConfigs  = new HashMap<>();
	private static final Map<String, EnumValue<?>> enumConfigs   = new HashMap<>();
	
	public static void init() {
		if (INSTANCE != null)
			return;
		
		define();
		Pair<ConfigBuilder, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigBuilder::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
		INSTANCE = new ApppConfig();
	}
	
	public static IConfig instance() {
		return INSTANCE;
	}
	
	@Override
	public boolean bool(Option<Boolean> key) {
		return boolConfigs.containsKey(key.key()) ? boolConfigs.get(key.key()).get() : key.def();
	}
	
	@Override
	public int hex(Option<Integer> key) {
		return hexConfigs.containsKey(key.key()) ? hexConfigs.get(key.key()).get() : key.def();
	}
	
	@Override
	public float dec(Option<Float> key) {
		return floatConfigs.containsKey(key.key()) ? floatConfigs.get(key.key()).get() : key.def();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T enm(Option<T> key) {
		return enumConfigs.containsKey(key.key()) ? (T) enumConfigs.get(key.key()).get() : key.def();
	}
	
	private static void define() {
		for (BooleanOption opt : BooleanOption.values())
			boolConfigs .put(opt.key(), new BoolValue  (opt.key(), opt.def(), opt.comments()));
		for (IntegerOption opt : IntegerOption.values())
			hexConfigs  .put(opt.key(), new HexValue   (opt.key(), opt.def(), opt.comments()));
		for (FloatOption opt : FloatOption.values())
			floatConfigs.put(opt.key(), new FloatValue (opt.key(), opt.def(), opt.min(), opt.max(), opt.comments()));
		enumConfigs.put(EnumOption.FROSTBITE_STYLE.key(), new EnumValue<>(EnumOption.FROSTBITE_STYLE.key(), EnumOption.FROSTBITE_STYLE.def(), EnumOption.FROSTBITE_STYLE.comments()));
		enumConfigs.put(EnumOption.SUFFIX.key(), new EnumValue<>(EnumOption.SUFFIX.key(), EnumOption.SUFFIX.def(), EnumOption.SUFFIX.comments()));
	}
	
	public static class ConfigBuilder {
		public ConfigBuilder(ForgeConfigSpec.Builder builder) {
			builder.push("general");
			builder.push("debug");
			ApppConfig.boolConfigs.get("debug").define(builder);
			builder.pop();
			ApppConfig.boolConfigs.values().forEach(v -> { if (!"debug".equals(v.name)) v.define(builder); });
			ApppConfig.enumConfigs.values().forEach(v -> v.define(builder));
			builder.pop();
			builder.push("representative");
			ApppConfig.floatConfigs.values().forEach(v -> v.define(builder));
			builder.pop();
			builder.push("textcolors");
			ApppConfig.hexConfigs.values().forEach(v -> v.define(builder));
			builder.pop();
		}
	}
}
