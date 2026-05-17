package better_progression.skillTreeV2;

import better_progression.BetterProgression;
import better_progression.skillTreeV2.nodeTypes.ChoiceNode;
import better_progression.skillTreeV2.nodeTypes.Node;
import better_progression.skills.Skill;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SkillTree {
    public static final Map<Identifier, SkillTree> REGISTRY = new HashMap<>();

    private final Identifier treeId;
    private final Map<String, Node> nodes = new HashMap<>();

    public SkillTree(Identifier treeId) {
        this.treeId = treeId;
    }

    public Node registerNode(Skill skill, int price) {
        String skillId = (skill == null) ? "root" : skill.id();
        long count = nodes.values().stream()
                .filter(n -> n.getSkill() != null && n.getSkill().id().equals(skillId))
                .count();

        String uniqueId = treeId.getPath() + "_" + skillId + "_" + (count + 1);
        Node node = new Node(uniqueId, skill, price);

        nodes.put(uniqueId, node);
        return node;
    }

    public void connect(Node parent, Node child) {
        if (parent == null || child == null) return;

        if (isReachable(child, parent)) {
            BetterProgression.getLogger().warn("[{}] Connection rejected (Loop detected)", treeId);
            return;
        }

        parent.addPlayerRelation(child);
    }

    public boolean isReachable(Node current, Node target) {
        if (current.equals(target)) return true;
        return current.getChildren().stream().anyMatch(next -> isReachable(next, target));
    }

    public List<Node> getRootNodes() {
        return nodes.values().stream()
                .filter(node -> node.getParents().isEmpty())
                .collect(Collectors.toList());
    }

    public void calcLayers() {
        nodes.values().forEach(node -> node.setYLayer(-1));
        nodes.values().forEach(this::getLayerRecursive);

        Map<Integer, List<Node>> nodesInLevel = nodes.values().stream()
                .collect(Collectors.groupingBy(Node::getYLayer, TreeMap::new, Collectors.toList()));

        nodesInLevel.forEach((level, levelNodes) -> {
            if (level == 0) {
                IntStream.range(0, levelNodes.size()).forEach(i ->
                        levelNodes.get(i).setXLayer(i - (levelNodes.size() - 1) / 2.0)
                );
            } else {
                levelNodes.forEach(node -> node.setXLayer(node.getParents().stream()
                        .mapToDouble(Node::getXLayer)
                        .average().orElse(0.0))
                );
                adjustOverlaps(levelNodes);
            }
        });
    }

    private void adjustOverlaps(List<Node> levelNodes) {
        double minDist = 1.0;
        double[] lastX = { Double.NEGATIVE_INFINITY };

        levelNodes.stream()
                .sorted(Comparator.comparingDouble(Node::getXLayer))
                .forEachOrdered(node -> {
                    double finalX = Math.max(node.getXLayer(), lastX[0] + minDist);
                    node.setXLayer(finalX);
                    lastX[0] = finalX;
                });

        double offset = levelNodes.stream()
                .mapToDouble(Node::getXLayer)
                .average().orElse(0.0);

        levelNodes.forEach(node -> node.setXLayer(node.getXLayer() - offset));
    }

    private int getLayerRecursive(Node node) {
        if (node.getYLayer() != -1) return node.getYLayer();

        int layer = node.getParents().stream()
                .mapToInt(this::getLayerRecursive)
                .max().orElse(-1) + 1;

        node.setYLayer(layer);
        return layer;
    }

    public Identifier getTreeId() { return treeId; }
    public Map<String, Node> getNodes() { return nodes; }
}
