package better_progression;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class BetterProgressionClient implements ClientModInitializer {
	public static final int HEARTS_OFFSET_Y = 6;

	@Override
	public void onInitializeClient() {

		BetterProgression.getLogger().info("Initializing BetterProgression Client");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		//adds a Second bar to the Hud
		final Identifier BAR_FULL = Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "bar_full");
		final Identifier BAR_EMPTY = Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "bar_empty");

		HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
			int x = guiGraphics.guiWidth() / 2 - 91;
			int y = guiGraphics.guiHeight() - 34;

			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BAR_EMPTY, x, y, 182, 5);

			int progress = 91;
			if (progress > 0) {
				guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BAR_FULL, 182, 5, 0, 0, x, y, progress, 5);
			}
		});
	}
}