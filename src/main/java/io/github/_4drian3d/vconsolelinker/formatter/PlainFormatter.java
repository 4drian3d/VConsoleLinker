package io.github._4drian3d.vconsolelinker.formatter;

import java.util.regex.Pattern;

public final class PlainFormatter implements Formatter {
  private final Pattern ANSI_COLOR_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

  @Override
  public String format(final String message) {
    return """
        ```
        %s
        ```""".formatted(ANSI_COLOR_PATTERN.matcher(message).replaceAll(""));
  }
}
