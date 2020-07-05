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
@WebServlet("/delete-comment")
public class DeleteCommentServlet extends HttpServlet { 

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String jsonString = deserializeJson(request);
    JsonObject jsonObj = new Gson().fromJson(jsonString, JsonObject.class);

    long id = (Long) jsonObj.get("commentId").getAsLong();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key commentEntityKey = KeyFactory.createKey("Comment", id);
    datastore.delete(commentEntityKey);
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
