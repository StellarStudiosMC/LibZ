package net.libz.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.shedaniel.autoconfig.ConfigData;

public interface ConfigSync {

    public void updateConfig(ConfigData data);

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ClientOnly {
    }
}
