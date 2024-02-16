/**
 * RunnerEnv.java created 10.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.java.Log;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

/**
 * RunnerEnv created 10.02.2024 by
 * <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
@Component
@Log
public class Runner implements CommandLineRunner {

	String schulferienHamburg2024 = "https://www.schulferien.org/media/ical/deutschland/ferien_hamburg_2024.ics?k=GKM6i4aBFk00t5tmjXTiMagrQxEqDa7N4woJ1tndlcq_UYgvclY2qQvCLvIBRYA_QZOSdTH4Y9JoZ80zQ9b-YRcwUQPZ6qJpIRdvnALid6I";
	String schulferienNiedersachsen2024 = "https://www.schulferien.org/media/ical/deutschland/ferien_niedersachsen_2024.ics?k=gyvpNtMh00w2kp316O1E0V1eHKhSsIpcqNDSJPZJyJk_meIdQtCwS9u6yR7VtgADuMTwx_ZrVrL5-mSLxNBjGJpydnalWpGvg9yThK1pVIA";
	private final RestTemplate restTemplate;

	public Runner(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public void run(String... args) throws Exception {
		byte[] forObject = restTemplate.getForObject(schulferienNiedersachsen2024, byte[].class);
		log.info(forObject.length + "Bytes geladen");
		
		InputStream inputStream = new ByteArrayInputStream(forObject);
		
        CalendarBuilder builder = new CalendarBuilder();
        try {
        Calendar calendar = builder.build(inputStream);
        
        calendar.getComponents().forEach( calComponent -> log.info(calComponent.toString()) );
        } catch (ParserException ex) {
        	log.warning("Could not understand " + new String(forObject));
        }
	}

}
