package sealriously.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sealriously.exception.SealriouslyException;
import sealriously.storage.TaskSerializer;
import sealriously.task.Task;
import sealriously.task.TaskList;

/**
 * Manages persistent storage of tasks in a local file.
 *
 * This class is responsible for:
 * - Loading tasks from a save file into memory when the application starts.
 * - Writing the current task list back to the file when changes occur.
 *
 * Each task is stored on a single line using a delimiter-based format.
 *
 * General format:
 * TYPE | STATUS | DESCRIPTION | [DATE_INFO...] | [TAGS]
 *
 * Example storage format:
 * T | [ ] | read book
 * T | [X] | submit report | #school #important
 * D | [X] | return book | 2026-02-20 1800
 * D | [ ] | project deadline | 2026-02-25 2359 | #cs2103
 * E | [ ] | meeting | 2026-02-20 1400 | 2026-02-20 1600
 * E | [X] | presentation | 2026-02-20 1400 | 2026-02-20 1600 | #work
 *
 * Where:
 * - T represents a Todo task
 * - D represents a Deadline task
 * - E represents an Event task
 * - [X] indicates a completed task
 * - [ ] indicates an incomplete task
 * - Tags (if present) appear at the end of the line, separated by spaces
 *
 * Corrupted or improperly formatted lines are safely skipped during loading.
 */
public class Storage {

    private final String filePath;
    private final TaskSerializer serializer;
    private boolean lastLoadHadCorruptedLines = false;

    /**
     * Returns whether the last load operation encountered corrupted lines that were skipped.
     *
     * @return true if at least one corrupted line was detected and skipped.
     */
    public boolean lastLoadHadCorruptedLines() {
        return lastLoadHadCorruptedLines;
    }

    /**
     * Creates a storage component that reads/writes tasks from/to the given file path.
     *
     * @param filePath Save file path.
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Storage file path cannot be null/empty.");
        }
        assert filePath != null : "Storage: filePath should not be null";
        assert !filePath.trim().isEmpty() : "Storage: filePath should not be empty";

        this.filePath = filePath;
        this.serializer = new TaskSerializer();
    }

    /**
     * Loads tasks from disk.
     *
     * @return List of tasks loaded from storage.
     * @throws SealriouslyException If reading fails.
     */
    public List<Task> load() throws SealriouslyException {
        lastLoadHadCorruptedLines = false;
    
        File file = new File(filePath);
        try {
            ensureFileExists(file);
            return readTasksFromFile(file);
        } catch (IOException e) {
            throw new SealriouslyException("Error loading saved data: " + e.getMessage());
        }
    }
    
    /**
     * Reads task lines from a file and converts them into Task objects.
     *
     * @param file File to read from.
     * @return List of parsed tasks.
     * @throws IOException If file I/O fails.
     */
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
    
    /**
     * Attempts to parse and add a task line into the list.
     * Corrupted lines are skipped and recorded.
     *
     * @param tasks List to append parsed tasks to.
     * @param line  Raw line from storage file.
     */
    private void addTaskIfValid(List<Task> tasks, String line) {
        try {
            tasks.add(serializer.fromStorageString(line));
        } catch (SealriouslyException e) {
            lastLoadHadCorruptedLines = true;
            // Skip corrupted lines but continue.
        }
    }
    
    /**
     * Saves the provided task list to disk.
     *
     * @param tasks TaskList to save.
     * @throws SealriouslyException If saving fails.
     */
    public void save(TaskList tasks) throws SealriouslyException {
        if (tasks == null) {
            throw new SealriouslyException("Nothing to save (task list is null).");
        }
        assert tasks != null : "Storage.save: tasks should not be null";
    
        File file = new File(filePath);
        try {
            ensureFileExists(file);
            writeTasksToFile(file, tasks);
        } catch (IOException e) {
            throw new SealriouslyException("Error saving data: " + e.getMessage());
        }
    }
    
    /**
     * Writes the tasks into the given file in storage format.
     *
     * @param file  Destination file.
     * @param tasks Task list to serialize.
     * @throws IOException If file I/O fails.
     * @throws SealriouslyException If serialization fails.
     */
    private void writeTasksToFile(File file, TaskList tasks) throws IOException, SealriouslyException {
        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(serializer.toStorageString(tasks.get(i)));
                writer.write(System.lineSeparator());
            }
        }
    }
    
    /**
     * Ensures the storage file and its parent directories exist.
     *
     * @param file Target file.
     * @throws IOException If file creation fails.
     */
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
