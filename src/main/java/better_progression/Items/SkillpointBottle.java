package better_progression.Items;

import better_progression.BetterProgression;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class SkillpointBottle extends ModItem{
    public static final String NAME_ID = "skillpoint_bottle";
    public static final String ENGLISH_NAME = "Skillpoint Bottle";
    public static final String GERMAN_NAME = "Fähigkeitspunkt Flasche";

    public static final ResourceKey<Item> ITEM_KEY = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, NAME_ID));

    public SkillpointBottle() {
        super(new Properties().setId(ITEM_KEY).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        //spawn Skillpoint Entity
        return  InteractionResult.PASS;
    }

    @Override
    public ResourceKey<Item> getItemKey() {
        return ITEM_KEY;
    }
}
