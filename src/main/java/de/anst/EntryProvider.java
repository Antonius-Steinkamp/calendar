/**
 * EntryProvider.java created 19.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.vaadin.stefan.fullcalendar.Entry;
import org.yaml.snakeyaml.parser.ParserException;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.DateEnd;
import lombok.extern.java.Log;

/**
 * EntryProvider created 19.02.2024 by
 * <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
@Component
@Log
public class EntryProvider {

	private static List<String> getUrls() {
		String schulferienHamburg2024 = "https://www.schulferien.org/media/ical/deutschland/ferien_hamburg_2024.ics?k=GKM6i4aBFk00t5tmjXTiMagrQxEqDa7N4woJ1tndlcq_UYgvclY2qQvCLvIBRYA_QZOSdTH4Y9JoZ80zQ9b-YRcwUQPZ6qJpIRdvnALid6I";
		String schulferienNiedersachsen2024 = "https://www.schulferien.org/media/ical/deutschland/ferien_niedersachsen_2024.ics?k=gyvpNtMh00w2kp316O1E0V1eHKhSsIpcqNDSJPZJyJk_meIdQtCwS9u6yR7VtgADuMTwx_ZrVrL5-mSLxNBjGJpydnalWpGvg9yThK1pVIA";
		String h1 = "https://calendar.google.com/calendar/ical/d8c9955e5934337b9874197e78013a8db9dd0af8f261698ec22751d7ca4422c6%40group.calendar.google.com/public/basic.ics";
		String h2 = "https://calendar.google.com/calendar/ical/d8c9955e5934337b9874197e78013a8db9dd0af8f261698ec22751d7ca4422c6%40group.calendar.google.com/private-99bccb85948ae4cfff56b87c64f04a8f/basic.ics";
		String emstekInfo = "https://emstek.gremien.info/webkalender/webkalender.php";
		String konzertmeister = "https://rest.konzertmeister.app/api/v1/ical/15bae1d1-09c1-429f-8555-6c711d790c68";
		String jagdHoerner = "https://calendar.google.com/calendar/ical/903410c2c244ecd2489ee2b178554da3f0d15bca7eca51b600230eb2795f07e5%40group.calendar.google.com/public/basic.ics";

		String feiertage = "https://www.feiertage-deutschland.de/content/kalender-download/force-download.php";
		
		List<String> result = new ArrayList<>();
		result.add(h1);
		result.add(schulferienHamburg2024);
		result.add(schulferienNiedersachsen2024);
		result.add(emstekInfo);
		result.add(konzertmeister);
		result.add(jagdHoerner);
		result.add(feiertage);
		return result;
	}

	private final RestTemplate restTemplate;

	public EntryProvider(final RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<Entry> getEntries() {
		List<Entry> result = new ArrayList<>();

		WebClient webClient = WebClient.create();

		for (String url : getUrls()) {
			try {
				byte[] forObject = restTemplate.getForObject(url, byte[].class);
//				forObject = webClient.get().uri(url).retrieve().bodyToMono(byte[].class).block();

				if (forObject == null || forObject.length == 0) {
					log.warning("No Calendar found for " + url);
					continue;
				} else {
					log.info(forObject.length + " Bytes for " + url);
				}

				ICalendar ical = Biweekly.parse(new String(forObject)).first();

				if (ical == null) {
					log.info("No Calendar for " + url);
					continue;
				}
				for (VEvent vEvent: ical.getEvents()) {
					Entry entry = createEntryFrom(vEvent);
					if ( entry != null ) {
						result.add(entry);
					}
				}
			} catch (RestClientException ex) {
				log.warning(ex.toString());
			}
		}

		return result;
	}

	private static Entry createEntryFrom(VEvent vEvent) {
		Entry result = new Entry();

		long millisStart = vEvent.getDateStart().getValue().getTime();
		result.setStart(Instant.ofEpochMilli(millisStart));

		DateEnd dateEnd = vEvent.getDateEnd();
		long millisEnd;
		if ( dateEnd == null ) {
			millisEnd = millisStart;
		} else {
			millisEnd = vEvent.getDateEnd().getValue().getTime();
		}
		result.setEnd(Instant.ofEpochMilli(millisEnd));
		
		result.setTitle(vEvent.getSummary().getValue());
		
		return result;
	}

}
