package se.bubbelbubbel.fakenews.model.helper;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class FunctionHelper {
	public static GregorianCalendar dateToGregCal(Date date) {
		GregorianCalendar retGregCal = new GregorianCalendar();
		retGregCal.setTime(date);
		return retGregCal;
	}

	public static GregorianCalendar dateToGregCal(Timestamp date) {
		GregorianCalendar retGregCal = new GregorianCalendar();
		retGregCal.setTime(date);
		return retGregCal;
	}

}
