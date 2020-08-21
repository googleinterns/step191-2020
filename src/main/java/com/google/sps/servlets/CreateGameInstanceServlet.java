package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.GameDao;
import com.google.sps.daos.GameInstanceDao;
import com.google.sps.data.Game;

@WebServlet("/newGameInstance")
public class CreateGameInstanceServlet extends HttpServlet {
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);

    GameDao gameDao = (GameDao) this.getServletContext().getAttribute("gameDao");

    Game game = gameDao.getGame(jsonObj.get("gameId").getAsString());

    GameInstanceDao gameInstanceDao = (GameInstanceDao) this.getServletContext().getAttribute("gameInstanceDao");

    String newGameInstanceId = gameInstanceDao.createNewGameInstance(jsonObj.get("idToken").getAsString(), jsonObj.get("gameId").getAsString(), game);

    // Convert the newGameInstanceId to JSON
    String json = convertToJson(newGameInstanceId);
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
