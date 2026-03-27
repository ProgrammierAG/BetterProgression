package better_progression.Items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.function.Supplier;

public class ModItems {
    //public static final EnderRod ENDER_ROD = register(EnderRod::new);
    public static final SkillpointBottle SKILLPOINT_BOTTLE = register(SkillpointBottle::new);

    public static <T extends ModItem> T register(Supplier<T> itemFactory) {
        T item = itemFactory.get();

        Registry.register(BuiltInRegistries.ITEM, item.getItemKey(), item);

        return item;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register((itemGroup) -> itemGroup.accept(ModItems.SKILLPOINT_BOTTLE));
    }
}

