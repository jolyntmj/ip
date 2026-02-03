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
<<<<<<< HEAD
        boolean corruptedFound = false;
    
=======
        File file = new File(filePath);
        List<Task> tasks = new ArrayList<>();
        boolean hasCorruptedFound = false;

>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
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
                    hasCorruptedFound = true;
                    continue;
                }

                if (status.equals("1")) {
                    task.done();
                }

                tasks.add(task);
            }
<<<<<<< HEAD
            s.close();
            if (corruptedFound) {
=======
            scanner.close();

            if (hasCorruptedFound) {
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
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
}
