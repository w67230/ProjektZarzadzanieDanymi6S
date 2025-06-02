let d = document;

let poprawnyLogin = false;
let poprawneHaslo = false;


async function wyslij(){
    let login = ""+d.querySelector("#login").value;
    let haslo = ""+d.querySelector("#password").value;

    try{
        let response = await fetch(`http://localhost:8080/user?login=${login}&password=${haslo}`, {
            method: "post"
        });

        if(!response.ok){
            throw new Error(`Response status: ${response.status}`);
        }

        let cos = await response.text();
        console.log(cos);
        alert("Rejestracja przebiegła pomyślnie");
        localStorage.setItem("user", login);
        window.location.replace("index.html");
        
    }
    catch(error){
        alert("Wystąpił błąd. Spróbuj ponownie później.");
        console.log(error.message);
    }
}

async function sprawdzCzyLoginDostepny(tryAgain = 0){
    let login = d.querySelector("#login").value;
    let response = await fetch(`http://localhost:8080/user?login=${login}`, {
        method: "GET"
    });
    if(!response.ok){
        if(tryAgain < 6){
            sprawdzCzyLoginDostepny(tryAgain+1);
        }

        poprawnyLogin = false;
        return;
    }

    let cos = await response.text();

    if(cos == "true"){
        poprawnyLogin = false;
        d.querySelector("#login").style.backgroundColor="red";
        zablokujGuzik();
    }
    else if(cos == "false"){
        poprawnyLogin = true;
        d.querySelector("#login").style.backgroundColor="white";
        sprobujOdblokowacGuzik();
    }
    else {
        poprawnyLogin = false;
        alert("Wystąpił błąd przy sprawdzaniu poprawności loginu. Spróbuj ponownie później");
        zablokujGuzik();
    }
}

function sprobujOdblokowacGuzik(){
    if(poprawnyLogin && poprawneHaslo){
        d.querySelector("#zatwierdz").disabled = false;
    }
    else {
        zablokujGuzik();
    }
}

function zablokujGuzik(){
    d.querySelector("#zatwierdz").disabled = true;
}

function sprawdzHaslo(){
    let haslo = d.querySelector("#password").value;
    if(haslo.length < 5){
        d.querySelector("#password").style.backgroundColor="red";
        poprawneHaslo = false;
        zablokujGuzik();
    }
    else if(haslo != d.querySelector("#pHaslo").value){
        d.querySelector("#password").style.backgroundColor="white";
        if(d.querySelector("#pHaslo").value != ""){
            d.querySelector("#pHaslo").style.backgroundColor="red";
        }
        poprawneHaslo = false;
        zablokujGuzik();
    }
    else {
        d.querySelector("#password").style.backgroundColor="white";
        d.querySelector("#pHaslo").style.backgroundColor="white";

        poprawneHaslo = true;
        sprobujOdblokowacGuzik();
    }
}

d.querySelector("#login").addEventListener("change", () => {
    if(d.querySelector("#login").value.length > 4){
        sprawdzCzyLoginDostepny();
    }
    else {
        d.querySelector("#login").style.backgroundColor="red";
        zablokujGuzik();
    }
});

d.querySelector("#password").addEventListener("change", () => {
    sprawdzHaslo();
});

d.querySelector("#pHaslo").addEventListener("change", () => {
    sprawdzHaslo();
});

d.querySelector("#zatwierdz").addEventListener("click", () => {
    if(!d.querySelector("#zatwierdz").disabled){
        wyslij();
    }
})


