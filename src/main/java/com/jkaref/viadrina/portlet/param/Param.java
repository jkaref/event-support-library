package com.jkaref.viadrina.portlet.param;

public enum Param {
	LIMIT,
	CALENDARS,
	CALENDAR_ID,
	VISIBLE_EVENTS,
	HIDDEN_EVENTS,
	HIDDEN_EVENTS_JSON, 
	BACKGROUND_COLOR,
	SELECTED_CALENDARS,
	CUR,
	DELTA;
	
	public String string() {
		return this.toString().toLowerCase();
	}

}
