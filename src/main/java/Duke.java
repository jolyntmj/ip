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
            if (input.isEmpty()) {
                System.out.println("Please enter a command.");
                return;
            }
            
            handleInput(input);
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

    public void handleInput(String input) {

        if (!input.equalsIgnoreCase("list")) {
            if (input.toLowerCase().startsWith("todo")) {
                todo(input);
            }

            if (input.toLowerCase().startsWith("deadline")) {
                deadline(input);
            }

            if (input.toLowerCase().startsWith("event")) {
                event(input);
            }

            if (input.toLowerCase().startsWith("mark ")) {
                mark(input);
            } 

        } else {
            printList();
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

    public void mark(String input) {

        String[] parts = input.split(" ", 2);
        int index;
        
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            System.out.println("Please specify a task number.");
            return;
        }

        try {
            index = Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Task number must be a number.");
            return;
        }
    
        if (index < 0 || index >= tasks.size()) {
            System.out.println("That task number does not exist.");
            return;
        }

        Task t = tasks.get(index);
        t.done();

        printLine();
        System.out.println("Good job for completing! I will mark it as done.");
        System.out.println("Please check:");
        System.out.println("[" + t.getStatusIcon() + "] " + t.getDescription());
        printLine();

    }

    public void todo(String input) {
        String[] parts = input.split(" ", 2);
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

    public void deadline(String input) {
        String remainder = input.substring(9).trim();

        String[] parts = remainder.split(" /by", 2);
        String description = parts[0];
        String by = parts[1];

        printLine();
        System.out.println("Alright. Added to task(s)");
        System.out.println("Please Check:");
        System.out.println(new Deadline(description, by).toString());
        int size = tasks.size();
        System.out.println("REMINDER: " + (size+1) + " pending task(s). Please complete it soon. Good Luck!");
        printLine();

        tasks.add(new Deadline(description, by));
    }

    public void event(String input) {
        String remainder = input.substring(5).trim();

        String[] parts = remainder.split(" /from", 2);
        String description = parts[0];
        String firstSplit = parts[1];

        String[] secondParting = firstSplit.split(" /to", 2);
        String start = secondParting[0];
        String end = secondParting[1];

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
