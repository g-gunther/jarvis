package com.gguproject.jarvis.core.events.tcp;

import com.gguproject.jarvis.core.daemon.DaemonThread;
import com.gguproject.jarvis.core.events.support.SocketUtils;
import com.gguproject.jarvis.core.events.tcp.support.TcpResponseEvent;
import com.gguproject.jarvis.core.events.tcp.support.TcpStatus;
import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP receiver that could be launch in a separate thread
 * This received listens on a specific port defined in the configuration
 *
 * @author GGUNTHER
 */
public abstract class AbstractTcpReceiver extends DaemonThread {
    private static final Logger LOG = AbstractLoggerFactory.getLogger(AbstractTcpReceiver.class);

    private ServerSocket serverSocket;

    private Integer port;

    public AbstractTcpReceiver(String threadName) {
        super(threadName);
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        if (this.port == null) {
            throw TechnicalException.get().message("Can't start thread {0} with null port", this.getName()).build();
        }

        LOG.info("Ready to receive tcp packets on port : {}", this.port);

        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            LOG.error("Error while creating the server socket on port: {}", port);
            LOG.error("Cause", e);
            return;
        }

        while (true) {
            try {
                run0();
            } catch (InterruptedException e) {
                LOG.error("Stop receiving tcp packets (thread has been interrupted)", e);
                return;
            }
        }
    }

    /**
     * Wait for a datagram, parse it as an event and dispatch it to our listeners.
     *
     * @throws InterruptedException If the thread has been interrupted.
     */
    private void run0() throws InterruptedException {
        LOG.debug("Waiting for tcp packets...");

        Socket socket;
        DataOutputStream outToClient;
        try {
            socket = this.serverSocket.accept();
            outToClient = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOG.error("Error while accepting a new socket", e);
            throw new InterruptedException(e.getMessage());
        }

        try {
            byte[] receivedData = SocketUtils.readBytesFromInputStream(socket.getInputStream());

            LOG.debug("Received tcp data: {}", receivedData);
            TcpResponseEvent event = TcpResponseEvent.buildResponseEvent(((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName(), receivedData);
            LOG.debug("Decoded event: {}", event);
            this.onMessage(event);

            outToClient.writeInt(TcpStatus.SUCCESS.getCode());
            outToClient.write(TcpConnectionSender.endPacket);
        } catch (IOException e) {
            try {
                outToClient.writeInt(TcpStatus.SERVER_ERROR.getCode());
            } catch (IOException e1) {
                LOG.error("Error while sending back the TcpStatus SERVER_ERROR", e);
            }
            LOG.error("Error while receiving/sending tcp data", e);
            throw new InterruptedException(e.getMessage());
        }

        // Interrupted?
        if (isInterrupted()) {
            throw new InterruptedException();
        }
    }

    protected abstract void onMessage(TcpResponseEvent event);
}
