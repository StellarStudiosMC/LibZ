package net.libz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.integrated.IntegratedServerLoader;

@Environment(EnvType.CLIENT)
@Mixin(value = IntegratedServerLoader.class, priority = 999)
public class IntegratedServerLoaderMixin {

    @ModifyVariable(method = "start(Lnet/minecraft/world/level/storage/LevelStorage$Session;Lcom/mojang/serialization/Dynamic;ZZLjava/lang/Runnable;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/SaveProperties;getLifecycle()Lcom/mojang/serialization/Lifecycle;"), ordinal = 1)
    private boolean startMixin(boolean original) {
        return false;
    }

}
