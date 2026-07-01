package better_progression.skillTree.nodeTypes;

import better_progression.rendering.RenderCommand;
import better_progression.skills.Skill;


import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String id;
    private final Skill skill;
    private final int cost;

    private final List<Node> parents = new ArrayList<>();
    private final List<Node> children = new ArrayList<>();

    private int yLayer = 0;
    private double xLayer = 0.0;

    private int xPos;
    private int yPos;

    public Node(String id, Skill skill, int cost) {
        this.id = id;
        this.skill = skill;
        this.cost = cost;
    }

    public int getIncomingAnchorX() {
        return this.xPos + 10;
    }

    public int getIncomingAnchorY() {
        return this.yPos + 10;
    }

    public void generateRenderCommands(List<RenderCommand> commandList, int windowX, int windowY, List<String> activeUnlocked, int currentPoints, Node globalRoot) {
        int myX = this.xPos + windowX;
        int myY = this.yPos + windowY;

        if (this.parents.isEmpty() && !this.id.equals("GLOBAL_ROOT") && globalRoot != null) {
            int lineColor = activeUnlocked.contains(this.id) ? 0xFF55FF55 : 0xFF555555;

            commandList.add(new RenderCommand.Line(
                    globalRoot.getXPos() + windowX + 10,
                    globalRoot.getYPos() + windowY + 10,
                    this,
                    lineColor
            ));
        }

        for (Node parent : this.parents) {
            int lineColor = activeUnlocked.contains(parent.getId()) && activeUnlocked.contains(this.id) ? 0xFF55FF55 : 0xFF555555;

            commandList.add(new RenderCommand.Line(
                    parent.getXPos() + windowX + 10,
                    parent.getYPos() + windowY + 10,
                    this,
                    lineColor
            ));
        }

        boolean isUnlocked = activeUnlocked.contains(this.id) || (this.skill == null);
        boolean hasRequiredParent = this.parents.isEmpty() || this.parents.stream().anyMatch(p -> activeUnlocked.contains(p.getId()));
        boolean hasEnoughPoints = currentPoints >= this.cost;

        int textureType = 0;
        if (isUnlocked) textureType = 1;
        else if (!hasRequiredParent || !hasEnoughPoints) textureType = 2;

        commandList.add(new RenderCommand.Background(this, myX, myY, textureType));
    }

    public void addChild(Node child) {
        if (!this.children.contains(child)) {
            this.children.add(child);
        }
        if (!child.getParents().contains(this)) {
            child.getParents().add(this);
        }
    }

    public double getXLayer() {
        return xLayer;
    }

    public void setXLayer(double xLayer) {
        this.xLayer = xLayer;
    }

    public int getYLayer() {
        return yLayer;
    }

    public void setYLayer(int yLayer) {
        this.yLayer = yLayer;
    }

    public List<Node> getChildren() {
        return children;
    }

    public List<Node> getParents() {
        return parents;
    }

    public int getCost() {
        return cost;
    }

    public Skill getSkill() {
        return skill;
    }

    public String getId() {
        return id;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public static boolean isBlockedByChoice(Node node, List<String> activeUnlocked) {
        if (activeUnlocked.contains(node.getId())) return false;

        List<Node> parents = node.getParents();
        if (parents == null || parents.isEmpty()) return false;

        return parents.stream().allMatch(parent -> {
            if (parent instanceof ChoiceNode cp) {
                int state = cp.getUnlockState(activeUnlocked);

                if (state > 0) {
                    // If this parent is a ChoiceNode, consult its registered child-sets
                    // to determine whether this particular child is blocked by the
                    // opponent half being chosen.
                    if (cp.isChildOnLeft(node) && state == 2) return true;
                    if (cp.isChildOnRight(node) && state == 1) return true;
                }
            }

            return isBlockedByChoice(parent, activeUnlocked);
        });
    }
}
