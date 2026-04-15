package better_progression.UIs;

import better_progression.BetterProgression;
import better_progression.networking.SkillUnlockPayload;
import better_progression.skills.Skill;
import better_progression.skills.Skills;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class SkillTree extends Screen {

    private int windowX = 0;
    private int windowY = 0;

    private boolean isDragging = false;
    private double dragX = 0;
    private double dragY = 0;

    private List<ImageButton> buttons = new ArrayList<>();
    private List<Vec2> relativePos = new ArrayList<>();

    private Identifier background = Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "task_frame_unobtained");

    public SkillTree() {
        super(Component.literal("SkillTree"));
    }

    @Override
    protected void init() {
        // Buttons:

        int offset = 0;
        for (Skill skill : Skills.SKILLS.values()) {
            Identifier ICON = skill.icon();
            WidgetSprites widget = new WidgetSprites(ICON);
            this.genSkillButton(offset, 0, 20, 20, widget, skill);
            offset += 25;
        }



        //genSkillButton(this.width / 2 - 10, this.height / 2 - 10, 20, 20, ICON, HOVERED);

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xA0101010, 0xB0101010);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 0xFFFFFF);
        for (ImageButton button : buttons) {
            guiGraphics.blit(background, ((int) relativePos.get(buttons.indexOf(button)).x), ((int) relativePos.get(buttons.indexOf(button)).y), 0, 0, 20, 20, 0, 0);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void genSkillButton(int x, int y, int width, int height,
                               WidgetSprites icon, Skill skill) {
        ImageButton Button = new ImageButton(
                x, y, width, height,
                icon,
                button -> {
                    BetterProgression.getLogger().info("sending Payload for: " + skill.NAME_ID());
                    ClientPlayNetworking.send(new SkillUnlockPayload(skill.NAME_ID()));
                }
        );
        Button.setTooltip(Tooltip.create(Component.translatable(skill.DESC_ID())));
        this.addRenderableWidget(Button);
        this.buttons.add(Button);
        this.relativePos.add(new Vec2(x, y));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() == 0) {
            this.isDragging = true;
            this.dragX = mouseButtonEvent.x() - windowX;
            this.dragY = mouseButtonEvent.y() - windowY;
            if (super.mouseClicked(mouseButtonEvent, bl)) {
                return true;
            }
        }
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        if (this.isDragging && mouseButtonEvent.button() == 0) {
            this.windowX = (int) (mouseButtonEvent.x() - this.dragX);
            this.windowY = (int) (mouseButtonEvent.y() - this.dragY);

            for (ImageButton button : buttons) {
                button.setPosition(
                        this.windowX + (int) relativePos.get(buttons.indexOf(button)).x,
                        this.windowY + (int) relativePos.get(buttons.indexOf(button)).y);
            }

            return true;
        }

        return super.mouseDragged(mouseButtonEvent, d, e);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.button() == 0) {
            this.isDragging = false;
        }
        return super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
