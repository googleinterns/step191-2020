package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import com.google.sps.daos.RoomDao;
import com.google.sps.data.Room;
import com.google.sps.data.Member;

@WebServlet("/joinRoom")
public class JoinRoomServlet extends HttpServlet {
  
  private static Logger log = Logger.getLogger(JoinRoomServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    // Generate room key
    String roomId = request.getParameter("room");

    if(roomId==null || roomId.isEmpty()){
        response.setStatus(500);
        response.getWriter().println("Room not specified");
        return;
    }

    String uId = "aroquev";
    RoomDao dao = (RoomDao) this.getServletContext().getAttribute("roomDao");
    Room newRoom = dao.getRoom(roomId);

    if(newRoom != null){
        newRoom.addMember(new Member(uId));
        dao.updateRoom(newRoom);
        response.getWriter().println(uId + " added");
    } else {
        response.setStatus(404);
        response.getWriter().println("Error, not found.");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
  }

}
