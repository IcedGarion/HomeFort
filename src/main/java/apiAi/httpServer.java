package apiAi;

import static spark.Spark.post;
import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;

import ai.api.GsonFactory;
import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import hue.Hue;
import hue.HueException;

public class httpServer
{
	public static void main(String args[])
	{
		//API.AI WEBHOOK
    	Gson aiGson = GsonFactory.getDefaultFactory().getGson();			//GsonFactory è nelle classi di api.ai sdk e ha metodi per convertire JSON richiesti da api.ai
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
    	
    	output.setSpeech(text);
    	output.setDisplayText(text);
	}
}
