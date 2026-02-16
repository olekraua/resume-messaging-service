package net.devstudy.resume.ms.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging.realtime.broker-relay")
public class MessagingBrokerRelayProperties {

    private String host = "localhost";
    private int port = 61613;
    private String clientLogin = "guest";
    private String clientPasscode = "guest";
    private String systemLogin = "guest";
    private String systemPasscode = "guest";
    private String virtualHost = "/";
    private long systemHeartbeatSendIntervalMs = 10000L;
    private long systemHeartbeatReceiveIntervalMs = 10000L;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClientLogin() {
        return clientLogin;
    }

    public void setClientLogin(String clientLogin) {
        this.clientLogin = clientLogin;
    }

    public String getClientPasscode() {
        return clientPasscode;
    }

    public void setClientPasscode(String clientPasscode) {
        this.clientPasscode = clientPasscode;
    }

    public String getSystemLogin() {
        return systemLogin;
    }

    public void setSystemLogin(String systemLogin) {
        this.systemLogin = systemLogin;
    }

    public String getSystemPasscode() {
        return systemPasscode;
    }

    public void setSystemPasscode(String systemPasscode) {
        this.systemPasscode = systemPasscode;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public long getSystemHeartbeatSendIntervalMs() {
        return systemHeartbeatSendIntervalMs;
    }

    public void setSystemHeartbeatSendIntervalMs(long systemHeartbeatSendIntervalMs) {
        this.systemHeartbeatSendIntervalMs = systemHeartbeatSendIntervalMs;
    }

    public long getSystemHeartbeatReceiveIntervalMs() {
        return systemHeartbeatReceiveIntervalMs;
    }

    public void setSystemHeartbeatReceiveIntervalMs(long systemHeartbeatReceiveIntervalMs) {
        this.systemHeartbeatReceiveIntervalMs = systemHeartbeatReceiveIntervalMs;
    }
}
