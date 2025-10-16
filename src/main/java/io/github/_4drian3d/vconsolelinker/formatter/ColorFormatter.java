package io.github._4drian3d.vconsolelinker.formatter;

import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    return RGB_TO_DISCORD_ANSI.get(TextColor.nearestColorTo(ANSI_COLORS, TextColor.color(red, green, blue)).value());
  }

  private static final List<TextColor> ANSI_COLORS;
  private static final Map<Integer, Integer> RGB_TO_DISCORD_ANSI = ImmutableMap
      .<Integer, Integer>builder()
      .put(0x000000, 30)
      .put(0x800000, 31)
      .put(0x008000, 32)
      .put(0x808000, 33)
      .put(0x000080, 34)
      .put(0x800080, 35)
      .put(0x008080, 36)
      .put(0xC0C0C0, 37)
      // Documented but not supported by Discord?
//      .put(0x808080, 90)
//      .put(0xFF0000, 91)
//      .put(0x00FF00, 92)
//      .put(0xFFFF00, 93)
//      .put(0x0000FF, 94)
//      .put(0xFF00FF, 95)
//      .put(0x00FFFF, 96)
//      .put(0xFFFFFF, 97)
      .build();

  static {
    final List<TextColor> textColorList = new ArrayList<>();
    RGB_TO_DISCORD_ANSI.forEach((rgb, ansi) -> textColorList.add(TextColor.color(rgb)));
    ANSI_COLORS = List.copyOf(textColorList);
  }
}
