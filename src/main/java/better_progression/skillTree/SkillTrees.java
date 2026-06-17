package better_progression.skillTree;

import better_progression.BetterProgression;
import better_progression.skills.Skills;
import net.minecraft.resources.Identifier;

public class SkillTrees {
    public static void initialize() {
        // ----- temporary -----

        //AI generated
        SkillTreeBuilder.create(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "class_mastery_tree"))
                // Ebene 1
                .node("master_start", Skills.SPEED, 1)

                .node("tmp", Skills.DEEP_BREATH, 1)

                // Ebene 2
                .node("prep_magic", Skills.NO_HUNGER_EFFECT, 2)
                .node("prep_melee", Skills.ATTACK_RANGE, 2)

                .node("class_mage", Skills.FIRE_IMMUNITY, 3)
                .node("class_knight", Skills.KNOCKBACK_RESIST, 3)

                // Ebene 4
                .node("mage_gills", Skills.GILLS, 4)
                .node("knight_health", Skills.MAX_HEALTH, 4)

                //ebene 6

                .node("step", Skills.STEP_ASSIST, 6)

                //eben 7

                .node("vision", Skills.NIGHT_VISION, 7)
                .node("aqua", Skills.AQUA_SPEED, 8)
                .node("regen", Skills.ALCH_REGEN, 8)
                .node("haste", Skills.MINING_HASTE, 8)

                .node("safe_falling", Skills.SAFE_FALL_DISTANCE, 8)

                .node("example", Skills.EXAMPLE, 1)

                // Verbindungen ziehen
                .connect("master_start", "prep_magic")
                .connect("master_start", "prep_melee")

                .connect("prep_magic", "class_mage")
                .connect("prep_melee", "class_knight")

                .connect("class_mage", "mage_gills")
                .connect("class_knight", "knight_health")

                .connect("knight_health", "step")
                .connect("mage_gills", "step")

                .connect("step", "vision")
                .connect("step", "aqua")
                .connect("step", "regen")
                .connect("step", "haste")

                .connect("haste", "safe_falling")

                .connect("safe_falling", "example")
                .build();
    }
}
