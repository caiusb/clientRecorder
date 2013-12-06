package edu.oregonstate.cope.clientRecorder.util;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class COPELogger {

	private static final String FILE_APPENDER = "fileAppender";

	public void enableFileLogging(String parentDir, String logFileName) {
		setLoggingFile(parentDir + File.separator + logFileName);
	}

	public void disableConsoleLogging() {
		Logger logger = getRootLogger();
		logger.getAppender("console").stop();
	}

	protected void testDisableFileLogging() {
		Logger logger = getRootLogger();
		logger.getAppender(FILE_APPENDER).stop();
		logger.detachAppender(FILE_APPENDER);
	}

	public void logOnlyErrors() {
		Logger logger = getRootLogger();
		logger.setLevel(Level.ERROR);
	}

	public void logEverything() {
		Logger logger = getRootLogger();
		logger.setLevel(Level.ALL);
	}

	public void error(Object obj, String message, Throwable t) {
		getLogger(obj).error(message, t);
	}

	public void error(Object obj, String message) {
		getLogger(obj).error(message, message);

	}

	public void info(Object obj, String message) {
		getLogger(obj).info(message);
	}

	protected Logger getLogger(Object obj) {
		Logger logger = (Logger)LoggerFactory.getLogger(obj.getClass());
		return logger;
	}

	protected Logger getRootLogger() {
		Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		return logger;
	}

	private Logger setLoggingFile(final String file) {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		
		final PatternLayoutEncoder ple = new PatternLayoutEncoder();
		ple.setPattern("%date{dd MMM yyyy;HH:mm:ss} [%thread] %level %logger %msg%n");
		ple.setContext(lc);
		ple.start();
		
		final FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
		fileAppender.setName(FILE_APPENDER);
		fileAppender.setFile(file);
		fileAppender.setEncoder(ple);
		fileAppender.setContext(lc);
		fileAppender.start();

		/*
		 * final Logger logger = (Logger) LoggerFactory.getLogger(string);
		 * logger.addAppender(fileAppender); logger.setLevel(Level.DEBUG);
		 * logger.setAdditive(false); set to true if root should log too
		 */

		Logger logger = getRootLogger();
		logger.addAppender(fileAppender);

		logger.getAppender("console").stop();

		return logger;
	}

	public void warning(Object obj, String msg) {
		getLogger(obj).warn(msg);
	}

}
