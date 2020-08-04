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


import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.sps.data.Game;
import com.google.sps.data.Question;
import com.google.sps.data.Answer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.daos.DatabaseGameDao;
import com.google.sps.daos.GameDao;


@WebServlet("/newGame")
public class NewGameServlet extends HttpServlet {


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException {
      
    // DISCLAIMER: ALL OF THE FOLLOWING STUFF IS HARD CODED FOR THE DEMO
    List<Question> questions = new ArrayList(Arrays.asList());


    List<Answer> answers0 = new ArrayList(Arrays.asList());
    for(int i = 0; i < 4; i++ ){    
      if(request.getParameter("question0answer" + i) == "")
        answers0.add(new Answer(request.getParameter("question0answer" + i), (request.getParameter("question0correct" + i) == null)));
    }
    questions.add(new Question(request.getParameter("question0title"), answers0));


    List<Answer> answers1 = new ArrayList(Arrays.asList());
    answers1.add(new Answer(request.getParameter("question1answer" + 0), true));
    answers1.add(new Answer(request.getParameter("question1answer" + 1), false));
    questions.add(new Question(request.getParameter("question3title"), answers1));

    List<Answer> answers2 = new ArrayList(Arrays.asList());
    for(int i = 0; i < 4; i++ ){    
      if(request.getParameter("question2answer" + i) == "")
        answers2.add(new Answer(request.getParameter("question2answer" + i), (request.getParameter("question2correct" + i) == null)));
    }
    questions.add(new Question(request.getParameter("question2title"), answers2));


    List<Answer> answers3 = new ArrayList(Arrays.asList());
    answers3.add(new Answer(request.getParameter("question3answer" + 0), true));
    answers3.add(new Answer(request.getParameter("question3answer" + 1), false));
    questions.add(new Question(request.getParameter("question3title"), answers3));

    List<Answer> answers4 = new ArrayList(Arrays.asList());
    for(int i = 0; i < 4; i++ ){    
      if(request.getParameter("question4answer" + i) == "")
        answers4.add(new Answer(request.getParameter("question4answer" + i), (request.getParameter("question4answer" + i) == null)));
    }
    questions.add(new Question(request.getParameter("question4title"), answers4));

    Game currentGame = Game.builder().title("Temp").creator(request.getParameter("uid")).questions(questions).build();

    GameDao dao = (GameDao) this.getServletContext().getAttribute("gameDao");
    dao.createNewGame(currentGame);


    // // Creates classes for answers, questions and games so when Game Dao is implemented you just pass a Game class.
    // Answer correct = new Answer(request.getParameter("correct-answer"), true);
    // Answer wrong = new Answer(request.getParameter("wrong-answer"), true);
    // List<Answer> answers = Arrays.asList(correct, wrong);

    // Question question0 = new Question(request.getParameter("question0title"), answers0);
    // List<Question> questions = Arrays.asList(question);

    // Game currentGame = Game.builder().title(request.getParameter("title")).creator(request.getParameter("uid")).questions(questions).build();
    
    // GameDao dao = (GameDao) this.getServletContext().getAttribute("gameDao");
    // dao.createNewGame(currentGame);

    // System.out.println(correct.toString());
    // System.out.println(wrong.toString());
    // System.out.println(question.toString());
    // System.out.println(currentGame.toString());
    response.sendRedirect("/teacher/createGame.html");
  }
}
