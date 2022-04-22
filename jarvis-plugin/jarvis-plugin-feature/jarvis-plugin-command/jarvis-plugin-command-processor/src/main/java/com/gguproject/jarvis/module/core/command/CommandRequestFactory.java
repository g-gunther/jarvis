package com.gguproject.jarvis.module.core.command;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.module.core.command.support.CommandRequestArgument;

import java.util.*;

public class CommandRequestFactory {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CommandRequestFactory.class);

    /**
     * @param commandToParse Command to process
     */
    public static CommandRequest parse(String commandToParse) {
        String command = commandToParse.trim();
        Map<String, String> arguments = new HashMap<>();

        // split the command using spaces
        Iterator<String> it = Arrays.asList(command.split(" ")).iterator();
        List<String> commandParts = new ArrayList<>();
        CommandRequestArgument argument = new CommandRequestArgument();

        while (it.hasNext()) {
            String part = it.next();

            // arguments with more that the "-" character
            if (part.startsWith("-") && part.length() > 1) {
                // add the previous parsed argument
                if (!argument.isEmpty()) {
                    arguments.put(argument.getName(), argument.getValue());
                }

                String cleanPart = part.substring(1); // remove "-"

                // split to get the key & value of the argument
                if (cleanPart.indexOf("=") >= 0) {
                    String argKey = cleanPart.substring(0, cleanPart.indexOf("="));
                    if (StringUtils.isEmpty(argKey)) {
                        LOGGER.warn("Empty arguments in command {} - skip", command);
                        break;
                    }

                    // create an argument
                    argument.init(argKey, cleanPart.substring(cleanPart.indexOf("=") + 1));
                }
                // there is only a key or the argument
                else {
                    argument.init(cleanPart);
                }
            }
            // else if it's a word, it might be a command unless there are already some arguments
            // then it has to be appended to the last argument value (because it contains space)
            else if (!arguments.isEmpty() || !argument.isEmpty()) {
                argument.append(part);
            }
            // command - will populate the iterator
            else {
                commandParts.add(part);
            }
        }

        if (!argument.isEmpty()) {
            arguments.put(argument.getName(), argument.getValue());
        }

        LOGGER.debug("Parsed command: {} with arguments: {}", commandParts, arguments);

        return new CommandRequest(command, commandParts, arguments);
    }
}
