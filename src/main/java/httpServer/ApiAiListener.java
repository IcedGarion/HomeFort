package httpServer;

import static spark.Spark.post;
import weatherApi.WeatherGetter;
import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;
import ai.api.GsonFactory;
import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import hueController.Hue;
import hueController.HueException;
import threads.ComfortControl;
import zWaveController.ZWave;


public class ApiAiListener
{
	public static int sensorNodeId = 5;
	public static int plugNodeId = 3;	//3 è safety home
	
	
	
	
	//da fare, magari, lista dei soli sensori/plug ID che hai?
	
	public static void main(String args[]) throws HueException, InterruptedException
	{
		ZWave.init();
		Thread comfort = new ComfortControl();
		comfort.start();

    	Gson aiGson = GsonFactory.getDefaultFactory().getGson();
																
    	post("/HomeFort", (req, res) ->
    	{
    		Fulfillment output = new Fulfillment();
    		doWebHook(aiGson.fromJson(req.body(), AIResponse.class), output);
    		return output;
    	}, aiGson::toJson);
    }

	
	
    private static void doWebHook(AIResponse input, Fulfillment output)
    {
    	String text = "ERRORE ";
    	String action = input.getResult().getAction();
    	
    	
    	switch(action)
    	{
    		case "lightsOn":
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
    			break;
    		}
    		case "lightsOff":
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
    			break;
    		}
    		case "lightsPower":
    		{
    			int percentage;
        		String val = input.getResult().getStringParameter("any");
        		if(val.contains("%"))
        			percentage = Integer.parseInt(val.substring(0, val.length()-1));
        		else
        			percentage = Integer.parseInt(val);
        		
        		try 
        		{
        			Hue.lightsPower(percentage);
    				text = "Done! " + EmojiParser.parseToUnicode(":waning_crescent_moon:");
    			} 
        		catch (HueException e) 
        		{
    				text = e.getMessage();
    			}
        		break;
    		}
    		case "time":
    		{
    			try
        		{
        			LocalDateTime t = LocalDateTime.now();
        			text = "" + t.getDayOfMonth() + "/"+ t.getMonthValue() +"/"+ t.getYear() +"\n"+ t.getHour() +":"+ t.getMinute() +":"+t.getSecond() ;
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
    			break;
    		}
    		case "temperature":
    		{
    			String temperature;
        		
        		try
        		{
        			temperature = ZWave.getTemperature(sensorNodeId);
        			
        			if(temperature.equals("ERRORE"))
        				text = "No device found";
        			else
        				text = "La temperatura e' " + temperature;
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
        		break;
    		}
    		case "luminosity":
    		{
    			String luminosity;
        		
        		try
        		{
        			luminosity = ZWave.getLuminosity(sensorNodeId);
        			
        			if(luminosity.equals("ERRORE"))
        				text = "Nessun dispositivo trovato";
        			else
        				text = "La luminosita' e' " + luminosity;
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
        		break;
    		}
    		case "humidity":
    		{
    			String humidity;
        		
        		try
        		{
        			humidity = ZWave.getHumidity(sensorNodeId);
        			
        			if(humidity.equals("ERRORE"))
        				text = "Nessun dispositivo trovato";
        			else
        				text = "L'umidita' e' " + humidity;
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
        		break;
    		}
    		case "motion":
    		{
    			String motion;
        		
        		try
        		{
        			motion = ZWave.getMotion(sensorNodeId);
        			
        			if(motion.equals("ERRORE"))
        				text = "Nessun dispositivo trovato";
        			else if(motion.toLowerCase().startsWith("on"))
        				text = "Nessun movimento ";
        			else
        				text = "C'e' movimento nella stanza ";
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
    		}
    		case "sensorInfo":
    		{
    			try
        		{
        			text = ZWave.renderSensorMeasurements(sensorNodeId);
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
    			break;
    		}
    		case "plugInfo":
    		{
    			try
        		{
        			text = ZWave.renderPlugMeasurements(plugNodeId);
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
    			break;
    		}
    		case "plugOn":
    		{
    			try
        		{
        			ZWave.plugOn(plugNodeId);
        			text = "Presa Accesa! ";
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
    			break;
    		}
    		case "plugOff":
    		{
    			try
        		{
        			ZWave.plugOff(plugNodeId);
        			text = "Presa Spenta! ";
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
    			break;
    		}
    		case "getWeather":
    		{
    			WeatherGetter.init();
    			String extTemp=WeatherGetter.getExternalTemp();
    			//e tutto il resto per sapere il tempo 
    		}
    		default:
    		{
    			text = "Instruction not recognised";
    		}
    	}
    	
    	output.setSpeech(text);
    	output.setDisplayText(text);
	}
}