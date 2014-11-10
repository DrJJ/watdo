package tests.units;
//@author A0114914L

import java.io.IOException;

import app.controllers.ActionController;
import app.controllers.CommandParser;
import app.controllers.UndoController;
import app.helpers.CommandObject;
import app.model.ModelManager;
import static org.junit.Assert.*;

import org.junit.Test;

public class ActionControllerUnits {
    ActionController actionTest;
    UndoController undoControllerTest = UndoController.getUndoController();
    CommandParser parserTest = new CommandParser();
    CommandObject commandObjectTest = new CommandObject();
    
    @Test
    public void canAddCorrectly() {
        ModelManager manager;
        try {
            manager = new ModelManager();
        } catch (Exception e) {
            fail();
            return;
        }
        
        actionTest = new ActionController(manager);
        actionTest.setUndoController(undoControllerTest);
        
        commandObjectTest = parserTest.parseCommand("saveto testDirectory");
        actionTest.changeSaveLocation(commandObjectTest);
        commandObjectTest = parserTest.parseCommand("clear");
        actionTest.clear(commandObjectTest);
        commandObjectTest = parserTest.parseCommand("add task 1");
        actionTest.add(commandObjectTest);
        commandObjectTest = parserTest.parseCommand("add *&$(*&$)(@ due yesterday");
        actionTest.add(commandObjectTest);
        commandObjectTest = parserTest.parseCommand("add 34987314 due today");
        actionTest.add(commandObjectTest);
        commandObjectTest = parserTest.parseCommand("add hello world due tomorrow");
        actionTest.add(commandObjectTest);
        assertEquals(4, manager.countTasks());
        
        commandObjectTest = parserTest.parseCommand("clear");
        actionTest.clear(commandObjectTest);
        assertEquals(0, manager.countTasks());
        
        commandObjectTest = parserTest.parseCommand("saveto .");
        actionTest.changeSaveLocation(commandObjectTest);
        assertEquals("./", manager.getFileDirectory());
    }

}
