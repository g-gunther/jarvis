package com.gguproject.jarvis.plugin.spotify.service;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.spotify.service.dto.CurrentDeviceContext;
import com.gguproject.jarvis.plugin.spotify.service.dto.DeviceDto;
import com.gguproject.jarvis.plugin.spotify.service.dto.PlayingType;

import javax.inject.Named;
import java.util.List;

@Named
public class SpotifyServiceOrchestrator {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpotifyServiceOrchestrator.class);

    private final SpotifyService spotifyService;

    private final DeviceConfigurationService deviceConfigurationService;

    public SpotifyServiceOrchestrator(SpotifyService spotifyService, DeviceConfigurationService deviceConfigurationService){
        this.spotifyService = spotifyService;
        this.deviceConfigurationService = deviceConfigurationService;
    }

    public void play(){
        DeviceDto targetDevice = this.deviceConfigurationService.getDevice();
        if(targetDevice == null){
            LOGGER.error("Could not start music - the device is not registered");
            return;
        }

        CurrentDeviceContext currentContext = this.spotifyService.getCurrentDevice();

        if(currentContext.hasCurrentContext() && currentContext.getId().equals(targetDevice.getId())){
            LOGGER.debug("The targeted device is already the active one: {} - {} with playing state: {}", currentContext.getName(), currentContext.getId(), currentContext.isPlaying());
            if(currentContext.isPlaying()){
                LOGGER.debug("Nothing to do, the device is already playing music");
            } else {
                LOGGER.debug("Start music on device");
                this.spotifyService.startMusicOnDevice(targetDevice.getId());
            }
        } else {
            List<DeviceDto> existingDevices = this.spotifyService.listDevices();
            boolean targetDeviceExists = existingDevices.stream().anyMatch(d -> d.getId().equals(targetDevice.getId()));
            if(targetDeviceExists){
                if(currentContext.isPlaying()) {
                    LOGGER.debug("Transfert music to the targeted device");
                    this.spotifyService.transfertToDevice(targetDevice.getId());
                } else {
                    LOGGER.debug("Start music on device");
                    this.spotifyService.startMusicOnDevice(targetDevice.getId());
                }
            } else {
                LOGGER.warn("The targeted device {} - {} does not exist in the list of active devices", targetDevice.getName(), targetDevice.getId());
            }
        }
    }

    public void playPlaylist(String playlistId){
        DeviceDto targetDevice = this.deviceConfigurationService.getDevice();
        if(targetDevice == null){
            LOGGER.error("Could not start music - the device is not registered");
            return;
        }

        CurrentDeviceContext currentContext = this.spotifyService.getCurrentDevice();

        if(currentContext.hasCurrentContext() && currentContext.getId().equals(targetDevice.getId())){
            LOGGER.debug("The targeted device is already the active one: {} - {} with playing state: {} - {}", currentContext.getName(), currentContext.getId(), currentContext.isPlaying(), currentContext.getPlayingUri());
            if(currentContext.isPlaying()){
                if(currentContext.getPlayingType() == PlayingType.PLAYLIST){
                    String currentPlalistId = PlayingType.PLAYLIST.findIdFromUri(currentContext.getPlayingUri());
                    if(currentPlalistId.equals(playlistId)){
                        LOGGER.debug("Nothing to do, the device is already playing the targeted playlist");
                    } else {
                        LOGGER.debug("The device is already playing a playlist, switch to targeted playlist");
                        this.spotifyService.startPlaylistOnDevice(targetDevice.getId(), playlistId);
                    }
                } else {
                    LOGGER.debug("The device is already playing music, switch to targeted playlist");
                    this.spotifyService.startPlaylistOnDevice(targetDevice.getId(), playlistId);
                }
            } else {
                LOGGER.debug("Start targeted playlist on device");
                this.spotifyService.startPlaylistOnDevice(targetDevice.getId(), playlistId);
            }
        } else {
            List<DeviceDto> existingDevices = this.spotifyService.listDevices();
            boolean targetDeviceExists = existingDevices.stream().anyMatch(d -> d.getId().equals(targetDevice.getId()));
            if(targetDeviceExists){
                if(currentContext.isPlaying()) {
                    if(currentContext.getPlayingType() == PlayingType.PLAYLIST){
                        String currentPlalistId = PlayingType.PLAYLIST.findIdFromUri(currentContext.getPlayingUri());
                        if(currentPlalistId.equals(playlistId)){
                            LOGGER.debug("Transfert music to the targeted device");
                            this.spotifyService.transfertToDevice(targetDevice.getId());
                        } else {
                            LOGGER.debug("Transfert music to the targeted device on the given playlist");
                            this.spotifyService.startPlaylistOnDevice(targetDevice.getId(), playlistId);
                        }
                    } else {
                        LOGGER.debug("Transfert music to the targeted device on the given playlist");
                        this.spotifyService.startPlaylistOnDevice(targetDevice.getId(), playlistId);
                    }
                } else {
                    LOGGER.debug("Start music on device");
                    this.spotifyService.startPlaylistOnDevice(targetDevice.getId(), playlistId);
                }
            } else {
                LOGGER.warn("The targeted device {} - {} does not exist in the list of active devices", targetDevice.getName(), targetDevice.getId());
            }
        }
    }
}
