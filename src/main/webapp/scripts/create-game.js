
firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
      // The parameter fot the user id on
      document.getElementById("user-id").value = getUserId();
    }
  });