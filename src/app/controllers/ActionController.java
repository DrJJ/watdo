package app.controllers;

import app.Main;
import app.helpers.CommandObject;
import app.helpers.LoggingService;
import app.model.ModelManager;
import app.model.TodoItem;
import app.viewmanagers.TitleBarViewManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * This class takes in a command object, which specifies the details of the action to be carried out,
 * and then interacts with the model through ModelManager to carry out the action. The data structure can then
 * be extracted from this class by the CommandController.
 * 
 * @author ryan
 *
 */

public class ActionController {
    // Errors
    private final String ERROR_FILE_EMPTY = "Task list is empty.\n";
    private final String ERROR_INVALID_INDEX = "Error. Index is not found.\n";
    private final String ERROR_WRONG_COMMAND_FORMAT = "Error. Incorrect %1$s command format. Click help icon or type help for info.\n";
    private final String ERROR_SEARCH_TERM_NOT_FOUND = "Search term not found.\n";

    // Messages
    private final String MESSAGE_ADD_COMPLETE = "Added: \"%1$s\"\n";
    private final String MESSAGE_CHANGE_DONE_STATUS_COMPLETE = "Changed done status: \"%1$s\"\n";
    private final String MESSAGE_CLEAR_COMPLETE = "Todo cleared\n";
    private final String MESSAGE_DELETE_COMPLETE = "Deleted: \"%1$s\"\n";
    private final String MESSAGE_SEARCH_COMPLETE = "Serch complete. \n%1$s";
    private final String MESSAGE_UPDATE_COMPLETE = "Updated: \"%1$s\"\n";
    
    private final String MESSAGE_CHANGE_SAVE_FILE_LOCATION = "Save file location is changed\n";
    private final String MESSAGE_DISPLAY = "Displaying tasks\n";
    private final String MESSAGE_OPEN_HELP = "Showing help\n";
    private final String MESSAGE_OPEN_SETTINGS = "Showing settings\n";
    private final String MESSAGE_REDO = "Redo\n";
    private final String MESSAGE_UNDO = "Undo\n";
    
    // Class variables
    private static CommandController commandController;
    private static ModelManager modelManager;
    private static TaskController taskController;
    private static Main main;
    private static ArrayList<TodoItem> returnList;

    // Individual command methods
    // Add command method(s)
    protected String addNewLine(CommandObject commandObject){
        if (commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "add"));
        }
        try {
            commandController.getUndoController().saveUndo(modelManager.getTodoItemList());
            commandController.getUndoController().clearRedo();
            modelManager.addTask(commandObject.getCommandString(), commandObject.getStartDate(), commandObject.getEndDate(), commandObject.getPriority(), null);
        } catch (IOException e) {
            CommandController.notifyWithError("Failed to write to file.");
            LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        return CommandController.notifyWithInfo(String.format(MESSAGE_ADD_COMPLETE, commandObject.getInputString()));
    }

    // Display command method(s)
    protected String display(CommandObject commandObject) {
        if (!commandObject.getCommandString().isEmpty()) {
            if (commandObject.getCommandString().equals("all")) {
                returnList = taskController.getAllTasks();
            } else if (commandObject.getCommandString().equals("done")) {
                returnList = taskController.getDoneTasks();
            } else if (commandObject.getCommandString().equals("overdue")) {
                returnList = taskController.getOverdueTasks();
            } else if (commandObject.getCommandString().equals("overdue")) {
                returnList = taskController.getUndoneTasks();
            } else {
                return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "display"));
            }
        } else {
            returnList = taskController.getUndoneTasks();
        }
        return MESSAGE_DISPLAY;
    }

    // Clear command method(s)
    protected String clear(CommandObject commandObject) {
        if (!commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "clear"));
        }
        try {
            commandController.getUndoController().saveUndo(modelManager.getTodoItemList());
            commandController.getUndoController().clearRedo();
            modelManager.clearTasks();
        } catch (IOException e) {
            CommandController.notifyWithError("Failed to write to file.");
            LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        return MESSAGE_CLEAR_COMPLETE;
    }
    
    // Delete command method(s)
    protected String deleteEntry(CommandObject commandObject, ArrayList<TodoItem> currentList) {
        if (commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "delete"));
        }
        // To check that the index input is an integer
        if(!isInt(commandObject.getCommandString())) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "delete"));
        }
        int index = Integer.parseInt(commandObject.getCommandString()) - 1;
        // To check that the index is valid
        if (index < 0 || index >= currentList.size()) {
            return CommandController.notifyWithError(ERROR_INVALID_INDEX);
        }
        String toBeDeleted = currentList.get(index).getTaskName();
        try {
            commandController.getUndoController().saveUndo(modelManager.getTodoItemList());
            commandController.getUndoController().clearRedo();
            modelManager.deleteTask(currentList.get(index).getUUID());
        } catch (IOException e) {
            CommandController.notifyWithError("Failed to write to file.");
            LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        return CommandController.notifyWithInfo(String.format(MESSAGE_DELETE_COMPLETE, toBeDeleted));
    }

    protected boolean isInt(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Sort command method(s)
    protected String sort(CommandObject commandObject) {
        if (commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "sort"));
        }
        if (commandObject.getCommandString().equalsIgnoreCase("name")) {
            taskController.setSortingStyle(0);
            main.getRootViewManager().getTitleBarViewManager().setSortStyle(TitleBarViewManager.SortStyle.START);
            return "Sorting by task name\n";
        } else if (commandObject.getCommandString().equalsIgnoreCase("start")) {
            taskController.setSortingStyle(1);
            main.getRootViewManager().getTitleBarViewManager().setSortStyle(TitleBarViewManager.SortStyle.START);
            return "Sorting by start date\n";
        } else if (commandObject.getCommandString().equalsIgnoreCase("end")) {
            taskController.setSortingStyle(2);
            main.getRootViewManager().getTitleBarViewManager().setSortStyle(TitleBarViewManager.SortStyle.END);
            return "Sorting by end date\n";
        } else if (commandObject.getCommandString().equalsIgnoreCase("priority")) {
            taskController.setSortingStyle(3);
            main.getRootViewManager().getTitleBarViewManager().setSortStyle(TitleBarViewManager.SortStyle.PRIORITY);
            return "Sorting by priority\n";
        } else {
            return CommandController.notifyWithError(ERROR_WRONG_COMMAND_FORMAT);
        }
        
    }
    
    /**
     * Calls taskController to search for query, then updates resultList (which will be used by CommandController
     * to show to view).
     * 
     * @param commandObject
     * @return A string notifying whether the method carries out properly.
     */
    // Search command method(s)
    protected String search(CommandObject commandObject) {
        if (commandObject.getCommandString().isEmpty() && !commandObject.isStartDateUpdated() && !commandObject.isEndDateUpdated()) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "search"));
        }
        try {
            ArrayList<TodoItem> todoList = modelManager.getTodoItemList();
            if (todoList.isEmpty()) {
                return CommandController.notifyWithError(String.format(ERROR_FILE_EMPTY));
            }
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        ArrayList<TodoItem> results = new ArrayList<TodoItem>();
        if (commandObject.isStartDateUpdated()) {
            if (commandObject.isEndDateUpdated()) {
                results = taskController.getTasksWithinDateRange(commandObject.getStartDate(), commandObject.getEndDate());
            } else {
                results = taskController.getTasksStartingOn(commandObject.getStartDate());
            }
        } else if (commandObject.isEndDateUpdated()) {
            results = taskController.getTasksEndingOn(commandObject.getEndDate());
        } else {
            results = taskController.instantSearch(commandObject.getCommandString());
        }
        returnList = results;
        if (results.isEmpty()) {
            // Error handling for when I/O with database failed.
            if (modelManager != null) {
                return CommandController.notifyWithError(ERROR_SEARCH_TERM_NOT_FOUND);
            } else {
                return null;
            }
        } else {
//            main.getPrimaryStage().setTitle("Search results for: \"" + commandObject.getCommandString() + "\"");
            taskController.setDisplayType(TaskController.DisplayType.SEARCH);
            return String.format(MESSAGE_SEARCH_COMPLETE, "updating task list view with results\n");
        }
    }

    /**
     * Updates ModelManager and gets new data based on the CommandObject
     * 
     * @param commandObject
     * @param currentList The current data to be passed to display.
     * @return a feedback string to notify whether the method has carried out successfully 
     */
    // Update command method(s)
    protected String update(CommandObject commandObject, ArrayList<TodoItem> currentList) {
        if (commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "update"));
        }
        Boolean[] parameters = {false, false, false, false, false};
        
        StringTokenizer st = new StringTokenizer(commandObject.getCommandString());
        String check = st.nextToken();
        // To check that the index input is an integer
        if(!isInt(check)) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "update"));
        }
        int index = Integer.parseInt(check) - 1;
        // To check that the index is valid
        if (index < 0 || index >= currentList.size()) {
            return CommandController.notifyWithError(ERROR_INVALID_INDEX);
        }
        String toBeUpdated = "";
        while (st.hasMoreTokens()) {
            toBeUpdated = toBeUpdated.concat(st.nextToken()) + " ";
            parameters[0] = true;
        }
        if (commandObject.isStartDateUpdated()) {
            parameters[1] = true;
        }
        if (commandObject.isEndDateUpdated()) {
            parameters[2] = true;
        }
        if (commandObject.getPriority() != null) {
            parameters[3] = true;
        }
        try {
            commandController.getUndoController().saveUndo(modelManager.getTodoItemList());
            commandController.getUndoController().clearRedo();
            modelManager.updateTask(currentList.get(index).getUUID(),
                                    parameters, toBeUpdated.trim(), commandObject.getStartDate(), commandObject.getEndDate(), commandObject.getPriority(), null);
        } catch (IOException e) {
            CommandController.notifyWithError("Failed to write to file.");
            LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        return CommandController.notifyWithInfo(String.format(MESSAGE_UPDATE_COMPLETE, commandObject.getInputString()));
    }

    // Done method
    protected String done(CommandObject commandObject, ArrayList<TodoItem> currentList) {
        if (commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "done"));
        }
        // To check that the index input is an integer
        if (!isInt(commandObject.getCommandString())) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "done"));
        }
        int index = Integer.parseInt(commandObject.getCommandString()) - 1;
        // To check that the index is valid
        if (index < 0 || index >= currentList.size()) {
            return CommandController.notifyWithError(ERROR_INVALID_INDEX);
        }
        Boolean[] parameters = {false, false, false, false, true};
        try {
            commandController.getUndoController().saveUndo(modelManager.getTodoItemList());
            commandController.getUndoController().clearRedo();
            modelManager.updateTask(currentList.get(index).getUUID(), parameters, null, null, null, null, true);
        } catch (IOException e) {
            CommandController.notifyWithError("Failed to write to file.");
            LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        return CommandController.notifyWithInfo(String.format(MESSAGE_CHANGE_DONE_STATUS_COMPLETE, commandObject.getCommandString()));
    }

    // Undone method
    protected String undone(CommandObject commandObject, ArrayList<TodoItem> currentList) {
        if (commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "undone"));
        }
        // To check that the index input is an integer
        if (!isInt(commandObject.getCommandString())) {
            return CommandController.notifyWithError(String.format(ERROR_WRONG_COMMAND_FORMAT, "undone"));
        }
        int index = Integer.parseInt(commandObject.getCommandString()) - 1;
        // To check that the index is valid
        if (index < 0 || index >= currentList.size()) {
            return CommandController.notifyWithError(ERROR_INVALID_INDEX);
        }
        Boolean[] parameters = {false, false, false, false, true};
        try {
            commandController.getUndoController().saveUndo(modelManager.getTodoItemList());
            commandController.getUndoController().clearRedo();
            modelManager.updateTask(currentList.get(index).getUUID(), parameters, null, null, null, null, false);
        } catch (IOException e) {
            CommandController.notifyWithError("Failed to write to file.");
            LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        return CommandController.notifyWithInfo(String.format(MESSAGE_CHANGE_DONE_STATUS_COMPLETE, commandObject.getCommandString()));
    }

    // Help method
    protected String help(CommandObject commandObject) {
        if (!commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(ERROR_WRONG_COMMAND_FORMAT);
        }
        main.getRootViewManager().openHelp();
        return MESSAGE_OPEN_HELP;
    }

    // Settings method
    protected String settings(CommandObject commandObject) {
        if (!commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(ERROR_WRONG_COMMAND_FORMAT);
        }
        main.getRootViewManager().openSettings();
        return MESSAGE_OPEN_SETTINGS;
    }
    
    // Change save file location (for .json)
    protected String changeSaveLocation(CommandObject commandObject) {
        if (commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(ERROR_WRONG_COMMAND_FORMAT);
        }
        try {
            modelManager.changeSettings(commandObject.getCommandString(), null, null);
        } catch (IOException e) {
            if (e.getMessage().equals(ModelManager.WRITE_SETTINGS_FAILED)) {
                CommandController.notifyWithError("Failed to write to settings.json file.");
            } else {
                CommandController.notifyWithError("Failed to load new file.");
            }
            LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        return MESSAGE_CHANGE_SAVE_FILE_LOCATION;
    }

    //@author A0111987X
    // Undo and redo method(s)
    protected String undo(CommandObject commandObject) {
        if (!commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(ERROR_WRONG_COMMAND_FORMAT);
        }
        if (main.getCommandController().getUndoController().isUndoEmpty()) {
            return CommandController.notifyWithError(ERROR_WRONG_COMMAND_FORMAT);
        } else {
            try {
                main.getCommandController().getUndoController().saveRedo(modelManager.getTodoItemList());
                modelManager.loadTodoItems(main.getCommandController().getUndoController().loadUndo());
            } catch (IOException e) {
                CommandController.notifyWithError("Failed to write to file.");
                LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
            } catch (NullPointerException e) {
                LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
            }
            return MESSAGE_UNDO;
        }
    }

    //@author A0111987X
    protected String redo(CommandObject commandObject) {
        if (!commandObject.getCommandString().isEmpty()) {
            return CommandController.notifyWithError(ERROR_WRONG_COMMAND_FORMAT);
        }
        if (main.getCommandController().getUndoController().isRedoEmpty()) {
            return CommandController.notifyWithError(ERROR_WRONG_COMMAND_FORMAT);
        } else {
            try {
                main.getCommandController().getUndoController().saveUndo(modelManager.getTodoItemList());
                modelManager.loadTodoItems(main.getCommandController().getUndoController().loadRedo());
            } catch (IOException e) {
                CommandController.notifyWithError("Failed to write to file.");
                LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
            } catch (NullPointerException e) {
                LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
            }
            return MESSAGE_REDO;
        }
    }

    //@author A0116703N
    /**
     * Calls modelManager to update with new settings.
     * 
     * @param filePath The new file directory to be used
     * @param randomColorsEnabled The new setting for random color display
     * @param notificationsEnabled The new setting for notifications display
     * @return The result string to be printed to the console
     */
    protected String changeSettings(String filePath, Boolean randomColorsEnabled, Boolean notificationsEnabled) {
        assert filePath != null;
        
        try {
            modelManager.changeSettings(filePath, randomColorsEnabled, notificationsEnabled);
        } catch (IOException e) {
            if (e.getMessage().equals(ModelManager.WRITE_SETTINGS_FAILED)) {
                CommandController.notifyWithError("Failed to write to settings.json file.");
            } else {
                CommandController.notifyWithError("Failed to load new file.");
            }
            LoggingService.getLogger().log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            LoggingService.getLogger().log(Level.SEVERE, "NullPointerException" + e.getMessage());
        }
        return "changed settings\n";
    }

    //@author A0114914L
    protected ActionController(ModelManager manager) {
        modelManager = manager;
        if (manager != null) {
            returnList = modelManager.getTodoItemList();
        } else {
            returnList = null;
        }
    }

    protected void setTaskController(TaskController controller) {
        taskController = controller;
    }

    protected ArrayList<TodoItem> getReturnList() {
        return returnList;
    }

    protected void setMainApp(Main main) {
        this.main = main;
    }

    protected void setCommandController(CommandController controller) {
        commandController = controller;
    }
}
