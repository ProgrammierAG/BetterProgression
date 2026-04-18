package better_progression.skillLogic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface SkillTickLogic {
    public void process(Player player, Level world, int level);
}
