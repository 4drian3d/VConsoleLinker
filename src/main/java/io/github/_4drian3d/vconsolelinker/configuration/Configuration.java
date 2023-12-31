package io.github._4drian3d.vconsolelinker.configuration;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface Configuration {

    String getChannelId();

    String getToken();

    static Configuration load(final Path path) throws IOException {
        final Path configPath = loadFiles(path);
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setPath(configPath)
                .build();
        final CommentedConfigurationNode loaded = loader.load();
        final String channelId = loaded.getNode("channel-id").getString();
        final String token =  loaded.getNode("token").getString();
        return new Configuration() {
            @Override
            public String getChannelId() {
                return channelId;
            }

            @Override
            public String getToken() {
                return token;
            }
        };
    }

    private static Path loadFiles(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }
        final Path configPath = path.resolve("config.conf");
        if (Files.notExists(configPath)) {
            try (final var stream = Configuration.class.getClassLoader().getResourceAsStream("config.conf")) {
                Files.copy(Objects.requireNonNull(stream), configPath);
            }
        }
        return configPath;
    }
}
