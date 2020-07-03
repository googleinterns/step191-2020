// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.gson.Gson;
import java.io.Reader;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/** Servlet that returns some example content.*/
@WebServlet("/vote-comment")
public class VotesServlet extends HttpServlet { 

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    StringBuilder jsonBuff = new StringBuilder();
    String line = null;
    try {
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
            jsonBuff.append(line);
    } catch (Exception e) { /*error*/ }
    String jsonString = jsonBuff.toString();
    System.out.println("Request JSON string :" + jsonBuff.toString());

    Gson gson = new Gson();
    JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);
    // Query query = new Query("Comment");

    // DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    // PreparedQuery results = datastore.prepare(query);
    
    // Key commentKey;
    // for (Entity entity : results.asIterable()) {
    //   commentKey = entity.getKey();
    //   datastore.delete(commentKey);
    // }
  }

}
