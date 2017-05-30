package zWaveController;

import de.fh_zwickau.informatik.sensor.IZWayApi;
import de.fh_zwickau.informatik.sensor.ZWayApiHttp;
import de.fh_zwickau.informatik.sensor.model.devices.Device;
import de.fh_zwickau.informatik.sensor.model.devices.DeviceList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample usage of the Z-Way API. It looks for all sensors and power outlets.
 * It reports the temperature and power consumption of available sensors and, then, turn power outlets on for 10 seconds.
 * <p>
 * It uses the Z-Way library for Java included in the lib folder of the project.
 *
 * @author <a href="mailto:luigi.derussis@uniupo.it">Luigi De Russis</a>
 * @version 1.0 (24/05/2017)
 * @see <a href="https://github.com/pathec/ZWay-library-for-Java">Z-Way Library on GitHub</a> for documentation about the used library
 */
public class ZWave
{
	private static Logger logger;
	private static IZWayApi zwayApi;

    public static void init()
    {
        // init logger
        logger = LoggerFactory.getLogger(ZWave.class);

        // example RaZberry IP address
        String ipAddress = "172.30.1.137";

        // example username and password
        String username = "admin";
        String password = "raz4reti2";

        // create an instance of the Z-Way library; all the params are mandatory (we are not going to use the remote service/id)
        zwayApi = new ZWayApiHttp(ipAddress, 8083, "http", username, password, 0, false, new ZWaySimpleCallback());
    }
    
    private static List<Device> getSensor(int sensorNodeId)
    {
    	//get all the Z-Wave devices
        DeviceList allDevices = zwayApi.getDevices();
        
        Map<Integer, List<Device>> sensor = allDevices.getDevicesByNodeId(sensorNodeId);	//prende il device numero x e ritorna una mappa in cui c'è solo l'elemento numero x
        List<Device> tmp = sensor.get(sensorNodeId);										//prende dalla mappa questo elemento numero x (una lista di comandi che puoi dare su quel device)
        																					//poi cicla sulla lista per trovare il probe "temperature" o altro
        return tmp;
    }
    
    public static Map<String, String> getMeasurements(int sensorNodeId)
    {
    	List<Device> sensorProbes = getSensor(sensorNodeId);
    	Map<String, String> measures = new HashMap<String, String>();
    	
    	for(Device probe : sensorProbes)
    	{
    		if(probe.getDeviceType().equalsIgnoreCase("SensorMultilevel"))
    		{
    			if(probe.getProbeType().equalsIgnoreCase("temperature"))
    				measures.put("temperature", probe.getMetrics().getLevel() + " " + probe.getMetrics().getScaleTitle());
    			else if(probe.getProbeType().equalsIgnoreCase("luminosity"))
    				measures.put("luminosity", probe.getMetrics().getLevel() + " " + probe.getMetrics().getScaleTitle());
    			else if(probe.getProbeType().equalsIgnoreCase("humidity"))
    				measures.put("humidity", probe.getMetrics().getLevel() + " " + probe.getMetrics().getScaleTitle());
    		}
    	}
    	
    	return measures;
    	
    	//simile a temperature, ma prende tutti i probes (vedi sotto, getallplugs)
    }
    
    
    //alla fine questa chiamerà measurements e si prende solo la temperatura dalla Map
    public static String getTemperature(int sensorNodeId)
    {
    	Map<String, String> measures = getMeasurements(sensorNodeId);
    	
    	return measures.get("temperature");
    }
    
    public static void getAllPlugs()
    {
    	//get all the Z-Wave devices
        DeviceList allDevices = zwayApi.getDevices();
        
        // search all sensors
        for (Device dev : allDevices.getAllDevices())
        {
            if (dev.getDeviceType().equalsIgnoreCase("SensorBinary"))
            {
            	 logger.info("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
            	
            	 // get only temperature and power consumption from available sensors
                 if (dev.getProbeType().equalsIgnoreCase("temperature")) {
                     logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " " + dev.getMetrics().getScaleTitle());
                 } else if (dev.getProbeType().equalsIgnoreCase("meterElectric_watt")) {
                     logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " " + dev.getMetrics().getScaleTitle());
                 } else {
                     // get all measurements from sensors
                     logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " uom: " + dev.getMetrics().getScaleTitle());
                 }
            }
        }
    }

    public static void turnAllOff()
    {
    	DeviceList allDevices = zwayApi.getDevices();
    	
        // search again all power outlets
        for (Device dev : allDevices.getAllDevices()) {
            if (dev.getDeviceType().equalsIgnoreCase("SwitchBinary")) {
                logger.debug("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
                // turn it off
                logger.info("Turn device " + dev.getNodeId() + " off...");
                dev.off();
            }
        }

    }

	public static String renderMeasurements(int sensorNodeId)
	{
		String text = "";
		Map<String, String> measurements = getMeasurements(sensorNodeId);
		
		text += "temperatura : " + measurements.get("temperature");
		text += "luminosita' : " + measurements.get("luminosity");
		text += "umidita' : " + measurements.get("humidity");
		
		return text;
	}
}