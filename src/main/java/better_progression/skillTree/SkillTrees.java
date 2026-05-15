package better_progression.skillTree;

import better_progression.BetterProgression;
import better_progression.skills.Skills;
import net.minecraft.resources.Identifier;

public class SkillTrees {

    public static void initialize() {
        BetterProgression.getLogger().info("Building clean Skill Trees based on UI design");

        // ----- tutorial -----


        // ----- temporary -----

        SkillTreeBuilder.create(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "test_tree"))
                .node("1", Skills.SPEED, 1)
                .node("2", Skills.ATTACK_RANGE, 1)
                .node("3", Skills.SPEED, 1)
                .node("4", Skills.ATTACK_RANGE, 1)
                .node("5", Skills.NO_HUNGER_EFFECT, 1)

                .connect("1", "2")
                //.connect("1", "3")
                .connect("2", "4")
                .connect("3", "5")

                .choice("2", "3")

                .build();
    }
}
