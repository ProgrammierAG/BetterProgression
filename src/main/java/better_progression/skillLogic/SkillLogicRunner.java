package better_progression.skillLogic;

import better_progression.Attachments;
import better_progression.BetterProgression;
import better_progression.skillTree.SkillTree;
import better_progression.skills.Skill;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class SkillLogicRunner {

    public static void initialize() {
        BetterProgression.getLogger().info("registering SkillLogicRunner for multiple trees");

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            server.getPlayerList().getPlayers().forEach(player -> {
                List<String> skills = player.getAttachedOrCreate(Attachments.UNLOCKED_SKILLS, ArrayList::new);
                Map<String, Integer> levels = player.getAttachedOrCreate(Attachments.SKILL_LEVELS, HashMap::new);

                skills.forEach(id -> {
                    getSkillFromRegistry(id).ifPresent(skill -> {
                        int level = levels.getOrDefault(skill.id(), 1);
                        skill.tick(player, level);
                    });
                });
            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            List<String> rawSkills = player.getAttachedOrCreate(Attachments.UNLOCKED_SKILLS, ArrayList::new);
            List<String> mutableSkills = new ArrayList<>(rawSkills);

            boolean changed = mutableSkills.removeIf(id -> getSkillFromRegistry(id).isEmpty() && !id.equals("GLOBAL_ROOT"));

            if (changed) {
                player.setAttached(Attachments.UNLOCKED_SKILLS, mutableSkills);
                BetterProgression.getLogger().info("Cleaned up {} obsolete skill IDs for player {}",
                        rawSkills.size() - mutableSkills.size(), player.getName().getString());
            }
        });
    }

    private static Optional<Skill> getSkillFromRegistry(String id) {
        return SkillTree.REGISTRY.values().stream()
                .map(tree -> tree.getSkillButtons().get(id))
                .filter(Objects::nonNull)
                .findFirst();
    }
}
