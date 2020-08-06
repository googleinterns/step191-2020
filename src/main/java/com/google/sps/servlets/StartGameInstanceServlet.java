package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List; 

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.cloud.firestore.Firestore;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;

import com.google.sps.daos.GameInstanceDao;
import com.google.sps.data.GameInstance;

import java.util.List; 
import java.util.ArrayList; 

@WebServlet("/startGameInstance")
public class StartGameInstanceServlet extends HttpServlet {


    @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
  // Generate room key
    String roomId = request.getParameter("gameInstance");
    Firestore db = (Firestore) this.getServletContext().getAttribute("firestoreDb");

    if(roomId==null || roomId.isEmpty()){
        response.setStatus(500);
        response.getWriter().println("Room not specified");
        return;
    }

    GameInstanceDao dao = (GameInstanceDao) this.getServletContext().getAttribute("gameInstanceDao");
    GameInstance newRoom = dao.getGameInstance(roomId);
        
    if(newRoom == null){
        response.setStatus(404);
        response.getWriter().println("Error, not found.");
        return;
    }

        ApiFuture<DocumentSnapshot> future = db.collection("games").document(newRoom.getGameId()).get();
        try {
            DocumentSnapshot document = future.get();
            newRoom.setCurrentQuestion(document.get("headQuestion").toString());
        } catch(Exception e) {
            System.out.println(e);
            return;
        }

        // Activate room
        newRoom.setIsActive(true);


    dao.updateGameInstance(newRoom);

  }

}
