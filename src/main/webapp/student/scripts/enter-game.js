
'use strict';

const db = firebase.firestore();

function getGame(){
    const uid = document.getElementById("roomId").value;
    console.log(uid);
    db.collection("gameInstance").doc(uid).get().then(function(doc) {
        if (!doc.exists) {
            var element = document.getElementById("resultRoom");
            element.classList.toggle("notActive");
            setTimeout(function() {
                element.classList.toggle("notActive");
            }, 5000);
        } else {
            window.location.href = "/student/play-game.html?room="+uid;
        }
    })
}
