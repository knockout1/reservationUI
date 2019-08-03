package org.knockout.reservationUI.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.Resource;
import org.vaadin.stefan.fullcalendar.ResourceEntry;
import org.vaadin.stefan.fullcalendar.Scheduler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Route
public class MainView extends VerticalLayout {

    public MainView() {
        FullCalendar calendar = FullCalendarBuilder.create().withScheduler().build();
        ((Scheduler) calendar).setSchedulerLicenseKey("GPL-My-Project-Is-Open-Source");


        VerticalLayout sample = new VerticalLayout();
        sample.setMargin(false);
        sample.setSpacing(false);

        sample.add(calendar);
        sample.setFlexGrow(1, calendar);

        calendar.setWeekNumbersVisible(true);
        calendar.changeView(CalendarViewImpl.AGENDA_WEEK);
        calendar.setHeight(500);


        calendar.addTimeslotClickedListener(event -> {
            Entry entry = new Entry();

            LocalDateTime start = event.getDateTime();
            entry.setStart(start);
            entry.setEditable(false);

            boolean allDay = event.isAllDay();
            entry.setAllDay(allDay);
            entry.setEnd(allDay ? start.plusDays(FullCalendar.DEFAULT_DAY_EVENT_DURATION) : start.plusHours(FullCalendar.DEFAULT_TIMED_EVENT_DURATION));

            new DemoDialog(calendar, entry, true).open();
        });

        //calendar.addEntryClickedListener(event -> new DemoDialog(calendar, event.getEntry(), false).open());
        add(calendar);
    }

    public static class DemoDialog extends Dialog {
        private static final String[] COLORS = {"tomato", "orange", "dodgerblue", "mediumseagreen", "gray", "slateblue", "violet"};

        DemoDialog(FullCalendar calendar, Entry entry, boolean newInstance) {
            setCloseOnEsc(true);
            setCloseOnOutsideClick(true);

            VerticalLayout layout = new VerticalLayout();
            layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
            layout.setSizeFull();

            TextField fieldTitle = new TextField("Title");
            fieldTitle.focus();

            ComboBox<String> fieldColor = new ComboBox<>("Color", COLORS);
            TextArea fieldDescription = new TextArea("Description");

            layout.add(fieldTitle, fieldColor, fieldDescription);

            TextField fieldStart = new TextField("Start");
            fieldStart.setReadOnly(true);

            TextField fieldEnd = new TextField("End");
            fieldEnd.setReadOnly(true);

            fieldStart.setValue(calendar.getTimezone().formatWithZoneId(entry.getStartUTC()));
            fieldEnd.setValue(calendar.getTimezone().formatWithZoneId(entry.getEndUTC()));

            Checkbox fieldAllDay = new Checkbox("All day event");
            fieldAllDay.setValue(entry.isAllDay());
            fieldAllDay.setReadOnly(true);

            layout.add(fieldStart, fieldEnd, fieldAllDay);

            if (entry instanceof ResourceEntry && ((ResourceEntry) entry).getResource().isPresent()) {
                TextArea fieldResource = new TextArea("Assigned resources");
                fieldResource.setReadOnly(true);
                fieldResource.setValue(((ResourceEntry) entry).getResources().stream().map(Resource::getTitle).collect(
                        Collectors.joining(", ")));
                layout.add(fieldResource);
            }

            Binder<Entry> binder = new Binder<>(Entry.class);
            binder.forField(fieldTitle)
                    .asRequired()
                    .bind(Entry::getTitle, Entry::setTitle);

            binder.bind(fieldColor, Entry::getColor, Entry::setColor);
            binder.bind(fieldDescription, Entry::getDescription, Entry::setDescription);
            binder.setBean(entry);

            HorizontalLayout buttons = new HorizontalLayout();
            Button buttonSave;
            if (newInstance) {
                buttonSave = new Button("Create", e -> {
                    if (binder.validate().isOk()) {
                        calendar.addEntry(entry);
                    }
                });
            } else {
                buttonSave = new Button("Save", e -> {
                    if (binder.validate().isOk()) {
                        calendar.updateEntry(entry);
                    }
                });
            }
            buttonSave.addClickListener(e -> close());
            buttons.add(buttonSave);

            Button buttonCancel = new Button("Cancel", e -> close());
            buttonCancel.getElement().getThemeList().add("tertiary");
            buttons.add(buttonCancel);

            if (!newInstance) {
                Button buttonRemove = new Button("Remove", e -> {
                    calendar.removeEntry(entry);
                    close();
                });
                ThemeList themeList = buttonRemove.getElement().getThemeList();
                themeList.add("error");
                themeList.add("tertiary");
                buttons.add(buttonRemove);
            }

            add(layout, buttons);
        }
    }
}