package dev.cheos.armorpointspp.render;

import java.awt.Color;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.Suffix;
import dev.cheos.armorpointspp.config.ApppConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class HUDRenderer {
	private static final ResourceLocation VANILLA_ICONS = new ResourceLocation(                     "textures/gui/icons.png");
	private static final ResourceLocation    APPP_ICONS = new ResourceLocation(Armorpointspp.MODID, "textures/gui/icons.png");
	private final Random random = new Random();
	private final Minecraft minecraft = Minecraft.getInstance();
	private int lastHealth, displayHealth, lastGuiTicks;
	private long healthBlinkTime, lastHealthTime;
	private int[] lastHeartY = new int[10];
	
	HUDRenderer() { }
	
	public void renderArmor(PoseStack pStack, int x, int y) {
		int armor = Math.min(armor(this.minecraft.player), 240);
		if (armor <= 0 && !confB("showArmorWhenZero")) return;
		
		if (armor == 137 || (!Armorpointspp.isAttributeFixLoaded() && armor == 30)) {
			renderRainbowArmor(pStack, x, y);
			return;
		}
		
		bind(APPP_ICONS);
		for (int i = 0; i < 10; i++)
			blit(pStack, x + 8 * i, y,
					18 * (armor / 20)
					+ ((armor % 20) - 2 * i == 1
					? 36 : (armor % 20) - 2 * i >= 2
					? 45 : 27), 0, 9, 9);
		bind(VANILLA_ICONS);
	}
	
	public void renderVanillaArmor(PoseStack pStack, int x, int y) {
		int level = minecraft.player.getArmorValue();
        for (int i = 1; level > 0 && i < 20; i += 2) {
            blit(pStack, x, y, i < level ? 34 : i == level ? 25 : 16, 9, 9, 9);
            x += 8;
        }
	}
	
	// fun rainbow armor bar only visible on 137 armor -- why? because 137 is my favourite number ^^
	private void renderRainbowArmor(PoseStack pStack, int x, int y) {
		pStack.pushPose();
		
		long millis = Util.getMillis() / 40;
		int color = 0;
		
		bind(APPP_ICONS);
		for (int i = 0; i < 10; i++) {
			millis += 5;
			color = Color.HSBtoRGB((millis % 360) / 360F, 1, 1);
			RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, 1);
			blit(pStack, x + 8 * i, y, 45, 0, 9, 9);
		}
		bind(VANILLA_ICONS);
		pStack.popPose();
	}
	
	public void renderResistance(PoseStack pStack, int x, int y) {
		int armor = armor(this.minecraft.player);
		int resistance = -1;
		
		if (this.minecraft.player.hasEffect(MobEffects.DAMAGE_RESISTANCE))
			resistance = 1 + this.minecraft.player.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier();
		if (resistance <= 0 || (armor <= 0 && !confB("showArmorWhenZero"))) return;
		bind(APPP_ICONS);
		for (int i = 0; i < 10 && i < resistance * confF("resistance"); i++, armor -= 2)
			if      (armor      <= 0) blit(pStack, x + 8 * i, y,  0, 0, 9, 9);
			else if (armor % 20 == 1) blit(pStack, x + 8 * i, y,  9, 0, 9, 9);
			else                      blit(pStack, x + 8 * i, y, 18, 0, 9, 9);
		bind(VANILLA_ICONS);
	}
	
	public void renderProtectionOverlay(PoseStack pStack, int x, int y) {
		if (armor(this.minecraft.player) <= 0 && !confB("showArmorWhenZero")) return;

		int protection = 0;

		// should this be separated for each protection type? -- maxes out with godarmor
		for (ItemStack stack : this.minecraft.player.getArmorSlots()) { // adds 0 for empty stacks
			protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, stack);
			protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLAST_PROTECTION     , stack);
			protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_PROTECTION      , stack);
			protection += EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, stack);
		}

		protection = Mth.ceil(protection * confF("protection"));
		protection = Mth.clamp(protection, 0, 10);
		if (protection <= 0) return;
		
		RenderSystem.enableBlend();
		bind(APPP_ICONS);
		for (int i = 0; i < 10 && i < protection; i++)
			blit(pStack, x + 8 * i, y, 9, 9, 9, 9);
		bind(VANILLA_ICONS);
		RenderSystem.disableBlend();
	}
	
	public void renderArmorToughness(PoseStack pStack, int x, int y) {
		if (armor(this.minecraft.player) <= 0 && !confB("showArmorWhenZero")) return;
		int toughness = Mth.ceil(toughness(this.minecraft.player) * confF("toughness"));
		if (toughness <= 0) return;
		
		pStack.pushPose();
		pStack.scale(0.5F, 0.5F, 1);
		
		bind(APPP_ICONS);
		for (int i = 0; i < 10 && i < toughness; i++)
			blit(pStack, 2 * (x + 8 * i) + 9, 2 * y + 8, 27, 9, 9, 9);
		bind(VANILLA_ICONS);
		
		pStack.popPose();
	}
	
	public void renderHealth(PoseStack pStack, int x, int y) { // TODO: frostbite
		LocalPlayer player = this.minecraft.player;
		boolean frozen   = player.isFullyFrozen();
		boolean hardcore = player.level.getLevelData().isHardcore();
		int health       = Mth.ceil(player.getHealth());
		int heartStack   = Math.min((health - 1) / 20, 10);
		
		this.lastGuiTicks = this.minecraft.gui.getGuiTicks();
		
		boolean blink = !frozen && this.healthBlinkTime > this.lastGuiTicks && (this.healthBlinkTime - this.lastGuiTicks) / 3L % 2L == 1L;
		int regen = this.minecraft.player.hasEffect(MobEffects.REGENERATION)
				? this.lastGuiTicks % 25 // in vanilla: % (maxHealth + 5), here: more than 20 + 5 does not make sense
				: -1;
		int margin = 36;
		
		if (hardcore) margin += 108;
		if (player.hasEffect(MobEffects.POISON)) margin += 36;
		else if (player.hasEffect(MobEffects.WITHER)) margin += 72;
		
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
			int heartValue = i * 2 + heartStack * 20 + 1;
			
			if (health <= 4) heartY += random.nextInt(2);
			if (i == regen && !frozen) heartY -= 2;
			
			lastHeartY[i] = heartY;
			
			blit(pStack, heartX, heartY, blink ? 18 : 0, 9, 9, 9); // draw background
			if (heartValue >  health && heartStack > 0) blit(pStack, heartX, heartY, margin, heartStack * 9, 9, 9); // part. draw row below
			
			if (blink) {
				if      (heartValue <  this.displayHealth) blit(pStack, heartX, heartY, margin + 18, 9 + heartStack * 9, 9, 9); // full
				else if (heartValue == this.displayHealth) blit(pStack, heartX, heartY, margin + 27, 9 + heartStack * 9, 9, 9); // half
			}
			
			if      (heartValue <  health) blit(pStack, heartX, heartY, margin    , 9 + heartStack * 9, 9, 9); // full
			else if (heartValue == health) blit(pStack, heartX, heartY, margin + 9, 9 + heartStack * 9, 9, 9); // half
			
			String test = confS("frostbiteStyle").toLowerCase();
			test = "full";
			
			if (frozen)
				switch (test) {
					case "full":
						if (heartValue < health)
							blit(pStack, heartX, heartY, hardcore ? 18 : 0, 117, 9, 9);
						else if (heartValue == health)
							blit(pStack, heartX, heartY, hardcore ? 27 : 9, 117, 5, 9); // only half width frostbite
						break;
					case "overlay":
						if (heartValue < health)
							blit(pStack, heartX, heartY, hardcore ? 18 : 0, 108, 9, 9);
						else if (heartValue == health)
							blit(pStack, heartX, heartY, hardcore ? 27 : 9, 108, 5, 9); // only half width frostbite
						break;
					case "icon": // fallthrough, icon is default
					default:
						pStack.pushPose();
						pStack.scale(0.5F, 0.5F, 1);
						blit(pStack, 2 * heartX + 8, 2 * heartY + 1, 36, 108, 9, 9);
						pStack.popPose();
						break;
				}
		}
		
		RenderSystem.disableBlend();
		bind(VANILLA_ICONS);
	}
	
	public void renderAbsorption(PoseStack pStack, int x, int y) {
		LocalPlayer player = this.minecraft.player;
		int absorb = Mth.ceil(player.getAbsorptionAmount());
		int fullBorders = Mth.floor(0.05F * absorb * confF("absorption"));
		
		if (absorb <= 0 || confF("absorption") <= 0) return;
		
		int inv = Mth.floor(20F / confF("absorption"));
		boolean highlight = this.healthBlinkTime > this.lastGuiTicks && (this.healthBlinkTime - this.lastGuiTicks) / 3L % 2L == 1L;
		
		bind(APPP_ICONS);
		
		for (int i = 9; i >= 0; i--) {
			if (i > fullBorders) continue;
			
			int heartX = x + i * 8;
			int heartY = lastHeartY[i]; // borders should of course line up with hearts :)
			
			if (i < fullBorders) blit(pStack, heartX, heartY, highlight ? 18 : 0, 99, 9, 9);
			else if (i == fullBorders && absorb % inv != 0)
				blit(pStack, heartX, heartY,
						(highlight ? 18 : 0) + 9 * (Mth.ceil(absorb * confF("absorption")) % 2),
						9 + 9 * Mth.ceil((absorb % inv) / 8F), 9, 9);
		}

		bind(VANILLA_ICONS);
	}
	
	public void renderArmorText(PoseStack pStack, int x, int y) {
		int armor = armor(this.minecraft.player);
		
		if (armor <= 0 && !confB("showArmorWhenZero")) return;
		
		Suffix.Type type = ApppConfig.getSuffix();
		int resistance = this.minecraft.player.hasEffect(MobEffects.DAMAGE_RESISTANCE)
				? this.minecraft.player.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier()
				: 0;
		
		int power = armor == 0 ? 0 : (int) Math.log10(armor);
		if (type != Suffix.Type.SCI && power < 27) power = power / 3 * 3; // 100 YOTTA is max. if higher switch to SCI notation
		else type = Suffix.Type.SCI;
		
		String significand = String.valueOf(Mth.floor(armor / Math.pow(10, power) * 10F) / 10F);  // one decimal precision
		if(significand.endsWith(".0")) significand = significand.substring(0, significand.length() - 2); // strip .0
		significand += (type == Suffix.Type.SCI ? "E" + power : Suffix.byPow(power).getSuffix());        // add suffix
		
		int color;
		if (armor == 137 || (!Armorpointspp.isAttributeFixLoaded() && armor == 30))
			color = Color.HSBtoRGB((((Util.getMillis() + 80) / 40) % 360) / 360F, 1, 1);
		else if (resistance >= 4) color = confH("resistanceFull");
		else if (armor == 0) color = confH("armor0");
		else if (armor < 25) color = confH("armorLT25");
		else if (armor > 25) color = confH("armorGT25");
		else color = confH("armorEQ25");
		
		text(pStack, significand, x - width(significand) - 1, y + 1, color);
	}
	
	public void renderHealthText(PoseStack pStack, int x, int y) { // TODO: frostbite
		int freeze = Math.round(100 * this.minecraft.player.getPercentFrozen());
		int maxHp  = Mth.ceil(this.minecraft.player.getMaxHealth());
		int absorb = Mth.ceil(this.minecraft.player.getAbsorptionAmount());
		int hp     = Mth.ceil(this.minecraft.player.getHealth());
		
		if(hp > maxHp) hp = maxHp;
		y++;
		
		int lenfreeze = freeze > 0 && confB("showFrostbitePercentage") ? width(", ", freeze, "%") : 0;
		int lenabsorb = width(     absorb) + 1;
		int lenplus   = width("+", absorb) + 1;
		int lenfull   = width(     hp    ) + (absorb == 0 ? 1 : lenplus) + lenfreeze;
		
		int hpcol = this.minecraft.player.isFullyFrozen()
				? confH("heartFrostbite")
				: this.minecraft.player.hasEffect(MobEffects.POISON)
						? confH("heartPoison")
						: this.minecraft.player.hasEffect(MobEffects.WITHER)
								? confH("heartWither")
								: confH("heart");
		
		if(hp < maxHp) {
			int lenmaxhp = width(maxHp) + (absorb == 0 ? 1 : lenplus) + lenfreeze;
			int lenslash = width("/")   + lenmaxhp;
			
			text(pStack, "/"       , x - lenslash, y, confH("separator"));
			text(pStack, "" + maxHp, x - lenmaxhp, y, hpcol);
			
			lenfull += width("/", maxHp);
		}
		
		text(pStack, "" + hp, x - lenfull, y, hpcol);
		
		if(absorb > 0) {
			text(pStack, "+"        , x - lenplus  , y, confH("separator"));
			text(pStack, "" + absorb, x - lenabsorb, y, confH("absorption"));
		}
		
		if (freeze > 0 && confB("showFrostbitePercentage")) {
			text(pStack, ", "        , x - lenfreeze         , y, confH("separator"));
			text(pStack, freeze + "%", x - width(freeze, "%"), y, confH("heartFrostbite"));
		}
	}
	
	public void debugTexture(PoseStack pStack) {
		pStack.pushPose();
		pStack.scale(0.8F, 0.8F, 1);
		pStack.translate(1.25D, 6.25D, 0);
		bind(APPP_ICONS);
		blit(pStack, 5, 25, 0, 0, 256, 128);
		bind(VANILLA_ICONS);
		pStack.popPose();
	}
	
	public void debugText(PoseStack pStack, int x, int y) {
		float maxHp  = this.minecraft.player.getMaxHealth();
		float absorb = this.minecraft.player.getAbsorptionAmount();
		int hpRows = Mth.ceil((maxHp + absorb) / 20F);
		
		text(pStack, "armor should be here (vanilla)", x, y, 0xff0000);
		text(pStack, "hp: "   + maxHp , 5,  5, 0xffffff);
		text(pStack, "rows: " + hpRows, 5, 15, 0xffffff);

		text(pStack, "armor = 0" , x, y - 40, confH("armor0"));
		text(pStack, "armor < 25", x, y - 30, confH("armorLT25"));
		text(pStack, "armor = 25", x, y - 20, confH("armorEQ25"));
		text(pStack, "armor > 25", x, y - 10, confH("armorGT25"));
	}
	
	
	private void bind(ResourceLocation res) {
//		this.minecraft.textureManager.bindForSetup(res);
        RenderSystem.setShaderTexture(0, res);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
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
		return le.getArmorValue();
	}
	
	private int toughness(LivingEntity le) {
		return Mth.floor(le.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
	}
	
	private void resetColor() {
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
	
	private void text(PoseStack pStack, String text, int x, int y, int color) {
		this.minecraft.font.draw(pStack, text, x, y, color);
		resetColor();
	}
	
	private void blit(PoseStack pStack, int x, int y, float u, float v, int width, int height) {
		GuiComponent.blit(pStack, x, y, u, v, width, height, 256, 128);
	}
	
	private boolean confB(String name) {
		return ApppConfig.getBool(name);
	}
	
	private int confH(String name) {
		return ApppConfig.getHex(name);
	}
	
	private String confS(String name) {
		return ApppConfig.getString(name);
	}
	
	private float confF(String name) {
		return ApppConfig.getFloat(name);
	}
}
