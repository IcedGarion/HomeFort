package httpServer;

import static spark.Spark.post;

import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;

import ai.api.GsonFactory;
import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import hueController.Hue;
import hueController.HueException;
import zWaveController.ZWave;

public class ApiAiListener
{
	private static int sensorNodeId = 5;
	private static int plugNodeId = 4;

	//lista dei soli sensori/plug ID che hai?
	
	public static void main(String args[])
	{
		//prova zwave
			ZWave.init();
		//	ZWave.getAllPlugs();
		//	ZWave.getAllSensors();
			
			
		//API.AI WEBHOOK
    	Gson aiGson = GsonFactory.getDefaultFactory().getGson();			//GsonFactory � nelle classi di api.ai sdk e ha metodi per convertire JSON richiesti da api.ai
																			//in stringhe JAVA
    	post("/HomeFort", (req, res) ->
    	{
    		Fulfillment output = new Fulfillment();
    		
    		//cambiare nome metodo
    		doWebHook(aiGson.fromJson(req.body(), AIResponse.class), output);
    		
    		return output;
    	}, aiGson::toJson);
    }

    private static void doWebHook(AIResponse input, Fulfillment output)
    {
    	String text = "ERRORE";
    	
    	if(input.getResult().getAction().equalsIgnoreCase("lightsOn"))				//cerca nel JSON di input la action richiesta
    	{
    		try 
    		{
				Hue.lightsOn();
				text = "Done! " + EmojiParser.parseToUnicode(":sunny:");
			} 
    		catch (HueException e) 
    		{
				text = e.getMessage();
			}
    	}
    	else if(input.getResult().getAction().equalsIgnoreCase("lightsOff"))
    	{
    		try 
    		{
				Hue.lightsOff();
				text = "Done! " + EmojiParser.parseToUnicode(":waning_crescent_moon:");
			} 
    		catch (HueException e) 
    		{
				text = e.getMessage();
			}
    	}
    	else if(input.getResult().getAction().equalsIgnoreCase("lightsPower"))
    	{
    		int percentage, value;
    		String val = input.getResult().getStringParameter("any");
    		
    		percentage = Integer.parseInt(val.substring(0, val.length()-1));
    		try 
    		{
    			if(percentage == 0)
    			{
    				Hue.lightsOff();
    				text = "Done! " + EmojiParser.parseToUnicode(":waning_crescent_moon:");
    			}
    			else
    			{
    				value = (254*percentage)/100;
    				Hue.lightsPower(value);
					text = "Done! " ;
    			}
			} 
    		catch (HueException e) 
    		{
				text = e.getMessage();
			}
    	}
    	else if(input.getResult().getAction().equalsIgnoreCase("temperature"))
    	{
    		String temperature = ZWave.getTemperature(sensorNodeId);

    		if(temperature.equals("ERRORE"))
    			text = "errore";
    		else
    			text = "La temperatura e' " + temperature;
    	}
    	else if(input.getResult().getAction().equalsIgnoreCase("temperature"))
    	{
    		
    	}
    	else
    	{
    		//GLOBAL CATCHER? done in api.ai?
    	}
    	
    	output.setSpeech(text);
    	output.setDisplayText(text);
	}
}
