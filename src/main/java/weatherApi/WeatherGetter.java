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
	
	
	private static String baseURL = "http://api.apixu.com/v1/";
	private static String today = "current.json?key=";
	private static String forecast = "forecast.json?key="; //+query +days=numero
	private static String search = "search.json?key=";
	private static String key = "bdd434f5875b481e92f142643170806";
	private static String query ="&q=";
	private static String city = "Vercelli";
	private static String zip = "13100";
	private static String days = "&days=";
	private static String ndays = "7";
	private static Map<String, ?> weather;
	
	public static void init()
	{
		weather = HttpClientUtil.get(baseURL+today+key+query+city);
	}
	
	public static String getExternalTemp()
	{
		Double x =  (Double) ((Map<String,?>) weather.get("current")).get("temp_c");
		
		return ""+x;
	}
	
}