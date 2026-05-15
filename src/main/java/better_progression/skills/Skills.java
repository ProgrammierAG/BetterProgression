package better_progression.skills;

import better_progression.BetterProgression;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;

public class Skills {
    public static Map<String , Skill> SKILLS = new HashMap<>();
    public static final Identifier BUTTON_BACKGROUND_UNOBTAINED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_unobtained");

    public static final Identifier BUTTON_BACKGROUND_UNOBTAINABLE = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_unobtainable");

    public static final Identifier BUTTON_BACKGROUND_OBTAINED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_obtained");

    public static final Identifier BUTTON_BACKGROUND_BLOCKED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_blocked"
    );

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

            .en("Speed", "Increases the players speed.")
            .build());

    public static final Skill ATTACK_RANGE = register(Skill
            .builder("attack_range")
            .descriptionId("attack_range_desc")
            .onUnlock((player, level) ->
                    player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(3 + (10 * level)))
            .onReset((player, level) ->
                    player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(3))
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "sword_range"))

            .en("Attack range", "Increases the players attack range.")
            .build());

    public static final Skill NO_HUNGER_EFFECT = register(Skill
            .builder("no_hunger_effect")
            .descriptionId("no_hunger_effect_desc")
            .action((player, level) -> player.removeEffect(MobEffects.HUNGER))
            .when((player, level) -> player.hasEffect(MobEffects.HUNGER))
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "no_rotten_flesh_effect"))

            .en("No hunger effect", "Disables the Hunger effect caused by rotten flesh.")
            .build());

    // ----- temporary -----
    public static final Skill MAX_HEALTH = register(Skill
            .builder("max_health")
            .descriptionId("max_health_desc")
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "heart_icon"))
            .en("Max Health", "Increases your maximum health permanently.")
            .de("Maximale Gesundheit", "Erhöht deine maximalen Herzen dauerhaft.")
            .onUnlock((player, level) -> {
                var attr = player.getAttribute(Attributes.MAX_HEALTH);
                if (attr != null) {
                    attr.setBaseValue(20.0 + (2.0 * level));
                    player.heal(2.0f);
                }
            })
            .onReset((player, level) -> {
                var attr = player.getAttribute(Attributes.MAX_HEALTH);
                if (attr != null) attr.setBaseValue(20.0);
            })
            .build());

    public static final Skill FEATHER_FALLING = register(Skill
            .builder("feather_falling")
            .descriptionId("feather_falling_desc")
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "feather_icon"))
            .en("Glider", "Slows your fall automatically when falling from great heights.")
            .de("Gleiter", "Verlangsamt deinen Fall automatisch bei großen Sturzhöhen.")
            .action((player, level) -> player.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false, true)))
            .when((player, level) -> player.fallDistance > 3.0f)
            .build());

    public static final Skill FIRE_IMMUNITY = register(Skill
            .builder("fire_immunity")
            .descriptionId("fire_immunity_desc")
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "shield_fire"))
            .en("Fire Shield", "Grants fire resistance whenever you are burning or touching lava.")
            .de("Feuerschild", "Gewährt Feuerresistenz, sobald du brennst oder Lava berührst.")
            .action((player, level) -> player.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false, true)))
            .when((player, level) -> player.isOnFire() || player.isInLava())
            .build());

    public static final Skill STEP_ASSIST = register(Skill
            .builder("step_assist")
            .descriptionId("step_assist_desc")
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "step_up"))
            .en("Step Assist", "Allows you to step up full blocks without jumping.")
            .de("Schritthilfe", "Ermöglicht es dir, ganze Blöcke ohne Springen hinaufzugehen.")
            .onUnlock((player, level) -> {
                var attr = player.getAttribute(Attributes.STEP_HEIGHT);
                if (attr != null) attr.setBaseValue(1.0 + (0.5 * (level - 1)));
            })
            .onReset((player, level) -> {
                var attr = player.getAttribute(Attributes.STEP_HEIGHT);
                if (attr != null) attr.setBaseValue(0.6);
            })
            .build());

    public static final Skill GILLS = register(Skill
            .builder("gills")
            .descriptionId("gills_desc")
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "fish_gills"))
            .en("Gills", "Refills your oxygen capacity when you are about to drown.")
            .de("Kiemen", "Füllt deinen Sauerstoff auf, kurz bevor du ertrinkst.")
            .action((player, level) -> player.setAirSupply(Math.min(player.getMaxAirSupply(), player.getAirSupply() + 60)))
            .when((player, level) -> player.getAirSupply() <= 20 && player.isEyeInFluid(net.minecraft.tags.FluidTags.WATER))
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