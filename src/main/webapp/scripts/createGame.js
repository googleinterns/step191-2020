
firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
      // The parameter fot the user id on
      document.getElementById("user-id").value = getUserId();
    }
  });

function hideAllExcept(ref) {
  size = document.getElementById("questionsSection").children.length -1;
  for(i = 0; i <  size; i++) {
    var currentQuestion = document.getElementById("question" + i);
    var currentSelector = document.getElementById("jsSelectQ" + i);
    if(ref == ("question" + i)) {
      currentQuestion.removeAttribute("hidden");
      currentSelector.className = "mdl-navigation__link mdl-navigation__link--current";
    } else { 
      currentQuestion.setAttribute("hidden", true);
      currentSelector.className = "mdl-navigation__link";
    }
  }
}
