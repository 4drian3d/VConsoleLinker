package io.github._4drian3d.vconsolelinker;

import com.google.inject.Inject;
import io.github._4drian3d.vconsolelinker.manager.WebHookManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "VConsoleLinker", category = "Core", elementType = "appender", printObject = true)
public final class ConsoleLogAppender extends AbstractAppender {
    private final WebHookManager webHookManager;

    @Inject
    public ConsoleLogAppender(final WebHookManager webHookManager) {
        super("VConsoleLinker", null, PatternLayout.newBuilder().build(), true, Property.EMPTY_ARRAY);
        this.webHookManager = webHookManager;
        this.start();
    }

    @Override
    public void append(final LogEvent event) {
        final String message = event.getMessage().getFormattedMessage();
        if (message == null) {
            return;
        }
        this.webHookManager.logMessage(message);
    }
}
