package de.slava.schoolaccounting.util;

import org.junit.Test;
import static junit.framework.TestCase.*;
import static de.slava.schoolaccounting.util.CsvUtils.*;

/**
 * @author by V.Sysoltsev
 */
public class CsvUtilsTest {
    @Test
    public void test_wrap_comma() {
        assertEquals("\"Slava, the I\"", wrapCsv("Slava, the I"));
    }

    @Test
    public void test_wrap_semicolon() {
        assertEquals("\"Slava; the I\"", wrapCsv("Slava; the I"));
    }

    @Test
    public void test_wrap_space() {
        assertEquals("\"Oje  \"", wrapCsv("Oje  "));
        assertEquals("\"  Oje\"", wrapCsv("  Oje"));
    }

    @Test
    public void test_wrap_newline() {
        assertEquals("\"news\nline2\"", wrapCsv("news\nline2"));
    }

    @Test
    public void test_wrap_doublequote() {
        assertEquals("here is \"\" doublequote", wrapCsv("here is \" doublequote"));
    }

    @Test
    public void test_wrap_alltogether() {
        assertEquals("\"here is \"\" doublequote,\nand some more   \"", wrapCsv("here is \" doublequote,\nand some more   "));
    }


    @Test
    public void test_unwrap_comma() {
        assertEquals("Slava, the I", unwrapCsv("\"Slava, the I\""));
    }

    @Test
    public void test_unwrap_semicolon() {
        assertEquals("Slava; the I", unwrapCsv("\"Slava; the I\""));
    }

    @Test
    public void test_unwrap_space() {
        assertEquals("Oje  ", unwrapCsv("\"Oje  \""));
        assertEquals("  Oje", unwrapCsv("\"  Oje\""));
    }

    @Test
    public void test_unwrap_newline() {
        assertEquals("news\nline2", unwrapCsv("\"news\nline2\""));
    }

    @Test
    public void test_unwrap_doublequote() {
        assertEquals("here is \" doublequote", unwrapCsv("here is \"\" doublequote"));
    }

    @Test
    public void test_unwrap_alltogether() {
        assertEquals("here is \" doublequote,\nand some more   ", unwrapCsv("\"here is \"\" doublequote,\nand some more   \""));
    }

}