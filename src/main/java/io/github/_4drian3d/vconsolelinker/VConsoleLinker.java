package io.github._4drian3d.vconsolelinker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github._4drian3d.vconsolelinker.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(
	id = "vconsolelinker",
	name = "VConsoleLinker",
	description = "Velocity Console Linker",
	version = Constants.VERSION,
	authors = { "4drian3d" }
)
public final class VConsoleLinker {
	@Inject
	private Injector injector;
	@Inject
	@DataDirectory
	private Path path;
	@Inject
	private org.slf4j.Logger logger;
	
	@Subscribe
	void onProxyInitialization(final ProxyInitializeEvent event) {
		final Configuration configuration;
		try {
			configuration = Configuration.load(path);
			if (configuration.getChannelId().isBlank() || configuration.getToken().isBlank()) {
				logger.error("The plugin has not yet been configured, no function will be enabled");
				return;
			}
		} catch (IOException e) {
			logger.error("An error occurred loading configuration", e);
			return;
		}
		this.injector = injector.createChildInjector(
			binder ->  binder.bind(Configuration.class).toInstance(configuration)
		);
		final Logger rootLogger = (Logger) LogManager.getRootLogger();
		rootLogger.addAppender(injector.getInstance(ConsoleLogAppender.class));
	}
}