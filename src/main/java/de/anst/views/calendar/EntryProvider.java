/**
 * EntryProvider.java created 19.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst.views.calendar;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

/**
 * EntryProvider created 19.02.2024 by
 * <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
@Component
@Log
public class EntryProvider {


	private static List<CalendarInfo> knownCalendars = new ArrayList<>();

	public EntryProvider() {
		
		log.info("ctor");
		
		knownCalendars.add(new CalendarInfo(
				"https://www.schulferien.org/media/ical/deutschland/ferien_hamburg_2024.ics?k=GKM6i4aBFk00t5tmjXTiMagrQxEqDa7N4woJ1tndlcq_UYgvclY2qQvCLvIBRYA_QZOSdTH4Y9JoZ80zQ9b-YRcwUQPZ6qJpIRdvnALid6I",
				"Ferien Hamburg", "#FFFF00"));
		knownCalendars.add(new CalendarInfo(
				"https://www.schulferien.org/media/ical/deutschland/ferien_niedersachsen_2024.ics?k=gyvpNtMh00w2kp316O1E0V1eHKhSsIpcqNDSJPZJyJk_meIdQtCwS9u6yR7VtgADuMTwx_ZrVrL5-mSLxNBjGJpydnalWpGvg9yThK1pVIA",
				"Ferien Niedersachsen", "orange", true));
		knownCalendars.add(new CalendarInfo(
				"https://calendar.google.com/calendar/ical/d8c9955e5934337b9874197e78013a8db9dd0af8f261698ec22751d7ca4422c6%40group.calendar.google.com/public/basic.ics",
				"Höltinghausen", "dodgerblue"));
		knownCalendars.add(new CalendarInfo(
				"https://calendar.google.com/calendar/ical/d8c9955e5934337b9874197e78013a8db9dd0af8f261698ec22751d7ca4422c6%40group.calendar.google.com/private-99bccb85948ae4cfff56b87c64f04a8f/basic.ics",
				"Höltinghausen privat", "mediumseagreen"));
		knownCalendars
				.add(new CalendarInfo("https://emstek.gremien.info/webkalender/webkalender.php", "Gemeinde", "gray"));
		knownCalendars.add(
				new CalendarInfo("https://rest.konzertmeister.app/api/v1/ical/15bae1d1-09c1-429f-8555-6c711d790c68",
						"Musikkorps", "slateblue"));
		knownCalendars.add(new CalendarInfo(
				"https://calendar.google.com/calendar/ical/903410c2c244ecd2489ee2b178554da3f0d15bca7eca51b600230eb2795f07e5%40group.calendar.google.com/public/basic.ics",
				"Jagdhörner", "green"));

		knownCalendars.add(
				new CalendarInfo("https://www.feiertage-deutschland.de/content/kalender-download/force-download.php",
						"Feiertage", "violet"));
	}

	public List<CalendarInfo> getCalendars() {
		return knownCalendars;
	}

	public List<String> getCalendarNames() {
		return knownCalendars.stream().map(c -> c.getDescription()).toList();
	}

	public List<CalendarInfo> getVisibleCalendars() {
		List<CalendarInfo> result = new ArrayList<>();
		for (CalendarInfo ci: knownCalendars) {
			if ( ci.isVisible()) {
				result.add(ci);
			}
		}
		log.info("visible: " + result);
		return result;
	}

	public List<String> getVisibleCalendarNames() {
		return getVisibleCalendars().stream().map(c -> c.getDescription()).toList();
	}
	
	public CalendarInfo byName(String name) {
		return knownCalendars.stream().filter(c -> c.getDescription().equals(name)).findFirst().get();
	}


}
