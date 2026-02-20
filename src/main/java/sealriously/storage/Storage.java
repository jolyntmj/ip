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
 * Handles loading tasks from and saving tasks to a local file.
 *
 * Storage format (one task per line):
 * T | [ ] | read book
 * D | [X] | return book | 2026-02-20 1800
 * E | [ ] | meeting | 2026-02-20 1400 | 2026-02-20 1600
 */
public class Storage {

    private final String filePath;
    private final TaskSerializer serializer;
    private boolean lastLoadHadCorruptedLines = false;

    public boolean lastLoadHadCorruptedLines() {
        return lastLoadHadCorruptedLines;
    }

    public Storage(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Storage file path cannot be null/empty.");
        }
        assert filePath != null : "Storage: filePath should not be null";
        assert !filePath.trim().isEmpty() : "Storage: filePath should not be empty";

        this.filePath = filePath;
        this.serializer = new TaskSerializer();
    }

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
        } catch (SealriouslyException e) {
            lastLoadHadCorruptedLines = true;
            // Skip corrupted lines but continue.
        }
    }
    
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
    
    private void writeTasksToFile(File file, TaskList tasks) throws IOException, SealriouslyException {
        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(serializer.toStorageString(tasks.get(i)));
                writer.write(System.lineSeparator());
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
