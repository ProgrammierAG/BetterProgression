package better_progression.UIs;

import better_progression.Attachments;
import better_progression.networking.SkillUnlockPayload;
import better_progression.rendering.RenderCommand;
import better_progression.rendering.SkillNodeRenderer;
import better_progression.skillTree.SkillTree;
import better_progression.skillTree.nodeTypes.Node;
import better_progression.skills.Skills;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;

public class SkillTreeUI extends Screen {
    private int windowX = 0;
    private int windowY = 0;
    private boolean isDragging = false;
    private double dragX = 0;
    private double dragY = 0;

    private float zoom = 1.0f;
    private final float minZoom = 0.5f;
    private final float maxZoom = 2.0f;

    private final List<Node> allGuiNodes = new ArrayList<>();
    private final Map<Node, ImageButton> nodeButtons = new HashMap<>();

    private Node globalRootNode;
    private final int spacing = 40;

    private final List<RenderCommand> cachedRenderQueue = new ArrayList<>();

    public SkillTreeUI() {
        super(Component.literal("SkillTreeUI"));
    }

    @Override
    protected void init() {
        allGuiNodes.clear();
        nodeButtons.clear();
        cachedRenderQueue.clear();
        this.clearWidgets();

        this.globalRootNode = new Node("GLOBAL_ROOT", null, 0);
        ImageButton rootBtn = SkillNodeRenderer.createButton(this.globalRootNode, 0, 0, this::onNodeClicked);
        this.addRenderableWidget(rootBtn);
        nodeButtons.put(this.globalRootNode, rootBtn);
        allGuiNodes.add(this.globalRootNode);

        final float[] treeOffsetShift = { 0.0f };
        SkillTree.REGISTRY.values().forEach(tree -> {
            float currentTreeOffset = treeOffsetShift[0];

            tree.getNodes().values().forEach(node -> {
                int renderX = (int) (spacing * (node.getXLayer() + currentTreeOffset));
                int renderY = spacing * (node.getYLayer() + 1);

                ImageButton btn = SkillNodeRenderer.createButton(node, renderX, renderY, this::onNodeClicked);
                this.addRenderableWidget(btn);
                nodeButtons.put(node, btn);
                allGuiNodes.add(node);
            });

            double maxTreeWidth = tree.getNodes().values().stream().mapToDouble(n -> Math.abs(n.getXLayer())).max().orElse(1.0);
            treeOffsetShift[0] += (float) ((maxTreeWidth * 2) + 2.0f);
        });

        assert Minecraft.getInstance().player != null;
        List<String> dummyList = List.of();

        allGuiNodes.forEach(node -> node.generateRenderCommands(
                cachedRenderQueue, 0, 0, dummyList, 0, globalRootNode
        ));

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xA0101010, 0xB0101010);

        assert Minecraft.getInstance().player != null;
        List<String> activeUnlocked = Minecraft.getInstance().player.getAttachedOrElse(Attachments.UNLOCKED_SKILLS, List.of());
        int currentPoints = Minecraft.getInstance().player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);

        allGuiNodes.forEach(node -> {
            ImageButton btn = nodeButtons.get(node);
            if (btn != null) {
                int actualX = (int) (this.width / 2.0f + (node.getXPos() + windowX) * zoom);
                int actualY = (int) (this.height / 2.0f + (node.getYPos() + windowY) * zoom);
                btn.setPosition(actualX, actualY);
                btn.setWidth((int) (20 * zoom));
                btn.setHeight((int) (20 * zoom));
            }
        });

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.width / 2.0f, this.height / 2.0f);
        guiGraphics.pose().scale(zoom, zoom);

        for (RenderCommand command : cachedRenderQueue) {
            if (command instanceof RenderCommand.Line line) {
                Node targetNode = line.getTargetNode();

                int endX = targetNode.getIncomingAnchorX() + windowX;
                int endY = targetNode.getIncomingAnchorY() + windowY;

                Node parentNode = allGuiNodes.stream()
                        .filter(n -> (n.getXPos() + 10) == line.getStartX() && (n.getYPos() + 10) == line.getStartY())
                        .findFirst().orElse(null);

                int lineColor = 0xFF555555;
                if (parentNode != null) {
                    if (activeUnlocked.contains(parentNode.getId()) && activeUnlocked.contains(targetNode.getId())) {
                        lineColor = 0xFF55FF55;
                    }
                } else if (activeUnlocked.contains(targetNode.getId())) {
                    lineColor = 0xFF55FF55;
                }

                this.drawLine(guiGraphics, line.getStartX() + windowX, line.getStartY() + windowY, endX, endY, lineColor);
            }
        }

        for (RenderCommand command : cachedRenderQueue) {
            if (command instanceof RenderCommand.Background bg) {
                Node node = bg.getNode();

                boolean isUnlocked = activeUnlocked.contains(node.getId()) || (node.getSkill() == null);
                boolean hasRequiredParent = node.getParents().isEmpty() || node.getParents().stream().anyMatch(p -> activeUnlocked.contains(p.getId()));boolean hasEnoughPoints = currentPoints >= node.getCost();

                Identifier sprite = Skills.BUTTON_BACKGROUND_UNOBTAINED;
                if (isUnlocked) sprite = Skills.BUTTON_BACKGROUND_OBTAINED;
                else if (!hasRequiredParent || !hasEnoughPoints) sprite = Skills.BUTTON_BACKGROUND_UNOBTAINABLE;

                int drawX = node.getXPos() + windowX;
                int drawY = node.getYPos() + windowY;

                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, drawX - 2, drawY - 2, 24, 24);
            }
        }

        guiGraphics.pose().popMatrix();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        float oldZoom = zoom;

        if (scrollY > 0) {
            zoom = Math.min(maxZoom, zoom + 0.1f);
        } else if (scrollY < 0) {
            zoom = Math.max(minZoom, zoom - 0.1f);
        }

        if (zoom != oldZoom) {
            double mouseInTreeX = (mouseX - this.width / 2.0) / oldZoom - windowX;
            double mouseInTreeY = (mouseY - this.height / 2.0) / oldZoom - windowY;

            this.windowX = (int) ((mouseX - this.width / 2.0) / zoom - mouseInTreeX);
            this.windowY = (int) ((mouseY / zoom) - mouseInTreeY);
        }

        return true;
    }

    private void onNodeClicked(Node node) {
        if (node.getSkill() == null) return;
        ClientPlayNetworking.send(new SkillUnlockPayload(node.getId()));
    }

    public Optional<Node> getGlobalRootNode() {
        return Optional.ofNullable(this.globalRootNode);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (event.button() == 0) {
            this.isDragging = true;

            this.dragX = (event.x() / zoom) - windowX;
            this.dragY = (event.y() / zoom) - windowY;

            if (super.mouseClicked(event, bl)) return true;
        }
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double d, double e) {
        if (this.isDragging && event.button() == 0) {
            int newWindowX = (int) ((event.x() / zoom) - this.dragX);
            int newWindowY = (int) ((event.y() / zoom) - this.dragY);

            int minTreeX = allGuiNodes.stream().mapToInt(Node::getXPos).min().orElse(-100) - 100;
            int maxTreeX = allGuiNodes.stream().mapToInt(Node::getXPos).max().orElse(100) + 100;
            int minTreeY = allGuiNodes.stream().mapToInt(Node::getYPos).min().orElse(-100) - 100;
            int maxTreeY = allGuiNodes.stream().mapToInt(Node::getYPos).max().orElse(100) + 100;

            int limitXLeft = (int) (-(maxTreeX) - (this.width / (2.0f * zoom)));
            int limitXRight = (int) (-(minTreeX) + (this.width / (2.0f * zoom)));
            int limitYTop = (int) (-(maxTreeY) - (this.height / (2.0f * zoom)));
            int limitYBottom = (int) (-(minTreeY) + (this.height / (2.0f * zoom)));

            this.windowX = Math.max(limitXLeft, Math.min(limitXRight, newWindowX));
            this.windowY = Math.max(limitYTop, Math.min(limitYBottom, newWindowY));

            return true;
        }
        return super.mouseDragged(event, d, e);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            this.isDragging = false;
        }
        return super.mouseReleased(event);
    }

    public void drawLine(GuiGraphics guiGraphics, float x1, float y1, float x2, float y2, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float lineWidth = 2.0f;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x1, y1);

        float angle = (float) Math.atan2(dy, dx);
        guiGraphics.pose().rotateAbout(angle, 0, 0);

        float length = (float) Math.sqrt(dx * dx + dy * dy);
        guiGraphics.fill(0, (int)(-lineWidth / 2.0f), (int)length, (int)(lineWidth / 2.0f), color);

        guiGraphics.pose().popMatrix();
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
