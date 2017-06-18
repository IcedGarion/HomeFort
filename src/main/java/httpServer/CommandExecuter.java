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
    	String text = "ERROR ";
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
    					append = "\nAutomode Off!";
    				LightControl.autoMode = false;
    				
    				text = "Lights are On! " + EmojiParser.parseToUnicode(":sunny:") + append;
    				
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
    					append = "\nAutomode Off!";
    				LightControl.autoMode = false;
    				
    				text = "Lights are Off! " + EmojiParser.parseToUnicode(":new_moon:") + append;
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
        				text = "Temperature: " + temperature+"° C";
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
        				text = "No device found";
        			else
        				text = "Luminosity is " + luminosity+" Lux";
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
        				text = "No device found";
        			else
        				text = "Humidity is " + humidity+"%";
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
        				text = "No device found";
        			else if(motion.toLowerCase().startsWith("off"))
        				text = "There's no one in the room ";
        			else
        				text = "There's someone in the room ";
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
        			text = "Plug is on! ";
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
        			text = "Plug is off! ";
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
    					for(int i=0;i<x.length;i++)
    						System.out.println(i + " "+x[i]);
    					y=fore[1].split("\"");
    					for(int i=0;i<y.length;i++)
    						System.out.println(i + " "+y[i]);
    					z=fore[2].split("\"");
    					for(int i=0;i<z.length;i++)
    						System.out.println(i +" "+ z[i]);
    					
    					text += ApiUtil.buildWeatherText(x)+"\n\n";
    					text += ApiUtil.buildWeatherText(y)+"\n\n";
    					text += ApiUtil.buildWeatherText(z);
    					
    				}
    				else
    					text += " Maximum forecast supported: 3 days";
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
					
					text = "Rule set: " + start  + " - " +  end;
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
    				text = "Rules deleted! ";
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
    			text = "Automode on! ";
    			
    			break;
    		}
    		case "autoModeOff":
    		{
    			LightControl.autoMode = false;
    			text = "Automode Off! ";
    			
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