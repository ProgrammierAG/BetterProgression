package better_progression.skillLogic;

import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface SkillTickLogic {
    public void process(Player player, int level);
}
