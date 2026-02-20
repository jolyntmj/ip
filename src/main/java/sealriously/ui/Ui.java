package sealriously.ui;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import sealriously.task.Task;
import sealriously.task.TaskList;

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
     * Prints the greeting message shown at the start of the program.
     */
    public String printGreeting() {
        return "Hello! I am sealriously.\n"
            + "         /\\  /\\\n"
            + "        /  \\\\/  \\\n"
            + "     .----------------.\n"
            + "    |                  |\n"
            + "    |   (  •́  ω  •̀ )   |\n"
            + "     '----------------'\n"
            + "What's going on?\n";
    }

    /**
     * Prints the goodbye message shown before exiting.
     */
    public String printGoodbye() {
        return "Bye. See you soon!";
    }

    /**
     * Prints a warning indicating saved tasks could not be loaded.
     */
    public String printLoadingError() {
        return "Warning: Could not load saved tasks.\nStarting fresh";
    }

    /**
     * Prints an error message wrapped by divider lines.
     *
     * @param message Error message to display.
     */
    public String printError(String message) {
        return message;
    }

    /**
     * Prints output after adding a task.
     *
     * @param task The task that was added.
     * @param size Current number of tasks before adding the new one.
     */
    public String printAdded(Task task, int size) {
        return "Alright. Added to task(s)\n"
            + "Please Check:\n"
            + task + "\n"
            + "REMINDER: " + size + " pending task(s).\nPlease complete it soon. Good Luck!";
    }

    /**
     * Prints output after deleting a task.
     *
     * @param task The task that was deleted.
     * @param size Current number of tasks before deletion.
     */
    public String printDeleted(Task task, int size) {
        return "Alright. Tasked removed.\n"
            + "Please check:\n"
            + " " + task + "\n"
            + "REMINDER: " + size + " pending task(s).\nPlease complete it soon. Good Luck!";
    }

    /**
     * Prints output after marking a task as completed.
     *
     * @param t The task that was marked as done.
     */
    public String printMark(Task t, int index) {
        return "Good job for completing! I will mark it as done.\n"
            + "Please check:\n"
            + (index + 1) + ". " + t.toString();
    }

    /**
     * Prints all tasks in the given task list.
     *
     * @param tasks The task list to display.
     */
    public String printList(TaskList tasks) {
        if (tasks.isEmpty()) {
            return "No tasks in your list.";
        }

        return IntStream.range(0, tasks.size())
            .mapToObj(i -> (i + 1) + ". " + formatTask(tasks.get(i)))
            .collect(Collectors.joining("\n"));

    }

    /**
     * Prints all tasks whose descriptions match the given keyword.
     * If no matching tasks are found, an appropriate message is shown.
     *
     * @param tasks The task list to search through.
     * @param match The keyword used to match task descriptions.
     */
    public String printFind(TaskList tasks, String match) {
        String matches = tasks.getTasks().stream()
                .filter(t -> t.getDescription().toLowerCase().contains(match.toLowerCase()))
                .map(Task::toString)
                .collect(Collectors.joining("\n"));

        if (matches.isEmpty()) {
            return "Here are the matching tasks in your list:\nNo matching task found!";
        }

        return "Here are the matching tasks in your list:\n" + matches;
    }

    /**
     * Returns a confirmation message after tagging a task.
     *
     * @param task  The updated task.
     * @param index Zero-based index of the task.
     * @param tag   Tag applied to the task.
     * @return User-facing confirmation message.
     */
    public String printTagged(Task task, int index, String tag) {
        return "Tagged task " + (index + 1) + " with " + tag + ":\n"
                + (index + 1) + ". " + formatTask(task);
    }

    /**
     * Formats a task into a user-facing string for UI output.
     *
     * @param task Task to format.
     * @return Formatted task string.
     */
    private String formatTask(Task task) {
        return task.toString() + task.tagsToDisplay();
    }

}
