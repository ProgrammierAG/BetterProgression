package better_progression.skillTreeV2;

import better_progression.BetterProgression;
import better_progression.skillTreeV2.nodeTypes.ChoiceNode;
import better_progression.skillTreeV2.nodeTypes.Node;
import better_progression.skills.Skill;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public SkillTree build() {
        // Berechnet direkt das saubere, unverschobene Schichten-Layout
        this.tree.calcLayers();
        SkillTree.REGISTRY.put(this.tree.getTreeId(), this.tree);
        return this.tree;
    }
}
