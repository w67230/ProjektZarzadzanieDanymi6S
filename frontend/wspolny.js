let doc = document;

if(localStorage.getItem("user") != null){
    let parent = doc.querySelector("#formularz").parentNode;
    parent.removeChild(doc.querySelector("#formularz"));

    let info = doc.createElement("h1");
    let info2 = doc.createElement("h2");
    let wyloguj = doc.createElement("button");

    info.innerHTML="Jesteś zalogowany jako: " + localStorage.getItem("user");
    info2.innerHTML="Wyloguj się, aby kontynuować";
    wyloguj.innerHTML="Wyloguj się";

    wyloguj.addEventListener("click", () => {
        localStorage.removeItem("user");
        window.location.reload();
    });

    parent.appendChild(info);
    parent.appendChild(info2);
    parent.appendChild(wyloguj);
}