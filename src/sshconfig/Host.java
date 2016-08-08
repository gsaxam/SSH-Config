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
    private String identityFile;
    private String forwardAgent;
    private String forwardX11;

    public Host(String host, String hostname, int port, String user, Map localForward, Map remoteForward, String identityFile, String forwardAgent, String forwardX11) {
        this.host = host;
        this.hostname = hostname;
        this.port = port;
        this.user = user;
        this.localForward = localForward;
        this.remoteForward = remoteForward;
        this.identityFile = identityFile;
        this.forwardAgent = forwardAgent;
        this.forwardX11 = forwardX11;
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

    public String getIdentityFile() {
        return this.identityFile;
    }

    public String getForwardAgent() {
        return this.forwardAgent;
    }

    public String getForwardX11() {
        return this.forwardX11;
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

    public void setIdentityFile(String identityFile) {
        this.identityFile = identityFile;
    }

    public void setForwardAgent(String forwardAgent) {
        this.forwardAgent = forwardAgent;
    }

    public void setForwardX11(String forwardX11) {
        this.forwardX11 = forwardX11;
    }

    public void addLocalForward(Map localForward) {
        this.localForward = localForward;
    }

    public void addRemoteForward(Map remoteForward) {
        this.remoteForward = remoteForward;
    }


}
