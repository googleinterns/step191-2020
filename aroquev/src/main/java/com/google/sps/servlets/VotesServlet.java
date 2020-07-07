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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that updates upvotes or downvotes of a comment*/
@WebServlet("/vote-comment")
public class VotesServlet extends HttpServlet { 

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);

    long id = (Long) jsonObj.get("commentId").getAsLong();
    boolean choice = jsonObj.get("commentChoice").getAsBoolean();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key commentEntityKey = KeyFactory.createKey("Comment", id);

    Entity comment = null;
    try {
      comment = datastore.get(commentEntityKey);
    } catch (Exception e) {
      //TODO: handle exception
    }
    
    if (choice) {
      int upvotes = Math.toIntExact((long) comment.getProperty("upvotes"));
      upvotes++;
      comment.setProperty("upvotes", upvotes);
    } else {
      int downvotes = Math.toIntExact((long) comment.getProperty("downvotes"));
      downvotes++;
      comment.setProperty("downvotes", downvotes);
    }
    
    datastore.put(comment);
  }

  private String deserializeJson(HttpServletRequest request) {
    StringBuilder jsonBuff = new StringBuilder();
    String line = null;
    try {
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
            jsonBuff.append(line);
    } catch (Exception e) { 
      /*error*/ 
    }

    return jsonBuff.toString();
  }

}
