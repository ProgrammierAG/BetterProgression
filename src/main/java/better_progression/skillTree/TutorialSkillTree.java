package better_progression.skillTree;

import better_progression.BetterProgression;
import better_progression.skills.Skills;
import net.minecraft.resources.Identifier;

public class TutorialSkillTree {
    public static void  initialize() {
        // ----------------------------------------------------------------=========
        // STEP 1: INITIALIZE THE TREE
        // ----------------------------------------------------------------=========
        // EVERY skill tree requires its own unique Identifier (Namespace and Path).
        // For this reference tutorial, we name the path "tutorial_tree".
        SkillTreeBuilder builder = SkillTreeBuilder.create(
                Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "tutorial_tree")
        );

        // ----------------------------------------------------------------=========
        // STEP 2: REGISTER NODES (.node)
        // ----------------------------------------------------------------=========
        // Syntax: .node("UNIQUE_ALIAS_NAME", Skills.YOUR_SKILL_RECORD, POINT_COST)
        // IMPORTANT: The alias names (like "start" or "path_left") are defined by you.
        // They MUST be completely unique within this specific tree!

        // Layer 1: The Starting Node
        // This node has no parents in this tree. The UI automatically hooks it up to the GLOBAL_ROOT!
        builder.node("start", Skills.SPEED, 1);

        // Layer 2: Standard Follow-up Skills
        builder.node("sword_range_1", Skills.ATTACK_RANGE, 2);
        builder.node("extra_health_1", Skills.MAX_HEALTH, 2);

        // Layer 3: THE CHOICE NODES (Mutually Exclusive Paths)
        // These are two distinct nodes that we will physically fuse together further down.
        // The player will be forced to choose exactly ONE of these two paths in the GUI!
        builder.node("choice_fire_immunity", Skills.FIRE_IMMUNITY, 3);
        builder.node("choice_water_breathing", Skills.GILLS, 3);

        // Layer 4: Independent Paths After The Choice Node
        // These nodes demonstrate how paths separate completely after a decision is made.
        builder.node("anti_hunger_buff", Skills.NO_HUNGER_EFFECT, 4);
        builder.node("feather_falling_buff", Skills.FEATHER_FALLING, 4);


        // ----------------------------------------------------------------=========
        // STEP 3: CONNECT THE PATHS (.connect)
        // ----------------------------------------------------------------=========
        // Syntax: .connect("PARENT_ALIAS", "CHILD_ALIAS")
        // This draws the pixelated lines hierarchically from top to bottom.
        // A child node becomes buyable only when at least one parent node is unlocked.

        // We branch out the starting node into two separate paths on Layer 2
        builder.connect("start", "sword_range_1");
        builder.connect("start", "extra_health_1");

        // We channel these independent paths straight into our choice node components
        builder.connect("sword_range_1",  "choice_fire_immunity");   // Left side leads to Fire
        builder.connect("extra_health_1", "choice_water_breathing"); // Right side leads to Water

        // STRICT SEPARATION AFTER THE CHOICE NODE:
        // We connect the left choice ONLY to the anti-hunger reward.
        // We connect the right choice ONLY to the feather-falling reward.
        // This guarantees that "anti_hunger_buff" CAN ONLY BE BOUGHT if Fire was chosen!
        builder.connect("choice_fire_immunity",  "anti_hunger_buff");
        builder.connect("choice_water_breathing", "feather_falling_buff");


        // ----------------------------------------------------------------=========
        // STEP 4: ENFORCE MUTUAL EXCLUSIVITY (.choice)
        // ----------------------------------------------------------------=========
        // Syntax: .choice("NODE_ALIAS_A", "NODE_ALIAS_B")
        // IMPORTANT: This snaps both buttons onto the exact same coordinate cell in the GUI
        // and draws the thick, 2-pixel wide gold selection frame around them.
        // Our automated background synchronization handles the parent convergence flawlessly,
        // so your friends don't have to manually criss-cross parent connections!

        builder.choice("choice_fire_immunity", "choice_water_breathing");


        // ----------------------------------------------------------------=========
        // STEP 5: FINALIZE & CALCULATE THE LAYOUT (.build)
        // ----------------------------------------------------------------=========
        // This MUST always be called at the very end of your builder chain.
        // It triggers layer processing, validates typos in your logs, and saves the tree.

        builder.build();
    }
}
