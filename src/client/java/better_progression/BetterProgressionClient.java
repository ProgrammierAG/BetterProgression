package better_progression;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
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

			//guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BAR_EMPTY, x, y, 182, 5);

			int progress = 91;
			if (progress > 0) {
				//guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BAR_FULL, 182, 5, 0, 0, x, y, progress, 5);
			}

			Minecraft minecraft = Minecraft.getInstance();

			if (minecraft.font == null || minecraft.player == null) {
				return;
			}

			if (minecraft.player.getAbilities().instabuild) {
				return;
			}

			int skillPointsValue = minecraft.player.getAttached(Attachments.SKILLPOINTS);

			String text = String.valueOf((int) skillPointsValue);

			int textWidth = minecraft.font.width(text);

			int textX = guiGraphics.guiWidth() / 2 - (textWidth / 2);

			int textY = y - 10;

			guiGraphics.pose().pushMatrix();

			guiGraphics.pose().translate(0, 0);

			guiGraphics.drawString(minecraft.font, text, textX + 1, textY, 0xFF000000, false);
			guiGraphics.drawString(minecraft.font, text, textX - 1, textY, 0xFF000000, false);
			guiGraphics.drawString(minecraft.font, text, textX, textY + 1, 0xFF000000, false);
			guiGraphics.drawString(minecraft.font, text, textX, textY - 1, 0xFF000000, false);

			guiGraphics.drawString(minecraft.font, text, textX, textY, 0xFF5555FF, false);

			guiGraphics.pose().popMatrix();
		});
	}
}