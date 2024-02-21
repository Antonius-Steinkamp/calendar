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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.stefan.fullcalendar.Entry;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.DateEnd;
import biweekly.util.ICalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;

/**
 * CalendarInfo created 21.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
@Data
@AllArgsConstructor
@Log
public  class CalendarInfo {
	private String url;
	private String description;
	private String color;
	private boolean visible;
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
		log.info("ctor" + this);
	}

	public CalendarInfo(String url, String description, String color, boolean isVisible) {
		this.url = url;
		this.description = description;
		this.color = color;
		this.visible = isVisible;
		log.info("ctor" + this);
	}

	synchronized public List<Entry> getEntries() {
		log.info("getEntries " + toString());
		if (entries.isEmpty()) {
			entries.addAll(createEntries(this));
			log.info("add Entries " + toString());
		}

		return entries;
	}
	private static List<Entry> createEntries(CalendarInfo url) {
		log.info("createEntries " + url);
		List<Entry> result = new ArrayList<>();
		try {
			String icsDatei = downloadFileToString(url.getUrl());
			for (ICalendar ical : Biweekly.parse(new String(icsDatei)).all()) {

				if (ical == null) {
					log.info("No Calendar for " + url.getDescription());
					continue;
				}
				for (VEvent vEvent : ical.getEvents()) {
					Entry entry = createEntryFrom(vEvent);
					entry.setColor(url.getColor());
					if (entry != null) {
						result.add(entry);
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

	private static Entry createEntryFrom(VEvent vEvent) {
		Entry result = new Entry();

		ICalDate dateStartValue = vEvent.getDateStart().getValue();

		Instant startInstant = dateStartValue.toInstant();
		LocalDateTime ldt = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
		result.setStart(ldt);

		result.setAllDay(!dateStartValue.hasTime());

		DateEnd dateEnd = vEvent.getDateEnd();
		Instant endInstant = startInstant;
		if (dateEnd != null) {
			endInstant = vEvent.getDateEnd().getValue().toInstant();
		}
		LocalDateTime endTime = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault());
		result.setEnd(endTime);

		result.setTitle(vEvent.getSummary().getValue());

		return result;
	}

}
