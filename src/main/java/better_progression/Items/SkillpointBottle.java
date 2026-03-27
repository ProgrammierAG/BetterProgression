package better_progression.Items;

import better_progression.BetterProgression;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class SkillpointBottle extends ModItem{
    public static final String NAME_ID = "skillpoint_bottle";
    public static final String ENGLISH_NAME = "Skillpoint Bottle";
    public static final String GERMAN_NAME = "Fähigkeitspunkt Flasche";

    public static final ResourceKey<Item> ITEM_KEY = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, NAME_ID));

    public SkillpointBottle() {
        super(new Properties().setId(ITEM_KEY));
    }

    @Override
    public ResourceKey<Item> getItemKey() {
        return ITEM_KEY;
    }
}
