package dev.cheos.armorpointspp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import dev.cheos.armorpointspp.Suffix;
import dev.cheos.armorpointspp.config.ApppConfigValue.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ApppConfig {
	private static final Map<String, BoolValue>   boolConfigs   = new HashMap<>();
	private static final Map<String, HexValue>    hexConfigs    = new HashMap<>();
	private static final Map<String, StringValue> stringConfigs = new HashMap<>();
	private static final Map<String, FloatValue>  floatConfigs  = new HashMap<>();
	private static final SuffixTypeValue suffixConfig = new SuffixTypeValue("suffix", Suffix.Type.SI, " Suffix type used for displaying armor values", " Available: SI, SCI, GER, ENG [default: %s]");
	
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
	
	public static String getString(String name) {
		return stringConfigs.containsKey(name) ? stringConfigs.get(name).get() : "";
	}
	
	public static float getFloat(String name) {
		return floatConfigs.containsKey(name) ? floatConfigs.get(name).get() : 0;
	}
	
	public static Suffix.Type getSuffix() {
		return suffixConfig.get();
	}
	
	private static void define() {
		boolConfigs  .put("debug"                  , new BoolValue  ("debug"                  , false   ,      " You don't want this to be on. Believe me"      , " Available: true, false [default: %s]"));
		boolConfigs  .put("enableArmorBar"         , new BoolValue  ("enableArmorBar"         , true    ,      " Enable custom armor bar"                       , " Available: true, false [default: %s]"));
		boolConfigs  .put("enableHealthBar"        , new BoolValue  ("enableHealthBar"        , true    ,      " Enable custom health bar"                      , " Available: true, false [default: %s]"));
		boolConfigs  .put("showArmorValue"         , new BoolValue  ("showArmorValue"         , true    ,      " Show armor value text next to health bar"      , " Available: true, false [default: %s]"));
		boolConfigs  .put("showHealthValue"        , new BoolValue  ("showHealthValue"        , true    ,      " Show health value text next to health bar"     , " Available: true, false [default: %s]"));
		boolConfigs  .put("showArmorWhenZero"      , new BoolValue  ("showArmorWhenZero"      , false   ,      " Show armor bar when armor is zero"             , " Available: true, false [default: %s]"));
		boolConfigs  .put("showResistance"         , new BoolValue  ("showResistance"         , true    ,      " Show resistance as border around armor"        , " Available: true, false [default: %s]"));
		boolConfigs  .put("showToughness"          , new BoolValue  ("showToughness"          , true    ,      " Show toughness as overlay over armor"          , " Available: true, false [default: %s]"));
		boolConfigs  .put("showProtection"         , new BoolValue  ("showProtection"         , true    ,      " Show protection as overlay over armor"         , " Available: true, false [default: %s]"));
		boolConfigs  .put("showAbsorption"         , new BoolValue  ("showAbsorption"         , true    ,      " Show absorption as border around health"       , " Available: true, false [default: %s]"));
		boolConfigs  .put("showFrostbitePercentage", new BoolValue  ("showFrostbitePercentage", true    ,      " Show frostbite percentage next to health bar"  , " Available: true, false [default: %s]"));
		hexConfigs   .put("resistanceFull"         , new HexValue   ("resistanceFull"         , 0x4c0000,      " Color when resistance > 5"                     , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("armor0"                 , new HexValue   ("armor0"                 , 0x3d3d3d,      " Color when armor = 0"                          , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("armorLT25"              , new HexValue   ("armorLT25"              , 0x44ff11,      " Color when armor < 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("armorEQ25"              , new HexValue   ("armorEQ25"              , 0xff8811,      " Color when armor = 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("armorGT25"              , new HexValue   ("armorGT25"              , 0xff3311,      " Color when armor > 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("heartFrostbite"         , new HexValue   ("heartFrostbite"         , 0x01bef2,      " Color when fully frozen"                       , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("heartPoison"            , new HexValue   ("heartPoison"            , 0x947818,      " Color when poisoned"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("heartWither"            , new HexValue   ("heartWither"            , 0x2b2b2b,      " Color when withered"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("heart"                  , new HexValue   ("heart"                  , 0xff1313,      " Color normal status"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("absorption"             , new HexValue   ("absorption"             , 0xffc300,      " Color of absorption"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		hexConfigs   .put("separator"              , new HexValue   ("separator"              , 0x3d3d3d,      " Color of separator"                            , " Available: 0x000000 ~ 0xffffff [default: %s]"));
		stringConfigs.put("frostbiteStyle"         , new StringValue("frostbiteStyle"         , "ICON"  ,      " Frostbite heart display style (full = vanilla)", " Available: FULL, OVERLAY, ICON [default: %s]"));
		floatConfigs .put("resistance"             , new FloatValue ("resistance"             , 2       ,  10, " Number of icons to show a resistance of 1"     , " Available: %s ~ %s [default: %s]"));
		floatConfigs .put("protection"             , new FloatValue ("protection"             , 0.5F    ,  10, " Number of icons to show a protection of 1"     , " Available: %s ~ %s [default: %s]"));
		floatConfigs .put("toughness"              , new FloatValue ("toughness"              , 0.5F    ,  10, " Number of icons to show a armor toughness of 1", " Available: %s ~ %s [default: %s]"));
		floatConfigs .put("absorption"             , new FloatValue ("absorption"             , 0.25F   , 200, " Number of pixels to show an absorption of 1"   , " Available: %s ~ %s [default: %s]"));
	}
	
	public static class ConfigBuilder {
		public ConfigBuilder(ForgeConfigSpec.Builder builder) {
			builder.push("general");
			builder.push("debug");
			ApppConfig.boolConfigs.get("debug").define(builder);
			builder.pop();
			ApppConfig.boolConfigs.values().forEach(v -> { if (!"debug".equals(v.name)) v.define(builder); });
			ApppConfig.stringConfigs.values().forEach(v -> v.define(builder));
			ApppConfig.suffixConfig.define(builder);
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
