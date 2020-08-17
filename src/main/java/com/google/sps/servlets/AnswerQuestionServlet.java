package com.google.sps.servlets;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.sps.data.GameInstance;

import com.google.sps.daos.GameInstanceDao;
import com.google.sps.data.GameInstance;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/answer")
public class AnswerQuestionServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String gameInstance = request.getParameter("gameInstance");

    if(gameInstance==null || gameInstance.isEmpty()){
        response.setStatus(500);
        response.getWriter().println("Room not specified");
        return;
    }

    String token = request.getParameter("student");
    if(token==null || token.isEmpty()){
        response.setStatus(500);
        response.getWriter().println("Room not specified");
        return;
    }

    //Get student id 
    String student = getUserId(token);


    GameInstanceDao dao = (GameInstanceDao) this.getServletContext().getAttribute("gameInstanceDao");
    //ToDo: Check if !currentQuestionActive
    boolean isAnswerCorrect = dao.getAnswer(gameInstance, student);  

    Map<String, Object> answerJson = new HashMap<>();
    answerJson.put("correct", isAnswerCorrect);
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(answerJson));

  }  

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);

    String userId = getUserId(jsonObj.get("idToken").getAsString());

    String gameInstanceId = jsonObj.get("gameInstanceId").getAsString();

    String gameId = jsonObj.get("gameId").getAsString();

    String questionId = jsonObj.get("questionId").getAsString();

    String questionTitle = jsonObj.get("questionTitle").getAsString();

    String answerId = jsonObj.get("answerId").getAsString();

    String answerTitle = jsonObj.get("answerTitle").getAsString();

    Firestore firestoreDb = (Firestore) this.getServletContext().getAttribute("firestoreDb");

    DocumentReference gameInstanceDocRef = firestoreDb.collection("gameInstance").document(gameInstanceId);

    boolean isQuestionActive = isQuestionActive(questionId, gameInstanceDocRef);
    
    //ToDo: Check is currentQuestionActive (if students can answer)
    if (!isQuestionActive) {
      return;
    }

    DocumentReference questionDocRef = firestoreDb.collection("games").document(gameId).collection("questions").document(questionId);

    boolean isAnswerCorrect;

    //If there was no answer it's wrong
    if(answerId == null || answerId.isEmpty()){
        System.out.println("STUDENT DIDN'T ANSWER");
        isAnswerCorrect = false;
    } else {
        String correctAnswerId = getCorrectAnswer(questionId, questionDocRef);
        isAnswerCorrect = answerId.equals(correctAnswerId);
    }

    DocumentReference answerInStudentDocRef = gameInstanceDocRef.collection("students").document(userId).collection("questions").document(questionId);

    // Store the answer in the user's profile if it does not exist
    boolean answerNotExists = registerAnswerInStudentAnswers(answerInStudentDocRef, isAnswerCorrect, questionTitle, answerTitle);

    if (answerNotExists) {
      if (isAnswerCorrect) {
        System.out.println("The answer is correct, adding points");
        updateStudentStats(isAnswerCorrect, userId, gameInstanceDocRef, firestoreDb);
      } else {
        System.out.println("The answer is incorrect");
      }

      DocumentReference questionInGameInstanceDocRef = gameInstanceDocRef.collection("questions").document(questionId);

      // register the the question's general answer statistics
      updateGeneralQuestionStats(questionInGameInstanceDocRef, isAnswerCorrect, firestoreDb);
      if(answerId != null && !answerId.isEmpty()){
        // update the answer's statistics in the answer document in the question document
        DocumentReference answerDocRef = questionInGameInstanceDocRef.collection("answers").document(answerId);
        updateAnswerInQuestionStats(answerDocRef, isAnswerCorrect, firestoreDb);
      }

      System.out.println("running supposedly");

    } else {
      System.out.println("Answer had already been answered");
    }

   

    
    

    // GameInstanceDao dao = (GameInstanceDao)
    // this.getServletContext().getAttribute("gameInstanceDao");

    // dao.createNewGameInstance(jsonObj.get("idToken").getAsString(),
    // jsonObj.get("gameId").getAsString());
  }

  private String getUserId(String idToken) {
    FirebaseAuth firebaseAuth = (FirebaseAuth) this.getServletContext().getAttribute("firebaseAuth");

    FirebaseToken decodedToken = null;
    String userId = null;

    try {
      decodedToken = firebaseAuth.verifyIdToken(idToken);
      userId = decodedToken.getUid();
    } catch (FirebaseAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return userId;
  }

  private boolean isQuestionActive(String questionId, DocumentReference gameInstanceDocRef) {

    ApiFuture<DocumentSnapshot> future = gameInstanceDocRef.get();

    DocumentSnapshot document;
    try {
      document = future.get();

      Map<String, Object> data = document.getData();

      if (data.get("currentQuestion").equals(questionId)) {
        System.out.println("Answer valid");
        return true;
      }

    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println("Answer invalid");

    return false;
  }

  // Right now it only takes one single correct answer
  private String getCorrectAnswer(String questionId, DocumentReference questionDocRef) {

    String correctAnswerId = null;

    ApiFuture<QuerySnapshot> future = questionDocRef.collection("answers").whereEqualTo("correct", true).get();

    List<QueryDocumentSnapshot> documents;
    try {
      documents = future.get().getDocuments();
      for (DocumentSnapshot document : documents) {
        correctAnswerId = document.getId();
      }
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    

    return correctAnswerId;
  }

  private void updateStudentStats(boolean isAnswerCorrect, String userId, DocumentReference gameInstanceDocRef, Firestore firestoreDb) {
    DocumentReference userInGameInstanceDocRef = gameInstanceDocRef.collection("students").document(userId);

    firestoreDb.runTransaction(transaction -> {
      DocumentSnapshot snapshot = transaction.get(userInGameInstanceDocRef).get();

      long oldNumberAnswered = snapshot.getLong("numberAnswered");
      transaction.update(userInGameInstanceDocRef, "numberAnswered", oldNumberAnswered + 1);

      if (isAnswerCorrect) {

        long oldNumberCorrect = snapshot.getLong("numberCorrect");
        transaction.update(userInGameInstanceDocRef, "numberCorrect", oldNumberCorrect + 1);

        long oldPoints = snapshot.getLong("points");
        transaction.update(userInGameInstanceDocRef, "points", oldPoints + 10);
      } else {
        long oldNumberWrong = snapshot.getLong("numberWrong");
        transaction.update(userInGameInstanceDocRef, "numberWrong", oldNumberWrong + 1);
      }
      
      return null;
    });

  }

  // Returns true if answer had not been aswered yet, else returns false
  private boolean registerAnswerInStudentAnswers(DocumentReference questionDocRef, boolean isCorrect, String questionTitle, String chosenAnswerTitle) {
    ApiFuture<DocumentSnapshot> questionAnswerFuture = questionDocRef.get();

    try {
      DocumentSnapshot document = questionAnswerFuture.get();
      if (!document.exists()) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("correct", isCorrect);
        docData.put("chosen", chosenAnswerTitle);
        docData.put("title", questionTitle);
        questionDocRef.set(docData);
        return true;
      } 
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return false;
  }

  private void updateGeneralQuestionStats(DocumentReference questionDocRef, boolean isAnswerCorrect, Firestore firestoreDb) {
    firestoreDb.runTransaction(transaction -> {
      // retrieve document and increment population field
      DocumentSnapshot snapshot = transaction.get(questionDocRef).get();
      long oldNumberOfAnswers = snapshot.getLong("numberAnswered");
      transaction.update(questionDocRef, "numberAnswered", oldNumberOfAnswers + 1);

      if (isAnswerCorrect) {
        long oldNumberOfCorrectAnswers = snapshot.getLong("numberCorrect");
        transaction.update(questionDocRef, "numberCorrect", oldNumberOfCorrectAnswers + 1);
      } else {
        long oldNumberOfWrongAnswers = snapshot.getLong("numberWrong");
        transaction.update(questionDocRef, "numberWrong", oldNumberOfWrongAnswers + 1);
      }
      
      return null;
    });
  }

  private void updateAnswerInQuestionStats(DocumentReference answerDocRef, boolean isAnswerCorrect, Firestore firestoreDb) {
    firestoreDb.runTransaction(transaction -> {
      // retrieve document and increment population field
      DocumentSnapshot snapshot = transaction.get(answerDocRef).get();
      long oldNumberAnswers = snapshot.getLong("numberAnswers");
      transaction.update(answerDocRef, "numberAnswers", oldNumberAnswers + 1);
      return null;
    });
  }

}