package httpServer;

import static spark.Spark.post;
import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;

import ai.api.GsonFactory;
import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import hueController.Hue;
import hueController.HueException;

public class ApiAiListener
{
	public static void main(String args[])
	{
		//CI SONO PIÙ FUNZIONI: LIGHTS, FORECAST, TEMPERATURE....
		//quindi: /HomeFort/lights; /HomeFort/forecast....
		//un metodo "doWebHook" per ognuno di questi
		
		
		//API.AI WEBHOOK
    	Gson aiGson = GsonFactory.getDefaultFactory().getGson();			//GsonFactory � nelle classi di api.ai sdk e ha metodi per convertire JSON richiesti da api.ai
    																	//in stringhe JAVA
    	post("/HomeFort", (req, res) ->
    	{
    		Fulfillment output = new Fulfillment();
    		
    		doWebHook(aiGson.fromJson(req.body(), AIResponse.class), output);
    		
    		return output;
    	}, aiGson::toJson);
    	
    	//se voglio che risponde a qualcos'altro, post("/temperature/..."
    }

    private static void doWebHook(AIResponse input, Fulfillment output)
    {
    	String text = "ERROR";
    	
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
    	else if(input.getResult().getAction().equalsIgnoreCase("other"))
    	{
    		//GLOBAL CATCHER? shows an error page instead of "404 not found"...
    	}
    	
    	output.setSpeech(text);
    	output.setDisplayText(text);
	}
}
