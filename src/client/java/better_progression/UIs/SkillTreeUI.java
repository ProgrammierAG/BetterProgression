package better_progression.UIs;

import better_progression.BetterProgression;
import better_progression.networking.SkillUnlockPayload;
import better_progression.Attachments;
import better_progression.skillLogic.SkillTree;
import better_progression.skills.Skill;
import better_progression.skills.Skills;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkillTreeUI extends Screen {

    private int windowX = 0;
    private int windowY = 0;

    private boolean isDragging = false;
    private double dragX = 0;
    private double dragY = 0;

    private List<ImageButton> buttons = new ArrayList<>();
    private Map<ImageButton, Vec2> positions = new HashMap<>();
    private Map<ImageButton, String> IDs = new HashMap<>();
    private final int spacing = 40;



    public SkillTreeUI() {
        super(Component.literal("SkillTreeUI"));
    }

    @Override
    protected void init() {
        buttons.clear();
        positions.clear();
        IDs.clear();
        // Buttons:

        SkillTree.skillButtons.keySet().forEach(id -> {
            int baseX = (int) (spacing * SkillTree.xLayer.get(id));
            int baseY = spacing * SkillTree.yLayer.get(id);

            if (SkillTree.isChoiceNode(id)) {
                String partner = SkillTree.getChoicePartner(id);
                int offset = (id.compareTo(partner) < 0) ? -12 : 12;
                this.genSkillButton(baseX + offset, baseY, 20, 20, id);
            } else {
                this.genSkillButton(baseX, baseY, 20, 20, id);
            }
        });

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xA0101010, 0xD0101010);

        assert Minecraft.getInstance().player != null;
        List<String> unlocked_skills = Minecraft.getInstance().player.getAttached(Attachments.UNLOCKED_SKILLS);
        List<String> activeUnlocked = unlocked_skills != null ? unlocked_skills : List.of();

        buttons.forEach(button -> {
            int x = (int) positions.get(button).x + windowX;
            int y = (int) positions.get(button).y + windowY;

            button.setPosition(x, y);
        });

        buttons.forEach(button -> {
            String id = IDs.get(button);
            int x = (int) (positions.get(button).x + windowX);
            int y = (int) (positions.get(button).y + windowY);

            Optional.ofNullable(SkillTree.getParents(id)).ifPresent(parents ->
                    parents.forEach(parent -> {
                        buttons.stream()
                                .filter(b -> IDs.get(b).equals(parent))
                                .findFirst()
                                .ifPresent(parentButton -> {
                                    int parX = (int) (positions.get(parentButton).x + windowX);
                                    int parY = (int) (positions.get(parentButton).y + windowY);

                                    int lineColor;

                                    if (isBlockedByChoice(id, activeUnlocked) ||
                                            isBlockedByChoice(parent, activeUnlocked)) {
                                        lineColor = 0x40FF0000;
                                    }
                                    else if (activeUnlocked.contains(parent) &&
                                            activeUnlocked.contains(id)) {
                                        lineColor = 0xFF55FF55;
                                    }
                                    else {
                                        lineColor = 0x40888888;
                                    }

                                    drawLine(guiGraphics, x + 10, y + 10,
                                            parX + 10, parY + 10, 2, lineColor);
                                });
                    })
            );
        });

        buttons.forEach(button -> {
            int x = (int) positions.get(button).x + windowX;
            int y = (int) positions.get(button).y + windowY;
            String id = IDs.get(button);

            boolean isUnlocked = activeUnlocked.contains(id);

            boolean isChoiceBlocked = isBlockedByChoice(id, activeUnlocked);

            if (isChoiceBlocked) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_BLOCKED,
                        x - 2, y - 2, 24, 24);
            } else if (isUnlocked) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_UNLOCKED,
                        x - 2, y - 2, 24, 24);
            } else {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND,
                        x - 2, y - 2, 24, 24);
            }
        });

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private boolean isBlockedByChoice(String id, List<String> activeUnlocked) {
        if (SkillTree.isChoiceNode(id) && activeUnlocked.contains(SkillTree.getChoicePartner(id))) {
            return true;
        }

        List<String> parents = SkillTree.parents.get(id);
        if (parents == null || parents.isEmpty()) {
            return false;
        }

        return parents.stream().allMatch(parent -> isBlockedByChoice(parent, activeUnlocked));
    }

    public void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int width, int color) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x1, y1);
        float angle = (float) Math.atan2(y2 - y1, x2 - x1);
        guiGraphics.pose().rotateAbout(angle, 0, 0);
        float length = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        guiGraphics.fill(0, -width/2, (int)length, width/2, color);
        guiGraphics.pose().popMatrix();
    }

    public void genSkillButton(int x, int y, int width, int height, String id) {
        BetterProgression.getLogger().info("generating new SkillButton for: {}", id);
        Skill skill = SkillTree.skillButtons.get(id);
        WidgetSprites icon = new WidgetSprites(skill.iconId());
        ImageButton Button = new ImageButton(
                x, y, width, height,
                icon,
                button -> {
                    assert Minecraft.getInstance().player != null;
                    List<String> unlocked = Minecraft.getInstance().player.getAttached(Attachments.UNLOCKED_SKILLS);

                    if (SkillTree.isChoiceNode(id) && unlocked != null &&
                            unlocked.contains(SkillTree.getChoicePartner(id))) {
                        return;
                    }

                    BetterProgression.getLogger().info("sending Payload for: {}", id);
                    ClientPlayNetworking.send(new SkillUnlockPayload(id));
                }
        );
        Button.setTooltip(Tooltip.create(Component.translatable(skill.id())));
        this.addRenderableWidget(Button);
        this.buttons.add(Button);
        this.positions.put(Button, new Vec2(x, y));
        this.IDs.put(Button, id);
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
    public boolean mouseDragged(@NotNull MouseButtonEvent mouseButtonEvent, double d, double e) {
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