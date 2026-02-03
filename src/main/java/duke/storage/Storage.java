package duke.storage;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.Todo;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;



public class Storage {

    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public List<Task> load() throws DukeException {
        boolean corruptedFound = false;
    
        try {
            File f = new File(filePath);

            //if file doesnt exist, then create a folder + file
            if (!f.exists()) {
                File parent = f.getParentFile();
                if (parent != null) {
                    parent.mkdirs(); //create all missing parent directories automatically 
                }
                f.createNewFile();
                return new ArrayList<>();
            }

            Scanner s = new Scanner(f);
            List<Task> tasks = new ArrayList<>();
            while (s.hasNext()) {
                String raw = s.nextLine();

                if (raw.isEmpty()) {
                    continue;
                }

                String[] parts = raw.split("\\|", 2);
                if (parts.length != 2) {
                    corruptedFound = true;
                    continue;
                }

                String status = parts[0].trim();
                String remaining = parts[1].trim();

                if ((!status.equals("0") && !status.equals("1")) || remaining.isEmpty()) {
                    corruptedFound = true;
                    continue;
                }

                Task task = parseTask(remaining);
                if (task == null) {
                    corruptedFound = true;
                    continue;
                }

                if (status.equals("1")) {
                    task.done();
                }

                tasks.add(task);
            }
            s.close();
            if (corruptedFound) {
                System.out.println("Warning: Some saved tasks were corrupted and skipped.");
            }
            return tasks;

        } catch (IOException e) {
            throw new DukeException("Error loading saved data: " + e.getMessage());
        }

    }

    private Task parseTask(String input) {
        try {
            String[] parts = input.trim().split("\\s+",2);
            String firstWord = parts[0].toLowerCase();

            switch(firstWord) {
                case "todo" -> {
                    String description = input.substring(5).trim();
                    return new Todo(description);
                }

                case "deadline" -> {
                    String remainder = input.substring(8).trim();
                    String[] parts2 = remainder.split("/by", 2);
                    if (parts.length < 2) return null;

                    String description = parts2[0].trim();
                    String byRaw = parts2[1].trim();
                    
                    if (description.isEmpty() || byRaw.isEmpty()) return null;

                    DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

                    try {
                        LocalDateTime by = LocalDateTime.parse(byRaw, inFmt);
                        return new Deadline(description, by);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                }

                case "event" -> {
                    String remainder = input.substring(5).trim();
                    String[] parts3 = remainder.split("/from", 2);
                    if (parts3.length < 2) return null;

                    String description = parts3[0].trim();
                    String[] secondParting = parts3[1].split("/to",2);
                    if (secondParting.length < 2) return null;

                    String start = secondParting[0].trim();
                    String end = secondParting[1].trim();
                    if (description.isEmpty() || start.isEmpty() || end.isEmpty()) return null;

                    DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
                    try {
                        LocalDateTime startTime = LocalDateTime.parse(start, inFmt);
                        LocalDateTime endTime = LocalDateTime.parse(end, inFmt);
                        return new Event(description, startTime, endTime);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                }
                default -> {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void save(TaskList tasks) throws DukeException {
        try {
            File f = new File(filePath);
            File parent = f.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            FileWriter fw = new FileWriter(f);

            int i=0;
            while (i < tasks.size()) {
                Task t = tasks.get(i);

                String done = t.isDone() ? "1" : "0" ;
                fw.write(done + " | " + t.toSaveString() + "\n");
                i++;
            }
            fw.close();
        } catch (IOException e) {
            throw new DukeException("Error saving data: " + e.getMessage());
        }
    }
<<<<<<< Updated upstream
=======

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
            }
        }
    }
>>>>>>> Stashed changes
}
