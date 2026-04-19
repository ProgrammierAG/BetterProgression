package better_progression.skillLogic;

import better_progression.BetterProgression;
import better_progression.skills.Skill;
import better_progression.skills.Skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillTree {
    public static Map<String, Skill> skillButtons = new HashMap<>();
    public static Map<String, List<String>> children = new HashMap<>();
    public static Map<Skill, List<String>> parents = new HashMap<>();

    public static final String SPEED_1 = registerNode(Skills.SPEED);
    public static final String ATTACK_RANGE_1 = registerNode(Skills.ATTACK_RANGE);
    public static final String NO_HUNGER_EFFECT_1 = registerNode(Skills.NO_HUNGER_EFFECT);

    public static String registerNode(Skill skill) {
        long count = skillButtons.values().stream()
                .filter(skill1 -> skill1.equals(skill))
                .count();
        String name = skill.NAME_ID() + "_lvl_" + (count + 1);
        BetterProgression.getLogger().info("registering SkillButton for Skill: " + skill.NAME_ID() + " with name: " + name);
        skillButtons.put(name, skill);
        return name;
    }

    public static void connect(String parent, String child) {
        List<String> childList = children.computeIfAbsent(parent,  list -> new ArrayList<>());
        List<String> parentList = children.computeIfAbsent(child,  list -> new ArrayList<>());

        if (childList.size() >= 3) {
            BetterProgression.getLogger().warn("Max 3 children allowed for SkillButton: " + parent + " ignoring Connection");
            return;
        }
        if (parentList.size() >= 3) {
            BetterProgression.getLogger().warn("Max 3 parents allowed for SkillButton: " + child + " ignoring Connection");
            return;
        }
        if (isReachable(child, parent)) {
            BetterProgression.getLogger().warn("SkillButton: " + child + " is already connected to: " + parent);
            return;
        }

        childList.add(child);
        parentList.add(parent);
    }

    private static boolean isReachable(String current, String target) {
        if (current.equals(target)) return true;

        return children.getOrDefault(current, List.of()).stream()
                .anyMatch(next -> isReachable(next, target));
    }
}
