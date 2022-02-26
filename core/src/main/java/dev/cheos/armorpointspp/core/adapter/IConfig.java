package dev.cheos.armorpointspp.core.adapter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import dev.cheos.armorpointspp.core.FrostbiteStyle;
import dev.cheos.armorpointspp.core.RenderableText.Alignment;
import dev.cheos.armorpointspp.core.Side;
import dev.cheos.armorpointspp.core.Suffix;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public interface IConfig { // use forges config update system... somehow
	public static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
	
	boolean               bool(Option<Boolean> key);
	int                   hex (Option<Integer> key);
	float                 dec (Option<Float  > key);
	String                str (Option<String > key);
	<T extends Enum<T>> T enm (Option<T      > key);
	void invalidateAll();
	
	public static enum Category {
		COMPAT("compatibility"),
		COMPAT_REP("compatibility.representative"),
		GENERAL("general"),
		GENERAL_DEBUG("general.debug"),
		REPRESENTATIVE("representative"),
		TEXT_COLOR("textcolors"),
		TEXT_CONFIG("textconfig");
		
		static {
			COMPAT.subCategories.add(COMPAT_REP);
			GENERAL.subCategories.add(GENERAL_DEBUG);
		}
		
		private final List<String> path;
		private final String pathJoined;
		private final Set<Category> subCategories = new HashSet<>();
		private final Set<Option<?>> options = new HashSet<>();
		
		Category(String path) {
			this.path = ImmutableList.copyOf(Arrays.asList(path.split("\\.")));
			this.pathJoined = path;
		}
		
		public List<String> getPath() {
			return this.path;
		}
		
		public String getPathJoined() {
			return this.pathJoined;
		}
		
		public Set<Option<?>> getOptions() {
			return ImmutableSet.copyOf(this.options);
		}
		
		public boolean hasOptions(Version version) {
			return !this.options.stream().noneMatch(o -> o.isAvailableIn(version));
		}
		
		public Set<Category> getSubCategories() {
			return ImmutableSet.copyOf(this.subCategories);
		}
	}
	
	public static enum Version {
		v1_12,
		v1_16,
		v1_17,
		v1_18,
		v1_18fabric;
		
		public static final ImmutableList<Version> ALL   = ImmutableList.copyOf(Version.values());
		public static final ImmutableList<Version> NONE  = ImmutableList.of();
		public static final ImmutableList<Version> V1_12 = ImmutableList.of(v1_12);
		public static final ImmutableList<Version> V1_16 = ImmutableList.of(v1_16);
		public static final ImmutableList<Version> V1_17 = ImmutableList.of(v1_17);
		public static final ImmutableList<Version> V1_18 = ImmutableList.of(v1_18, v1_18fabric);
	}
	
	public static interface Option<T> {
		String key();
		String[] comments();
		T def();
		Category category();
		boolean isAvailableIn(Version version);
		Class<T> type();
		
		default BoundedOption<T> asBounded() {
			return this instanceof BoundedOption<?> ? (BoundedOption<T>) this : new NullBoundedOptionWrapper<>(this);
		}
		
		static class NullBoundedOptionWrapper<T> implements BoundedOption<T> {
			private final Option<T> opt;
			private final boolean   num;
			private final boolean  bool;
			
			private NullBoundedOptionWrapper(Option<T> opt) {
				this.opt  = opt;
				this.num  = opt.def() instanceof Number;
				this.bool = opt.def() instanceof Boolean;
			}
			
			@Override
			public String key() {
				return this.opt.key();
			}
			
			@Override
			public String[] comments() {
				return this.opt.comments();
			}
			
			@Override
			public T def() {
				return this.opt.def();
			}
			
			@Override
			public Category category() {
				return this.opt.category();
			}
			
			@Override
			public boolean isAvailableIn(Version version) {
				return opt.isAvailableIn(version);
			}
			
			@Override
			public Class<T> type() {
				return this.opt.type();
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public T min() {
				return (T) (this.num ? 0 : this.bool ? false : null);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public T max() {
				return (T) (this.num ? 0 : this.bool ? false : null);
			}
		}
	}
	
	public static interface BoundedOption<T> extends Option<T> {
		T min();
		T max();
	}
	
	public static enum BooleanOption implements Option<Boolean> {
		ABSORPTION_ENABLE           ("showAbsorption"             , true , Category.GENERAL      , " Show absorption as border around health"             , " Available: true, false [default: %s]"),
		ARMOR_ENABLE                ("enableArmorBar"             , true , Category.GENERAL      , " Enable custom armor bar"                             , " Available: true, false [default: %s]"),
		ARMOR_TEXT_ENABLE           ("showArmorValue"             , true , Category.GENERAL      , " Show armor value text next to armor bar"             , " Available: true, false [default: %s]"),
		ARMOR_TEXT_CONFIG_ENABLE    ("enableArmorValueConfig"     , false, Category.TEXT_CONFIG  , " Enables custom armor value configuration"            , " Available: true, false [default: %s]"),
		ARMOR_SHOW_ON_0             ("showArmorWhenZero"          , false, Category.GENERAL      , " Show armor bar when armor is zero"                   , " Available: true, false [default: %s]"),
		DEBUG                       ("debug"                      , false, Category.GENERAL_DEBUG, " You don't want this to be on. Believe me"            , " Available: true, false [default: %s]"),
		FROSTBITE_TEXT_ENABLE       ("showFrostbitePercentage"    , true , Category.GENERAL      , " Show frostbite percentage next to health bar"        , " Available: true, false [default: %s]"),
		HEALTH_ENABLE               ("enableHealthBar"            , true , Category.GENERAL      , " Enable custom health bar"                            , " Available: true, false [default: %s]"),
		HEALTH_TEXT_ENABLE          ("showHealthValue"            , true , Category.GENERAL      , " Show health value text next to health bar"           , " Available: true, false [default: %s]"),
		HEALTH_TEXT_CONFIG_ENABLE   ("enableHealthValueConfig"    , false, Category.TEXT_CONFIG  , " Enables custom health value configuration"           , " Available: true, false [default: %s]"),
		MANTLE_COMPAT               ("mantle"                     , true , Category.COMPAT       , Version.V1_12, " Fixes mantle compatibility"           , " Available: true, false [default: %s]"),
		POTIONCORE_COMPAT           ("potioncore"                 , true , Category.COMPAT       , Version.V1_12, " Adds support for potioncore's effects and attributes", " Available: true, false [default: %s]"),
		PROTECTION_ENABLE           ("showProtection"             , true , Category.GENERAL      , " Show protection as overlay over armor"               , " Available: true, false [default: %s]"),
		RESISTANCE_ENABLE           ("showResistance"             , true , Category.GENERAL      , " Show resistance as border around armor"              , " Available: true, false [default: %s]"),
		TEXT_SHADOW                 ("textShadow"                 , true , Category.GENERAL      , " Draw shadows for all rendered texts"                 , " Available: true, false [default: %s]"),
		TOUGHNESS_BAR               ("useToughnessBar"            , false, Category.GENERAL      , " Show toughness as it's own bar"                      , " Available: true, false [default: %s]"),
		TOUGHNESS_ENABLE            ("enableToughness"            , true , Category.GENERAL      , " Show toughness as overlay over armor or it's own bar", " Available: true, false [default: %s]"),
		TOUGHNESS_SHOW_ON_0         ("showToughnessWhenZero"      , false, Category.GENERAL      , " Show toughness bar when toughness is zero"           , " Available: true, false [default: %s]"),
		TOUGHNESS_TEXT_ENABLE       ("showToughnessValue"         , true , Category.GENERAL      , " Show toughness value text next to toughness bar"     , " Available: true, false [default: %s]"),
		TOUGHNESS_TEXT_CONFIG_ENABLE("enableToughnessValueConfig" , false, Category.TEXT_CONFIG  , " Enables custom toughness value configuration"        , " Available: true, false [default: %s]");
		
		final String key;
		final boolean def;
		final Category category;
		final List<Version> versions;
		final List<String> comments;
		BooleanOption(String key, boolean def, Category category, String... comments) { this(key, def, category, Version.ALL, comments); }
		BooleanOption(String key, boolean def, Category category, List<Version> versions, String... comments) {
			this.key = key;
			this.def = def;
			this.category = category;
			this.versions = ImmutableList.copyOf(versions);
			this.comments = Arrays.stream(comments).map(s -> String.format(s, def)).collect(ImmutableList.toImmutableList());
			this.category.options.add(this);
		}
		
		@Override
		public String key() {
			return this.key;
		}
		
		@Override
		public String[] comments() {
			return this.comments.toArray(new String[0]);
		}
		
		@Override
		public Boolean def() {
			return this.def;
		}
		
		@Override
		public Category category() {
			return this.category;
		}
		
		@Override
		public boolean isAvailableIn(Version version) {
			return this.versions.contains(version);
		}
		
		@Override
		public Class<Boolean> type() {
			return Boolean.class;
		}
	}
	
	public static enum IntegerOption implements BoundedOption<Integer> {
		TEXT_COLOR_FULL_RESISTANCE("resistanceFull", 0x8a0f0f, Category.TEXT_COLOR, " Color when resistance > 5"                     , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ARMOR_0        ("armor0"        , 0x3d3d3d, Category.TEXT_COLOR, " Color when armor = 0"                          , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ARMOR_LT25     ("armorLT25"     , 0x44ff11, Category.TEXT_COLOR, " Color when armor < 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ARMOR_EQ25     ("armorEQ25"     , 0xff8811, Category.TEXT_COLOR, " Color when armor = 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ARMOR_GT25     ("armorGT25"     , 0xff3311, Category.TEXT_COLOR, " Color when armor > 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_FROSTBITE      ("heartFrostbite", 0x01bef2, Category.TEXT_COLOR, " Color when fully frozen"                       , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_POISON         ("heartPoison"   , 0x947818, Category.TEXT_COLOR, " Color when poisoned"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_WITHER         ("heartWither"   , 0x2b2b2b, Category.TEXT_COLOR, " Color when withered"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_HEART          ("heart"         , 0xff1313, Category.TEXT_COLOR, " Color normal status"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ABSORPTION     ("absorption"    , 0xffc300, Category.TEXT_COLOR, " Color of absorption"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_SEPARATOR      ("separator"     , 0x3d3d3d, Category.TEXT_COLOR, " Color of separator"                            , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_TOUGHNESS      ("toughness"     , 0xff8811, Category.TEXT_COLOR, " Color of toughness"                            , " Available: 0x000000 ~ 0xffffff [default: %s]");
		
		final String key;
		final int    def;
		final Category category;
		final List<Version> versions;
		final List<String> comments;
		IntegerOption(String key, int def, Category category, String... comments) { this(key, def, category, Version.ALL, comments); }
		IntegerOption(String key, int def, Category category, List<Version> versions, String... comments) {
			this.key = key;
			this.def = def;
			this.category = category;
			this.versions = versions;
			this.comments = Arrays.stream(comments).map(s -> String.format(s, String.format("%06x", def))).collect(ImmutableList.toImmutableList());
			this.category.options.add(this);
		}
		
		@Override
		public String key() {
			return this.key;
		}
		
		@Override
		public String[] comments() {
			return this.comments.toArray(new String[0]);
		}
		
		@Override
		public Integer def() {
			return this.def;
		}
		
		@Override
		public Integer min() {
			return 0;
		}
		
		@Override
		public Integer max() {
			return 0xffffff;
		}
		
		@Override
		public Category category() {
			return this.category;
		}
		
		@Override
		public boolean isAvailableIn(Version version) {
			return this.versions.contains(version);
		}
		
		@Override
		public Class<Integer> type() {
			return Integer.class;
		}
	}
	
	public static enum FloatOption implements BoundedOption<Float> {
		ABSORPTION_VALUE("absorption"     , 0.25F,   200, Category.REPRESENTATIVE, " Number of pixels to show an absorption of 1"              , " Available: %s ~ %s [default: %s]"),
		ARMOR_TEXT_X    ("armorValueX"    , 0F   , 32768, Category.TEXT_CONFIG   , " if enabled, custom X position of the armor value text"    , " Available: %s ~ %s [default: %s]"),
		ARMOR_TEXT_Y    ("armorValueY"    , 0F   , 32768, Category.TEXT_CONFIG   , " if enabled, custom Y position of the armor value text"    , " Available: %s ~ %s [default: %s]"),
		HEALTH_TEXT_X   ("healthValueX"   , 0F   , 32768, Category.TEXT_CONFIG   , " if enabled, custom X position of the health value text"   , " Available: %s ~ %s [default: %s]"),
		HEALTH_TEXT_Y   ("healthValueY"   , 0F   , 32768, Category.TEXT_CONFIG   , " if enabled, custom Y position of the health value text"   , " Available: %s ~ %s [default: %s]"),
		TOUGHNESS_TEXT_X("toughnessValueX", 0F   , 32768, Category.TEXT_CONFIG   , " if enabled, custom X position of the toughness value text", " Available: %s ~ %s [default: %s]"),
		TOUGHNESS_TEXT_Y("toughnessValueY", 0F   , 32768, Category.TEXT_CONFIG   , " if enabled, custom Y position of the toughness value text", " Available: %s ~ %s [default: %s]"),
		MAGIC_RES_VALUE ("magicResist"    , 2F   ,    20, Category.COMPAT_REP    , Version.V1_12, " Number of icons to show a magic resistance of 1", " Available: %s ~ %s [default: %s]"),
		PROTECTION_VALUE("protection"     , 0.5F ,    10, Category.REPRESENTATIVE, " Number of icons to show a protection of 1"                , " Available: %s ~ %s [default: %s]"),
		RESISTANCE_VALUE("resistance"     , 2F   ,    10, Category.REPRESENTATIVE, " Number of icons to show a resistance of 1"                , " Available: %s ~ %s [default: %s]"),
		@Deprecated // effectively disabled
		TOUGHNESS_VALUE ("toughness"      , 2F   ,    10, Category.REPRESENTATIVE, Version.NONE, " Number of icons to show a armor toughness of 1", " Available: %s ~ %s [default: %s]");
		
		final String key;
		final float def, min, max;
		final Category category;
		final List<Version> versions;
		final List<String> comments;
		FloatOption(String key, float def, float max, Category category, String... comments) { this(key, def, max, category, Version.ALL, comments); }
		FloatOption(String key, float def, float max, Category category, List<Version> versions, String... comments) { this(key, def, 0, max, category, versions, comments); }
		FloatOption(String key, float def, float min, float max, Category category, List<Version> versions, String... comments) {
			this.key = key;
			this.def = def;
			this.min = min;
			this.max = max;
			this.category = category;
			this.versions = versions;
			this.comments = Arrays.stream(comments).map(s ->
					String.format(s,
							FLOAT_FORMAT.format(this.min),
							FLOAT_FORMAT.format(this.max),
							FLOAT_FORMAT.format(this.def))).collect(ImmutableList.toImmutableList());
			this.category.options.add(this);
		}
		
		@Override
		public String key() {
			return this.key;
		}
		
		@Override
		public String[] comments() {
			return this.comments.toArray(new String[0]);
		}
		
		@Override
		public Float def() {
			return this.def;
		}
		
		@Override
		public Float min() {
			return this.min;
		}
		
		@Override
		public Float max() {
			return this.max;
		}
		
		@Override
		public Category category() {
			return this.category;
		}
		
		@Override
		public boolean isAvailableIn(Version version) {
			return this.versions.contains(version);
		}
		
		@Override
		public Class<Float> type() {
			return Float.class;
		}
	}
	
	public static enum StringOption implements Option<String> {
		TEXTURE_SHEET("textureSheet", "default", Category.GENERAL, " Sets the texture sheet used for rendering", " See https://github.com/Cheos137/ArmorpointsPlusplus/wiki/Custom-Texture-Sheets for more information", " Builtin: %s [default: %s]");
		
		final String key;
		final String def;
		final Category category;
		final List<Version> versions;
		final List<String> comments;
		StringOption(String key, String def, Category category, String... comments) { this(key, def, category, Version.ALL, comments); }
		StringOption(String key, String def, Category category, List<Version> versions, String... comments) {
			this.key = key;
			this.def = def;
			this.category = category;
			this.versions = versions;
			this.comments = Arrays.stream(comments).map(s -> String.format(s, formatBuiltinTexSheets(), def)).collect(ImmutableList.toImmutableList());
			this.category.options.add(this);
		}
		
		@Override
		public String key() {
			return this.key;
		}

		@Override
		public String[] comments() {
			return this.comments.toArray(new String[0]);
		}

		@Override
		public String def() {
			return this.def;
		}

		@Override
		public Category category() {
			return this.category;
		}
		
		@Override
		public boolean isAvailableIn(Version version) {
			return this.versions.contains(version);
		}

		@Override
		public Class<String> type() {
			return String.class;
		}
		
		private static String formatBuiltinTexSheets() {
			StringBuilder out = new StringBuilder();
			for (int i = 0; i < ITextureSheet.builtins.size(); i++)
				out.append(i != 0 ? ", " : "").append(ITextureSheet.sheets.inverse().get(ITextureSheet.builtins.get(i)));
			return out.toString();
		}
	}
	
	public static class EnumOption<T extends Enum<T>> implements Option<T> {
		public static final EnumOption<FrostbiteStyle>
				FROSTBITE_STYLE = new EnumOption<>("frostbiteStyle"                  , FrostbiteStyle.ICON, Category.GENERAL    , " Frostbite heart display style (full = vanilla)"          , " Available: %s [default: %s]");
		public static final EnumOption<Suffix.Type>
				SUFFIX          = new EnumOption<>("suffix"                          , Suffix.Type.SI     , Category.GENERAL    , " Suffix type used for displaying high numeric values"     , " Available: %s [default: %s]");
		public static final EnumOption<Alignment>
				ARMOR_TEXT_ALIGNMENT     = new EnumOption<>("armorValueAlignment"    , Alignment.RIGHT    , Category.TEXT_CONFIG, " if enabled, custom alignment of the armor value text"    , " Available: %s [default: %s]"),
				HEALTH_TEXT_ALIGNMENT    = new EnumOption<>("healthValueAlignment"   , Alignment.RIGHT    , Category.TEXT_CONFIG, " if enabled, custom alignment of the health value text"   , " Available: %s [default: %s]"),
				TOUGHNESS_TEXT_ALIGNMENT = new EnumOption<>("toughnessValueAlignment", Alignment.RIGHT    , Category.TEXT_CONFIG, " if enabled, custom alignment of the toughness value text", " Available: %s [default: %s]");
		public static final EnumOption<Side>
				TOUGHNESS_SIDE  = new EnumOption<>("toughnessSide"                   , Side.LEFT          , Category.GENERAL    , " Determines the side of the toughness bar"                , " Available: %s [default: %s]", " Only effective if useToughnessBar is set to true!");
		
		private static final List<EnumOption<?>> ALL = ImmutableList.copyOf(new EnumOption<?>[] {
			FROSTBITE_STYLE,
			SUFFIX,
			ARMOR_TEXT_ALIGNMENT,
			HEALTH_TEXT_ALIGNMENT,
			TOUGHNESS_TEXT_ALIGNMENT,
			TOUGHNESS_SIDE
		});
		
		final String key;
		final T def;
		final Category category;
		final List<Version> versions;
		final List<String> comments;
		final Class<T> type;
		
		EnumOption(String key, T def, Category category, String... comments) { this(key, def, category, Version.ALL, comments); }
		@SuppressWarnings("unchecked")
		EnumOption(String key, T def, Category category, List<Version> versions, String... comments) {
			this.key = key;
			this.def = def;
			this.category = category;
			this.versions = versions;
			this.comments = Arrays.stream(comments).map(s -> String.format(s, formatOptions(def.getClass()), def.name())).collect(ImmutableList.toImmutableList());
			this.type = (Class<T>) def.getClass();
			this.category.options.add(this);
		}
		
		@Override
		public String key() {
			return this.key;
		}
		
		@Override
		public String[] comments() {
			return this.comments.toArray(new String[0]);
		}
		
		@Override
		public T def() {
			return this.def;
		}
		
		@Override
		public Category category() {
			return this.category;
		}
		
		@Override
		public boolean isAvailableIn(Version version) {
			return this.versions.contains(version);
		}
		
		@Override
		public Class<T> type() {
			return this.type;
		}
		
		public static Iterable<EnumOption<?>> values() {
			return ALL;
		}
		
		private static <T extends Enum<T>> String formatOptions(Class<T> clazz) {
			StringBuilder out = new StringBuilder();
			try {
				T[] values = clazz.getEnumConstants();
				if (values != null && values.length != 0)
					for (int i = 0; i < values.length; i++)
						out.append(i != 0 ? ", " : "").append(values[i].name());
				else out.append("<none>");
			} catch (Throwable t) { out.append("[!] <ERROR> [!]"); }
			return out.toString();
		}
	}
}
