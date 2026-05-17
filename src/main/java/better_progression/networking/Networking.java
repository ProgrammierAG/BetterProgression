package better_progression.networking;

import better_progression.BetterProgression;
import better_progression.Attachments;
import better_progression.skillLogic.SkillContext;
import better_progression.skillTreeV2.SkillTree;
import better_progression.skillTreeV2.nodeTypes.ChoiceNode;
import better_progression.skillTreeV2.nodeTypes.Node;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class Networking {
    public static void registerServerReceiver() {
        BetterProgression.getLogger().info("registering Server receiver");
        PayloadTypeRegistry.playC2S().register(SkillUnlockPayload.TYPE, SkillUnlockPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SkillUnlockPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();

            context.server().execute(() -> {
                BetterProgression.getLogger().info("received Payload for: {}", payload.NAME_ID());

                // Sucht das Node-Objekt in allen registrierten Bäumen
                Optional<Node> targetNode = SkillTree.REGISTRY.values().stream()
                        .map(tree -> tree.getNodes().get(payload.NAME_ID()))
                        .filter(Objects::nonNull)
                        .findFirst();

                if (targetNode.isEmpty()) {
                    BetterProgression.getLogger().warn("Player {} tried to unlock a non-existent node: {}",
                            player.getName().getString(), payload.NAME_ID());
                    return;
                }

                // Holt den zugehörigen Baum für Kontext-Abfragen
                SkillTree tree = SkillTree.REGISTRY.values().stream()
                        .filter(t -> t.getNodes().containsKey(payload.NAME_ID()))
                        .findFirst().orElse(null);

                if (tree != null && canUnlock(player, targetNode.get(), tree)) {
                    unlockSkillForPlayer(player, targetNode.get(), tree);
                }
            });
        });
    }

    private static boolean canUnlock(ServerPlayer player, Node node, SkillTree tree) {
        Integer skillPoints = player.getAttached(Attachments.SKILLPOINTS);
        int currentPoints = (skillPoints != null) ? skillPoints : 0;
        if (currentPoints < node.getCost()) {
            return false;
        }

        List<String> unlockedSkills = player.getAttached(Attachments.UNLOCKED_SKILLS);
        List<String> activeUnlocked = (unlockedSkills != null) ? unlockedSkills : List.of();

        List<Node> parents = node.getParents();

        if (parents != null && !parents.isEmpty()) {
            boolean hasUnlockedParent = parents.stream().anyMatch(p -> activeUnlocked.contains(p.getId()));
            if (!hasUnlockedParent) {
                player.displayClientMessage(Component.translatable("message.betterprogression.requires_parent"), true);
                return false;
            }
        } else {
            Optional<Node> globalStartNode = SkillTree.REGISTRY.values().stream()
                    .flatMap(t -> t.getRootNodes().stream())
                    .findFirst();

            if (globalStartNode.isPresent()) {
                String firstNodeId = globalStartNode.get().getId();
                if (!node.getId().equals(firstNodeId) && !activeUnlocked.contains(firstNodeId)) {
                    player.displayClientMessage(Component.translatable("message.betterprogression.requires_parent"), true);
                    return false;
                }
            }
        }

        return true;
    }

    private static void unlockSkillForPlayer(ServerPlayer player, Node node, SkillTree tree) {
        List<String> currentSkills = player.getAttached(Attachments.UNLOCKED_SKILLS);
        List<String> newList = (currentSkills == null) ? new ArrayList<>() : new ArrayList<>(currentSkills);

        Map<String, Integer> currentSkillLevels = player.getAttached(Attachments.SKILL_LEVELS);
        Map<String, Integer> newMap = (currentSkillLevels == null) ? new HashMap<>() : new HashMap<>(currentSkillLevels);

        String skillName = node.getSkill().id();

        if (!newMap.containsKey(skillName)) {
            newMap.put(skillName, 1);
        } else {
            newMap.put(skillName, newMap.get(skillName) + 1);
        }

        player.setAttached(Attachments.SKILL_LEVELS, newMap);

        if (!newList.contains(node.getId())) {
            newList.add(node.getId());
            player.setAttached(Attachments.UNLOCKED_SKILLS, newList);

            int currentPoints = player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);
            player.setAttached(Attachments.SKILLPOINTS, currentPoints - node.getCost());


            node.getSkill().unlock(new SkillContext(player, newMap.get(skillName)));

            BetterProgression.getLogger().info("Skill {} unlocked in tree {}", node.getId(), tree.getTreeId());
            player.displayClientMessage(Component.translatable("message.betterprogression.unlocked_success"), true);
        }
    }

    private static boolean isBlockedByChoice(Node node, List<String> activeUnlocked) {
        if (activeUnlocked.contains(node.getId())) return false;

        if (node instanceof ChoiceNode choiceNode && choiceNode.getPartner() != null) {
            if (activeUnlocked.contains(choiceNode.getPartner().getId())) {
                return true;
            }
        }

        List<Node> parents = node.getParents();
        if (parents == null || parents.isEmpty()) return false;

        return parents.stream().allMatch(parent -> isBlockedByChoice(parent, activeUnlocked));
    }
}