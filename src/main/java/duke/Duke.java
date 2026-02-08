package duke;

import duke.command.CommandType;
import duke.exception.DukeException;
import duke.parser.Parser;
import duke.storage.Storage;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.Todo;
import duke.ui.Ui;
import javafx.scene.web.HTMLEditorSkin.Command;

/**
 * Main entry point of the Duke chatbot application.
 * Duke coordinates the UI, storage, task list and parser to execute user commands.
 */
public class Duke {
    private TaskList tasks;
    private Storage storage;
    private Ui ui;
    private Parser parser;

    /**
     * Constructs a Duke instance and attempts to load saved tasks from the given file path.
     *
     * @param filePath File path used for loading and saving tasks.
     */
    public Duke(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.parser = new Parser();

        TaskList loaded;
        storage = new Storage(filePath);
        try {
            loaded = new TaskList(storage.load());
        } catch (DukeException e) {
            ui.printLoadingError();
            loaded = new TaskList();
        }
        this.tasks = loaded;
    }

    /**
     * Starts the Duke chatbot.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        new Duke("./data/duke.txt").run();
    }


    /**
     * Runs the main input processing loop until the user types "bye".
     */
    public void run() {
        ui.printGreeting();

        while (true) {

            String input = ui.readCommand();

            if (input.isEmpty()) {
                ui.printError("Please enter a command.");
                continue;
            }

            try {
                handleInput(input);
            } catch (DukeException e) {
                ui.printError(e.getMessage());
            }

            if (input.equalsIgnoreCase("bye")) {
                ui.printGoodbye();
                break;
            }
        }
    }

    /**
     * Determines the command type and dispatches it to the appropriate handler.
     *
     * @param input Full user input string.
     * @throws DukeException If the command is unknown or contains invalid data.
     */
    public String handleInput(String input) throws DukeException {

        CommandType command = parser.parseCommandType(input);

        switch (command) {
        case LIST -> { 
            return ui.printList(tasks); 
        }
        case TODO -> { 
            return todo(input); 
        }
        case DEADLINE -> { 
            return deadline(input); 
        }
        case EVENT -> { 
            return event(input); 
        }
        case MARK -> { 
            return mark(input); 
        }
        case DELETE -> { 
            return delete(input); 
        }
        case FIND -> { 
            return find(input); 
        }
        case BYE -> { 
            return "";/* do nothing here; your while loop exits on bye */ 
        }
        default -> throw new DukeException("Unknown command.\nPlease enter the correct command.");
    }
    }


    /**
     * Marks the specified task as done.
     *
     * @param input User input string containing the task number.
     * @throws DukeException If the task number is invalid or out of range.
     */
    public String mark(String input) throws DukeException {
        int index = parser.parseIndex(input);

        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("That task number does not exist.");
        }

        tasks.mark(index);
        storage.save(tasks);

        return ui.printMark(tasks.get(index), index);

    }

    /**
     * Adds a todo task based on user input.
     *
     * @param input User input string.
     * @throws DukeException If the description is missing.
     */
    public String todo(String input) throws DukeException {

        String description = parser.parseTodo(input);

        Task task = new Todo(description);

        tasks.add(task);
        storage.save(tasks);

        return  ui.printAdded(task, tasks.size());
    }

    /**
     * Adds a deadline task based on user input.
     *
     * @param input User input string.
     * @throws DukeException If the deadline format is invalid.
     */
    public String deadline(String input) throws DukeException {
        Deadline deadline = parser.parseDeadline(input);
        tasks.add(deadline);
        storage.save(tasks);
        return ui.printAdded(deadline, tasks.size());
    }

    /**
     * Adds an event task based on user input.
     *
     * @param input User input string.
     * @throws DukeException If the event format is invalid.
     */
    public String event(String input) throws DukeException {
        Event event = parser.parseEvent(input);
        tasks.add(event);
        storage.save(tasks);
        return ui.printAdded(event, tasks.size());
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param input User input string containing the task number.
     * @throws DukeException If the task number is invalid or out of range.
     */
    public String delete(String input) throws DukeException {
        int index = parser.parseIndex(input);
        tasks.delete(index);
        storage.save(tasks);
        return ui.printDeleted(tasks.get(index), tasks.size());
    }

    /**
     * Finds and displays tasks whose descriptions contain the given keyword.
     *
     * @param input The full user input containing the {@code find} command.
     * @throws DukeException If the find command does not contain a keyword.
     */
    public String find(String input) throws DukeException {
        String match = parser.parseDescription(input).toLowerCase();
        return ui.printFind(tasks, match);
    }

    // public static void main(String[] args) {
    //     System.out.println("Hello!");
    // }

    /**
     * Generates a response for the user's chat message.
     */
    // public String getResponse(String input) {
    //     return "Duke heard: " + input;
    // }

    public Duke() {
        this("./data/duke.txt");
    }

    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return ui.printError("Please enter a command.");
        }
    
        try {
            return handleInput(input.trim());
        } catch (DukeException e) {
            return ui.printError(e.getMessage());
        }
    }

    public String getGreeting() {
        return ui.printGreeting();
    }
    

}
