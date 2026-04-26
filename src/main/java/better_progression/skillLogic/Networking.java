package better_progression.skillLogic;

import better_progression.BetterProgression;
import better_progression.networking.SkillUnlockPayload;
import better_progression.skills.Skills;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

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

        if (!newList.contains(Name_ID)) {
            newList.add(Name_ID);

            player.setAttached(Attachments.UNLOCKED_SKILLS, newList);

            Skills.SKILLS.get(Name_ID).onUnlock().process(player, player.level(), 1);

            BetterProgression.getLogger().info("Skill " + Name_ID + " unlocked");
            player.displayClientMessage(Component.literal("Skill " + Name_ID + " freigeschaltet!"), true);
        }
    }
}
