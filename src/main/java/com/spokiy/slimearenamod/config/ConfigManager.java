package com.spokiy.slimearenamod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("slimearena-server.json");

    public static ServerConfig load() {
        try {
            if (Files.exists(PATH)) {
                String json = Files.readString(PATH);
                return GSON.fromJson(json, ServerConfig.class);
            } else {
                ServerConfig config = new ServerConfig();
                save(config);
                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ServerConfig();
        }
    }

    public static void save(ServerConfig config) {
        try {
            Files.writeString(PATH, GSON.toJson(config));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}