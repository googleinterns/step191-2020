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

import java.io.IOException;
import com.google.common.collect.ImmutableList; 
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/new-game")
public class NewGameServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException {
      String gameTitle = request.getParameter("title");
      String question = request.getParameter("question");
      String correctAnswer = request.getParameter("correct-answer");
      String wrongAnswer = request.getParameter("wrong-answer");
      String uid = request.getParameter("uid");
        
      System.out.println(gameTitle);
      System.out.println(question);
      System.out.println(correctAnswer);
      System.out.println(wrongAnswer);
      System.out.println(uid);
      response.sendRedirect("/create-game.html");
  }
}
