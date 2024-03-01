/**
 * AbstractCalendarView.java created 19.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 */
package de.anst.views.calendar;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.stefan.fullcalendar.BrowserTimezoneObtainedEvent;
import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.DatesRenderedEvent;
import org.vaadin.stefan.fullcalendar.DayNumberClickedEvent;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;
import org.vaadin.stefan.fullcalendar.EntryDroppedEvent;
import org.vaadin.stefan.fullcalendar.EntryResizedEvent;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.MoreLinkClickedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotClickedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotsSelectedEvent;
import org.vaadin.stefan.fullcalendar.ViewSkeletonRenderedEvent;
import org.vaadin.stefan.fullcalendar.WeekNumberClickedEvent;

/**
 * AbstractCalendarView created 19.02.2024 by <a href="mailto:antonius.steinkamp@gmail.com">Antonius</a>
 *
 */
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import elemental.json.Json;
import elemental.json.JsonObject;
import lombok.extern.java.Log;

/**
 * A basic class for simple calendar views, e.g. for demo or testing purposes.
 * Takes care of creating a toolbar, a description element and embedding the
 * created calendar into the view. Also registers a dates rendered listener to
 * update the toolbar.
 */
@PageTitle("Kalender")
@Route(value = "calendar")
@RouteAlias(value = "")

// @Getter
@Log
public class EventView extends VerticalLayout {

	private static final long serialVersionUID = EventView.class.hashCode();

	// private final FullCalendar calendar;

	private final EntryProvider entryProvider;

	public EventView() {
		this.entryProvider = new EntryProvider();

		FullCalendar calendar = FullCalendarBuilder.create().withInitialOptions(createDefaultInitialOptions())
				.withInitialEntries(initialEntries()).withEntryLimit(3).build();

		log.info(calendar.getClass().getSimpleName() + " Version "
				+ FullCalendar.class.getPackage().getImplementationVersion());
		log.info(log.getClass().getSimpleName() + " Version " + log.getClass().getPackage().getImplementationVersion());

		calendar.setBusinessHours(new BusinessHours(LocalTime.of(9, 0), LocalTime.of(17, 0), BusinessHours.DEFAULT_BUSINESS_WEEK));

		calendar.addEntryClickedListener(this::onEntryClick);
		calendar.addEntryDroppedListener(this::onEntryDropped);
		calendar.addEntryResizedListener(this::onEntryResized);
		calendar.addDayNumberClickedListener(this::onDayNumberClicked);
		calendar.addBrowserTimezoneObtainedListener(this::onBrowserTimezoneObtained);
		calendar.addMoreLinkClickedListener(e -> {
			calendar.getElement().setProperty("moreLinkClickAction", "day");
		});
		calendar.addTimeslotClickedListener(this::onTimeslotClicked);
		calendar.addTimeslotsSelectedListener(this::onTimeslotsSelected);
		calendar.addViewSkeletonRenderedListener(this::onViewSkeletonRendered);
		calendar.addDatesRenderedListener(this::onDatesRendered);
		calendar.addWeekNumberClickedListener(this::onWeekNumberClicked);

		calendar.setSlotMinTime(LocalTime.of(7, 0));
		calendar.setSlotMaxTime(LocalTime.of(17, 0));
		
		calendar.setLocale(Locale.GERMAN);

		var ueberschrift = new H2("Termine für Höltinghausen") ;
		add( ueberschrift);

		var controller = initDateItems(calendar);
		add(controller);

		add(calendar);

		setHorizontalComponentAlignment(Alignment.CENTER, ueberschrift, controller);
		
		setFlexGrow(1, calendar);
		setHorizontalComponentAlignment(Alignment.STRETCH, calendar);

		setSizeFull();
	}

	private List<Entry> initialEntries() {
		List<Entry> result = new ArrayList<>();

		for (CalendarInfo ci : entryProvider.getVisibleCalendars()) {
			log.info(ci.toString());
			result.addAll(ci.getEntries());
		}
//		
//		Entry weeklyEntry = new Entry();
//		
//		weeklyEntry.setRecurringStart(LocalDateTime.now().minusDays(36));
//		weeklyEntry.setStart(Instant.now());
//		weeklyEntry.setEnd(Instant.now().plusSeconds(7200));
//		
//		weeklyEntry.setRecurringDaysOfWeek(DayOfWeek.THURSDAY);
//		
//		result.add(weeklyEntry);
	
		return result;
	}

	private Button buttonDatePicker;

	private Component initDateItems(FullCalendar calendar) {
		var hv = new HorizontalLayout();
		
		var dater = new HorizontalLayout();
		Button buttonLeft = new Button(VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
		buttonLeft.setId("period-previous-button");
		dater.add(buttonLeft);

		// simulate the date picker light that we can use in polymer
		DatePicker gotoDate = new DatePicker();
		gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
		gotoDate.getElement().getStyle().set("visibility", "hidden");
		gotoDate.getElement().getStyle().set("position", "fixed");
		gotoDate.setWidth("0px");
		gotoDate.setHeight("0px");
		gotoDate.setWeekNumbersVisible(true);
		// add(gotoDate);
		buttonDatePicker = new Button(VaadinIcon.CALENDAR.create());
		buttonDatePicker.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		// buttonDatePicker.getStyle().set("width", "15em");
		buttonDatePicker.getElement().appendChild(gotoDate.getElement());
		buttonDatePicker.addClickListener(event -> gotoDate.open());

		dater.add(buttonDatePicker);
		
		dater.add(new Button(VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next()));
		
		hv.add(dater);
		
		hv.add(new Button("Heute", e -> calendar.today()));

		var calendarsList = new MultiSelectListBox<String>();
		calendarsList.setTooltipText("Kalender");
		calendarsList.setItems(entryProvider.getCalendarNames());
		calendarsList.select(entryProvider.getVisibleCalendarNames());

		calendarsList.addSelectionListener(e -> {
			log.info("Selected: " + e.getAllSelectedItems().toString());
			log.info("Removed: " + e.getRemovedSelection());
			log.info("Added: " + e.getAddedSelection());

			var ep = calendar.getEntryProvider().asInMemory();
			e.getAddedSelection().forEach(cal -> ep.addEntries(entryProvider.byName(cal).getEntries()));
			e.getRemovedSelection().forEach(cal -> ep.removeEntries(entryProvider.byName(cal).getEntries()));

			ep.refreshAll();
		});

		Map<String, String> ansichten = Map.of("dayGridMonth", "Monatsansicht", "dayGridWeek", "Wochenansicht",
				"dayGridYear", "Jahresansicht", "listDay", "Liste Tag", "listWeek", "Liste Woche", "listMonth",
				"Liste Monat", "listYear", "Liste Jahr");

		List<String> viewNames = List.of("dayGridMonth", "dayGridWeek", "dayGridYear", "listDay", "listWeek",
				"listMonth", "listYear");

		var views = new ComboBox<String>("Ansichten", viewNames);
		views.setValue("dayGridMonth");
		views.setItemLabelGenerator(e -> ansichten.get(e));
		views.addValueChangeListener(e -> {
			log.info(e.toString());
			String neuerViewName = e.getValue();
			log.info("Neue View ist: " + neuerViewName);
			calendar.getElement().callJsFunction("changeView", neuerViewName);
		});

		SetupDialog setupDialog = new SetupDialog(calendarsList, views);
		setupDialog.setDraggable(true);
		setupDialog.addDialogCloseActionListener((e) -> {
			e.getSource().close();
			calendar.getEntryProvider().refreshAll();
		});
		setupDialog.setCloseListener( () -> {
			calendar.getEntryProvider().refreshAll();
		});
		hv.add(new Button(LineAwesomeIcon.COG_SOLID.create(), e -> setupDialog.open() ));


		hv.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

		return hv;
	}

	protected boolean isToolbarDateChangeable() {
		return true;
	}

	protected boolean isToolbarViewChangeable() {
		return true;
	}

	protected boolean isToolbarSettingsAvailable() {
		return true;
	}

	/**
	 * Creates a default set of initial options.
	 *
	 * @return initial options
	 */
	protected JsonObject createDefaultInitialOptions() {
		JsonObject initialOptions = Json.createObject();
		JsonObject eventTimeFormat = Json.createObject();
		eventTimeFormat.put("hour", "2-digit");
		eventTimeFormat.put("minute", "2-digit");
		eventTimeFormat.put("meridiem", false);
		eventTimeFormat.put("hour12", false);
		initialOptions.put("eventTimeFormat", eventTimeFormat);
		return initialOptions;
	}

	/**
	 * Called by the calendar's entry click listener. Noop by default.
	 * 
	 * @see FullCalendar#addEntryClickedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onEntryClick(EntryClickedEvent event) {
		log.info("onEntryClick" + event.toString());
        DemoDialog dialog = new DemoDialog(event.getEntry());
        /*
        dialog.setSaveConsumer(this::onEntryChanged);
        dialog.setDeleteConsumer(e -> onEntriesRemoved(Collections.singletonList(e)));
        */
        dialog.open();

	}

	/**
	 * Called by the calendar's entry drop listener (i. e. an entry has been dragged
	 * around / moved by the user). Applies the changes to the entry and calls
	 * {@link #onEntryChanged(Entry)} by default.
	 * 
	 * @see FullCalendar#addEntryDroppedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onEntryDropped(EntryDroppedEvent event) {
		event.applyChangesOnEntry();
		log.info("onEntryDropped" + event.toString());
	}

	/**
	 * Called by the calendar's entry resize listener. Applies the changes to the
	 * entry and calls {@link #onEntryChanged(Entry)} by default.
	 * 
	 * @see FullCalendar#addEntryResizedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onEntryResized(EntryResizedEvent event) {
		event.applyChangesOnEntry();
		log.info("onEntryResized" + event.toString());
	}

	/**
	 * Called by the calendar's week number click listener. Noop by default.
	 * 
	 * @see FullCalendar#addWeekNumberClickedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onWeekNumberClicked(WeekNumberClickedEvent event) {
		log.info("onWeekNumberClicked" + event.toString());
	}

	/**
	 * Called by the calendar's dates rendered listener. Noop by default. Please
	 * note, that there is a separate dates rendered listener taking care of
	 * updating the toolbar.
	 * 
	 * @see FullCalendar#addDatesRenderedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onDatesRendered(DatesRenderedEvent event) {
		log.info("onDatesRendered" + event.toString());
		if (buttonDatePicker != null) {
			buttonDatePicker.setText(event.getIntervalStart()
					.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.getDefault())));
		}
	}

	/**
	 * Called by the calendar's view skeleton rendered listener. Noop by default.
	 * 
	 * @see FullCalendar#addViewSkeletonRenderedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onViewSkeletonRendered(ViewSkeletonRenderedEvent event) {
		log.info("onViewSkeletonRendered" + event.toString());
	}

	/**
	 * Called by the calendar's timeslot selected listener. Noop by default.
	 * 
	 * @see FullCalendar#addTimeslotsSelectedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onTimeslotsSelected(TimeslotsSelectedEvent event) {
		log.info("onTimeslotsSelected" + event.toString());
	}

	/**
	 * Called by the calendar's timeslot clicked listener. Noop by default.
	 * 
	 * @see FullCalendar#addTimeslotClickedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onTimeslotClicked(TimeslotClickedEvent event) {
		log.info("onTimeslotClicked" + event.toString());
	}

	/**
	 * Called by the calendar's "more" link clicked listener. Noop by default.
	 * 
	 * @see FullCalendar#addMoreLinkClickedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onMoreLinkClicked(MoreLinkClickedEvent event) {
		log.info("onMoreLinkClicked" + event.toString());
	}

	/**
	 * Called by the calendar's browser timezone obtained listener. Noop by default.
	 * Please note, that the full calendar builder registers also a listener, when
	 * the {@link FullCalendarBuilder#withAutoBrowserTimezone()} option is used.
	 * 
	 * @see FullCalendar#addBrowserTimezoneObtainedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onBrowserTimezoneObtained(BrowserTimezoneObtainedEvent event) {
		log.info("onBrowserTimezoneObtained" + event.toString());
	}

	/**
	 * Called by the calendar's day number click listener. Noop by default.
	 * 
	 * @see FullCalendar#addDayNumberClickedListener(ComponentEventListener)
	 * @param event event
	 */
	protected void onDayNumberClicked(DayNumberClickedEvent event) {
		log.info("onDayNumberClicked" + event.toString());
	}

	protected Component createDescriptionElement() {
		var descriptionElement = new H2("Termine für Höltinghausen");

		return descriptionElement;
	}

}
