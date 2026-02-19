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
 *
 * Storage format (one task per line):
 * T | [ ] | read book
 * D | [X] | return book | 2026-02-20 1800
 * E | [ ] | meeting | 2026-02-20 1400 | 2026-02-20 1600
 */
public class Storage {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final String filePath;
    private final TaskSerializer serializer;
    private boolean lastLoadHadCorruptedLines = false;

    public boolean lastLoadHadCorruptedLines() {
        return lastLoadHadCorruptedLines;
    }

    public Storage(String filePath) {
<<<<<<< Updated upstream
=======
        assert filePath != null : "Storage: filePath should not be null";
        assert !filePath.trim().isEmpty() : "Storage: filePath should not be empty";

>>>>>>> Stashed changes
        this.filePath = filePath;
        this.serializer = new TaskSerializer();
    }

    public List<Task> load() throws DukeException {
        lastLoadHadCorruptedLines = false;
    
        File file = new File(filePath);
        try {
            ensureFileExists(file);
            return readTasksFromFile(file);
        } catch (IOException e) {
            throw new DukeException("Error loading saved data: " + e.getMessage());
        }
    }
    
    private List<Task> readTasksFromFile(File file) throws IOException {
        List<Task> tasks = new ArrayList<>();
    
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                addTaskIfValid(tasks, line);
            }
        }
        return tasks;
    }
    
    private void addTaskIfValid(List<Task> tasks, String line) {
        try {
            tasks.add(serializer.fromStorageString(line));
        } catch (DukeException e) {
            lastLoadHadCorruptedLines = true;
            // Skip corrupted lines but continue.
        }
    }
    
    public void save(TaskList tasks) throws DukeException {
<<<<<<< Updated upstream
=======
        assert tasks != null : "Storage.save: tasks should not be null";
    
        File file = new File(filePath);
>>>>>>> Stashed changes
        try {
            ensureFileExists(file);
            writeTasksToFile(file, tasks);
        } catch (IOException e) {
            throw new DukeException("Error saving data: " + e.getMessage());
        }
    }
<<<<<<< Updated upstream

    /**
     * Converts tasks to and from their storage format.
     * Format example:
     * {@code 1 | todo read book}
     * {@code 0 | deadline submit report /by 2019-12-02 1800}
     * {@code 1 | event meeting START: 2019-12-02 1800 DUE: 2019-12-02 2000}
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
            String[] parts = remainder.split(" DUE: ", 2);
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
            String[] parts = remainder.split(" START: ", 2);
            if (parts.length < 2) {
                return null;
            }

            String description = parts[0].trim();
            String[] timeParts = parts[1].trim().split(" DUE: ", 2);
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
=======
    
    private void writeTasksToFile(File file, TaskList tasks) throws IOException, DukeException {
        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(serializer.toStorageString(tasks.get(i)));
                writer.write(System.lineSeparator());
>>>>>>> Stashed changes
            }
        }
    }
    
    private static void ensureFileExists(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
    }    
}
