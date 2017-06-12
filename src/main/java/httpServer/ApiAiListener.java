package httpServer;

import static spark.Spark.post;
import weatherApi.WeatherGetter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
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
	public final static String LIGHTS_TIMES_FILE = "resources/lightsTimes";
	
	
	
	//da fare, magari, lista dei soli sensori/plug ID che hai?
	
	public static void main(String args[]) throws HueException, InterruptedException
	{
		ZWave.init();
		/*Thread comfort = new ComfortControl();
		comfort.start();*/

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
    			String city = input.getResult().getStringParameter("any");
    			int days = input.getResult().getIntParameter("number");
    			WeatherGetter.init(city,days);
    			if(days ==1 )
    			{
    				String extWeat=WeatherGetter.getExternalWeather();
    				String[] x = extWeat.split(" ");
    				text = x[2];
    				text += " " + setEmoji(x[1]);
    				text += " "+x[0]+"° C";
    			}
    			else
    			{
    				String forecast = WeatherGetter.getForecast();
    			}
    			break;
    		}
    		case "setLights":
			{
				try
				{
					String start = input.getResult().getStringParameter("start");
					String end = input.getResult().getStringParameter("end");

					writeTimes(start, end);
					text = "Regola impostata! ";
				}
				catch (Exception e)
				{
					text += e.getMessage();
				}
				break;
			}
    		default:
    		{
    			text = "Instruction not recognised";
    		}
    	}
    	
    	output.setSpeech(text);
    	output.setDisplayText(text);
	}

    private static void writeTimes(String start, String end) throws Exception
	{
		//trasforma inizio e fine in milisecondi, a partire da 00:00 e salva i due valori in un file
		long startMillisec, endMillisec;
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(LIGHTS_TIMES_FILE, true)));
		String tmpStart[] = start.split(":");
		String tmpEnd[] = end.split(":");
		long startHour = Long.parseLong(tmpStart[0]);
		long startMin = Long.parseLong(tmpStart[1]);
		long endHour = Long.parseLong(tmpEnd[0]);
		long endMin = Long.parseLong(tmpEnd[1]);

		startMillisec = (startMin * 60 * 1000) + (startHour * 60 * 60 * 1000);
		endMillisec = (endMin * 60 * 1000) + (endHour * 60 * 60 * 1000);

		writer.println(startMillisec + ", " + endMillisec);
		writer.close();
	}

	private static String setEmoji(String code) 
	{
		int cd=(int) Float.parseFloat(code);
		
		switch(cd)
		{
			case 1000:
			{
				return EmojiParser.parseToUnicode(":sunny:");
			}
			case 1003:
			case 1006:
			case 1009:
			{
				return EmojiParser.parseToUnicode(":cloud:");
			}
			case 1030:
			case 1135:
			{
				return EmojiParser.parseToUnicode(":fog:");
			}
			case 1063:
			case 1180:
			case 1183:
			case 1186:
			case 1189:
			case 1192:
			case 1195:
			case 1240:
			case 1243:
			case 1246:
			case 1072:
			case 1150:
			case 1153:
			case 1168:
			case 1171:
			{
				return EmojiParser.parseToUnicode(":cloud_rain:");
			}
			case 1066:
			case 1069:
			case 1255:
			case 1258:
			case 1261:
			case 1264:
			case 1204:
			case 1207:
			case 1249:
			case 1252:
			case 1114:
			case 1117:
			case 1210:
			case 1213:
			case 1216:
			case 1219:
			case 1222:
			case 1225:
			{
				return EmojiParser.parseToUnicode(":cloud_snow:");
			}
			case 1273:
			case 1276:
			case 1279:
			case 1282:
			{
				return EmojiParser.parseToUnicode(":thunder_cloud_rain:");
			}
		}
		return null;
	}
}