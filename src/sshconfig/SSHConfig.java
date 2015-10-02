package sshconfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author saksham.ghimire
 */
public class SSHConfig {
    
    final String userHome = System.getProperty("user.home");
    final String sshDir = ".ssh";
    final String configFile = "config";
    final String sshConfigFile_ = new File(userHome, sshDir).toString();
    final String sshConfigFile = new File(sshConfigFile_, configFile).toString();

    public void print(Object o){
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
    
    public void readFileIntoList() throws IOException{
        FileReader reader = null;
        try {
            reader = new FileReader(new File(sshConfigFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSHConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader fileReader = new BufferedReader(reader);
        List<String> whiteBoard = new ArrayList<String>();
        while (fileReader.readLine()!= null){
//            System.out.println(fileReader.readLine());
            whiteBoard.add(fileReader.readLine());
            
            
        }
        print(whiteBoard);
    }
    
    public void loadFileFromListIntoAMap(){
        
    }

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        SSHConfig starter = new SSHConfig();
        starter.checkSSHConfig();
        starter.readFileIntoList();
        starter.loadFileFromListIntoAMap();
    }

}
