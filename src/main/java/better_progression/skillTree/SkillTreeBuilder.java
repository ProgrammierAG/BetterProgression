package better_progression.skillTree;

import better_progression.skillTree.nodeTypes.ChoiceNode;
import better_progression.skillTree.nodeTypes.Node;
import better_progression.skills.Skill;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SkillTreeBuilder {
    private final SkillTree tree;
    private final Map<String, Node> aliasMap = new HashMap<>();

    public SkillTreeBuilder(Identifier treeId) {
        this.tree = new SkillTree(treeId);
    }

    public static SkillTreeBuilder create(Identifier treeId) {
        return new SkillTreeBuilder(treeId);
    }

    public SkillTreeBuilder node(String alias, Skill skill, int price) {
        String uniqueAlias = tree.getTreeId().getPath() + "_" + alias;
        Node node = this.tree.registerNode(skill, price);
        this.aliasMap.put(uniqueAlias, node);
        return this;
    }

    public SkillTreeBuilder choiceNode(String alias, Skill skillA, int costA, Skill skillB, int costB) {
        String uniqueAlias = tree.getTreeId().getPath() + "_" + alias;
        ChoiceNode choiceNode = this.tree.registerChoiceNode(alias, skillA, costA, skillB, costB);
        this.aliasMap.put(uniqueAlias, choiceNode);
        return this;
    }

    public SkillTreeBuilder connect(String parentAlias, String childAlias) {
        String uniqueParent = tree.getTreeId().getPath() + "_" + parentAlias;
        String uniqueChild = tree.getTreeId().getPath() + "_" + childAlias;

        Node parentNode = aliasMap.get(uniqueParent);
        Node childNode = aliasMap.get(uniqueChild);

        if (parentNode != null && childNode != null) {
            this.tree.connect(parentNode, childNode);
        }
        return this;
    }

    /**
     * Connect a ChoiceNode parent to a child and register which half of the choice
     * the child belongs to (left=true, right=false). The parentAlias should refer
     * to the base alias used when creating the ChoiceNode.
     */
    public SkillTreeBuilder connectToChoiceHalf(String parentAlias, boolean leftHalf, String childAlias) {
        String uniqueParent = tree.getTreeId().getPath() + "_" + parentAlias;
        String uniqueChild = tree.getTreeId().getPath() + "_" + childAlias;

        Node parentNode = aliasMap.get(uniqueParent);
        Node childNode = aliasMap.get(uniqueChild);

        if (parentNode != null && childNode != null) {
            this.tree.connectToChoiceHalf(parentNode, childNode, leftHalf);
        }
        return this;
    }

    public SkillTree build() {
        // Berechnet direkt das saubere, unverschobene Schichten-Layout
        this.tree.calcLayers();
        SkillTree.REGISTRY.put(this.tree.getTreeId(), this.tree);
        return this.tree;
    }
}
