package net.m3tte.tactical_imbuements.mixin;


import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.m3tte.tactical_imbuements.renderer.GlintRenderers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.SortedMap;

import static net.minecraft.client.renderer.MultiBufferSource.immediateWithBuffers;

@Mixin(value = RenderBuffers.class)
public class RenderBuffersMixin {


    private static MultiBufferSource.BufferSource savedBufferSource = null;
    private static OutlineBufferSource savedOutlineBuffers = null;

    private static boolean hasRendered = false;

    @Inject(at = @At(value = "TAIL"), method = "bufferSource", cancellable = true)
    private void injectBufferSource(CallbackInfoReturnable<MultiBufferSource.BufferSource> cbk) {

        if (savedBufferSource == null) {
            System.out.println("First Override");
            RenderBuffers renderBuffers = ((RenderBuffers)(Object)this);

            SortedMap<RenderType, BufferBuilder> fixedBuffers = ((renderBufferInterface) renderBuffers).getFixedBuffers();
            put(fixedBuffers, GlintRenderers.getFireGlintDirect());
            put(fixedBuffers, GlintRenderers.getFireEntityGlintDirect());
            put(fixedBuffers, GlintRenderers.getVenomGlintDirect());
            put(fixedBuffers, GlintRenderers.getVenomEntityGlintDirect());
            put(fixedBuffers, GlintRenderers.getSparkGlintDirect());
            put(fixedBuffers, GlintRenderers.getSparkEntityGlintDirect());
            put(fixedBuffers, GlintRenderers.getFreezeGlintDirect());
            put(fixedBuffers, GlintRenderers.getFreezeEntityGlintDirect());
            savedBufferSource = immediateWithBuffers(fixedBuffers, new BufferBuilder(256));
        }
        cbk.setReturnValue(savedBufferSource);
    }

    @Inject(at = @At(value = "TAIL"), method = "outlineBufferSource", cancellable = true)
    private void injectOutlineBuffer(CallbackInfoReturnable<OutlineBufferSource> cbk) {
        if (savedOutlineBuffers != null) {
            cbk.setReturnValue(savedOutlineBuffers);
        }
    }


    private static void put(SortedMap<RenderType, BufferBuilder> p_110102_, RenderType p_110103_) {
        p_110102_.put(p_110103_, new BufferBuilder(p_110103_.bufferSize()));
    }
}
