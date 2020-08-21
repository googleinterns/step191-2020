package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.DocumentSnapshot;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.sps.data.Answer;
import com.google.sps.data.Game;
import com.google.sps.data.GameInstance;
import com.google.sps.data.Question;

public class DatabaseGameInstanceDao implements GameInstanceDao {

  private FirebaseAuth firebaseAuth;
  private Firestore firestoreDb;

  public DatabaseGameInstanceDao(Firestore firestoreDb, FirebaseAuth firebaseAuth) {
    this.firestoreDb = firestoreDb;
    this.firebaseAuth = firebaseAuth;
  }

  @Override
  public String createNewGameInstance(String idToken, String gameId, Game game) {
    DocumentReference newGameInstanceRef = firestoreDb.collection("gameInstance").document();

    FirebaseToken decodedToken = null;
    String userId = null;

    try {
      decodedToken = firebaseAuth.verifyIdToken(idToken);
      userId = decodedToken.getUid();
    } catch (FirebaseAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // I'm gonna do it manually for now

    // GameInstance newGameInstance = new GameInstance();
    // newGameInstance.setCreator(userId);
    // newGameInstance.setGameId(gameId);
    // newGameInstance.setNumberOfMembers(0);
    // newGameInstance.setId(newGameInstanceRef.getId());

    Map<String, Object> gameInstanceData = new HashMap<String, Object>();
    gameInstanceData.put("creator", userId);
    gameInstanceData.put("currentQuestion", null);
    gameInstanceData.put("currentQuestionActive", false);
    gameInstanceData.put("gameId", gameId);
    gameInstanceData.put("id", newGameInstanceRef.getId());
    gameInstanceData.put("isActive", false);
    gameInstanceData.put("numberOfMembers", 0);

    // Post to db
    newGameInstanceRef.set(gameInstanceData);

    // Now post 
    for (Question question: game.questions()) {
      DocumentReference questionDocRef = newGameInstanceRef.collection("questions").document(question.getId());
      
      Map<String, Object> questionData = new HashMap<String, Object>();
      questionData.put("numberAnswered", 0);
      questionData.put("numberCorrect", 0);
      questionData.put("numberWrong", 0);
      questionData.put("title", question.getTitle());

      questionDocRef.set(questionData);

      for (Answer answer: question.getAnswers()) {
        DocumentReference answerDocRef = questionDocRef.collection("answers").document(answer.getId());
        
        Map<String, Object> answerData = new HashMap<String, Object>();
        answerData.put("correct", answer.isCorrect());
        answerData.put("numberAnswers", 0);
        answerData.put("title", answer.getTitle());

        answerDocRef.set(answerData);
      }

    }

    // Update the User's entry with game he just started
    firestoreDb.collection("users").document(userId).update("activeGameInstanceId", newGameInstanceRef.getId());

    return newGameInstanceRef.getId();
  }


  @Override
  public GameInstance getGameInstance(String gameInstanceId) {
    DocumentReference docRef = firestoreDb.collection("gameInstance").document(gameInstanceId);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    GameInstance gameInstance = new GameInstance();
    try {
      DocumentSnapshot document = future.get();
      gameInstance = document.toObject(GameInstance.class);

      // Id is not a field in the db document so we add it manually
      gameInstance.setId(document.getId());

    } catch (Exception e) {
      System.out.println(e);
    }
    return gameInstance;
  }

  @Override
  public void updateGameInstance(GameInstance update) {
    firestoreDb.collection("gameInstance").document(update.getId()).set(update, SetOptions.merge());
  }

  @Override
  public boolean getAnswer(String gameInstance, String student) {
    // To get current question
    GameInstance actualInstance = this.getGameInstance(gameInstance);
    DocumentReference gameInstanceDocRef = firestoreDb.collection("gameInstance").document(gameInstance);
    DocumentReference answerInStudentDocRef = gameInstanceDocRef.collection("students").document(student)
        .collection("questions").document(actualInstance.getCurrentQuestion());
    ApiFuture<DocumentSnapshot> questionAnswerFuture = answerInStudentDocRef.get();
    try {
      DocumentSnapshot document = questionAnswerFuture.get();
      return Boolean.parseBoolean(document.get("correct").toString());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return false;
  }

}
