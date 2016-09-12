package de.slava.schoolaccounting.util;

/**
 * @author by V.Sysoltsev
 */
public class CsvUtils {
    public final static String CSV_SEPARATOR = ";";
    public final static String CSV_EOL = "\n";

    public static String wrapCsv(String text) {
        boolean needQuotation = text.startsWith(" ") || text.endsWith(" ") || text.contains(",") || text.contains(";") || text.contains("\n");
        // replace all " with double ""
        text = text.replaceAll("\"", "\"\"");
        // add quotation if needed
        if (needQuotation) {
            text = "\"" + text + "\"";
        }
        return text;
    }

    public static String unwrapCsv(String text) {
        // remove quotation
        if (text.startsWith("\"") && text.endsWith("\""))
            text = text.substring(1, text.length()-1);
        return text.replaceAll("\"\"", "\"");
    }
}
