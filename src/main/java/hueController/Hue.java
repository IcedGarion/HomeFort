package hueController;

import java.util.Map;

/**
 * Sample usage of the Philips Hue API.
 *
 * @author <a href="mailto:luigi.derussis@uniupo.it">Luigi De Russis</a>
 * @version 1.0 (18/05/2017)
 */
public class Hue
{
	// the URL of the Philips Hue bridge
	
	 // LAB CONFIGURATIONS
	/*
	  private static String baseURL = "http://172.30.1.138";
	  private static String username = "lqo778nsVu54Kb1mSLa6pyIGPysfxYzQdt5litQR";
	  */
	 
	
	/*
	 * EMULATOR CONFIGURATIONS
	 */
	 private static String baseURL = "http://127.0.0.1:8000";
	  private static String username = "newdeveloper";
	 

	// base URL for lights
    private static String lightsURL = baseURL + "/api/" + username + "/lights/";
    
    private static Map<String, ?> allLights;
    

    private static void init()
    {
    	// get the Hue lamps
    	allLights = HttpClientUtil.get(lightsURL);
    }
    
	//GLI SI DOVR� PASSARE COME PARAMETRO IL USERNAME TOKEN?
    public static void lightsOn() throws HueException 
    {
    	init();

        // prende la prima lampadina e accende
		if (allLights.containsKey("1")) 
		{
			String callURL = lightsURL + "1" + "/state";
			String body = "{ \"on\" : true }";
			HttpClientUtil.put(callURL, body, "application/json");
		}
		else
			throw new HueException("No lights found!");
    }
    public static void lightsOff() throws HueException 
    {
    	init();
    	
        //prende la prima lampadina e accende
		if (allLights.containsKey("1")) 
		{
			String callURL = lightsURL + "1" + "/state";
			String body = "{ \"on\" : false}";
			HttpClientUtil.put(callURL, body, "application/json");
		}
		else
			throw new HueException("No lights found!");
    }
    
    public static void lightsPower(int value) throws HueException 
    {
    	init();
    	
    	lightsOn();

        // prende la prima lampadina e accende
		if (allLights.containsKey("1")) 
		{
			String callURL = lightsURL + "1" + "/state";
			String body = "{ \"bri\" : "+value+" }";
			HttpClientUtil.put(callURL, body, "application/json");
		}
		else
			throw new HueException("No lights found!");
    }
}