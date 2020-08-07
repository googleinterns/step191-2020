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
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;

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
import org.junit.Assert;
import org.junit.runners.JUnit4;
import org.hamcrest.CoreMatchers;

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
  private String questionId = "NWUzaBz7SJEiKvQEwAkt";
  private StartGameInstanceServlet servletUnderTest;
  private GameInstance newRoom;
  private StringWriter responseWriter;

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

    responseWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

    //Simulate Room to be updated
    newRoom = new GameInstance(roomId);
    newRoom.setGameId(gameId);

  }

  @Test
  public void doPostStartGameInstance() throws IOException {

    when(request.getParameter("gameInstance")).thenReturn(roomId);
    ArgumentCaptor<GameInstance> varArgs = ArgumentCaptor.forClass(GameInstance.class);

    //Return mock room 
    when(mockGameInstanceDao.getGameInstance(roomId)).thenReturn(newRoom);

    //Return mock game
    Game newGame = Game.builder()
          .title("")
          .creator("")
          .headQuestion(questionId)
          .questions(new ArrayList<Question>())
          .build();
    when(mockGameDao.getGame(gameId)).thenReturn(newGame);

    servletUnderTest.doPost(request, response);

    verify(mockGameDao).getGame(gameId);
    verify(mockGameInstanceDao).getGameInstance(roomId);
    verify(mockGameInstanceDao).updateGameInstance(varArgs.capture());
    Assert.assertEquals(true, newRoom.getIsActive());
    Assert.assertEquals(questionId, newRoom.getCurrentQuestion());

  }

  @Test
  public void noRoomId() throws IOException {

    when(request.getParameter("gameInstance")).thenReturn(null);

    servletUnderTest.doPost(request, response);
    String responseString = responseWriter.toString();

    verify(response).setStatus(500);
    Assert.assertThat(responseString, CoreMatchers.containsString("Room not specified"));

  }

  @Test
  public void noGameInstanceFound() throws IOException {

    when(request.getParameter("gameInstance")).thenReturn(roomId);
    when(mockGameInstanceDao.getGameInstance(roomId)).thenReturn(null);

    servletUnderTest.doPost(request, response);
    String responseString = responseWriter.toString();

    verify(response).setStatus(404);
    Assert.assertThat(responseString, CoreMatchers.containsString("Error, game instance not found."));

  }

  @Test
  public void noGameFound() throws IOException {

    when(request.getParameter("gameInstance")).thenReturn(roomId);
    when(mockGameInstanceDao.getGameInstance(roomId)).thenReturn(newRoom);

    //Return mock game
    when(mockGameDao.getGame(gameId)).thenReturn(null);

    servletUnderTest.doPost(request, response);
    String responseString = responseWriter.toString();

    verify(response).setStatus(404);
    Assert.assertThat(responseString, CoreMatchers.containsString("Error, game not found."));

  }
}
