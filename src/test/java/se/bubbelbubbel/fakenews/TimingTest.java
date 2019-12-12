package se.bubbelbubbel.fakenews;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Random;

import org.junit.Test;

public class TimingTest {

	@Test
	public void testTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
		GregorianCalendar sendTime = new GregorianCalendar();
		System.out.println("sendTime: " + sdf.format(sendTime.getTime()));
		Random random = new Random();
		for(int i=0; i< 100; i++) {
			sendTime = new GregorianCalendar();
			int minuteInterval = 30 + random.nextInt(60);
			sendTime.add(GregorianCalendar.MINUTE, minuteInterval);
			assertTrue(minuteInterval > 29);
			System.out.println("minuteInterval: " + minuteInterval);
			System.out.println("sendTime: " + sdf.format(sendTime.getTime()));
		}
	}

}
