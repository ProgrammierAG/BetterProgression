package better_progression.skills;

import better_progression.BetterProgression;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Skills {
    public static Map<String , Skill> SKILLS = new HashMap<>();
    public static final Identifier BUTTON_BACKGROUND = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_unobtained");

    public static final Identifier BUTTON_BACKGROUND_UNLOCKED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_obtained");


    //Skills:
    public static final Skill TEST = register(new Skill("test"/*ID*/, "test_desc"/*description ID*/,
            (player, world, level) -> {
                //put thing that should be run when activated
            },
            (player, world, level) -> {
                //put thing that should be run every game tick
            },
            (player, world, level) -> {
                //put thing that should be run when skill is reset
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "test")
            // enter name of related image whithout .png in "src/main/resources/assets/better_progression/textures/gui/sprites"
    ));
    public static final Skill SPEED = register(new Skill("speed", "speed_desc",
            (player, world, level) -> {
                player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.1 + (.01 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.1);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "speed_multiplier")
    ));

    public static final Skill SPEED_IN_WATER = register(new Skill("speed_in_water", "speed_in_water_desc",
            (player, world, level) -> {
                player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(0 + (.01 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(0);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "speed_in_water_multiplier")
    ));

    public static final Skill ATTACK_RANGE = register(new Skill("attack_range", "attack_range_desc",
            (player, world, level) -> {
                player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(3 + (10 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                (player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)).setBaseValue(3);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "sword_range")
    ));

    public static final Skill PLACING_RANGE = register(new Skill("placing_range","placing_range_desc",
            (player,world, level) ->  {
                player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).setBaseValue(3 +(10 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).setBaseValue(3);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID,"jumper")


    ));

    public static final Skill NO_HUNGER_EFFECT = register(new Skill("no_hunger_effect", "no_hunger_effect_desc",
            (player, world, level) -> {},
            (player, world, level) -> {
                if (player.hasEffect(MobEffects.HUNGER)) player.removeEffect(MobEffects.HUNGER);
            },
            (player, world, level) -> {},
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "no_rotten_flesh_effect")
    ));



    public static Skill register(Skill skill) {
        BetterProgression.getLogger().info("registering Skill: " + skill.NAME_ID());
        SKILLS.put(skill.NAME_ID(), skill);
        return skill;
    }
    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skills");
    }
}

