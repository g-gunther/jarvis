package com.gguproject.jarvis.plugin.command.analyser;

import com.gguproject.jarvis.plugin.freeboxv6.event.InfraRedPluginConfiguration;
import com.gguproject.jarvis.plugin.freeboxv6.event.InfraRedPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.CommandAnalyser;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor.CommandProcessor;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandAnalyserTest {

    private CommandAnalyser analyser;

    public CommandAnalyserTest() throws IllegalArgumentException, IllegalAccessException {
        // get the command description file from test resources
        File commandFile = new File(this.getClass().getClassLoader().getResource("command.json").getFile());

        // mock configuration class
        InfraRedPluginConfiguration configuration = Mockito.mock(InfraRedPluginConfiguration.class);
        Mockito.when(configuration.getProperty(PropertyKey.configurationFile)).thenReturn("command.json");
        Mockito.when(configuration.getConfigurationFile("command.json")).thenReturn(Optional.of(commandFile));

        // build the service to test
        this.analyser = new CommandAnalyser(configuration);

        this.analyser.loadConfiguration();
    }

    @Test
    public void test() {
        this.doTest("TV", "POWER", "irsend SEND_ONCE sound KEY_TV");
    }

    private void doTest(String context, String action, String command) {
        List<CommandProcessor> outputCommands = this.analyser.analyse(Arrays.asList(new InfraredCommand(context, action)));
    }
}
