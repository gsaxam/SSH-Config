package SSHConfig;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    Host currentHost = new Host(null, null, 0, null, null, null, null, null, null);

    // declare TextFields to retrieve updated values
    TextField editableHostField = new TextField();
    TextField editablePortField = new TextField();
    TextField editableUserField = new TextField();
    TextField editableIdentityFileField = new TextField();
    TextField editableForwardAgentField = new TextField();
    TextField editableForwardX11Field = new TextField();

    public static void print(Object o) {
        System.out.println(o.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }

    Config allHosts = new Config();
    BorderPane border = new BorderPane();

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


        HBox hbox = addHBox();
        border.setTop(hbox);
        border.setLeft(addLeftNavigation());
        border.setCenter(addShuttleIconAsDefaultView());
        headingBar(hbox);         // Add stack to HBox in top region

        Scene mainScene = new Scene(border, 800, 500);

        File f = new File("src/SShConfig/stylesheet.css");
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        primaryStage.setScene(mainScene);
//        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public VBox addShuttleIconAsDefaultView() {
        VBox imageBox = new VBox();
        ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/bus.png")));
        image.setFitHeight(120);
        image.setFitWidth(120);
        Label imageCaption = new Label("  SELECT A HOST TO GET STARTED  ");
        imageBox.getChildren().add(image);
        imageBox.getChildren().add(imageCaption);
        imageBox.setAlignment(Pos.CENTER);
        return imageBox;
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
                        starter.getRemoteForwarding(m.group(0)),
                        (hostProperties.containsKey("IdentityFile")) ? hostProperties.get("IdentityFile").toString() : null,
                        (hostProperties.containsKey("ForwardAgent")) ? hostProperties.get("ForwardAgent").toString() : null,
                        (hostProperties.containsKey("ForwardX11")) ? hostProperties.get("ForwardX11").toString() : null)
                );
            }
        }
        return allHosts;
    }

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #0ca0b8;");

        Button newHostButton = new Button("+ New Host");
        newHostButton.setPrefSize(100, 20);

        newHostButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                border.setCenter(detailsVBox(addNewHost()));
            }
        });

        Button saveAllButton = new Button("Save All");
        saveAllButton.setPrefSize(100, 20);
        hbox.getChildren().addAll(newHostButton, saveAllButton);

        return hbox;
    }

    public VBox detailsVBox(GridPane grid) {
        VBox detailsBox = new VBox();
        detailsBox.setPadding(new Insets(30, 0, 0, 0));
        detailsBox.setSpacing(0);

        ScrollPane s1 = new ScrollPane();
        s1.setPrefSize(510, 490); //set scrollpane width and height
        s1.setContent(grid);
        detailsBox.getChildren().add(s1);

        return detailsBox;
    }

    public VBox addLeftNavigation() throws IOException {
        VBox hostList = new VBox();
        hostList.setPadding(new Insets(10));
        hostList.setSpacing(8);

        Text title = new Text("HOSTS");
        title.setFont(Font.font("Optima", FontWeight.BOLD, 14));
        hostList.getChildren().add(title);

        //list navigation start here
        ObservableList hostNavigationList = FXCollections.observableArrayList();
        final ListView listView = new ListView(hostNavigationList);
        listView.setPrefSize(200, 490); // left navigation width, height
        listView.setEditable(true);
        //list navigation end here

        for (Host h : allHosts.getAllHosts()) {
            hostNavigationList.add(h.getHostAlias());
        }

        listView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                                        String old_val, String new_val) {
                        border.setCenter(detailsVBox(getHostDetailsAsGrid(new_val)));

                    }
                });
        hostList.getChildren().add(listView);

        return hostList;
    }

    public void headingBar(HBox hb) {
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

        hb.getChildren().add(stack);
        HBox.setHgrow(stack, Priority.ALWAYS);    // Give stack any extra space
    }

    public GridPane getHostDetailsAsGrid(String hostAlias) {

        resetGlobals();

        GridPane grid = new GridPane();

        // store existing local and remote forwards so that they can be combined with new edits
        Map<Integer, String> addLocalForwards = new HashMap<>();
        Map<Integer, String> addRemoteForwards = new HashMap<>();

        // store existing 'otherOptions' such as identityFile, forwardAgent and forwardX11
        Map<String, TextField> otherOptions = new HashMap<>();


        for (Host h : allHosts.getAllHosts()) {
            int rowToInsert = 1;
            if (h.getHostAlias().equals(hostAlias)) {
                print(h.getHostName());

                // set currentHost as the validated host for this view
                currentHost = h;
                grid.setVgap(4); //line spacing between grid elements
                grid.setPadding(new Insets(5, 5, 5, 50)); //last value sets left-indentation

                grid.addRow(rowToInsert, new Label("Host "));
                editableHostField = new TextField(h.getHostName());
                grid.add(editableHostField, 1, rowToInsert);
                rowToInsert++;

                if (h.getPort() != -1) {
                    grid.addRow(rowToInsert, new Label("Port "));
                    editablePortField = new TextField(String.valueOf(h.getPort()));
                    grid.add(editablePortField, 1, rowToInsert);
                    rowToInsert++;
                }

                grid.addRow(rowToInsert, new Label("User "));
                editableUserField = new TextField(h.getUser());
                grid.add(editableUserField, 1, rowToInsert);
                rowToInsert++;

                try {
                    Set<Integer> localForwardList = h.getLocalForward().keySet();
                    Set<Integer> remoteForwardList = h.getRemoteForward().keySet();
                    if (localForwardList.size() > 0) {
                        for (int key : localForwardList) {
                            grid.addRow(rowToInsert, new Label("Local Forward "));
                            grid.add(new TextField(String.valueOf(key)), 1, rowToInsert);
                            grid.add(new TextField(h.getLocalForward().get(key).toString()), 2, rowToInsert);
                            addLocalForwards.put(key, h.getLocalForward().get(key).toString());
                            rowToInsert++;
                        }
                    }
                    if (remoteForwardList.size() > 0) {
                        for (int key : remoteForwardList) {
                            grid.addRow(rowToInsert, new Label("Remote Forward "));
                            grid.add(new TextField(String.valueOf(key)), 1, rowToInsert);
                            grid.add(new TextField(h.getRemoteForward().get(key).toString()), 2, rowToInsert);
                            addRemoteForwards.put(key, h.getRemoteForward().get(key).toString());
                            rowToInsert++;
                        }
                    }
                } catch (NullPointerException ex) {
                    System.out.println("No LocalForwards Found");
                }
                try {
                    if (h.getIdentityFile() != null && h.getIdentityFile() != "") {
                        grid.addRow(rowToInsert, new Label("IdentityFile"));
                        editableIdentityFileField = new TextField(String.valueOf(h.getIdentityFile()));
                        grid.add(editableIdentityFileField, 1, rowToInsert);
                        rowToInsert++;
                    }
                    if (h.getForwardAgent() != null && h.getForwardAgent() != "") {
                        grid.addRow(rowToInsert, new Label("ForwardAgent"));
                        editableForwardAgentField = new TextField(String.valueOf(h.getForwardAgent()));
                        grid.add(editableForwardAgentField, 1, rowToInsert);
                        rowToInsert++;
                    }
                    if (h.getForwardX11() != null && h.getForwardX11() != "") {
                        grid.addRow(rowToInsert, new Label("ForwardX11"));
                        editableForwardX11Field = new TextField(String.valueOf(h.getForwardX11()));
                        grid.add(editableForwardX11Field, 1, rowToInsert);
                        rowToInsert++;
                    }
                } catch (NullPointerException ex) {
                    System.out.println("One of the SSH Options was not set");
                }

                //manually set existing identityFile, forwardAgent and forwardX11 so that they are not
                //overwritten by new edits.
//                if (currentHost.getIdentityFile() != null && currentHost.getIdentityFile() != "") {
//                    editableIdentityFileField.setText(currentHost.getIdentityFile());
//                }
//                if (currentHost.getForwardAgent() != null && currentHost.getForwardAgent() != "") {
//                    editableForwardAgentField.setText(currentHost.getForwardAgent());
//                }
//                if (currentHost.getForwardX11() != null && currentHost.getForwardX11() != "") {
//                    editableForwardX11Field.setText(currentHost.getForwardX11());
//                }
            }
        }

        Button newHostEditSaveBtn = new Button("Save");
        ObservableList<String> options = FXCollections.observableArrayList(
                "IdentityFile",
                "LocalForward",
                "RemoteForward",
                "ForwardAgent",
                "ForwardX11"
        );
        ComboBox propertyBox = new ComboBox(options);

        propertyBox.setCellFactory(param -> {
            final ListCell<String> cell = new ListCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty)
                        setText(item);
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                propertyBox.setValue(null);
                propertyBox.getSelectionModel().select(cell.getItem());

                e.consume();
            });
            return cell;
        });

        Map<TextField, TextField> tempMapLF = new HashMap<>();
        Map<TextField, TextField> tempMapRF = new HashMap<>();

        propertyBox.valueProperty().addListener((obs, oldVal, selectedValue) -> {
            if (selectedValue != null) {
                int rowCounter = getRowCount(grid);
                grid.add(new Label(selectedValue.toString()), 0, rowCounter + 1);
                TextField forwardingsKey = newTextFieldWithIdPrompt(selectedValue.toString(), "port to forward");
                TextField forwardingsValue = newTextFieldWithIdPrompt(selectedValue.toString(), "destination address");
                TextField field = new TextField();
                if (selectedValue.toString().equals("LocalForward")) {
                    tempMapLF.put(forwardingsKey, forwardingsValue);
                    grid.add(forwardingsKey, 1, rowCounter + 1);
                    grid.add(forwardingsValue, 2, rowCounter + 1);
                } else if (selectedValue.toString().equals("RemoteForward")) {
                    tempMapRF.put(forwardingsKey, forwardingsValue);
                    grid.add(forwardingsKey, 1, rowCounter + 1);
                    grid.add(forwardingsValue, 2, rowCounter + 1);
                } else {
                    field = newTextFieldWithIdPrompt(selectedValue.toString(), "");
                    otherOptions.put(selectedValue.toString(), field);
                    grid.add(field, 1, rowCounter + 1);
                }

                newHostEditSaveBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        for (Map.Entry<TextField, TextField> entry : tempMapLF.entrySet()) {
                            addLocalForwards.put(Integer.parseInt(entry.getKey().getText()), entry.getValue().getText());
                        }
                        for (Map.Entry<TextField, TextField> entry : tempMapRF.entrySet()) {
                            addRemoteForwards.put(Integer.parseInt(entry.getKey().getText()), entry.getValue().getText());
                        }
                        System.out.println("EDITED =>" + editableHostField.getText());
                        System.out.println("EDITED =>" + editableIdentityFileField.getText());
                        System.out.println("EDITED =>" + editableForwardAgentField.getText());


                        // make sure otherOptions map contains edited/updated values
                        for (Map.Entry<String, TextField> entry : otherOptions.entrySet()) {
                            if (entry.getValue().equals("IdentityFile")) {
                                entry.setValue(editableIdentityFileField);
                            } else if (entry.getValue().equals("ForwardAgent")) {
                                entry.setValue(editableForwardAgentField);
                            } else if (entry.getValue().equals("ForwardX11")) {
                                entry.setValue(editableForwardX11Field);
                            }
                        }
                        print(currentHost.getHostAlias());
                        setExistingHostProperties(currentHost,
                                currentHost.getHostAlias(),
                                editableHostField.getText().isEmpty() ? currentHost.getHostName() : editableHostField.getText().toString(),
                                editablePortField.getText().isEmpty() ? currentHost.getPort() : Integer.parseInt(editablePortField.getText()),
                                editableUserField.getText().isEmpty() ? currentHost.getUser() : editableUserField.getText(),
                                addLocalForwards,
                                addRemoteForwards,
                                editableIdentityFileField.getText().isEmpty() ? null : editableIdentityFileField.getText(),
                                editableForwardAgentField.getText().isEmpty() ? null : editableForwardAgentField.getText(),
                                editableForwardX11Field.getText().isEmpty() ? null : editableForwardX11Field.getText()
                        );

                        reDrawHostNav();
                    }
                });
            }
        });
        propertyBox.setPromptText("Add SSH Option");

        grid.add(propertyBox, 1, 0);
        grid.add(newHostEditSaveBtn, 2, 0);
        return grid;

    }

    public GridPane addNewHost() {
        GridPane grid = new GridPane();
        grid.setVgap(4); //line spacing between grid elements
        grid.setPadding(new Insets(5, 5, 5, 50)); //last value sets left-indentation
        Text newHostTitle = new Text("+ NEW HOST");
        newHostTitle.setFont(Font.font("Optima", FontWeight.BOLD, 14));
        Button newHostSaveBtn = new Button("Save");
        grid.add(newHostTitle, 0, 0);

        grid.add(new Label("Host Alias"), 0, 1);
        TextField hostAliasField = newTextFieldWithIdPrompt("HOST_ALIAS", "alias or nickname");
        grid.add(hostAliasField, 1, 1);
        grid.add(new Label("Host"), 0, 2);
        TextField hostField = newTextFieldWithIdPrompt("HOST", "ip address or DNS name");
        grid.add(hostField, 1, 2);
        grid.add(new Label("Port"), 0, 3);
        TextField portField = newTextFieldWithIdPrompt("PORT", "port number");
        grid.add(portField, 1, 3);
        grid.add(new Label("User"), 0, 4);
        TextField userField = newTextFieldWithIdPrompt("USER", "username for login");
        grid.add(userField, 1, 4);

        ObservableList<String> options = FXCollections.observableArrayList(
                "IdentityFile",
                "LocalForward",
                "RemoteForward",
                "ForwardAgent",
                "ForwardX11"
        );
        ComboBox propertyBox = new ComboBox(options);

        propertyBox.setCellFactory(param -> {
            final ListCell<String> cell = new ListCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty)
                        setText(item);
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                propertyBox.setValue(null);
                propertyBox.getSelectionModel().select(cell.getItem());

                e.consume();
            });
            return cell;
        });

        Map<Integer, String> addLocalForwards = new HashMap<>();
        Map<Integer, String> addRemoteForwards = new HashMap<>();
        Map<TextField, TextField> tempMapLF = new HashMap<>();
        Map<TextField, TextField> tempMapRF = new HashMap<>();
        Map<String, TextField> otherOptions = new HashMap<>();

        propertyBox.valueProperty().addListener((obs, oldVal, selectedValue) -> {
            if (selectedValue != null) {
                int rowCounter = getRowCount(grid);
                grid.add(new Label(selectedValue.toString()), 0, rowCounter + 1);
                TextField forwardingsKey = newTextFieldWithIdPrompt(selectedValue.toString(), "port to forward");
                TextField forwardingsValue = newTextFieldWithIdPrompt(selectedValue.toString(), "destination address");
                TextField field = new TextField();
                if (selectedValue.toString().equals("LocalForward")) {
                    tempMapLF.put(forwardingsKey, forwardingsValue);
                    grid.add(forwardingsKey, 1, rowCounter + 1);
                    grid.add(forwardingsValue, 2, rowCounter + 1);
                } else if (selectedValue.toString().equals("RemoteForward")) {
                    tempMapRF.put(forwardingsKey, forwardingsValue);
                    grid.add(forwardingsKey, 1, rowCounter + 1);
                    grid.add(forwardingsValue, 2, rowCounter + 1);
                } else {
                    field = newTextFieldWithIdPrompt(selectedValue.toString(), "");
                    otherOptions.put(selectedValue.toString(), field);
                    grid.add(field, 1, rowCounter + 1);
                }

                newHostSaveBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        for (Map.Entry<TextField, TextField> entry : tempMapLF.entrySet()) {
                            addLocalForwards.put(Integer.parseInt(entry.getKey().getText()), entry.getValue().getText());
                        }
                        for (Map.Entry<TextField, TextField> entry : tempMapRF.entrySet()) {
                            addRemoteForwards.put(Integer.parseInt(entry.getKey().getText()), entry.getValue().getText());
                        }

                        Boolean createNewHost = false;
                        Host existingHost = null;
                        for (Host host : allHosts.getAllHosts()) {
                            if (hostAliasField.getText().equals(host.getHostAlias())) {
                                createNewHost = false;
                                existingHost = host;
                                break;
                            } else {
                                createNewHost = true;
                            }
                        }
                        print("New host =" + hostAliasField.getText() + "and createNewHost =" + createNewHost);
                        if (createNewHost) {
                            allHosts.addHost(new Host(hostAliasField.getText(),
                                    hostField.getText(),
                                    Integer.parseInt(portField.getText()),
                                    userField.getText(),
                                    addLocalForwards,
                                    addRemoteForwards,
                                    (otherOptions.containsKey("IdentityFile")) ? otherOptions.get("IdentityFile").getText().toString() : null,
                                    (otherOptions.containsKey("ForwardAgent")) ? otherOptions.get("ForwardAgent").getText().toString() : null,
                                    (otherOptions.containsKey("ForwardX11")) ? otherOptions.get("ForwardX11").getText().toString() : null));
                        } else {
                            setExistingHostProperties(existingHost,
                                    hostAliasField.getText(),
                                    hostField.getText(),
                                    Integer.parseInt(portField.getText()),
                                    userField.getText(),
                                    addLocalForwards,
                                    addRemoteForwards,
                                    (otherOptions.containsKey("IdentityFile")) ? otherOptions.get("IdentityFile").getText().toString() : "",
                                    (otherOptions.containsKey("ForwardAgent")) ? otherOptions.get("ForwardAgent").getText().toString() : "",
                                    (otherOptions.containsKey("ForwardX11")) ? otherOptions.get("ForwardX11").getText().toString() : ""
                            );
                        }


                        reDrawHostNav();
                    }
                });
            }
        });
        propertyBox.setPromptText("Add SSH Option");

        grid.add(propertyBox, 1, 0);
        grid.add(newHostSaveBtn, 2, 0);

        return grid;
    }

    private int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if (rowIndex != null) {
                    numRows = Math.max(numRows, rowIndex + 1);
                }
            }
        }
        return numRows;
    }

    public void reDrawHostNav() {
        try {
            border.setLeft(addLeftNavigation());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public TextField newTextFieldWithIdPrompt(String textFieldID, String promptText) {
        TextField textField = new TextField();
        textField.setId(textFieldID);
        textField.setPromptText(promptText);
        return textField;
    }

    public void setExistingHostProperties(Host host, String hostAlias, String hostName, int port, String userName,
                                          Map localForwards, Map remoteForwards, String identityFile, String forwardAgent,
                                          String forwardX11) {
        host.setHostAlias(hostAlias);
        host.setHostName(hostName);
        host.setPort(port);
        host.setUser(userName);
        host.addLocalForward(localForwards);
        host.addRemoteForward(remoteForwards);
        host.setIdentityFile(identityFile);
        host.setForwardAgent(forwardAgent);
        host.setForwardX11(forwardX11);

    }

    public void resetGlobals() {
        editableHostField = new TextField();
        editablePortField = new TextField();
        editableUserField = new TextField();
        editableIdentityFileField = new TextField();
        editableForwardAgentField = new TextField();
        editableForwardX11Field = new TextField();
    }
}