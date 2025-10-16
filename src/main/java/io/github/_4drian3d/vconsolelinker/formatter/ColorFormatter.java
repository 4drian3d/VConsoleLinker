package io.github._4drian3d.vconsolelinker.formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorFormatter implements Formatter {

  private final Pattern RGB_PATTERN = Pattern.compile("\u001B\\[38;2;(\\d+);(\\d+);(\\d+)m");
  private final Pattern RESET_PATTERN = Pattern.compile("\u001B\\[0m");

  @Override
  public String format(final String message) {
    final Matcher matcher = RGB_PATTERN.matcher(message);
    final StringBuilder builder = new StringBuilder();
    while (matcher.find()) {
      final int red = Integer.parseInt(matcher.group(1));
      final int green = Integer.parseInt(matcher.group(2));
      final int blue = Integer.parseInt(matcher.group(3));

      final int ansiCode = approximateAnsiColor(red, green, blue);
      matcher.appendReplacement(builder, "\u001B[" + ansiCode + "m");
    }
    matcher.appendTail(builder);

    final String clean = RESET_PATTERN.matcher(builder.toString())
        .replaceAll("\u001B[0m");
    return """
        ```ansi
        %s
        ```""".formatted(clean);
  }

  private int approximateAnsiColor(final int red, final int green, final int blue) {
    if (red > 200 && green < 80 && blue < 80) return 31;
    if (red < 80 && green > 200 && blue < 80) return 32;
    if (red > 200 && green > 200 && blue < 80) return 33;
    if (red < 80 && green < 80 && blue > 200) return 34;
    if (red > 200 && green < 80 && blue > 200) return 35;
    if (red < 80 && green > 200 && blue > 200) return 36;
    if (red > 200 && green > 200 && blue > 200) return 97;
    if (red < 100 && green < 100 && blue < 100) return 90;
    return 37;
  }
}
