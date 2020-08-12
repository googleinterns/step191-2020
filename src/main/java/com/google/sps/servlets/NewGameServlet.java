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

import com.google.sps.data.Game;
import com.google.sps.data.Question;
import com.google.sps.data.Answer;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.daos.DatabaseGameDao;
import com.google.sps.daos.GameDao;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@WebServlet("/newGame")
public class NewGameServlet extends HttpServlet {


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException {
      
    // The servlet recieves the JSON and converts to a JsonObject
    JsonElement gameJsonElem = new JsonParser().parse(request.getParameter("game"));
    JsonObject gameJsonObj = gameJsonElem.getAsJsonObject();

    // We retrieve the attributes from the game
    String gameTitle = gameJsonObj.get("title").getAsString();
    String gameCreator = gameJsonObj.get("creator").getAsString();

    List<Question> questions = new ArrayList(Arrays.asList()); // Here we will store the questions as objects

    // We get the questions as a JsonArray and then we iterate to add them to the list
    JsonArray questionsJsonArray = (JsonArray) gameJsonObj.get("questions");

    for ( int i = 0; i < questionsJsonArray.size(); i++ ) {
      JsonObject questionJsonObj = (JsonObject) questionsJsonArray.get(i);

      // We retrieve the attributes from the question
      String questionTitle = questionJsonObj.get("title").getAsString();
      boolean isMC = questionJsonObj.get("isMC").getAsBoolean();

      List<Answer> answers = new ArrayList(Arrays.asList()); // Here we will store the answers as objects

      // We get the answers as a JsonArray and then we iterate to add them to the list
      JsonArray answersJsonArray = (JsonArray) questionJsonObj.get("answers");

      for ( int j = 0; j < answersJsonArray.size(); j++ ) {
        JsonObject answerJsonObj = (JsonObject) answersJsonArray.get(j);
      
        // We retrieve the attributes from the answer
        String answerTitle = answerJsonObj.get("title").getAsString();
        boolean isCorrect = answerJsonObj.get("correct").getAsBoolean();

        // We create a new answer object with the attributes that we recovered then add the object to the list
        answers.add(new Answer(answerTitle, isCorrect));
      }
      // We create a new question object with the attributes that we recovered and the answers, then we add the object to the list
      questions.add(new Question(questionTitle, answers, isMC));
    }

    // We create the game object
    Game currentGame = Game.builder().title(gameTitle).creator(gameCreator).questions(questions).build(); 
    

    GameDao dao = (GameDao) this.getServletContext().getAttribute("gameDao");
    dao.createNewGame(currentGame);

    response.sendRedirect("/teacher/createGame.html");
  }
}
