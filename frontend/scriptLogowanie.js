let d = document;


async function sprobujZalogowac(tryAgain = 0){
    let login = d.querySelector("#login").value;
    let password = d.querySelector("#password").value;
    let response = await fetch(`http://localhost:8080/correctLogin?login=${login}&password=${password}`, {
        method: "GET"
    });
    if(!response.ok){
        if(tryAgain < 3){
            sprobujZalogowac(tryAgain+1);
        }

        return;
    }

    let cos = await response.text();

    if(cos == "true"){
        localStorage.setItem("user", login);
        window.location.replace("index.html");
    }
    else if(cos == "false"){
        alert("Niepoprawny login lub hasło");
    }
    else {
        alert("Wystąpił błąd przy logowaniu. Spróbuj ponownie później");
    }
}

d.querySelector("#zatwierdz").addEventListener("click", () => {
    sprobujZalogowac();
});