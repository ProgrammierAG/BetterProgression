package better_progression.skillLogic;

import better_progression.skills.Skills;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SkillLogicRunner {

    public static void initialize() {
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
    }
}
