package better_progression.skills;

import better_progression.BetterProgression;

import java.util.ArrayList;
import java.util.List;

public class Skills {
    public static List<Skill> SKILLS = new ArrayList<>();

    public static final Skill SPEED = register(new Skill("Speed", "Speed_desc",
            ((player, level) -> {
                player.setSpeed(2);
            })
    ));

    public static Skill register(Skill skill) {
        BetterProgression.getLogger().info("registering Skill: " + skill.NAME_ID());
        SKILLS.add(skill);
        return skill;
    }
    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skills");
    }
}
