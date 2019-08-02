package org.knockout.reservationUI.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import elemental.css.CSSStyleDeclaration;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.Scheduler;
import org.vaadin.stefan.fullcalendar.SchedulerView;

import java.time.LocalDate;

@Route
public class MainView extends VerticalLayout {

    public MainView() {
        // Create a new calendar instance and attach it to our layout
        FullCalendar calendar = FullCalendarBuilder.create().withScheduler().build();
        ((Scheduler) calendar).setSchedulerLicenseKey("GPL-My-Project-Is-Open-Source");


        VerticalLayout sample = new VerticalLayout();
        sample.setMargin(false);
        sample.setSpacing(false);

        sample.add(calendar);
        sample.setFlexGrow(1, calendar);

        // Create a initial sample entry
        Entry entry = new Entry();
        entry.setTitle("Some event");
        entry.setStart(LocalDate.now().atTime(10, 0));
        entry.setEnd(entry.getStart().plusHours(2));
        entry.setColor("#ff3333");

        calendar.addEntry(entry);

        calendar.setWeekNumbersVisible(true);
        calendar.changeView(CalendarViewImpl.AGENDA_WEEK);
        calendar.setHeight(500);


        add(calendar);
        //add(new Button("Click me", e -> Notification.show("Hello Spring+Vaadin user!")));
    }
}