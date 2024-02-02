package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

public class Google {

    // Really should never push the real key...
    private String apiKey = "";

    // Hard coded units for American people
    private String units = "imperial";

    // Constructor that does really nothing for now
    public Google() {
        System.out.println("Google object created");
    }

    // Throw it all in a try catch, because of using the url response appraoch. WIll
    //      fail on a timeout or maybe API key error
    public int getDistance(String to, String from) {
        String output = "";
        Double distance = 0.0;
        try {

            String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json"
                                + "?origins=" + encodeURL(from)
                                + "&destinations=" + encodeURL(to)
                                + "&units=" + units
                                + "&key=" + apiKey;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());

            // Check if the response is valid, checking if there is an error message
            if (jsonResponse.has("error_message")) {
                System.out.println("Error: " + jsonResponse.getString("error_message"));
                conn.disconnect();
                return -1;
            }

            // Parse out the data from the json object
            output = jsonResponse.getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("distance")
                                    .getString("text");

            // System.out.println("Distance between " + from + " and " + to + ": " + distance);

            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Crazy. This will break up the distance string into parts. As of now, distance is usually something like "X,XXX miles", 
        //      so the normal parser does not work on it because of the miles tag. Below will break into into parts, with the delimter
        //      being spaces "\\s+". So, we should always have 2 parts, with the first (zeroth positon) being the actual number. We can then
        //      just parse that part like usual. HOWEVER, the comma in "X,XXX" is now an issue, so we can simply use the replace method, and
        //      replace it with nothing to remove them. 
        String[] distanceParts = output.split("\\s+");
        
        // This will overwrite the existing distance String
        try {
            output = distanceParts[0].replace(",", "");

        } catch (Exception e) {
            System.out.println("No , in the string, lame way to do this but whatever");
        }
        distance = Double.parseDouble(output);
        System.out.println(distance);
        return distance.intValue();
    }

    private String encodeURL(String city) {
        String output = "";
        try {
            output = URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println(output);
        return output;
    }

    // setter for the API key
    public void setAPIKey(String key) {
        apiKey = key;
        ;
    }
    
}
