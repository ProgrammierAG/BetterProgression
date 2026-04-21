package better_progression.UIs;

import better_progression.BetterProgression;
import better_progression.networking.SkillUnlockPayload;
import better_progression.skills.Skill;
import better_progression.skills.Skills;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class SkillTreeUI extends Screen {

    private int windowX = 0;
    private int windowY = 0;

    private boolean isDragging = false;
    private double dragX = 0;
    private double dragY = 0;

    private List<ImageButton> buttons = new ArrayList<>();
    private List<Vec2> relativePos = new ArrayList<>();
    private List<String> SkillIDs = new ArrayList<>();



    public SkillTreeUI() {
        super(Component.literal("SkillTreeUI"));
    }

    @Override
    protected void init() {
        // Buttons:

        final int[] offset = {0};
        for (Skill skill : Skills.SKILLS.values()) {

        }

        Skills.SKILLS.values().stream().forEach(skill -> {
            Identifier ICON = skill.icon();
            WidgetSprites widget = new WidgetSprites(ICON);
            this.genSkillButton(offset[0], 0, 20, 20, widget, skill);
            offset[0] += 25;
        });

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xA0101010, 0xB0101010);

        buttons.stream().forEach(button -> {
            int x = ((int) relativePos.get(buttons.indexOf(button)).x) + windowX;
            int y = ((int) relativePos.get(buttons.indexOf(button)).y) + windowY;

            button.setPosition(x, y);

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND,
                    x - 2, y - 2, 24, 24);
        });

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int width, int color) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x1, y1, 0);
        float angle = (float) Math.atan2(y2 - y1, x2 - x1);
        guiGraphics.pose().rotateAbout(angle, 0, 0);
        float length = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
// Zeichne ein horizontales Rechteck, das durch die Rotation diagonal wird
        guiGraphics.fill(0, (int)(-thickness/2), (int)length, (int)(thickness/2), color);
        guiGraphics.pose().pop();
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
        this.SkillIDs.add(skill.NAME_ID());
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
