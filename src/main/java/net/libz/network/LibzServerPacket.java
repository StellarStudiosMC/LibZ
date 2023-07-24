package net.libz.network;

import io.netty.buffer.Unpooled;
import net.libz.util.ConfigHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

public class LibzServerPacket {

    public static final Identifier SYNC_CONFIG_PACKET = new Identifier("libz", "sync_config");

    public static void init() {
    }

    public static void writeS2CConfigPacket(ServerPlayNetworkHandler serverPlayNetworkHandler, String configName, boolean gson) {

        byte[] bytes = ConfigHelper.getConfigBytes(configName, gson, true);

        if (bytes != null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeString(configName);
            buf.writeBoolean(gson);
            buf.writeBytes(bytes);
            CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(SYNC_CONFIG_PACKET, buf);
            serverPlayNetworkHandler.sendPacket(packet);
        }
    }

}
