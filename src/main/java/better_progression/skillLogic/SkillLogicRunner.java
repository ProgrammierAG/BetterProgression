package better_progression.skillLogic;

import better_progression.BetterProgression;
import better_progression.skills.Skills;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SkillLogicRunner {

    public static void initialize() {
        BetterProgression.getLogger().info("registering SkillLogicRunner");
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                Optional.ofNullable(player.getAttached(Attachments.UNLOCKED_SKILLS))
                        .ifPresent(unlockedSkills -> unlockedSkills.stream()
                                .map(Skills.SKILLS::get)
                                .filter(Objects::nonNull)
                                .forEach(skill -> {
                                    skill.onTick().process(player, player.level(), 1);
                                })
                        );
            }
        });
        BetterProgression.getLogger().info("registering PlayerJoin Event");
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            handler.player.getAttached(Attachments.UNLOCKED_SKILLS).stream().forEach(Name_ID -> {
                        Skills.SKILLS.get(Name_ID).onUnlock().process(handler.player, handler.player.level(), 1);
                    }
            );
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, sender) -> {
            handler.player.getAttached(Attachments.UNLOCKED_SKILLS).stream().forEach(Name_ID -> {
                Skills.SKILLS.get(Name_ID).onReset().process(handler.player, handler.player.level(), 0);
            });
        });
    }
}
