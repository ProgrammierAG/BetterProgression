package better_progression.skillLogic;

import better_progression.BetterProgression;
import better_progression.skills.Skill;

import better_progression.skills.Skills;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SkillTree {
    public static Map<String, Skill> skillButtons = new HashMap<>();
    public static Map<String, List<String>> children = new HashMap<>();
    public static Map<String, List<String>> parents = new HashMap<>();
    public static Map<String, Integer> yLayer = new HashMap<>();
    public static Map<String, Double> xLayer = new HashMap<>();
    public static Map<String, Integer> cost = new HashMap<>();

    public static final String SPEED_1 = registerNode(Skills.SPEED, 1);
    public static final String ATTACK_RANGE_1 = registerNode(Skills.ATTACK_RANGE, 1);
    public static final String NO_HUNGER_EFFECT_1 = registerNode(Skills.NO_HUNGER_EFFECT, 1);


    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skill_v1 tree");
        connect(SPEED_1, ATTACK_RANGE_1);
        connect(ATTACK_RANGE_1, NO_HUNGER_EFFECT_1);

        calcLayers();

    }

    public static String registerNode(Skill skill, int price) {
        if (skill != null) {
            long count = skillButtons.values().stream()
                    .filter(skill1 -> skill1.equals(skill))
                    .count();
            String name = skill.id() + "_" + (count + 1);
            BetterProgression.getLogger().info("registering SkillButton for Skill_v1: {}, with name: {}", skill.id(), name);
            skillButtons.put(name, skill);
            cost.put(name, price);
            return name;
        }
        return "root";
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
        BetterProgression.getLogger().info("connecting Skills_v1 {} and {}", parent, child);
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

                adjustOverlaps(ids);
            }
        });
    }

    public static void adjustOverlaps(List<String> ids) {
        double minDist = 1.0;

        double[] lastX = { Double.NEGATIVE_INFINITY };

        ids.stream()
                .sorted(Comparator.comparingDouble(xLayer::get))
                .forEachOrdered(id -> {
                    double finalX = Math.max(xLayer.get(id), lastX[0] + minDist);
                    xLayer.put(id, finalX);
                    lastX[0] = finalX;
                });

        double offset = ids.stream()
                .mapToDouble(xLayer::get)
                .average()
                .orElse(0.0);

        ids.forEach(id -> xLayer.put(id, xLayer.get(id) - offset));
    }

    public static int getLayerRecursive(String id) {
        if (yLayer.containsKey(id)) return (int) yLayer.get(id);

        List<String> parentIDs = parents.getOrDefault(id, List.of());
        int layer = parentIDs.isEmpty() ? 0
                : parentIDs.stream()
                .mapToInt(SkillTree::getLayerRecursive)
                .max().getAsInt() + 1;
        SkillTree.yLayer.put(id, layer);
        return layer;
    }

    public static List<String> getParents(String id) {
        return parents.get(id);
    }
}
