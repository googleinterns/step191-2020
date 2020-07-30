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

@WebServlet("/newGameInstance")
public class CreateGameInstanceServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    // Generate room key
    GameInstanceDao dao = (GameInstanceDao) this.getServletContext().getAttribute("gameInstanceDao");
    GameInstance newRoom = dao.createNewGameInstance("5GoXkueDimFjk1wEBLhV");
    
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("roomId", newRoom.getId());
    
    String json = new Gson().toJson(jsonObj);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

  }

}
