package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Google {

    // Really should never push the real key...
    private String apiKey = "NiceTryPal";

    // Hard coded units for American people
    private String units = "imperial";

    // Constructor that does really nothing for now
    public Google() {
        System.out.println("Google object created");
    }

    // Throw it all in a try catch, because of using the url response appraoch. WIll
    //      fail on a timeout or maybe API key error
    public void distance(String to, String from) {
        try {

            String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json"
                                + "?origins=" + from
                                + "&destinations=" + to
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

            // Parse out the data from the json object
            String distance = jsonResponse.getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("distance")
                                    .getString("text");

            System.out.println("Distance between " + from + " and " + to + ": " + distance);

            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
