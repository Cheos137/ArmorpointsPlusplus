package dev.cheos.armorpointspp.render;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.Suffix;
import dev.cheos.armorpointspp.config.ApppConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("deprecation")
public class HUDRenderer {
	private static final ResourceLocation VANILLA_ICONS = new ResourceLocation(                     "textures/gui/icons.png");
	private static final ResourceLocation    APPP_ICONS = new ResourceLocation(Armorpointspp.MODID, "textures/gui/icons.png");
	private final Random random = new Random();
	private final Minecraft minecraft;
	private int lastHealth, displayHealth, lastGuiTicks;
	private long healthBlinkTime, lastHealthTime;
	private int[] lastHeartY = new int[10];
	
	HUDRenderer(Minecraft minecraft) {
		this.minecraft = minecraft;
	}
	
	public void renderArmor(MatrixStack mStack, int x, int y) {
		int armor = Math.min(armor(this.minecraft.player), 240);
		if (armor <= 0 && !ApppConfig.getBool("showArmorWhenZero")) return;
		
		bind(APPP_ICONS);
		for (int i = 0; i < 10; i++)
			blit(mStack, x + 8 * i, y,
					18 * (armor / 20)
					+ ((armor % 20) - 2 * i == 1
					? 36 : (armor % 20) - 2 * i >= 2
					? 45 : 27), 0, 9, 9);
		
		bind(VANILLA_ICONS);
	}
	
	public void renderResistance(MatrixStack mStack, int x, int y) {
		int armor = armor(this.minecraft.player);
		int resistance = -1;
		
		if (this.minecraft.player.hasEffect(Effects.DAMAGE_RESISTANCE)) resistance = this.minecraft.player.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier() + 1;
		if (resistance <= 0 || (armor <= 0 && !ApppConfig.getBool("showArmorWhenZero"))) return;
		bind(APPP_ICONS);
		for (int i = 0; i < 10 && i < 2 * resistance; i++, armor -= 2)
			if      (armor      <= 0) blit(mStack, x + 8 * i, y,  0, 0, 9, 9);
			else if (armor % 20 == 1) blit(mStack, x + 8 * i, y,  9, 0, 9, 9);
			else                      blit(mStack, x + 8 * i, y, 18, 0, 9, 9);
		bind(VANILLA_ICONS);
	}
	
	public void renderArmorToughness(MatrixStack mStack, int x, int y) {
		if (armor(this.minecraft.player) <= 0 && !ApppConfig.getBool("showArmorWhenZero")) return;
		
//		System.out.println(this.minecraft.player.getAttributes().save());
		
	}
	
	public void renderProtectionOverlay(MatrixStack mStack, int x, int y) {
		if (armor(this.minecraft.player) <= 0 && !ApppConfig.getBool("showArmorWhenZero")) return;

		int protection = 0;

		// should this be separated for each prot types? -- maxes out with godarmor
		for (ItemStack stack : this.minecraft.player.getArmorSlots()) { // adds 0 for empty stacks
			protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, stack);
			protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLAST_PROTECTION     , stack);
			protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_PROTECTION      , stack);
			protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, stack);
		}

		protection = MathHelper.ceil(protection / 2F);
		protection = MathHelper.clamp(protection, 0, 10);
		if (protection <= 0) return;
		
		RenderSystem.enableBlend();
		bind(APPP_ICONS);
		for (int i = 0; i < 10 && i < protection; i++)
			blit(mStack, x + 8 * i, y, 9, 9, 9, 9);
		bind(VANILLA_ICONS);
		RenderSystem.disableBlend();
	}
	
	public void renderHealth(MatrixStack mStack, int x, int y) {
		PlayerEntity player = this.minecraft.player;
		int health      = MathHelper.ceil(player.getHealth());
		int heartStack  = Math.min((health - 1) / 20, 10);
		
		this.lastGuiTicks = this.minecraft.gui.getGuiTicks();
		
		boolean highlight = this.healthBlinkTime > this.lastGuiTicks && (this.healthBlinkTime - this.lastGuiTicks) / 3L % 2L == 1L;
		int regen = this.minecraft.player.hasEffect(Effects.REGENERATION)
				? this.lastGuiTicks % 25 // in vanilla: % (maxHealth + 5), here: more than 20 + 5 does not make sense
				: -1;
		int margin = 36;
		
		if (player.level.getLevelData().isHardcore()) margin += 108;
		if (player.hasEffect(Effects.POISON)) margin += 36;
		else if (player.hasEffect(Effects.WITHER)) margin += 72;
		
		if (health < this.lastHealth && player.invulnerableTime > 0) {
			this.lastHealthTime = Util.getMillis();
			this.healthBlinkTime = this.lastGuiTicks + 20;
		} else if (health > this.lastHealth && player.invulnerableTime > 0) {
			this.lastHealthTime = Util.getMillis();
			this.healthBlinkTime = this.lastGuiTicks + 10;
		}
		
		if (Util.getMillis() - this.lastHealthTime > 1000L) {
			this.displayHealth = health;
			this.lastHealthTime = Util.getMillis();
		}
		
		this.lastHealth = health;
		this.random.setSeed(this.lastGuiTicks * 312871L);
		
		RenderSystem.enableBlend();
		bind(APPP_ICONS);
		
		for (int i = 9; i >= 0; i--) {
			int heartX = x + i * 8;
			int heartY = y;
			
			if (health <= 4) heartY += random.nextInt(2);
			if (i == regen) heartY -= 2;
			
			lastHeartY[i] = heartY;
			
			blit(mStack, heartX, heartY, highlight ? 18 : 0, 9, 9, 9); // draw background
			if (i * 2 + heartStack * 20 + 2 >  health && heartStack > 0) blit(mStack, heartX, heartY, margin, heartStack * 9, 9, 9); // part. draw row below

			if (highlight) {
				if      (i * 2 + heartStack * 20 + 1 <  this.displayHealth) blit(mStack, heartX, heartY, margin + 18, 9 + heartStack * 9, 9, 9); // full
				else if (i * 2 + heartStack * 20 + 1 == this.displayHealth) blit(mStack, heartX, heartY, margin + 27, 9 + heartStack * 9, 9, 9); // half
			}
			
			if      (i * 2 + heartStack * 20 + 1 <  health) blit(mStack, heartX, heartY, margin    , 9 + heartStack * 9, 9, 9); // full
			else if (i * 2 + heartStack * 20 + 1 == health) blit(mStack, heartX, heartY, margin + 9, 9 + heartStack * 9, 9, 9); // half
		}
		
		RenderSystem.disableBlend();
		bind(VANILLA_ICONS);
	}
	
	public void renderAbsorption(MatrixStack mStack, int x, int y) {
		PlayerEntity player = this.minecraft.player;
		int absorb = MathHelper.ceil(player.getAbsorptionAmount());
		int fullBorders = absorb / 80;
		
		if (absorb <= 0) return;
		
		boolean highlight = this.healthBlinkTime > this.lastGuiTicks && (this.healthBlinkTime - this.lastGuiTicks) / 3L % 2L == 1L;
		
		bind(APPP_ICONS);
		
		for (int i = 9; i >= 0; i--) {
			if (i > fullBorders) continue;
			
			int heartX = x + i * 8;
			int heartY = lastHeartY[i]; // borders should of course line up with hearts :)
			
			if (i < fullBorders) blit(mStack, heartX, heartY, highlight ? 18 : 0, 99, 9, 9);
			else if (i == fullBorders && absorb % 80 != 0)
				blit(mStack, heartX, heartY,
						(highlight ? 18 : 0) + 9 * (MathHelper.ceil(absorb / 4F) % 2),
						9 + 9 * MathHelper.ceil((absorb % 80) / 8F), 9, 9);
		}

		bind(VANILLA_ICONS);
	}
	
	public void renderArmorText(MatrixStack mStack, int x, int y) {
		double armor = armor(this.minecraft.player);
		
		if (armor <= 0 && !ApppConfig.getBool("showArmorWhenZero")) return;
		
		Suffix.Type type = ApppConfig.getSuffix();
		int resistance = this.minecraft.player.hasEffect(Effects.DAMAGE_RESISTANCE)
				? this.minecraft.player.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier()
				: 0;
		
		int power = armor == 0 ? 0 : (int) Math.log10(armor);
		if (type != Suffix.Type.SCI && power < 27) power = power / 3 * 3; // 100 YOTTA is max. if higher switch to SCI notation
		else type = Suffix.Type.SCI;
		
		String significand = String.valueOf(MathHelper.floor(armor / Math.pow(10, power) * 10F) / 10F);  // one decimal precision
		if(significand.endsWith(".0")) significand = significand.substring(0, significand.length() - 2); // strip .0
		significand += (type == Suffix.Type.SCI ? "E" + power : Suffix.byPow(power).getPrefix());        // add suffix
		
		int color;
		if (resistance >= 4) color = ApppConfig.getHex("resistanceFull");
		else if (armor == 0) color = ApppConfig.getHex("armor0");
		else if (armor < 25) color = ApppConfig.getHex("armorLT25");
		else if (armor > 25) color = ApppConfig.getHex("armorGT25");
		else color = ApppConfig.getHex("armorEQ25");
		
		text(mStack, significand, x - width(significand) - 1, y + 1, color);
	}
	
	public void renderHealthText(MatrixStack mStack, int x, int y) {
		int maxHp  = MathHelper.ceil(this.minecraft.player.getMaxHealth());
		int absorb = MathHelper.ceil(this.minecraft.player.getAbsorptionAmount());
		int hp     = MathHelper.ceil(this.minecraft.player.getHealth());
		
		if(hp > maxHp) hp = maxHp;
		y++;
		
		int lenabsorb = width(     absorb) + 1;
		int lenplus   = width("+", absorb) + 1;
		int lenfull   = width(     hp    ) + (absorb == 0 ? 1 : lenplus);
		
		int hpcol = this.minecraft.player.hasEffect(Effects.POISON)
				? ApppConfig.getHex("heartPoison")
				: this.minecraft.player.hasEffect(Effects.WITHER)
						? ApppConfig.getHex("heartWither")
						: ApppConfig.getHex("heart");
		
		if(hp < maxHp) {
			int lenmaxhp = width(maxHp) + (absorb == 0 ? 1 : lenplus);
			int lenslash = width("/")   + lenmaxhp;
			
			text(mStack, "/"       , x - lenslash, y, ApppConfig.getHex("separator"));
			text(mStack, "" + maxHp, x - lenmaxhp, y, hpcol);
			
			lenfull += width("/", maxHp);
		}
		
		text(mStack, "" + hp, x - lenfull, y, hpcol);
		if(absorb > 0) {
			text(mStack, "+"        , x - lenplus  , y, ApppConfig.getHex("separator"));
			text(mStack, "" + absorb, x - lenabsorb, y, ApppConfig.getHex("absorption"));
		}
	}
	
	public void debugTexture(MatrixStack mStack) {
		bind(APPP_ICONS);
		mStack.pushPose();
		mStack.scale(0.8F, 0.8F, 1);
		mStack.translate(1.25D, 6.25D, 0);
		blit(mStack, 5, 25, 0, 0, 256, 128);
		mStack.popPose();
		bind(VANILLA_ICONS);
	}
	
	public void debugText(MatrixStack mStack, int x, int y) {
		float maxHp  = this.minecraft.player.getMaxHealth();
		float absorb = this.minecraft.player.getAbsorptionAmount();
		int hpRows = MathHelper.ceil((maxHp + absorb) / 20F);
		
		text(mStack, "armor should be here (vanilla)", x, y, 0xff0000);
		text(mStack, "hp: "   + maxHp , 5,  5, 0xffffff);
		text(mStack, "rows: " + hpRows, 5, 15, 0xffffff);

		text(mStack, "armor = 0" , x, y - 40, ApppConfig.getHex("armor0"));
		text(mStack, "armor < 25", x, y - 30, ApppConfig.getHex("armorLT25"));
		text(mStack, "armor = 25", x, y - 20, ApppConfig.getHex("armorEQ25"));
		text(mStack, "armor > 25", x, y - 10, ApppConfig.getHex("armorGT25"));
	}
	
	
	private void bind(ResourceLocation res) {
		this.minecraft.textureManager.bind(res);
	}
	
	private int width(Object... objs) {
		if (objs == null) return 0;
		if (objs.length == 1) return this.minecraft.font.width(String.valueOf(objs[0]));
		
		String s = "";
		for (Object obj : objs)
			s += String.valueOf(obj);
		return this.minecraft.font.width(s);
	}
	
	private int armor(LivingEntity le) {
		return le.getArmorValue() + (int) le.getAttributeValue(Attributes.ARMOR);
	}
	
	private void resetColor() {
		RenderSystem.color4f(1, 1, 1, 1);
	}
	
	private void text(MatrixStack mStack, String text, int x, int y, int color) {
		this.minecraft.font.draw(mStack, text, x, y, color);
		resetColor();
	}
	
	private void blit(MatrixStack mStack, int x, int y, float u, float v, int width, int height) {
		AbstractGui.blit(mStack, x, y, u, v, width, height, 256, 128);
	}
}
