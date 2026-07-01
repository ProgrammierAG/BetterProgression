package better_progression.UIs;

import better_progression.Attachments;
import better_progression.networking.SkillUnlockPayload;
import better_progression.rendering.RenderCommand;
import better_progression.rendering.SkillNodeRenderer;
import better_progression.skillTree.SkillTree;
import better_progression.skillTree.nodeTypes.ChoiceNode;
import better_progression.skillTree.nodeTypes.Node;
import better_progression.skills.Skills;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;

public class SkillTreeUI extends Screen {
    // View transform
    private int windowX = 0;
    private int windowY = 0;
    private float zoom = 1.0f;
    private final float minZoom = 0.5f;
    private final float maxZoom = 2.5f;

    // Dragging
    private boolean dragging = false;
    private double dragStartX = 0;
    private double dragStartY = 0;

    // Nodes and widgets
    private final List<Node> allNodes = new ArrayList<>();
    private final Map<Node, List<ImageButton>> nodeButtons = new HashMap<>();
    private final List<RenderCommand> renderQueue = new ArrayList<>();

    private final int spacing = 48;
    private Node globalRoot;

    public SkillTreeUI() {
        super(Component.literal("SkillTree"));
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.allNodes.clear();
        this.nodeButtons.clear();
        this.renderQueue.clear();

        // Ensure skill trees have calculated layers
        for (SkillTree tree : SkillTree.REGISTRY.values()) {
            tree.calcLayers();
        }

        // create a virtual global root for nodes without parents
        this.globalRoot = new Node("GLOBAL_ROOT", null, 0);
        this.globalRoot.setXPos(0);
        this.globalRoot.setYPos(0);
        this.allNodes.add(this.globalRoot);

        float[] treeOffset = {0f};

        for (SkillTree tree : SkillTree.REGISTRY.values()) {
            double maxWidth = tree.getNodes().values().stream().mapToDouble(n -> Math.abs(n.getXLayer())).max().orElse(1.0);

            for (Node node : tree.getNodes().values()) {
                // compute screen positions based on layers and per-tree offset
                int x = (int) ((node.getXLayer() + treeOffset[0]) * spacing);
                int y = (int) ((node.getYLayer() + 1) * spacing);
                node.setXPos(x);
                node.setYPos(y);

                // create buttons for node
                if (node instanceof ChoiceNode choice) {
                    ImageButton left = SkillNodeRenderer.createButtonForChoiceHalf(choice, x - 10, y, true, this::onChoiceClicked);
                    ImageButton right = SkillNodeRenderer.createButtonForChoiceHalf(choice, x + 10, y, false, this::onChoiceClicked);

                    this.addRenderableWidget(left);
                    this.addRenderableWidget(right);

                    nodeButtons.put(node, Arrays.asList(left, right));
                } else {
                    ImageButton btn = SkillNodeRenderer.createButton(node, x, y, this::onNodeClicked);
                    this.addRenderableWidget(btn);
                    nodeButtons.put(node, Collections.singletonList(btn));
                }

                allNodes.add(node);
            }

            treeOffset[0] += (float) ((maxWidth * 2.0) + 2.0);
        }

        // Prepare render queue once (we will update dynamic coloring in render())
        for (Node n : allNodes) {
            n.generateRenderCommands(renderQueue, 0, 0, Collections.emptyList(), 0, globalRoot);
        }

        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // background
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xA0101010, 0xB0101010);

        // read dynamic player state
        var player = Minecraft.getInstance().player;
        List<String> unlockedRaw = (player == null) ? Collections.emptyList() : player.getAttachedOrCreate(Attachments.UNLOCKED_SKILLS, ArrayList::new);
        // Treat the virtual global root as unlocked for client-side rendering so that
        // nodes without parents appear available. Do not modify server-side data.
        List<String> unlocked = new ArrayList<>(unlockedRaw);
        if (this.globalRoot != null && !unlocked.contains(this.globalRoot.getId())) {
            unlocked.add(this.globalRoot.getId());
        }
        int currentPoints = (player == null) ? 0 : player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);

        // update widget positions and sizes according to transform
        for (var entry : nodeButtons.entrySet()) {
            Node node = entry.getKey();
            List<ImageButton> buttons = entry.getValue();

            int baseX = (int) (this.width / 2.0 + (node.getXPos() + windowX) * zoom);
            int baseY = (int) (this.height / 2.0 + (node.getYPos() + windowY) * zoom);

            if (buttons.size() == 2) {
                ImageButton left = buttons.get(0);
                ImageButton right = buttons.get(1);

                left.setPosition((int) (baseX - 10 * zoom), (int) (baseY));
                right.setPosition((int) (baseX + 10 * zoom), (int) (baseY));

                left.setWidth((int) (20 * zoom)); left.setHeight((int) (20 * zoom));
                right.setWidth((int) (20 * zoom)); right.setHeight((int) (20 * zoom));
            } else if (buttons.size() == 1) {
                ImageButton btn = buttons.get(0);
                btn.setPosition(baseX, baseY);
                btn.setWidth((int) (20 * zoom));
                btn.setHeight((int) (20 * zoom));
            }
        }

        // render connections and backgrounds using transformed coordinates
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.width / 2.0f, this.height / 2.0f);
        guiGraphics.pose().scale(zoom, zoom);

        // lines
        for (RenderCommand cmd : renderQueue) {
            if (cmd instanceof RenderCommand.Line line) {
                Node target = line.getTargetNode();
                int startX = line.getStartX() + windowX;
                int startY = line.getStartY() + windowY;
                int endX = target.getIncomingAnchorX() + windowX;
                int endY = target.getIncomingAnchorY() + windowY;

                int color = 0xFF555555;

                // try to locate parent node for color logic
                Node parent = allNodes.stream().filter(n -> (n.getIncomingAnchorX() == line.getStartX()) && (n.getIncomingAnchorY() == line.getStartY())).findFirst().orElse(null);

                if (parent != null) {
                    if (Node.isBlockedByChoice(parent, unlocked) || Node.isBlockedByChoice(target, unlocked)) {
                        color = 0xFF880000;
                    } else {
                        boolean pUnlocked = unlocked.contains(parent.getId()) || (parent.getSkill() == null);
                        boolean tUnlocked = unlocked.contains(target.getId());
                        if (target instanceof ChoiceNode cp) tUnlocked = cp.getUnlockState(unlocked) > 0;
                        if (pUnlocked && tUnlocked) color = 0xFF55FF55;
                    }
                } else {
                    boolean tUnlocked = unlocked.contains(target.getId());
                    if (target instanceof ChoiceNode cp) tUnlocked = cp.getUnlockState(unlocked) > 0;
                    if (tUnlocked) color = 0xFF55FF55;
                }

                drawLine(guiGraphics, startX, startY, endX, endY, color);
            }
        }

        // choice backgrounds (frames)
        for (RenderCommand cmd : renderQueue) {
            if (cmd instanceof RenderCommand.ChoiceBackground bg) {
                ChoiceNode choice = bg.getChoiceNode();
                int state = choice.getUnlockState(unlocked);

                int x = bg.getX() + windowX;
                int y = bg.getY() + windowY;

                int minX = x - 10 - 2;
                int maxX = x + 10 + 20 + 2;
                int minY = y - 2;
                int maxY = y + 20 + 2;

                int frame = 0xFFFFAA00;
                if (state > 0) frame = 0xFF55FF55;
                else if (Node.isBlockedByChoice(choice, unlocked)) frame = 0xFF880000;

                guiGraphics.fill(minX, minY, maxX, minY + 2, frame);
                guiGraphics.fill(minX, maxY - 2, maxX, maxY, frame);
                guiGraphics.fill(minX, minY + 2, minX + 2, maxY - 2, frame);
                guiGraphics.fill(maxX - 2, minY + 2, maxX, maxY - 2, frame);
            }
        }

        // backgrounds (button sprites)
        for (RenderCommand cmd : renderQueue) {
            if (cmd instanceof RenderCommand.Background bg) {
                Node n = bg.getNode();
                boolean isUnlocked = unlocked.contains(n.getId()) || (n.getSkill() == null);
                boolean hasParent = n.getParents().isEmpty() || n.getParents().stream().anyMatch(p -> unlocked.contains(p.getId()));
                boolean hasPoints = currentPoints >= n.getCost();

                Identifier sprite = Skills.BUTTON_BACKGROUND_UNOBTAINED;
                if (isUnlocked) sprite = Skills.BUTTON_BACKGROUND_OBTAINED;
                else if (!hasParent || !hasPoints) sprite = Skills.BUTTON_BACKGROUND_UNOBTAINABLE;

                int drawX = n.getXPos() + windowX - 2;
                int drawY = n.getYPos() + windowY - 2;
                guiGraphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, sprite, drawX, drawY, 24, 24);
            }
        }

        guiGraphics.pose().popMatrix();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void onNodeClicked(Node node) {
        if (node == null) return;
        if (node.getSkill() == null) return;
        ClientPlayNetworking.send(new SkillUnlockPayload(node.getId()));
    }

    private void onChoiceClicked(Node node) {
        if (node == null) return;
        ClientPlayNetworking.send(new SkillUnlockPayload(node.getId()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        float old = zoom;
        if (deltaY > 0) zoom = Math.min(maxZoom, zoom + 0.1f);
        else if (deltaY < 0) zoom = Math.max(minZoom, zoom - 0.1f);

        if (zoom != old) {
            double treeX = (mouseX - this.width / 2.0) / old - windowX;
            double treeY = (mouseY - this.height / 2.0) / old - windowY;

            this.windowX = (int) ((mouseX - this.width / 2.0) / zoom - treeX);
            this.windowY = (int) ((mouseY - this.height / 2.0) / zoom - treeY);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouse, boolean bl) {
        int button = mouse.button();
        double mouseX = mouse.x();
        double mouseY = mouse.y();
        if (button == 0) {
            this.dragging = true;
            this.dragStartX = mouseX / zoom - windowX;
            this.dragStartY = mouseY / zoom - windowY;
            return super.mouseClicked(mouse, bl);
        }
        return super.mouseClicked(mouse, bl);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouse, double dx, double dy) {
        int button = mouse.button();
        double mouseX = mouse.x();
        double mouseY = mouse.y();
        if (this.dragging && button == 0) {
            int newX = (int) ((mouseX / zoom) - this.dragStartX);
            int newY = (int) ((mouseY / zoom) - this.dragStartY);

            int minX = allNodes.stream().mapToInt(Node::getXPos).min().orElse(-200) - 100;
            int maxX = allNodes.stream().mapToInt(Node::getXPos).max().orElse(200) + 100;
            int minY = allNodes.stream().mapToInt(Node::getYPos).min().orElse(-200) - 100;
            int maxY = allNodes.stream().mapToInt(Node::getYPos).max().orElse(200) + 100;

            int limitLeft = (int) (-(maxX) - (this.width / (2.0f * zoom)));
            int limitRight = (int) (-(minX) + (this.width / (2.0f * zoom)));
            int limitTop = (int) (-(maxY) - (this.height / (2.0f * zoom)));
            int limitBottom = (int) (-(minY) + (this.height / (2.0f * zoom)));

            this.windowX = Math.max(limitLeft, Math.min(limitRight, newX));
            this.windowY = Math.max(limitTop, Math.min(limitBottom, newY));

            return true;
        }
        return super.mouseDragged(mouse, dx, dy);
    }


    @Override
    public boolean mouseReleased(MouseButtonEvent mouse) {
        if (mouse.button() == 0) this.dragging = false;
        return super.mouseReleased(mouse);
    }

    private void drawLine(GuiGraphics g, float x1, float y1, float x2, float y2, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float angle = (float) Math.atan2(dy, dx);
        float len = (float) Math.sqrt(dx * dx + dy * dy);

        g.pose().pushMatrix();
        g.pose().translate(x1, y1);
        g.pose().rotate(angle);
        //g.pose().rotateZ(angle);
        g.fill(0, (int) (-1), (int) len, (int) 1, color);
        g.pose().popMatrix();
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
