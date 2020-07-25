package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.RoomDao;

@WebServlet("/newRoom")
public class CreateRoomServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    // Generate room key
    RoomDao dao = (RoomDao) this.getServletContext().getAttribute("roomDao");
    String roomId = dao.createNewRoom();
    
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("roomId", roomId);
    
    String json = new Gson().toJson(jsonObj);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Generate room key
    RoomDao dao = (RoomDao) this.getServletContext().getAttribute("roomDao");
    dao.createNewRoom();

  }

}
