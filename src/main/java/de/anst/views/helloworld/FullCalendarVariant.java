/**
 * FullCalendarVariant.java created 19.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst.views.helloworld;

/**
 * FullCalendarVariant created 19.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
import lombok.Getter;

/**
 * Themevariants for the {@link FullCalendar}. Use {@link FullCalendar#addThemeVariants(FullCalendarVariant...)} to
 * apply them.
 *
 */
@Getter
public enum FullCalendarVariant {
    /**
     * An experimental theme variant, that applies lumo styles to the calendar to align it more with the
     * default styling of other Vaadin components.
     */
    LUMO("lumo");

    private final String variantName;

    FullCalendarVariant(String variantName) {
        this.variantName = variantName;
    }


}