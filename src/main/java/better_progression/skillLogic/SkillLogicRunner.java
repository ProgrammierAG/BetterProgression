package better_progression.skillLogic;

import better_progression.Attachments;
import better_progression.BetterProgression;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.*;

public class SkillLogicRunner {

    public static void initialize() {
        BetterProgression.getLogger().info("registering SkillLogicRunner");
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            server.getPlayerList().getPlayers().forEach(player -> {
                List<String> skills = player.getAttachedOrCreate(Attachments.UNLOCKED_SKILLS, ArrayList::new);
                Map<String, Integer> levels = player.getAttachedOrCreate(Attachments.SKILL_LEVELS, HashMap::new);

                skills.forEach(skill_ID -> SkillTree.skillButtons.get(skill_ID).onTick().process(
                        player, player.level(), levels.getOrDefault(
                                SkillTree.skillButtons.get(skill_ID).NAME_ID(), 0)));
            });
        });
    }
}
