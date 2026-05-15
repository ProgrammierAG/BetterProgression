package better_progression.networking;

import better_progression.BetterProgression;
import better_progression.Attachments;
import better_progression.skillTree.SkillTree;
import better_progression.skills.Skill;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class Networking {

    public static void  registerServerReceiver() {
        BetterProgression.getLogger().info("registering Server receiver");
        PayloadTypeRegistry.playC2S().register(SkillUnlockPayload.TYPE, SkillUnlockPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SkillUnlockPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();

            context.server().execute(() -> {
                BetterProgression.getLogger().info("received Payload for: {}", payload.NAME_ID());

                Optional<SkillTree> targetTree = SkillTree.REGISTRY.values().stream()
                        .filter(tree -> tree.getSkillButtons().containsKey(payload.NAME_ID()))
                        .findFirst();

                if (targetTree.isEmpty()) {
                    BetterProgression.getLogger().warn("Player {} tried to unlock a node that does not exist in any registered tree: {}",
                            player.getName().getString(), payload.NAME_ID());
                    return;
                }

                if (canUnlock(player, payload.NAME_ID(), targetTree.get())) {
                    unlockSkillForPlayer(player, payload.NAME_ID(), targetTree.get());
                }
            });
        });
    }

    private static boolean canUnlock(ServerPlayer player, String nameId, SkillTree tree) {
        Integer skillPoints = player.getAttached(Attachments.SKILLPOINTS);
        int currentPoints = (skillPoints != null) ? skillPoints : 0;
        if (currentPoints < tree.getCost().get(nameId)) {
            return false;
        }

        List<String> unlockedSkills = player.getAttached(Attachments.UNLOCKED_SKILLS);
        List<String> activeUnlocked = (unlockedSkills != null) ? unlockedSkills : List.of();

        if (tree.isChoiceNode(nameId)) {
            String partnerId = tree.getChoicePartner(nameId);
            if (activeUnlocked.contains(partnerId)) {
                player.displayClientMessage(Component.translatable("message.betterprogression.blocked"), true);
                return false;
            }
        }

        List<String> parents = tree.getParents(nameId);

        if (parents != null && !parents.isEmpty()) {
            boolean hasUnlockedParent = parents.stream().anyMatch(activeUnlocked::contains);
            if (!hasUnlockedParent) {
                player.displayClientMessage(Component.translatable("message.betterprogression.requires_parent"), true);
                return false;
            }
        }

        return true;
    }

    private static void unlockSkillForPlayer(ServerPlayer player, String nameId, SkillTree tree) {
        List<String> currentSkills = player.getAttached(Attachments.UNLOCKED_SKILLS);
        List<String> newList = (currentSkills == null) ? new ArrayList<>() : new ArrayList<>(currentSkills);

        Map<String, Integer> currentSkillLevels = player.getAttached(Attachments.SKILL_LEVELS);
        Map<String, Integer> newMap = (currentSkillLevels == null) ? new HashMap<>() : new HashMap<>(currentSkillLevels);

        Skill skill = tree.getSkillButtons().get(nameId);
        String skillName = skill.id();

        if (!newMap.containsKey(skillName)) {
            newMap.put(skillName, 1);
        } else {
            newMap.put(skillName, newMap.get(skillName) + 1);
        }

        player.setAttached(Attachments.SKILL_LEVELS, newMap);

        if (!newList.contains(nameId)) {
            newList.add(nameId);

            player.setAttached(Attachments.UNLOCKED_SKILLS, newList);

            int currentPoints = player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);
            player.setAttached(Attachments.SKILLPOINTS, currentPoints - tree.getCost().get(nameId));

            skill.unlock(player, newMap.get(skillName));

            BetterProgression.getLogger().info("Skill {} unlocked in tree {}", nameId, tree.getTreeId());
            player.displayClientMessage(Component.translatable("message.betterprogression.unlocked_success", nameId), true);
        }
    }
}
