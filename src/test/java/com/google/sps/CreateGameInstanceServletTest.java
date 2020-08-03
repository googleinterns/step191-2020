// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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

import com.google.sps.daos.GameInstanceDao;
import com.google.sps.servlets.CreateGameInstanceServlet;

import org.springframework.mock.web.DelegatingServletInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CreateGameInstanceServletTest {

  private GameInstanceDao mockGameInstanceDao = mock(GameInstanceDao.class);
  private ServletContext mockServletContext = mock(ServletContext.class);

  private HttpServletRequest request = mock(HttpServletRequest.class);
  private HttpServletResponse response = mock(HttpServletResponse.class);

  private CreateGameInstanceServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    servletUnderTest = new CreateGameInstanceServlet() {
      @Override
      public ServletContext getServletContext() {
        return mockServletContext;
      }
    };

    when(mockServletContext.getAttribute("gameInstanceDao")).thenReturn(mockGameInstanceDao);
  }

  @Test
  public void doPostNewGameInstance() throws IOException {
    
    String json = "{\"idToken\":\"asdfqwerty\", \"gameId\":\"222\"}";

    when(request.getInputStream()).thenReturn(
        new DelegatingServletInputStream(
            new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
    when(request.getReader()).thenReturn(
        new BufferedReader(new StringReader(json)));
    when(request.getContentType()).thenReturn("application/json");
    when(request.getCharacterEncoding()).thenReturn("UTF-8");

    servletUnderTest.doPost(request, response);
    verify(mockGameInstanceDao).createNewGameInstance("asdfqwerty", "222");
  }
  
}
