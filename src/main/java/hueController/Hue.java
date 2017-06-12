package hueController;

import java.util.Map;

public class Hue
{
	 // LAB CONFIGURATIONS
	
	  private static String baseURL = "http://172.30.1.138";
	  private static String username = "lqo778nsVu54Kb1mSLa6pyIGPysfxYzQdt5litQR";
	  public static boolean isOn = false;
	  

	 //EMULATOR CONFIGURATIONS

	 /*private static String baseURL = "http://127.0.0.1:8000";
	  private static String username = "newdeveloper";*/


	// base URL for lights
    private static String lightsURL = baseURL + "/api/" + username + "/lights/";
    
    private static Map<String, ?> allLights;
    

    private static void init()
    {
    	// get the Hue lamps
    	allLights = HttpClientUtil.get(lightsURL);
    }
    
    public static void lightsOn() throws HueException 
    {
    	init();

       	if (allLights.containsKey("1")) 
		{
			String callURL = lightsURL + "1" + "/state";
			String body = "{ \"on\" : true, \"sat\":0, \"bri\" : 255 }";
			HttpClientUtil.put(callURL, body, "application/json");
			isOn = true;
		}
		else
			throw new HueException("No lights found!");
    }
    
    public static void lightsOff() throws HueException 
    {
    	init();
    	
		if (allLights.containsKey("1")) 
		{
			String callURL = lightsURL + "1" + "/state";
			String body = "{ \"on\" : false}";
			HttpClientUtil.put(callURL, body, "application/json");
			isOn = false;
		}
		else
			throw new HueException("No lights found!");
    }
    
    public static void lightsPower(int value) throws HueException 
    {
    	init();
    	
    	lightsOn();

        if (allLights.containsKey("1")) 
		{
			String callURL = lightsURL + "1" + "/state";
			String body = "{ \"bri\" : "+value+" }";
			HttpClientUtil.put(callURL, body, "application/json");
		}
		else
			throw new HueException("No lights found!");
    }

	public static void cold() throws HueException 
	{
		init();

       	if (allLights.containsKey("1")) 
		{
			String callURL = lightsURL + "1" + "/state";
			String body = "{ \"on\" : true, \"hue\" : 46920 }";
			HttpClientUtil.put(callURL, body, "application/json");
			isOn = true;
		}
		else
			throw new HueException("No lights found!");
	}

	public static void hot() throws HueException 
	{
		init();

       	if (allLights.containsKey("1")) 
		{
			String callURL = lightsURL + "1" + "/state";
			String body = "{ \"on\" : true, \"hue\" : 65535 }";
			HttpClientUtil.put(callURL, body, "application/json");
			isOn = true;
		}
		else
			throw new HueException("No lights found!");
		
	}
}