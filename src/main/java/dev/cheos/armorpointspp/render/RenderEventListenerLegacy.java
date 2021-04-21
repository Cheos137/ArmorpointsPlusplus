//package dev.cheos.armorpointspp.render;
//
//import java.util.Random;
//
//import org.lwjgl.opengl.GL11;
//
//import com.mojang.blaze3d.matrix.MatrixStack;
//import com.mojang.blaze3d.systems.RenderSystem;
//
//import dev.cheos.armorpointspp.Suffix;
//import dev.cheos.armorpointspp.TriCondition;
//import dev.cheos.armorpointspp.config.ApppConfig;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.BufferBuilder;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.renderer.WorldVertexBufferUploader;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.potion.Effects;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.Util;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.vector.Matrix4f;
//import net.minecraftforge.client.event.RenderGameOverlayEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//
//// static only: @EventBusSubscriber(modid = Armorpointspp.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
//public class RenderEventListenerLegacy {
//	private static final ResourceLocation APPP_ICONS    = new ResourceLocation("armorpointspp", "textures/gui/icons.png");
//	private static final ResourceLocation VANILLA_ICONS = new ResourceLocation("minecraft"    , "textures/gui/icons.png");
//
//	private Minecraft mc = Minecraft.getInstance();
//	private Random rand = new Random();
//	private long healthUpdateCounter = 0L;
//	private long lastSystemTime = 0L;
//	private int prevHealth = 0;
//	private int lastHealth = 0; //last updated health
//
//	@SubscribeEvent
//	public void renderGameOverlay(RenderGameOverlayEvent event) {
//		if(event.getType() == RenderGameOverlayEvent.ElementType.ARMOR
//				&& (ApppConfig.getBool("showArmorValue")
//						|| ApppConfig.getBool("showResistance")
//						|| ApppConfig.getBool("enableArmorBar")
//						|| ApppConfig.getBool("debug") || true)) {
//			MatrixStack matrixStack = event.getMatrixStack();
//
//			int anchX = event.getWindow().getGuiScaledWidth() / 2 - 91;
//			int anchY = getArmorYAnchor(event.getWindow().getGuiScaledHeight());
//
//			double armor = mc.player.getArmorValue();
//			int resistance = mc.player.hasEffect(Effects.DAMAGE_RESISTANCE)
//					? mc.player.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier()
//					: -1;
//
//			if((ApppConfig.getBool("enableArmorBar")
//					&& (ApppConfig.getBool("showArmorWhenZero")
//							|| armor > 0))
//					|| (ApppConfig.getBool("showResistance")
//							&& resistance > 0))
//				mc.getTextureManager().bind(APPP_ICONS);
//
//			if(ApppConfig.getBool("enableArmorBar") && (ApppConfig.getBool("showArmorWhenZero") || armor > 0)) {
//				GL11.glPushMatrix();
//				GL11.glEnable(GL11.GL_ALPHA_TEST);
//				for(int i = 0; i < 10; i++)
//					for(ArmorTexture tex : ArmorTexture.armor())
//						if(tex.condition.check(i, armor, 0))
//							renderArmorTexture(anchX + i * 8, anchY, tex, matrixStack);
//				GL11.glPopMatrix();
//				event.setCanceled(true);
//			}
//
//			if(ApppConfig.getBool("showResistance") && resistance >= 0) {
//				GL11.glPushMatrix();
//				GL11.glEnable(GL11.GL_ALPHA_TEST);
//
//				for(int i = 0; i < (resistance + 1) * 2 && i < 10; i++)
//					for(ArmorTexture tex : ArmorTexture.resistance())
//						if(tex.condition.check(i, armor, resistance + 1))
//							renderArmorTexture(anchX + i * 8, anchY, tex, matrixStack);
//
//				GL11.glPopMatrix();
//				event.setCanceled(true);
//			}
//
//			if(ApppConfig.getBool("debug") || true) {
//				if(!ApppConfig.getBool("enableArmorBar")) mc.getTextureManager().bind(APPP_ICONS);
//
//				float maxHp = mc.player.getMaxHealth();
//				float absorb = mc.player.getAbsorptionAmount();
//				int hpRows = MathHelper.ceil((maxHp + absorb) / 20F);
//				RenderSystem.enableAlphaTest();
//				renderFullTexture(5, 25, 256, 128, matrixStack);
//				renderString("armor should be here", anchX, anchY, 0xff0000, matrixStack);
//				renderString("hp: "   + maxHp , 5,  5, 0xffffff, matrixStack);
//				renderString("rows: " + hpRows, 5, 15, 0xffffff, matrixStack);
//
//				renderString("armor < 25", anchX, anchY - 30, ApppConfig.getHex("armorLT25"), matrixStack);
//				renderString("armor = 25", anchX, anchY - 20, ApppConfig.getHex("armorEQ25"), matrixStack);
//				renderString("armor > 25", anchX, anchY - 10, ApppConfig.getHex("armorGT25"), matrixStack);
//			}
//
//			if(ApppConfig.getBool("showArmorValue")) {
//				//need 0 check here, log(0) returns infinity
//				int vallen = armor == 0 ? 0 : MathHelper.floor(Math.log10(armor) / 3) * 3;
//
//				//no more shortening after YOTTA
//				vallen = vallen > 24 ? 24 : vallen;
//
//				//remove shortened powers of 10 from armor value (keep one decimal precision)
//				String val = String.valueOf(MathHelper.floor(armor / Math.pow(10, vallen) * 10F) / 10F);
//
//				//remove .0 if present
//				if(val.endsWith(".0")) val = val.substring(0, val.length() - 2);
//
//				//add power of 10 SI-prefix to shortened armor value
//				String armorVal = val + Suffix.byPow(vallen).getPrefix();
//				int strlen = mc.font.width(armorVal);
//
//				renderString(
//						armorVal,
//						anchX - strlen - 1,
//						anchY + 1,
//						resistance >= 4
//								? ApppConfig.getHex("resistanceFull")
//								: armor == 0
//										? ApppConfig.getHex("armor0")
//										: armor < 25
//												? ApppConfig.getHex("armorLT25")
//												: armor == 25
//														? ApppConfig.getHex("armorEQ25")
//														: ApppConfig.getHex("armorGT25"),
//						matrixStack);
//			}
//
//			//required so that gui elements rendered after the armorbar are textured correctly
//			mc.getTextureManager().bind(VANILLA_ICONS);
//
//		} else if(event.getType() == RenderGameOverlayEvent.ElementType.HEALTH
//				&& (ApppConfig.getBool("enableHealthBar") || ApppConfig.getBool("showHealthValue"))) {
//			MatrixStack matrixStack = event.getMatrixStack();
//
//			int anchX  = event.getWindow().getGuiScaledWidth() / 2 - 91;
//			int anchY  = event.getWindow().getGuiScaledHeight() - 39;
//			int maxHp  = MathHelper.ceil(mc.player.getMaxHealth());
//			int absorb = MathHelper.ceil(mc.player.getAbsorptionAmount());
//			int fullAbsorbBorders = MathHelper.floor(absorb / 80F);
//			int hp     = MathHelper.ceil(mc.player.getHealth());
//			int hpline = Math.min(MathHelper.floor((hp - 1F) / 20F), 10);
//
//			int update;
//			boolean showFlashingHearts;
//
//			if(hp > maxHp) hp = maxHp;
//
//			if(ApppConfig.getBool("enableHealthBar")) {
//			//calculates the timings to draw white borders and offset hearts
//				update = mc.player.hasEffect(Effects.REGENERATION)
//						? mc.gui.getGuiTicks() % MathHelper.ceil(maxHp + 5F)
//						: -1;
//				showFlashingHearts = healthUpdateCounter > mc.gui.getGuiTicks()
//						&& (healthUpdateCounter - mc.gui.getGuiTicks()) / 3L % 2L == 1L;
//
//				if(hp < prevHealth && mc.player.invulnerableTime > 0) {
//					lastSystemTime = Util.getMillis();
//					healthUpdateCounter = mc.gui.getGuiTicks() + 20;
//				} else if(hp > prevHealth && mc.player.invulnerableTime > 0) {
//					lastSystemTime = Util.getMillis();
//					healthUpdateCounter = mc.gui.getGuiTicks() + 10;
//				}
//				if(Util.getMillis() - lastSystemTime > 1000L) {
//					prevHealth = hp;
//					lastHealth = hp;
//					lastSystemTime = Util.getMillis();
//				}
//				prevHealth = hp;
//				rand.setSeed(mc.gui.getGuiTicks() * 312871L);
//
//
//				mc.getTextureManager().bind(APPP_ICONS);
//				GL11.glPushMatrix();
//				GL11.glEnable(GL11.GL_ALPHA_TEST);
//
//			//draw the healthbar
//				for(int i = 9; i >= 0; i--) {
//					int drawX    = anchX + i * 8;
//					int drawY    = anchY;
//					// offset to use hardcore / poison / wither hearts
//					int heartOff = (mc.player.level.getLevelData().isHardcore() ? 144 : 36)
//								 + (mc.player.hasEffect(Effects.POISON)
//										 ? 36
//										 : mc.player.hasEffect(Effects.WITHER)
//										 		? 72
//								 				: 0);
//					if(hp <= 4)     drawY += rand.nextInt(2);
//					if(i == update) drawY -= 2;
//
//					// background // border
//					int backgroundOff = (showFlashingHearts ? 18 : 0);
//					renderHeartTexture(drawX, drawY, backgroundOff, 9, false, matrixStack);
//					if(absorb > 0) {
//						if(i <  fullAbsorbBorders) renderHeartTexture(drawX, drawY, backgroundOff, 99, false, matrixStack);
//						if(i == fullAbsorbBorders)
//							renderHeartTexture(
//								drawX,
//								drawY,
//								backgroundOff + 9 * ((MathHelper.ceil(absorb / 4F) % 2)),
//								9 + 9 * MathHelper.ceil((absorb % 80) / 8F),
//								false,
//								matrixStack);
//					}
//
//					// flashing hearts
//					if(showFlashingHearts) {
//						if(i * 2 + hpline * 20 + 1 <  lastHealth) renderHeartTexture(drawX, drawY, heartOff + 18, 9 + hpline * 9, false, matrixStack);
//						if(i * 2 + hpline * 20 + 1 == lastHealth) renderHeartTexture(drawX, drawY, heartOff + 27, 9 + hpline * 9, true, matrixStack);
//						if(i * 2 + hpline * 20 + 1 >  lastHealth && hpline != 0)
//							renderHeartTexture(drawX, drawY, heartOff + 18, 9 + (hpline - 1) * 9, false, matrixStack);
//					}
//
//					// hearts
//					if(i * 2 + hpline * 20 + 1 <  hp) renderHeartTexture(drawX, drawY, heartOff    , 9 + hpline * 9, false, matrixStack);
//					if(i * 2 + hpline * 20 + 1 == hp) renderHeartTexture(drawX, drawY, heartOff + 9, 9 + hpline * 9, true, matrixStack);
//					if(i * 2 + hpline * 20 + 1 >  hp && hpline != 0) renderHeartTexture(drawX, drawY, heartOff, 9 + (hpline - 1) * 9, false, matrixStack);
//				}
//
//				GL11.glPopMatrix();
//				event.setCanceled(true);
//			}
//
//			if(ApppConfig.getBool("showHealthValue")) {
//				int lenabsorb = mc.font.width(""  + absorb);
//				int lenplus   = mc.font.width("+" + absorb);
//				int lenfull   = mc.font.width(""  + hp    ) + (absorb == 0 ? 0 : lenplus);
//
//				int hpcol = mc.player.hasEffect(Effects.POISON)
//						? ApppConfig.getHex("heartPoison")
//						: mc.player.hasEffect(Effects.WITHER)
//								? ApppConfig.getHex("heartWither")
//								: ApppConfig.getHex("heart");
//
//				if(hp < maxHp) {
//					int lenmaxhp = mc.font.width("" + maxHp) + (absorb == 0 ? 0 : lenplus);
//					int lenslash = mc.font.width("/") + lenmaxhp;
//
//					renderString("/"       , anchX - lenslash - 1, anchY + 1, ApppConfig.getHex("separator"), matrixStack);
//					renderString("" + maxHp, anchX - lenmaxhp - 1, anchY + 1, hpcol                         , matrixStack);
//
//					lenfull += mc.font.width("/" + maxHp);
//				}
//
//				renderString("" + hp, anchX - lenfull - 1, anchY + 1, hpcol, matrixStack);
//				if(absorb > 0) {
//					renderString("+"        , anchX - lenplus   - 1, anchY + 1, ApppConfig.getHex("separator") , matrixStack);
//					renderString("" + absorb, anchX - lenabsorb - 1, anchY + 1, ApppConfig.getHex("absorption"), matrixStack);
//				}
//			}
//
//			//required so that gui elements rendered after the armorbar are textured correctly
//			mc.getTextureManager().bind(VANILLA_ICONS);
//		}
//	}
//
//	private void renderFullTexture(int x, int y, int w, int h, MatrixStack matrixStack) {
//        Matrix4f modelViewMatrix = matrixStack.last().pose();
//		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
//	    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//	    bufferbuilder.vertex(modelViewMatrix, x    , y + h, 0).uv(0, 1).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x + w, y + h, 0).uv(1, 1).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x + w, y    , 0).uv(1, 0).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x    , y    , 0).uv(0, 0).endVertex();
//	    bufferbuilder.end();
//	    WorldVertexBufferUploader.end(bufferbuilder);
//	}
//
//	private void renderHeartTexture(int x, int y, int u, int v, boolean drawUnderlying, MatrixStack matrixStack) {
//		if(v != 0 && (u % 2) == 1 && drawUnderlying) renderHeartTexture(x, y, u - 9, v - 9, false, matrixStack);
//
//		float f  = 1F / 256F;
//		float f1 = 1F / 128F;
//        float h, w = h = 9F;
//
//        Matrix4f modelViewMatrix = matrixStack.last().pose();
//		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
//	    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//	    bufferbuilder.vertex(modelViewMatrix, x    , y + h, 0).uv((u    ) * f, (v + h) * f1).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x + w, y + h, 0).uv((u + w) * f, (v + h) * f1).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x + w, y    , 0).uv((u + w) * f, (v    ) * f1).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x    , y    , 0).uv((u    ) * f, (v    ) * f1).endVertex();
//	    bufferbuilder.end();
//	    WorldVertexBufferUploader.end(bufferbuilder);
//	}
//
//	private void renderArmorTexture(float x, float y, ArmorTexture tex, MatrixStack matrixStack) { // TODO y offset ??????
//        float f = 1F / ArmorTexture.TEX_SIZE_X, f1 = 1F / ArmorTexture.TEX_SIZE_Y;
//		float h, w  = h = ArmorTexture.ICON_SIZE;
//
//		Matrix4f modelViewMatrix = matrixStack.last().pose();
//		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
//	    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//	    bufferbuilder.vertex(modelViewMatrix, x    , y + h, 0).uv((tex.x    ) * f, (tex.y + h) * f1).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x + w, y + h, 0).uv((tex.x + w) * f, (tex.y + h) * f1).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x + w, y    , 0).uv((tex.x + w) * f, (tex.y    ) * f1).endVertex();
//	    bufferbuilder.vertex(modelViewMatrix, x    , y    , 0).uv((tex.x    ) * f, (tex.y    ) * f1).endVertex();
//	    bufferbuilder.end();
//	    WorldVertexBufferUploader.end(bufferbuilder);
//	}
//
//	private void renderString(String text, int x, int y, int color, MatrixStack matrixStack) {
//        mc.font.draw(matrixStack, text, x, y, color);
//        GL11.glColor4f(1f, 1f, 1f, 1f);
//	}
//
//	private int getArmorYAnchor(int scaledHeight) {
//		int anchY = scaledHeight - 39;
//
//		float hp = mc.player.getMaxHealth();
//		float absorb = mc.player.getAbsorptionAmount();
//		int hpRows = MathHelper.ceil((hp + absorb) / 20F);
//		int rowHeight = Math.max(10 - (hpRows - 2), 3);
//
//		return ApppConfig.getBool("enableHealthBar")
//				? anchY - 10
//				: anchY - (hpRows * rowHeight + (rowHeight != 10 ? 10 - rowHeight : 0));
//	}
//
//	public static enum ArmorTexture {
//		RESISTANCE		(  0, 0, (i, armor, resis) -> { return armor       - i * 2 <= 0 && resis * 2 - i >= 1 && armor >    0; }),
//		RESISTANCE_HALF (  9, 0, (i, armor, resis) -> { return armor %  20 - i * 2 == 1 && resis * 2 - i >= 1 && armor <= 240; }),
//		RESISTANCE_FULL	( 18, 0, (i, armor, resis) -> { return armor       - i * 2 >= 2 && resis * 2 - i >= 1; }),
//		EMPTY			( 27, 0, (i, armor, resis) -> { return armor       - i * 2 <= 0 && armor <=  20; }),
//		HALF_WHITE		( 36, 0, (i, armor, resis) -> { return armor       - i * 2 == 1 && armor <=  20; }),
//		WHITE			( 45, 0, (i, armor, resis) -> { return armor       - i * 2 >= 2 && armor <=  40; }),
//		HALF_RED		( 54, 0, (i, armor, resis) -> { return armor -  20 - i * 2 == 1 && armor <=  40; }),
//		RED				( 63, 0, (i, armor, resis) -> { return armor -  20 - i * 2 >= 2 && armor <=  60; }),
//		HALF_ORANGE		( 72, 0, (i, armor, resis) -> { return armor -  40 - i * 2 == 1 && armor <=  60; }),
//		ORANGE			( 81, 0, (i, armor, resis) -> { return armor -  40 - i * 2 >= 2 && armor <=  80; }),
//		HALF_YELLOW		( 90, 0, (i, armor, resis) -> { return armor -  60 - i * 2 == 1 && armor <=  80; }),
//		YELLOW			( 99, 0, (i, armor, resis) -> { return armor -  60 - i * 2 >= 2 && armor <= 100; }),
//		HALF_LIME		(108, 0, (i, armor, resis) -> { return armor -  80 - i * 2 == 1 && armor <= 100; }),
//		LIME			(117, 0, (i, armor, resis) -> { return armor -  80 - i * 2 >= 2 && armor <= 120; }),
//		HALF_GREEN		(126, 0, (i, armor, resis) -> { return armor - 100 - i * 2 == 1 && armor <= 120; }),
//		GREEN			(135, 0, (i, armor, resis) -> { return armor - 100 - i * 2 >= 2 && armor <= 140; }),
//		HALF_TURQUOISE	(144, 0, (i, armor, resis) -> { return armor - 120 - i * 2 == 1 && armor <= 140; }),
//		TURQOUISE     	(153, 0, (i, armor, resis) -> { return armor - 120 - i * 2 >= 2 && armor <= 160; }),
//		HALF_CYAN	  	(162, 0, (i, armor, resis) -> { return armor - 140 - i * 2 == 1 && armor <= 160; }),
//		CYAN			(171, 0, (i, armor, resis) -> { return armor - 140 - i * 2 >= 2 && armor <= 180; }),
//		HALF_LIGHTBLUE	(180, 0, (i, armor, resis) -> { return armor - 160 - i * 2 == 1 && armor <= 180; }),
//		LIGHTBLUE		(189, 0, (i, armor, resis) -> { return armor - 160 - i * 2 >= 2 && armor <= 200; }),
//		HALF_BLUE		(198, 0, (i, armor, resis) -> { return armor - 180 - i * 2 == 1 && armor <= 200; }),
//		BLUE			(207, 0, (i, armor, resis) -> { return armor - 180 - i * 2 >= 2 && armor <= 220; }),
//		HALF_PURPLE		(216, 0, (i, armor, resis) -> { return armor - 200 - i * 2 == 1 && armor <= 220; }),
//		PURPLE			(225, 0, (i, armor, resis) -> { return armor - 200 - i * 2 >= 2 && armor <= 240; }),
//		HALF_PINK		(234, 0, (i, armor, resis) -> { return armor - 220 - i * 2 == 1 && armor <= 240; }),
//		PINK			(243, 0, (i, armor, resis) -> { return armor - 220 - i * 2 >= 2; });
//
//		public static final float TEX_SIZE_X = 256F;
//		public static final float TEX_SIZE_Y = 128F;
//		public static final float ICON_SIZE = 9F;
//
//		public final int x;
//		public final int y;
//		public final TriCondition<Integer, Double, Integer> condition;
//
//		ArmorTexture(int x, int y, TriCondition<Integer, Double, Integer> condition) {
//			this.x = x;
//			this.y = y;
//			this.condition = condition;
//		}
//
//		public static ArmorTexture[] armor() {
//			ArmorTexture[] out = new ArmorTexture[values().length - 3];
//			int i = 0;
//
//			for(ArmorTexture tex : values())
//				if(!contains(resistance(), tex))
//					out[i++] = tex;
//			return out;
//		}
//
//		public static ArmorTexture[] resistance() {
//			return new ArmorTexture[] {RESISTANCE, RESISTANCE_HALF, RESISTANCE_FULL};
//		}
//
//		public static <T> boolean contains(T[] array, T value) {
//			for(T t : array) {
//				if(t == null && value == null) return true;
//				if(t == null || value == null) continue;
//				if(t.equals(value)) return true;
//			}
//
//			return false;
//		}
//	}
//}
