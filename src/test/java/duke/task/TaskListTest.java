package duke.task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import duke.exception.DukeException;

public class TaskListTest {
<<<<<<< HEAD
    
=======

    /**
     * Tests deleting a valid index removes the correct task and shifts remaining tasks.
     */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
    @Test
    public void delete_validIndex_removesAndReturnsTask() throws DukeException {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("sleep"));

        Task removed = list.delete(0);

        assertEquals("[T][ ] read book", removed.toString());
        assertEquals(1, list.size());
        assertEquals("[T][ ] sleep", list.get(0).toString());
    }

    @Test
    public void delete_invalidIndex_throwsException() {
        TaskList list = new TaskList();
        assertThrows(DukeException.class, () -> list.delete(0));
    }

    @Test
    public void mark_validIndex_marksTaskDone() throws DukeException {
        TaskList list = new TaskList();
        list.add(new Todo("gym"));

        list.mark(0);

        assertTrue(list.get(0).isDone());
    }

    @Test
    public void mark_invalidIndex_throwsException() {
        TaskList list = new TaskList();
        assertThrows(DukeException.class, () -> list.mark(0));
    }

}
