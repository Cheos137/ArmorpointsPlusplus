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

public class ApppConfig implements IConfig { // TODO: reload config on world restart
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
		enumConfigs.put(EnumOption.ARMOR_TEXT_ALIGNMENT.key(), new EnumValue<>(EnumOption.ARMOR_TEXT_ALIGNMENT.key(), EnumOption.ARMOR_TEXT_ALIGNMENT.def(), EnumOption.ARMOR_TEXT_ALIGNMENT.comments()));
		enumConfigs.put(EnumOption.HEALTH_TEXT_ALIGNMENT.key(), new EnumValue<>(EnumOption.HEALTH_TEXT_ALIGNMENT.key(), EnumOption.HEALTH_TEXT_ALIGNMENT.def(), EnumOption.HEALTH_TEXT_ALIGNMENT.comments()));
	}
	
	public static class ConfigBuilder {
		public ConfigBuilder(ForgeConfigSpec.Builder builder) {
			for (Category category : Category.values()) {
				builder.push(category.getPath());
				for (Option<?> option : category.getOptions())
					findValue(option).define(builder); // if we can't find a value, this is a serious issue (either an error in the code or something else), which should crash
				builder.pop(category.getPath().size());
			}
		}
	}
	
	private static ApppConfigValue<?, ?> findValue(Option<?> option) {
		if (option instanceof BooleanOption)
			return boolConfigs .get(option.key());
		if (option instanceof IntegerOption)
			return hexConfigs  .get(option.key());
		if (option instanceof FloatOption)
			return floatConfigs.get(option.key());
		if (option instanceof EnumOption<?>)
			return enumConfigs .get(option.key());
		return null;
	}
}
