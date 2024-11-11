package org.cs4471.dogsite.controller;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import reactor.core.publisher.Mono;

/*
 * Static controller for the anime microservice
 */



@Controller

//replace with my API urls
//process MAL json in webpage not here.
public class StaticController {
   
    //to be deleted and replaced by anime, a sanity check to see the server is still working
    @GetMapping("/")
    public String testpage(Model model) {
        String response = WebClient.builder().baseUrl("https://dog.ceo/api/breeds/image/random").build().get().retrieve().bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(Exception.class, ex -> Mono.just(""))
                .block();


        

        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> mapping = new Gson().fromJson(response, type);

        model.addAttribute("imgurl", mapping.get("message"));
        
        return "hello";
    }

    
    //current getmapping is placeholder
    @GetMapping("/anime")
    public String anime(Model model) {
        JSONObject gann = getAnnTitles();
        JSONArray items = gann.getJSONArray("item");


        //model.addAttribute("anntitles",getAnnTitles().toString());
        jikanConnector(items, model);


        


        return "anime";
    }
    

    //helper method
    //gets top ann animes labelled for json processing
    private JSONObject getAnnTitles() {
        //5 most recent articles
        String ann_response = WebClient.builder().baseUrl("https://www.animenewsnetwork.com/encyclopedia/reports.xml?id=155&type=anime&nlist=5")
        .build()
        .get()
        .retrieve()
        .bodyToMono(String.class)
        .timeout(Duration.ofSeconds(10))
        .onErrorResume(Exception.class, ex -> Mono.just(""))
        .block();

        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();

        try {
            //converts XML to json object
            JSONObject xmlJSONObj = XML.toJSONObject(ann_response);
            return xmlJSONObj;
        } catch (JSONException je) {
            return null;
        }
        
    }

    //helper method
    //get MAL data of that title
    private JSONObject getJikanOnce(String title) {
        //String apiurl = "https://api.jikan.moe/v4/anime?q='%s'";
        String apiquery = String.format("https://api.jikan.moe/v4/anime?q='%s'", title);

        String mal_response = WebClient.builder().baseUrl(apiquery)
        .build()
        .get()
        .retrieve()
        .bodyToMono(String.class)
        .timeout(Duration.ofSeconds(10))
        .onErrorResume(Exception.class, ex -> Mono.just(""))
        .block();


        try {
            JSONObject jikanAPI = new Gson().fromJson(mal_response, JSONObject.class);
            
            JSONArray data = jikanAPI.getJSONArray("data");

            JSONObject firstResult = data.getJSONObject(0);

            return firstResult;
        } catch (JSONException je) {
            return null;
        }
        

        
    }

    private boolean  jikanConnector(JSONArray items, Model model) {
        try{
            if (items == null) {
                return false;
            }
            else {
                for (int i = 0; i < items.length(); i++) {
                    //ANN
                    JSONObject item = items.getJSONObject(i);
                    String title = item.getString("name");
                    int id = item.getInt("id");
                    
                    //handles that bootstrap will use
                    String handle = String.format("anime%d", i); //json for the title on MAL
                    String annHandle = String.format("ann%d", i); //anime news network wiki page
        
                    //pass to MAL Jikan API
                    model.addAttribute(handle, getJikanOnce(title)); //json
                    model.addAttribute(annHandle,annPage(id)); //url

                    


                }
                return true;
            }
        }catch (JSONException je) {
            return false;
        }
        
    

    }

    //creates link for ann page
    private String annPage(int id) {
        return String.format("https://www.animenewsnetwork.com/encyclopedia/anime.php?id=%d", id);
    }
    
    
 

    
    
    
    
    
    
    
    //Plan is for this to be the anime news to get the top 5 recent anime titles in the news and search the current rating in My Anime List


}
