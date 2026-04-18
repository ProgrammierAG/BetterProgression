package better_progression.skills;

import better_progression.BetterProgression;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
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


    //Skills
    public static final Skill SPEED = register(new Skill("speed", "speed_desc",
            (player, world, level) -> {
                player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.1 + (.05 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.1);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "speed_multiplier")
    ));


    public static final Skill ATTACK_RANGE = register(new Skill("attack_range", "attack_range_desc",
            (player, world, level) -> {
                player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(3 + (10 * level));
            },
            (player, world, level) -> {},
            (player, world, level) -> {
                player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(3);
            },
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "sword_range")
    ));

    public static final Skill NO_ROTTEN_FLESH_EFFECT = register(new Skill("no_rotten_flesh_effect", "no_rotten_flesh_effect_desc",
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
