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
    public void handleInput(String input) throws DukeException {

        CommandType command = parser.parseCommandType(input);

        switch (command) {
        case LIST -> ui.printList(tasks);
        case TODO -> todo(input);
        case DEADLINE -> deadline(input);
        case EVENT -> event(input);
        case MARK -> mark(input);
        case DELETE -> delete(input);
        case FIND -> find(input);
        case BYE -> { /* do nothing here; your while loop exits on bye */ }
        case UNKNOWN -> throw new DukeException(
                "Unknown command. Please enter the correct command.");
        default -> throw new DukeException(
            "Unknown command. Please enter the correct command.");
        }
    }

    /**
     * Marks the specified task as done.
     *
     * @param input User input string containing the task number.
     * @throws DukeException If the task number is invalid or out of range.
     */
    public void mark(String input) throws DukeException {
        int index = parser.parseIndex(input);

        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("That task number does not exist.");
        }

        tasks.mark(index);
        storage.save(tasks);

        ui.printMark(tasks.get(index));

    }

    /**
     * Adds a todo task based on user input.
     *
     * @param input User input string.
     * @throws DukeException If the description is missing.
     */
    public void todo(String input) throws DukeException {

        String description = parser.parseTodo(input);

        Task task = new Todo(description);

        ui.printAdded(task, tasks.size());

        tasks.add(task);
        storage.save(tasks);
    }

    /**
     * Adds a deadline task based on user input.
     *
     * @param input User input string.
     * @throws DukeException If the deadline format is invalid.
     */
    public void deadline(String input) throws DukeException {
        Deadline deadline = parser.parseDeadline(input);
        ui.printAdded(deadline, tasks.size());
        tasks.add(deadline);
        storage.save(tasks);
    }

    /**
     * Adds an event task based on user input.
     *
     * @param input User input string.
     * @throws DukeException If the event format is invalid.
     */
    public void event(String input) throws DukeException {
        Event event = parser.parseEvent(input);
        ui.printAdded(event, tasks.size());
        tasks.add(event);
        storage.save(tasks);
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param input User input string containing the task number.
     * @throws DukeException If the task number is invalid or out of range.
     */
    public void delete(String input) throws DukeException {
        int index = parser.parseIndex(input);
        tasks.delete(index);
        storage.save(tasks);
        ui.printDeleted(tasks.get(index), index);
    }

    /**
     * Finds and displays tasks whose descriptions contain the given keyword.
     *
     * @param input The full user input containing the {@code find} command.
     * @throws DukeException If the find command does not contain a keyword.
     */
    public void find(String input) throws DukeException {
        String match = parser.parseDescription(input).toLowerCase();
        ui.printFind(tasks, match);
    }
}
