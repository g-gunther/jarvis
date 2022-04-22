package com.gguproject.jarvis.plugin.vaccum;

import com.gguproject.jarvis.plugin.core.dev.AbstractPluginDevStartup;
import com.gguproject.jarvis.plugin.vaccum.service.VacuumService;

import javax.inject.Named;

@Named
public class PluginDevStartup extends AbstractPluginDevStartup {

    public static void main(String[] args) {
        init("jarvis-plugin-vacuum");
    }

    private final VacuumService vacuumService;

    public PluginDevStartup(VacuumService vacuumService){
        this.vacuumService = vacuumService;
    }

    protected void process() {
        this.vacuumService.test();
    }
}
