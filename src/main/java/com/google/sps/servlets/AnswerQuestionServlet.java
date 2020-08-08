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

import java.io.IOException;
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
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JsonObject jsonObj = new Gson().fromJson(request.getReader(), JsonObject.class);

    String userId = getUserId(jsonObj.get("idToken").getAsString());

    String gameInstanceId = jsonObj.get("gameInstanceId").getAsString();

    String gameId = jsonObj.get("gameId").getAsString();

    String questionId = jsonObj.get("questionId").getAsString();

    String answerId = jsonObj.get("answerId").getAsString();

    Firestore firestoreDb = (Firestore) this.getServletContext().getAttribute("firestoreDb");

    DocumentReference gameInstanceDocRef = firestoreDb.collection("gameInstance").document(gameInstanceId);

    boolean isQuestionActive = isQuestionActive(questionId, gameInstanceDocRef);

    if (!isQuestionActive) {
      return;
    }

    DocumentReference questionDocRef = firestoreDb.collection("games").document(gameId).collection("questions")
        .document(questionId);

    String correctAnswerId = getCorrectAnswer(questionId, questionDocRef);

    if (answerId.equals(correctAnswerId)) {
      System.out.println("The answer is correct");

      addPoints(userId, gameInstanceDocRef, firestoreDb);
    } else {
      System.out.println("The answer is incorrect");
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

  private void addPoints(String userId, DocumentReference gameInstanceDocRef, Firestore firestoreDb) {
    DocumentReference userInGameInstanceDocRef = gameInstanceDocRef.collection("students").document(userId);

    firestoreDb.runTransaction(transaction -> {
      // retrieve document and increment population field
      DocumentSnapshot snapshot = transaction.get(userInGameInstanceDocRef).get();
      long oldPoints = snapshot.getLong("points");
      transaction.update(userInGameInstanceDocRef, "points", oldPoints + 10);
      return null;
    });

  }

}