package SSHConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author saksham.ghimire
 */
public class SSHConfig {

    final String userHome = System.getProperty("user.home");
    final String sshDir = ".ssh";
    final String configFile = "config";
    final String sshConfigFile_ = new File(userHome, sshDir).toString();
    final String sshConfigFile = new File(sshConfigFile_, configFile).toString();


    public static void print(Object o) {
        System.out.println(o.toString());
    }

    public void checkSSHConfig() throws IOException {

        System.out.println(sshConfigFile);

        File sshFile = new File(sshConfigFile);
        if (sshFile.exists()) {
            System.out.println("The SSH config file already exists.");
        } else {
            PrintWriter writer = new PrintWriter(sshFile, "UTF-8");
            writer.println("# File update by SSHConfig");
            writer.close();
        }
    }

    public String[] readFileIntoList() throws IOException {
        Path filePath = null;
        try {
            filePath = new File(sshConfigFile).toPath();
        } catch (Exception ex) {
            Logger.getLogger(SSHConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        Charset charset = Charset.defaultCharset();
        List<String> stringList = Files.readAllLines(filePath, charset);
        String[] stringArray = stringList.toArray(new String[]{});
        return stringArray;
    }

    public String getHostAlias(String str) {
        String[] hostNameLine = str.split("/n");
        if (!hostNameLine[0].trim().startsWith("#")) {
            String[] hostNameToSplit = hostNameLine[0].trim().split(" ");
            return hostNameToSplit[1].trim();
        } else {
            return null;
        }

    }

    public Map<String, Object> getHostProperties(String str) {
        Map<String, Object> properties = new HashMap<>();
        String[] lines = str.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].trim().split(" ");

            String fieldName = "";
            if (fields.length != 0) {
                fieldName = fields[0].trim();
            }
            switch (fieldName.toLowerCase()) {
                case "hostname":
                    properties.put("HostName", fields[1].trim());
                    break;
                case "port":
                    properties.put("Port", fields[1].trim());
                    break;
                case "user":
                    properties.put("User", fields[1].trim());
                    break;
                case "identityfile":
                    properties.put("IdentityFile", fields[1].trim());
                    break;
                case "forwardagent":
                    properties.put("ForwardAgent", fields[1].trim());
                    break;
                case "forwardx11":
                    properties.put("ForwardX11", fields[1].trim());
                    break;
            }
        }
        return properties;
    }

    public Map<Integer, String> getLocalForwarding(String str) {
        Map<Integer, String> localForwardMap = new HashMap<>();
        String[] lines = str.split("\n");

        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].trim().split(" ");

            String fieldName = "";
            if (fields.length != 0) {
                fieldName = fields[0].trim();
            }
            switch (fieldName.toLowerCase()) {

                case "localforward":
                    localForwardMap.put(Integer.parseInt(fields[1].trim()), fields[2].trim());
                    break;
            }
        }
        return localForwardMap;
    }

    public Map<Integer, String> getRemoteForwarding(String str) {
        Map<Integer, String> remoteForwardMap = new HashMap<>();
        String[] lines = str.split("\n");

        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].trim().split(" ");

            String fieldName = "";
            if (fields.length != 0) {
                fieldName = fields[0].trim();
            }
            switch (fieldName.toLowerCase()) {

                case "remoteforward":
                    remoteForwardMap.put(Integer.parseInt(fields[1].trim()), fields[2].trim());
            }
        }
        return remoteForwardMap;
    }
}

