package better_progression.UIs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.awt.*;

public class SkillTree extends Screen {

    public SkillTree() {
        super(Component.literal("SkillTree"));
    }

    @Override
    protected void init() {
        // Buttons:
//        Button squareButton = Button.builder(Component.empty(), button -> {
//                    // Hier kommt die Aktion rein, die beim Klick ausgeführt wird
//                    System.out.println("Button wurde gedrückt!");
//                })
//                .bounds(this.width / 2 - 10, this.height / 2 - 10, 20, 20)// x, y, Breite, Höhe
//                .tooltip(Tooltip.create(Component.literal("Tooltip!")))
//
//                .build();
//
//        this.addRenderableWidget(squareButton);
        Identifier BOOK_SPRITE = Identifier.fromNamespaceAndPath("minecraft", "textures/item/book.png");
        // Da wir ein leeres Widget-Sprites-Objekt übergeben, verschwindet der graue Hintergrund
        WidgetSprites iconSprites = new WidgetSprites(BOOK_SPRITE, BOOK_SPRITE);

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
