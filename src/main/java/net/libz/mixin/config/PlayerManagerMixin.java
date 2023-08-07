package net.libz.mixin.config;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.libz.api.ConfigSync;
import net.libz.init.ConfigInit;
import net.libz.network.LibzServerPacket;
import net.libz.util.ConfigHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/world/ServerWorld;getGameRules()Lnet/minecraft/world/GameRules;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info, GameProfile gameProfile, UserCache userCache, String string, NbtCompound nbtCompound,
            RegistryKey<World> registryKey, ServerWorld serverWorld, ServerWorld serverWorld2, String string2, WorldProperties worldProperties, ServerPlayNetworkHandler serverPlayNetworkHandler) {

        // TEST
        // AutoConfigAccess.getHolders().forEach((data, holder) -> {
        //     if (holder.getConfig() instanceof ConfigSync) {
        //         ConfigSerializer<?> configSerializer = ((ConfigManager<?>) holder).getSerializer();

        //         if (configSerializer instanceof GsonConfigSerializer || configSerializer instanceof JanksonConfigSerializer) {

        //             byte[] bytes = ConfigHelper.getConfigBytes(((ConfigManager<?>) holder).getDefinition().name(), configSerializer instanceof GsonConfigSerializer, true);
        //             System.out.println(bytes + " : " + bytes.length);
        //             for (int i = 0; i < bytes.length; i++) {
        //                 // System.out.println(FileUtils.sizeOf(bytes[i]));
        //                 System.out.println(i + ": " + bytes[i]);
        //             }
        //         }
        //     }
        // });

        if (ConfigInit.CONFIG.syncConfig) {
            if (!connection.isLocal()) {
                AutoConfigAccess.getHolders().forEach((data, holder) -> {
                    if (holder.getConfig() instanceof ConfigSync) {
                        ConfigSerializer<?> configSerializer = ((ConfigManager<?>) holder).getSerializer();

                        if (configSerializer instanceof GsonConfigSerializer || configSerializer instanceof JanksonConfigSerializer) {
                            LibzServerPacket.writeS2CConfigPacket(serverPlayNetworkHandler, ((ConfigManager<?>) holder).getDefinition().name(), configSerializer instanceof GsonConfigSerializer);
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
    }
}
