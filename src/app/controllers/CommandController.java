package app.controllers;

import app.model.TodoItem;
import app.model.TodoItemList;

import java.util.ArrayList;
import java.util.Date;

public class CommandController {
    protected enum COMMAND_TYPE {
        ADD, DELETE, DISPLAY, EXIT, INVALID, SEARCH
    }

    // Errors
    private final String ERROR_FILE_EMPTY = "Task list is empty.\n";
    private final String ERROR_WRONG_COMMAND_FORMAT = "Command error.\n";
    private final String ERROR_SEARCH_TERM_NOT_FOUND = "Search term not found.\n";

    // Messages
    private final String MESSAGE_ADD_COMPLETE = "added: \"%1$s\"\n";
    private final String MESSAGE_DELETE_COMPLETE = "deleted: \"%1$s\"\n";
    private final String MESSAGE_SEARCH_COMPLETE = "Search result(s):\n%1$s";

    // Class variables
    private TodoItemList taskList;

    // String manipulation methods
    protected void printString(String message) {
        System.out.print(message);
    }

    protected int firstSpacePosition(String command) {
        return command.indexOf(" ");
    }

    protected String getFirstWord(String command) {
        int firstWordPos = firstSpacePosition(command);
        if(firstWordPos == -1) {
            return command;
        }
        return command.substring(0, firstWordPos);
    }

    // Individual command methods
    protected String addNewLine(String command){
        int firstWordPos = firstSpacePosition(command);
        if (firstWordPos == -1) {
            return ERROR_WRONG_COMMAND_FORMAT;
        }
        String toBeInserted = command.substring(firstWordPos + 1);
        taskList.addTodoItem(new TodoItem(toBeInserted, new Date(), new Date()));
        return String.format(MESSAGE_ADD_COMPLETE, toBeInserted);
    }

    protected String display(String command) {
        int firstWordPos = firstSpacePosition(command);
        if (firstWordPos != -1) {
            return ERROR_WRONG_COMMAND_FORMAT;
        }
        ArrayList<TodoItem> todoList = taskList.getTodoItems();
        if (todoList.isEmpty()) {
            return String.format(ERROR_FILE_EMPTY);
        }
        return displayTasks(todoList);
    }

    protected String displayTasks(ArrayList<TodoItem> todoList) {
        String returnString = "";
        int index = 1;
        for (TodoItem todo : todoList) {
            returnString += index + ". " + todo.getTaskName() + "\n";
            index++;
        }
        return returnString;
    }

    protected String deleteEntry(String command) {
        int firstWordPos = firstSpacePosition(command);
        if (firstWordPos == -1) {
            return ERROR_WRONG_COMMAND_FORMAT;
        }
        int index = -1;
        if(isInt(command.substring(firstWordPos + 1))) {
            index = Integer.parseInt(command.substring(firstWordPos + 1)) - 1;
        }
        ArrayList<TodoItem> todoList = taskList.getTodoItems();
        if (index < 0 || index >= todoList.size()) {
            return ERROR_WRONG_COMMAND_FORMAT;
        }
        String toBeDeleted = todoList.get(index).getTaskName();
        taskList.deleteTodoItem(index);
        return String.format(MESSAGE_DELETE_COMPLETE, toBeDeleted);
    }

    protected boolean isInt(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    protected String search(String command) {
        int firstWordPos = firstSpacePosition(command);
        if (firstWordPos == -1) {
            return ERROR_WRONG_COMMAND_FORMAT;
        }
        ArrayList<TodoItem> todoList = taskList.getTodoItems();
        if (todoList.isEmpty()) {
            return String.format(ERROR_FILE_EMPTY);
        }
        String returnString = searchList(command.substring(firstWordPos + 1), todoList);
        if (returnString.equals("")) {
            return ERROR_SEARCH_TERM_NOT_FOUND;
        } else {
            return String.format(MESSAGE_SEARCH_COMPLETE, returnString);
        }
    }

    protected String searchList(String query, ArrayList<TodoItem> todoList) {
        String returnString = "";
        int index = 1;
        for (TodoItem todo : todoList) {
            if (todo.getTaskName().toLowerCase().
                    contains(query.toLowerCase())) {
                returnString += index + ". " + todo.getTaskName() + "\n";
            }
            index++;
        }
        return returnString;
    }

    // Command processing methods
    protected COMMAND_TYPE determineCommandType(String commandWord) {
        if (commandWord.equals("add")) {
            return COMMAND_TYPE.ADD;
        } else if (commandWord.equals("delete")) {
            return COMMAND_TYPE.DELETE;
        } else if (commandWord.equals("display")) {
            return COMMAND_TYPE.DISPLAY;
        } else if (commandWord.equals("exit")) {
            return COMMAND_TYPE.EXIT;
        } else if (commandWord.equals("search")) {
            return COMMAND_TYPE.SEARCH;
        } else {
            return COMMAND_TYPE.INVALID;
        }
    }

    protected String processCommand(String command) {
        String commandWord = getFirstWord(command);
        COMMAND_TYPE commandType = determineCommandType(commandWord);
        switch (commandType) {
            case ADD :
                return addNewLine(command);
            case DELETE :
                return deleteEntry(command);
            case DISPLAY :
                return display(command);
            case EXIT :
                System.exit(0);
            case SEARCH :
                return search(command);
            default :
                return ERROR_WRONG_COMMAND_FORMAT;
        }
    }

    public CommandController() {
        taskList = new TodoItemList();
    }

    public void parseCommand(String command) {
        printString("Parsing: \"" + command + "\"\n");
        printString(processCommand(command));
    }
}
