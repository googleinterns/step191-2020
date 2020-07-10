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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login-status")
public class LoginServlet extends HttpServlet { 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    
    boolean isLoggedIn = userService.isUserLoggedIn();
    String url;
    String nickname = "";

    if (isLoggedIn) {
      url = userService.createLogoutURL("/");
      Optional<String> optNickname = getUserNickname(userService.getCurrentUser().getUserId());
      if (!optNickname.isPresent()) {
        setNickname(userService.getCurrentUser().getUserId(), userService.getCurrentUser().getEmail());
        nickname = userService.getCurrentUser().getEmail();
      } else {
        nickname = optNickname.get();
      }
    } else {
      url = userService.createLoginURL("/#comments");
    }

    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("isLoggedIn", isLoggedIn);
    jsonObj.addProperty("url", url);
    jsonObj.addProperty("nickname", nickname);
    
    String json = new Gson().toJson(jsonObj);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/");
      return;
    }
    
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);
    String nickname = jsonObj.get("nickname").getAsString();
    String id = userService.getCurrentUser().getUserId();
    
    setNickname(id, nickname);
  }

  /**
   * Returns the nickname of the user with id, or empty Optional if the user has not set a nickname.
   */
  private Optional<String> getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return Optional.empty();
    }
    String nickname = (String) entity.getProperty("nickname");
    return Optional.of(nickname);
  }

  /**
   * Function that sets the User's nickname in DS
   */
  private void setNickname(String id, String nickname) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("UserInfo", id);
    entity.setProperty("id", id);
    entity.setProperty("nickname", nickname);
    // The put() function automatically inserts new data or updates existing data based on ID
    datastore.put(entity);
  }

}
