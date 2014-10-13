package app.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.helpers.Keyword;
import app.model.TodoItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 * Created by jolly on 24/9/14.
 *
 * computeHighlighting code taken from:
 * https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywords.java
 * will rewrite/refactor later
 */
public class InputFieldController {

    private String lastCommand;
    private StyleClassedTextArea inputField;

    private RootViewController rootViewController;

    private final String[] KEYWORDS = new String[] {
        "add", "delete", "display", "clear", "exit", "search", "update", "help", "settings", "start", "end"
    };

    private final Pattern KEYWORD_PATTERN = Pattern.compile("\\b(" + String.join("|", KEYWORDS) + ")\\b");

    public InputFieldController() {
        inputField = new StyleClassedTextArea();
        inputField.setPrefHeight(100);
        inputField.getStylesheets().add("app/stylesheets/inputField.css");
        inputField.getStyleClass().add("input-field");
        inputField.setWrapText(true);

        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
//            inputField.setStyle(0, inputField.getLength(), "-fx-fill: black;");
            inputField.setStyleSpans(0, computeHighlighting(newValue));
//            inputField.setStyleSpans(0, keywordDetection(newValue));
            if (inputField.getText().startsWith("search ")) {
                String query = inputField.getText().substring(7);
                System.out.println("query: " + query);
                ArrayList<TodoItem> results =
                        rootViewController.getMainApp().getCommandController().instantSearch(query);
                rootViewController.getMainApp().getCommandController().updateView(results);
                if (results.isEmpty()) {
                    rootViewController.getTaskListViewController().setEmptySearchPlaceholder();
                }
            } else {
                rootViewController.getMainApp().getCommandController().updateView();
                rootViewController.getTaskListViewController().setUserGuidePlaceholder();
            }
        });

        inputField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if (!inputField.getText().equals("")) {
                    lastCommand = inputField.getText();
                    inputField.clear();
                    rootViewController.getMainApp().getCommandController().parseCommand(lastCommand);
                    rootViewController.getMainApp().getCommandController().updateView();
                }
            }
//            else if (event.getCode() == KeyCode.TAB) {
//                event.consume();
//                System.out.println("TAB: \"" + inputField.getText() + "\"");
//            }
        });
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = KEYWORD_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private StyleSpans<Collection<String>> keywordDetection(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        // pass string to commandcontroller, commandcontroller returns arraylist of keywords
        // for now this is just a dummay arraylist of keywords
        ArrayList<Keyword> keywords = new ArrayList<Keyword>();

        if (text.length() >= 3) {
            keywords.add(new Keyword(0, 2));
        }

        if (text.length() >= 5) {
            keywords.add(new Keyword(3, 4));
        }

        if (text.length() >= 10) {
            keywords.add(new Keyword(7, 9));
        }

        int lastWordEnd = 0;
        for (Keyword keyword : keywords) {
            spansBuilder.add(Collections.emptyList(), keyword.getStartIndex() - lastWordEnd);
            spansBuilder.add(Collections.singleton("keyword"), keyword.getEndIndex() - keyword.getStartIndex() + 1);
            lastWordEnd = keyword.getEndIndex() + 1;
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastWordEnd);
        return spansBuilder.create();
    }


    public StyleClassedTextArea getInputField() {
        return inputField;
    }

    public void setRootViewController(RootViewController rootViewController) {
        this.rootViewController = rootViewController;
    }
}
