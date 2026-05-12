package better_progression.skills;

import better_progression.BetterProgression;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;

public class Skills_v1 {
    public static Map<String , Skill_v1> SKILLS = new HashMap<>();
    public static final Identifier BUTTON_BACKGROUND = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_unobtained");

    public static final Identifier BUTTON_BACKGROUND_UNLOCKED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_obtained");


    //Skills_v1:
    public static final Skill_v1 TEST = register(new Skill_v1("test"/*ID*/, "test_desc"/*description ID*/,
            (player, world, level) -> {
                //put thing that should be run when activated
            },
            (player, world, level) -> {
                //put thing that should be run every game tick
            },
            (player, world, level) -> {
                //put thing that should be run when skillV1 is reset
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "test")
            // enter name of related image whithout .png in "src/main/resources/assets/better_progression/textures/gui/sprites"
    ));
    public static final Skill_v1 SPEED = register(new Skill_v1("speed", "speed_desc",
            (player, world, level) -> {
                player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.1 + (.01 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.1);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "speed_multiplier")
    ));

    public static final Skill_v1 SPEED_IN_WATER = register(new Skill_v1("speed_in_water", "speed_in_water_desc",
            (player, world, level) -> {
                player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(0 + (.01 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(0);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "speed_in_water_multiplier")
    ));

    public static final Skill_v1 ATTACK_RANGE = register(new Skill_v1("attack_range", "attack_range_desc",
            (player, world, level) -> {
                player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(3 + (10 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                (player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)).setBaseValue(3);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "sword_range")
    ));

    public static final Skill_v1 PLACING_RANGE = register(new Skill_v1("placing_range","placing_range_desc",
            (player,world, level) ->  {
                player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).setBaseValue(3 +(10 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).setBaseValue(3);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID,"jumper")


    ));

    public static final Skill_v1 NO_HUNGER_EFFECT = register(new Skill_v1("no_hunger_effect", "no_hunger_effect_desc",
            (player, world, level) -> {},
            (player, world, level) -> {
                if (player.hasEffect(MobEffects.HUNGER)) player.removeEffect(MobEffects.HUNGER);
            },
            (player, world, level) -> {},
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "no_rotten_flesh_effect")
    ));



    public static Skill_v1 register(Skill_v1 skillV1) {
        BetterProgression.getLogger().info("registering Skill_v1: " + skillV1.NAME_ID());
        SKILLS.put(skillV1.NAME_ID(), skillV1);
        return skillV1;
    }
    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skills_v1");
    }
}

