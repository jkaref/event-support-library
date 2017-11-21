package com.jkaref.viadrina.portlet.service;


import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalServiceUtil;
import com.liferay.calendar.service.CalendarLocalServiceUtil;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

public class ServiceUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceUtil.class);
	
	private static final ObjectMapper JSON = new ObjectMapper();

	private static final ClassLoader CLASS_LOADER = 
			(ClassLoader) PortletBeanLocatorUtil.locate("calendar-portlet", "portletClassLoader");

	
	public static List<Calendar> getCalendars() {

		List<Calendar> result = Collections.emptyList();

		try {

			result = CalendarLocalServiceUtil.getCalendars(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			LOG.trace("[getCalendars] - Found {} {}.", 
					result.size(), result.size() == 1 ? "calendar" : "calendars");

		} catch (Exception e) {
			LOG.warn("[getCalendars] - Failed to retrieve calendars, using empty list.", e);
		}
		return result;
	}

	public static List<CalendarBooking> getVisibleEvents(String limit, String calendarId) {

		return getVisibleEvents(limit, calendarId, StringPool.BLANK);
	}

    @SuppressWarnings("unchecked")
	public static List<CalendarBooking> getVisibleEvents(String limit, String calendarId, String hiddenEventIds) {

		List<CalendarBooking> result = Collections.emptyList();

		try {
			
			if(Validator.isNotNull(calendarId) && !calendarId.isEmpty()) {
							
				DynamicQuery hiddenQuery = createHiddenQuery(hiddenEventIds);
				DynamicQuery query = createVisibleQuery(limit, calendarId, hiddenQuery);
	
				result = CalendarBookingLocalServiceUtil.dynamicQuery(query);
			}

			LOG.trace("[getVisibleEvents] - Found {} visible {}.", 
					result.size(), result.size() == 1 ? "event" : "events");

		} catch (Exception e) {
			LOG.warn("[getVisibleEvents] - Failed to retrieve list of visible calender events, using empty list.", e);
		}

		return result;
	}
    
	@SuppressWarnings("unchecked")
	public static List<CalendarBooking> getHiddenEvents(String hiddenEventsIds) {

		List<CalendarBooking> result = Collections.emptyList();

		try {

			DynamicQuery query = createHiddenQuery(hiddenEventsIds);
			
			if(Validator.isNotNull(query))
				result = CalendarBookingLocalServiceUtil.dynamicQuery(query);

			LOG.trace("[getHiddenEvents] - Found {} hidden {}.",
					result.size(), result.size() == 1 ? "event" : "events");

		} catch (Exception e) {
			LOG.warn("[getHiddenEvents] - Failed to retrieve list of hidden calender events, using empty list.", e);
		}

		return result;
	}
    



	public static List<KeyValuePair> getSelectedCalendars(List<Calendar> calendars, List<Long> selectedCalendarIds) {
				
		return calendars.stream()
				.filter(filterSelected(selectedCalendarIds))
				.map(toKeyValuePair).collect(Collectors.toList());
		
	}

	public static List<KeyValuePair> getAvailableCalendars(List<Calendar> calendars, List<Long> selectedCalendarIds) {
		return getCalendars()
				.stream()
				.filter(filterAvailable(selectedCalendarIds))
				.map(toKeyValuePair)
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public static List<CalendarBooking> getCalendarBookings(List<Long> calendarIds, int current, int delta) {
		
		List<CalendarBooking> result = Collections.emptyList();
		
		if(!calendarIds.isEmpty()) {
			int start = getStart(current, delta);
			int end = getEnd(current, delta);
			
			DynamicQuery query = createQuery();		
			query.add(PropertyFactoryUtil.forName("calendarId").in(calendarIds));
			query.add(RestrictionsFactoryUtil.ne("status", WorkflowConstants.STATUS_IN_TRASH));
			query.addOrder(OrderFactoryUtil.desc("startTime"));
			
			try {
				result = CalendarBookingLocalServiceUtil.dynamicQuery(query, start, end);
				
				LOG.trace("[getCalendarBookings] - Found {} {}.", 
						result.size(), result.size() == 1 ? "event" : "events");
				
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			LOG.warn("[getCalendarBookings] - Failed to retrieve events because there is no calendar selected.");
		}
		
		return result;
	}

	public static long getCalendarBookingsCount(List<Long> calendarIds) {
		long result = 0;
		
		if(!calendarIds.isEmpty()) {
			DynamicQuery query = createQuery();
			query.add(PropertyFactoryUtil.forName("calendarId").in(calendarIds));
			
			try {
				result = CalendarBookingLocalServiceUtil.dynamicQueryCount(query);
				
				LOG.trace("[getCalendarBookingsCount] - Count is {}", result);
				
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		return result;
	}

	private static DynamicQuery createVisibleQuery(String limit, String calendarId, DynamicQuery hiddenQuery) {

		DynamicQuery query = createQuery();

		Long calendarIdLong = Long.parseLong(calendarId);
		Integer limitInt = Integer.parseInt(limit);

		query.addOrder(OrderFactoryUtil.asc("startTime"));
		query.setLimit(0, limitInt);

		query.add(RestrictionsFactoryUtil.gt("startTime", System.currentTimeMillis()));
		query.add(RestrictionsFactoryUtil.ne("status", WorkflowConstants.STATUS_IN_TRASH));
		query.add(RestrictionsFactoryUtil.eq("calendarId", calendarIdLong));

		if (Validator.isNotNull(hiddenQuery))
			query.add(PropertyFactoryUtil.forName("uuid").notIn(hiddenQuery));

		return query;
	}

	private static DynamicQuery createHiddenQuery(String hiddenEventsJson)
			throws JsonParseException, JsonMappingException, IOException {

		DynamicQuery query = null;
		
		Collection<String> hiddenEventIds = getEventIdsFromJson(hiddenEventsJson);
		
		if(!hiddenEventIds.isEmpty()) {
			query = createQuery();
			query.add(PropertyFactoryUtil.forName("uuid").in(hiddenEventIds));	
			query.setProjection(ProjectionFactoryUtil.groupProperty("uuid"));
		}
		
		return query;
	}

	private static DynamicQuery createQuery() {

		return DynamicQueryFactoryUtil.forClass(CalendarBooking.class, CLASS_LOADER);
	}
	
	@SuppressWarnings("unchecked")
	private static Collection<String> getEventIdsFromJson(String json) {
		
		Collection<String> result = Collections.emptyList();
		
		if (Validator.isNotNull(json) && !json.isEmpty()) { 
			
			try {
				result = JSON.readValue(json, Collection.class);
			} catch (Exception e) {
				
				e.printStackTrace();
			} 			
		}
		
		return result;
		
	}
	
	private static Function<Calendar, KeyValuePair> toKeyValuePair = new Function<Calendar, KeyValuePair>() {
		
		public KeyValuePair apply(Calendar t) {
			
			String calendarId = String.valueOf(t.getCalendarId());
			
			
			return new KeyValuePair(calendarId, t.getNameCurrentValue());
			
		};
		
	};
	
	private static Predicate<Calendar> filterAvailable(List<Long> selectedCalendarIds) {
		return c -> !selectedCalendarIds.contains(c.getCalendarId());
	}

	private static Predicate<Calendar> filterSelected(List<Long> selectedCalendarIds) {
		return c -> selectedCalendarIds.contains(c.getCalendarId());
	}
	

	private static int getStart(int current, int delta) {
		return (current * delta) - delta;
	}

	private static int getEnd(int current, int delta) {
		return current * delta;
	}
	
	
}
