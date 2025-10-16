package io.github._4drian3d.vconsolelinker.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github._4drian3d.jdwebhooks.WebHook;
import io.github._4drian3d.jdwebhooks.WebHookClient;
import io.github._4drian3d.vconsolelinker.configuration.Configuration;
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
      final StringBuilder builder = new StringBuilder(2000);
      String log;
      while ((log = logQueue.peek()) != null) {
        // Discord character limit: 2000
        if (builder.length() + log.length() > 2000) {
          // Single line limit: 2000
          if (builder.isEmpty() && log.length() > 2000) {
            final String logQueued = logQueue.poll();
            builder.append(logQueued, 0, Math.min(logQueued.length() - 1, 2000));
          }
         break;
        }
        builder.append('\n').append(logQueue.poll());
      }
      if (!builder.isEmpty()) {
        final String webHookOutput = builder.toString();
        client.sendWebHook(WebHook.builder().content(configuration.formatter().format(webHookOutput)).build())
            .handle((response, e) -> {
              if (e != null) {
                logger.warn("An error occurred while trying to send console output to Discord Channel 1", e);
              }
              return null;
            });
      }
    }, 0, 500, TimeUnit.MILLISECONDS);
  }

  public void logMessage(String string) {
    this.logQueue.add(string);
  }
}
