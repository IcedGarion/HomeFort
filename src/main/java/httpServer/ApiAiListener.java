package httpServer;

import static spark.Spark.post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

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
	public static int sensorNodeId = 5;
	public static int plugNodeId = 4;

	//lista dei soli sensori/plug ID che hai?
	
	public static void main(String args[])
	{
		ZWave.init();
		
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
    	String text = "ERRORE ";
    	
    	if(input.getResult().getAction().equalsIgnoreCase("lightsOn"))				//cerca nel JSON di input la action richiesta
    	{
    		try 
    		{
				Hue.lightsOn();
				text = "Done! " + EmojiParser.parseToUnicode(":sunny:");
			} 
    		catch (HueException e) 
    		{
				text += e.getMessage();
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
				text += e.getMessage();
			}
    	}
    	else if(input.getResult().getAction().equalsIgnoreCase("lightsPower"))
    	{
    		int percentage;
    		String val = input.getResult().getStringParameter("any");
    		percentage = Integer.parseInt(val.substring(0, val.length()-1));
    		
    		try 
    		{
    			Hue.lightsPower(percentage);
				text = "Done! " + EmojiParser.parseToUnicode(":waning_crescent_moon:");
			} 
    		catch (HueException e) 
    		{
				text = e.getMessage();
			}
    	}
    	else if(input.getResult().getAction().equalsIgnoreCase("time"))
    	{
    		try
    		{
    			LocalDateTime t = LocalDateTime.now();
    			text = "" + t.getDayOfMonth() + "/"+ t.getMonthValue() +"/"+ t.getYear() +"\n"+ t.getHour() +":"+ t.getMinute() +":"+t.getSecond() ;
    			//text="" + t.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    		}
    		catch (Exception e) 
    		{
				text += e.getMessage();
			}
    	}
    	else if(input.getResult().getAction().equalsIgnoreCase("temperature"))
    	{
    		String temperature;
    		
    		try
    		{
    			temperature = ZWave.getTemperature(sensorNodeId);
    			
    			if(temperature.equals("ERRORE"))
    				text = "Nessun dispositivo trovato";
    			else
    				text = "La temperatura e' " + temperature;
    		}
    		catch (Exception e) 
    		{
				text += e.getMessage();
			}
    	}
    	else if(input.getResult().getAction().equalsIgnoreCase("sensorInfo"))
    	{
    		try
    		{
    			text = ZWave.renderMeasurements(sensorNodeId);
    		}
    		catch (Exception e) 
    		{
				text += e.getMessage();
			}
    	}
    	else
    	{
    		//GLOBAL CATCHER? done in api.ai?
    	}

    	output.setSpeech(text);
    	output.setDisplayText(text);
	}
}
