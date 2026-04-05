package better_progression.mixin.client;

import better_progression.BetterProgression;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(ArmorRenderer.class)
@Mixin(Gui.class)
public class InGameHudMixin {
    @ModifyVariable(
            method = "renderPlayerHealth",
            at = @At("STORE"),
            ordinal = 4
    )
    private int moveHeartsUp(int y) {
        return y - 5;
    }
}
