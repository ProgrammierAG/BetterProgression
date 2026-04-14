package better_progression.networking;

import better_progression.BetterProgression;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SkillUnlockPayload(String NAME_ID) implements CustomPacketPayload {
    public static final Type<SkillUnlockPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "unlocked_skills"));

    public static final StreamCodec<ByteBuf, SkillUnlockPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SkillUnlockPayload::NAME_ID,
            SkillUnlockPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
