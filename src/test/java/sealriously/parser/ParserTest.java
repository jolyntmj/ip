package sealriously.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sealriously.exception.SealriouslyException;

/**
 * Unit tests for {@link Parser}.
 */
public class ParserTest {

    /**
     * Tests that a valid one based task number is converted into a zero based index.
     */
    @Test
    public void parseIndex_validNumber_returnsZeroBasedIndex() throws SealriouslyException {
        Parser parser = new Parser();
        assertEquals(0, parser.parseIndex("mark 1"));
        assertEquals(1, parser.parseIndex("delete  2"));
    }

    /**
     * Tests that missing task number causes a {@link SealriouslyException}.
     */
    @Test
    public void parseIndex_missingNumber_throwsException() {
        Parser parser = new Parser();
        assertThrows(SealriouslyException.class, () -> parser.parseIndex("mark"));
    }

    /**
     * Tests that a non-numeric task number causes a {@link SealriouslyException}.
     */
    @Test
    public void parseIndex_nonNumeric_throwsException() {
        Parser parser = new Parser();
        assertThrows(SealriouslyException.class, () -> parser.parseIndex("delete abc"));
    }
}
