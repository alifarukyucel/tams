package nl.tudelft.sem.template.course.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ali Faruk Yücel
 * @version 1.0
 * @created 09/12/2021, 16:05
 */
public class DateUtil {
    public static Date stringToDate(String s) {
        Date result = null;
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = dateFormat.parse(s);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        return result ;
    }
}
