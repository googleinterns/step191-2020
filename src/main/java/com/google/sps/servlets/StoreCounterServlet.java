package com.google.sps.servlets;

import com.google.sps.daos.CounterDao;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/store")
public class StoreCounterServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Storing to Firestore</h1>");

    // Writing to Realtime DB
    CounterDao counterDao = (CounterDao) this.getServletContext().getAttribute("dao");

    // Increase counter by one
    counterDao.storeCounter();
    
  }

}
