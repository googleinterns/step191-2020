package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.sps.daos.UserDao;

@WebServlet("/verifyUserInFirestore")
public class UserServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);

    UserDao dao = (UserDao) this.getServletContext().getAttribute("userDao");

    dao.createIfNotExists(jsonObj.get("idToken").getAsString());

  }

}
