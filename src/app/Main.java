package app;

import app.controllers.*;
import app.helpers.HotkeyActivator;
import app.helpers.LoggingService;
import app.viewmanagers.RootViewManager;
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.dialog.Dialogs;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;

public class Main extends Application {

    private Stage primaryStage;

    private CommandController commandController;
    private TaskController taskController;
    private RootViewManager rootViewManager;

    @Override
    public void start(Stage stage) throws Exception {
        LoggingService.getLogger().log(Level.INFO, "Launching app");

        createPrimaryStage(stage);
        initViewComponent();
        initControllerComponents();

        rootViewManager.setAndFocusInputField("");
    }

    private void createPrimaryStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("wat do");
        primaryStage.setResizable(false);
    }

    private void initViewComponent() {
        rootViewManager = new RootViewManager();
        rootViewManager.setMainApp(this);
        rootViewManager.initLayout(primaryStage);
    }

    private void initControllerComponents() {
        commandController = new CommandController();
        taskController = taskController.getTaskController();
        commandController.setMainApp(this);
        taskController.setMainApp(this);
        commandController.setTaskList(commandController.getTaskList());
        commandController.updateView();

        HotkeyActivator hotkeyActivator = new HotkeyActivator();
        hotkeyActivator.setMainApp(this);
    }

    public void showInfoDialog(String title, String message) {
        Dialogs.create()
                .owner(primaryStage)
                .title(title)
                .masthead(null)
                .message(message)
                .showInformation();
    }

    public void showErrorDialog(String title, String error) {
        Dialogs.create()
                .owner(primaryStage)
                .title(title)
                .masthead(null)
                .message(error)
                .showError();
    }

    public File getSaveLocation() {
        // call TaskController/FileStorage getter
        return new File("");
    }

    public void setSaveLocation(File file) {
        // call TaskController/FileStorage setter
    }

    public URL getResourceURL(String relativePath) {
        return this.getClass().getResource(relativePath);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public CommandController getCommandController() {
        return commandController;
    }

    public TaskController getTaskController() {
        return taskController;
    }

    public RootViewManager getRootViewManager() {
        return rootViewManager;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
