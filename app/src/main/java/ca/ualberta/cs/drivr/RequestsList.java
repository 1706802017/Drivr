/*
 * Copyright 2016 CMPUT301F16T10
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package ca.ualberta.cs.drivr;

import java.util.ArrayList;

/**
 * Stores a list of requests and provides some useful operations on it.
 * @see Request
 */
public class RequestsList {

    /**
     * The list of requests.
     */
    private ArrayList<Request> requestsList;

    /**
     * Instantiates a new item_request list.
     */
    public RequestsList() {
        requestsList = new ArrayList<Request>();
    }

    /**
     * Add a item_request to the list.
     * @param request The item_request to add.
     */
    public void add(Request request) {
        requestsList.add(request);
    }

    /**
     * Clears all requests from the collection.
     */
    public void clear() {
        requestsList.clear();
    }

    /**
     * Removes a item_request from the list if it exists.
     * @param request The item_request to remove.
     */
    public void remove(Request request) {
        requestsList.remove(request);
    }

    /**
     * Remove a requests by the ID.
     * @param request The request to use the ID from.
     */
    public void removeById(Request request) {
        // Make a list of requests to remove and remove them in a separate loop. This is because we
        // cannot modify a collection while we are iterating over it.
        ArrayList<Request> requestsToRemove = new ArrayList<>();
        for (Request existingRequest : requestsList) {
            if (existingRequest.getId().equals(request.getId())) {
                requestsToRemove.add(existingRequest);
            }
        }
        for (Request requestToRemove : requestsToRemove)
            requestsList.remove(requestToRemove);

        /*
        Old version
        for (Request existingRequest : requestsList) {
            if (existingRequest.getId().equals(request.getId())) {
                requestsList.remove(existingRequest);
            }
        }
        */
    }

    /**
     * Test if the list has a item_request.
     * @param request The item_request to check.
     * @return True if the list has the item_request, false otherwise.
     */
    public Boolean has(Request request) {
        return requestsList.contains(request);
    }

    /**
     * Determine if a request exists by ID.
     * @param request The request to check the ID against.
     * @return True if exists, false otherwise.
     */
    public Boolean hasById(Request request) {
        for (Request existingRequest : requestsList) {
            if (existingRequest.getId().equals(request.getId())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Get the number of requests in the list.
     * @return The size of the list.
     */
    public int size() {
        return requestsList.size();
    }

    /**
     * Gets a request by the ID.
     * @param id The ID of the request.
     * @return The request or null if the ID does not exist.
     */
    public Request getById(String id) {
        for (Request request: requestsList) {
            if (request.getId().equals(id))
                return request;
        }
        return null;
    }

    /**
     * Get a the list of all requests.
     * @return All requests.
     */
    public ArrayList<Request> getRequests() {
        return requestsList;
    }

    /**
     * Get a sub-list of all requests in the list that have agiven state.
     * @param state The state to find.
     * @return A list of requests with the state.
     */
    public ArrayList<Request> getRequests(RequestState state) {
        ArrayList<Request> result = new ArrayList<Request>();
        for (Request request : requestsList)
            if (request.getRequestState() == state)
                result.add(request);
        return result;
    }
}
