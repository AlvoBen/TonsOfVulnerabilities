package com.sap.xml.datatype;

import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class GregorianCalendar extends java.util.GregorianCalendar {

	public GregorianCalendar(String str) {
		this(new StringTokenizer(str,"-"));
	}
	public GregorianCalendar(StringTokenizer toks) {
		this(toks.nextToken(), toks.nextToken(), toks.nextToken());
	}
	public GregorianCalendar(String yString, String mString, String dString) {
		this(Integer.parseInt(yString), Integer.parseInt(mString), Integer.parseInt(dString));
	}
	public GregorianCalendar(TimeZone zone) {
		super(zone);
		// TODO Auto-generated constructor stub
	}

	public GregorianCalendar(Locale aLocale) {
		super(aLocale);
		// TODO Auto-generated constructor stub
	}

	public GregorianCalendar(TimeZone zone, Locale aLocale) {
		super(zone, aLocale);
		// TODO Auto-generated constructor stub
	}

	public GregorianCalendar(int year, int month, int dayOfMonth) {
		super(year, month, dayOfMonth);
		// TODO Auto-generated constructor stub
	}

	public GregorianCalendar(int year, int month, int dayOfMonth,
			int hourOfDay, int minute) {
		super(year, month, dayOfMonth, hourOfDay, minute);
		// TODO Auto-generated constructor stub
	}

	public GregorianCalendar(int year, int month, int dayOfMonth,
			int hourOfDay, int minute, int second) {
		super(year, month, dayOfMonth, hourOfDay, minute, second);
		// TODO Auto-generated constructor stub
	}
    
    public String toString() {
        return get(YEAR) + "-" + get(MONTH) + "-" + get(DAY_OF_MONTH);
    }

}
