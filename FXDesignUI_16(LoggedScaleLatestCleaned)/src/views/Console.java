package views;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import org.apache.log4j.Logger;

import utils.Constance;
import utils.DrawingUtils;

public class Console {
	
	static final Logger logger = Logger.getLogger(Console.class);
	
	private TextArea textArea;
	private TextField textField;
	
    protected final List<String> history = new ArrayList<>();
    protected int historyPointer = 0;

    private Consumer<String> onMessageReceivedHandler;

    public Console(TextArea tArea, TextField tField) {
    	textArea = tArea;
    	textField = tField;
    	
        textArea.setEditable(false);
        textField.requestFocus();
        textField.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            switch (keyEvent.getCode()) {
            
                case ENTER:
                    String text = textField.getText();
                    logger.info(text);
                    textArea.appendText(Constance.CURSOR_DUB + text + System.lineSeparator());
                    history.add(text);
                    historyPointer++;
                    if (onMessageReceivedHandler != null) {
                        onMessageReceivedHandler.accept(text);
                    }
                    textField.clear();
                    break;
                    
                case UP:
                    if (historyPointer == 0) {
                        break;
                    }
                    historyPointer--;
                    DrawingUtils.runSafe(() -> {
                        textField.setText(history.get(historyPointer));
                        textField.selectAll();
                    });
                    break;
                    
                case DOWN:
                    if (historyPointer == history.size() - 1) {
                        break;
                    }
                    historyPointer++;
                    DrawingUtils.runSafe(() -> {
                        textField.setText(history.get(historyPointer));
                        textField.selectAll();
                    });
                    break;
                    
                default:
                    break;
            }
        });
    }

    public void setOnMessageReceivedHandler(final Consumer<String> onMessageReceivedHandler) {
        this.onMessageReceivedHandler = onMessageReceivedHandler;
    }

    public void clear() {
    	DrawingUtils.runSafe(() -> textArea.clear());
    }

    public void print(final String text) {
        Objects.requireNonNull(text, "text");
        DrawingUtils.runSafe(() -> textArea.appendText(text));
        logger.info(text);
    }

    public void println(final String text) {
        Objects.requireNonNull(text, "text");
        DrawingUtils.runSafe(() -> textArea.appendText(Constance.CURSOR + text + System.lineSeparator()));
        logger.info(text);
    }

    public void println() {
    	DrawingUtils.runSafe(() -> textArea.appendText(Constance.CURSOR+ System.lineSeparator()));
    }
}