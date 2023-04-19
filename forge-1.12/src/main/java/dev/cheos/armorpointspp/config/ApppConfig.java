package dev.cheos.armorpointspp.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.config.ApppConfigValue.*;
import dev.cheos.armorpointspp.core.adapter.IConfig;
import net.minecraftforge.common.config.Configuration;

public class ApppConfig implements IConfig { // TODO: config gui?
	private static ApppConfig INSTANCE;
	private static final Version VERSION = Version.v1_12;
	private static final Map<String, BoolValue>    boolConfigs   = new HashMap<>();
	private static final Map<String, IntValue>     intConfigs    = new HashMap<>();
	private static final Map<String, HexValue>     hexConfigs    = new HashMap<>();
	private static final Map<String, FloatValue>   floatConfigs  = new HashMap<>();
	private static final Map<String, StringValue>  stringConfigs = new HashMap<>();
	private static final Map<String, EnumValue<?>> enumConfigs   = new HashMap<>();
	
	private final Configuration config;
	
	private ApppConfig(File file) {
		this.config = new Configuration(file, "3");
		
		  boolConfigs.values().forEach(v -> v.define(this.config));
		   intConfigs.values().forEach(v -> v.define(this.config));
		   hexConfigs.values().forEach(v -> v.define(this.config));
		 floatConfigs.values().forEach(v -> v.define(this.config));
		stringConfigs.values().forEach(v -> v.define(this.config));
		  enumConfigs.values().forEach(v -> v.define(this.config));
		this.config.save();
	}
	
	public static void init(File file) {
		if (INSTANCE != null)
			return;
		
		define();
		INSTANCE = new ApppConfig(file);
	}
	
	public static IConfig instance() {
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
	public void invalidateAll() {
		INSTANCE.config.load();
		  boolConfigs.values().forEach(ApppConfigValue::invalidate);
		   intConfigs.values().forEach(ApppConfigValue::invalidate);
		   hexConfigs.values().forEach(ApppConfigValue::invalidate);
		 floatConfigs.values().forEach(ApppConfigValue::invalidate);
		stringConfigs.values().forEach(ApppConfigValue::invalidate);
		  enumConfigs.values().forEach(ApppConfigValue::invalidate);
	}
	
	private static void define() {
		for (BooleanOption opt : BooleanOption.values())
			if (opt.isAvailableIn(VERSION))
				boolConfigs  .put(opt.key(), new BoolValue  (opt.key(), opt.category().getPathJoined(), opt.def(), opt.comments()));
		for (IntegerOption opt : IntegerOption.values())
			if (opt.isAvailableIn(VERSION))
				intConfigs   .put(opt.key(), new IntValue   (opt.key(), opt.category().getPathJoined(), opt.def(), opt.comments()));
		for (HexOption opt : HexOption.values())
			if (opt.isAvailableIn(VERSION))
				hexConfigs   .put(opt.key(), new HexValue   (opt.key(), opt.category().getPathJoined(), opt.def(), opt.comments()));
		for (FloatOption opt : FloatOption.values())
			if (opt.isAvailableIn(VERSION))
				floatConfigs .put(opt.key(), new FloatValue (opt.key(), opt.category().getPathJoined(), opt.def(), opt.min(), opt.max(), opt.comments()));
		for (StringOption opt : StringOption.values())
			if (opt.isAvailableIn(VERSION))
				stringConfigs.put(opt.key(), new StringValue(opt.key(), opt.category().getPathJoined(), opt.def(), opt.comments()));
		for (EnumOption<?> opt : EnumOption.values())
			if (opt.isAvailableIn(VERSION))
				enumConfigs.put(opt.key(), EnumValue.of(opt));
	}
}
