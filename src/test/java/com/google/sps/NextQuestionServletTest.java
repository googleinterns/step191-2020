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

import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.daos.GameInstanceDao;
import com.google.sps.data.GameInstance;
import com.google.sps.daos.GameDao;

import com.google.sps.servlets.NextQuestionServlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.runners.JUnit4;
import org.hamcrest.CoreMatchers;

@RunWith(JUnit4.class)
public final class NextQuestionServletTest {

  private GameInstanceDao mockGameInstanceDao = mock(GameInstanceDao.class);
  private ServletContext mockServletContext = mock(ServletContext.class);
  private GameDao mockGameDao = mock(GameDao.class);
  private HttpServletRequest request = mock(HttpServletRequest.class);
  private HttpServletResponse response = mock(HttpServletResponse.class);
  private String roomId = "aSpX3cmZa5PB994uEoW2";
  private String gameId = "iBxba1vsaWT1SIqxeonJ";
  private String questionId = "NWUzaBz7SJEiKvQEwAkt";
  private NextQuestionServlet servletUnderTest;
  private GameInstance newRoom;
  private StringWriter responseWriter;


  @Before
  public void setUp() throws Exception {
    servletUnderTest = new NextQuestionServlet() {
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
    newRoom.setCurrentQuestion(questionId);
  }

  @Test
  public void doPostNextQuestion() throws IOException {

    String nextQuestionId = "mU0dPEQtV0AKwrotOAYk";
    when(request.getParameter("gameInstance")).thenReturn(roomId);
    ArgumentCaptor<GameInstance> varArgs = ArgumentCaptor.forClass(GameInstance.class);

    //Return mock room 
    when(mockGameInstanceDao.getGameInstance(roomId)).thenReturn(newRoom);

    when(mockGameDao.getQuestionId("nextQuestion", gameId, questionId)).thenReturn(nextQuestionId);

    servletUnderTest.doPost(request, response);

    verify(mockGameDao).getQuestionId("nextQuestion", gameId, questionId);
    verify(mockGameInstanceDao).getGameInstance(roomId);
    verify(mockGameInstanceDao).updateGameInstance(varArgs.capture());
    Assert.assertEquals(nextQuestionId, newRoom.getCurrentQuestion());

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
  public void noMoreQuestions() throws IOException {
    String nextQuestionId = "mU0dPEQtV0AKwrotOAYk";
    when(request.getParameter("gameInstance")).thenReturn(roomId);

    newRoom.setCurrentQuestion(nextQuestionId);
    when(mockGameInstanceDao.getGameInstance(roomId)).thenReturn(newRoom);
    when(mockGameDao.getQuestionId("nextQuestion", gameId, nextQuestionId)).thenReturn("");

    servletUnderTest.doPost(request, response);
    String responseString = responseWriter.toString();

    verify(response).setStatus(404);
    Assert.assertThat(responseString, CoreMatchers.containsString("Error, there's no more questions"));

  }
}
