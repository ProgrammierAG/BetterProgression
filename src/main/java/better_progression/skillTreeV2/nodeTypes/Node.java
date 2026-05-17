package better_progression.skillTreeV2.nodeTypes;

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

    public void addPlayerRelation(Node child) {
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
}
