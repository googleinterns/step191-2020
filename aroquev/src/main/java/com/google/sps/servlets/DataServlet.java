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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet { 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int maxComments = Integer.parseInt(request.getParameter("maxComments"));

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    final ArrayList<Comment> comments = new ArrayList<Comment>();
    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(maxComments))) {
      long id = entity.getKey().getId();
      String username = (String) entity.getProperty("commentUsername");
      String body = (String) entity.getProperty("commentBody");
      long timestamp = (long) entity.getProperty("timestamp");
      int upvotes = ((Long) entity.getProperty("upvotes")).intValue();
      int downvotes = ((Long) entity.getProperty("downvotes")).intValue();

      final Comment comment = new Comment(id, body, username, timestamp, upvotes, downvotes);
      
      comments.add(comment);
    }

    // Convert the comments to JSON
    String json = convertToJson(comments);

    // Send the JSON as response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);
    String comment = jsonObj.get("commentBody").getAsString();

    UserService userService = UserServiceFactory.getUserService();

    String username = userService.getCurrentUser().getEmail();
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("commentUsername", username);
    commentEntity.setProperty("commentBody", comment);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("upvotes", 0);
    commentEntity.setProperty("downvotes", 0);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  /**
   * Convert to JSON using Gson
   */
  private String convertToJson(ArrayList<Comment> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

}
