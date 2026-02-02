package duke.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import duke.exception.DukeException;

public class ParserTest {
    @Test 
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

    @Test
    public void parseIndex_nonNumeric_throwsException() {
        Parser parser = new Parser();
        assertThrows(DukeException.class, () -> parser.parseIndex("delete abc"));
    }
}
