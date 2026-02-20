package sealriously;

import java.util.List;

import sealriously.command.CommandType;
import sealriously.exception.SealriouslyException;
import sealriously.parser.Parser;
import sealriously.storage.Storage;
import sealriously.task.Deadline;
import sealriously.task.Event;
import sealriously.task.Task;
import sealriously.task.TaskList;
import sealriously.task.Todo;
import sealriously.ui.Ui;

/**
 * Main entry point of the Sealriously chatbot application.
 * Sealriously coordinates the UI, storage, task list and parser to execute user commands.
 */
public class Sealriously {
    private TaskList tasks;
    private Storage storage;
    private Ui ui;
    private Parser parser;

    /**
     * Constructs a Sealriously instance and attempts to load saved tasks from the given file path.
     *
     * @param filePath File path used for loading and saving tasks.
     */
    public Sealriously(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.parser = new Parser();

        TaskList loaded;

        try {
            List<Task> loadedTasks = storage.load();

            if (storage.lastLoadHadCorruptedLines()) {
                ui.printError("Some saved tasks were corrupted and skipped.");
            }

            loaded = new TaskList(loadedTasks);

        } catch (SealriouslyException e) {
            ui.printLoadingError();
            loaded = new TaskList();
        }

        this.tasks = loaded;
    }


    /**
     * Starts the Sealriously chatbot.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        new Sealriously("./data/sealriously.txt").run();
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
            } catch (SealriouslyException e) {
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
     * @throws SealriouslyException If the command is unknown or contains invalid data.
     */
    public String handleInput(String input) throws SealriouslyException {

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
        case TAG -> {
            return tag(input);
        }
        case BYE -> { 
            return "";/* do nothing here; your while loop exits on bye */ 
        }
        default -> throw new SealriouslyException("Unknown command.\nPlease enter the correct command.");
    }
    }


    /**
     * Marks the specified task as done.
     *
     * @param input User input string containing the task number.
     * @throws SealriouslyException If the task number is invalid or out of range.
     */
    public String mark(String input) throws SealriouslyException {
        int index = parser.parseIndex(input);

        if (index < 0 || index >= tasks.size()) {
            throw new SealriouslyException("That task number does not exist.");
        }

        tasks.mark(index);
        storage.save(tasks);

        return ui.printMark(tasks.get(index), index);

    }

    /**
     * Adds a todo task based on user input.
     *
     * @param input User input string.
     * @throws SealriouslyException If the description is missing.
     */
    public String todo(String input) throws SealriouslyException {

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
     * @throws SealriouslyException If the deadline format is invalid.
     */
    public String deadline(String input) throws SealriouslyException {
        Deadline deadline = parser.parseDeadline(input);
        tasks.add(deadline);
        storage.save(tasks);
        return ui.printAdded(deadline, tasks.size());
    }

    /**
     * Adds an event task based on user input.
     *
     * @param input User input string.
     * @throws SealriouslyException If the event format is invalid.
     */
    public String event(String input) throws SealriouslyException {
        Event event = parser.parseEvent(input);
        tasks.add(event);
        storage.save(tasks);
        return ui.printAdded(event, tasks.size());
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param input User input string containing the task number.
     * @throws SealriouslyException If the task number is invalid or out of range.
     */
    public String delete(String input) throws SealriouslyException {
        int index = parser.parseIndex(input);
        Task deleted = tasks.delete(index);
        storage.save(tasks);
        return ui.printDeleted(deleted, tasks.size());
    }

    /**
     * Finds and displays tasks whose descriptions contain the given keyword.
     *
     * @param input The full user input containing the {@code find} command.
     * @throws SealriouslyException If the find command does not contain a keyword.
     */
    public String find(String input) throws SealriouslyException {
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
    //     return "Sealriously heard: " + input;
    // }

    /**
     * Creates a chatbot instance using the default storage file path.
     */
    public Sealriously() {
        this("./data/sealriously.txt");
    }

    /**
     * Processes user input and returns a response for display in the UI.
     * This method is the main entry point used by the GUI.
     *
     * @param input Raw user input (may be null/blank depending on caller).
     * @return Response string for the UI.
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return ui.printError("Please enter a command.");
        }
    
        try {
            return handleInput(input.trim());
        } catch (SealriouslyException e) {
            return ui.printError(e.getMessage());
        }
    }

    /**
     * Returns the greeting message shown when the application starts.
     *
     * @return Greeting message.
     */
    public String getGreeting() {
        return ui.printGreeting();
    }
    
    /**
     * Tags an existing task with a tag string.
     *
     * @param input Raw tag command input (e.g., "tag 2 #school").
     * @return Confirmation message including the updated task.
     * @throws SealriouslyException If the command format is invalid or index is out of range.
     */
    public String tag(String input) throws SealriouslyException {
        Parser.TagArgs args = parser.parseTagArgs(input);
    
        if (args.index < 0 || args.index >= tasks.size()) {
            throw new SealriouslyException("That task number does not exist.");
        }
    
        Task task = tasks.get(args.index);
        task.addTag(args.tag);
        storage.save(tasks);
    
        return ui.printTagged(task, args.index, args.tag);
    }
    

}
