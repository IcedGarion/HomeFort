package weatherApi;

import java.util.ArrayList;
import java.util.Map;
import hueController.HttpClientUtil;

public class WeatherGetter 
{
		//Apixu key 2a3787deb793405c99181134171206
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
	private static String baseURL = "http://api.apixu.com/v1/current.json?key=2a3787deb793405c99181134171206&q=";
	private static String URL = "http://api.apixu.com/v1/forecast.json?key=2a3787deb793405c99181134171206&q=";
	private static String city = "";
	private static String zip = "13100";
	private static String days = "&days=";
	private static Map<String, ?> weather;
	
	public static void init(String cityUser, int userDays)
	{
		city=cityUser;
		if(userDays == 1)
			weather = null;
			//weather = HttpClientUtil.get(baseURL+city);
		else
			weather = null;
			//weather = HttpClientUtil.get(URL+city+days+userDays);
	}
	
	public static String getExternalWeather()
	{
		Map<String,?> current = (Map<String,?>) weather.get("current");
		
		Double extTemp =  (Double) current.get("temp_c");
		Double extWeatherCode = (Double) ((Map<String,?>) current.get("condition")).get("code");
		String extWeatherDescr = (String) ((Map<String,?>) current.get("condition")).get("text");
		return ""+extTemp+" "+extWeatherCode+" "+extWeatherDescr;
	}

	public static String getForecast() 
	{
		String[] day = new String[3];
		Double[] maxT = new Double[3];
		Double[] minT = new Double[3];
		String[] cond = new String[3];
		Double[] code = new Double[3];
		String[] sRise = new String[3];
		String[] sSet = new String[3];
		ArrayList<Map<String,?>> forecast = new ArrayList<Map<String,?>>();
		
		forecast.add((Map<String, ?>) ((ArrayList<?>) ((Map<String, ?>) weather.get("forecast")).get("forecastday")).get(0));
		forecast.add((Map<String, ?>) ((ArrayList<?>) ((Map<String, ?>) weather.get("forecast")).get("forecastday")).get(1));
		forecast.add((Map<String, ?>) ((ArrayList<?>) ((Map<String, ?>) weather.get("forecast")).get("forecastday")).get(2));
		
		day[0] = (String) forecast.get(0).get("date");
		day[1] = (String) forecast.get(1).get("date");
		day[2] = (String) forecast.get(2).get("date");
		
		maxT[0] = (Double) ((Map<String,?>) forecast.get(0).get("day")).get("maxtemp_c");
		maxT[1] = (Double) ((Map<String,?>) forecast.get(1).get("day")).get("maxtemp_c");
		maxT[2] = (Double) ((Map<String,?>) forecast.get(2).get("day")).get("maxtemp_c");

		minT[0] = (Double) ((Map<String,?>) forecast.get(0).get("day")).get("mintemp_c");
		minT[1] = (Double) ((Map<String,?>) forecast.get(1).get("day")).get("mintemp_c");
		minT[2] = (Double) ((Map<String,?>) forecast.get(2).get("day")).get("mintemp_c");
		
		cond[0] = (String) ((Map<String,?>) ((Map<String,?>) forecast.get(0).get("day")).get("condition")).get("text");
		cond[1] = (String) ((Map<String,?>) ((Map<String,?>) forecast.get(1).get("day")).get("condition")).get("text");
		cond[2] = (String) ((Map<String,?>) ((Map<String,?>) forecast.get(2).get("day")).get("condition")).get("text");
		
		code[0] = (Double) ((Map<String,?>) ((Map<String,?>) forecast.get(0).get("day")).get("condition")).get("code");
		code[1] = (Double) ((Map<String,?>) ((Map<String,?>) forecast.get(1).get("day")).get("condition")).get("code");
		code[2] = (Double) ((Map<String,?>) ((Map<String,?>) forecast.get(2).get("day")).get("condition")).get("code");
		
		sRise[0] = (String) ((Map<String,?>) forecast.get(0).get("astro")).get("sunrise");
		sRise[1] = (String) ((Map<String,?>) forecast.get(1).get("astro")).get("sunrise");
		sRise[2] = (String) ((Map<String,?>) forecast.get(2).get("astro")).get("sunrise");

		sSet[0] = (String) ((Map<String,?>) forecast.get(0).get("astro")).get("sunset");
		sSet[1] = (String) ((Map<String,?>) forecast.get(1).get("astro")).get("sunset");
		sSet[2] = (String) ((Map<String,?>) forecast.get(2).get("astro")).get("sunset");
		 
		String[] ret = new String[3];
		
		for(int i=0;i<day.length;i++)
			ret[i]= "\"" + day[i]+"\"  \""+maxT[i]+"\" \""+minT[i]+"\" \""+cond[i]+"\" \""+code[i]+"\" \""+sRise[i]+"\" \""+sSet[i]+"\"\n";
		return ret[0]+ "" + ret[1] + "" + ret[2];
	}
}