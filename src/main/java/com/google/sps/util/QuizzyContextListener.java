package com.google.sps.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.sps.daos.RealtimeDao;

@WebListener("Context Listener")
public class QuizzyContextListener implements ServletContextListener {
  @Override
  public void contextDestroyed(javax.servlet.ServletContextEvent event) {
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    // This function is called when the application starts and will safely set a few required
    // context attributes such as the RealtimeDao

    RealtimeDao dao = (RealtimeDao) event.getServletContext().getAttribute("dao");
    if (dao == null) {
      dao = new RealtimeDao();
      event.getServletContext().setAttribute("dao", dao);
    }
  }
}
