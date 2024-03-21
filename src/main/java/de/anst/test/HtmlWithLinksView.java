/**
 * HtmlWithLinksView.java created 13.03.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst.test;

/**
 * HtmlWithLinksView created 13.03.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("html-with-links")
public class HtmlWithLinksView extends VerticalLayout {

    public HtmlWithLinksView() {
        // Beispiel-HTML mit einem Link
        String htmlContent = "<p>Das ist ein Beispiel-<a href=\"https://www.example.com\">Link</a>.</p>";

        // Html-Komponente erstellen und den HTML-Code setzen
        Html html = new Html(htmlContent);

        // Button zum Öffnen des Links erstellen
        Button openLinkButton = new Button("Link öffnen", event -> {
            // Aktion beim Klicken des Buttons, z.B. Öffnen des Links
            getUI().ifPresent(ui -> ui.getPage().executeJs("window.open('https://www.example.com', '_blank');"));
        });

        // Komponenten zum Layout hinzufügen
        add(html, openLinkButton);
    }
}

