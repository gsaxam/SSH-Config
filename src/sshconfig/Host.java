package SSHConfig;

import java.util.Map;

/**
 * Created by saksham.ghimire on 7/22/16.
 */
public class Host {
    private String host;
    private String hostname;
    private int port;
    private String user;
    private Map localForward;
    private Map remoteForward;

    public Host(String host, String hostname, int port, String user, Map localForward, Map remoteForward) {
        this.host = host;
        this.hostname = hostname;
        this.port = port;
        this.user = user;
        this.localForward = localForward;
        this.remoteForward = remoteForward;
    }

    public String getHostAlias() {
        return this.host;
    }

    public String getHostName() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public String getUser() {
        return this.user;
    }

    public Map getLocalForward() {
        return this.localForward;
    }

    public Map getRemoteForward() {
        return this.remoteForward;
    }

    public void setHostName(String hostname) {
        this.hostname = hostname;
    }

    public void setHostAlias(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;

    }

    public void addLocalForward(int localPort, String destinationAddress) {
        this.localForward.put(localPort, destinationAddress);
    }

    public void addRemoteForward(int remotePort, String destinationAddress) {
        this.remoteForward.put(remotePort, destinationAddress);
    }

}
