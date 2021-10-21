package dev.cheos.armorpointspp.core.render;

public class Components { // TODO each component should be able to trust that their caller handles all the pre-render setup (blend settings, etc.) and texture binding
	public static final        ArmorComponent ARMOR         = new        ArmorComponent();
	public static final VanillaArmorComponent VANILLA_ARMOR = new VanillaArmorComponent();
	public static final   ResistanceComponent RESISTANCE    = new   ResistanceComponent();
	public static final   ProtectionComponent PROTECTION    = new   ProtectionComponent();
	public static final    ToughnessComponent TOUGHNESS     = new    ToughnessComponent();
	public static final       HealthComponent HEALTH        = new       HealthComponent();
	public static final   AbsorptionComponent ABSOPRTION    = new   AbsorptionComponent();
	public static final    ArmorTextComponent ARMOR_TEXT    = new    ArmorTextComponent();
	public static final   HealthTextComponent HEALTH_TEXT   = new   HealthTextComponent();
	
	public static final        DebugComponent DEBUG         = new        DebugComponent();
	public static final    DebugTextComponent DEBUG_TEXT    = new    DebugTextComponent();
}
