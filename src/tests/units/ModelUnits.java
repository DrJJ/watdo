package tests.units;
//@author A0116703N

import app.model.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ModelUnits {

    // Tests TodoItem constructor
    @Test
    public void testTodoItemConstructor() {
        String testInput1 = "Test String 1";
        Date startDate1 = new Date();
        Date endDate1 = new Date();
        String testInput2 = "Dummy priority";
        String testInput3 = "1. High";
        Boolean testBoolean1 = true;
        
        // Valid inputs, standard constructor
        TodoItem testedTodoItem1 = new TodoItem(testInput1, startDate1, endDate1, testInput3, testBoolean1);
        assertEquals(testInput1, testedTodoItem1.getTaskName());
        assertEquals(startDate1.getTime(), testedTodoItem1.getStartDate().getTime());
        assertEquals(endDate1.getTime(), testedTodoItem1.getEndDate().getTime());
        assertEquals(TodoItem.HIGH, testedTodoItem1.getPriority());
        assertEquals(testBoolean1, testedTodoItem1.isDone());
        
        // Special constructor
        TodoItem testedTodoItem2 = new TodoItem(testInput1, startDate1, endDate1);
        assertEquals(testInput1, testedTodoItem2.getTaskName());
        assertEquals(startDate1.getTime(), testedTodoItem2.getStartDate().getTime());
        assertEquals(endDate1.getTime(), testedTodoItem2.getEndDate().getTime());
        assertEquals(TodoItem.MEDIUM, testedTodoItem2.getPriority());
        assertEquals(false, testedTodoItem2.isDone());
        
        // Borderline bad inputs, standard constructor
        TodoItem testedTodoItem3 = new TodoItem(null, null, null, testInput2, null);
        assertEquals(null, testedTodoItem3.getTaskName());
        assertEquals(null, testedTodoItem3.getStartDate());
        assertEquals(null, testedTodoItem3.getEndDate());
        assertEquals(TodoItem.MEDIUM, testedTodoItem3.getPriority());
        assertEquals(false, testedTodoItem3.isDone());
    }
    
    // Tests TodoItemList constructor
    @Test
    public void testTodoItemListConstructor() {
        // Standard constructor
        TodoItemList testedList1 = new TodoItemList();
        assertEquals(0, testedList1.countTodoItems());
        
        // Special constructor for existing data
        ArrayList<TodoItem> inputArrayList1 = new ArrayList<TodoItem>();
        TodoItemList testedList2 = new TodoItemList(inputArrayList1);
        assertEquals(0, testedList2.countTodoItems());
        
        // Another special constructor for existing data
        ArrayList<TodoItem> inputArrayList2 = new ArrayList<TodoItem>();
        inputArrayList2.add(new TodoItem(null, null, null));
        TodoItemList testedList3 = new TodoItemList(inputArrayList2);
        assertEquals(1, testedList3.countTodoItems());
    }
    
    // Tests TodoItemList create and delete
    @Test
    public void testTodoItemListAddAndDelete() {
        String testInput1 = "Test String 1";
        String testInput2 = "Test String 2";
        String testInput3 = "Test String 3";
        Date startDate1 = new Date();
        Date endDate1 = new Date();
        
        // Add
        TodoItemList testedList1 = new TodoItemList();
        testedList1.addTodoItem(new TodoItem(testInput1, startDate1, endDate1));
        assertEquals(1, testedList1.countTodoItems());
        testedList1.addTodoItem(new TodoItem(testInput2, startDate1, null));
        assertEquals(2, testedList1.countTodoItems());
        testedList1.addTodoItem(new TodoItem(testInput2, null, endDate1));
        assertEquals(3, testedList1.countTodoItems());
        testedList1.addTodoItem(new TodoItem(testInput3, null, null));
        assertEquals(4, testedList1.countTodoItems());
        
        // Test for task type
        ArrayList<TodoItem> currentTestedList = testedList1.getTodoItems();
        assertEquals(TodoItem.EVENT, currentTestedList.get(0).getTodoItemType());
        assertEquals(TodoItem.ENDLESS, currentTestedList.get(1).getTodoItemType());
        assertEquals(TodoItem.DEADLINE, currentTestedList.get(2).getTodoItemType());
        assertEquals(TodoItem.FLOATING, currentTestedList.get(3).getTodoItemType());
        
        // Delete
        testedList1.deleteByUUID(currentTestedList.get(2).getUUID());
        assertEquals(3, testedList1.countTodoItems());
        currentTestedList = testedList1.getTodoItems();
        assertEquals(testInput1, currentTestedList.get(0).getTaskName());
        assertEquals(null, currentTestedList.get(1).getEndDate());
        assertEquals(null, currentTestedList.get(2).getEndDate());
        
        // Clear
        testedList1.clearTodoItems();
        assertEquals(0, testedList1.countTodoItems());
    }
    
    // Tests TodoItemSorter
    // Thou who trieth to debug this crawling horror, surrender.
    @Test
    public void testTodoItemSorter() {
        String testInput1 = "Test String 1";
        String testInput2 = "Test String 2";
        String testInput4 = "Test String 4";
        String testInput5 = "Test String 5";
        String testInput6 = "Test String 6";
        Date earlyDate = new Date();
        Date lateDate = new Date(earlyDate.getTime() + 100000);
        
        // Setup fixtures
        TodoItemList testedList1 = new TodoItemList();
        testedList1.addTodoItem(new TodoItem(testInput1, null, lateDate, TodoItem.HIGH, false));
        testedList1.addTodoItem(new TodoItem(testInput2, null, lateDate, TodoItem.MEDIUM, false));
        testedList1.addTodoItem(new TodoItem(null, earlyDate, lateDate, TodoItem.LOW, false));
        testedList1.addTodoItem(new TodoItem(testInput4, lateDate, earlyDate, TodoItem.MEDIUM, false));
        testedList1.addTodoItem(new TodoItem(testInput4, earlyDate, lateDate, TodoItem.HIGH, false));
        testedList1.addTodoItem(new TodoItem(null, null, lateDate, TodoItem.MEDIUM, false));
        testedList1.addTodoItem(new TodoItem(testInput5, null, lateDate, TodoItem.HIGH, false));
        testedList1.addTodoItem(new TodoItem(testInput6, null, lateDate, TodoItem.MEDIUM, false));
        
        // TaskName then EndDate
        TodoItemSorter.changeSortStyle(0);
        TodoItemSorter.resortTodoList(testedList1.getTodoItems());
        ArrayList<TodoItem> currentTodoItems = testedList1.getTodoItems();
        for (int i = testedList1.countTodoItems() - 1; i > 0; i--) {
            TodoItem currentTodoItem = currentTodoItems.get(i);
            TodoItem nextTodoItem = currentTodoItems.get(i - 1);
            if (currentTodoItem.getTaskName() == null) {
                if (nextTodoItem.getTaskName() == null) {
                   if (currentTodoItem.getEndDate() != null) {
                       if (nextTodoItem.getEndDate() == null) fail();
                       if (currentTodoItem.getEndDate().getTime() < nextTodoItem.getEndDate().getTime()) fail();
                   }
                }
            } else {
                if (nextTodoItem.getTaskName() == null) fail();
                if (currentTodoItem.getTaskName().compareTo(nextTodoItem.getTaskName()) < 0) fail();
                if (currentTodoItem.getTaskName().equals(nextTodoItem.getTaskName())) {
                    if (currentTodoItem.getEndDate() != null) {
                        if (nextTodoItem.getEndDate() == null) fail();
                        if (currentTodoItem.getEndDate().getTime() < nextTodoItem.getEndDate().getTime()) fail();
                    }
                }
            }
        }
        
        // StartDate then Priority
        TodoItemSorter.changeSortStyle(1);
        TodoItemSorter.resortTodoList(testedList1.getTodoItems());
        currentTodoItems = testedList1.getTodoItems();
        for (int i = testedList1.countTodoItems() - 1; i > 0; i--) {
            TodoItem currentTodoItem = currentTodoItems.get(i);
            TodoItem nextTodoItem = currentTodoItems.get(i - 1);
            if (currentTodoItem.getStartDate() == null) {
                if (nextTodoItem.getStartDate() == null) {
                   if (currentTodoItem.getPriority() != null) {
                       if (nextTodoItem.getPriority() == null) fail();
                       if (currentTodoItem.getPriority().compareTo(nextTodoItem.getPriority()) < 0) fail();
                   }
                }
            } else {
                if (nextTodoItem.getStartDate() == null) fail();
                if (currentTodoItem.getStartDate().getTime() < nextTodoItem.getStartDate().getTime()) fail();
                if (currentTodoItem.getStartDate().getTime() == nextTodoItem.getStartDate().getTime()) {
                    if (currentTodoItem.getPriority() != null) {
                        if (nextTodoItem.getPriority() == null) fail();
                        if (currentTodoItem.getPriority().compareTo(nextTodoItem.getPriority()) < 0) fail();
                    }
                }
            }
        }
        
        // EndDate then Priority
        TodoItemSorter.changeSortStyle(2);
        TodoItemSorter.resortTodoList(testedList1.getTodoItems());
        currentTodoItems = testedList1.getTodoItems();
        for (int i = testedList1.countTodoItems() - 1; i > 0; i--) {
            TodoItem currentTodoItem = currentTodoItems.get(i);
            TodoItem nextTodoItem = currentTodoItems.get(i - 1);
            if (currentTodoItem.getEndDate() == null) {
                if (nextTodoItem.getEndDate() == null) {
                   if (currentTodoItem.getPriority() != null) {
                       if (nextTodoItem.getPriority() == null) fail();
                       if (currentTodoItem.getPriority().compareTo(nextTodoItem.getPriority()) < 0) fail();
                   }
                }
            } else {
                if (nextTodoItem.getEndDate() == null) fail();
                if (currentTodoItem.getEndDate().getTime() < nextTodoItem.getEndDate().getTime()) fail();
                if (currentTodoItem.getEndDate().getTime() == nextTodoItem.getEndDate().getTime()) {
                    if (currentTodoItem.getPriority() != null) {
                        if (nextTodoItem.getPriority() == null) fail();
                        if (currentTodoItem.getPriority().compareTo(nextTodoItem.getPriority()) < 0) fail();
                    }
                }
            }
        }
        
        // Priority then EndDate
        TodoItemSorter.changeSortStyle(3);
        TodoItemSorter.resortTodoList(testedList1.getTodoItems());
        currentTodoItems = testedList1.getTodoItems();
        for (int i = testedList1.countTodoItems() - 1; i > 0; i--) {
            TodoItem currentTodoItem = currentTodoItems.get(i);
            TodoItem nextTodoItem = currentTodoItems.get(i - 1);
            if (currentTodoItem.getPriority() == null) {
                if (nextTodoItem.getPriority() == null) {
                   if (currentTodoItem.getEndDate() != null) {
                       if (nextTodoItem.getEndDate() == null) fail();
                       if (currentTodoItem.getEndDate().getTime() < nextTodoItem.getEndDate().getTime()) fail();
                   }
                }
            } else {
                if (nextTodoItem.getPriority() == null) fail();
                if (currentTodoItem.getPriority().compareTo(nextTodoItem.getPriority()) < 0) {
                    fail();
                }
                if (currentTodoItem.getPriority().equals(nextTodoItem.getPriority())) {
                    if (currentTodoItem.getEndDate() != null) {
                        if (nextTodoItem.getEndDate() == null) fail();
                        if (currentTodoItem.getEndDate().getTime() < nextTodoItem.getEndDate().getTime()) fail();
                    }
                }
            }
        }
    }
    
    // Tests FileStorage
    @Test
    public void testFileStorage() {
        FileStorage testStorage = new FileStorage();
        assertEquals(FileStorage.DEFAULT_FILE_NAME, testStorage.getFileName());
        assertEquals(FileStorage.DEFAULT_FILE_DIRECTORY, testStorage.getFileDirectory());
        
        // Try to load settings file
        try {
            testStorage.loadSettings();
        } catch (Exception e) {
            fail();
        }

        // Yay! Successfully loaded settings file!
        String tempFileDirectory = FileStorage.DEFAULT_FILE_DIRECTORY;
        Boolean tempRandomColorsEnabled = false;
        Boolean tempNotificationsEnabled = false;
        
        if (!testStorage.getFileDirectory().equals(tempFileDirectory)) {
            tempFileDirectory = testStorage.getFileDirectory();
        }
        
        if (testStorage.areRandomColorsEnabled()) {
            tempRandomColorsEnabled = true;
        }
        
        if (testStorage.areNotificationsEnabled()) {
            tempRandomColorsEnabled = true;
        }
        
        // Switch to test directory!
        try {
            testStorage.changeSettings("testDirectory", false, false);
        } catch (Exception e) {
            fail();
        }
        
        try {
            testStorage.loadSettings();
        } catch (Exception e) {
            fail();
        }
        
        assertEquals("testDirectory/", testStorage.getFileDirectory()); // Trailing slash added!
        assertEquals(false, testStorage.areRandomColorsEnabled());
        assertEquals(false, testStorage.areNotificationsEnabled());
        
        // Nice! Successfully switched to test directory!
        
        // Now we set up an empty watdo.json
        ArrayList<TodoItem> testTodoItems = new ArrayList<TodoItem>();
        try {
            testStorage.updateFile(testTodoItems);
        } catch (Exception e) {
            fail();
        }
        
        // Now we try to load it.
        FileReader fileToRead;
        try {
            fileToRead = new FileReader("testDirectory/watdo.json");
        } catch (FileNotFoundException e) {
            fail();
            return;
        }
        BufferedReader reader = new BufferedReader(fileToRead);
        String fileString = "";
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                fileString += line;
            }
            fileToRead.close();
        } catch (Exception e) {
            fail();
        }
        
        assertEquals("[]", fileString);
        
        // Yay! Now we insert actual data inside!
        String testInput1 = "Test String 1";
        Date earlyDate = new Date();
        earlyDate = new Date(earlyDate.getTime() - (earlyDate.getTime() % 1000)); // This i
        Date lateDate = new Date(earlyDate.getTime() + 100000);
        testTodoItems.add(new TodoItem(null, null, null));
        testTodoItems.add(new TodoItem(null, earlyDate, lateDate));
        testTodoItems.add(new TodoItem(testInput1, null, lateDate));
        testTodoItems.add(new TodoItem(testInput1, earlyDate, null));
        testTodoItems.add(new TodoItem(testInput1, earlyDate, lateDate, TodoItem.HIGH, true));
        try {
            testStorage.updateFile(testTodoItems);
        } catch (Exception e) {
            fail();
        }
        
        // Now we extract the data and see if it's the same.
        ArrayList<TodoItem> extractedResult;
        try {
            extractedResult = testStorage.loadFile();
        } catch (Exception e) {
            fail();
            return;
        }
        
        assertEquals(5, extractedResult.size());
        assertEquals(null, extractedResult.get(0).getTaskName());
        assertEquals(null, extractedResult.get(0).getStartDate());
        assertEquals(null, extractedResult.get(0).getEndDate());
        assertEquals(TodoItem.MEDIUM, extractedResult.get(0).getPriority());
        assertEquals(false, extractedResult.get(0).isDone());
        assertEquals(null, extractedResult.get(1).getTaskName());
        assertEquals(earlyDate.getTime(), extractedResult.get(1).getStartDate().getTime());
        assertEquals(lateDate.getTime(), extractedResult.get(1).getEndDate().getTime());
        assertEquals(lateDate.getTime(), extractedResult.get(1).getEndDate().getTime());
        assertEquals(TodoItem.HIGH, extractedResult.get(4).getPriority());
        assertEquals(true, extractedResult.get(4).isDone());
        
        // Yay! We're done! Time to clean up.
        try {
            testStorage.updateFile(new ArrayList<TodoItem>());
        } catch (Exception e) {
            fail();
        }
        
        // Switch back to old directory!
        try {
            testStorage.changeSettings(tempFileDirectory, tempRandomColorsEnabled, tempNotificationsEnabled);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
        
        try {
            testStorage.loadSettings();
        } catch (Exception e) {
            fail();
        }
        
        assertEquals(FileStorage.DEFAULT_FILE_NAME, testStorage.getFileName());
        assertEquals(tempFileDirectory, testStorage.getFileDirectory());
        assertEquals(tempRandomColorsEnabled, testStorage.areRandomColorsEnabled());
        assertEquals(tempNotificationsEnabled, testStorage.areNotificationsEnabled());
    }
    
    // Everyone together now!
    @Test
    public void testModelManager() {
        ModelManager testManager1;
        try {
            testManager1 = new ModelManager();
        } catch (Exception e) {
            fail();
            return;
        }
        
        try {
            testManager1.changeSettings("testDirectory/", null, null);
        } catch (Exception e) {
            fail();
        }
        
        // Create
        String testInput1 = "Test String 1";
        String testInput2 = "Test String 2";
        String testInput3 = "Test String 3";
        String testInput4 = "Test String 4";
        Date earlyDate = new Date();
        Date lateDate = new Date(earlyDate.getTime() + 100000);
        
        try {
            testManager1.clearTasks();
            testManager1.addTask(testInput1, earlyDate, lateDate, TodoItem.HIGH, true);
            testManager1.addTask(testInput2, null, null, TodoItem.LOW, null);
            testManager1.addTask(testInput3, earlyDate, null, TodoItem.MEDIUM, null);
            testManager1.addTask(testInput4, null, earlyDate, TodoItem.LOW, false);
        } catch (Exception e) {
            fail();
        }
        
        // Load
        ModelManager testManager2;
        try {
            testManager2 = new ModelManager();
        } catch (Exception e) {
            fail();
            return;
        }
        
        assertEquals(4, testManager2.countTasks());
        
        testManager2.setSortingStyle(0);
        
        assertEquals(testInput1, testManager2.getTodoItemList().get(0).getTaskName());
        assertEquals(testInput2, testManager2.getTodoItemList().get(1).getTaskName());
        assertEquals(testInput3, testManager2.getTodoItemList().get(2).getTaskName());
        assertEquals(testInput4, testManager2.getTodoItemList().get(3).getTaskName());
        
        // Sort
        testManager2.setSortingStyle(3);
        
        // Remember, collections.sort is stable.
        assertEquals(testInput1, testManager2.getTodoItemList().get(0).getTaskName());
        assertEquals(testInput3, testManager2.getTodoItemList().get(1).getTaskName());
        assertEquals(testInput4, testManager2.getTodoItemList().get(2).getTaskName());
        assertEquals(testInput2, testManager2.getTodoItemList().get(3).getTaskName());

        // Update
        Boolean[] testParameters = {false, false, false, true, true};
        
        try {
            testManager2.updateTask(testManager2.getTodoItemList().get(2).getUUID(), testParameters, null, null, null, TodoItem.HIGH, true);
        } catch (Exception e) {
            fail();
        }
        
        assertEquals(testInput4, testManager2.getTodoItemList().get(0).getTaskName());
        assertEquals(testInput1, testManager2.getTodoItemList().get(1).getTaskName());
        assertEquals(testInput3, testManager2.getTodoItemList().get(2).getTaskName());
        assertEquals(testInput2, testManager2.getTodoItemList().get(3).getTaskName());
        
        // Delete
        try {
            testManager2.deleteTask(testManager2.getTodoItemList().get(1).getUUID());
        } catch (Exception e) {
            fail();
        }
        
        assertEquals(3, testManager2.countTasks());
        assertEquals(testInput4, testManager2.getTodoItemList().get(0).getTaskName());
        assertEquals(testInput3, testManager2.getTodoItemList().get(1).getTaskName());
        assertEquals(testInput2, testManager2.getTodoItemList().get(2).getTaskName());
        
        // Clear
        try {
            testManager2.clearTasks();
        } catch (Exception e) {
            fail();
        }
        
        ModelManager testManager3;
        try {
            testManager3 = new ModelManager();
        } catch (Exception e) {
            fail();
            return;
        }
        
        assertEquals("testDirectory/watdo.json", testManager3.getFullFileName());
        assertEquals(0, testManager3.countTasks());
        
        try {
            testManager3.changeSettings("", null, null);
        } catch (Exception e) {
            fail();
        }
    }
    
    // Test for extra values in changeSettings
    @Test
    public void testChangeSettings() {
        FileStorage testStorage = new FileStorage();

        // Saves old working directory settings 
        try {
            testStorage.loadSettings();
        } catch (Exception e) {
            fail();
        }
        
        String tempFileDirectory = testStorage.getFileDirectory();
        Boolean tempRandomColorsEnabled = testStorage.areRandomColorsEnabled();
        Boolean tempNotificationsEnabled = testStorage.areNotificationsEnabled();

        // Tests for correct use case
        try {
            testStorage.changeSettings("testDirectory", false, true);
            testStorage.loadSettings();
        } catch (Exception e) {
            fail();
        }
        
        assertEquals("testDirectory/", testStorage.getFileDirectory());
        assertEquals(false, testStorage.areRandomColorsEnabled());
        assertEquals(true, testStorage.areNotificationsEnabled());
        
        // Revert back to working directory
        try {
            testStorage.changeSettings(tempFileDirectory, tempRandomColorsEnabled, tempNotificationsEnabled);
        } catch (Exception e) {
            fail();
        }
    }
}