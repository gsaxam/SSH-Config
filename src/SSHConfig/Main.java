package SSHConfig;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jdk.management.cmm.SystemResourcePressureMXBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    public static void print(Object o){
        System.out.println(o.toString());
    }
    public static void main(String[] args) {
        launch(args);
    }

    Config allHosts = new Config();

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("SSHUTTLE");
        Button btn = new Button();
        btn.setText("+NEW");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("SSHUTTLE");
            }
        });
        Config hostBoard = new Config();
        hostBoard = readConfig();

        BorderPane border = new BorderPane();
        HBox hbox = addHBox();
        border.setTop(hbox);
        border.setLeft(addVBox());
        addStackPane(hbox);         // Add stack to HBox in top region

        primaryStage.setScene(new Scene(border));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public Config readConfig() throws IOException {
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


        while (m.find()) {
            if (starter.getHostAlias(m.group(0)) != null) {
                Map hostProperties = new HashMap(starter.getHostProperties(m.group(0)));

                allHosts.addHost(new Host(starter.getHostAlias(m.group(0)),
                        hostProperties.get("HostName").toString(),
                        (hostProperties.containsKey("Port")) ? Integer.parseInt(hostProperties.get("Port").toString()) : -1,
                        hostProperties.get("User").toString(),
                        starter.getLocalForwarding(m.group(0)),
                        starter.getRemoteForwarding(m.group(0)))
                );
            }
        }
        return allHosts;
    }

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #44a5b7;");

        Button buttonCurrent = new Button("+ New Host");
        buttonCurrent.setPrefSize(100, 20);

        Button buttonProjected = new Button("Save All");
        buttonProjected.setPrefSize(100, 20);
        hbox.getChildren().addAll(buttonCurrent, buttonProjected);

        return hbox;
    }

    public VBox addVBox() throws IOException {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Text title = new Text("Available Host Details");
        title.setFont(Font.font("Arial", 14));
        vbox.getChildren().add(title);

        final Accordion accordion = new Accordion();
        accordion.setPrefWidth(600);
        TitledPane[] hostPaneList = new TitledPane[allHosts.getNumberOfHosts()];

        int hostCounter=0;

        for (Host h: allHosts.getAllHosts()) {

            GridPane grid = new GridPane();
            grid.setVgap(4); //line spacing between grid elements
            grid.setPadding(new Insets(5, 5, 5, 50)); //last value sets left-indentation
            grid.add(new Label("Host "), 0, 0);
            grid.add(new TextField(h.getHostName()), 1, 0);
            grid.add(new Label("Port "), 0, 1);
            grid.add(new TextField(String.valueOf(h.getPort())), 1, 1);
            grid.add(new Label("User "), 0, 2);
            grid.add(new TextField(h.getUser()), 1, 2);
            Set<Integer> localForwardList = h.getLocalForward().keySet();

            int addRowCount = 3; //the last index used was 2
            for(int key: localForwardList){
                grid.addRow(addRowCount, new Label("Local Forward "));
                grid.add(new TextField(String.valueOf(key)), 1, addRowCount);
                grid.add(new TextField(h.getLocalForward().get(key).toString()), 2, addRowCount);
                addRowCount ++;
            }

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setContent(grid);

            hostPaneList[hostCounter] = new TitledPane(h.getHostAlias(), scrollPane);

            hostPaneList[hostCounter].setText(h.getHostAlias());
            hostPaneList[hostCounter].setContent(scrollPane);
            hostCounter ++;
        }
        accordion.getPanes().addAll(hostPaneList);
        accordion.setPrefHeight(400);
//        accordion.setExpandedPane(hostPaneList[0]);
        vbox.getChildren().add(accordion);

        return vbox;
    }

    public void addStackPane(HBox hb) {
        StackPane stack = new StackPane();
        Rectangle helpIcon = new Rectangle(30.0, 25.0);
        helpIcon.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop[]{
                        new Stop(0, Color.web("#4977A3")),
                        new Stop(0.5, Color.web("#B0C6DA")),
                        new Stop(1, Color.web("#9CB6CF")),}));
        helpIcon.setStroke(Color.web("#D0E6FA"));
        helpIcon.setArcHeight(3.5);
        helpIcon.setArcWidth(3.5);

        Text helpText = new Text("?");
        helpText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        helpText.setFill(Color.WHITE);
        helpText.setStroke(Color.web("#7080A0"));

        stack.getChildren().addAll(helpIcon, helpText);
        stack.setAlignment(Pos.CENTER_RIGHT);     // Right-justify nodes in stack
        StackPane.setMargin(helpText, new Insets(0, 10, 0, 0)); // Center "?"

        hb.getChildren().add(stack);            // Add to HBox from Example 1-2
        HBox.setHgrow(stack, Priority.ALWAYS);    // Give stack any extra space
    }

}