package edu.fudan.eservice.common.struts;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.XWorkException;


@SuppressWarnings("rawtypes")
public class EserviceDateConverter extends StrutsTypeConverter {


	@Override
	
	public Object convertFromString( Map context, String[] values, Class toType) {
		Date result = null;
		if(values==null ||values.length<=0)
			return result;
		String value=values[0];
        if (value instanceof String && value != null && ((String) value).length() > 0) {
            String sa = (String) value;
            Locale locale = Locale.CHINA;

            DateFormat df = null;
            if (java.sql.Time.class == toType) {
                df = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
            } else if (java.sql.Timestamp.class == toType) {
                Date check = null;
                SimpleDateFormat dtfmt = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.MEDIUM,
                        locale);
                SimpleDateFormat fullfmt = new SimpleDateFormat(dtfmt.toPattern() + MILLISECOND_FORMAT,
                        locale);

                SimpleDateFormat dfmt = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT,
                        locale);

                SimpleDateFormat[] fmts = {fullfmt, dtfmt, dfmt};
                for (SimpleDateFormat fmt : fmts) {
                    try {
                        check = fmt.parse(sa);
                        df = fmt;
                        if (check != null) {
                            break;
                        }
                    } catch (ParseException ignore) {
                    }
                }
            } else if (java.util.Date.class == toType) {
                Date check;
                DateFormat[] dfs = getDateFormats(locale);
                for (DateFormat df1 : dfs) {
                    try {
                        check = df1.parse(sa);
                        df = df1;
                        if (check != null) {
                            break;
                        }
                    } catch (ParseException ignore) {
                    }
                }
            }
            //final fallback for dates without time
            if (df == null) {
                df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
            }
            try {
                df.setLenient(false); // let's use strict parsing (XW-341)
                result = df.parse(sa);
                if (!(Date.class == toType)) {
                    try {
                        @SuppressWarnings("unchecked")
						Constructor constructor = toType.getConstructor(new Class[]{long.class});
                        return constructor.newInstance(new Object[]{Long.valueOf(result.getTime())});
                    } catch (Exception e) {
                        throw new XWorkException("Couldn't create class " + toType + " using default (long) constructor", e);
                    }
                }
            } catch (ParseException e) {
                throw new XWorkException("Could not parse date", e);
            }
        }
        return result;
    }

    private DateFormat[] getDateFormats(Locale locale) {
        DateFormat dt1 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale);
        DateFormat dt2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        DateFormat dt3 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);

        DateFormat d1 = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        DateFormat d2 = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        DateFormat d3 = DateFormat.getDateInstance(DateFormat.LONG, locale);

        DateFormat rfc3399 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        return new DateFormat[]{dt1, dt2, dt3, rfc3399, d1, d2, d3};
    }


	@Override
	public String convertToString(Map context, Object value) {
		if(value==null)
			return "";
		if(value instanceof Date)
		{
			if(((Date) value).getTime()%(24*3600000)==0)
				return new SimpleDateFormat("yyyy-MM-dd").format(value);
			else
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
		}else
			return value.toString();
	}
}
