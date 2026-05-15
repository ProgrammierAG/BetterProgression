package better_progression.UIs;

import better_progression.Attachments;
import better_progression.BetterProgression;
import better_progression.networking.SkillUnlockPayload;
import better_progression.skillTree.SkillTree;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkillTreeUI extends Screen {
    private int windowX = 0;
    private int windowY = 0;

    private boolean isDragging = false;
    private double dragX = 0;
    private double dragY = 0;

    private final List<ImageButton> buttons = new ArrayList<>();
    private final Map<ImageButton, Vec2> positions = new HashMap<>();
    private final Map<ImageButton, String> IDs = new HashMap<>();

    private final Map<ImageButton, SkillTree> buttonTrees = new HashMap<>();

    private final int spacing = 40;

    public SkillTreeUI() {
        super(Component.literal("SkillTreeUI"));
    }

    @Override
    protected void init() {
        buttons.clear();
        positions.clear();
        IDs.clear();
        buttonTrees.clear();

        this.genGlobalRootButton(0, 0);

        float[] treeOffsetShift = { 0.0f };

        SkillTree.REGISTRY.values().forEach(tree -> {
            float currentTreeOffset = treeOffsetShift[0];

            tree.getSkillButtons().keySet().forEach(id -> {
                int baseX = (int) (spacing * (tree.getXLayer().get(id) + currentTreeOffset));
                int baseY = spacing * (tree.getYLayer().get(id) + 1);

                if (tree.isChoiceNode(id)) {
                    String partner = tree.getChoicePartner(id);
                    int offset = (id.compareTo(partner) < 0) ? -12 : 12;
                    this.genSkillButton(baseX + offset, baseY, 20, 20, id, tree);
                } else {
                    this.genSkillButton(baseX, baseY, 20, 20, id, tree);
                }
            });

            double maxTreeWidth = tree.getXLayer().values().stream().mapToDouble(Math::abs).max().orElse(1.0);
            treeOffsetShift[0] += (float) ((maxTreeWidth * 2) + 2.0f);
        });

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xA0101010, 0xB0101010);

        assert Minecraft.getInstance().player != null;
        List<String> unlocked_skills = Minecraft.getInstance().player.getAttached(Attachments.UNLOCKED_SKILLS);
        List<String> activeUnlocked = unlocked_skills != null ? unlocked_skills : List.of();

        // positions the buttons
        buttons.forEach(button -> button.setPosition((int) positions.get(button).x + windowX, (int) positions.get(button).y + windowY));

        // connects the buttons
        buttons.forEach(button -> {
            String id = IDs.get(button);
            SkillTree tree = buttonTrees.get(button);

            if (id.equals("GLOBAL_ROOT")) return;
            if (tree != null && tree.isChoiceNode(id) && id.compareTo(tree.getChoicePartner(id)) >= 0) return;

            int calcX = (int) (positions.get(button).x + windowX);
            int calcY = (int) (positions.get(button).y + windowY);
            if (tree != null && tree.isChoiceNode(id)) calcX += 12;

            final int targetX = calcX + 10;
            final int targetY = calcY + 10;

            if (tree != null && tree.getRootNodes().contains(id)) {
                buttons.stream().filter(b -> IDs.get(b).equals("GLOBAL_ROOT")).findFirst().ifPresent(rootBtn -> {
                    int startX = (int) (positions.get(rootBtn).x + windowX + 10);
                    int startY = (int) (positions.get(rootBtn).y + windowY + 10);

                    int lineColor = activeUnlocked.contains(id) ? 0xFF55FF55 : 0xFF555555;
                    drawLine(guiGraphics, targetX, targetY, startX, startY, 2, lineColor);
                });
            }

            if (tree != null) {
                Optional.ofNullable(tree.getParents(id)).ifPresent(parents -> parents.forEach(parent -> {
                    buttons.stream().filter(b -> IDs.get(b).equals(parent) && buttonTrees.get(b) == tree).findFirst().ifPresent(parentButton -> {
                        int parX = (int) (positions.get(parentButton).x + windowX);
                        int parY = (int) (positions.get(parentButton).y + windowY);

                        int startX = parX + 10;
                        int startY = parY + 10;

                        int lineColor = isBlockedByChoice(id, tree, activeUnlocked) || isBlockedByChoice(parent, tree, activeUnlocked) ? 0xFF880000
                                : activeUnlocked.contains(parent) && activeUnlocked.contains(id) ? 0xFF55FF55 : 0xFF555555;

                        drawLine(guiGraphics, targetX, targetY, startX, startY, 2, lineColor);
                    });
                }));
            }
        });

        // draws backgrounds for choices
        buttons.stream().filter(b -> buttonTrees.get(b) != null)
                .filter(b -> buttonTrees.get(b).isChoiceNode(IDs.get(b)))
                .filter(b -> IDs.get(b).compareTo(buttonTrees.get(b).getChoicePartner(IDs.get(b))) < 0)
                .forEach(button -> {
                    String id = IDs.get(button);
                    SkillTree tree = buttonTrees.get(button);
                    ImageButton btnB = buttons.stream().filter(b -> IDs.get(b).equals(tree.getChoicePartner(id)) && buttonTrees.get(b) == tree).findFirst().orElse(null);
                    if (btnB != null) {
                        int minX = Math.min(button.getX(), btnB.getX()) - 2;
                        int maxX = Math.max(button.getX(), btnB.getX()) + button.getWidth() + 2;
                        int minY = Math.min(button.getY(), btnB.getY()) - 2;
                        int maxY = Math.max(button.getY(), btnB.getY()) + button.getHeight() + 2;

                        int frameColor = activeUnlocked.contains(id) || activeUnlocked.contains(tree.getChoicePartner(id)) ? 0xFF55FF55
                                : isBlockedByChoice(id, tree, activeUnlocked) ? 0xFF880000 : 0xFFFFAA00;

                        guiGraphics.fill(minX, minY, maxX, minY + 2, frameColor);
                        guiGraphics.fill(minX, maxY - 2, maxX, maxY, frameColor);
                        guiGraphics.fill(minX, minY + 2, minX + 2, maxY - 2, frameColor);
                        guiGraphics.fill(maxX - 2, minY + 2, maxX, maxY - 2, frameColor);
                    }
                });

        // draws backgrounds
        buttons.forEach(button -> {
            int x = (int) positions.get(button).x + windowX;
            int y = (int) positions.get(button).y + windowY;
            String id = IDs.get(button);
            SkillTree tree = buttonTrees.get(button);

            if (id.equals("GLOBAL_ROOT")) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_OBTAINED, x - 2, y - 2, 24, 24);
                return;
            }

            boolean isUnlocked = activeUnlocked.contains(id);
            boolean isChoiceBlocked = isBlockedByChoice(id, tree, activeUnlocked);

            List<String> parents = tree.getParents(id);
            boolean hasRequiredParent = (parents == null || parents.isEmpty()) || parents.stream().anyMatch(activeUnlocked::contains);
            int currentPoints = Minecraft.getInstance().player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);
            boolean hasEnoughPoints = currentPoints >= tree.getCost().getOrDefault(id, 0);

            if (isUnlocked) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_OBTAINED, x - 2, y - 2, 24, 24);
            } else if (isChoiceBlocked) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_BLOCKED, x - 2, y - 2, 24, 24);
            } else if (!hasRequiredParent || !hasEnoughPoints) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_UNOBTAINABLE, x - 2, y - 2, 24, 24);
            } else {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_UNOBTAINED, x - 2, y - 2, 24, 24);
            }
        });

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private boolean isBlockedByChoice(String id, SkillTree tree, List<String> activeUnlocked) {
        if (activeUnlocked.contains(id)) return false;

        if (tree.isChoiceNode(id) && activeUnlocked.contains(tree.getChoicePartner(id))) {
            return true;
        }

        List<String> parents = tree.getParents(id);
        if (parents == null || parents.isEmpty()) {
            return false;
        }

        return parents.stream().allMatch(parent -> isBlockedByChoice(parent, tree, activeUnlocked));
    }

    public void genGlobalRootButton(int x, int y) {
        WidgetSprites icon = new WidgetSprites(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "skillbook"));
        ImageButton button = new ImageButton(x, y, 20, 20, icon, b -> {});
        button.setTooltip(Tooltip.create(Component.translatable("tooltip.betterprogression.global_root").withStyle(net.minecraft.ChatFormatting.GOLD)));
        this.addRenderableWidget(button);
        this.buttons.add(button);
        this.positions.put(button, new Vec2(x, y));
        this.IDs.put(button, "GLOBAL_ROOT");
    }

    public void genSkillButton(int x, int y, int width, int height, String id, SkillTree tree) {
        Skill skill = tree.getSkillButtons().get(id);
        ImageButton button = new ImageButton(x, y, width, height,
                new WidgetSprites(skill.iconId()), b -> {
            List<String> activeUnlocked = Minecraft.getInstance().player.getAttached(Attachments.UNLOCKED_SKILLS);
            List<String> unlockedList = (activeUnlocked != null) ? activeUnlocked : List.of();

            // 1. Bereits gekauft? oder durch Choice blockiert? -> Abbrechen
            if (unlockedList.contains(id) || isBlockedByChoice(id, tree, unlockedList)) return;

            // 2. Eltern-Bedingung prüfen
            List<String> parents = tree.getParents(id);
            if (parents != null && !parents.isEmpty()) {
                // Wenn der Skill echte Eltern hat, muss mindestens einer davon gekauft sein
                boolean hasUnlockedParent = parents.stream().anyMatch(unlockedList::contains);
                if (!hasUnlockedParent) {
                    return; // Abbrechen, da der Vorgänger fehlt
                }
            }
            // HINWEIS: Wenn 'parents' null oder leer ist, ist es ein Startknoten.
            // Er hängt an der GLOBAL_ROOT und überspringt diese Prüfung automatisch!

            // 3. Kosten prüfen
            if (Minecraft.getInstance().player.getAttachedOrElse(Attachments.SKILLPOINTS, 0) < tree.getCost().get(id)) return;

            // Paket an den Server senden
            ClientPlayNetworking.send(new SkillUnlockPayload(id));
        });

        button.setTooltip(Tooltip.create(Component.translatable(skill.id()).withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("\n")).append(Component.translatable(skill.desc_id()).withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC))
                .append(Component.literal("\n\n")).append(Component.translatable("tooltip.betterprogression.cost", tree.getCost().getOrDefault(id, 0)).withStyle(net.minecraft.ChatFormatting.DARK_GREEN))));

        this.addRenderableWidget(button);
        this.buttons.add(button);
        this.positions.put(button, new Vec2(x, y));
        this.IDs.put(button, id);
        this.buttonTrees.put(button, tree);
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
