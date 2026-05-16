package better_progression.skillLogic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;



public class SkillContext {
    private ServerPlayer player;
    private int SkillLevel;

    public SkillContext(ServerPlayer player, int SkillLevel) {
        this.player = player;
        this.SkillLevel = SkillLevel;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    public void setSkillLevel(int skillLevel) {
        this.SkillLevel = skillLevel;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public int getSkillLevel() {
        return this.SkillLevel;
    }

    public Level gtWorld() {
        return this.player.level();
    }
}
