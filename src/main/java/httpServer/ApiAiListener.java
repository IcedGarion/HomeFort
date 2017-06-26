package httpServer;

import static spark.Spark.post;

import java.util.logging.Logger;

import com.google.gson.Gson;
import ai.api.GsonFactory;
import ai.api.model.AIResponse;
import ai.api.model.Fulfillment;
import controlThreads.ComfortControl;
import controlThreads.LightControl;
import hueController.Hue;
import zWaveController.ZWave;


public class ApiAiListener
{	
	public static int COMFORT_TEMPERATURE = 23;
	private static Logger logger = Logger.getLogger(ApiAiListener.class.getName());
	
	public static void main(String args[]) throws Exception
	{
		try
		{
			//inizijalizza convertitore json
			Gson aiGson = GsonFactory.getDefaultFactory().getGson();
    	
			//inizializza parametri zwave
			ZWave.init();
		
			//inizializza tutto a OFF per un punto di partenza pulito
			Hue.lightsOff();
			ZWave.plugOff();
			Thread.sleep(500);
	
			//fa partire thread controllo luminosità
			Thread comfort = new ComfortControl();
			comfort.start();
		
			//fa partire thread controllo accensione luci a ore
			Thread lights = new LightControl();
			lights.start();

			logger.info("Server started... ");
		
			//poi aspetta comandi
			post("/HomeFort", (req, res) ->
			{	
				Fulfillment output = new Fulfillment();
				CommandExecuter.doWebHook(aiGson.fromJson(req.body(), AIResponse.class), output);
				return output;
			}, aiGson::toJson);
			
			logger.info("Quitting server... ");
		}
		catch(Exception e)
		{
			logger.severe("Exception : " + e + "\n" + e.getMessage());
			e.printStackTrace();
		}
    }
}
