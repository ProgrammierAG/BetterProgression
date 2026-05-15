package better_progression.skillTree;


import better_progression.BetterProgression;
import better_progression.skills.Skill;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillTreeBuilder {
    private final SkillTree tree;
    private final Identifier treeId;
    private final Map<String, String> aliasMap = new HashMap<>();

    public SkillTreeBuilder(Identifier treeId) {
        this.treeId = treeId;
        this.tree = new SkillTree(treeId);
        BetterProgression.getLogger().info("Starting creation of skillTree: '{}'", treeId);
    }

    public static SkillTreeBuilder create(Identifier treeId) {
        return new SkillTreeBuilder(treeId);
    }

    public SkillTreeBuilder node(String alias, Skill skill, int price) {
        if (alias == null || alias.isEmpty()) {
            BetterProgression.getLogger().error("[SkillTree - {}] CRITICAL: Tried to register a node with an empty name!", treeId);
            return this;
        }

        String uniqueAlias = treeId.getPath() + "_" + alias;

        if (aliasMap.containsKey(uniqueAlias)) {
            BetterProgression.getLogger().error("[SkillTree - {}] CRITICAL: Duplicate name found! '{}' is already used in this tree.", treeId, alias);
            return this;
        }

        String generatedId = this.tree.registerNode(skill, price);
        this.aliasMap.put(uniqueAlias, generatedId);

        if (skill == null) {
            BetterProgression.getLogger().info("[SkillTree - {}] -> Registered structural ROOT node '{}'", treeId, alias);
        } else {
            BetterProgression.getLogger().info("[SkillTree - {}] -> Registered node '{}' (Cost: {} points) using skill: {}",
                    treeId, alias, price, skill.id());
        }
        return this;
    }

    public SkillTreeBuilder connect(String parentAlias, String childAlias) {
        String uniqueParent = treeId.getPath() + "_" + parentAlias;
        String uniqueChild = treeId.getPath() + "_" + childAlias;

        String parentId = aliasMap.get(uniqueParent);
        String childId = aliasMap.get(uniqueChild);

        if (parentId == null) {
            BetterProgression.getLogger().error("[SkillTree - {}] CONNECTION ERROR: The parent name '{}' does not exist!", treeId, parentAlias);
            return this;
        }
        if (childId == null) {
            BetterProgression.getLogger().error("[SkillTree - {}] CONNECTION ERROR: The child name '{}' does not exist!", treeId, childAlias);
            return this;
        }

        this.tree.connect(parentId, childId);
        BetterProgression.getLogger().info("[SkillTree - {}] -> Connected path: '{}' leads to '{}'", treeId, parentAlias, childAlias);
        return this;
    }

    public SkillTreeBuilder choice(String aliasA, String aliasB) {
        String uniqueA = treeId.getPath() + "_" + aliasA;
        String uniqueB = treeId.getPath() + "_" + aliasB;

        String idA = aliasMap.get(uniqueA);
        String idB = aliasMap.get(uniqueB);

        if (idA == null) {
            BetterProgression.getLogger().error("[SkillTree - {}] CHOICE ERROR: The name '{}' does not exist!", treeId, aliasA);
            return this;
        }
        if (idB == null) {
            BetterProgression.getLogger().error("[SkillTree - {}] CHOICE ERROR: The name '{}' does not exist!", treeId, aliasB);
            return this;
        }

        this.tree.mergeToChoice(idA, idB);
        BetterProgression.getLogger().info("[SkillTree - {}] -> Exclusive Choice created: '{}' OR '{}'", treeId, aliasA, aliasB);

        return this;
    }

    public SkillTree build() {
        BetterProgression.getLogger().info("[SkillTree - {}] Processing layout and calculating layers...", treeId);
        this.tree.calcLayers();
        BetterProgression.getLogger().info("[SkillTree - {}] SUCCESS: Tree generated with {} nodes.", treeId, aliasMap.size());
        SkillTree.REGISTRY.put(this.tree.getTreeId(), this.tree);
        return this.tree;
    }
}
