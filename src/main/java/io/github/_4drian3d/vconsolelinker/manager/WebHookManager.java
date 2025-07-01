package io.github._4drian3d.vconsolelinker.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github._4drian3d.jdwebhooks.WebHook;
import io.github._4drian3d.jdwebhooks.WebHookClient;
import io.github._4drian3d.vconsolelinker.configuration.Configuration;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public final class WebHookManager {
    private final Queue<String> logQueue = new LinkedList<>();
    private final WebHookClient client;

    @Inject
    private WebHookManager(final Configuration configuration, Logger logger) {
        this.client = WebHookClient.from(configuration.getChannelId(), configuration.getToken());
        final ScheduledExecutorService executor =  Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setNameFormat("VConsoleLinker %s")
                .setUncaughtExceptionHandler((thread, ex) -> logger.error("An error occurred", ex))
                .build());
        executor.scheduleAtFixedRate(() -> {
            final StringBuilder builder = new StringBuilder();
            String log;
            while ((log = logQueue.poll()) != null) {
                builder.append('\n').append(log);
            }
            String WebHookOutput = "```ansi\n" + builder.toString() + "\n```";
            if (!WebHookOutput.equals("```ansi\n\n```")) {
                client.sendWebHook(WebHook.builder().content(WebHookOutput).build());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void logMessage(String string) {
        this.logQueue.add(string);
    }
}
