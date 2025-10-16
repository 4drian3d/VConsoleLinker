package io.github._4drian3d.vconsolelinker.configuration;

import io.github._4drian3d.vconsolelinker.formatter.ColorFormatter;
import io.github._4drian3d.vconsolelinker.formatter.Formatter;
import io.github._4drian3d.vconsolelinker.formatter.PlainFormatter;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface Configuration {

  String getChannelId();

  String getToken();

  Formatter formatter();

  static Configuration load(final Path path) throws IOException {
    final Path configPath = loadFiles(path);
    final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
        .path(configPath)
        .build();
    final CommentedConfigurationNode loaded = loader.load();
    final String channelId = loaded.node("channel-id").getString();
    final String token = loaded.node("token").getString();
    final Formatter formatter = loaded.node("color-support").getBoolean(true)
        ? new ColorFormatter()
        : new PlainFormatter();
    return new Configuration() {
      @Override
      public String getChannelId() {
        return channelId;
      }

      @Override
      public String getToken() {
        return token;
      }

      @Override
      public Formatter formatter() {
        return formatter;
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
