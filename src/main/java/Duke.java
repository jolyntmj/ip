public class Duke {
    private TaskList tasks;
    private Storage storage;
    private Ui ui;
    private Parser parser;

    public Duke(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.parser = new Parser();

        TaskList loaded;
        storage = new Storage(filePath);
            try {
                loaded =  new TaskList(storage.load());
            } catch (DukeException e) {
                ui.printLoadingError();
                loaded = new TaskList();
            }
            this.tasks = loaded;
    }

    public static void main(String[] args) {
        new Duke("./data/duke.txt").run();
    }

    public void run() {
        
        ui.printGreeting();
        
        while(true) {

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

    public void handleInput(String input) throws DukeException {

        CommandType command = parser.parseCommandType(input);

        switch (command) {
        case LIST -> ui.printList(tasks);
        case TODO -> todo(input); 
        case DEADLINE -> deadline(input);
        case EVENT -> event(input);
        case MARK -> mark(input); 
        case DELETE -> delete(input);
        case BYE -> { /* do nothing here; your while-loop exits on bye */ }
        case UNKNOWN -> throw new DukeException(
                "Unknown command. Please enter the correct command. Eg. event project meeting /from Mon 2pm /to 4pm");
        }
    }

    public void mark(String input) throws DukeException {
        int index = parser.parseIndex(input);
    
        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("That task number does not exist.");
        }

        tasks.mark(index);
        storage.save(tasks);

        ui.printMark(tasks.get(index));

    }

    public void todo(String input) throws DukeException {

        String description = parser.parseTodo(input);

        Task task = new Todo(description);

        ui.printAdded(task, tasks.size());

        tasks.add(task);
        storage.save(tasks);
    }

    public void deadline(String input) throws DukeException {
        
        Deadline deadline = parser.parseDeadline(input);
        ui.printAdded(deadline, tasks.size());
        tasks.add(deadline);
        storage.save(tasks);
    }

    public void event(String input) throws DukeException {
        Event event = parser.parseEvent(input);
        ui.printAdded(event, tasks.size());
        tasks.add(event);
        storage.save(tasks);
    }

    public void delete(String input) throws DukeException {
        int index = parser.parseIndex(input);
        tasks.delete(index);
        storage.save(tasks);
        ui.printDeleted(tasks.get(index), index);
    }
}
