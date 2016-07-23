package SSHConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saksham.ghimire on 7/22/16.
 */
public class Config {

    List<Host> hostList = new ArrayList<>();

    public Config() {
    }

    public List<Host> getAllHosts() {
        return this.hostList;
    }

    public void addHost(Host host) {
        this.hostList.add(host);
    }

    public void removeHost(Host host) {
        this.hostList.remove(host);
    }

}
