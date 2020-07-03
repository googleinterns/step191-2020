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

import java.util.Enumeration;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList; 
import java.util.List; 
import java.util.Date; 
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//ToDo: Separate doPost and doGet in different files

/** Servlet that returns some example content.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(3);

    // If this servlet is passed a cursor parameter, let's use it.
     String startCursor = request.getParameter("cursor");
     String next = request.getParameter("next");
     String back = request.getParameter("back");
     String cursorString = "";

    if (startCursor != null && next != null) {
      fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
    } else if (startCursor != null && back != null) {
      fetchOptions.endCursor(Cursor.fromWebSafeString(startCursor));
    }

    Query q = new Query("Comment").addSort("date", SortDirection.DESCENDING);
    PreparedQuery pq = datastore.prepare(q);

    QueryResultList<Entity> results;
    try {
      results = pq.asQueryResultList(fetchOptions);
    } catch (IllegalArgumentException e) {
      // IllegalArgumentException happens when an invalid cursor is used.
      // A user could have manually entered a bad cursor in the URL or there
      // may have been an internal implementation detail change in App Engine.
      // Redirect to the page without the cursor parameter to show something
      // rather than an error.
      System.out.println(e);
      response.sendRedirect("/#comments");
      return;
    }
   
    if (next != null) {
      cursorString = results.getCursor().toWebSafeString();
    } else if (back != null) {
      cursorString = results.getCursor().toWebSafeString();
    }

    ArrayList<Comment> comments = new ArrayList<>();  

    //Query of entities to JSON
    for (Entity entity : results) {
        
        //Returns comment converted to json (method in Comment object)
        Comment comment = entityToComment(entity);

        comments.add(comment);
    }

    CommentsPage commentsPage = new CommentsPage(comments, cursorString);
    String res = commentsPage.toJson();

    response.setContentType("application/json;");
    response.getWriter().println(res);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
     
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 
    // Get the input from the form.
    Comment newComment = getComment(request);

    if (!validateComment(newComment)){
      response.setContentType("text/html");
      response.getWriter().println("Subject and comment are required");
      return;
    }

    //Storing in database
    Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("subject", newComment.getSubject());
        commentEntity.setProperty("msg", newComment.getMessage());
        commentEntity.setProperty("date", newComment.getDate());

    datastore.put(commentEntity);

    // Redirect back to the page in the comments section.
    //ToDo: Successful comment post alert
    response.sendRedirect("/#comments");
  }

  /** Returns the entity as a String*/
  private Comment entityToComment(Entity entity) {
      
    // Gets the properties to convert to json
    String subject = (String) entity.getProperty("subject");
    String msg = (String) entity.getProperty("msg");
    Date date = (Date) entity.getProperty("date");

    Comment newComment = new Comment(subject, msg, date);
//    String commentJson = newComment.toJson();

    return newComment;
  }

  /** Returns the comment posted or a comment with subject error if something is missing. */
  private Comment getComment(HttpServletRequest request) {
    // Get the input from the form.
    String subject = request.getParameter("subject");
    String msg = request.getParameter("msg");

    Comment newComment = new Comment(subject, msg, new Date(System.currentTimeMillis()));

    return newComment;
  }

  /*Checks if an input is missing from comment*/
  private boolean validateComment(Comment comment) {

    String subject = comment.getSubject();
    String msg = comment.getMessage();

    if (subject == null || subject.isEmpty() || msg == null || msg.isEmpty()) {
      return false;
    }

    return true;
  }
}
