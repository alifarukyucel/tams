package nl.tudelft.sem.template.course.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date is deprecated, this is an easier way to convert a String to a Date.
 *
 * @created 09/12/2021, 16:05
 */
public class DateUtil {
    /**
     * Converts a Date into a String.
     *
     * @param s the s
     * @return the date
     */
    public static Date stringToDate(String s) {
        Date result = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
