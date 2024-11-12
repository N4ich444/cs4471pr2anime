package org.cs4471.dogsite.controller;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

//pojo for the returned JSON
public class AncillaryStructs {
    //ann variables
    String name = "";
    String annurl = "";
    //mal variables
    String url = "";
    String imgurl = "";
    //<0 or lower represents n/a
    BigDecimal score = BigDecimal.valueOf(-1);
    Integer rank = -1;

    public AncillaryStructs(String name, String url) {
        this.annurl = url;
        this.name = name;
        this.imgurl = "";  // Default to empty if no image is found
        this.score = BigDecimal.valueOf(-1);   // Default to -1 to signify "N/A"
        this.rank = -1;    // Default to -1 to signify "N/A"
    }

    public void addMalRating(JSONObject data, String name) {
        try {
            if (!data.isEmpty()) {
                this.url = data.getString("url");
                if (data.has("rank") && !data.isNull("rank")) this.rank = data.getInt("rank");
                if (data.has("score") && !data.isNull("score")) this.score = data.getBigDecimal("score");
                
                
                
                //getting images
                JSONObject images = data.optJSONObject("images");
                if (images != null) {
                    // Try to get jpg image
                    JSONObject jpg = images.optJSONObject("jpg");
                    if (jpg != null && jpg.has("image_url")) {
                        this.imgurl = jpg.getString("image_url");
                    } else {
                        // Try to get webp image if jpg is not available
                        JSONObject webp = images.optJSONObject("webp");
                        if (webp != null && webp.has("image_url")) {
                            this.imgurl = webp.getString("image_url");
                        }
                    }
                }

                
                if (this.imgurl == null) this.imgurl = "";
                
            }
        }
        catch (JSONException je) {
            System.err.println(String.format("MALRating error: %s", je));

        }
        
        
    }

    

    //getters
    public String getTitle() {
        return this.name;
    }

    public String getAnnUrl() {
        return this.annurl;
    }

    public String getMalUrl() {
        return this.url; 
    }  

    public String getImgUrl() {
        return this.imgurl;
    }

    public BigDecimal getScore() {
        return this.score;
    }

    public Integer getRank() {
        return this.rank;
    }



}
