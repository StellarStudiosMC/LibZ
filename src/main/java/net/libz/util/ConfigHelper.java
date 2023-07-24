package net.libz.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.Nullable;

import me.shedaniel.autoconfig.util.Utils;

public class ConfigHelper {

    public static void copyConfig(String configName, boolean gson) {
        Path configPath = getConfigPath(configName, gson);
        Path singleplayerConfigPath = configPath.resolveSibling("singleplayer_" + configPath.getFileName());
        try {
            Files.copy(configPath, singleplayerConfigPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readConfigFile(String configName, boolean gson, boolean excludeClientOnly) {
        try {
            String string = Files.readString(getConfigPath(configName, gson));
            if (!gson && excludeClientOnly) {

                StringBuilder configString = new StringBuilder();
                String[] configStrings = string.split("\n");

                for (int i = 0; i < configStrings.length; i++) {
                    if (!configStrings[i].contains("//") || !configStrings[i].contains("client only")) {
                        configString.append(configStrings[i] + "\n");
                    } else {
                        i += 1;
                    }
                }
                return configString.toString().replaceAll(",\\s*\\}", "\n}");
            }
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static JsonNode getConfigNode(String configName, boolean gson, boolean excludeClientOnly) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        try {
            return objectMapper.readTree(readConfigFile(configName, gson, excludeClientOnly));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static byte[] getConfigBytes(String configName, boolean gson, boolean excludeClientOnly) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsBytes(getConfigNode(configName, gson, excludeClientOnly));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static JsonNode readJsonTree(ObjectMapper objectMapper, byte[] bytes) {
        try {
            return objectMapper.readTree(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Path getConfigPath(String configName, boolean gson) {
        return Utils.getConfigFolder().resolve(configName + (gson ? ".json" : ".json5"));
    }

}
