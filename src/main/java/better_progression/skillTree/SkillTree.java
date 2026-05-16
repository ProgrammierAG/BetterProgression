package better_progression.skillTree;

import better_progression.BetterProgression;
import better_progression.skills.Skill;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SkillTree {
    public static final Map<Identifier, SkillTree> REGISTRY = new HashMap<>();


    //Instance variables
    private final Identifier treeId;
    private final Map<String, Skill> skillButtons = new HashMap<>();
    private final Map<String, List<String>> children = new HashMap<>();
    private final Map<String, List<String>> parents = new HashMap<>();

    private final Map<String, String> choicePairs = new HashMap<>();
    private final Map<String, Integer> yLayer = new HashMap<>();

    private final Map<String, Double> xLayer = new HashMap<>();
    private final Map<String, Integer> cost = new HashMap<>();


    public SkillTree(Identifier treeId) {
        this.treeId = treeId;
    }

    public String registerNode(Skill skill, int price) {
        if (skill == null) {
            String name = treeId.getPath() + "_root_" + (skillButtons.size() + 1);
            skillButtons.put(name, null);
            cost.put(name, price);
            return name;
        }

        long count = skillButtons.values().stream()
                .filter(Objects::nonNull)
                .filter(s -> s.equals(skill))
                .count();

        String name = treeId.getPath() + "_" + skill.id() + "_" + (count + 1);

        BetterProgression.getLogger().info("[{}] Registering SkillButton: {}, with unique name: {} and price {}",
                treeId, skill.id(), name, price);

        skillButtons.put(name, skill);
        cost.put(name, price);
        return name;
    }

    public void mergeToChoice(String nodeA, String nodeB) {
        choicePairs.put(nodeA, nodeB);
        choicePairs.put(nodeB, nodeA);
        BetterProgression.getLogger().info("[{}] Merged {} and {} into a choice node", treeId, nodeA, nodeB);

        List<String> parentsA = parents.computeIfAbsent(nodeA, k -> new ArrayList<>());
        List<String> parentsB = parents.computeIfAbsent(nodeB, k -> new ArrayList<>());

        Set<String> combinedParents = new HashSet<>();
        combinedParents.addAll(parentsA);
        combinedParents.addAll(parentsB);

        parentsA.clear();
        parentsA.addAll(combinedParents);

        parentsB.clear();
        parentsB.addAll(combinedParents);

        parentsA.forEach(parent -> {
            List<String> parentChildren = children.computeIfAbsent(parent, k -> new ArrayList<>());
            if (!parentChildren.contains(nodeA)) parentChildren.add(nodeA);
            if (!parentChildren.contains(nodeB)) parentChildren.add(nodeB);
        });
    }

    public void connect(String parent, String child) {
        List<String> childList = children.computeIfAbsent(parent, list -> new ArrayList<>());
        List<String> parentList = parents.computeIfAbsent(child, list -> new ArrayList<>());

        if (childList.size() >= 3 || parentList.size() >= 3 || isReachable(child, parent)) {
            BetterProgression.getLogger().warn("[{}] Connection rejected between {} and {}", treeId, parent, child);
            return;
        }
        BetterProgression.getLogger().info("[{}] Connecting Skills {} and {}", treeId, parent, child);

        if (!childList.contains(child)) {
            childList.add(child);
        }

        if (!parentList.contains(parent)) {
            parentList.add(parent);
        }
    }

    public boolean isReachable(String current, String target) {
        if (current.equals(target)) return true;
        return children.getOrDefault(current, List.of()).stream()
                .anyMatch(next -> isReachable(next, target));
    }

    public void calcLayers() {
        xLayer.clear();
        yLayer.clear();
        skillButtons.keySet().forEach(this::getLayerRecursive);

        Map<Integer, List<String>> nodesInLevel = skillButtons.keySet().stream()
                .collect(Collectors.groupingBy(yLayer::get, TreeMap::new, Collectors.toList()));

        nodesInLevel.forEach((level, ids) -> {
            if (level == 0) {
                IntStream.range(0, ids.size()).forEach(i ->
                        xLayer.put(ids.get(i), i - (ids.size() - 1) / 2.0)
                );
            } else {
                ids.forEach(id -> xLayer.put(id, parents.getOrDefault(id, List.of()).stream()
                        .mapToDouble(p -> xLayer.getOrDefault(p, 0.0))
                        .average().orElse(0.0))
                );

                ids.stream()
                        .filter(this::isChoiceNode)
                        .filter(id -> ids.contains(getChoicePartner(id)))
                        .forEach(id -> {
                            String partner = getChoicePartner(id);
                            double avgX = Stream.of(id, partner)
                                    .mapToDouble(xLayer::get)
                                    .average().orElse(0.0);

                            xLayer.put(id, avgX);
                            xLayer.put(partner, avgX);
                        });

                adjustOverlaps(ids);
            }
        });
    }

    private void adjustOverlaps(List<String> ids) {
        double minDist = 1.0;
        double[] lastX = { Double.NEGATIVE_INFINITY };
        Set<String> processedChoices = new HashSet<>();

        ids.stream()
                .sorted(Comparator.comparingDouble(xLayer::get))
                .forEachOrdered(id -> {
                    String partner = getChoicePartner(id);

                    if (this.isChoiceNode(id) && processedChoices.contains(partner)) {
                        xLayer.put(id, xLayer.get(partner));
                    } else {
                        double finalX = Math.max(xLayer.get(id), lastX[0] + minDist);
                        xLayer.put(id, finalX);
                        lastX[0] = finalX;

                        if (this.isChoiceNode(id)) {
                            processedChoices.add(id);
                        }
                    }
                });

        double offset = ids.stream()
                .mapToDouble(xLayer::get)
                .average()
                .orElse(0.0);

        ids.forEach(id -> xLayer.put(id, xLayer.get(id) - offset));
    }

    private int getLayerRecursive(String id) {
        if (yLayer.containsKey(id)) return yLayer.get(id);

        int layer = parents.getOrDefault(id, List.of()).stream()
                .mapToInt(this::getLayerRecursive)
                .max().orElse(-1) + 1;

        yLayer.put(id, layer);

        if (this.isChoiceNode(id)) {
            String partner = getChoicePartner(id);
            if (!yLayer.containsKey(partner)) {
                yLayer.put(partner, layer);
                getLayerRecursive(partner);
            } else {
                layer = yLayer.get(partner);
                yLayer.put(id, layer);
            }
        }

        return layer;
    }

    public Identifier getTreeId() {
        return treeId;
    }
    public Map<String, Skill> getSkillButtons() {
        return skillButtons;
    }
    public Map<String, Integer> getCost() {
        return cost; }
    public Map<String, Double> getXLayer() { return xLayer;
    }
    public Map<String, Integer> getYLayer() {
        return yLayer;
    }
    public List<String> getParents(String id) {
        return parents.get(id);
    }
    public boolean isChoiceNode(String id) {
        return choicePairs.containsKey(id);
    }
    public String getChoicePartner(String id) {
        return choicePairs.get(id);
    }

    public List<String> getRootNodes() {
        return skillButtons.keySet().stream()
                .filter(id -> !parents.containsKey(id) || parents.get(id).isEmpty())
                .collect(Collectors.toList());
    }
}