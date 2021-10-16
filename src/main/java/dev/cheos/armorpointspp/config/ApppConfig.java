package dev.cheos.armorpointspp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.Suffix;
import net.minecraftforge.common.config.Config;

@Config(modid = Armorpointspp.MODID, name = "general")
public class ApppConfig {
	@Config.Ignore
	private static final Map<String, Supplier<Boolean>>  boolValues = new HashMap<>();
	@Config.Ignore
	private static final Map<String, Supplier<Integer>>   hexValues = new HashMap<>();
	@Config.Ignore
	private static final Map<String, Supplier<Float>>   floatValues = new HashMap<>();
	
	@Config.Name("enableArmorBar")
	@Config.Comment({ "Enable custom armor bar"                , "Available: true, false [default: true]" })
	public static boolean enableArmorBar    = true;
	@Config.Name("enableHealthBar")
	@Config.Comment({ "Enable custom health bar"               , "Available: true, false [default: true]" })
	public static boolean enableHealthBar   = true;
	@Config.Name("showArmorValue")
	@Config.Comment({ "Show armor value text next to bar"      , "Available: true, false [default: true]" })
	public static boolean showArmorValue    = true;
	@Config.Name("showHealthValue")
	@Config.Comment({ "Show health value text next to bar"     , "Available: true, false [default: true]" })
	public static boolean showHealthValue   = true;
	@Config.Name("showArmorWhenZero")
	@Config.Comment({ "Show armor bar when armor is zero"      , "Available: true, false [default: false]" })
	public static boolean showArmorWhenZero = false;
	@Config.Name("showResistance")
	@Config.Comment({ "Show resistance as border around armor" , "Available: true, false [default: true]" })
	public static boolean showResistance    = true;
	@Config.Name("showToughness")
	@Config.Comment({ "Show toughness as overlay over armor"   , "Available: true, false [default: true]" })
	public static boolean showToughness     = true;
	@Config.Name("showProtection")
	@Config.Comment({ "Show protection as overlay over armor"  , "Available: true, false [default: true]" })
	public static boolean showProtection    = true;
	@Config.Name("showAbsorption")
	@Config.Comment({ "Show absorption as border around health", "Available: true, false [default: true]" })
	public static boolean showAbsorption    = true;
	@Config.Name("suffix")
	@Config.Comment({ "Suffix type used for displaying armor values", "Available: SI, SCI, GER, ENG [default: SI]" })
	public static String suffix = Suffix.Type.SI.name();
	
	public static void init() {
		if (!boolValues.isEmpty()) return;
		
		boolValues.put("debug"            , () -> ApppConfigDebug.debug       );
		boolValues.put("enableArmorBar"   , () -> ApppConfig.enableArmorBar   );
		boolValues.put("enableHealthBar"  , () -> ApppConfig.enableHealthBar  );
		boolValues.put("showArmorValue"   , () -> ApppConfig.showArmorValue   );
		boolValues.put("showHealthValue"  , () -> ApppConfig.showHealthValue  );
		boolValues.put("showArmorWhenZero", () -> ApppConfig.showArmorWhenZero);
		boolValues.put("showResistance"   , () -> ApppConfig.showResistance   );
		boolValues.put("showToughness"    , () -> ApppConfig.showToughness    );
		boolValues.put("showProtection"   , () -> ApppConfig.showProtection   );
		boolValues.put("showAbsorption"   , () -> ApppConfig.showAbsorption   );
		boolValues.put("mantle"           , () -> ApppConfigCompat.mantle     );
		
		hexValues.put("resistanceFull", () -> fromHex(ApppConfigTextCol.resistanceFull));
		hexValues.put("armor0"        , () -> fromHex(ApppConfigTextCol.armor0        ));
		hexValues.put("armorLT25"     , () -> fromHex(ApppConfigTextCol.armorLT25     ));
		hexValues.put("armorEQ25"     , () -> fromHex(ApppConfigTextCol.armorEQ25     ));
		hexValues.put("armorGT25"     , () -> fromHex(ApppConfigTextCol.armorGT25     ));
		hexValues.put("heartPoison"   , () -> fromHex(ApppConfigTextCol.heartPoison   ));
		hexValues.put("heartWither"   , () -> fromHex(ApppConfigTextCol.heartWither   ));
		hexValues.put("heart"         , () -> fromHex(ApppConfigTextCol.heart         ));
		hexValues.put("absorption"    , () -> fromHex(ApppConfigTextCol.absorption    ));
		hexValues.put("separator"     , () -> fromHex(ApppConfigTextCol.separator     ));
		
		floatValues.put("resistance", () -> (float) ApppConfigRep.resistance);
		floatValues.put("protection", () -> (float) ApppConfigRep.protection);
		floatValues.put("toughness" , () -> (float) ApppConfigRep.toughness );
		floatValues.put("absorption", () -> (float) ApppConfigRep.absorption);
	}
	
	public static boolean getBool(String name) {
		return boolValues.containsKey(name) ? boolValues.get(name).get() : false;
	}
	
	public static int getHex(String name) {
		return hexValues.containsKey(name) ? hexValues.get(name).get() : 0;
	}
	
	public static float getFloat(String name) {
		return floatValues.containsKey(name) ? floatValues.get(name).get() : 0;
	}
	
	public static Suffix.Type getSuffix() {
		return Suffix.Type.fromName(suffix);
	}
	
	private static int fromHex(String hex) {
		return Integer.parseInt(hex.substring(2), 16);
	}
	
	@Config(modid = Armorpointspp.MODID, name = "representative", category = "representative")
	public static class ApppConfigRep {
		@Config.Name("resistance")
		@Config.Comment({ "Number of icons to show a resistance of 1"     , "Available: 0.0 ~ 10.0 [default: 2.0]" })
		@Config.RangeDouble(min = 0, max =  10)
		@Config.SlidingOption
		public static double resistance = 2;
		@Config.Name("protection")
		@Config.Comment({ "Number of icons to show a protection of 1"     , "Available: 0.0 ~ 10.0 [default: 0.5]" })
		@Config.RangeDouble(min = 0, max =  10)
		@Config.SlidingOption
		public static double protection = 0.5;
		@Config.Name("toughness")
		@Config.Comment({ "Number of icons to show a armor toughness of 1", "Available: 0.0 ~ 10.0 [default: 0.5]" })
		@Config.RangeDouble(min = 0, max =  10)
		@Config.SlidingOption
		public static double toughness = 0.5;
		@Config.Name("absorption")
		@Config.Comment({ "Number of pixels to show an absorption of 1"   , "Available: 0.0 ~ 200.0 [default: 0.25]" })
		@Config.RangeDouble(min = 0, max = 200)
		@Config.SlidingOption
		public static double absorption = 0.25;
	}
	
	@Config(modid = Armorpointspp.MODID, name = "textcolors", category = "textcolors")
	public static class ApppConfigTextCol {
		@Config.Name("resistanceFull")
		@Config.Comment({ "Color when resistance > 5", "Available: 0x000000 ~ 0xffffff [default: 0x4c0000]" })
		public static String resistanceFull = "0x4c0000";
		@Config.Name("armor0")
		@Config.Comment({ "Color when armor = 0"     , "Available: 0x000000 ~ 0xffffff [default: 0x3d3d3d]" })
		public static String armor0 = "0x3d3d3d";
		@Config.Name("armorLT25")
		@Config.Comment({ "Color when armor < 25"    , "Available: 0x000000 ~ 0xffffff [default: 0x44ff11]" })
		public static String armorLT25 = "0x44ff11";
		@Config.Name("armorEQ25")
		@Config.Comment({ "Color when armor = 25"    , "Available: 0x000000 ~ 0xffffff [default: 0xff8811]" })
		public static String armorEQ25 = "0xff8811";
		@Config.Name("armorGT25")
		@Config.Comment({ "Color when armor > 25"    , "Available: 0x000000 ~ 0xffffff [default: 0xff3311]" })
		public static String armorGT25 = "0xff3311";
		@Config.Name("heartPoison")
		@Config.Comment({ "Color when poisoned"      , "Available: 0x000000 ~ 0xffffff [default: 0x947818]" })
		public static String heartPoison = "0x947818";
		@Config.Name("heartWither")
		@Config.Comment({ "Color when withered"      , "Available: 0x000000 ~ 0xffffff [default: 0x2b2b2b]" })
		public static String heartWither = "0x2b2b2b";
		@Config.Name("heart")
		@Config.Comment({ "Color normal status"      , "Available: 0x000000 ~ 0xffffff [default: 0xff1313]" })
		public static String heart = "0xff1313";
		@Config.Name("absorption")
		@Config.Comment({ "Color of absorption"      , "Available: 0x000000 ~ 0xffffff [default: 0xffc300]" })
		public static String absorption = "0xffc300";
		@Config.Name("separator")
		@Config.Comment({ "Color of separator"       , "Available: 0x000000 ~ 0xffffff [default: 0x3d3d3d]" })
		public static String separator = "0x3d3d3d";
	}
	
	@Config(modid = Armorpointspp.MODID, name = "compatibility", category = "compatibility")
	public static class ApppConfigCompat {
		@Config.Name("mantle")
		@Config.Comment({ "If enabled, fixes compatibilty issues with mantle by disabling mantles heart renderer", "Available: true, false [default: false]" })
		@Config.RequiresMcRestart
		public static boolean mantle = true;
	}
	
	@Config(modid = Armorpointspp.MODID, name = "debug", category = "debug")
	public static class ApppConfigDebug {
		@Config.Name("debug")
		@Config.Comment({ "You don't want this to be on. Believe me", "Available: true, false [default: false]" })
		public static boolean debug = false;
	}
}
