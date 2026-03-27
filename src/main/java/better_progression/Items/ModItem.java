package better_progression.Items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.Properties;

public abstract class ModItem extends Item {

    public ModItem(Properties properties) {
        super(properties);
    }

    public abstract ResourceKey<Item> getItemKey();
}