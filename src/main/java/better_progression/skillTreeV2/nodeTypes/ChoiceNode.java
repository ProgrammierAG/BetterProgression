package better_progression.skillTreeV2.nodeTypes;

import better_progression.skills.Skill;

import java.util.HashSet;
import java.util.Set;

public class ChoiceNode extends Node{
    private ChoiceNode partner;

    public ChoiceNode(String id, Skill skill, int cost) {
        super(id, skill, cost);
    }

    public void linkWithPartner(ChoiceNode other) {
        this.partner = other;
        other.partner = this;
    }

    public void synchronizeParents() {
        if (this.partner == null) return;

        Set<Node> combinedParents = new HashSet<>();
        combinedParents.addAll(this.getParents());
        combinedParents.addAll(this.partner.getParents());

        this.getParents().clear();
        this.getParents().addAll(combinedParents);

        this.partner.getParents().clear();
        this.partner.getParents().addAll(combinedParents);

        for (Node parent : combinedParents) {
            if (!parent.getChildren().contains(this)) {
                parent.getChildren().add(this);
            }
            if (!parent.getChildren().contains(this.partner)) {
                parent.getChildren().add(this.partner);
            }
        }
    }

    public ChoiceNode getPartner() {
        return partner;
    }
}
