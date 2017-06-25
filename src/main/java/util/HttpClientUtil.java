package util;

import com.google.gson.Gson;

import hueController.HueException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtil
{

    // init gson
    private static final Gson gson = new Gson();

    /**
     * Perform a GET request towards a server
     *
     * @param URL the @link{URL} to call
     * @return the response, parsed from JSON
     * @throws HueException 
     */
	public static Map<String, ?> get(String URL) 			//http GET
    {
        Map<String, ?> response = new HashMap<>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet request = new HttpGet(URL);

        CloseableHttpResponse result = null;									//risposta che riceve dalla get

        try
        {									
            //this can cause HttpHostConnectException if no bridge is found
        	//(different from no lights found)
        	result = httpclient.execute(request);								//esegue la get su una URL passata per parametro 
            String json = EntityUtils.toString(result.getEntity());				//la get ritorna un JSON 
            // do something useful with the response body
            response = gson.fromJson(json, Map.class);							//trasforma il JSON in MAP JAVA
            // should be inside a finally...
            result.close();														//chiude connessioni
            httpclient.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Perform a PUT request towards a server
     *
     * @param URL         the @link{URL} to call
     * @param contentBody the content body of the request
     * @param contentType the content type of the request
     */
    public static void put(String URL, String contentBody, String contentType) 	//http PUT
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPut request = new HttpPut(URL);
        StringEntity params = null;

        try
        {
            params = new StringEntity(contentBody);									//inizializza il body da passare
            request.addHeader("content-type", contentType);							//set content-type
            request.setEntity(params);												//imposta il body con quello passato per parametro
            httpclient.execute(request);											//come nella GET, la put ritorna qualcosa, che so essere un JSON
            
            // should be in finally...
            httpclient.close();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
