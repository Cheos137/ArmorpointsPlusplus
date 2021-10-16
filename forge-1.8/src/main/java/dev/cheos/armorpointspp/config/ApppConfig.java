package dev.cheos.armorpointspp.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.Suffix;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ApppConfig {
	private static final Map<String, Property> boolConfigs  = new HashMap<>();
	private static final Map<String, Property> hexConfigs   = new HashMap<>();
	private static final Map<String, Property> floatConfigs = new HashMap<>();
	private static Configuration cfg;
	private static Property suffix;

	public static void init(File file) {
		if (!boolConfigs.isEmpty()) return;
		
		cfg = new Configuration(file);
		cfg.load();
		
		boolConfigs  .put("debug"            , cfg.get("debug"         , "debug"            , false     , " You don't want this to be on. Believe me\n"      + " Available: true, false [default: false]"));
		boolConfigs  .put("enableArmorBar"   , cfg.get("general"       , "enableArmorBar"   , true      , " Enable custom armor bar\n"                       + " Available: true, false [default: true]"));
		boolConfigs  .put("enableHealthBar"  , cfg.get("general"       , "enableHealthBar"  , true      , " Enable custom health bar\n"                      + " Available: true, false [default: true]"));
		boolConfigs  .put("showArmorValue"   , cfg.get("general"       , "showArmorValue"   , true      , " Show armor value text next to bar\n"             + " Available: true, false [default: true]"));
		boolConfigs  .put("showHealthValue"  , cfg.get("general"       , "showHealthValue"  , true      , " Show health value text next to bar\n"            + " Available: true, false [default: true]"));
		boolConfigs  .put("showArmorWhenZero", cfg.get("general"       , "showArmorWhenZero", false     , " Show armor bar when armor is zero\n"             + " Available: true, false [default: false]"));
		boolConfigs  .put("showResistance"   , cfg.get("general"       , "showResistance"   , true      , " Show resistance as border around armor\n"        + " Available: true, false [default: true]"));
		boolConfigs  .put("showToughness"    , cfg.get("general"       , "showToughness"    , true      , " Show toughness as overlay over armor\n"          + " Available: true, false [default: true]"));
		boolConfigs  .put("showProtection"   , cfg.get("general"       , "showProtection"   , true      , " Show protection as overlay over armor\n"         + " Available: true, false [default: true]"));
		boolConfigs  .put("showAbsorption"   , cfg.get("general"       , "showAbsorption"   , true      , " Show absorption as border around health\n"       + " Available: true, false [default: true]"));
		hexConfigs   .put("resistanceFull"   , cfg.get("textcolors"    , "resistanceFull"   , "0x4c0000", " Color when resistance > 5\n"                     + " Available: 0x000000 ~ 0xffffff [default: 0x4c0000]"));
		hexConfigs   .put("armor0"           , cfg.get("textcolors"    , "armor0"           , "0x3d3d3d", " Color when armor = 0\n"                          + " Available: 0x000000 ~ 0xffffff [default: 0x3d3d3d]"));
		hexConfigs   .put("armorLT25"        , cfg.get("textcolors"    , "armorLT25"        , "0x44ff11", " Color when armor < 25\n"                         + " Available: 0x000000 ~ 0xffffff [default: 0x44ff11]"));
		hexConfigs   .put("armorEQ25"        , cfg.get("textcolors"    , "armorEQ25"        , "0xff8811", " Color when armor = 25\n"                         + " Available: 0x000000 ~ 0xffffff [default: 0xff8811]"));
		hexConfigs   .put("armorGT25"        , cfg.get("textcolors"    , "armorGT25"        , "0xff3311", " Color when armor > 25\n"                         + " Available: 0x000000 ~ 0xffffff [default: 0xff3311]"));
		hexConfigs   .put("heartPoison"      , cfg.get("textcolors"    , "heartPoison"      , "0x947818", " Color when poisoned\n"                           + " Available: 0x000000 ~ 0xffffff [default: 0x947818]"));
		hexConfigs   .put("heartWither"      , cfg.get("textcolors"    , "heartWither"      , "0x2b2b2b", " Color when withered\n"                           + " Available: 0x000000 ~ 0xffffff [default: 0x2b2b2b]"));
		hexConfigs   .put("heart"            , cfg.get("textcolors"    , "heart"            , "0xff1313", " Color normal status\n"                           + " Available: 0x000000 ~ 0xffffff [default: 0xff1313]"));
		hexConfigs   .put("absorption"       , cfg.get("textcolors"    , "absorption"       , "0xffc300", " Color of absorption\n"                           + " Available: 0x000000 ~ 0xffffff [default: 0xffc300]"));
		hexConfigs   .put("separator"        , cfg.get("textcolors"    , "separator"        , "0x3d3d3d", " Color of separator\n"                            + " Available: 0x000000 ~ 0xffffff [default: 0x3d3d3d]"));
		floatConfigs .put("resistance"       , cfg.get("representative", "resistance"       , 2         , " Number of icons to show a resistance of 1\n"     + " Available: 0 ~ 10 [default: 2]"    , 0,  10));
		floatConfigs .put("protection"       , cfg.get("representative", "protection"       , 0.5       , " Number of icons to show a protection of 1\n"     + " Available: 0 ~ 10 [default: 0.5]"  , 0,  10));
		floatConfigs .put("toughness"        , cfg.get("representative", "toughness"        , 0.5       , " Number of icons to show a armor toughness of 1\n"+ " Available: 0 ~ 10 [default: 0.5]"  , 0,  10));
		floatConfigs .put("absorption"       , cfg.get("representative", "absorption"       , 0.25      , " Number of pixels to show an absorption of 1\n"   + " Available: 0 ~ 200 [default: 0.25]", 0, 200));
		suffix = cfg.get("general", "suffix", Suffix.Type.SI.name(), "Suffix type used for displaying armor values\n Available: SI, SCI, GER, ENG [default: SI]");
		
		if(cfg.hasChanged()) cfg.save();
	}

	public static boolean getBool(String name) {
		return boolConfigs.containsKey(name) ? boolConfigs.get(name).getBoolean() : false;
	}

	public static int getHex(String name) {
		return hexConfigs.containsKey(name) ? fromHex(hexConfigs.get(name).getString()) : 0;
	}

	public static float getFloat(String name) {
		return floatConfigs.containsKey(name) ? (float) floatConfigs.get(name).getDouble() : 0;
	}

	public static Suffix.Type getSuffix() {
		return Suffix.Type.fromName(suffix.getString());
	}

	private static int fromHex(String hex) {
		return Integer.parseInt(hex.substring(2), 16);
	}
}
