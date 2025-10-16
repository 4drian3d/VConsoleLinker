package io.github._4drian3d.vconsolelinker.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github._4drian3d.jdwebhooks.WebHook;
import io.github._4drian3d.jdwebhooks.WebHookClient;
import io.github._4drian3d.vconsolelinker.configuration.Configuration;
import io.github._4drian3d.vconsolelinker.formatter.Formatter;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public final class WebHookManager {

  private final Queue<String> logQueue = new ConcurrentLinkedQueue<>();
  private final WebHookClient client;

  @Inject
  private WebHookManager(final Configuration configuration, final Logger logger) {
    this.client = WebHookClient.from(configuration.getChannelId(), configuration.getToken());
    final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
        .setNameFormat("VConsoleLinker %s")
        .setUncaughtExceptionHandler((thread, ex) -> logger.error("An error occurred", ex))
        .build());
    executor.scheduleAtFixedRate(() -> {
      final String webHookOutput = this.populateLogMessage();
      if (webHookOutput != null) {
        client.sendWebHook(WebHook.builder().content(configuration.formatter().format(webHookOutput)).build());
      }
    }, 0, 500, TimeUnit.MILLISECONDS);
  }

  public void logMessage(String string) {
    this.logQueue.add(string);
  }

  private String populateLogMessage() {
    String logMessage = logQueue.poll();
    if (logMessage != null) {
      // Single line limit: 2000
      if (logMessage.length() > 2000) {
        return Formatter.trimAnsi(logMessage.substring(0, 2000));
      }
      final StringBuilder builder = new StringBuilder(2000);
      do {
        // Discord character limit: 2000
        if (builder.length() + logMessage.length() > 2000) {
          break;
        }
        builder.append('\n').append(logMessage);
      } while((logMessage = logQueue.poll()) != null);
      return Formatter.trimAnsi(builder.toString());
    }
    return null;
  }
}
