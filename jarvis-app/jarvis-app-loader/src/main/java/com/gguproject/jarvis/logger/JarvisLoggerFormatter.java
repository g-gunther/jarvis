package com.gguproject.jarvis.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class JarvisLoggerFormatter extends Formatter {

    // format string for printing the log record
    private static final String format = "%1$tT.%1$tL [%2$s] [%3$s] %4$s %5$s - %6$s %7$s%n";
    private final Date dat = new Date();

    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
        dat.setTime(record.getMillis());
        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
        } else {
            source = "undefined";
        }
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(format,
                             dat,
                             Thread.currentThread().getName(),
                             "app",
                             record.getLevel().getName(),
                             source,
                             message,
                             throwable);
    }
}
