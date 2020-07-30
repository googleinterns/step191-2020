package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.GameInstanceDao;
import com.google.sps.data.GameInstance;

@WebServlet("/startGameInstance")
public class StartGameInstanceServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
  // Generate room key
    String roomId = request.getParameter("room");

    if(roomId==null || roomId.isEmpty()){
        response.setStatus(500);
        response.getWriter().println("Room not specified");
        return;
    }

    GameInstanceDao dao = (GameInstanceDao) this.getServletContext().getAttribute("gameInstanceDao");
    GameInstance newRoom = dao.getRoom(roomId);
    // Activate room
    if(newRoom != null){
        newRoom.setIsActive(true);
        dao.updateRoom(newRoom);
        response.getWriter().println("Game started");
    } else {
        response.setStatus(404);
        response.getWriter().println("Error, not found.");
    }

    //Todo: get room's game and get questions

  }

}
