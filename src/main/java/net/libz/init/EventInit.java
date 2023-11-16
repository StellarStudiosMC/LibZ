package net.libz.init;

import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.libz.api.ConfigSync;
import net.libz.mixin.config.AutoConfigAccess;
import net.libz.network.LibzServerPacket;

public class EventInit {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // check local
            if (ConfigInit.CONFIG.syncConfig) {
                if (server.isDedicated()) {
                    AutoConfigAccess.getHolders().forEach((data, holder) -> {
                        if (holder.getConfig() instanceof ConfigSync) {
                            ConfigSerializer<?> configSerializer = ((ConfigManager<?>) holder).getSerializer();

                            if (configSerializer instanceof GsonConfigSerializer || configSerializer instanceof JanksonConfigSerializer) {
                                LibzServerPacket.writeS2CConfigPacket(handler, ((ConfigManager<?>) holder).getDefinition().name(), configSerializer instanceof GsonConfigSerializer);
                            }
                        }
                    });
                } else {
                    AutoConfigAccess.getHolders().forEach((data, holder) -> {
                        if (holder.getConfig() instanceof ConfigSync) {
                            holder.load();
                            ((ConfigSync) holder.getConfig()).updateConfig(holder.getConfig());
                        }
                    });
                }
            }
        });
    }

}
