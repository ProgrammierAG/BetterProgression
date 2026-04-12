package better_progression.skillLogic;

import better_progression.BetterProgression;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Attachments {

    public static final AttachmentType<List<String>> UNLOCKED_SKILLS = AttachmentRegistry.createPersistent(
            Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "unlocked_skills"),
            Codec.list(Codec.STRING)
    );
}
