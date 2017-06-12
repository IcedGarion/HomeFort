package weatherApi;

import java.util.Map;
import hueController.HttpClientUtil;

public class WeatherGetter 
{
		//Apixu key bdd434f5875b481e92f142643170806
		//apixu base url "http://api.apixu.com/v1
		/* ESEMPI RICHIESTA JSON
		 ** current weather for London: 
		   	http://api.apixu.com/v1/current.json?key=<YOUR_API_KEY>&q=London
		   	=> baseURL+today+key+query+city
		   	
		 ** 7 days weather for US ZipCode 07112
		    http://api.apixu.com/v1/forecast.json?key=<YOUR_API_KEY>&q=07112&days=7
		    =>baseURL+forecast+key+query+zip+days+ndays
		    
		 ** search for cities starting with Lond
		 	http://api.apixu.com/v1/search.json?key=<YOUR_API_KEY>&q=lond
		 	baseURL+today+key+query+city
		 */

	//private static String today = "current.json?key=";
	private static String baseURL = "http://api.apixu.com/v1/current.json?key=bdd434f5875b481e92f142643170806&q=";
	private static String URL = "http://api.apixu.com/v1/forecast.json?key=bdd434f5875b481e92f142643170806&q=";
	private static String city = "";
	private static String zip = "13100";
	private static String days = "&days=";

	
	private static Map<String, ?> weather;
	
	
	public static void init(String cityUser, int userDays)
	{
		city=cityUser;
		if(userDays == 1)
			weather = HttpClientUtil.get(baseURL+city);
		else
			weather = HttpClientUtil.get(URL+city+days+userDays);
	}
	
	public static String getExternalWeather()
	{
		Double extTemp =  (Double) ((Map<String,?>) weather.get("current")).get("temp_c");
		Double extWeatherCode = (Double) ((Map<String,?>) ((Map<String,?>) weather.get("current")).get("condition")).get("code");
		String extWeatherDescr = (String) ((Map<String,?>)((Map<String,?>) weather.get("current")).get("condition")).get("text");
		return ""+extTemp+" "+extWeatherCode+" "+extWeatherDescr;
	}

	public static String getForecast() 
	{
		return null;
	}

}