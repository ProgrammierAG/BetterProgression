package better_progression.UIs;

import better_progression.BetterProgression;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SkillTree extends Screen {

    public SkillTree() {
        super(Component.literal("SkillTree"));
    }

    @Override
    protected void init() {
        // Buttons:

        Identifier TEST_SPRIT = Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "test");
        WidgetSprites iconSprites = new WidgetSprites(TEST_SPRIT, TEST_SPRIT);
        ImageButton squareButton = new ImageButton(
                this.width / 2 - 10, this.height / 2 - 10, 20, 20,
                iconSprites,
                button -> {
                    System.out.println("Button wurde gedrückt!");
                }
        );
        squareButton.setTooltip(Tooltip.create(Component.literal("Tooltip!")));
        this.addRenderableWidget(squareButton);

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xA0101010, 0xB0101010);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
