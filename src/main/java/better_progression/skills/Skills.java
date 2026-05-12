package better_progression.skills;

import better_progression.BetterProgression;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;

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

    public static final Skill SPEED = register(Skill
            .builder("speed")
            .descriptionId("speed_desc")
            .onUnlock((player, level) ->
                    player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.1 + (.01 * level)))
            .onReset((player, level) ->
                    player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.1))
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "speed_multiplier"))
            .build());

    public static final Skill ATTACK_RANGE = register(Skill
            .builder("attack_range")
            .descriptionId("attack_range_desc")
            .onUnlock((player, level) ->
                    player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(3 + (10 * level)))
            .onReset((player, level) ->
                    player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(3))
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "sword_range"))
            .build());

    public static final Skill NO_HUNGER_EFFECT = register(Skill
            .builder("no_hunger_effect")
            .descriptionId("no_hunger_effect_desc")
            .action((player, level) -> player.removeEffect(MobEffects.HUNGER))
            .when((player, level) -> player.hasEffect(MobEffects.HUNGER))
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "no_rotten_flesh_effect"))
            .build());


    public static Skill register(Skill skill) {
        BetterProgression.getLogger().info("registering Skill: {}", skill.id());
        SKILLS.put(skill.id(), skill);
        return skill;
    }
    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skills");
    }
}
