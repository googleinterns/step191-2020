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

package com.google.sps.servlets;

import java.io.IOException;
import com.google.common.collect.ImmutableList; 
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final ImmutableList<String> FUN_FACTS = ImmutableList.of(
    "I develop Nintendo GameBoy Advance ROMs in my spare time",
    "I was a YouTube star back in the days <a href='https://youtube.com/thefredo1000'>Check out my channel</a>",
    "I am a huge Star Wars fan",
    "I love Weezer, they are my favorite band",
    "I can play the bass (Still a rookie)",
    "I have three dogs!");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String fact = FUN_FACTS.get((int) (Math.random() * FUN_FACTS.size()));

    response.setContentType("text/html;");
    response.getWriter().println(fact);
  }
}
