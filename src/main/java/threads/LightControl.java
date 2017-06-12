package threads;

import httpServer.ApiAiListener;
import hueController.Hue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class LightControl extends Thread
{
    private final int FREQUENCY = 3000/*108000*/;
    private final String LIGHTS_TIMES_FILE = ApiAiListener.LIGHTS_TIMES_FILE;

    @Override
    public void run()
    {
        BufferedReader reader;
        long currentMillisec, startMillisec, endMillisec;
        String line;
        String tmp[];

        //utente attiva o disattiva modalità  automatica luci!
        //ogni tanto legge da file e controlla se e' in un'ora in cui puo' accendere
        try
        {
            while(true)	
            {	
            	//continua a ciclare finchè nn viene inserita la modalita' automatica
            	sleep(FREQUENCY);
            	if(! ApiAiListener.autoMode)
            		continue;

            	//prende l'ora attuale (in millisecondi) e controlla riga per riga nel file di configurazione, se c'e' un orario di accensione
            	currentMillisec = getCurrentMs();
            	reader = new BufferedReader(new FileReader(LIGHTS_TIMES_FILE));
            	while((line = reader.readLine()) != null)
            	{
            		tmp = line.split(", ");
            		startMillisec = Long.parseLong(tmp[0]);
            		endMillisec = Long.parseLong(tmp[1]);
                
            		//controlla se è nel range...
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
    
    private long getCurrentMs()
    {
    	ZonedDateTime now = ZonedDateTime.now();
    	ZonedDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
    	Duration duration = Duration.between(midnight, now);

    	return duration.getSeconds();
    }
}
