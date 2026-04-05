package better_progression.mixin.client;

import better_progression.BetterProgressionClient;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public class InGameHudMixin {
    @ModifyVariable(
            method = "renderPlayerHealth",
            at = @At("STORE"),
            ordinal = 4
    )
    private int moveHeartsUp(int y) {
        return y - BetterProgressionClient.HEARTS_OFFSET_Y;
    }
}
