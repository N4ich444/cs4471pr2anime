package org.cs4471.dogsite.controller;

import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

/*
 * Static controller for the anime microservice
 */



@Controller

//replace with my API urls
//process MAL json in webpage not here.
public class StaticController {
   

    
    //current getmapping is placeholder
    //todo error checking
    @GetMapping("/")
    public String anime(Model model) {
        JSONObject gann = getAnnTitles();
        JSONArray items = gann.getJSONArray("item");


        //binds everything needed on anime.html
        Boolean jkc = jikanConnector(items, model);

        //System.out.println(model);

        if (!jkc) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Anime Microservice is currently unavailable.");
        }


        //anime.html
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

        

        try {
            //converts XML to json object
            JSONObject xmlJSONObj = XML.toJSONObject(ann_response);
            return xmlJSONObj.getJSONObject("report");
        } catch (JSONException je) {
            return null;
        }
        
    }

    //helper method
    //get MAL data of that title
    private JSONObject getJikanOnce(String title) {
        //String apiurl = "https://api.jikan.moe/v4/anime?q='%s'";
        //test error handling with %s = zzzz
        String apiquery = String.format("https://api.jikan.moe/v4/anime?q='%s'", title);

        String mal_response = WebClient.builder().baseUrl(apiquery)
        .build()
        .get()
        .retrieve()
        .bodyToMono(String.class)
        .timeout(Duration.ofSeconds(10))
        .onErrorResume(Exception.class, ex -> Mono.just(""))
        .block();

        //system.out.println(mal_response);


        try {
            //JSONObject jikanAPI = new Gson().fromJson(mal_response, JSONObject.class);
            //System.err.println(mal_response);
            JSONObject jikanAPI = new JSONObject(mal_response); //fixed bug
            System.err.println("a");
            
            JSONArray data = jikanAPI.getJSONArray("data");
            System.err.println("b");

            JSONObject firstResult;
            System.err.println("c");

            //checks if anime is not in MAL and returns an empty JSONobject if it is the case
            if (data.isEmpty()) {
                firstResult = new JSONObject();
            }
            else {
                firstResult = data.getJSONObject(0);
            }
            
            return firstResult;
        } catch (JSONException je) {
            System.err.println(je);
            return null;
        }
        

        
    }

    //returns a success or failure status
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
                    
                    //handles that bootstrap can access
                    //String handle = String.format("anime%d", i); //json for the title on MAL
                    //String annHandle = String.format("ann%d", i); //anime news network wiki page
                    //String nameHandle = String.format("name%d", i); //anime news net title (may be redundant)
                    String pojoHandle = String.format("pojo%d", i); //anime news net title

                    System.out.println(annPage(id));
                    
                    //pass to MAL Jikan API
                    String ann = annPage(id);
                    JSONObject mal = getJikanOnce(title);
                    
                    //model.addAttribute(annHandle, ann); //url
                    //model.addAttribute(handle, mal); //json
                    //model.addAttribute(nameHandle, title); //anime name

                    //pojo containing everything
                    AncillaryStructs pojo = new AncillaryStructs(title, ann);
                    pojo.addMalRating(mal, title);
                    model.addAttribute(pojoHandle,pojo);

                    //Jikan API has a request limit
                    //delays to prevent hitting denial
                    try {
                        Thread.sleep(500);  // Delay for 500 milliseconds
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupted state
                    }
                    

                    


                }
                return true;
            }
            //something is wrong with the json file
        }catch (JSONException | NullPointerException je) {
            return false;
        }
        
    

    }

    //creates link for ann page
    private String annPage(int id) {
        return String.format("https://www.animenewsnetwork.com/encyclopedia/anime.php?id=%d", id);
    }
    
    
    
 

    
    
    
    
    
    
    
    //Plan is for this to be the anime news to get the top 5 recent anime titles in the news and search the current rating in My Anime List


}
