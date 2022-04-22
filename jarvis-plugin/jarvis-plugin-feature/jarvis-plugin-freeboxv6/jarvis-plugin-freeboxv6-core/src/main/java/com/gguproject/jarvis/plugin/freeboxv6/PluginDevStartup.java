package com.gguproject.jarvis.plugin.freeboxv6;

import com.gguproject.jarvis.plugin.core.dev.AbstractPluginDevStartup;
import com.gguproject.jarvis.plugin.freeboxv6.service.FreeboxRemoteException;
import com.gguproject.jarvis.plugin.freeboxv6.service.FreeboxRemoteService;

import javax.inject.Named;

@Named
public class PluginDevStartup extends AbstractPluginDevStartup {

    public static void main(String[] args) {
        init("jarvis-plugin-freeboxv6");
    }

    private final FreeboxRemoteService freeboxRemoteService;

    public PluginDevStartup(FreeboxRemoteService freeboxRemoteService){
        this.freeboxRemoteService = freeboxRemoteService;
    }

    protected void process() {
        try {
            //service.power();
            this.freeboxRemoteService.playPause();
            //service.netflix();
        } catch (FreeboxRemoteException e) {
            e.printStackTrace();
        }

		/*FreeboxServerService service = PluginApplicationContextAware.getApplicationContext().getBean(FreeboxServerService.class);
		try {
			System.out.println(service.listPlayers());
			System.out.println(service.playerStatus("1"));
		} catch (FreeboxServerException | HttpRequestException e) {
			e.printStackTrace();
		}*/
    }
}
