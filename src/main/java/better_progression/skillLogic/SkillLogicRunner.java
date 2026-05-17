package better_progression.skillLogic;

import better_progression.Attachments;
import better_progression.BetterProgression;
import better_progression.skillTreeV2.nodeTypes.Node;
import better_progression.skillTreeV2.SkillTree;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class SkillLogicRunner {
    private static final Map<UUID, SkillContext> CONTEXT_CACHE = new HashMap<>();

    public static void initialize() {
        BetterProgression.getLogger().info("registering SkillLogicRunner");

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            server.getPlayerList().getPlayers().forEach(player -> {
                SkillContext context = CONTEXT_CACHE.get(player.getUUID());
                if (context == null) return;

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
