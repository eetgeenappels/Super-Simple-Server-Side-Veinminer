package nl.eetgeenappels.ssssv.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import nl.eetgeenappels.ssssv.client.render.BlockOutlineRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {


    @Inject(
            method = "renderBlockOutline",
            at = @At("HEAD")
    )
    private void renderOutlines(MultiBufferSource.BufferSource bufferSource,
                                PoseStack poseStack,
                                boolean isTransparentPass,
                                LevelRenderState levelRenderState,
                                CallbackInfo ci) {

        BlockOutlineRenderer.INSTANCE.render(poseStack, bufferSource,levelRenderState.cameraRenderState.pos, isTransparentPass);
    }
}
