package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import com.google.sps.daos.GameInstanceDao;
import com.google.sps.daos.UserDao;
import com.google.sps.data.GameInstance;
import com.google.sps.data.Member;

@WebServlet("/joinGameInstance")
public class JoinGameInstanceServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    // Generate room key
    String roomId = request.getParameter("gameInstance");

    if(roomId==null || roomId.isEmpty()){
        response.setStatus(500);
        response.getWriter().println("Room not specified");
        return;
    }

    // we should do this with a firebase Auth Token
    String uId = "aroquev";
    GameInstanceDao dao = (GameInstanceDao) this.getServletContext().getAttribute("gameInstanceDao");
    GameInstance joinedGameInstance = dao.getGameInstance(roomId);

    if(joinedGameInstance != null){
        joinedGameInstance.addMember(new Member(uId));
        dao.updateGameInstance(joinedGameInstance);
        response.getWriter().println(uId + " added");
    } else {
        response.setStatus(404);
        response.getWriter().println("Error, not found.");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);

    UserDao dao = (UserDao) this.getServletContext().getAttribute("userDao");

    String animal = dao.joinGameInstance(jsonObj.get("idToken").getAsString(), jsonObj.get("gameInstanceId").getAsString());
    
    // Convert the newGameInstanceId to JSON
    String json = convertToJson(animal);
    // Send the JSON as response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Convert to JSON using Gson
   */
  private String convertToJson(String newGameInstanceId) {
    Gson gson = new Gson();
    String json = gson.toJson(newGameInstanceId);
    return json;
  }

}
