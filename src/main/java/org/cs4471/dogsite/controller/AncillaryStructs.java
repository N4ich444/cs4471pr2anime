package org.cs4471.dogsite.controller;

import org.json.JSONException;
import org.json.JSONObject;

//pojo for the storage thing
public class AncillaryStructs {
    //ann variables
    String name = "";
    String annurl = "";
    //mal variables
    String url = "";
    String imgurl = "";
    Float score = -1f;
    Integer rank = -1;

    public AncillaryStructs(String name, String url) {
        this.annurl = url;
        this.name = name;
    }

    public void addMalRating(JSONObject data, String name, String annlink) {
        try {
            if (!data.isEmpty()) {
                this.url = data.getString("url");
                this.score = data.getFloat("score");
                this.rank = data.getInt("rank");

                JSONObject img = data.getJSONObject("images");

                JSONObject jpg = data.getJSONObject("jpg");

                String imgurl = data.getString("image_url");

                if (imgurl != null) {
                    this.imgurl = imgurl;
                }

            }
        }
        catch (JSONException je) {
            System.err.println(String.format("MALRating error: %s", je));

        }
        
        
    }

    //getters
    public String getName() {
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

    public Float getScore() {
        return this.score;
    }

    public Integer getRank() {
        return this.rank;
    }



}
