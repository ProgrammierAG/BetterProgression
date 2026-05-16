package better_progression.items;

import better_progression.BetterProgression;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final SkillpointBottle SKILLPOINT_BOTTLE = Registry.register(
            BuiltInRegistries.ITEM,
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "skill_point_bottle"),
            new SkillpointBottle()
    );


    public static void initialize() {
        BetterProgression.getLogger().info("Registering Mod Items");

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(itemGroup -> itemGroup.accept(ModItems.SKILLPOINT_BOTTLE));
    }
}

