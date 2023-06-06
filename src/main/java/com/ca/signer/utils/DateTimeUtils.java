package com.ca.signer.utils;

import java.util.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

	//Timestamp => Datetime
	public static LocalDateTime getLocalDatetimeFromTimeStamp(Long timestamp) {
		if (timestamp == null || timestamp == 0) {
			return null;
		}
		return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	//Date => Datetime
	public static LocalDateTime getLocalDatetimeFromDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	//Date => LocalDate
	public static LocalDate getLocalDateFromDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	//LocalDate => Timestamp
	public static long getTimestampFromLocaldate(LocalDate localDate) {
		Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
		return instant.toEpochMilli();
	}

	//Str => LocalDate
	public static LocalDate getLocalDateFromStr(String dateStr) {
		return LocalDate.parse(dateStr, formatter);
	}

	//Str => LocalDate
	public static LocalDate getLocalDateFromStr(String dateStr, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDate.parse(dateStr, formatter);
	}

	public static boolean isValid(String dateStr) {
		try {
			LocalDate.parse(dateStr, formatter);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	public static long diffFromCurrent(LocalDateTime datetime) {
		return ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), datetime.toLocalDate());
		// return Period.between(LocalDateTime.now().toLocalDate(),
		// datetime.toLocalDate()).getDays();
	}

	//LocalDate => Str 
	public static String formatLocalDate(LocalDate localDate, String formatter) {
		DateTimeFormatter formatters = DateTimeFormatter.ofPattern(formatter);
		return localDate.format(formatters);
	}

	public static String convertDateFormat(String fromDateStr, String fromFormat, String toFormat) {
		LocalDate fromDate = getLocalDateFromStr(fromDateStr, fromFormat);
		String toDateStr = formatLocalDate(fromDate, toFormat);
		return toDateStr;
	}

	public static String getNidDateFromOcrDate(String fromDateStr) {
		String fromFormat = "ddMMMyyyy";
		String toFormat = "yyyy-MM-dd";
		return convertDateFormat(fromDateStr, fromFormat, toFormat);
	}
}
