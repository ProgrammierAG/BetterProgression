package better_progression.items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public abstract class ModItem extends Item {

    public ModItem(Properties properties) {
        super(properties);
    }

    public abstract ResourceKey<Item> getItemKey();
}