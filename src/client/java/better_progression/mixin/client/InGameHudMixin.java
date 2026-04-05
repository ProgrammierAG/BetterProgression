package better_progression.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBarRenderer.class)
public class InGameHudMixin {

    @Inject(at = @At("HEAD"), method = "renderBackground")
    private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().pushMatrix();

        int centerX = guiGraphics.guiWidth() / 2;
        int leftEdgeHotbar = centerX - 91;

        guiGraphics.pose().translate(leftEdgeHotbar, 0);
        guiGraphics.pose().scale(0.5f, 1.0f);
        guiGraphics.pose().translate(-leftEdgeHotbar, 0);
    }

    @Inject(at = @At("TAIL"), method = "renderBackground")
    private void afterRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().popMatrix();
    }
}
