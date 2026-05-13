package better_progression.skillLogic;

import better_progression.BetterProgression;
import better_progression.skills.Skill;

import better_progression.skills.Skills;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SkillTree {
    public static Map<String, Skill> skillButtons = new HashMap<>();

    public static Map<String, List<String>> children = new HashMap<>();
    public static Map<String, List<String>> parents = new HashMap<>();

    public static Map<String, String> choicePairs = new HashMap<>();

    public static Map<String, Integer> yLayer = new HashMap<>();
    public static Map<String, Double> xLayer = new HashMap<>();

    public static Map<String, Integer> cost = new HashMap<>();

    public static final String START_NODE = registerNode(Skills.SPEED, 1);

    public static final String PATH_A = registerNode(Skills.ATTACK_RANGE, 2);
    public static final String PATH_B = registerNode(Skills.NO_HUNGER_EFFECT, 2);

    public static final String PATH_A_FOLLOWUP = registerNode(Skills.SPEED, 3);
    public static final String PATH_B_FOLLOWUP = registerNode(Skills.ATTACK_RANGE, 3);

    public static final String CHOICE_B_SUB1 = registerNode(Skills.SPEED, 4);
    public static final String CHOICE_B_SUB2 = registerNode(Skills.NO_HUNGER_EFFECT, 4);

    public static final String FINAL_ULTIMATE = registerNode(Skills.ATTACK_RANGE, 5);


    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skill tree");

        connect(START_NODE, PATH_A);
        connect(START_NODE, PATH_B);

        connect(PATH_A, PATH_A_FOLLOWUP);

        connect(PATH_B, PATH_B_FOLLOWUP);
        connect(PATH_B_FOLLOWUP, CHOICE_B_SUB1);
        connect(PATH_B_FOLLOWUP, CHOICE_B_SUB2);

        connect(CHOICE_B_SUB1, FINAL_ULTIMATE);

        mergeToChoice(PATH_A, PATH_B);

        mergeToChoice(CHOICE_B_SUB1, CHOICE_B_SUB2);

        calcLayers();

    }

    public static String registerNode(Skill skill, int price) {
        if (skill != null) {
            long count = skillButtons.values().stream()
                    .filter(skill1 -> skill1.equals(skill))
                    .count();
            String name = skill.id() + "_" + (count + 1);
            BetterProgression.getLogger().info("registering SkillButton for Skill: {}, with name: {}", skill.id(), name);
            skillButtons.put(name, skill);
            cost.put(name, price);
            return name;
        }
        return "root";
    }

    public static void mergeToChoice(String nodeA, String nodeB) {
        choicePairs.put(nodeA, nodeB);
        choicePairs.put(nodeB, nodeA);
        BetterProgression.getLogger().info("Merged {} and {} into a choice node", nodeA, nodeB);
    }

    public static void connect(String parent, String child) {
        List<String> childList = children.computeIfAbsent(parent,  list -> new ArrayList<>());
        List<String> parentList = parents.computeIfAbsent(child,  list -> new ArrayList<>());

        if (childList.size() >= 3) {
            BetterProgression.getLogger().warn("Max 3 children allowed for SkillButton: {} ignoring Connection", parent);
            return;
        }
        if (parentList.size() >= 3) {
            BetterProgression.getLogger().warn("Max 3 parents allowed for SkillButton: {} ignoring Connection", child);
            return;
        }
        if (isReachable(child, parent)) {
            BetterProgression.getLogger().warn("tried to connect SkillButton {} and {}, but failed, because loops are not allowed",
                    parent, child);
            return;
        }
        BetterProgression.getLogger().info("connecting Skills {} and {}", parent, child);
        childList.add(child);
        parentList.add(parent);
    }

    public static boolean isReachable(String current, String target) {
        if (current.equals(target)) return true;

        return children.getOrDefault(current, List.of()).stream()
                .anyMatch(next -> isReachable(next, target));
    }

    public static void calcLayers() {
        xLayer.clear();
        skillButtons.keySet().forEach(SkillTree::getLayerRecursive);

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
                        .average().orElse(0))
                );

                ids.stream()
                        .filter(SkillTree::isChoiceNode)
                        .filter(id -> ids.contains(getChoicePartner(id)))
                        .forEach(id -> {
                            String partner = getChoicePartner(id);
                            double avgX = Stream.of(id, partner)
                                    .mapToDouble(xLayer::get)
                                    .average().orElse(0);

                            xLayer.put(id, avgX);
                            xLayer.put(partner, avgX);
                        });

                adjustOverlaps(ids);
            }
        });
    }

    public static void adjustOverlaps(List<String> ids) {
        double minDist = 1.0;

        double[] lastX = { Double.NEGATIVE_INFINITY };

        Set<String> processedChoices = new HashSet<>();

        ids.stream()
                .sorted(Comparator.comparingDouble(xLayer::get))
                .forEachOrdered(id -> {
                    String partner = getChoicePartner(id);

                    if (isChoiceNode(id) && processedChoices.contains(partner)) {
                        xLayer.put(id, xLayer.get(partner));
                    } else {
                        double finalX = Math.max(xLayer.get(id), lastX[0] + minDist);
                        xLayer.put(id, finalX);
                        lastX[0] = finalX;

                        if (isChoiceNode(id)) {
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

    public static int getLayerRecursive(String id) {
        if (yLayer.containsKey(id)) return yLayer.get(id);

        int layer = parents.getOrDefault(id, List.of()).stream()
                .mapToInt(SkillTree::getLayerRecursive)
                .max().orElse(-1) + 1;

        yLayer.put(id, layer);

        if (isChoiceNode(id)) {
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

    public static boolean isChoiceNode(String id) {
        return choicePairs.containsKey(id);
    }

    public static String getChoicePartner(String id) {
        return choicePairs.get(id);
    }

    public static List<String> getParents(String id) {
        return parents.get(id);
    }
}
