package dev.cheos.armorpointspp;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yurisuika.raised.Raised;

import dev.cheos.armorpointspp.compat.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class ApppGui extends Gui {
	public int leftHeight = 39, rightHeight = 39;
	private float partialTicksCur;
	
	
	public ApppGui(Minecraft minecraft, ItemRenderer itemRenderer) {
		super(minecraft, itemRenderer);
		
		if (Compat.isRaisedLoaded()) {
			this.leftHeight += Raised.getDistance();
			this.rightHeight += Raised.getDistance();
		}
	}
	
	@Override
	public void render(PoseStack poseStack, float partialTicks) {
		this.leftHeight = this.rightHeight = 39;
		this.partialTicksCur = partialTicks;
		
		if (Compat.isRaisedLoaded()) {
			this.leftHeight += Raised.getDistance();
			this.rightHeight += Raised.getDistance();
		}
		
		super.render(poseStack, partialTicks);
	}
	
	@Override
	@SuppressWarnings("unused")
	protected void renderPlayerHealth(PoseStack poseStack) {
		// LEFT SIDE
		boolean health, absorb, armor, magics, resist, protec, toughn, bewitchmentVampire = false;
		if (Compat.isBewitchmentLoaded()) bewitchmentVampire = BewitchmentCompat.render(this, poseStack, getCameraPlayer(), this.leftHeight, this.leftHeight); // COMPAT
		if (bewitchmentVampire){
			this.leftHeight += 10;
			Overlays.updateHealthY(this, this.screenHeight);
		} else
			health = Overlays.playerHealth(this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		absorb = Overlays.absorption      (this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		armor  = Overlays.armorLevel      (this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		magics = Overlays.magicShield     (this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight); // is this even necessary on fabric?
		resist = Overlays.resistance      (this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		protec = Overlays.protection      (this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		toughn = Overlays.armorToughnessOv(this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		
		// RIGHT SIDE
		if (getVehicleMaxHearts(getPlayerVehicleWithHealth()) == 0)
			renderFood(poseStack);
		renderMountHealth(poseStack);
		
		// OTHER
		Overlays.armorToughness(this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		
		// RIGHT SIDE AGAIN
		renderAir(poseStack);
		
		// COMPAT
		if (Compat.isSpectrumLoaded()) {
			SpectrumSafeAccess.handleOnRender(poseStack, this.screenWidth, this.screenHeight - (this.leftHeight - (armor ? 59 : 49)), getCameraPlayer()); // little hack to get spectrum to render at the correct y
			this.leftHeight += 10;
		}
		
		// TEXT STUFF
		Overlays.armorText    (this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		Overlays.healthText   (this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		Overlays.toughnessText(this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		
		// DEBUG AND CLEANUP
		Overlays.debug(this, poseStack, this.partialTicksCur, this.screenWidth, this.screenHeight);
		Overlays.cleanup();
	}
	
	public void renderHealth(PoseStack poseStack) {
		this.minecraft.getProfiler().push("health");
		setup(true, false, GUI_ICONS_LOCATION);
		
		Player player = getCameraPlayer();
		if (player == null) return;
		int health = Mth.ceil(player.getHealth());
		boolean highlight = this.healthBlinkTime > this.tickCount && (this.healthBlinkTime - this.tickCount) / 3L % 2L == 1L;
		
		if (health < this.lastHealth && player.invulnerableTime > 0) {
			this.lastHealthTime = Util.getMillis();
			this.healthBlinkTime = this.tickCount + 20;
		} else if (health > this.lastHealth && player.invulnerableTime > 0) {
			this.lastHealthTime = Util.getMillis();
			this.healthBlinkTime = this.tickCount + 10;
		}
		
		if (Util.getMillis() - this.lastHealth > 1000L) {
			this.displayHealth = health;
			this.lastHealthTime = Util.getMillis();
		}
		
		this.lastHealth = health;
		float maxHealth = Math.max(player.getMaxHealth(), Math.max(this.displayHealth, health));
		int absorb = Mth.ceil(player.getAbsorptionAmount());
		int rows = Mth.ceil((maxHealth + absorb) / 20F);
		int rowHeight = Math.max(10 - (rows - 2), 3);
		int left = this.screenWidth / 2 - 91;
		int top = this.screenHeight - this.leftHeight;
		int regen = player.hasEffect(MobEffects.REGENERATION) ? this.tickCount % Mth.ceil(maxHealth + 5F) : -1;
		
		this.leftHeight += rows * rowHeight;
		if (rowHeight != 10) this.leftHeight += 10 - rowHeight;
		this.random.setSeed(this.tickCount * 312871);
		
		this.renderHearts(poseStack, player, left, top, rowHeight, regen, maxHealth, health, this.displayHealth, absorb, highlight);
		this.minecraft.getProfiler().pop();
	}
	
	public void renderAir(PoseStack poseStack) {
		this.minecraft.getProfiler().push("air");
		setup(true, false, GUI_ICONS_LOCATION);
		
		Player player = getCameraPlayer();
		if (player == null) return;
		int left = this.screenWidth / 2 + 91;
		int top = this.screenHeight - this.rightHeight;
		int air = player.getAirSupply();
		
		if (player.isEyeInFluid(FluidTags.WATER) || air < 300) {
			int full = Mth.ceil((air - 2) * 10.0D / 300.0D);
			int partial = Mth.ceil(air * 10.0D / 300.0D) - full;
			
			for (int i = 0; i < full + partial; i++)
				blit(poseStack, left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
			this.rightHeight += 10;
		}
		minecraft.getProfiler().pop();
	}
	
	public void renderFood(PoseStack poseStack) {
		this.minecraft.getProfiler().push("food");
		if (Compat.isAppleskinLoaded())
			AppleskinSafeAccess.handlerOnPreRender(poseStack);
		setup(true, false, GUI_ICONS_LOCATION);
		
		Player player = getCameraPlayer();
		if (player == null) return;
		int left = this.screenWidth / 2 + 91;
		int top = this.screenHeight - this.rightHeight;
		this.rightHeight += 10;
		
		FoodData stats = this.minecraft.player.getFoodData();
		int level = stats.getFoodLevel();
		
		for (int i = 0; i < 10; i++) {
			int idx = i * 2 + 1;
			int x = left - i * 8 - 9;
			int y = top;
			int icon = 16;
			int background = 0;
			
			if (minecraft.player.hasEffect(MobEffects.HUNGER)) {
				icon += 36;
				background = 13;
			}
			
			if (player.getFoodData().getSaturationLevel() <= 0.0F && tickCount % (level * 3 + 1) == 0)
				y = top + (random.nextInt(3) - 1);
			
			blit(poseStack, x, y, 16 + background * 9, 27, 9, 9);
			if (idx < level)
				blit(poseStack, x, y, icon + 36, 27, 9, 9);
			else if (idx == level)
				blit(poseStack, x, y, icon + 45, 27, 9, 9);
		}
		
		if (Compat.isAppleskinLoaded())
			AppleskinSafeAccess.handlerOnRender(poseStack);
		minecraft.getProfiler().pop();
	}
	
	// important distinction:
	// renderVehicleHealth - vanilla - should not render anything
	@Override protected void renderVehicleHealth(PoseStack poseStack) { }
	
	// renderMountHealth - is now the actual render method - gets called from somewhere else
	public void renderMountHealth(PoseStack poseStack) {
		LivingEntity mount = getPlayerVehicleWithHealth();
		if (mount == null) return;
		
		this.minecraft.getProfiler().push("mountHealth");
		setup(true, false, GUI_ICONS_LOCATION);
		
		int maxHearts = this.getVehicleMaxHearts(mount);
        if (maxHearts == 0) return;
        int health = (int) Math.ceil(mount.getHealth());
        int right = this.screenWidth / 2 + 91;
        
        for (int healthIdx = 0; maxHearts > 0; healthIdx += 20) {
            int top = this.screenHeight - this.rightHeight;
            int heartsInRow = Math.min(maxHearts, 10);
            maxHearts -= heartsInRow;
            
            for (int heart = 0; heart < heartsInRow; heart++) {
                int pos = right - heart * 8 - 9;
                this.blit(poseStack, pos, top, 52, 9, 9, 9);
                if (heart * 2 + 1 + healthIdx < health)
                    this.blit(poseStack, pos, top, 88, 9, 9, 9);
                if (heart * 2 + 1 + healthIdx == health)
                	this.blit(poseStack, pos, top, 97, 9, 9, 9);
            }
    		this.rightHeight += 10;
        }
        this.minecraft.getProfiler().pop();
	}
	
	public void setup(boolean blend, boolean depthTest, ResourceLocation texture) {
		if (blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		} else RenderSystem.disableBlend();
		if (depthTest)
			RenderSystem.enableDepthTest();
		else RenderSystem.disableDepthTest();
		if (texture != null) {
			RenderSystem.enableTexture();
			RenderSystem.setShaderTexture(0, texture);
		} else RenderSystem.disableTexture();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
	}
}
