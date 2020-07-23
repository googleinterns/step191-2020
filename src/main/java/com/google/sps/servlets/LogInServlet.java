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

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.User;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/login")
public class LogInServlet extends HttpServlet {

  // Reference to Firestore database
  private Firestore firestoreDb;

  // Reference to Realtime database
  private DatabaseReference realtimeDb;
  private FirebaseAuth auth;
  private String email;
  

  private FirebaseOptions options;
  @Override
  public void init() {

  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
  }
  @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws  IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String uid = request.getParameter("uid");
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
    
    User user = new User(username, email, uid);
	}	

}
