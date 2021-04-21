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
		boolConfigs.put("debug"            , new BoolValue("debug"            , false   , " You don't want this on. Believe me"     , " Available: true, false [default: %s]"));
		boolConfigs.put("enableArmorBar"   , new BoolValue("enableArmorBar"   , true    , " Enable custom armor bar"                , " Available: true, false [default: %s]"));
		boolConfigs.put("enableHealthBar"  , new BoolValue("enableHealthBar"  , true    , " Enable custom health bar"               , " Available: true, false [default: %s]"));
		boolConfigs.put("showArmorValue"   , new BoolValue("showArmorValue"   , true    , " Show armor value next to bar"           , " Available: true, false [default: %s]"));
		boolConfigs.put("showHealthValue"  , new BoolValue("showHealthValue"  , true    , " Show health value next to bar"          , " Available: true, false [default: %s]"));
		boolConfigs.put("showArmorWhenZero", new BoolValue("showArmorWhenZero", false   , " Show armor bar when armor is zero"      , " Available: true, false [default: %s]"));
		boolConfigs.put("showResistance"   , new BoolValue("showResistance"   , true    , " Show resistance as border around armor" , " Available: true, false [default: %s]"));
		boolConfigs.put("showAbsorption"   , new BoolValue("showAbsorption"   , true    , " Show absorption as border around health", " Available: true, false [default: %s]"));
		hexConfigs .put("resistanceFull"   , new HexValue( "resistanceFull"   , 0x4c0000, " Color when resistance > 5"              , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("armor0"           , new HexValue( "armor0"           , 0x3d3d3d, " Color when armor = 0"                   , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("armorLT25"        , new HexValue( "armorLT25"        , 0x44ff11, " Color when armor < 25"                  , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("armorEQ25"        , new HexValue( "armorEQ25"        , 0xff8811, " Color when armor = 25"                  , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("armorGT25"        , new HexValue( "armorGT25"        , 0xff3311, " Color when armor > 25"                  , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("heartPoison"      , new HexValue( "heartPoison"      , 0x947818, " Color when poisoned"                    , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("heartWither"      , new HexValue( "heartWither"      , 0x2b2b2b, " Color when withered"                    , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("heart"            , new HexValue( "heart"            , 0xff1313, " Color normal status"                    , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("absorption"       , new HexValue( "absorption"       , 0xffc300, " Color of absorption"                    , " Available: 0x000000 - 0xffffff [default: %s]"));
		hexConfigs .put("separator"        , new HexValue( "separator"        , 0x3d3d3d, " Color of separator"                     , " Available: 0x000000 - 0xffffff [default: %s]"));
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
