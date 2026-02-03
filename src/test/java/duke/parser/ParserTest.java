package duke.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import duke.exception.DukeException;

public class ParserTest {
<<<<<<< HEAD
    @Test 
=======

    /**
     * Tests that a valid one based task number is converted into a zero based index.
     */
    @Test
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
    public void parseIndex_validNumber_returnsZeroBasedIndex() throws DukeException {
        Parser parser = new Parser();
        assertEquals(0, parser.parseIndex("mark 1"));
        assertEquals(1, parser.parseIndex("delete  2"));
    }

    @Test
    public void parseIndex_missingNumber_throwsException() {
        Parser parser = new Parser();
        assertThrows(DukeException.class, () -> parser.parseIndex("mark"));
    }

<<<<<<< HEAD
=======
    /**
     * Tests that a non numeric task number causes a {@link DukeException}.
     */
>>>>>>> 41f80c9 (A-CodingStandard: fix style issues and formatting)
    @Test
    public void parseIndex_nonNumeric_throwsException() {
        Parser parser = new Parser();
        assertThrows(DukeException.class, () -> parser.parseIndex("delete abc"));
    }
}
