package better_progression;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class BetterProgressionClient implements ClientModInitializer {
	public static final int XP_LEVEL_COUNTER_OFFSET_Y = -5;
	public static final int SKILL_POINT_COUNTER_Y = 39;

	@Override
	public void onInitializeClient() {

		BetterProgression.getLogger().info("Initializing BetterProgression Client");

		final Identifier BAR_FULL = Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "bar_full");
		final Identifier BAR_EMPTY = Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "bar_empty");

		HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {

			Minecraft minecraft = Minecraft.getInstance();

			if (minecraft.font == null || minecraft.player == null) {
				return;
			}

			if (minecraft.player.getAbilities().instabuild) {
				return;
			}

			int skillPointsValue = minecraft.player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);

			if (skillPointsValue <= 0) {
				return;
			}

			String text = String.valueOf(skillPointsValue);

			int textWidth = minecraft.font.width(text);

			int textX = guiGraphics.guiWidth() / 2 - (textWidth / 2);

			int textY = guiGraphics.guiHeight() - SKILL_POINT_COUNTER_Y;

			guiGraphics.pose().pushMatrix();

			guiGraphics.pose().translate(0, 0);

			//black background
			guiGraphics.drawString(minecraft.font, text, textX + 1, textY, 0xFF000000, false);
			guiGraphics.drawString(minecraft.font, text, textX - 1, textY, 0xFF000000, false);
			guiGraphics.drawString(minecraft.font, text, textX, textY + 1, 0xFF000000, false);
			guiGraphics.drawString(minecraft.font, text, textX, textY - 1, 0xFF000000, false);

			//blue number
			guiGraphics.drawString(minecraft.font, text, textX, textY, 0xFF82D1ED, false);

			guiGraphics.pose().popMatrix();
		});
	}
}