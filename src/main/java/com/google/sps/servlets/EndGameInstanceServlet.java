package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List; 

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.GameInstanceDao;
import com.google.sps.data.GameInstance;

import java.util.List; 
import java.util.ArrayList; 

@WebServlet("/endGameInstance")
public class EndGameInstanceServlet extends HttpServlet {


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
    GameInstance newRoom = dao.getGameInstance(roomId);
        
    if(newRoom == null){
        response.setStatus(404);
        response.getWriter().println("Error, game instance not found.");
        return;
    }

    newRoom.setIsActive(false);
    newRoom.setCurrentQuestion(null);
    newRoom.setCurrentQuestionActive(false);
    newRoom.setIsFinished(true);
    dao.updateGameInstance(newRoom);


  }

}
