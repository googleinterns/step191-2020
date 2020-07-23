package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.CounterDao;

public class UpdateCounterServlet extends HttpServlet {
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    // Writing to Realtime DB
    CounterDao counterDao = (CounterDao) this.getServletContext().getAttribute("dao");
    
    // Increase counter by one
    counterDao.increaseCounter();
  }

}
