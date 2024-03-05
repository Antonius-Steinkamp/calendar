/**
 * CalendarInfo.java created 21.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst.views.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.vaadin.stefan.fullcalendar.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.DateEnd;
import biweekly.util.Frequency;
import biweekly.util.ICalDate;
import biweekly.util.Period;
import de.anst.AUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

/**
 * CalendarInfo created 21.02.2024 by
 * <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Log
public class CalendarInfo {
	/**
	 * String URL {@value #URL} since 22.02.2024
	 */
	public static final String URL = "Url";
	/**
	 * String LOCATION {@value #LOCATION} since 22.02.2024
	 */
	public static final String LOCATION = "Location";

	public static final String UID = "Uid";
	public static final String CDATE = "CDATE";

	private String url;
	private String description;
	private String color;
	private boolean visible;

	@JsonIgnore
	private List<Entry> entries = new ArrayList<>();

	@Override
	public String toString() {
		return description;
	}

	public CalendarInfo(String url, String description, String color) {
		this.url = url;
		this.description = description;
		this.color = color;
		visible = false;
		log.info("ctor " + this);
	}

	public CalendarInfo(String url, String description, String color, boolean isVisible) {
		this.url = url;
		this.description = description;
		this.color = color;
		this.visible = isVisible;
		log.info("ctor " + this);
	}

	public List<Entry> getEntries() {
		log.info("getEntries " + toString());
		if (entries.isEmpty()) {
			List<Entry> createEntries = createEntries(this);

			entries.addAll(createEntries);
			log.info("add " + createEntries.size() + " Entries");
		}

		return entries;
	}

	private static List<Entry> createEntries(CalendarInfo url) {

		List<Entry> result = new ArrayList<>();
		try {
			String icsDatei = downloadFileToString(url.getUrl());
			for (ICalendar ical : Biweekly.parse(new String(icsDatei)).all()) {

				if (ical == null) {
					log.info("No Calendar for " + url.getDescription());
					continue;
				}
				for (VEvent vEvent : ical.getEvents()) {
					List<Entry> entries = createEntriesOf(vEvent);
					for (Entry entry : entries) {
						entry.setColor(url.getColor());
					}
					if (entries != null) {
						result.addAll(entries);
					}
				}
			}
		} catch (IOException e) {
			log.warning("No Calendar for " + url.getDescription() + e.getLocalizedMessage());
		}

		return result;

	}

	private static String downloadFileToString(String fileUrl) throws IOException {
		URL url = new URL(fileUrl);
		URLConnection connection = url.openConnection();

		StringBuilder content = new StringBuilder();

		try (InputStream inputStream = connection.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line).append("\n");
			}
		}

		return content.toString();
	}

	private static ZoneId usedZoneId = ZoneId.of("Europe/Berlin");
	
	private static List<Entry> createEntriesOf(VEvent vEvent) {
		Entry entry = new Entry(UUID.randomUUID().toString());

		ICalDate dateStartValue = vEvent.getDateStart().getValue();

		Instant startInstant = dateStartValue.toInstant();
		LocalDateTime ldt = LocalDateTime.ofInstant(startInstant, usedZoneId);
		entry.setStart(ldt);

		entry.setAllDay(!dateStartValue.hasTime());

		DateEnd dateEnd = vEvent.getDateEnd();
		Instant endInstant = startInstant;
		if (dateEnd != null) {
			endInstant = vEvent.getDateEnd().getValue().toInstant();
		}
		LocalDateTime endTime = LocalDateTime.ofInstant(endInstant, usedZoneId);
		entry.setEnd(endTime);

		entry.setTitle(vEvent.getSummary().getValue());
		var description = vEvent.getDescription();
		if (description != null) {
			entry.setDescription(description.getValue());
		}
		var location = vEvent.getLocation();
		if (location != null) {
			entry.setCustomProperty(LOCATION, location.getValue());
			// log.info(LOCATION + " is "+location.getValue());
		}
		var uid = vEvent.getUid();
		if (uid != null) {
			entry.setCustomProperty(UID, uid.getValue());
			// log.info(UID + " is "+uid.getValue());
		}
		var cdate = vEvent.getDateTimeStamp();
		if (cdate != null) {
			entry.setCustomProperty(CDATE, cdate.getValue());
			// log.info(CDATE + " is "+cdate.getValue());
		}

		var comments = vEvent.getComments();
		if (comments != null) {
			comments.forEach(c -> log.info(c.toString()));
		}

		var attendees = vEvent.getAttendees();
		if (attendees != null) {
			attendees.forEach((c -> log.info(c.toString())));
		}

		var attachments = vEvent.getAttachments();
		if (attachments != null) {
			attachments.forEach((c -> log.info(c.toString())));
		}

		var url = vEvent.getUrl();
		if (url != null) {
			entry.setCustomProperty(URL, url.getValue());
			// log.info(URL + " is " +url.getValue());
		}

		var rrdate = vEvent.getRecurrenceDates();
		rrdate.forEach(r -> {
			log.info("Wiederholungstermine: " + AUtils.getAllGetters(r));
			List<Period> periods = r.getPeriods();
			for (Period period : periods) {
				log.info("Periaod: " + AUtils.getAllGetters(period));
			}
		});

		var result = new ArrayList<Entry>();
		result.add(entry);

		var rrule = vEvent.getRecurrenceRule();
		if (rrule != null) {
			log.info("Wiederholungsregel: " + AUtils.getAllGetters(rrule));

			var recurrency = rrule.getValue();
			log.info("Wiederholung: " + AUtils.getAllGetters(recurrency));

			Frequency frequency = recurrency.getFrequency();
			if (Frequency.WEEKLY.equals(frequency)) {
				log.info("Frequency Weekly: " + frequency);
				entry.setRecurringDaysOfWeek(DayOfWeek.of(recurrency.getWorkweekStarts().getCalendarConstant() + 1));
				entry.setRecurringStart(LocalDateTime.now().minusDays(36));
				entry.setRecurringStartTime(entry.getStart().toLocalTime());
				entry.setRecurringEndTime(entry.getEnd().toLocalTime());

			} else if (Frequency.MONTHLY.equals(frequency)) {
				log.info("Frequency Monthly: " + frequency);
				for (int i = 1; i < 48; i++) {
					Entry newEntry = entry.copy();
					newEntry.setStart(entry.getStart().plusMonths(i));
					newEntry.setEnd(entry.getEnd().plusMonths(i));

					result.add(newEntry);
				}
			} else if (Frequency.YEARLY.equals(frequency)) {
				log.info("Frequency Yearly: " + frequency);
				int startMenge = result.size();
				for (int i = 1; i < 10; i++) {
					Entry newEntry = new Entry();

					Entry.copy(entry, newEntry, false);
					newEntry.setStart(entry.getStart().plusYears(i));
					newEntry.setEnd(entry.getEnd().plusYears(i));

					result.add(newEntry);
				}
				log.info("added " + (result.size() - startMenge) + " entries");
			}
		}

		return result;
	}

}
