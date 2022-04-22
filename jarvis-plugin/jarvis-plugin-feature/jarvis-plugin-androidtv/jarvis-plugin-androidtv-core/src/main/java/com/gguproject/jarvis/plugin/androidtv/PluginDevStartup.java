package com.gguproject.jarvis.plugin.androidtv;

import com.gguproject.jarvis.plugin.androidtv.encoder.Key;
import com.gguproject.jarvis.plugin.androidtv.service.AndroidTvRemoteControlService;
import com.gguproject.jarvis.plugin.androidtv.util.AndroidTvException;
import com.gguproject.jarvis.plugin.core.dev.AbstractPluginDevStartup;

import javax.inject.Named;

@Named
public class PluginDevStartup extends AbstractPluginDevStartup {

    public static void main(String[] args) {
        init("jarvis-plugin-androidtv");
    }

    private final AndroidTvRemoteControlService androidTvRemoteControlService;

    public PluginDevStartup(AndroidTvRemoteControlService androidTvRemoteControlService){
        this.androidTvRemoteControlService = androidTvRemoteControlService;
    }

    protected void process() {
        try {
            this.androidTvRemoteControlService.send(Key.Code.KEYCODE_POWER);
            Thread.sleep(15000); // wait until the box is started
        } catch (AndroidTvException | InterruptedException e) {
            e.printStackTrace();
        }

        //service.send(Key.Code.KEYCODE_HOME);
        //Thread.sleep(500);
        //service.send(Key.Code.KEYCODE_DPAD_DOWN);
        //Thread.sleep(500);
        //service.send(Key.Code.KEYCODE_ENTER);

		/*InputStreamReader in = new InputStreamReader(System.in);
		String command;
		while((command = new BufferedReader(in).readLine()) != null) {
			service.sendChannel(Integer.valueOf(command));
		}*/
    }
}
