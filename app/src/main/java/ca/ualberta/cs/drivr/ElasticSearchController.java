package ca.ualberta.cs.drivr;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * In ElasticSearchController, the actual calls to ElasticSearch are done. This requires the use of
 * internal classes that use asynchronous tasks in order to perform these calls, as attempts at not
 * using them have resulted in ElasticSearch not working.
 *
 * The controller contains 5 classes for requests and 2 for users. For requests, it can add a
 * request, update it, search for requests by username, by location or by keyword. Requests are
 * stored in a document that doesn't translate 1-to-1 with Request, requiring the use of a model
 * class named ElasticSearchRequest used solely in this class and solely just for allowing Request
 * to be gotten from ElasticSearch
 *
 * For users, it adds and updates users using the same class, and it can also search for a user.
 *
 * @author Tiegan Bonowicz
 * @see ElasticSearch
 * @see ElasticSearchRequest
 */

//TODO: Build the search result for Requests.
public class ElasticSearchController {
    private static JestDroidClient client;

    /**
     * Here, a request is added to ElasticSearch by first building the add query for the document
     * to be stored in ElasticSearch, then adding it into ElasticSearch, then immediately afterwards
     * getting the ID where it's stored, appending it to the end of the add query and then replacing
     * the query we just added with the new query including the ID.
     */
    public static class AddRequest extends
            AsyncTask<Request, Void, Void> {

        /**
         * Add query:
         * {
         *     "rider": "rider" (in Request)
         *     "driver": [{
         *         "username": "username", (in Request.Driver)
         *         "status": "status" (in Request.Driver)
         *     }, ...],
         *     "description": "description" (in Request)
         *     "fare": fare (in Request)
         *     "date": "date" (in Request)
         *     "start": [ startLongitude, startLatitude], (in Request)
         *     "end": [ endLongitude, endLatitude] (in Request)
         *     "id": "id" (gotten from ElasticSearch after first add)
         * }
         *
         * @param requests The request given by the user.
         * @return null
         */
        @Override
        protected Void doInBackground(Request... requests) {
            verifySettings();
            for(Request request: requests) {
                String add = "{" +
                        "\"rider\": \"" + request.getRider() + "\"," +
                        "\"driver\": [";
                        for(int i = 0; i < size.drivers(); i++) {
                            add = add + "{\"driver\": \"" + drivers.getDriver() + "\", \"status\": \""
                                    + drivers.getStatus() + "\"";
                            if(i != size.drivers()-1) {
                                add += "}, ";
                            }
                        }

                add += "}]," +
                        "\"description\": \"" + request.getDescription() + "\"," +
                        "\"fare\": " + request.getFare().toString() + "," +
                        "\"date\": \"" + request.getDate() + "\"," + // Change this.
                        "\"start\": [" + Double.toString(request.getSourcePlace().getLatLng().longitude)
                        + ", " + Double.toString(request.getSourcePlace().getLatLng().latitude) + "]," +
                        "\"end\": [" + Double.toString(request.getDestinationPlace().getLatLng().longitude)
                        + ", " + Double.toString(request.getDestinationPlace().getLatLng().latitude) + "]";

                Index index = new Index.Builder(add + "}")
                        .index("drivrtestzone")
                        .type("requests")
                        .build();
                try {
                    DocumentResult result = client.execute(index);
                    if(result.isSucceeded()) {
                        request.setId(result.getId());
                        add += ", \"id:\" \"" + request.getId() + "\" }";
                        index = new Index.Builder(add + "}").index("drivrtestzone").type("requests")
                                .index(request.getId()).build();
                        try {
                            result = client.execute(index);
                            if(!result.isSucceeded()) {
                                Log.i("Error", "Failed to insert the request into elastic search.");
                            }
                        } catch (Exception e) {
                            Log.i("Error", "The application failed to build and send the requests.");
                        }
                    } else {
                        Log.i("Error", "Failed to insert the request into elastic search.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the requests.");
                }

            }
            return null;
        }
    }

    // Updates a request in ElasticSearch
    public static class UpdateRequest extends
            AsyncTask<Request, Void, Void> {

        /**
         * Add query:
         * {
         *     "rider": "rider" (in Request)
         *     "driver": [{
         *         "username": "username", (in Request.Driver)
         *         "status": "status" (in Request.Driver)
         *     }, ...],
         *     "description": "description" (in Request)
         *     "fare": fare (in Request)
         *     "date": "date" (in Request)
         *     "start": [ startLongitude, startLatitude], (in Request)
         *     "end": [ endLongitude, endLatitude] (in Request)
         *     "id": "id" (in Request)
         * }
         *
         * @param requests The request given by the user.
         * @return null
         */
        @Override
        protected Void doInBackground(Request... requests) {
            verifySettings();
            for(Request request: requests) {
                String add = "{" +
                        "\"rider\": \"" + request.getRider() + "\"," +
                        "\"driver\": [";
                for(int i = 0; i < size.drivers(); i++) {
                    add = add + "{\"driver\": \"" + drivers.getDriver() + "\", \"status\": \""
                            + drivers.getStatus() + "\"";
                    if(i != size.drivers()-1) {
                        add += "}, ";
                    }
                }

                add += "}]," +
                        "\"description\": \"" + request.getDescription() + "\"," +
                        "\"fare\": " + request.getFare().toString() + "," +
                        "\"date\": \"" + request.getDate() + "\"," + // Change this.
                        "\"start\": [" + Double.toString(request.getSourcePlace().getLatLng().longitude)
                        + ", " + Double.toString(request.getSourcePlace().getLatLng().latitude) + "]," +
                        "\"end\": [" + Double.toString(request.getDestinationPlace().getLatLng().longitude)
                        + ", " + Double.toString(request.getDestinationPlace().getLatLng().latitude) + "], "
                        + "\"id\": \"" + request.getId() + "\"}";

                Index index = new Index.Builder(add)
                        .index("drivrtestzone")
                        .type("request")
                        .id(request.getId())
                        .build();

                try {
                    DocumentResult result = client.execute(index);
                    if(!result.isSucceeded()) {
                        Log.i("Error", "Failed to insert the request into elastic search.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the requests.");
                }

            }
            return null;
        }
    }

    /**
     * Here, all the requests that have the description containing the keyword given by the user
     * will be gotten.
     */
    public static class SearchForKeywordRequests extends
            AsyncTask<String, Void, ArrayList<Request>> {

        /**
         * Search query:
         * {
         *     "from": 0, "size": 10000, "query":
         *     {
         *         "match":
         *         {
         *             "description": "keyword" (given by user)
         *         }
         *     }
         * }
         *
         * @param keywords The keyword specified by the user
         * @return ArrayList<Request>
         */
        @Override
        protected ArrayList<Request> doInBackground(String... keywords) {
            verifySettings();
            ArrayList<Request> requests = new ArrayList<Request>();
            for (String keyword: keywords) {
                String search_string = "{\"from\": 0, \"size\": 10000, "
                        + "\"query\": {\"match\": {\"description\": \""
                        + keyword + "\"}}}";

                Search search = new Search.Builder(search_string)
                        .addIndex("drivrtestzone")
                        .addType("request")
                        .build();

                try {
                    SearchResult result = client.execute(search);
                    if (result.isSucceeded()) {
                        //TODO: Fix this to build the object from a hit list.
                        List<Request> foundRequests = result
                                .getSourceAsObjectList(Request.class);
                        requests.addAll(foundRequests);
                    } else {
                        Log.i("Error", "The search executed but it didn't work.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "Executing the get user method failed.");
                }

            }
            return requests;
        }
    }

    /**
     * Here, the program will get take in the specified geolocation from the user, search within a
     * 50 km radius for requests where the start locations are within that range and return the
     * gotten requests.
     */
    public static class SearchForLocationRequests extends
            AsyncTask<Location, Void, ArrayList<Request>> {

        /**
         * Search query:
         * {
         *     "from": 0, "size": 10000, "geo_distance":
         *     {
         *         "distance": "50km",
         *         "start": [geolocation Longitude, geolocationLatitude]
         *     }
         * }
         *
         * @param geolocations The location given by the user.
         * @return ArrayList<Request>
         */
        @Override
        protected ArrayList<Request> doInBackground(Location... geolocations) {
            verifySettings();
            ArrayList<Request> requests = new ArrayList<Request>();
            for (Location geolocation : geolocations) {
                String search_string = "{\"from\": 0, \"size\": 10000, \"geo_distance\":"
                        + "{ \"distance\": \"50km\", \"start\": ["
                        + Double.toString(geolocation.getLongitude()) + ", "
                        + Double.toString(geolocation.getLatitude())
                        + "]}}";

                Search search = new Search.Builder(search_string)
                        .addIndex("drivrtestzone")
                        .addType("request")
                        .build();

                try {
                    SearchResult result = client.execute(search);
                    if (result.isSucceeded()) {
                        //TODO: Fix this to build the object from a hit list.
                        List<Request> foundRequests = result
                                .getSourceAsObjectList(Request.class);
                        requests.addAll(foundRequests);
                    } else {
                        Log.i("Error", "The search executed but it didn't work.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "Executing the get user method failed.");
                }

            }
            return requests;
        }
    }

    /**
     * Here, all requests associated to a user are gotten by checking all rider and driver usernames
     * to see if they contain the user's username, adding them to the ArrayList and then returning
     * that ArrayList.
     */
    public static class SearchForRequests extends AsyncTask<String, Void, ArrayList<Request>> {

        /**
         * Search query:
         * {
         *     "from": 0, "size": 10000, "query":
         *     {
         *         "match":
         *         {
         *             "rider": "username" (given by user)
         *             "driver":
         *             {
         *                 "username": "username" (given by user)
         *             }
         *         }
         *     }
         * }
         *
         * @param usernames The username given by the user.
         * @return ArrayList<Request>
         */
        @Override
        protected ArrayList<Request> doInBackground(String... usernames) {
            verifySettings();
            ArrayList<Request> requests = new ArrayList<Request>();
            for(String username: usernames) {
                String search_string = "{\"from\": 0, \"size\": 10000, "
                        + "\"query\": {\"term\": " +
                        "{\"rider\": \"" + username +
                        "\", \"driver\": {\"username\": \"" + username + "\"}}}}";

                Search search = new Search.Builder(search_string)
                        .addIndex("drivrtestzone")
                        .addType("request")
                        .build();

                try {
                    SearchResult result = client.execute(search);
                    if (result.isSucceeded()) {
                        //TODO: Fix this to build the object from a hit list.
                        List<Request> foundRequests = result
                                .getSourceAsObjectList(Request.class);
                        requests.addAll(foundRequests);
                    } else {
                        Log.i("Error", "The search executed but it didn't work.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "Executing the get user method failed.");
                }
            }

            return requests;
        }
    }

    /**
     * Here, the user is either added or updated (since this class handles both) in ElasticSearch
     * by building it then adding it in the ID which is the same as the user's unique username.
     */
    public static class AddUser extends
            AsyncTask<User, Void, Void> {

        /**
         * Add query:
         * {
         *     "name": "name", (in User)
         *     "username": "username", (in User)
         *     "email": "email", (in User)
         *     "phoneNumber": "phoneNumber" (in User)
         * }
         *
         * @param users The user to be added.
         * @return null
         */
        @Override
        protected Void doInBackground(User... users) {
            verifySettings();
            for(User user: users) {
                String addUser = "{\"name\": \"" + user.getName() + "\"" +
                        "\"username\": \"" + user.getUsername() + "\"," +
                        "\"email\": \"" + user.getEmail() + "\"," +
                        "\"phoneNumber\": \"" + user.getPhoneNumber() + "\"}";

                Index index = new Index.Builder(addUser)
                        .index("drivrtestzone")
                        .type("user")
                        .id(user.getUsername())
                        .build();

                try {
                    DocumentResult result = client.execute(index);
                    if(!result.isSucceeded()) {
                        Log.i("Error", "Failed to insert the request into elastic search.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the requests.");
                }

            }
            return null;
        }
    }

    /**
     * Here, we get a user profile by searching for the unique username and returning it when we
     * find it.
     */
    public static class GetUser extends AsyncTask<String, Void, User> {

        /**
         * Search query:
         * {
         *     "query":
         *     {
         *         "match":
         *         {
         *             "username": "username" (given by user)
         *         }
         *     }
         * }
         * @param usernames Username to be gotten.
         * @return user
         */
        @Override
        protected User doInBackground(String... usernames) {
            verifySettings();
            User user = new User();
            for(String username: usernames) {
                String search_string = "{\"query\": {\"match\": "
                        + "{\"username\": \"" + username + "\" }}}";

                Search search = new Search.Builder(search_string)
                        .addIndex("drivrtestzone")
                        .addType("user")
                        .build();

                try {
                    SearchResult result = client.execute(search);
                    if (result.isSucceeded()) {
                        user = result.getSourceAsObject(User.class);
                    } else {
                        Log.i("Error", "The search executed but it didn't work.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "Executing the get user method failed.");
                }
            }

            return user;
        }
    }

    /**
     * Here, we set our client settings to make sure it's on the correct website where our
     * ElasticSearch results are stored.
     */
    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig
                    .Builder("http://cmput301.softwareprocess.es:8080/");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}