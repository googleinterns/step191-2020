package com.google.sps.servlets;

import com.google.firebase.auth.FirebaseToken;
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
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);

    RoomDao dao = (RoomDao) this.getServletContext().getAttribute("roomDao");

    dao.createNewRoom(jsonObj.get("idToken").getAsString(), jsonObj.get("gameId").getAsString());
  }

}
