package better_progression.skillLogic;

import better_progression.BetterProgression;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Attachments {

    public static final AttachmentType<List<String>> UNLOCKED_SKILLS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "unlocked_skills"),
            builder -> builder
                    .initializer(ArrayList::new)
                    .persistent(Codec.STRING.listOf())
                    .syncWith(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
                            AttachmentSyncPredicate.targetOnly())
                    .copyOnDeath()
    );

    public static final AttachmentType<Integer> SKILLPOINTS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "skillpoints"),
            builder -> builder
                    .initializer(() -> 0)
                    .persistent(Codec.INT)
                    .syncWith(
                            ByteBufCodecs.VAR_INT,
                            AttachmentSyncPredicate.targetOnly()
                    )
    );

    public static void initialize() {
        BetterProgression.getLogger().info("Initializing Attachments");
    }
}
