package com.google.sps;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.UserDao;
import com.google.sps.servlets.UserServlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.DelegatingServletInputStream;

@RunWith(JUnit4.class)
public final class UserServletTest {

  private UserDao mockUserDao = mock(UserDao.class);
  private ServletContext mockServletContext = mock(ServletContext.class);
  
  private HttpServletRequest request = mock(HttpServletRequest.class);
  private HttpServletResponse response = mock(HttpServletResponse.class);
  
  private UserServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    servletUnderTest = new UserServlet() {
      @Override
      public ServletContext getServletContext() {
        return mockServletContext;
      }
    };

    when(mockServletContext.getAttribute("userDao")).thenReturn(mockUserDao);
  }

  @Test
  public void doPostUser() throws IOException {
    String json = "{\"idToken\":\"asdfqwerty\"}";

    when(request.getInputStream()).thenReturn(
        new DelegatingServletInputStream(
            new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(json)));
    when(request.getContentType()).thenReturn("application/json");
    when(request.getCharacterEncoding()).thenReturn("UTF-8");

    servletUnderTest.doPost(request, response);

    verify(mockUserDao).createIfNotExists("asdfqwerty");

  }

}