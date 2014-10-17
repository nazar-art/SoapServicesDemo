package core.logger;

import org.apache.log4j.LogManager;

import java.text.MessageFormat;

public class Logger {
    /**
     * prefixes for logging operations
     */
    public static final String PREFIX_OPERATION = "OPERATION";
    public static final String PREFIX_METHOD_STARTED = "METHOD STARTED";
    public static final String PREFIX_METHOD_SUCCESS = "METHOD SUCCESS";
    public static final String PREFIX_METHOD_FAILED = "METHOD FAILED";
    public static final String PREFIX_METHOD_SKIPPED = "METHOD SKIPPED";
    public static final String PREFIX_CONFIGURATION_STARTED = "CONFIGURATION STARTED";
    public static final String PREFIX_CONFIGURATION_SUCCESS = "CONFIGURATION SUCCESS";
    public static final String PREFIX_CONFIGURATION_FAILED = "CONFIGURATION FAILED";
    public static final String PREFIX_CONFIGURATION_SKIPPED = "CONFIGURATION SKIPPED";
    public static final String PREFIX_TEST_STARTED = "TEST STARTED";
    public static final String PREFIX_TEST_FINISHED = "TEST FINISHED";
    public static final String PREFIX_TEST_SUITE_STARTED = "TEST SUITE STARTED";
    public static final String PREFIX_TEST_SUITE_FINISHED = "TEST SUITE FINISHED";

    /**
     * Logger
     */
    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger("SoapServicesLogger");

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void info(String msg, Object... formatParams) {
        LOGGER.info(MessageFormat.format(msg, formatParams));
    }

    public static void info(String msg, Throwable error) {
        LOGGER.info(msg, error);
    }

    public static void info(String msg, Throwable error, Object... formatParams) {
        LOGGER.info(MessageFormat.format(msg, formatParams), error);
    }

    public static void info(Object msg) {
        LOGGER.info(msg);
    }

    public static void operation(String msg) {
        LOGGER.info(buildOperationMessage(msg));
    }

    public static void operation(String msg, Object... formatParams) {
        LOGGER.info(buildOperationMessage(MessageFormat.format(msg, formatParams)));
    }

    public static void operation(String msg, Throwable error) {
        LOGGER.info(buildOperationMessage(msg), error);
    }

    public static void operation(String msg, Throwable error, Object... formatParams) {
        LOGGER.info(buildOperationMessage(MessageFormat.format(msg, formatParams)), error);
    }

    public static void debug(String msg) {
        LOGGER.debug(msg);
    }

    public static void debug(String msg, Object... formatParams) {
        LOGGER.debug(MessageFormat.format(msg, formatParams));
    }

    public static void debug(String msg, Throwable error) {
        LOGGER.debug(msg, error);
    }

    public static void debug(String msg, Throwable error, Object... formatParams) {
        LOGGER.debug(MessageFormat.format(msg, formatParams), error);
    }

    public static void debug(Object message) {
        LOGGER.debug(message);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

    public static void warn(String msg, Object... formatParams) {
        LOGGER.warn(MessageFormat.format(msg, formatParams));
    }

    public static void warn(String msg, Throwable error) {
        LOGGER.warn(msg, error);
    }

    public static void warn(String msg, Throwable error, Object... formatParams) {
        LOGGER.warn(MessageFormat.format(msg, formatParams), error);
    }

    public static void fatal(String msg) {
        LOGGER.fatal(msg);
    }

    public static void fatal(String msg, Object... formatParams) {
        LOGGER.fatal(MessageFormat.format(msg, formatParams));
    }

    public static void fatal(String msg, Throwable error) {
        LOGGER.fatal(msg, error);
    }

    public static void fatal(String msg, Throwable error, Object... formatParams) {
        LOGGER.fatal(MessageFormat.format(msg, formatParams), error);
    }

    public static void error(String msg) {
        LOGGER.error(msg);
    }

    public static void error(String msg, Object... formatParams) {
        LOGGER.error(MessageFormat.format(msg, formatParams));
    }

    public static void error(String msg, Throwable error) {
        LOGGER.error(msg, error);
    }

    public static void error(String msg, Throwable error, Object... formatParams) {
        LOGGER.error(MessageFormat.format(msg, formatParams), error);
    }

    private static String buildOperationMessage(String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append(PREFIX_OPERATION);
        builder.append(" - ");
        builder.append(msg);
        return builder.toString();
    }
}
