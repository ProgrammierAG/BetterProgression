package better_progression.skillTree;

import better_progression.BetterProgression;
import better_progression.skills.Skills;
import net.minecraft.resources.Identifier;

public class SkillTrees {
    public static void initialize() {
        SkillTreeBuilder.create(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "class_mastery_tree"))
                // ===== Ebene 0: Einstieg =====
                .node("master_start", Skills.SPEED, 1)

                // ===== Ebene 1: Erste Wahl (Spezialisierung) =====
                .choiceNode("class_choice", Skills.NO_HUNGER_EFFECT, 2, Skills.ATTACK_RANGE, 2)

                // ===== Ebene 2: Mage-Pfad =====
                .node("mage_magic", Skills.FIRE_IMMUNITY, 3)
                .node("mage_defense", Skills.GILLS, 4)

                // ===== Ebene 2: Knight-Pfad =====
                .node("knight_armor", Skills.KNOCKBACK_RESIST, 3)
                .node("knight_health", Skills.MAX_HEALTH, 4)

                // ===== Ebene 3: Allgemeine Fähigkeiten (erreichbar von beiden Pfaden) =====
                .node("movement", Skills.STEP_ASSIST, 5)
                .choiceNode("vision_choice", Skills.NIGHT_VISION, 6, Skills.DEEP_BREATH, 6)

                // ===== Ebene 4: Spezielle Boni =====
                .node("speed_boost", Skills.AQUA_SPEED, 7)
                .node("regen_aura", Skills.ALCH_REGEN, 7)
                .node("mining_boost", Skills.MINING_HASTE, 7)
                .node("safe_landing", Skills.SAFE_FALL_DISTANCE, 7)

                // ===== Ebene 5: Letzter Knoten =====
                .node("mastery", Skills.EXAMPLE, 10)

                // ===== Verbindungen: Master Start -> Choice =====
                .connect("master_start", "class_choice")

                // ===== Mage-Pfad-Verbindungen =====
                // Mage branch attaches to the LEFT half of the class choice
                .connectToChoiceHalf("class_choice", true, "mage_magic")
                .connect("mage_magic", "mage_defense")
                .connect("mage_defense", "movement")
                .connectToChoiceHalf("vision_choice", true, "mage_defense")

                // ===== Knight-Pfad-Verbindungen =====
                // Knight branch attaches to the RIGHT half of the class choice
                .connectToChoiceHalf("class_choice", false, "knight_armor")
                .connect("knight_armor", "knight_health")
                .connect("knight_health", "movement")
                .connectToChoiceHalf("vision_choice", false, "knight_health")

                // ===== Allgemeine Verbindungen (von movement aus) =====
                .connect("movement", "speed_boost")
                .connect("movement", "regen_aura")
                .connect("movement", "mining_boost")
                // safe_landing is attached to right half of vision choice
                .connectToChoiceHalf("vision_choice", false, "safe_landing")

                // ===== Finale Verbindung zu Mastery =====
                .connect("speed_boost", "mastery")
                .connect("regen_aura", "mastery")
                .connect("mining_boost", "mastery")
                .connect("safe_landing", "mastery")

                .build();
    }
}


