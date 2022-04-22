package com.gguproject.jarvis.plugin.loader.command.utils;

import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarReport;

/**
 * Output Utils
 */
public class CommandOutputUtils {

    /**
     * generate {@link CommandOutput} from a {@link PluginJarReport}
     *
     * @param report Report
     * @return Command output
     */
    public static CommandOutput build(PluginJarReport report) {
        CommandOutputBuilder output = CommandOutputBuilder.build();
        for (PluginJarReport.Counter counter : PluginJarReport.Counter.values()) {
            if (report.getCounter(counter) > 0) {
                output.newLine().append("Number of {0}: {1}", counter.getName(), report.getCounter(counter));
            }
        }

        if (report.hasErrors()) {
            output.newLine()
                    .append("With errors: ")
                    .newLine();
            report.getErrors().forEach(e -> output.append(e).newLine());
        }
        return output.get();
    }
}
