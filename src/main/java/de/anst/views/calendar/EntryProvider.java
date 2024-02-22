/**
 * EntryProvider.java created 19.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst.views.calendar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.java.Log;

/**
 * EntryProvider created 19.02.2024 by
 * <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
@Component
@Log
public class EntryProvider {

	private List<CalendarInfo> knownCalendars = new ArrayList<>();

	public EntryProvider() {

		log.info("ctor");

		readCalendars();		
	}

	private static ObjectMapper getObjectMapper() {
		final ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());

		om.configure(SerializationFeature.INDENT_OUTPUT, true);

		return om;

	}

	private static String fileName = System.getProperty("user.home") + "/Calendars.json";

	@SuppressWarnings("unused")
	private void writeCalendars() {
		try {
			getObjectMapper().writeValue(new File(fileName), knownCalendars);
			log.info("Known Calendars written to " + fileName);
		} catch (IOException e) {
			log.warning(e.getLocalizedMessage());
		}
		
	}
	
	private void readCalendars() {
		ObjectMapper om = getObjectMapper();
		
		try {
			knownCalendars = om.readValue(new File(fileName), om.getTypeFactory().constructCollectionType(List.class, CalendarInfo.class));
		} catch (JsonMappingException e) {
			log.warning(e.getLocalizedMessage());
		} catch (JsonProcessingException e) {
			log.warning(e.getLocalizedMessage());
		} catch (IOException e) {
			log.warning(e.getLocalizedMessage());
		}
		
		log.info("found " + knownCalendars.size() + " calendars in file " + fileName);
		
	}
	
	public List<CalendarInfo> getCalendars() {
		return knownCalendars;
	}

	public List<String> getCalendarNames() {
		return knownCalendars.stream().map(c -> c.getDescription()).toList();
	}

	public List<CalendarInfo> getVisibleCalendars() {
		List<CalendarInfo> result = new ArrayList<>();
		for (CalendarInfo ci : knownCalendars) {
			if (ci.isVisible()) {
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
