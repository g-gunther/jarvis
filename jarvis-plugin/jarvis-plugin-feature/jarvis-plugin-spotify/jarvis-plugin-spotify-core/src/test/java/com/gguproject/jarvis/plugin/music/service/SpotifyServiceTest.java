package com.gguproject.jarvis.plugin.music.service;

import com.gguproject.jarvis.core.ioc.utils.ReflectionUtils;
import com.gguproject.jarvis.plugin.spotify.SpotifyPluginConfiguration;
import com.gguproject.jarvis.plugin.spotify.SpotifyPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.spotify.service.DeviceConfigurationService;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyService;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyServiceOrchestrator;
import com.gguproject.jarvis.plugin.spotify.service.dto.CurrentDeviceContext;
import com.gguproject.jarvis.plugin.spotify.service.dto.DeviceDto;
import com.gguproject.jarvis.plugin.spotify.service.dto.PlaylistDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Disabled
public class SpotifyServiceTest {
    private enum Playlist {
        FOURRE_TOUT("70hhQbCyu66V4ztPHV7QiE"),
        EN_BOUCLE("37i9dQZF1EpnEw10CBWory"),
        LEGENDE_DU_ROCK("37i9dQZF1DWXTHBOfJ8aI7");

        private String id;

        Playlist(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    private enum Device {
        PIXEL4A("bf90348ba0698233c06fdb7b45c9a344e9566b50"),
        MAC("a4f38681ccbf9a852cf15c7c0db47a722b9db3aa"),
        RASPOTIFY("98bb0735e28656bac098d927d410c3138a4b5bca");

        private String id;

        Device(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    private SpotifyService service;

    private SpotifyServiceOrchestrator orchestrator;

    @BeforeEach
    public void initTest() throws IllegalArgumentException, IllegalAccessException, IOException {
        File configurationFile = new File(this.getClass().getClassLoader().getResource("configuration.test.properties").getFile());
        Properties prop = new Properties();
        prop.load(new FileInputStream(configurationFile));

        SpotifyPluginConfiguration configuration = Mockito.mock(SpotifyPluginConfiguration.class);
        Mockito.when(configuration.getProperty(PropertyKey.spotifyClientId)).thenReturn(prop.getProperty(PropertyKey.spotifyClientId));
        Mockito.when(configuration.getProperty(PropertyKey.spotifyClientSecret)).thenReturn(prop.getProperty(PropertyKey.spotifyClientSecret));
        Mockito.when(configuration.getProperty(PropertyKey.spotifyApiRefreshToken)).thenReturn(prop.getProperty(PropertyKey.spotifyApiRefreshToken));

        this.service = new SpotifyService(configuration);

        DeviceConfigurationService deviceConfiguration = new DeviceConfigurationService(configuration);
        ReflectionUtils.setFieldByType(deviceConfiguration, DeviceDto.class,
                new DeviceDto(new com.wrapper.spotify.model_objects.miscellaneous.Device.Builder().setId(Device.RASPOTIFY.id).setName("raspotify").build()));

        this.orchestrator = new SpotifyServiceOrchestrator(this.service, deviceConfiguration);

        this.service.postConstruct();
    }

    @Test
    public void testPlayOrchestrator() {
        this.orchestrator.play();
    }

    @Test
    public void testPlaylistOrchestrator() {
        this.orchestrator.playPlaylist(Playlist.FOURRE_TOUT.getId());
    }

    @Test
    public void testLoadPlaylist() {
        List<PlaylistDto> playlists = this.service.getCurrentUserPlaylists();
        for (PlaylistDto playlist : playlists) {
            System.out.println(playlist.getName() + " " + playlist.getId());
        }
    }

    @Test
    public void testLoadDevices() {
        List<DeviceDto> devices = this.service.listDevices();
        for (DeviceDto d : devices) {
            System.out.println(d.getName() + " " + d.getId());
        }
    }

    @Test
    public void testPause() {
        this.service.pause();
    }

    @Test
    public void testCurrentDevice() {
        CurrentDeviceContext context = this.service.getCurrentDevice();
        System.out.println(context);
    }

    @Test
    public void testPlayOnDevice() {
        this.service.startMusicOnDevice(Device.MAC.id);
    }

    @Test
    public void testTransfert() {
        this.service.transfertToDevice(Device.MAC.getId());
    }
}
