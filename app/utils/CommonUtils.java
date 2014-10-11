package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;

import com.fasterxml.jackson.databind.JsonNode;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestClientImpl;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;

public class CommonUtils {

    public static String getDateInstance(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return date == null ? "" : fmt.format(date);
    }

    public static String getTimeInstance(Date date) {
        return date == null ? "" : DateFormat.getTimeInstance().format(date);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return date == null ? "2014-01-01 00:00:01" : fm.format(date);
    }

    public static Date stringToDate(String dateStr) {
        if (isBlank(dateStr)) {
            return new Date();
        }
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = fm.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date afterDays(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, day);
        return c.getTime();
    }

    public static int getIntervalDays(Date fDate, Date oDate) {
        if (null == fDate || null == oDate) {
            return -1;
        }
        long intervalMilli = fDate.getTime() - oDate.getTime();
        return (int) (intervalMilli / (24 * 60 * 60 * 1000));
    }

    public static <E> List<E> toList(Iterable<E> iterable) {
        if (iterable instanceof List) {
            return (List<E>) iterable;
        }
        List<E> list = new ArrayList<E>();
        if (iterable != null) {
            for (E e : iterable) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * 
     * @param isbn
     * @return
     * @throws JSONException
     */
    public static JSONObject isbnService(String isbn) throws JSONException {
        HttpClient httpclient = new HttpClient();
        RestClient client = new RestClientImpl(httpclient);
        client.setBaseUrl("http://10.167.129.109:3000");
        RestRequest request = (RestRequest) new RestRequest().setMethod(RestRequest.Method.Get)
                .setResource("/ISBNService/" + isbn);
        RestResponse response = client.execute(request);
        return new JSONObject(response.getBody());
    }

    public static boolean isBlank(final CharSequence str) {
        if (str == null) {
            return true;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
