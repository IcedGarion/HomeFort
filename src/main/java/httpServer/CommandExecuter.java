package httpServer;

import java.time.LocalDateTime;

import com.vdurmont.emoji.EmojiParser;

import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import controlThreads.LightControl;
import hueController.Hue;
import hueController.HueException;
import util.ApiUtil;
import util.Writer;
import weatherApi.WeatherGetter;
import zWaveController.ZWave;

public class CommandExecuter
{
	private static Writer writer;
	
    public static void doWebHook(AIResponse input, Fulfillment output)
    {
    	String text = "ERRORE ";
    	String action = input.getResult().getAction();
    	
    	
    	switch(action)
    	{
    		case "lightsOn":
    		{
    			String append = "";
				
    			try 
        		{
    				Hue.lightsOn();
    				
    				if(LightControl.autoMode)
    					append = "\nModalita' automatica disattivata!";
    				LightControl.autoMode = false;
    				
    				text = "Done! " + EmojiParser.parseToUnicode(":sunny:") + append;
    				
    			} 
        		catch (HueException e) 
        		{
    				text += e.getMessage();
    			}
    			break;
    		}
    		case "lightsOff":
    		{
    			String append = "";
    			
    			try 
        		{
    				Hue.lightsOff();
    				
    				if(LightControl.autoMode)
    					append = "\nModalita' automatica disattivata!";
    				LightControl.autoMode = false;
    				
    				text = "Done! " + EmojiParser.parseToUnicode(":waning_crescent_moon:") + append;
    			} 
        		catch (HueException e) 
        		{
    				text += e.getMessage();
    			}
    			
    			break;
    		}
    		case "lightsPower":
    		{
    			String append = "";
    			int percentage;
        		String val = input.getResult().getStringParameter("any");
        		if(val.contains("%"))
        			percentage = Integer.parseInt(val.substring(0, val.length()-1));
        		else
        			percentage = Integer.parseInt(val);
        		
        		try 
        		{
        			if(LightControl.autoMode)
        				append = "\nModalita' automatica disattivata!";
        			Hue.lightsPower(percentage);
    				text = "Done! " + EmojiParser.parseToUnicode(":waning_crescent_moon:") + append;
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
        			temperature = ZWave.getTemperature();
        			
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
        			luminosity = ZWave.getLuminosity();
        			
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
        			humidity = ZWave.getHumidity();
        			
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
        			motion = ZWave.getMotion();
        			
        			if(motion.equals("ERRORE"))
        				text = "Nessun dispositivo trovato";
        			else if(motion.toLowerCase().startsWith("off"))
        				text = "Nessun movimento ";
        			else
        				text = "C'e' movimento nella stanza ";
        		}
        		catch (Exception e) 
        		{
    				text += e.getMessage();
    			}
        		
        		break;
    		}
    		case "sensorInfo":
    		{
    			try
        		{
        			text = ZWave.renderSensorMeasurements();
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
        			text = ZWave.renderPlugMeasurements();
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
        			ZWave.plugOn();
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
        			ZWave.plugOff();
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
    			if(days ==1)
    			{
    				String extWeat=WeatherGetter.getExternalWeather();
    				String[] x = extWeat.split(" ");
    				text = x[2];
    				text += " " + ApiUtil.convertEmoji(x[1]);
    				text += " "+x[0]+"� C";
    			}
    			else if(days == 3)
    			{
    				String forecast = WeatherGetter.getForecast();
    			}
    			else
    				text += "Previsione massima 3 giorni";
    			break;
    		}
    		case "setLights":
			{
				try
				{
					String start = input.getResult().getStringParameter("start");
					String end = input.getResult().getStringParameter("end");

					//fa partire il thread che scrive le ore di luce
					writer = new Writer();
					writer.reset(false);
					writer.setData(start, end);
					writer.start();
					
					text = "Regola impostata: " + start  + " - " +  end;
				}
				catch (Exception e)
				{
					text += e.getMessage();
				}
				break;
			}
    		case "setLightsReset":
    		{
    			try
    			{
    				//fa partire il thread che cancella il file
    				writer = new Writer();
    				writer.reset(true);
    				writer.start();
    				text = "Regole cancellate! ";
    			}
    			catch (Exception e)
				{
					text += e.getMessage();
				}
				break;
    		}
    		case "autoModeOn":
    		{
    			LightControl.autoMode = true;
    			text = "Modalita' automatica attivata! ";
    			
    			break;
    		}
    		case "autoModeOff":
    		{
    			LightControl.autoMode = false;
    			text = "Modalita' automatica disattivata! ";
    			
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
}
