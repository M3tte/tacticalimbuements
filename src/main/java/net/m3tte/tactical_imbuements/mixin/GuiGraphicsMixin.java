package net.m3tte.tactical_imbuements.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.m3tte.tactical_imbuements.definitions.ImbuementDefinitions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(at = @At(value = "TAIL"), method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", cancellable = true)
    private void renderDecorations(Font p_115175_, ItemStack item, int p_115177_, int p_115178_, @Nullable String p_115179_, CallbackInfo inf) {
        LocalPlayer localplayer = Minecraft.getInstance().player;
        if (localplayer == null || item.getTag() == null)
            return;

        PoseStack posestack = new PoseStack();
        posestack.translate(0.0D, 0.0D, 400.0D);


        int timeout = (int)item.getOrCreateTag().getDouble("imbueCounter") - localplayer.tickCount;
        int maxtime = (int)item.getOrCreateTag().getDouble("maxImbueTime");
        String type = item.getOrCreateTag().getString("imbueType");
        if (timeout > maxtime)
            timeout = maxtime;
        //System.out.println(timeout + " | " +Minecraft.getInstance().player.getLevel().isClientSide()+" | "+(Minecraft.getInstance().player.getServer()!=null));
        if (timeout > 0) {
            // Creates the cooldown-ish bar

            int r = 255;
            int g = 255;
            int b = 255;
            int a = 32;
            String texture = "";
            switch (type) {
                case ImbuementDefinitions.FLAMEID -> texture = ImbuementDefinitions.FLAME.getFireOverlayIdent();
                case ImbuementDefinitions.VENOMID -> texture = ImbuementDefinitions.VENOM.getFireOverlayIdent();
                case ImbuementDefinitions.SPARKID -> texture = ImbuementDefinitions.SPARK.getFireOverlayIdent();
                case ImbuementDefinitions.FREEZEID -> texture = ImbuementDefinitions.FREEZE.getFireOverlayIdent();
            }



            switch (type) {
                case ImbuementDefinitions.FLAMEID -> {
                    g=(int)(ImbuementDefinitions.FLAME.getgCol() * 255);
                    b=(int)(ImbuementDefinitions.FLAME.getbCol() * 255);
                }
                case ImbuementDefinitions.VENOMID -> {
                    r=(int)(ImbuementDefinitions.VENOM.getrCol() * 255);
                    g=(int)(ImbuementDefinitions.VENOM.getgCol() * 255);
                    b=(int)(ImbuementDefinitions.VENOM.getbCol() * 255);
                }
                case ImbuementDefinitions.FREEZEID -> {
                    r=(int)(ImbuementDefinitions.FREEZE.getrCol() * 255);
                    g=(int)(ImbuementDefinitions.FREEZE.getgCol() * 255);
                    b=(int)(ImbuementDefinitions.FREEZE.getbCol() * 255);
                }
                case ImbuementDefinitions.SPARKID -> {
                    r=(int)(ImbuementDefinitions.SPARK.getrCol() * 255);
                    g=(int)(ImbuementDefinitions.SPARK.getgCol() * 255);
                    b=(int)(ImbuementDefinitions.SPARK.getbCol() * 255);
                }
            }
            float mult = (float) timeout / maxtime;
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            int i1 = p_115178_ + Mth.floor(16.0F * (1.0F - mult));
            int j1 = i1 + Mth.ceil(16.0F * mult);
            ((GuiGraphics)(Object)this).fill(RenderType.guiOverlay(), p_115177_, i1, p_115177_ + 16, j1, FastColor.ARGB32.color(a, r, g, b));

            RenderSystem.enableDepthTest();


            // Creates the border depending on type
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            ResourceLocation location = new ResourceLocation(texture);
            RenderSystem.setShaderTexture(0, location);

            ((GuiGraphics)(Object)this).blit(location, p_115177_, p_115178_ + (int)(16 * (1 - mult)), 0, (int)(16 * (1 - mult)), 16, (int) (Math.ceil(16*mult)), 16, 16);
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);

        }
    }
}
