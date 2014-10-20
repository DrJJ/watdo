package app.model;

//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.ListIterator;
import java.util.UUID;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class TodoItemList
 * 
 * The model class to hold one to-do task of wat do.
 * 
 * @author Nguyen Quoc Dat (A0116703N)
 */

public class TodoItemList {
	private ArrayList<TodoItem> todoItems;
	
	public TodoItemList() {
		todoItems = new ArrayList<TodoItem>();
	}
	
	public TodoItemList(ArrayList<TodoItem> newTodoItems) {
	    if (newTodoItems != null) {
	        this.todoItems = newTodoItems;
	    } else {
	        this.todoItems = new ArrayList<TodoItem>();
	    }
	}
	
	public ArrayList<TodoItem> getTodoItems() {
		return todoItems;
	}
	
	public ArrayList<TodoItem> getUndoneTodoItems() {
	    ArrayList<TodoItem> undoneTodoItems = new ArrayList<TodoItem>();
	    for (TodoItem task : todoItems) {
	        if (!task.isDone()) {
	            undoneTodoItems.add(task);
	        }
	    }
	    return undoneTodoItems;
	}
	
	public TodoItem getByUUID(UUID itemID) {
	    for (int i = 0; i < todoItems.size(); i++) {
	        TodoItem currentItem = todoItems.get(i);
	        if (currentItem.getUUID().equals(itemID)) {
	            return currentItem;
	        }
	    }
	    return null;
	}
	
	
	public ListIterator<TodoItem> getTodoItemsIterator() {
	    return todoItems.listIterator();
	}

    public int countTodoItems() {
        return todoItems.size();
    }

    public void sortTodoItems(Comparator<TodoItem> todoItemComparator) {
        Collections.sort(todoItems, todoItemComparator);
    }
	
	// CRUD
	public void addTodoItem(TodoItem newItem) {
		todoItems.add(newItem);
	}
	
	public void clearTodoItems() {
        todoItems = new ArrayList<TodoItem>();
	}

    public TodoItem deleteByUUID(UUID itemID) {
        for (int i = 0; i < todoItems.size(); i++) {
            if (todoItems.get(i).getUUID().equals(itemID)) {
                return todoItems.remove(i);
            } 
        }
        return null;
    }
}