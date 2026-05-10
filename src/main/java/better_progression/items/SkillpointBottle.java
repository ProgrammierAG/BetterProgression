package better_progression.items;

import better_progression.Attachments;
import better_progression.BetterProgression;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class SkillpointBottle extends Item {
    public static final String ENGLISH_NAME = "Skill point Bottle";
    public static final String GERMAN_NAME = "Skill point Flasche";

    public static final Identifier ID = Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID,
            "skill_point_bottle");
    public static final ResourceKey<Item> KEY = ResourceKey.create(Registries.ITEM, ID);

    public SkillpointBottle() {
        super(new Item.Properties()
                .setId(KEY)
                .stacksTo(64)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            Integer currentPoints = serverPlayer.getAttached(Attachments.SKILLPOINTS);
            int newPoints = (currentPoints != null ? currentPoints : 0) + 1;

            serverPlayer.setAttached(Attachments.SKILLPOINTS, newPoints);

            level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5f, 1.0f);

            if (!serverPlayer.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }


        return InteractionResult.SUCCESS;
    }
}
