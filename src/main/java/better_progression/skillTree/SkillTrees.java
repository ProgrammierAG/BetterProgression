package better_progression.skillTree;

import better_progression.BetterProgression;
import better_progression.skills.Skills;
import net.minecraft.resources.Identifier;

public class SkillTrees {

    public static void initialize() {
        BetterProgression.getLogger().info("Building Skill Trees");
        // ----- temporary -----
        //only used for testing (AI generated)
        SkillTreeBuilder.create(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "master_tree"))

                // ========================================================================
                // EBENE 1: Start (Wird automatisch mit der GLOBAL_ROOT verbunden)
                // ========================================================================
                .node("master_start", Skills.SPEED, 1)

                // ========================================================================
                // EBENE 2: Vorbereitung auf die Spezialisierung
                // ========================================================================
                .node("prep_magic", Skills.NO_HUNGER_EFFECT, 2)
                .node("prep_melee", Skills.ATTACK_RANGE, 2)

                // ========================================================================
                // EBENE 3: DIE CHOICE-NODE (Exklusive Wahl: Magier ODER Krieger)
                // ========================================================================
                .node("class_mage", Skills.FIRE_IMMUNITY, 3)  // Magier-Pfad
                .node("class_knight", Skills.KNOCKBACK_RESIST, 3) // Krieger-Pfad

                // ========================================================================
                // EBENE 4: Unabhängige Folge-Skills (Klassen-Spezifische Fähigkeiten)
                // ========================================================================
                // Magier-Erweiterungen
                .node("mage_gills", Skills.GILLS, 4)
                .node("mage_haste", Skills.MINING_HASTE, 4)

                // Krieger-Erweiterungen
                .node("knight_health", Skills.MAX_HEALTH, 4)
                .node("knight_step", Skills.STEP_ASSIST, 4)

                // ========================================================================
                // EBENE 5: ZUSAMMENFÜHRUNG (Der gemeinsame Ultimative Endknoten!)
                // ========================================================================
                // Dieser mächtige Skill erfordert, dass man ENTWEDER den Magier-Pfad
                // ODER den Krieger-Pfad komplett abgeschlossen hat.
                .node("ultimate_avatar", Skills.MAX_HEALTH, 6)


                // ========================================================================
                // PFADE VERBINDEN
                // ========================================================================
                // Zuleitung von der Basis in die Verzweigung
                .connect("master_start", "prep_magic")
                .connect("master_start", "prep_melee")

                // Hinführung zur Choice-Node
                .connect("prep_magic", "class_mage")
                .connect("prep_melee", "class_knight")

                // Ablauf nach der Choice-Node (Getrennte Pfade)
                .connect("class_mage", "mage_gills")
                .connect("class_mage", "mage_haste")

                .connect("class_knight", "knight_health")
                .connect("class_knight", "knight_step")

                // RE-CONNECTION (Wiedervereinigung):
                // Alle vier Pfade münden am Ende wieder in derselben ultimativen Node!
                .connect("mage_gills", "ultimate_avatar")
                .connect("mage_haste", "ultimate_avatar")
                .connect("knight_health", "ultimate_avatar")
                .connect("knight_step", "ultimate_avatar")


                // ========================================================================
                // EXKLUSIVE WEICHE AKTIVIEREN
                // ========================================================================
                // Verschmilzt Magier und Krieger auf Ebene 3 miteinander
                .choice("class_mage", "class_knight")

                .build();
    }
}
