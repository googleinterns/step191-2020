package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;
import java.util.List; 
import java.util.logging.Logger;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firestore.v1.StructuredQuery.CollectionSelector;
import java.io.IOException;

import com.google.sps.data.Member;
import com.google.sps.data.Game;
import com.google.sps.data.Question;
import com.google.sps.data.Answer;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class DatabaseGameDao implements GameDao {

  private Firestore firestoreDb;
  private static Logger log = Logger.getLogger(DatabaseGameDao.class.getName());

  public DatabaseGameDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }
  
  @Override
  public boolean createNewGame(Game newGame) {
    // We create the game document
    DocumentReference newGameRef = firestoreDb.collection("games").document(); 
    newGameRef.set(newGame.gameData());

    try {
      CollectionReference questionRef = newGameRef.collection("questions");

      List<String> questionsIds = new ArrayList(Arrays.asList()); // the question id's that will work for the "next" and "previous" attributes on the questions
      List<Question> questions = newGame.questions(); // The questions list from the game class object

      String prevId = "null";
      // We get the unique id for the class from the database and we store it on the Question class
      // We also set the previous and the next question ids
      for (int i = 0; i < questions.size(); i++ ) {
        DocumentReference questionDoc = questionRef.document();
        Question currQ = questions.get(i);
        // Set the unique id to the current question object
        currQ.setId(questionDoc.getId());
        // Set the unique id for the previous question
        currQ.setPrevId(prevId);
        // If we are in the second question we set to the previous question the next id
        if ( i > 0 ) {
          Question prevQ = questions.get(i-1);
          prevQ.setNextId(currQ.getId());
          questions.set(i-1, prevQ);
        }
        // We update the question
        questions.set(i, currQ);

        // Our next prevId will be the current id
        prevId = currQ.getId();
      }
      // Now that every question object has its proper attributes for the id's we can store them back up to the database
      for (Question q : questions){
        DocumentReference questionDoc = questionRef.document(q.getId());
        questionDoc.set(q.questionData());

        CollectionReference answerRef = questionDoc.collection("answers"); // Creates a collection of answers
        for (Answer a : q.getAnswers()) {
          DocumentReference answerDoc = answerRef.document(); // Creates a document for the answer
          answerDoc.set(a); // Sets the attributes to the answer data on the database
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }

    return true;
  }
}
