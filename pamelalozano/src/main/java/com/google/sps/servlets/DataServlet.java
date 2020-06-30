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

import java.util.ArrayList; 
import java.util.Date; 
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    Comment prueba = new Comment("Title", "Msg", new Date(System.currentTimeMillis()));
    String commentJson = prueba.toJson();
    /*
      This array will just be needed once to create the comments, and then it 
      will not be needed since I'll obtain it from the get request of the comments
    */
    ArrayList<String> comments = new ArrayList<String>();  
        
    comments.add(commentJson);  

    response.setContentType("application/json;");
    response.getWriter().println(comments);
  }

}
