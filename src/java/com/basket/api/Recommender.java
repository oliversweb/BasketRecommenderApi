/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.basket.api;

import com.basket.OrderItemWithAnonymousRecommender;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Administrator
 */
@Path("recommendations")
public class Recommender {

    private static final String api_version = "00.01.00";

    @GET
    @Path("/version")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVersion() {
        return api_version;
    }

    /**
     * Creates a new instance of Recommender
     */
    public Recommender() {
    }

    /**
     * Retrieves representation of an instance of com.basket.api.Recommender
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecomendations(@QueryParam("products") String productIds) {

        if (productIds == null || productIds.length() == 0) {

            return Response.serverError()
                    .entity("Please supply valid 'productIds'").build();
        }

        JSONArray jsonArrary = new JSONArray();

        try {

            List<String> items = Arrays.asList(productIds.split("\\s*,\\s*"));

            PreferenceArray prefs = getFakeAnonymousBooleanPreferences(items);

            OrderItemWithAnonymousRecommender recommender = new OrderItemWithAnonymousRecommender();

            List<RecommendedItem> recommendations = recommender.recommend(prefs, 5);

            for (RecommendedItem i : recommendations) {

                JSONObject obj = new JSONObject();

                obj.put("Id", i.getItemID());

                obj.put("Value", i.getValue());

                jsonArrary.put(obj);
            }

        } catch (Exception e) {

            return (Response.serverError().entity("Server Exception : " + e.getMessage()).build());
        }

        return (Response.ok(jsonArrary, MediaType.APPLICATION_JSON).build());
    }

    private static PreferenceArray getFakeAnonymousBooleanPreferences(
            List<String> items) {
        PreferenceArray anonymousPrefs = new GenericUserPreferenceArray(
                items.size());

        int counter = 0;
        for (String i : items) {
            anonymousPrefs.setItemID(counter++, Long.valueOf(i).longValue());
        }

        return (anonymousPrefs);
    }
}
