package com.jkaref.viadrina.portlet.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import com.jkaref.viadrina.portlet.param.Param;
import com.liferay.portal.kernel.util.StringPool;

public class ParamUtil extends com.liferay.portal.kernel.util.ParamUtil {
	
	private static final Integer DEFAULT_DELTA = 20;
	private static final Integer DEFAULT_CUR = 1;
	private static final Integer DEFAULT_LIMIT = 20;
	private static final String DEFAULT_CALENDAR_ID = StringPool.BLANK;
	private static final String DEFAULT_CALENDAR_URL = "/veranstaltungskalender/";
	private static final String DEFAULT_HIDDEN_EVENTS_JSON = "[]";
	private static final String DEFAULT_BACKGROUND_COLOR = "#52a5fa";
	private static final String DEFAULT_SELECTED_CALENDARS = StringPool.BLANK;

	private static final String LIMIT = Param.LIMIT.string();
	private static final String CALENDAR_ID = Param.CALENDAR_ID.string();
	private static final String CALENDAR_URL = Param.CALENDAR_PORTLET_URL.string();
	private static final String HIDDEN_EVENTS_JSON = Param.HIDDEN_EVENTS_JSON.string();
	private static final String BACKGROUND_COLOR = Param.BACKGROUND_COLOR.string();
	private static final String SELECTED_CALENDARS = Param.SELECTED_CALENDARS.string();
	private static final String CUR = Param.CUR.string();
	private static final String DELTA = Param.DELTA.string();
	
	public static String getLimit(PortletPreferences prefs) {

		return prefs.getValue(LIMIT, String.valueOf(DEFAULT_LIMIT));

	}

	public static String getLimit(PortletRequest request) {

		return getString(request, LIMIT, String.valueOf(DEFAULT_LIMIT));

	}

	public static String getCalendarId(PortletPreferences prefs) {

		return prefs.getValue(CALENDAR_ID, DEFAULT_CALENDAR_ID);
	}

	public static String getCalendarId(PortletRequest request) {

		return getString(request, CALENDAR_ID, DEFAULT_CALENDAR_ID);

	}

	public static String getHiddenEventsJson(PortletPreferences prefs) {

		return prefs.getValue(HIDDEN_EVENTS_JSON, DEFAULT_HIDDEN_EVENTS_JSON);

	}

	public static String getHiddenEventsJson(PortletRequest request) {

		return getString(request, HIDDEN_EVENTS_JSON, DEFAULT_HIDDEN_EVENTS_JSON);

	}

	public static String getBackgroundColor(PortletPreferences prefs) {
		
		return prefs.getValue(BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
		
	}

	public static String getBackgroundColor(PortletRequest request) {

		return getString(request, BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
		
	}

	public static List<Long> getCalendarIds(PortletRequest request) {
		
		List<Long> result = Collections.emptyList();
		
		String selectedCalendarIds = request.getPreferences().getValue(
				SELECTED_CALENDARS, DEFAULT_SELECTED_CALENDARS);
		
		if (!selectedCalendarIds.isEmpty()) {
			List<String> list = Arrays.asList(selectedCalendarIds.split(StringPool.COMMA));

			result = new ArrayList<Long>();
			for (String id : list) {
				result.add(Long.valueOf(id));
			}

		}
	
		return result;
				
	}

	public static int getCurrent(PortletRequest request) {
		
		return getInteger(request, CUR, DEFAULT_CUR);
		
	}

	public static int getDelta(PortletRequest request) {
		
		return getInteger(request, DELTA, DEFAULT_DELTA);
	}

	public static String getCalendarUrl(PortletPreferences prefs) {
		
		return prefs.getValue(CALENDAR_URL, DEFAULT_CALENDAR_URL);
		
	}
	
	public static String getCalendarUrl(PortletRequest request) {
		
		return getString(request, CALENDAR_URL, DEFAULT_CALENDAR_URL);
		
	}

}
