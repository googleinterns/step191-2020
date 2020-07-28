package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.RoomDao;

@WebServlet("/startGame")
public class StartGame extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 

    // Activate room
    RoomDao dao = (RoomDao) this.getServletContext().getAttribute("roomDao");
    dao.startGame();
    //Todo: get room's game and get questions

    response.getWriter().println(dao.getActualRoom());
  }

}
