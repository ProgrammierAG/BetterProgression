package better_progression.networking;

import better_progression.BetterProgression;
import better_progression.Attachments;
import better_progression.skillLogic.SkillContext;
import better_progression.skillTree.SkillTree;
import better_progression.skillTree.nodeTypes.Node;
import better_progression.skillTree.nodeTypes.ChoiceNode;
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

                // Try to find a direct Node with the given id first
                Optional<Node> direct = SkillTree.REGISTRY.values().stream()
                        .map(tree -> tree.getNodes().get(payload.NAME_ID()))
                        .filter(Objects::nonNull)
                        .findFirst();

                if (direct.isPresent()) {
                    // Found a normal node
                    SkillTree tree = SkillTree.REGISTRY.values().stream()
                            .filter(t -> t.getNodes().containsKey(payload.NAME_ID()))
                            .findFirst().orElse(null);

                    if (tree != null && canUnlock(player, direct.get(), tree)) {
                        unlockSkillForPlayer(player, direct.get(), tree);
                    }
                    return;
                }

                // Not a normal node — maybe it's one half of a ChoiceNode (id ends with _left/_right)
                // Search for a ChoiceNode that defines this half id
                SkillTree foundTree = null;
                ChoiceNode foundChoice = null;
                for (SkillTree tree : SkillTree.REGISTRY.values()) {
                    for (Node n : tree.getNodes().values()) {
                        if (n instanceof ChoiceNode cp) {
                            if (cp.getIdA().equals(payload.NAME_ID()) || cp.getIdB().equals(payload.NAME_ID())) {
                                foundTree = tree;
                                foundChoice = cp;
                                break;
                            }
                        }
                    }
                    if (foundChoice != null) break;
                }

                if (foundChoice == null) {
                    BetterProgression.getLogger().warn("Player {} tried to unlock a non-existent node: {}",
                            player.getName().getString(), payload.NAME_ID());
                    return;
                }

                // Determine which half was clicked and create a temporary Node representing that half
                String halfId = payload.NAME_ID();
                boolean isA = foundChoice.getIdA().equals(halfId);
                Node virtualHalf = new Node(halfId, isA ? foundChoice.getSkillA() : foundChoice.getSkillB(), isA ? foundChoice.getCostA() : foundChoice.getCostB());

                // The prerequisites for unlocking the half are the parents of the ChoiceNode
                // (i.e. you must have unlocked upstream nodes to reach the choice)
                for (Node p : foundChoice.getParents()) {
                    virtualHalf.getParents().add(p);
                }

                if (foundTree != null && canUnlock(player, virtualHalf, foundTree)) {
                    // When unlocking, we will add the half-id to unlocked skills and
                    // invoke the corresponding skill unlock logic (via the temporary Node)
                    unlockSkillForPlayer(player, virtualHalf, foundTree);
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
        }

        if (Node.isBlockedByChoice(node, activeUnlocked)) {
            player.displayClientMessage(Component.translatable("message.betterprogression.blocked"), true);
            return false;
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
}