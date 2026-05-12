package better_progression.skills;

import better_progression.BetterProgression;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class Skills {
    public static Map<String , Skill> SKILLS = new HashMap<>();
    public static final Identifier BUTTON_BACKGROUND = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_unobtained");

    public static final Identifier BUTTON_BACKGROUND_UNLOCKED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_obtained");

    // ----- Skills -----
    public static final Skill EXAMPLE = register(Skill
            .builder("example") //the skills id
            .descriptionId("example_desc") // Sets a custom translation key for the description

            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "skillbook"/*Filename without file extension*/))
            //adds an icon from the folder:
            //"src/main/resources/assets/better_progression/textures/gui/sprites"

            .de("Beispiel Skill", "Ein Skill, der nichts tut") // Adds German name and description
            .en("Example Skill", "A skill that does nothing") // Adds English name and description

            .action((player, level) -> {}) // The main action to execute every tick
            .when((player, level) -> true) // Acts as a gatekeeper:
            // if this returns false, the action is skipped. If omitted, it defaults to 'true' (always execute).
            .elseAction((player, level) -> {}) // Executed only if the 'when' condition fails

            .onUnlock((player, level) -> {}) // Executed once when the skill is unlocked
            .onReset((player, level) -> {}) // Executed once when the skill is reset

            .build()); // Finalizes the builder and creates the Skill record




    public static Skill register(Skill skill) {
        BetterProgression.getLogger().info("registering Skill_v1: " + skill.id());
        SKILLS.put(skill.id(), skill);
        return skill;
    }
    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skills");
    }
}
