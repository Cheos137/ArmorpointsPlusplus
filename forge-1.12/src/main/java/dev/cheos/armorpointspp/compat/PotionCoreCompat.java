package dev.cheos.armorpointspp.compat;

import com.tmtravlr.potioncore.PotionCoreAttributes;

import net.minecraft.client.Minecraft;

public class PotionCoreCompat {
	private static final Minecraft minecraft = Minecraft.getMinecraft();
	
	public static double resistance() {
		return minecraft.player.getEntityAttribute(PotionCoreAttributes.DAMAGE_RESISTANCE).getAttributeValue();
	}
	
	public static double magicShield() {
		return minecraft.player.getEntityAttribute(PotionCoreAttributes.MAGIC_SHIELDING).getAttributeValue();
	}
}
