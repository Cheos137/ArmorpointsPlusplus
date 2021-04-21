package dev.cheos.armorpointspp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import dev.cheos.armorpointspp.Suffix;
import dev.cheos.armorpointspp.config.ApppConfigValue.BoolValue;
import dev.cheos.armorpointspp.config.ApppConfigValue.HexValue;
import dev.cheos.armorpointspp.config.ApppConfigValue.SuffixTypeValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ApppConfig {
	private static final Map<String, BoolValue> boolConfigs = new HashMap<>();
	private static final Map<String, HexValue>  hexConfigs  = new HashMap<>();
	private static final SuffixTypeValue suffixConfig = new SuffixTypeValue("suffix", Suffix.Type.SI, "Available: SI, SCI, GER, ENG [default: %s]");
	
	public static void init() {
		if (boolConfigs.isEmpty()) define();
		Pair<ConfigBuilder, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigBuilder::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
	}
	
	public static boolean getBool(String name) {
		return boolConfigs.containsKey(name) ? boolConfigs.get(name).get() : false;
	}
	
	public static int getHex(String name) {
		return hexConfigs.containsKey(name) ? hexConfigs.get(name).get() : 0;
	}
	
	public static Suffix.Type getSuffix() {
		return suffixConfig.get();
	}
	
	private static void define() {
		boolConfigs.put("debug"            , new BoolValue("debug"                                  , false   , "Available: true, false [default: %s]"));
		boolConfigs.put("enableArmorBar"   , new BoolValue("Enable custom armor bar"                , true    , "Available: true, false [default: %s]"));
		boolConfigs.put("enableHealthBar"  , new BoolValue("Enable custom health bar"               , true    , "Available: true, false [default: %s]"));
		boolConfigs.put("showArmorValue"   , new BoolValue("Show armor value next to bar"           , true    , "Available: true, false [default: %s]"));
		boolConfigs.put("showHealthValue"  , new BoolValue("Show health value next to bar"          , true    , "Available: true, false [default: %s]"));
		boolConfigs.put("showArmorWhenZero", new BoolValue("Show armor bar when armor is zero"      , false   , "Available: true, false [default: %s]"));
		boolConfigs.put("showResistance"   , new BoolValue("Show resistance as border around armor" , true    , "Available: true, false [default: %s]"));
		boolConfigs.put("showAbsorption"   , new BoolValue("Show absorption as border around health", true    , "Available: true, false [default: %s]"));
		hexConfigs .put("resistanceFull"   , new HexValue( "Color when resistance > 5"              , 0x4c0000, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("armor0"           , new HexValue( "Color when armor = 0"                   , 0x3d3d3d, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("armorLT25"        , new HexValue( "Color when armor < 25"                  , 0x44ff11, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("armorEQ25"        , new HexValue( "Color when armor = 25"                  , 0xff8811, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("armorGT25"        , new HexValue( "Color when armor > 25"                  , 0xff3311, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("heartPoison"      , new HexValue( "Color when poisoned"                    , 0x947818, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("heartWither"      , new HexValue( "Color when withered"                    , 0x2b2b2b, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("heart"            , new HexValue( "Color normal status"                    , 0xff1313, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("absorption"       , new HexValue( "Color separator"                        , 0xffc300, "Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("separator"        , new HexValue( "Color absorption"                       , 0x3d3d3d, "Available: 0x000000 - 0xffffff [default: %s]"));
	}
	
	public static class ConfigBuilder {
		public ConfigBuilder(ForgeConfigSpec.Builder builder) {
			builder.push("general");
			ApppConfig.boolConfigs.values().forEach(v -> v.define(builder));
			ApppConfig.suffixConfig.define(builder);
			builder.pop();
			builder.push("textcolors");
			ApppConfig.hexConfigs.values().forEach(v -> v.define(builder));
			builder.pop();
		}
	}
}
