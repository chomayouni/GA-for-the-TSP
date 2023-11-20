package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class Google {
    private String to = "";
    private String from = "";
    // Really should never push the real key...
    private String apiKey = "NiceTryPal";
    private String units = "imperial";

    public Google() {
        System.out.println("Google object created");
    }

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
