package better_progression.skillLogic;

import better_progression.BetterProgression;
import better_progression.skills.Skill;
import better_progression.skills.Skills;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillTree {
    public static Map<String, Skill> skillButtons = new HashMap<>();
    public static Map<String, List<String>> children = new HashMap<>();
    public static Map<String, List<String>> parents = new HashMap<>();
    public static Map<String, Integer> layers = new HashMap<>();

    public static final String ROOT = registerNode(null);
    public static final String SPEED_1 = registerNode(Skills.SPEED);
    public static final String ATTACK_RANGE_1 = registerNode(Skills.ATTACK_RANGE);
    public static final String NO_HUNGER_EFFECT_1 = registerNode(Skills.NO_HUNGER_EFFECT);

    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skill tree");
        connect(ROOT, SPEED_1);
        connect(SPEED_1, ATTACK_RANGE_1);
        connect(ATTACK_RANGE_1, NO_HUNGER_EFFECT_1);
        connect(NO_HUNGER_EFFECT_1, SPEED_1);
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
        skillButtons.keySet().forEach(SkillTree::getLayerRecursive);
    }

    public static int getLayerRecursive(String id) {
        if (layers.containsKey(id)) return layers.get(id);

        List<String> parentIDs = parents.getOrDefault(id, List.of());
        int layer = parentIDs.isEmpty() ? 0
                : parentIDs.stream()
                .mapToInt(SkillTree::getLayerRecursive)
                .max().getAsInt() + 1;

        layers.put(id, layer);
        return layer;
    }
}
