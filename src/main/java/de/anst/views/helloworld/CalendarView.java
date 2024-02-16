package de.anst.views.helloworld;

import java.time.DayOfWeek;

import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Kalender")
@Route(value = "calendar")
@RouteAlias(value = "")
public class CalendarView extends HorizontalLayout {

	private static final long serialVersionUID = CalendarView.class.hashCode();

    public CalendarView() {
    	// Create a new calendar instance and attach it to our layout
    	FullCalendar calendar = FullCalendarBuilder.create().build();
    	 calendar.setBusinessHours(new BusinessHours(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
    	setFlexGrow(1, calendar);
        add(calendar);
    }

}
