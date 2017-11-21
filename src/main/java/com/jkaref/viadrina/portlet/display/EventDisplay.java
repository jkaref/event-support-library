package com.jkaref.viadrina.portlet.display;


import java.util.Calendar;

import com.liferay.calendar.model.CalendarBooking;

public enum EventDisplay {
    SINGLE_DAY,
    MULTI_DAY,
    MUTLI_MONTH;

    public static final long DAY_IN_MILLIES = 86400000;

    public static EventDisplay fromEvent(CalendarBooking event) {

        if (event.getEndTime() - event.getStartTime() > DAY_IN_MILLIES) {

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();

            start.setTimeInMillis(event.getStartTime());
            end.setTimeInMillis(event.getEndTime());

            if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH))
                return MULTI_DAY;

            else
                return MUTLI_MONTH;

        } else {
            return SINGLE_DAY;
        }

    }


}
