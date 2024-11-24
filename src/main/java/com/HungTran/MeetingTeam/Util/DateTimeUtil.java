package com.HungTran.MeetingTeam.Util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;



@Component
public class DateTimeUtil {
	private DateTimeFormatter formatter=DateTimeFormatter.ofPattern("HH:mm dd/MM/YYYY");

	public String format(LocalDateTime time) {
		return formatter.format(time);
	}
	public LocalDateTime parse(String time) {
		Instant instant = Instant.ofEpochMilli(Long.parseLong(time));
		LocalDateTime localDateTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime();
		return localDateTime;
	}
	public List<LocalDateTime> getWeekRange(Integer week){
		var today=LocalDateTime.now();
		Integer currentDayOfWeek=today.getDayOfWeek().getValue();
		// Calculate the start date of the week
		var startDate=today.plusDays(week* 7+1-currentDayOfWeek)
					.withHour(0).withMinute(0).withSecond(0);;
		// Calculate the end date of the week
		var endDate=startDate.plusDays(6).withHour(23).withMinute(59).withSecond(59);
		return List.of(startDate,endDate);
	}
}
