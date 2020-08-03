package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.GameInstanceDao;

@WebServlet("/newGameInstance")
public class CreateGameInstanceServlet extends HttpServlet {
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);

    GameInstanceDao dao = (GameInstanceDao) this.getServletContext().getAttribute("gameInstanceDao");

    dao.createNewGameInstance(jsonObj.get("idToken").getAsString(), jsonObj.get("gameId").getAsString());
  }

}
