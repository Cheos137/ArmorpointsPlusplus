package dev.cheos.armorpointspp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import dev.cheos.armorpointspp.config.ApppConfigValue.*;
import dev.cheos.armorpointspp.core.adapter.IConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ApppConfig implements IConfig {
	private static ApppConfig INSTANCE;
	private static final Version VERSION = Version.v1_20;
	private static final Map<String, BoolValue>    boolConfigs   = new HashMap<>();
	private static final Map<String, IntValue>     intConfigs    = new HashMap<>();
	private static final Map<String, HexValue>     hexConfigs    = new HashMap<>();
	private static final Map<String, FloatValue>   floatConfigs  = new HashMap<>();
	private static final Map<String, StringValue>  stringConfigs = new HashMap<>();
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
		if (INSTANCE == null) init();
		return INSTANCE;
	}
	
	@Override
	public boolean bool(Option<Boolean> key) {
		return boolConfigs.containsKey(key.key()) ? boolConfigs.get(key.key()).get() : key.def();
	}
	
	@Override
	public int num(Option<Integer> key) {
		return intConfigs.containsKey(key.key()) ? intConfigs.get(key.key()).get() : key.def();
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
	public String str(Option<String> key) {
		return stringConfigs.containsKey(key.key()) ? stringConfigs.get(key.key()).get() : key.def();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T enm(Option<T> key) {
		return enumConfigs.containsKey(key.key()) ? (T) enumConfigs.get(key.key()).get() : key.def();
	}
	
	@Override
	@Deprecated
	public void invalidateAll() {
//		  boolConfigs.values().forEach(ApppConfigValue::invalidate);
//		   intConfigs.values().forEach(ApppConfigValue::invalidate);
//		   hexConfigs.values().forEach(ApppConfigValue::invalidate);
//		 floatConfigs.values().forEach(ApppConfigValue::invalidate);
//		stringConfigs.values().forEach(ApppConfigValue::invalidate);
//		  enumConfigs.values().forEach(ApppConfigValue::invalidate);
	}
	
	private static void define() {
		for (BooleanOption opt : BooleanOption.values())
			if (opt.isAvailableIn(VERSION))
				boolConfigs  .put(opt.key(), new BoolValue  (opt.key(), opt.def(), opt.comments()));
		for (IntegerOption opt : IntegerOption.values())
			if (opt.isAvailableIn(VERSION))
				intConfigs   .put(opt.key(), new IntValue   (opt.key(), opt.def(), opt.comments()));
		for (HexOption opt : HexOption.values())
			if (opt.isAvailableIn(VERSION))
				hexConfigs   .put(opt.key(), new HexValue   (opt.key(), opt.def(), opt.comments()));
		for (FloatOption opt : FloatOption.values())
			if (opt.isAvailableIn(VERSION))
				floatConfigs .put(opt.key(), new FloatValue (opt.key(), opt.def(), opt.min(), opt.max(), opt.comments()));
		for (StringOption opt : StringOption.values())
			if (opt.isAvailableIn(VERSION))
				stringConfigs.put(opt.key(), new StringValue(opt.key(), opt.def(), opt.comments()));
		for (EnumOption<?> opt : EnumOption.values())
			if (opt.isAvailableIn(VERSION))
				enumConfigs.put(opt.key(), EnumValue.of(opt));
	}
	
	public static class ConfigBuilder {
		public ConfigBuilder(ForgeConfigSpec.Builder builder) {
			for (Category category : Category.values()) {
				builder.push(category.getPath());
				for (Option<?> option : category.getOptions())
					if (option.isAvailableIn(VERSION))
						findValue(option).define(builder); // if we can't find a value, this is a serious issue (either an error in the code or something else), which should crash
				builder.pop(category.getPath().size());
			}
		}
	}
	
	private static ApppConfigValue<?, ?> findValue(Option<?> option) {
		if (option instanceof BooleanOption)
			return boolConfigs  .get(option.key());
		if (option instanceof IntegerOption)
			return intConfigs   .get(option.key());
		if (option instanceof HexOption)
			return hexConfigs   .get(option.key());
		if (option instanceof FloatOption)
			return floatConfigs .get(option.key());
		if (option instanceof StringOption)
			return stringConfigs.get(option.key());
		if (option instanceof EnumOption<?>)
			return enumConfigs  .get(option.key());
		return null;
	}
}
