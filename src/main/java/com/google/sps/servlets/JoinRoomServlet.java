package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.RoomDao;

@WebServlet("/joinRoom")
public class JoinRoomServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    // Generate room key
    String roomId = "-MD1gAzMoXhFuphNBdd5";
    String uId = "ayup";
    RoomDao dao = (RoomDao) this.getServletContext().getAttribute("roomDao");
    dao.joinRoom(roomId, uId);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // getRoomID
    String roomId = "-MD1gAzMoXhFuphNBdd5";
    String uId = "aroquev";
    RoomDao dao = (RoomDao) this.getServletContext().getAttribute("roomDao");
    dao.joinRoom(roomId, uId);
  }

}