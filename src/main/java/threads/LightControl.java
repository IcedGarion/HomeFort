package threads;

import httpServer.ApiAiListener;
import hueController.Hue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;

public class LightControl extends Thread
{
    private final int FREQUENCY = 108000;
    private final String LIGHTS_TIMES_FILE = ApiAiListener.LIGHTS_TIMES_FILE;

    @Override
    public void run()
    {
        BufferedReader reader;
        long currentMillisec, startMillisec, endMillisec;
        String line;
        String tmp[];

        //utente attiva o disattiva modalità sutomatica luci!
        //ogni tanto legge da file e controlla se è in un'ora in cui può accendere
        try
        {
            while(true)	
            {	
            	//continua a ciclare finch� nn viene inserita la modalita' automatica
            	sleep(FREQUENCY);
            	if(! ApiAiListener.autoMode)
            		break;

            	currentMillisec = new Date().getTime();
            	reader = new BufferedReader(new FileReader(LIGHTS_TIMES_FILE));
            	while((line = reader.readLine()) != null)
            	{
            		//date in millisec; è nel range? se si, mustWork false e poi break
            		tmp = line.split(",");
            		startMillisec = Long.parseLong(tmp[0]);
            		endMillisec = Long.parseLong(tmp[1]);
                
            		//controlla se � nel range...
            		if((startMillisec <= currentMillisec) && (endMillisec >= currentMillisec))
            		{
            			if(! Hue.isOn)
            				Hue.lightsOn();
            				
            		}
            	}
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
