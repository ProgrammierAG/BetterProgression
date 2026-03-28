package better_progression.Items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Properties;

public abstract class ModItem extends Item {

    public ModItem(Properties properties) {
        super(properties);
    }

    public abstract ResourceKey<Item> getItemKey();
}