package better_progression.skillLogic;

import better_progression.Attachments;
import better_progression.BetterProgression;
import better_progression.skillTree.nodeTypes.Node;
import better_progression.skillTree.SkillTree;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class SkillLogicRunner {
    private static final Map<UUID, SkillContext> CONTEXT_CACHE = new HashMap<>();

    public static void initialize() {
        BetterProgression.getLogger().info("registering SkillLogicRunner");

        // Run skills at the end of the server tick so movement changes (e.g. setDeltaMovement)
        // applied by skills are not immediately overwritten by later player movement logic.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getPlayerList().getPlayers().forEach(player -> {
                SkillContext context = CONTEXT_CACHE.get(player.getUUID());
                if (context == null) {
                    return;
                }

                // Check if the player reference is stale (e.g., after respawn) and update it
                if (context.getPlayer() != player) {
                    context.setPlayer(player);
                }

                List<String> skills = player.getAttachedOrCreate(Attachments.UNLOCKED_SKILLS, ArrayList::new);
                Map<String, Integer> levels = player.getAttachedOrCreate(Attachments.SKILL_LEVELS, HashMap::new);

                skills.forEach(id -> {
                    getNodeFromRegistry(id).ifPresent(node -> {
                        int currentLevel = levels.getOrDefault(node.getSkill().id(), 1);

                        context.setSkillLevel(currentLevel);
                        node.getSkill().tick(context);
                    });
                });
            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            CONTEXT_CACHE.put(player.getUUID(), new SkillContext(player, 1));

            List<String> rawSkills = player.getAttachedOrCreate(Attachments.UNLOCKED_SKILLS, ArrayList::new);
            List<String> mutableSkills = new ArrayList<>(rawSkills);

            boolean changed = mutableSkills.removeIf(id -> getNodeFromRegistry(id).isEmpty() && !id.equals("GLOBAL_ROOT"));

            if (changed) {
                player.setAttached(Attachments.UNLOCKED_SKILLS, mutableSkills);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            CONTEXT_CACHE.remove(handler.getPlayer().getUUID());
        });
    }

    private static Optional<Node> getNodeFromRegistry(String id) {
        return SkillTree.REGISTRY.values().stream()
                .map(tree -> tree.getNodes().get(id))
                .filter(Objects::nonNull)
                .filter(node -> node.getSkill() != null)
                .findFirst();
    }
}
