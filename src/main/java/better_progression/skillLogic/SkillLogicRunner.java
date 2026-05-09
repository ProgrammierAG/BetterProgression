package better_progression.skillLogic;

import better_progression.Attachments;
import better_progression.BetterProgression;
import better_progression.skills.Skill;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class SkillLogicRunner {

    public static void initialize() {
        BetterProgression.getLogger().info("registering SkillLogicRunner");

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            server.getPlayerList().getPlayers().forEach(player -> {
                List<String> skills = player.getAttachedOrCreate(Attachments.UNLOCKED_SKILLS, ArrayList::new);
                Map<String, Integer> levels = player.getAttachedOrCreate(Attachments.SKILL_LEVELS, HashMap::new);

                skills.forEach(id -> {
                    Skill skill = SkillTree.skillButtons.get(id);

                    int level = levels.getOrDefault(skill.NAME_ID(), 1);

                    skill.onTick().process(player, player.level(), level);
                });

            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            List<String> rawSkills = player.getAttachedOrCreate(Attachments.UNLOCKED_SKILLS, ArrayList::new);

            List<String> mutableSkills = new ArrayList<>(rawSkills);

            boolean changed = mutableSkills.removeIf(id -> SkillTree.skillButtons.get(id) == null);

            if (changed) {
                player.setAttached(Attachments.UNLOCKED_SKILLS, mutableSkills);
            }
        });
    }
}
