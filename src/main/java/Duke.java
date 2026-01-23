import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Duke {
    private List<Task> tasks = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Duke().run();
    }

    public void run() {
        printLine();
        printName();
        printGreeting();

        String input = scanner.nextLine().trim();
        
        while(!input.equalsIgnoreCase("bye")) {
            // if (input.isEmpty()) {
            //     System.out.println("Please enter a command.");
            //     return;
            // }

            try {
                handleInput(input);
            } catch (DukeException e) {
                printLine();
                System.out.println(e.getMessage());
                printLine();
            }
            
            // handleInput(input);
            input = scanner.nextLine().trim();
        } 
        printLine();
        printGoodbye();
        printLine();
    }

    private void printLine() {
        System.out.println("-----------------------------------------------------------------");
    }

    private void printName() {
        System.out.println("Hello! I am sealriously");
    }

    private void printGreeting() {
        System.out.println("What can I do for you?");
    }

    private void printGoodbye() {
        System.out.println("Bye. See you soon!");
    }

    public void handleInput(String input) throws DukeException {

        if (input.isEmpty()) {
            throw new DukeException("Please enter a command.");
        } else if (input.equalsIgnoreCase("list")) {
            printList();
        } else if (input.toLowerCase().startsWith("todo")) {
            todo(input);
        } else if (input.toLowerCase().startsWith("deadline")) {
            deadline(input);
        } else if (input.toLowerCase().startsWith("event")) {
            event(input);
        } else if (input.toLowerCase().startsWith("mark ")) {
            mark(input);
        } else {
            throw new DukeException("Unknown command. Please enter the correct command. Eg. event project meeting /from Mon 2pm /to 4pm");
        }
    }

    public void printList() { 
        printLine();                              
        for(int i=0; i < tasks.size(); i++ ) {
            Task t = tasks.get(i);
            System.out.println((i+1) + ". " + t.getType() + "[" + t.getStatusIcon() + "] " + t.getDescription());
        }
        printLine();
    }
    public void split(String input) {
        
    }

    public void mark(String input) throws DukeException {

        String[] parts = input.split(" ", 2);

        if (parts.length == 1) {
            throw new DukeException("Please enter a task number to mark.");
        }

        int index;
        
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new DukeException("Please specify a task number to mark.");
        }

        try {
            index = Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a number.");
        }
    
        if (index < 0 || index >= tasks.size()) {
            throw new DukeException("That task number does not exist.");
        }

        Task t = tasks.get(index);
        t.done();

        printLine();
        System.out.println("Good job for completing! I will mark it as done.");
        System.out.println("Please check:");
        System.out.println("[" + t.getStatusIcon() + "] " + t.getDescription());
        printLine();

    }

    public void todo(String input) throws DukeException {
        String[] parts = input.split(" ", 2);

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new DukeException("The description of a todo cannot be empty!");
        }

        String description = parts[1];

        printLine();
        System.out.println("Alright. Added to task(s)");
        System.out.println("Please Check:");
        System.out.println(new Todo(description).toString());
        int size = tasks.size();
        System.out.println("REMINDER: " + (size+1) + " pending task(s). Please complete it soon. Good Luck!");
        printLine();

        tasks.add(new Todo(description));
    }

    public void deadline(String input) throws DukeException {
        String remainder = input.substring(8).trim();

        if (remainder.isEmpty()) {
            throw new DukeException("The description and /by of a deadline cannot be empty!");
        }
    

        String[] parts = remainder.split("/by", 2);

        if (parts.length < 2) {
            throw new DukeException("The /by of a deadline cannot be empty!");
        }

        String description = parts[0];
        String by = parts[1];

        if (description.isEmpty()) {
            throw new DukeException("The description of a deadline cannot be empty!");
        }

        if (by.isEmpty()) {
            throw new DukeException("The /by of a deadline cannot be empty!");
        }

        printLine();
        System.out.println("Alright. Added to task(s)");
        System.out.println("Please Check:");
        System.out.println(new Deadline(description, by).toString());
        int size = tasks.size();
        System.out.println("REMINDER: " + (size+1) + " pending task(s). Please complete it soon. Good Luck!");
        printLine();

        tasks.add(new Deadline(description, by));
    }

    public void event(String input) throws DukeException {
        String remainder = input.substring(5).trim();

        if (remainder.isEmpty()) {
            throw new DukeException("The description, /from and /to of an event cannot be empty!");
        }

        if (remainder.startsWith("/from")) {
            if (!remainder.contains("/to")) {
                throw new DukeException("The description and /to of an event cannot be empty!");
            }
        }

        if (remainder.startsWith("/to")) {
            throw new DukeException("The description and /from of an event cannot be empty!");
        }

        String[] parts = remainder.split("/from", 2);

        if (parts.length < 2) {
            if (remainder.contains("/to")) {
                throw new DukeException("The /from of an event cannot be empty!");
            } else {
                throw new DukeException("The /from and /to of an event cannot be empty!");
            }
        }

        String description = parts[0].trim();
        String firstSplit = parts[1].trim();

        if (description.isEmpty()) {
            throw new DukeException("The description of an event cannot be empty!");
        }

        String[] secondParting = firstSplit.split("/to", 2);

        if (secondParting.length < 2) {
            throw new DukeException("The /to of an event cannot be empty!");
        }

        String start = secondParting[0].trim();
        String end = secondParting[1].trim();

        if (start.isEmpty()) {
            throw new DukeException("The /from of an event cannot be empty!");
        }
    
        if (end.isEmpty()) {
            throw new DukeException("The /to of an event cannot be empty!");
        }

        printLine();
        System.out.println("Alright. Added to task(s)");
        System.out.println("Please Check:");
        System.out.println(new Event(description, start, end).toString());
        int size = tasks.size(); 
        System.out.println("REMINDER: " + (size+1) + " pending task(s). Please complete it soon. Good Luck!");
        printLine();

        tasks.add(new Event(description, start, end));
    }
}
