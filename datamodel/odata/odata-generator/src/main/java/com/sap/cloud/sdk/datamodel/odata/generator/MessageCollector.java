/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Wrapper for the {@code LoggerFactory}, collecting all logged messages.
 */
public class MessageCollector
{
    /**
     * The collection storing the log messages of all created loggers. Shared between all logger, so not thread safe!
     */
    private static final MessageCollectorCollection collection = new MessageCollectorCollection();

    /**
     * Static builder for the wrapped logger.
     *
     * @param cls
     *            The class to build the logger for.
     * @return A logger statically collecting all logged messages in the {@code MessageCollector}.
     */
    @Nonnull
    public static Logger getLogger( @Nonnull final Class<?> cls )
    {
        return new MessageCollectorLogger(cls, collection);
    }

    /**
     * Getter for all error messages collected over all logger.
     *
     * @return A list containing all logged error messages.
     */
    @Nonnull
    public static List<String> getErrorMessages()
    {
        return collection.getMessages(LogLevel.ERROR);
    }

    /**
     * Getter for all warning messages collected over all logger.
     *
     * @return A list containing all logged warning messages.
     */
    @Nonnull
    public static List<String> getWarningMessages()
    {
        return collection.getMessages(LogLevel.WARN);
    }

    private enum LogLevel
    {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    private static final class LoggerContent
    {
        private final String message;
        private final Object[] arguments;

        private LoggerContent( final String message )
        {
            this(message, (Object[]) null);
        }

        private LoggerContent( final String message, final Object... arguments )
        {
            this.message = message;
            this.arguments = arguments;
        }

        private String getFormattedMessage()
        {
            if( arguments != null ) {
                return String.format(message, arguments);
            } else {
                return message;
            }
        }
    }

    private static final class MessageCollectorCollection
    {
        private final Map<LogLevel, Map<Class<?>, List<LoggerContent>>> collector = new EnumMap<>(LogLevel.class);

        void collectLogMessage( final LogLevel logLevel, final Class<?> cls, final LoggerContent content )
        {
            final Map<Class<?>, List<LoggerContent>> levelEntries = getOrDefault(collector, logLevel, new HashMap<>());

            final List<LoggerContent> classEntries = getOrDefault(levelEntries, cls, new ArrayList<>());

            classEntries.add(content);
            levelEntries.put(cls, classEntries);
            collector.put(logLevel, levelEntries);
        }

        List<String> getMessages( final LogLevel level )
        {
            final Map<Class<?>, List<LoggerContent>> levelEntries =
                getOrDefault(collector, level, Collections.emptyMap());

            final List<String> allMessages = new ArrayList<>();
            for( final List<LoggerContent> contents : levelEntries.values() ) {
                for( final LoggerContent content : contents ) {
                    allMessages.add(content.getFormattedMessage());
                }
            }

            return allMessages;
        }

        static <K, V> V getOrDefault( final Map<K, V> map, final K key, final V defaultValue )
        {
            return map.getOrDefault(key, defaultValue);
        }
    }

    @SuppressWarnings( "PMD.NullAnnotationMissingOnPublicMethodParameter" )
    private static final class MessageCollectorLogger implements Logger
    {
        private final Logger baseLogger;
        private final Class<?> cls;
        private final MessageCollectorCollection collection;

        private MessageCollectorLogger( final Class<?> cls, final MessageCollectorCollection collection )
        {
            baseLogger = LoggerFactory.getLogger(cls);
            this.cls = cls;
            this.collection = collection;
        }

        @Nonnull
        @Override
        public String getName()
        {
            return baseLogger.getName();
        }

        @Override
        public boolean isTraceEnabled()
        {
            return baseLogger.isTraceEnabled();
        }

        @Override
        public void trace( final String msg )
        {
            baseLogger.trace(msg);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(msg));

        }

        @Override
        public void trace( final String format, final Object arg )
        {
            baseLogger.trace(format, arg);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(format, arg));
        }

        @Override
        public void trace( final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.trace(format, arg1, arg2);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void trace( final String format, final Object... arguments )
        {
            baseLogger.trace(format, arguments);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void trace( final String msg, final Throwable t )
        {
            baseLogger.trace(msg, t);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(msg));
        }

        @Override
        public boolean isTraceEnabled( final Marker marker )
        {
            return baseLogger.isTraceEnabled(marker);
        }

        @Override
        public void trace( final Marker marker, final String msg )
        {
            baseLogger.trace(marker, msg);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(msg));
        }

        @Override
        public void trace( final Marker marker, final String format, final Object arg )
        {
            baseLogger.trace(marker, format, arg);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(format, arg));
        }

        @Override
        public void trace( final Marker marker, final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.trace(marker, format, arg1, arg2);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void trace( final Marker marker, final String format, final Object... argArray )
        {
            baseLogger.trace(marker, format, argArray);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(format, argArray));
        }

        @Override
        public void trace( final Marker marker, final String msg, final Throwable t )
        {
            baseLogger.trace(marker, msg, t);
            collection.collectLogMessage(LogLevel.TRACE, cls, new LoggerContent(msg));
        }

        @Override
        public boolean isDebugEnabled()
        {
            return baseLogger.isDebugEnabled();
        }

        @Override
        public void debug( final String msg )
        {
            baseLogger.debug(msg);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(msg));
        }

        @Override
        public void debug( final String format, final Object arg )
        {
            baseLogger.debug(format, arg);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(format, arg));
        }

        @Override
        public void debug( final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.debug(format, arg1, arg2);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void debug( final String format, final Object... arguments )
        {
            baseLogger.debug(format, arguments);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void debug( final String msg, final Throwable t )
        {
            baseLogger.debug(msg, t);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(msg));
        }

        @Override
        public boolean isDebugEnabled( final Marker marker )
        {
            return baseLogger.isDebugEnabled(marker);
        }

        @Override
        public void debug( final Marker marker, final String msg )
        {
            baseLogger.debug(marker, msg);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(msg));
        }

        @Override
        public void debug( final Marker marker, final String format, final Object arg )
        {
            baseLogger.debug(marker, format, arg);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(format, arg));
        }

        @Override
        public void debug( final Marker marker, final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.debug(marker, format, arg1, arg2);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void debug( final Marker marker, final String format, final Object... arguments )
        {
            baseLogger.debug(marker, format, arguments);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void debug( final Marker marker, final String msg, final Throwable t )
        {
            baseLogger.debug(marker, msg, t);
            collection.collectLogMessage(LogLevel.DEBUG, cls, new LoggerContent(msg));
        }

        @Override
        public boolean isInfoEnabled()
        {
            return baseLogger.isInfoEnabled();
        }

        @Override
        public void info( final String msg )
        {
            baseLogger.info(msg);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(msg));
        }

        @Override
        public void info( final String format, final Object arg )
        {
            baseLogger.info(format, arg);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(format, arg));
        }

        @Override
        public void info( final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.info(format, arg1, arg2);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void info( final String format, final Object... arguments )
        {
            baseLogger.info(format, arguments);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void info( final String msg, final Throwable t )
        {
            baseLogger.info(msg, t);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(msg));
        }

        @Override
        public boolean isInfoEnabled( final Marker marker )
        {
            return baseLogger.isInfoEnabled(marker);
        }

        @Override
        public void info( final Marker marker, final String msg )
        {
            baseLogger.info(marker, msg);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(msg));
        }

        @Override
        public void info( final Marker marker, final String format, final Object arg )
        {
            baseLogger.info(marker, format, arg);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(format, arg));
        }

        @Override
        public void info( final Marker marker, final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.info(marker, format, arg1, arg2);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void info( final Marker marker, final String format, final Object... arguments )
        {
            baseLogger.info(marker, format, arguments);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void info( final Marker marker, final String msg, final Throwable t )
        {
            baseLogger.info(marker, msg, t);
            collection.collectLogMessage(LogLevel.INFO, cls, new LoggerContent(msg, t));
        }

        @Override
        public boolean isWarnEnabled()
        {
            return baseLogger.isWarnEnabled();
        }

        @Override
        public void warn( final String msg )
        {
            baseLogger.warn(msg);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(msg));
        }

        @Override
        public void warn( final String format, final Object arg )
        {
            baseLogger.warn(format, arg);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(format, arg));
        }

        @Override
        public void warn( final String format, final Object... arguments )
        {
            baseLogger.warn(format, arguments);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void warn( final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.warn(format, arg1, arg2);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void warn( final String msg, final Throwable t )
        {
            baseLogger.warn(msg, t);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(msg, t));
        }

        @Override
        public boolean isWarnEnabled( final Marker marker )
        {
            return baseLogger.isWarnEnabled(marker);
        }

        @Override
        public void warn( final Marker marker, final String msg )
        {
            baseLogger.warn(marker, msg);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(msg));
        }

        @Override
        public void warn( final Marker marker, final String format, final Object arg )
        {
            baseLogger.warn(marker, format, arg);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(format, arg));
        }

        @Override
        public void warn( final Marker marker, final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.warn(marker, format, arg1, arg2);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void warn( final Marker marker, final String format, final Object... arguments )
        {
            baseLogger.warn(marker, format, arguments);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void warn( final Marker marker, final String msg, final Throwable t )
        {
            baseLogger.warn(marker, msg, t);
            collection.collectLogMessage(LogLevel.WARN, cls, new LoggerContent(msg));
        }

        @Override
        public boolean isErrorEnabled()
        {
            return baseLogger.isErrorEnabled();
        }

        @Override
        public void error( final String msg )
        {
            baseLogger.error(msg);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(msg));
        }

        @Override
        public void error( final String format, final Object arg )
        {
            baseLogger.error(format, arg);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(format, arg));
        }

        @Override
        public void error( final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.error(format, arg1, arg2);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void error( final String format, final Object... arguments )
        {
            baseLogger.error(format, arguments);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void error( final String msg, final Throwable t )
        {
            baseLogger.error(msg, t);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(msg, t));
        }

        @Override
        public boolean isErrorEnabled( final Marker marker )
        {
            return baseLogger.isErrorEnabled(marker);
        }

        @Override
        public void error( final Marker marker, final String msg )
        {
            baseLogger.error(marker, msg);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(msg));
        }

        @Override
        public void error( final Marker marker, final String format, final Object arg )
        {
            baseLogger.error(marker, format, arg);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(format, arg));
        }

        @Override
        public void error( final Marker marker, final String format, final Object arg1, final Object arg2 )
        {
            baseLogger.error(marker, format, arg1, arg2);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(format, arg1, arg2));
        }

        @Override
        public void error( final Marker marker, final String format, final Object... arguments )
        {
            baseLogger.error(marker, format, arguments);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(format, arguments));
        }

        @Override
        public void error( final Marker marker, final String msg, final Throwable t )
        {
            baseLogger.error(marker, msg, t);
            collection.collectLogMessage(LogLevel.ERROR, cls, new LoggerContent(msg));
        }
    }
}
