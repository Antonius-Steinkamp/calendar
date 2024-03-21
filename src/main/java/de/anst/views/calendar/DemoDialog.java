package de.anst.views.calendar;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vaadin.stefan.fullcalendar.Delta;
import org.vaadin.stefan.fullcalendar.Entry;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.ValueProvider;

import de.anst.AUtils;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class DemoDialog extends Dialog {
	/**
	 * String NO_VALUE {@value #NO_VALUE}
	 * since 22.02.2024
	 */
	private static final String NO_VALUE = "---";

	private static final long serialVersionUID = 1L;

//	private static final String[] COLORS = { "tomato", "orange", "dodgerblue", "mediumseagreen", "gray", "slateblue", "violet" };
	private final VerticalLayout componentsLayout;

	@Setter
	private SerializableConsumer<Entry> onSaveConsumer;

	@Setter
	private SerializableConsumer<Entry> onDeleteConsumer;

	private final Entry tmpEntry;

	private final CustomDateTimePicker fieldStart;
	private final CustomDateTimePicker fieldEnd;
	private final MultiSelectComboBox<DayOfWeek> fieldRDays;
	private boolean recurring;
	private final Entry entry;

	public DemoDialog(Entry entry) {
		this.entry = entry;

		// tmp entry is a copy. we will use its start and end to represent either the
		// start/end or the recurring start/end
		this.tmpEntry = entry.copyAsType(entry.getClass());

		this.recurring = entry.isRecurring();
		tmpEntry.setStart(recurring ? entry.getRecurringStart() : entry.getStartWithOffset());
		tmpEntry.setEnd(recurring ? entry.getRecurringEnd() : entry.getEndWithOffset());

		setCloseOnEsc(true);
		setCloseOnOutsideClick(true);
		setDraggable(true);
		setResizable(true);
		setHeaderTitle(entry.getTitle());

		addThemeVariants(DialogVariant.LUMO_NO_PADDING);
		setWidth("500px");

		componentsLayout = new VerticalLayout();

		if ( AUtils.hasValue(entry.getDescription()) ) {
			H3 fieldDescription = new H3("Beschreibung");
			log.info(entry.getDescription());
			Html htmlDescription = new Html("<p>" + replaceLinks(entry.getDescription()) + "</p>");
			componentsLayout.add(htmlDescription);
		}

		if (entry.getCustomProperty(CalendarInfo.URL) != null ) {
			H3 fieldUrl = new H3("Url");
			Html htmlUrl = new Html("<p>" + replaceLinks(entry.getOrCreateCustomProperties().getOrDefault(CalendarInfo.URL, NO_VALUE).toString()) + "</p>");
			componentsLayout.add(htmlUrl);
		}
		
		if (entry.getCustomProperty(CalendarInfo.LOCATION) != null) {
			H3 fieldUrl = new H3("Ort");
			Html htmlUrl = new Html("<p>" + replaceLinks(entry.getOrCreateCustomProperties().getOrDefault(CalendarInfo.LOCATION, NO_VALUE).toString()) + "</p>");
			componentsLayout.add(fieldUrl, htmlUrl);
		}

		fieldStart = new CustomDateTimePicker("Start");
		fieldEnd = new CustomDateTimePicker("End");


		Span infoEnd = new Span("End is always exclusive, e.g. for a 1 day event you need to set for instance 4th of May as start and 5th of May as end.");
		infoEnd.getStyle().set("font-size", "0.8em");
		infoEnd.getStyle().set("color", "gray");

		fieldRDays = new MultiSelectComboBox<>("Recurrence days of week", DayOfWeek.values());
		fieldRDays.setItemLabelGenerator(item -> item.getDisplayName(TextStyle.FULL, getLocale()));

		fieldStart.setValue(entry.getStart());
		fieldStart.setEnabled(false);
		
		fieldEnd.setValue(entry.getEnd());
		fieldEnd.setEnabled(false);

		componentsLayout.add(new HorizontalLayout(fieldStart, fieldEnd), fieldRDays);
		
		componentsLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
		componentsLayout.setSizeFull();
		componentsLayout.setSpacing(false);

		HorizontalLayout buttons = new HorizontalLayout();

		Button buttonCancel = new Button("Ok", e -> close());
		buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		buttons.add(buttonCancel);

		buttons.setPadding(true);
		buttons.getStyle().set("border-top", "1px solid #ddd");


		Scroller scroller = new Scroller(componentsLayout);
		VerticalLayout outer = new VerticalLayout();
		outer.add(scroller, buttons);
		outer.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
		outer.setFlexGrow(1, scroller);
		outer.setSizeFull();
		outer.setPadding(false);
		outer.setSpacing(false);

		add(outer);

		// additional layout init
		onRecurringChanged(this.tmpEntry.isRecurring());
	}

	private static String  replaceLinks(String text) {
		String regex = "\\r?\\n\\s*";
		String newText = text.replaceAll(regex, "<br>");
		
		
		// Regex-Pattern für die Erkennung von URLs
        Pattern pattern = getLinkPattern();
        Matcher matcher = pattern.matcher(newText);

        // Ersetzen der gefundenen Links durch HTML-Link-Tags
        StringBuffer replacedText = new StringBuffer();
        while (matcher.find()) {
            String url = matcher.group();
            String linkTag = "<a href='" + url + "' target='_blank'>" + url + "</a>";
            matcher.appendReplacement(replacedText, linkTag);
        }
        matcher.appendTail(replacedText);

        // Ausgabe des resultierenden Texts mit ersetzen Links
        String result = replacedText.toString();
        log.info(result);
        return result;
	}

    private static Pattern pattern;

	private static Pattern getLinkPattern() {
		if (pattern == null) {
			String urlRegex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	        pattern = Pattern.compile(urlRegex);
		}
		
		return pattern;
	}
	protected void onSave() {
		if (onSaveConsumer == null) {
			throw new UnsupportedOperationException("No save consumer set");
		}

//		if (binder.validate().isOk()) {
//			// to prevent accidentally "disappearing" days
//			if (this.tmpEntry.isAllDay()
//					&& this.tmpEntry.getStart().toLocalDate().equals(this.tmpEntry.getEnd().toLocalDate())) {
//				this.tmpEntry.setEnd(this.tmpEntry.getEnd().plusDays(1));
//			}
//
//			// we can also create a fresh copy and leave the initial entry totally untouched
//			entry.copyFrom(tmpEntry);
//			if (recurring) {
//				entry.clearStart();
//				entry.clearEnd();
//				entry.setRecurringStart(tmpEntry.getStart());
//				entry.setRecurringEnd(tmpEntry.getEnd());
//			} else {
//				entry.setStartWithOffset(tmpEntry.getStart());
//				entry.setEndWithOffset(tmpEntry.getEnd());
//				entry.setRecurringDaysOfWeek(); // remove the DoW
//				entry.clearRecurringStart();
//				entry.clearRecurringEnd();
//			}
//
//			onSaveConsumer.accept(this.entry);
//			close();
//		}
	}

	protected void onRemove() {
		if (onDeleteConsumer == null) {
			throw new UnsupportedOperationException("No remove consumer set");
		}
		onDeleteConsumer.accept(entry);
		close();
	}

	protected void onRecurringChanged(boolean recurring) {
		/*
		if (recurring) {
			fieldStart.setLabel("Start of recurrence");
			fieldEnd.setLabel("End of recurrence");
			if (!fieldRDays.getParent().isPresent()) {
				componentsLayout.add(fieldRDays);
			}
		} else {
			fieldStart.setLabel("Start");
			fieldEnd.setLabel("End");
			fieldRDays.getElement().removeFromParent();
		}
*/
		fieldRDays.setVisible(false);
	}

	public void setDeleteConsumer(SerializableConsumer<Entry> onDeleteConsumer) {
		this.onDeleteConsumer = onDeleteConsumer;
	}

	public void setSaveConsumer(SerializableConsumer<Entry> onSaveConsumer) {
		this.onSaveConsumer = onSaveConsumer;
	}
}
