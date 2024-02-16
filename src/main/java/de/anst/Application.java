package de.anst;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "calendar", variant = Lumo.DARK)
public class Application implements AppShellConfigurator {
private static final long serialVersionUID = Application.class.hashCode();

    @SuppressWarnings("resource")
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(clientHttpRequestFactory());
	}

	private ClientHttpRequestFactory clientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		// Konfigurieren Sie hier optional die Eigenschaften der Factory, z.B.
		// Verbindungszeitüberschreitung usw.
		factory.setConnectTimeout(5000);
		factory.setReadTimeout(5000);
		return factory;
	}

}
