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
    public static Map<String, Integer> Y_layer = new HashMap<>();
    public static Map<String, Double> X_Layer = new HashMap<>();

    public static final String SPEED_1 = registerNode(Skills.SPEED);
    public static final String ATTACK_RANGE_1 = registerNode(Skills.ATTACK_RANGE);
    public static final String PLACING_RANGE_1 = registerNode(Skills.PLACING_RANGE);
    public static final String NO_HUNGER_EFFECT_1 = registerNode(Skills.NO_HUNGER_EFFECT);
    public static final String SPEED_IN_WATER_1 = registerNode(Skills.SPEED_IN_WATER);

    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skill tree");
        connect(SPEED_1, ATTACK_RANGE_1);
        connect(SPEED_1, PLACING_RANGE_1);
        connect(SPEED_1, SPEED_IN_WATER_1);
        connect(ATTACK_RANGE_1, NO_HUNGER_EFFECT_1);
        connect(PLACING_RANGE_1, NO_HUNGER_EFFECT_1);
        calcLayers();

    }

    public static String registerNode(Skill skill) {
        if (skill != null) {
            long count = skillButtons.values().stream()
                    .filter(skill1 -> skill1.equals(skill))
                    .count();
            String name = skill.NAME_ID() + "_" + (count + 1);
            BetterProgression.getLogger().info("registering SkillButton for Skill: " + skill.NAME_ID() + ", with name: " + name);
            skillButtons.put(name, skill);
            return name;
        }
        return "root";
    }

    public static void connect(String parent, String child) {
        List<String> childList = children.computeIfAbsent(parent,  list -> new ArrayList<>());
        List<String> parentList = parents.computeIfAbsent(child,  list -> new ArrayList<>());

        if (childList.size() >= 3) {
            BetterProgression.getLogger().warn("Max 3 children allowed for SkillButton: " + parent + " ignoring Connection");
            return;
        }
        if (parentList.size() >= 3) {
            BetterProgression.getLogger().warn("Max 3 parents allowed for SkillButton: " + child + " ignoring Connection");
            return;
        }
        if (isReachable(child, parent)) {
            BetterProgression.getLogger().warn("tried to connect SkillButton " + parent + " and " + child +
                    ", but failed, because loops are not allowed");
            return;
        }
        BetterProgression.getLogger().info("connecting Skills " + parent + " and " + child);
        childList.add(child);
        parentList.add(parent);
    }

    public static boolean isReachable(String current, String target) {
        if (current.equals(target)) return true;

        return children.getOrDefault(current, List.of()).stream()
                .anyMatch(next -> isReachable(next, target));
    }

    public static void calcLayers() {
        X_Layer.clear();
        skillButtons.keySet().forEach(SkillTree::getLayerRecursive);

        Map<Integer, List<String>> nodesInLevel = skillButtons.keySet().stream()
                .collect(Collectors.groupingBy(Y_layer::get, TreeMap::new, Collectors.toList()));

        nodesInLevel.forEach((level, ids) -> {
            if (level == 0) {
                IntStream.range(0, ids.size()).forEach(i ->
                        X_Layer.put(ids.get(i), i - (ids.size() - 1) / 2.0)
                );
            } else {
                ids.forEach(id -> X_Layer.put(id, parents.getOrDefault(id, List.of()).stream()
                        .mapToDouble(p -> X_Layer.getOrDefault(p, 0.0))
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
                .sorted(Comparator.comparingDouble(X_Layer::get))
                .forEachOrdered(id -> {
                    double finalX = Math.max(X_Layer.get(id), lastX[0] + minDist);
                    X_Layer.put(id, finalX);
                    lastX[0] = finalX;
                });

        double offset = ids.stream()
                .mapToDouble(X_Layer::get)
                .average()
                .orElse(0.0);

        ids.forEach(id -> X_Layer.put(id, X_Layer.get(id) - offset));
    }

    public static int getLayerRecursive(String id) {
        if (Y_layer.containsKey(id)) return (int) Y_layer.get(id);

        List<String> parentIDs = parents.getOrDefault(id, List.of());
        int layer = parentIDs.isEmpty() ? 0
                : parentIDs.stream()
                .mapToInt(SkillTree::getLayerRecursive)
                .max().getAsInt() + 1;
        SkillTree.Y_layer.put(id, layer);
        return layer;
    }

    public static List<String> getParents(String id) {
        return parents.get(id);
    }
}
