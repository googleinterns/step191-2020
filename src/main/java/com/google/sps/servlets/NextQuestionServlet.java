package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List; 

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;

import com.google.sps.daos.GameInstanceDao;
import com.google.sps.data.GameInstance;
import com.google.sps.daos.GameDao;
import com.google.sps.data.Game;

import java.util.List; 
import java.util.ArrayList; 

@WebServlet("/nextQuestion")
public class NextQuestionServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
  // Generate room key
    String roomId = request.getParameter("gameInstance");

    if(roomId==null || roomId.isEmpty()){
        response.setStatus(500);
        response.getWriter().println("Room not specified");
        return;
    }

    GameInstanceDao dao = (GameInstanceDao) this.getServletContext().getAttribute("gameInstanceDao");
    GameDao gameDao = (GameDao) this.getServletContext().getAttribute("gameDao");

    GameInstance newRoom = dao.getGameInstance(roomId);       
    if(newRoom == null){
        response.setStatus(404);
        response.getWriter().println("Error, game instance not found.");
        return;
    }

    String nextQuestionId = gameDao.getQuestionId("nextQuestion", newRoom.getGameId(), newRoom.getCurrentQuestion());

    if(nextQuestionId == null || nextQuestionId.isEmpty()){
        response.setStatus(404);
        response.getWriter().println("Error, there's no more questions");
        System.out.println("Error, there's no more questions");
        //End Game
        newRoom.setIsActive(false);
        newRoom.setCurrentQuestion(null);
        newRoom.setCurrentQuestionActive(false);
        newRoom.setIsFinished(true);
 
    } else {
        newRoom.setCurrentQuestion(nextQuestionId);
        newRoom.setCurrentQuestionActive(true);
    }
    dao.updateGameInstance(newRoom);

  }

}
