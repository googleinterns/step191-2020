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
import com.google.sps.data.GameInstance;
import com.google.sps.daos.GameDao;
import com.google.sps.data.Game;
import com.google.sps.data.Question;
import com.google.sps.servlets.StartGameInstanceServlet;

import com.google.cloud.firestore.Firestore;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.springframework.mock.web.DelegatingServletInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

@RunWith(JUnit4.class)
public final class StartGameInstanceServletTest {

  private GameInstanceDao mockGameInstanceDao = mock(GameInstanceDao.class);
  private ServletContext mockServletContext = mock(ServletContext.class);
  private GameDao mockGameDao = mock(GameDao.class);
  private HttpServletRequest request = mock(HttpServletRequest.class);
  private HttpServletResponse response = mock(HttpServletResponse.class);
  private String roomId = "aSpX3cmZa5PB994uEoW2";
  private String gameId = "iBxba1vsaWT1SIqxeonJ";
  private StartGameInstanceServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    servletUnderTest = new StartGameInstanceServlet() {
      @Override
      public ServletContext getServletContext() {
        return mockServletContext;
      }
    };
    
    when(mockServletContext.getAttribute("gameInstanceDao")).thenReturn(mockGameInstanceDao);
    when(mockServletContext.getAttribute("gameDao")).thenReturn(mockGameDao);
    when(request.getParameter("gameInstance")).thenReturn(roomId);

  }

  @Test
  public void doPostStartGameInstance() throws IOException {

    //Simulate Game 
    Game newGame = newGame = Game.builder()
          .title("")
          .creator("")
          .headQuestion("NWUzaBz7SJEiKvQEwAkt")
          .questions(new ArrayList<Question>())
          .build();

    when(mockGameDao.getGame(gameId)).thenReturn(newGame);

    //Simulate Room to be updated
    GameInstance newRoom = new GameInstance(roomId);
    newRoom.setGameId(gameId);
    newRoom.setCreator("UfQ5TlrtFtNJnR1ywXb8W7Hrz6y1");
    newRoom.setIsActive(true);
    newRoom.setCurrentQuestion("NWUzaBz7SJEiKvQEwAkt");

    //Return mock room 
    when(mockGameInstanceDao.getGameInstance(roomId)).thenReturn(newRoom);

    servletUnderTest.doPost(request, response);

    verify(mockGameDao).getGame(gameId);
    verify(mockGameInstanceDao).getGameInstance(roomId);
    verify(mockGameInstanceDao).updateGameInstance(newRoom);
  }

}
