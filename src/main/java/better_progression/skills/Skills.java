package better_progression.skills;

import better_progression.BetterProgression;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.List;

public class Skills {
    public static final List<Skill> SKILLS = new ArrayList<>();

    public static final Skill SPEED_BOOST = register(new Skill(
            "speed_boost",
            "Speeed boost",
            "makes the player faster",
            player -> player.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 1))
    ));

    private static Skill register(Skill skill) {
        SKILLS.add(skill);
        return skill;
    }

    public static void initialize() {
        BetterProgression.getLogger().info("Registering Skills");
    }
}
