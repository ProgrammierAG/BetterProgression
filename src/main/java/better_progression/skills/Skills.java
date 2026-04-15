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

    public static final Skill SPEED = register(new Skill("speed", "speed_desc",
            ((player, level) -> {
                player.addEffect(new MobEffectInstance(MobEffects.SPEED));
            }),
            (player, level) -> {},
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "")
    ));


    public static final Skill ATTACK_RANGE = register(new Skill("attack_range", "attack_range_desc",
            (player, level) -> {},
            ((player, level) -> {
                player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(
                        player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).getBaseValue() + level + 10);
            }),
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "")
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
