/**
 * LinkInTextFieldView.java created 13.03.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst.test;

import com.vaadin.flow.component.Html;
/**
 * LinkInTextFieldView created 13.03.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")

@Route("link-in-textfield")
public class LinkInTextFieldView extends VerticalLayout {

    public LinkInTextFieldView() {
        // Beispiel-Link
        String linkUrl = "https://www.example.com";
        String linkText = "Besuche unsere Website";

        // Anchor (Link) erstellen
        Anchor link = new Anchor(linkUrl, linkText);

        // TextField erstellen und den Link als Wert setzen
        TextField textField = new TextField();
        textField.getElement().setText("Uffi <a href=\"" + linkUrl + "\">" + linkUrl + "</a>");
        textField.setReadOnly(true);

        Html html = new Html("<div></div>");
        html.setHtmlContent("<div>Hi Uffi <a href=\"" + linkUrl + ">" + linkUrl + "</a></div>");
        // Button zum Öffnen des Links erstellen
        Button openLinkButton = new Button("Link öffnen", event -> {
            // Aktion beim Klicken des Buttons, z.B. Öffnen des Links
            getUI().ifPresent(ui -> ui.getPage().executeJs("window.open('" + linkUrl + "', '_blank');"));
        });

        // Komponenten zum Layout hinzufügen
        add(link, html, textField, openLinkButton);
    }
}


