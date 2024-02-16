package de.anst.views.about;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ICalController {

    @GetMapping("/ical")
    public ResponseEntity<byte[]> downloadICalFile() {
        // Hier sollte der Code zur Generierung Ihrer iCal-Datei stehen.
        String icalContent = generateICalContent();

        // Konvertieren Sie den iCal-String in ein Byte-Array.
        byte[] icalBytes = icalContent.getBytes();

        // Erstellen Sie einen HttpHeaders-Objekt, um den Dateinamen und Content-Type zu setzen.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "calendar.ics");

        // Bauen Sie die ResponseEntity mit dem Byte-Array, HttpHeaders und HttpStatus auf.
        return ResponseEntity.ok()
                .headers(headers)
                .body(icalBytes);
    }

    private String generateICalContent() {
        // Hier sollten Sie den Code zur Generierung Ihrer iCal-Datei implementieren.
        // Sie können eine iCal-Bibliothek verwenden oder den iCal-String manuell erstellen.
        // Hier ist ein einfaches Beispiel ohne Bibliothek:
        return "BEGIN:VCALENDAR\n" +
               "VERSION:2.0\n" +
               "PRODID:-//Antonius Calendar//EN\n" +
               "BEGIN:VEVENT\n" +
               "SUMMARY:Test Event\n" +
               "DTSTART:20240216T120000\n" +
               "DTEND:20240216T140000\n" +
               "LOCATION:Annebar\n" +
               "DESCRIPTION:Testtermin\n" +
               "END:VEVENT\n" +
               "END:VCALENDAR";
    }
}
