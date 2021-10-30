package se.bubbelbubbel.fakenews;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import se.bubbelbubbel.fakenews.model.TimeFactor;
import se.bubbelbubbel.fakenews.model.TweetRequest;
import se.bubbelbubbel.fakenews.model.TweetScheduleEntry;

public class TweetRequestTest {

	@Test
	public void testSchedule() {
		TweetRequest tr = new TweetRequest();
		tr.setSendSchedule("2h, 3h,  3d, 4v, 6m, 2 d, 1w");
		List<TweetScheduleEntry> l = tr.getScheduleEntries();
		assertTrue(l.get(0).getTimeValue() == 2);
		assertTrue(l.get(0).getTimeFactor() == TimeFactor.HOUR);

		assertTrue(l.get(1).getTimeValue() == 3);
		assertTrue(l.get(1).getTimeFactor() == TimeFactor.HOUR);

		assertTrue(l.get(2).getTimeValue() == 3);
		assertTrue(l.get(2).getTimeFactor() == TimeFactor.DAY);

		assertTrue(l.get(3).getTimeValue() == 4);
		assertTrue(l.get(3).getTimeFactor() == TimeFactor.WEEK);

		assertTrue(l.get(4).getTimeValue() == 6);
		assertTrue(l.get(4).getTimeFactor() == TimeFactor.MINUTE);

		assertTrue(l.get(5).getTimeValue() == 2);
		assertTrue(l.get(5).getTimeFactor() == TimeFactor.DAY);

		assertTrue(l.get(6).getTimeValue() == 1);
		assertTrue(l.get(6).getTimeFactor() == TimeFactor.WEEK);

		tr.setSendSchedule("2ö");
		l = tr.getScheduleEntries();
		assertEquals(0, l.get(0).getTimeValue());
		assertEquals(TimeFactor.AUTO, l.get(0).getTimeFactor());

		tr.setSendSchedule("u,,d,jdh");
		l = tr.getScheduleEntries();
		assertEquals(0, l.get(0).getTimeValue());
		assertEquals(TimeFactor.AUTO, l.get(0).getTimeFactor());
	}
	
}
