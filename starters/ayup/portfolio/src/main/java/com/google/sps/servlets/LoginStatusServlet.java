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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      // TODO: Find a more efficient way to put this on the html
      response.getWriter().println("<p>Hello " + userEmail + "! Post a comment</p>");
      response.getWriter().println("<form method=\"POST\" action=\"/new-comment\">");
      response.getWriter().println("<input class=\"new-comment-input\" type=\"text\" name=\"title\" placeholder=\"Title\"/><br>");
      response.getWriter().println("<textarea class=\"new-comment-textarea\" type=\"text\" name=\"description\" placeholder=\"Description\"></textarea><br>");
      response.getWriter().println("<input class=\"new-comment-input\"type=\"text\" name=\"username\" placeholder=\"Username\" /><br>");
      response.getWriter().println("<button  class=\"btn btn-danger\" id=\"about-button\">Submit</button>");
      response.getWriter().println("</form>");
      response.getWriter().println("<a class=\"btn btn-primary\" id=\"about-button\" role=\"button\" href=\"" + logoutUrl + "\">Log Out</a>");
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      response.getWriter().println("<p>You need to log in to comment.</p>");
      response.getWriter().println("<a class=\"btn btn-primary\" id=\"about-button\" role=\"button\" href=\"" + loginUrl + "\">Log In</a>");
    }
  }
}
