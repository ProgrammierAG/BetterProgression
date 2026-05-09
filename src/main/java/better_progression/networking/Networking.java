package better_progression.networking;

import better_progression.BetterProgression;
import better_progression.Attachments;
import better_progression.skillLogic.SkillTree;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Networking {

    public static void  registerServerReceiver() {
        BetterProgression.getLogger().info("registering Server receiver");
        PayloadTypeRegistry.playC2S().register(SkillUnlockPayload.TYPE, SkillUnlockPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SkillUnlockPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();

            context.server().execute(() -> {
                BetterProgression.getLogger().info("recived Payload for: " + payload.NAME_ID());
                if (canUnlock(player, payload.NAME_ID())) {
                    unlockSkillForPlayer(player, payload.NAME_ID());
                }
            });
        });
    }

    private static boolean canUnlock(ServerPlayer player, String Name_ID) {
        return true;
    }

    private static void unlockSkillForPlayer(ServerPlayer player, String Name_ID) {
        //work in progress
        List<String> currentSkills = player.getAttached(Attachments.UNLOCKED_SKILLS);
        List<String> newList = (currentSkills == null) ? new ArrayList<>() : new ArrayList<>(currentSkills);

        Map<String, Integer> currentSkillLevels = player.getAttached(Attachments.SKILL_LEVELS);
        Map<String, Integer> newMap = (currentSkillLevels == null) ? new HashMap<>() : new HashMap<>(currentSkillLevels);

        String SkillName = SkillTree.skillButtons.get(Name_ID).NAME_ID();

        if (!newMap.containsKey(SkillName)) {
            newMap.put(SkillName, 1);
        } else {
            newMap.put(SkillName, newMap.get(SkillName) + 1);
        }

        player.setAttached(Attachments.SKILL_LEVELS, newMap);

        if (!newList.contains(Name_ID)) {
            newList.add(Name_ID);

            player.setAttached(Attachments.UNLOCKED_SKILLS, newList);

            SkillTree.skillButtons.get(Name_ID).onUnlock().process(player, player.level(),
                    newMap.get(SkillTree.skillButtons.get(Name_ID).NAME_ID()));

            BetterProgression.getLogger().info("Skill " + Name_ID + " unlocked");
            player.displayClientMessage(Component.literal("Skill " + Name_ID + " unlocked!"), true);
        }
    }
}
