package net.m3tte.tactical_imbuements.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.m3tte.tactical_imbuements.procedures.TickProcedure;
import net.m3tte.tactical_imbuements.renderer.GlintRenderers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {
    @Redirect(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
            method = "render")
    public VertexConsumer overlayRendererDirect(MultiBufferSource multiBuffer, RenderType p_115224_, boolean p_115225_, boolean p_115226_, ItemStack item, ItemDisplayContext context , boolean bool) {

        LocalPlayer localplayer = Minecraft.getInstance().player;

        if (localplayer != null && item.getTag() != null) {

            int timeout = (int)item.getOrCreateTag().getDouble("imbueCounter") - localplayer.tickCount;

            if (item.getOrCreateTag().getString("imbueType").length() > 2 && timeout < TickProcedure.defaultMaxTime + 60 && timeout > 0){
                boolean flag1;
                if (context != ItemDisplayContext.GUI && !context.firstPerson() && item.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem)item.getItem()).getBlock();
                    flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
                } else {
                    flag1 = true;
                }
                // Modified Enchantment Buffer
                return getEffectFoilBufferDirect(item, multiBuffer, p_115224_, true, true);
            }
        }
        // Default Enchantment Buffer
        return ItemRenderer.getFoilBufferDirect(multiBuffer, p_115224_, true, item.hasFoil());
    }

    private static VertexConsumer getEffectFoilBuffer(ItemStack item, MultiBufferSource p_115212_, RenderType p_115213_, boolean p_115214_, boolean hasFoil) {
        if (hasFoil) {
            return Minecraft.useShaderTransparency() && p_115213_ == Sheets.translucentItemSheet() ? VertexMultiConsumer.create(p_115212_.getBuffer(RenderType.glintTranslucent()), p_115212_.getBuffer(p_115213_)) : VertexMultiConsumer.create(p_115212_.getBuffer(p_115214_ ? RenderType.glint() : RenderType.entityGlint()), p_115212_.getBuffer(p_115213_));
        } else {
            return p_115212_.getBuffer(p_115213_);
        }
    }



    private static VertexConsumer getEffectFoilBufferDirect(ItemStack item, MultiBufferSource mbs, RenderType p_115224_, boolean p_115225_, boolean hasFoil) {

        String imbuetype = item.getOrCreateTag().getString("imbueType");

        VertexConsumer cons = hasFoil ? VertexMultiConsumer.create(mbs.getBuffer(p_115225_ ? GlintRenderers.getDirectGlintRender(imbuetype) : GlintRenderers.getEntityGlintDirect(imbuetype)), mbs.getBuffer(p_115224_)) : mbs.getBuffer(p_115224_);

        return cons;
    }

    /*@Inject(at = @At(value = "HEAD"), method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms/TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", cancellable = true)
    public void overlayRenderer(ItemStack item, ItemTransforms.TransformType p_115145_, boolean p_115146_, PoseStack poseStack, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_, CallbackInfo inf) {
        ItemRenderer renderer = ((ItemRenderer)(Object)this);

        LocalPlayer localplayer = Minecraft.getInstance().player;




        int timeout = (int)item.getOrCreateTag().getDouble("imbueCounter") - localplayer.tickCount;
        int maxtime = (int)item.getOrCreateTag().getDouble("maxImbueTime");
        String type = item.getOrCreateTag().getString("imbueType");



        if (timeout > 0) {
            //System.out.println(timeout);
            int r = 255;
            int g = 255;
            int b = 255;
            int a = 125;

            switch (type) {
                case "flame" -> {
                    g=103;
                    b=0;
                }
            }

            float mult = (float) timeout / maxtime;


        }



    }*/


    /*private void fillRect(BufferBuilder p_115153_, int p_115154_, int p_115155_, int p_115156_, int p_115157_, int p_115158_, int p_115159_, int p_115160_, int p_115161_) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        p_115153_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        p_115153_.vertex((double)(p_115154_ + 0), (double)(p_115155_ + 0), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + 0), (double)(p_115155_ + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + p_115156_), (double)(p_115155_ + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + p_115156_), (double)(p_115155_ + 0), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.end();
        BufferUploader.end(p_115153_);
    }*/

    public void blit(PoseStack poseStack, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        innerBlit(poseStack, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    public void blit(PoseStack poseStack, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight) {
        blit(poseStack, x, y, width, height, uOffset, vOffset, width, height, textureWidth, textureHeight);
    }
    private void innerBlit(PoseStack p_93188_, int p_93189_, int p_93190_, int p_93191_, int p_93192_, int p_93193_, int p_93194_, int p_93195_, float p_93196_, float p_93197_, int p_93198_, int p_93199_) {
        innerBlit(p_93188_.last().pose(), p_93189_, p_93190_, p_93191_, p_93192_, p_93193_, (p_93196_ + 0.0F) / (float)p_93198_, (p_93196_ + (float)p_93194_) / (float)p_93198_, (p_93197_ + 0.0F) / (float)p_93199_, (p_93197_ + (float)p_93195_) / (float)p_93199_);
    }

    private void innerBlit(org.joml.Matrix4f p_93113_, int p_93114_, int p_93115_, int p_93116_, int p_93117_, int p_93118_, float p_93119_, float p_93120_, float p_93121_, float p_93122_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93117_, (float)p_93118_).uv(p_93119_, p_93122_).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93117_, (float)p_93118_).uv(p_93120_, p_93122_).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93116_, (float)p_93118_).uv(p_93120_, p_93121_).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93116_, (float)p_93118_).uv(p_93119_, p_93121_).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }


}
