package duke.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.Todo;

/**
 * Handles loading tasks from and saving tasks to a local file.
 */
public class Storage {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final String filePath;
    private final TaskSerializer serializer;

    /**
     * Constructs a {@code Storage} that reads from and writes to the given file path.
     *
     * @param filePath File path used for storing tasks.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
        this.serializer = new TaskSerializer();
    }

    /**
     * Loads tasks from the save file.
     * If the save file does not exist, it will be created and an empty list is returned.
     *
     * @return List of loaded tasks.
     * @throws DukeException If an IO error occurs while reading the file.
     */
    public List<Task> load() throws DukeException {
        File file = new File(filePath);
        List<Task> tasks = new ArrayList<>();
        boolean corruptedFound = false;

        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                file.createNewFile();
                return tasks;
            }

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                Task task = serializer.fromStorageString(line);
                if (task == null) {
                    corruptedFound = true;
                    continue;
                }

                tasks.add(task);
            }
            scanner.close();

            if (corruptedFound) {
                System.out.println("Warning: Some saved tasks were corrupted and skipped.");
            }

            return tasks;

        } catch (IOException e) {
            throw new DukeException("Error loading saved data: " + e.getMessage());
        }
    }

    /**
     * Saves the given task list to the save file.
     *
     * @param tasks Task list to save.
     * @throws DukeException If an IO error occurs while writing the file.
     */
    public void save(TaskList tasks) throws DukeException {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            FileWriter writer = new FileWriter(file);

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                writer.write(serializer.toStorageString(task));
                writer.write(System.lineSeparator());
            }

            writer.close();

        } catch (IOException e) {
            throw new DukeException("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Converts tasks to and from their storage format.
     * Format example:
     * {@code 1 | todo read book}
     * {@code 0 | deadline submit report /by 2019-12-02 1800}
     * {@code 1 | event meeting /from 2019-12-02 1800 /to 2019-12-02 2000}
     */
    private static class TaskSerializer {

        /**
         * Converts a stored line into a {@link Task}.
         *
         * @param line A single line from the save file.
         * @return Parsed task, or {@code null} if the line is corrupted.
         */
        Task fromStorageString(String line) {
            String[] parts = line.split("\\|", 2);
            if (parts.length != 2) {
                return null;
            }

            String status = parts[0].trim();
            String payload = parts[1].trim();

            if ((!status.equals("0") && !status.equals("1")) || payload.isEmpty()) {
                return null;
            }

            Task task = parsePayload(payload);
            if (task == null) {
                return null;
            }

            if (status.equals("1")) {
                task.done();
            }

            return task;
        }

        /**
         * Converts a {@link Task} into its storage line representation.
         *
         * @param task Task to convert.
         * @return Storage string line for the task.
         */
        String toStorageString(Task task) {
            String doneFlag = task.isDone() ? "1" : "0";
            return doneFlag + " | " + task.toSaveString();
        }

        private Task parsePayload(String payload) {
            String[] parts = payload.trim().split("\\s+", 2);
            String type = parts[0].toLowerCase();
            String remainder = parts.length > 1 ? parts[1].trim() : "";

            return switch (type) {
            case "todo" -> parseTodo(remainder);
            case "deadline" -> parseDeadline(remainder);
            case "event" -> parseEvent(remainder);
            default -> null;
            };
        }

        private Task parseTodo(String remainder) {
            if (remainder.isEmpty()) {
                return null;
            }
            return new Todo(remainder);
        }

        private Task parseDeadline(String remainder) {
            String[] parts = remainder.split("/by", 2);
            if (parts.length < 2) {
                return null;
            }

            String description = parts[0].trim();
            String byRaw = parts[1].trim();

            if (description.isEmpty() || byRaw.isEmpty()) {
                return null;
            }

            try {
                LocalDateTime by = LocalDateTime.parse(byRaw, DATE_FORMAT);
                return new Deadline(description, by);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        private Task parseEvent(String remainder) {
            String[] parts = remainder.split("/from", 2);
            if (parts.length < 2) {
                return null;
            }

            String description = parts[0].trim();
            String[] timeParts = parts[1].trim().split("/to", 2);
            if (timeParts.length < 2) {
                return null;
            }

            String startRaw = timeParts[0].trim();
            String endRaw = timeParts[1].trim();

            if (description.isEmpty() || startRaw.isEmpty() || endRaw.isEmpty()) {
                return null;
            }

            try {
                LocalDateTime start = LocalDateTime.parse(startRaw, DATE_FORMAT);
                LocalDateTime end = LocalDateTime.parse(endRaw, DATE_FORMAT);
                return new Event(description, start, end);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
    }
}
