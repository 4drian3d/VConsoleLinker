package io.github._4drian3d.vconsolelinker.formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Formatter {
  String format(String message);

  Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

  static String trimAnsi(String input) {
    if (input.length() <= 2000 - 20) {
      return input;
    }
    int end = 2000 - 20;
    final Matcher m = ANSI_PATTERN.matcher(input);
    while (m.find()) {
      if (m.start() < end && m.end() > end) {
        end = m.start();
        break;
      }
    }
    String trimmed = input.substring(0, end);
    if (!trimmed.endsWith("\u001B[0m")) {
      trimmed += "\u001B[0m";
    }
    return trimmed;
  }
}
