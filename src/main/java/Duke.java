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
        System.out.println("--------------------------------");
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
            if (input.toLowerCase().startsWith("mark ")) {
                mark(input);
            } else {
                printLine();
                System.out.println("added: " + input);
                printLine();
                tasks.add(new Task(input));
            }
        } else {
            printList();
        }
    }

    public void printList() {                               
        for(int i=0; i < tasks.size(); i++ ) {
            Task t = tasks.get(i);
            System.out.println((i+1) + ". [" + t.getStatusIcon() + "] " + t.getDescription());
        }
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

        System.out.println("Good job for completing! I will mark it as done.");
        System.out.println("Please check:");
        System.out.println("[" + t.getStatusIcon() + "] " + t.getDescription());
        printLine();

    }
    
    public List<Task> getList() {
        return tasks;
    }
}
