package nl.eetgeenappels.ssssv.network

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class PreviewPayload(val blocks: List<BlockPos>) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<PreviewPayload> =
            CustomPacketPayload.Type(
                ResourceLocation.parse("ssssv:preview_payload")
            )

        val CODEC: StreamCodec<ByteBuf, PreviewPayload> = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            PreviewPayload::blocks,
            ::PreviewPayload
        )
    }
}