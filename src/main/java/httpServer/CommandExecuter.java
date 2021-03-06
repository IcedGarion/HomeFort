package httpServer;

import java.time.LocalDateTime;
import java.util.logging.Logger;
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
	private static Logger logger = Logger.getLogger(CommandExecuter.class.getName());
	
    public static void doWebHook(AIResponse input, Fulfillment output)
    {
    	String text = "ERROR ";
    	String action = input.getResult().getAction();
    	
    	logger.info("User says : " + action);
    	
    	switch(action)
    	{
    		case "lightsOn":
    		{
    			String append = "";
				
    			try 
        		{
    				Hue.lightsOn();
    				
    				if(LightControl.autoMode)
    					append = "\nAutomode Off!";
    				LightControl.autoMode = false;
    				
    				text = "Lights are On! " + EmojiParser.parseToUnicode(":sunny:") + append;
    				
    			} 
        		catch (HueException e) 
        		{
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
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
    					append = "\nAutomode Off!";
    				LightControl.autoMode = false;
    				
    				text = "Lights are Off! " + EmojiParser.parseToUnicode(":new_moon:") + append;
    			} 
        		catch (HueException e) 
        		{
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
    				text += e.getMessage();
    			}
    			
    			break;
    		}
    		case "lightsPower":
    		{
    			String append = "";
    			int percentage;
        		String val = input.getResult().getStringParameter("number");
        		percentage = Integer.parseInt(val);
        	
        		
        		if(percentage < 0 || percentage > 100)
        		{
        			text = "You must insert a percentage (0 - 100)";
        			break;
        		}

        		try 
        		{
        			if(LightControl.autoMode)
        				append = "\nAutomode Off!";
        			Hue.lightsPower(percentage);
    				text = "Lights set at " + percentage +"%"+ append;
    			} 
        		catch (HueException e) 
        		{
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
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
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
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
        				text = "Temperature: " + temperature;
        		}
        		catch (Exception e) 
        		{
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
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
        				text = "No device found";
        			else
        				text = "Luminosity is " + luminosity;
        		}
        		catch (Exception e) 
        		{
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
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
        				text = "No device found";
        			else
        				text = "Humidity is " + humidity;
        		}
        		catch (Exception e) 
        		{
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
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
        				text = "No device found";
        			else if(motion.toLowerCase().startsWith("off"))
        				text = "There's no one in the room ";
        			else
        				text = "There's someone in the room ";
        		}
        		catch (Exception e) 
        		{
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
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
        			logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
    				text += e.getMessage();
    			}
    			break;
    		}
    		case "getWeather":
    		{
    			try
    			{
    				String city = input.getResult().getStringParameter("any");
    				int days = input.getResult().getIntParameter("number");
    				WeatherGetter.init(city,days);
    			
    					if(days == 1 || days == 0)
    					{
    						String extWeat=WeatherGetter.getExternalWeather();
    						String[] x = extWeat.split(" ");
    						text = x[2];
    						text += " " + ApiUtil.convertEmoji(x[1]);
    						text += " "+x[0]+"� C";
    					}
    					else if(days ==3)
    					{
    						text="";
    						String forecast = WeatherGetter.getForecast();
    						String[] fore =forecast.split("\n");
    						String[] x= new String[14];
    						String[] y= new String[14];
    						String[] z= new String[14];
    						x=fore[0].split("\"");
    						y=fore[1].split("\"");
    						z=fore[2].split("\"");
    						
    						text += ApiUtil.buildWeatherText(x)+"\n\n";
    						text += ApiUtil.buildWeatherText(y)+"\n\n";
    						text += ApiUtil.buildWeatherText(z);
    						
    					}
    					else
    						text += " Forecasts supported : 1 OR 3 days";
    			}
    			catch(Exception e)
    			{
    				logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
    				text += "Command not recognized";
    			}
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
					
					text = "Rule set: " + start  + " - " +  end + "\nRemember the automode!";
				}
				catch (Exception e)
				{
					logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
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
    				text = "Rules deleted! ";
    			}
    			catch (Exception e)
				{
    				logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
					text += e.getMessage();
				}
				break;
    		}
    		case "autoModeOn":
    		{
    			LightControl.autoMode = true;
    			text = "Automode on! ";
    			
    			break;
    		}
    		case "autoModeOff":
    		{
    			LightControl.autoMode = false;
    			text = "Automode Off! ";
    			
    			break;
    		}
    		case "setComfort":
    		{
    			try
    			{
    				int newTemp = Integer.parseInt(input.getResult().getStringParameter("temp"));
    				ApiAiListener.COMFORT_TEMPERATURE = newTemp;
    				text = "Comfort temperature set!";
    			}
    			catch(Exception e)
    			{
    				logger.severe("Exception : " + e + "\n" + e.getMessage());
        			e.printStackTrace();
    				text += "Command not recognised ";
    			}
    			
    			break;
    		}
    		default:
    		{
    			text = "Instruction not recognised";
    		}
    	}
    	
    	logger.info("Server responds : " + text);
    	output.setSpeech(text);
    	output.setDisplayText(text);
	}
}