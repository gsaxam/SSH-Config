package sshconfig;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author saksham.ghimire
 */
public class SSHConfig {

    final String userHome = System.getProperty("user.home");
    final String sshDir = ".ssh";
    final String configFile = "config";
    final String sshConfigFile_ = new File(userHome, sshDir).toString();
    final String sshConfigFile = new File(sshConfigFile_, configFile).toString();

    public void print(Object o) {
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

    public void loadFileFromListIntoAMap(List<String> contents) {

    }

    public String getHostName(String str) {
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
                case "port":
                    properties.put("Port", fields[1].trim());
                case "user":
                    properties.put("User", fields[1].trim());
                case "localforward":
                    properties.put(fields[1].trim(), new PortForwarding("LocalForward", fields[1].trim(),fields[1].trim()));
                    System.out.println(new PortForwarding("LocalForward", fields[1].trim(),fields[1].trim()).toString());
            }
        }
        return properties;
    }

    public class PortForwarding {
        private final String forwardType;
        private final String localPort;
        private final String destinationAddress;

        public PortForwarding(String forwardType, String localPort, String destinationAddress) {
            this.forwardType = forwardType;
            this.localPort = localPort;
            this.destinationAddress = destinationAddress;
        }

    }

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        SSHConfig starter = new SSHConfig();
        starter.checkSSHConfig();
        String[] contents = starter.readFileIntoList();
        String fullString = "";
        for (String str : contents) {
            fullString += str + "\n";
        }

        String pattern = "(^(?!\\s).*?\\n(?:\\s.*?(?:\\n|$))*)";
        Pattern r = Pattern.compile(pattern, Pattern.MULTILINE);
        Matcher m = r.matcher(fullString);

        Map<String, Map<String, Object>> hostWithSections = new HashMap<>();
        while (m.find()) {
            if (starter.getHostName(m.group(0)) != null) {
                hostWithSections.put(starter.getHostName(m.group(0)), starter.getHostProperties(m.group(0)));
            }
        }
        starter.print(hostWithSections);
    }

}

