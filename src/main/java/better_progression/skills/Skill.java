package better_progression.skills;


import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record Skill(String NAME_ID, String ENGLISH_NAME, String ENGLISH_DECRIPTION, Consumer<Player> action) {

    public String getName() {
        return NAME_ID;
    }
}
