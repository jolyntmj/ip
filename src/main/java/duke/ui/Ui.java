package duke.ui;
import java.util.Scanner;

import duke.task.Task;
import duke.task.TaskList;

/**
 * Handles all user interaction (reading input and printing output).
 */
public class Ui {

    private final Scanner input;

    /**
     * Constructs a {@code Ui} that reads user input from standard input.
     */
    public Ui() {
        this.input = new Scanner(System.in);
    }

    /**
     * Reads a command from the user.
     *
     * @return The trimmed command string entered by the user.
     */
    public String readCommand() {
        return input.nextLine().trim();
    }

    /**
     * Prints a horizontal divider line.
     */
    public void printLine() {
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Prints the greeting message shown at the start of the program.
     */
    public void printGreeting() {
        printLine();
        System.out.println("Hello! I am sealriously.");
        System.out.println(
                "         /\\  /\\\n" +
                "        /  \\\\/  \\\n" +
                "     .----------------.\n" +
                "    |                  |\n" +
                "    |   (  •́  ω  •̀ )   |\n" +
                "     '----------------'\n"
        );
        System.out.println("What's going on?");
        printLine();
    }    

    /**
     * Prints the goodbye message shown before exiting.
     */
    public void printGoodbye() {
        printLine();
        System.out.println("Bye. See you soon!");
        printLine();
    }

    /**
     * Prints a warning indicating saved tasks could not be loaded.
     */
    public void printLoadingError() {
        printLine();
        System.out.println("Warning: Could not load saved tasks. Starting fresh");
        printLine();
    }

    /**
     * Prints an error message wrapped by divider lines.
     *
     * @param message Error message to display.
     */
    public void printError(String message) {
        printLine();
        System.out.println(message);
        printLine();
    }

    /**
     * Prints output after adding a task.
     *
     * @param task The task that was added.
     * @param size Current number of tasks before adding the new one.
     */
    public void printAdded(Task task, int size) {
        printLine();
        System.out.println("Alright. Added to task(s)");
        System.out.println("Please Check:");
        System.out.println(task);
        System.out.println("REMINDER: " + (size+1) + " pending task(s). Please complete it soon. Good Luck!");
        printLine();
    }

     /**
     * Prints output after deleting a task.
     *
     * @param task The task that was deleted.
     * @param size Current number of tasks before deletion.
     */
    public void printDeleted(Task task, int size) {
        printLine();
        System.out.println("Alright. Tasked removed.");
        System.out.println("Please check:");
        System.out.println(" " + task);
        System.out.println("REMINDER: " + (size+1) + " pending task(s). Please complete it soon. Good Luck!");
        printLine();
    }

    /**
     * Prints output after marking a task as completed.
     *
     * @param t The task that was marked as done.
     */
    public void printMark(Task t) {
        printLine();
        System.out.println("Good job for completing! I will mark it as done.");
        System.out.println("Please check:");
        System.out.println("[" + t.getStatusIcon() + "] " + t.toString());
        printLine();
    }

    /**
     * Prints all tasks in the given task list.
     *
     * @param tasks The task list to display.
     */
    public void printList(TaskList tasks) {
        printLine();
        if (tasks.isEmpty()) {
            System.out.println("No tasks in your list.");
        } else {
            for (int i=0; i < tasks.size(); i++ ) {
                Task t = tasks.get(i);
                System.out.println((i + 1) + ". " + t);
            }
        printLine();
        }
    }

    /**
     * Prints all tasks whose descriptions match the given keyword.
     * If no matching tasks are found, an appropriate message is shown.
     *
     * @param tasks The task list to search through.
     * @param match The keyword used to match task descriptions.
     */
    public void printFind(TaskList tasks, String match) {
        System.out.println("Here are the matching tasks in your list:");
        int count=0;
        for (int i=0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase().contains(match)) {
                System.out.println(tasks.get(i));
                count++;
            } 
        }

        if (count==0) {
            System.out.println("No matching task found!");
        }
    }
}
