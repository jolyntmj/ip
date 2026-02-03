package duke.ui;
import java.util.Scanner;

import duke.task.Task;
import duke.task.TaskList;

public class Ui {

    private final Scanner input;

    public Ui() {
        this.input = new Scanner(System.in);
    }

    public String readCommand() {
        return input.nextLine().trim();
    }

    public void printLine() {
        System.out.println("-----------------------------------------------------------------");
    }

    public void printGreeting() {
        printLine();
        System.out.println("Hello! I am sealriously");
        System.out.println("What can I do for you?");
        printLine();
    }

    public void printGoodbye() {
        printLine();
        System.out.println("Bye. See you soon!");
        printLine();
    }

    public void printLoadingError() {
        printLine();
        System.out.println("Warning: Could not load saved tasks. Starting fresh");
        printLine();
    }

    public void printError(String message) {
        printLine();
        System.out.println(message);
        printLine();
    }

    public void printAdded(Task task, int size) {
        printLine();
        System.out.println("Alright. Added to task(s)");
        System.out.println("Please Check:");
        System.out.println(task.toString());
        System.out.println("REMINDER: " + (size+1) + " pending task(s). Please complete it soon. Good Luck!");
        printLine();
    }

    public void printDeleted(Task task, int size) {
        printLine();
        System.out.println("Alright. Tasked removed.");
        System.out.println("Please check:");
        System.out.println(" " + task);
        System.out.println("REMINDER: " + (size+1) + " pending task(s). Please complete it soon. Good Luck!");
        printLine();
    }

    public void printMark(Task t) {
        printLine();
        System.out.println("Good job for completing! I will mark it as done.");
        System.out.println("Please check:");
        System.out.println("[" + t.getStatusIcon() + "] " + t.getDescription());
        printLine();
    }
<<<<<<< HEAD
    
    public void printList(TaskList tasks) { 
        printLine();             
=======

    /**
     * Prints all tasks in the given task list.
     *
     * @param tasks The task list to display.
     */
    public void printList(TaskList tasks) {
        printLine();
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
        if (tasks.isEmpty()) {
            System.out.println("No tasks in your list.");
        } else {
            for (int i=0; i < tasks.size(); i++ ) {
                Task t = tasks.get(i);
                System.out.println((i + 1) + ". " + t.toString());
            }
        printLine();
        }
    }
}
