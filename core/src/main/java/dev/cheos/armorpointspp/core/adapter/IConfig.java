package dev.cheos.armorpointspp.core.adapter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import dev.cheos.armorpointspp.core.FrostbiteStyle;
import dev.cheos.armorpointspp.core.Suffix;

public interface IConfig {
	boolean               bool  (Option<Boolean> key);
	int                   hex   (Option<Integer> key);
	float                 dec   (Option<Float  > key);
	<T extends Enum<T>> T enm   (Option<T      > key);
	
	public static interface Option<T> {
		String key();
		String[] comments();
		T def();
		
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
			@SuppressWarnings("unchecked")
			public T getMin() {
				return (T) (this.num ? 0 : this.bool ? false : null);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public T getMax() {
				return (T) (this.num ? 0 : this.bool ? false : null);
			}
		}
	}
	
	public static interface BoundedOption<T> extends Option<T> {
		T getMin();
		T getMax();
	}
	
	public static enum BooleanOption implements Option<Boolean> {
		ABSORPTION_ENABLE    ("showAbsorption"         , true , " Show absorption as border around health"       , " Available: true, false [default: %s]"),
		ARMOR_ENABLE         ("enableArmorBar"         , true , " Enable custom armor bar"                       , " Available: true, false [default: %s]"),
		ARMOR_TEXT_ENABLE    ("showArmorValue"         , true , " Show armor value text next to armor bar"       , " Available: true, false [default: %s]"),
		ARMOR_SHOW_ON_0      ("showArmorWhenZero"      , false, " Show armor bar when armor is zero"             , " Available: true, false [default: %s]"),
		DEBUG                ("debug"                  , false, " You don't want this to be on. Believe me"      , " Available: true, false [default: %s]"),
		FROSTBITE_TEXT_ENABLE("showFrostbitePercentage", true , " Show frostbite percentage next to health bar"  , " Available: true, false [default: %s]"),
		HEALTH_ENABLE        ("enableHealthBar"        , true , " Enable custom health bar"                      , " Available: true, false [default: %s]"),
		HEALTH_TEXT_ENABLE   ("showHealthValue"        , true , " Show health value text next to health bar"     , " Available: true, false [default: %s]"),
		PROTECTION_ENABLE    ("showProtection"         , true , " Show protection as overlay over armor"         , " Available: true, false [default: %s]"),
		RESISTANCE_ENABLE    ("showResistance"         , true , " Show resistance as border around armor"        , " Available: true, false [default: %s]"),
		TEXT_SHADOW          ("textShadow"             , true , " Draw shadows for all rendered texts"           , " Available: true, false [default: %s]"),
		TOUTHNESS_ENABLE     ("showToughness"          , true , " Show toughness as overlay over armor"          , " Available: true, false [default: %s]");
		
		final String key;
		final boolean def;
		final List<String> comments;
		BooleanOption(String key, boolean def, String... comments) {
			this.key = key;
			this.def = def;
			this.comments = Arrays.stream(comments).map(s -> String.format(s, def)).collect(ImmutableList.toImmutableList());
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
	}
	
	public static enum IntegerOption implements Option<Integer> {
		TEXT_COLOR_FULL_RESISTANCE("resistanceFull", 0x4c0000, " Color when resistance > 5"                     , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ARMOR_0        ("armor0"        , 0x3d3d3d, " Color when armor = 0"                          , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ARMOR_LT25     ("armorLT25"     , 0x44ff11, " Color when armor < 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ARMOR_EQ25     ("armorEQ25"     , 0xff8811, " Color when armor = 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ARMOR_GT25     ("armorGT25"     , 0xff3311, " Color when armor > 25"                         , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_FROSTBITE      ("heartFrostbite", 0x01bef2, " Color when fully frozen"                       , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_POISON         ("heartPoison"   , 0x947818, " Color when poisoned"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_WITHER         ("heartWither"   , 0x2b2b2b, " Color when withered"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_HEART          ("heart"         , 0xff1313, " Color normal status"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_ABSORPTION     ("absorption"    , 0xffc300, " Color of absorption"                           , " Available: 0x000000 ~ 0xffffff [default: %s]"),
		TEXT_COLOR_SEPARATOR      ("separator"     , 0x3d3d3d, " Color of separator"                            , " Available: 0x000000 ~ 0xffffff [default: %s]");
		
		final String key;
		final int    def;
		final List<String> comments;
		IntegerOption(String key, int def, String... comments) {
			this.key = key;
			this.def = def;
			this.comments = Arrays.stream(comments).map(s -> String.format(s, String.format("%06x", def))).collect(ImmutableList.toImmutableList());
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
	}
	
	public static enum FloatOption implements BoundedOption<Float> {
		ABSORPTION_VALUE("absorption", 0.25F, 200, " Number of pixels to show an absorption of 1"   , " Available: %s ~ %s [default: %s]"),
		PROTECTION_VALUE("protection", 0.5F ,  10, " Number of icons to show a protection of 1"     , " Available: %s ~ %s [default: %s]"),
		RESISTANCE_VALUE("resistance", 0.25F,  10, " Number of icons to show a resistance of 1"     , " Available: %s ~ %s [default: %s]"),
		TOUGHNESS_VALUE ("toughness" , 2F   ,  10, " Number of icons to show a armor toughness of 1", " Available: %s ~ %s [default: %s]");
		
		final String key;
		final float def, min, max;
		final List<String> comments;
		FloatOption(String key, float def, float max, String... comments) { this(key, def, 0, max, comments); }
		FloatOption(String key, float def, float min, float max, String... comments) {
			this.key = key;
			this.def = def;
			this.min = min;
			this.max = max;
			this.comments = Arrays.stream(comments).map(s ->
					String.format(s,
							String.format("%0.3f", this.min),
							String.format("%0.3f", this.max),
							String.format("%0.3f", this.def))).collect(ImmutableList.toImmutableList());
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
		public Float getMin() {
			return this.min;
		}
		
		@Override
		public Float getMax() {
			return this.max;
		}
	}
	
	public static class EnumOption<T extends Enum<T>> implements Option<T> {
		public static final EnumOption<FrostbiteStyle>
				FROSTBITE_STYLE = new EnumOption<>("frostbiteStyle", FrostbiteStyle.ICON, " Frostbite heart display style (full = vanilla)", " Available: %s [default: %s]");
		public static final EnumOption<Suffix.Type>
				SUFFIX          = new EnumOption<>("suffix"        , Suffix.Type.SI     , " Suffix type used for displaying armor values"  , " Available: %s [default: %s]");
		
		final String key;
		final T def;
		final List<String> comments;
		@SuppressWarnings("unchecked")
		EnumOption(String key, T def, String... comments) {
			this.key = key;
			this.def = def;
			this.comments = Arrays.stream(comments).map(s -> String.format(s, formatOptions(def.getClass()), def.name())).collect(ImmutableList.toImmutableList());
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
		
		private static <T extends Enum<T>> String formatOptions(Class<T> clazz) {
			StringBuilder out = new StringBuilder();
			try {
				T[] values = getValues(clazz);
				if (values != null && values.length != 0)
					for (int i = 0; i < values.length; i++)
						out.append(i != 0 ? ", " : "").append(values[i].name());
				else out.append("<none>");
			} catch (Throwable t) { out.append("[!] <ERROR> [!]"); }
			return out.toString();
		}
		
		@SuppressWarnings("unchecked")
		private static <T extends Enum<T>> T[] getValues(Class<T> clazz) throws Exception {
			Method v = clazz.getDeclaredMethod("values");
			return (T[]) v.invoke(null);
		}
	}
}
