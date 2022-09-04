package dev.cheos.armorpointspp.config;

import java.io.*;
import java.util.*;

import dev.cheos.armorpointspp.config.ApppConfigValue.*;
import dev.cheos.armorpointspp.core.adapter.IConfig;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.loader.api.FabricLoader;

public class ApppConfig implements IConfig {
	private static ApppConfig INSTANCE;
	private static ConfigTree tree;
	private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "armorpointspp.json");
	public static final Version VERSION = Version.v1_18;
	private static final Map<String, BoolValue>    boolConfigs   = new HashMap<>();
	private static final Map<String, HexValue>     hexConfigs    = new HashMap<>();
	private static final Map<String, FloatValue>   floatConfigs  = new HashMap<>();
	private static final Map<String, StringValue>  stringConfigs = new HashMap<>();
	private static final Map<String, EnumValue<?>> enumConfigs   = new HashMap<>();
	
	public static void init() {
		if (INSTANCE != null)
			return;
		
		define();
		
		new ConfigBuilder(ConfigTree.builder()); // config definitions + defaults
		load(); // load already saved values
		save(); // save everything that might have been added by defaulting, etc...
		
		INSTANCE = new ApppConfig();
	}
	
	public static void load() {
		if (configFile.isFile())
			try {
				FiberSerialization.deserialize(tree, new FileInputStream(configFile), new JanksonValueSerializer(false));
			} catch (ValueDeserializationException | IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void save() {
		try {
			if (!configFile.exists()) {
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
			}
			if (configFile.isDirectory())
				throw new IllegalStateException("config file must not be a directory: " + configFile.getAbsolutePath());
			FiberSerialization.serialize(tree, new FileOutputStream(configFile), new JanksonValueSerializer(false));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				hexConfigs   .put(opt.key(), new HexValue   (opt.key(), opt.def(), opt.comments()));
		for (FloatOption opt : FloatOption.values())
			if (opt.isAvailableIn(VERSION))
				floatConfigs .put(opt.key(), new FloatValue (opt.key(), opt.def(), opt.min(), opt.max(), opt.comments()));
		for (StringOption opt : StringOption.values())
			if (opt.isAvailableIn(VERSION))
				stringConfigs.put(opt.key(), new StringValue(opt.key(), opt.def(), opt.comments()));
		for (EnumOption<?> opt : EnumOption.values())
			enumConfigs.put(opt.key(), EnumValue.of(opt));
	}
	
	public static class ConfigBuilder {
		final Set<Category> done = new HashSet<>();
		
		public ConfigBuilder(ConfigTreeBuilder builder) { // note to self: this requires parent categories to have lower ordinals to work
			for (Category category : Category.values())
				configure(builder, category, 0);
			tree = builder.build();
		}
		
		private void configure(ConfigTreeBuilder builder, Category category, int currentDepth) {
			if (!this.done.add(category) || !category.hasOptions(VERSION))
				return;
			
			for (String node : category.getPath().subList(currentDepth, category.getPath().size()))
				builder = builder.fork(node);
			for (Option<?> option : category.getOptions())
				if (option.isAvailableIn(VERSION))
					findValue(option).define(builder); // if we can't find a value, this is a serious issue (either an error in the code or something else), which should crash on
			for (Category sub : category.getSubCategories())
				configure(builder, sub, category.getPath().size());
			for (int i = 0; i < category.getPath().size() - currentDepth; i++)
				builder = builder.finishBranch();
		}
	}
	
	public static ApppConfigValue<?, ?, ?> findValue(Option<?> option) {
		if (option instanceof BooleanOption)
			return boolConfigs  .get(option.key());
		if (option instanceof IntegerOption)
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
