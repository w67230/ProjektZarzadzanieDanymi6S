let d = document;


async function czytaj(zasob){
    var data = await fetch(`http://localhost:8080/${zasob}`);
    var cos = await data.json();
    
    return cos;
}

let promiseOferty = czytaj("offers");
let promiseKsiazki = czytaj("books");
let promiseAutorzy = czytaj("authors");

let ksiazki = [];
let oferty = [];
let autorzy = [];

let sortowanie = null;

let table = d.createElement("table");
table.appendChild(stworzPierwszyWiersz());
d.querySelector("#content").appendChild(table);

let div = d.createElement("div");
d.querySelector("#szczegol").appendChild(div);

function wyswietlOferty(odCeny = 0, doCeny = 0, slowo = ""){
    let newTable = d.createElement("table");
    newTable.appendChild(stworzPierwszyWiersz());

    let kopia = oferty.filter(o => {
        let blOd = o['price'] >= odCeny;
        let blDo = doCeny <= 0 ? true : o['price'] <= doCeny;
        let blSlowo = ksiazki.filter(k => {
            return k['id'] == o['bookId'];
        }).at(0)['name'].toLowerCase().includes(slowo.toLowerCase());
        return blOd && blDo && blSlowo;
    });

    kopia.sort((a, b) => {
        switch(sortowanie) {
            case 1:
                return compareTwoStrings(a, b);
            case -1:
                return compareTwoStrings(b, a);
            case 2:
                return a['price'] - b['price'];
            case -2:
                return b['price'] - a['price'];
            case 3:
                return a['amount'] - b['amount'];
            case -3:
                return b['amount'] - a['amount'];
            case 0:
                return b['id'] - a['id'];
            default:
                return a['id'] - b['id'];
        }
    });

    kopia.forEach(element => {
        let tr = d.createElement("tr");

        let id = d.createElement("td");
        let nazwa = d.createElement("td");
        let cena = d.createElement("td");
        let ilosc = d.createElement("td");

        id.innerHTML=element['id'];
        nazwa.innerHTML=ksiazki.filter(value => value['id'] == element['bookId']).at(0)['name'];
        cena.innerHTML=element["price"];
        ilosc.innerHTML=element["amount"];

        tr.appendChild(id);
        tr.appendChild(nazwa);
        tr.appendChild(cena);
        tr.appendChild(ilosc);
        
        
        tr.addEventListener("click", () => {
            console.log(element);
            wyswietlSzczegoly(
                element
            );
        });
        newTable.appendChild(tr);
    });

    d.querySelector("#content").replaceChild(newTable, table);
    table = newTable;
}

function stworzPierwszyWiersz(){
    let row = d.createElement("tr");

    let id = d.createElement("th");
    let name = d.createElement("th");
    let price = d.createElement("th");
    let amount = d.createElement("th");

    let trSort = d.createElement("img");

    if(sortowanie == null){
        trSort.src = "gora_50.png";
    }
    else {
        trSort.src = sortowanie > 0 ? "gora_50.png" : "dol_50.png";
    }
    trSort.alt = "strzalka";
    trSort.style.width="14px";
    

    id.innerHTML=`#`;
    name.innerHTML=`Nazwa`;
    price.innerHTML=`Cena`;
    amount.innerHTML=`Ilość`;

    switch(sortowanie){
        case 1:
            name.appendChild(trSort);
            break;
        case -1:
            name.appendChild(trSort);
            break;
        case 2:
            price.appendChild(trSort);
            break;
        case -2:
            price.appendChild(trSort);
            break;
        case 3:
            amount.appendChild(trSort);
            break;
        case -3:
            amount.appendChild(trSort);
            break;
        default:
            id.appendChild(trSort);
    }



    id.addEventListener("click", () => {
        sortowanie = sortowanie == null ? 0 : null;
        wyswietlFiltrowaneCeny();
    });

    name.addEventListener("click", () => {
        sortowanie = sortowanie == 1 ? -1 : 1;
        wyswietlFiltrowaneCeny();
    });

    price.addEventListener("click", () => {
        sortowanie = sortowanie == 2 ? -2 : 2;
        wyswietlFiltrowaneCeny();
    });

    amount.addEventListener("click", () => {
        sortowanie = sortowanie == 3 ? -3 : 3;
        wyswietlFiltrowaneCeny();
    });

    row.appendChild(id);
    row.appendChild(name);
    row.appendChild(price);
    row.appendChild(amount);

    return row;
}


function wyswietlSzczegoly(element){
    let lewaStrona = d.querySelector("#szczegol");
    let clickedBook = ksiazki.filter(el => el['id'] == element['bookId']).at(0);
    let clickedAuthor = autorzy.filter(el => el['id'] == clickedBook['authorId']).at(0);

    let newDiv = d.createElement("div");

    let tytul = d.createElement("h1");
    tytul.innerHTML=clickedBook['name'];

    let autor = d.createElement("p");
    let smierc = clickedAuthor['deathDate'] == null ? "" : " - " + clickedAuthor['deathDate'].substring(0, 10);
    autor.innerHTML="Autor: " + clickedAuthor['firstName'] + " " + 
        clickedAuthor['lastName'] + " " + "(" + 
        clickedAuthor['birthDate'].substring(0, 10) + smierc + ")";

    let cena = d.createElement("p");
    cena.innerHTML="Cena: " + element['price'] + " zł";

    let zakup;
    if(localStorage.getItem("user") != null){
        zakup = kupKsiazke(element);
    }
    else {
        zakup = d.createElement("h3");
        if(element['amount'] > 0){
            zakup.innerHTML="Zaloguj się aby kupić tę książkę!";
        }
        else {
            zakup.innerHTML="Produkt niedostępny - nie można go obecnie zakupić.";
        }
    }

    
    newDiv.appendChild(tytul);
    newDiv.appendChild(autor);
    newDiv.appendChild(cena);
    newDiv.appendChild(zakup);
    

    lewaStrona.replaceChild(newDiv, div);
    div = newDiv;
}

function kupKsiazke(element){
    let div = d.createElement("div");

    let kup = d.createElement("button");
    let ilosc = d.createElement("input");
    let cena = d.createElement("p");
    let ileJest = parseInt(element['amount']);
    let wybranaOferta = element['id'];

    ilosc.type="number";
    ilosc.min=ileJest > 1 ? 1 : 0;
    ilosc.value=ilosc.min;
    ilosc.max= ileJest;
    ilosc.id = "ileKupujesz";

    kup.innerHTML="Kup";

    cena.innerHTML="Do zapłaty: " + (ilosc.value*element['price']).toFixed(2) + " zł";
    ilosc.addEventListener("change", () => {
        if(parseInt(ilosc.value) < parseInt(ilosc.min)){
            ilosc.value = ilosc.min;
        }
        else if(parseInt(ilosc.value) > parseInt(ilosc.max)){
            ilosc.value = ilosc.max;
        }
        cena.innerHTML="Do zapłaty: " + (ilosc.value*element['price']).toFixed(2) + " zł";
    });

    if(parseInt(ilosc.value) > 0){
        kup.addEventListener("click", () => {
            dokonajTransakcji(wybranaOferta);
        });
    }
    else {
        kup.disabled = true;
    }


    div.appendChild(ilosc);
    div.appendChild(kup);
    div.appendChild(cena);

    return div;
}

async function dokonajTransakcji(wybranaOferta){
    let ilosc = d.querySelector("#ileKupujesz").value;
    console.log(wybranaOferta);
    if(ilosc == undefined || ilosc == null || ilosc < 1 || wybranaOferta == undefined || wybranaOferta == null){
        alert("Coś poszło nie tak. Spróbuj odświeżyć stronę");
        return;
    }
    else if(localStorage.getItem("user") == null){
        alert("Sesja wygasła. Musisz się zalogować, aby kontynuować");
        window.location.replace("logowanie.html");
        return;
    }

    let user = localStorage.getItem("user");

    try{
        let response = await fetch(`http://localhost:8080/order?userLogin=${user}&offerId=${wybranaOferta}&amount=${ilosc}`, {
            method: "post"
        });

        if(!response.ok){
            throw new Error(`Response status: ${response.status}`);
        }

        let cos = await response.text();
        console.log(cos);
        alert("Zamówienie zostało złożone!");
        window.location.reload();
        
    }
    catch(error){
        alert("Wystąpił błąd. Spróbuj ponownie później");
        console.log(error.message);
    }
}

function compareTwoStrings(a, b){
    let aName = ksiazki.filter(k => k['id'] == a['bookId']).at(0)['name'];
    let bName = ksiazki.filter(k => k['id'] == b['bookId']).at(0)['name'];
    return String(aName).localeCompare(String(bName));
}

promiseOferty.then(offers => {
    promiseKsiazki.then(books => {
        promiseAutorzy.then(authors => {
            books.forEach(bElement => {
                ksiazki.push(bElement);
            });
            //console.log(ksiazki);

            offers.forEach(element => {
                oferty.push(element);
            });
            //console.log(oferty);

            authors.forEach(element => {
                autorzy.push(element);
            });
            //console.log(autorzy);

            wyswietlOferty();
        });
    });
});

function wyswietlFiltrowaneCeny(){
    let slowo = d.querySelector("#filtrText").value;
    let odCe = d.querySelector("#filtrOdCeny").value;
    let doCe = d.querySelector("#filtrDoCeny").value;

    wyswietlOferty(odCe, doCe, slowo);
}

d.querySelector("#filtrText").addEventListener("change", () => {
    wyswietlFiltrowaneCeny();
});

d.querySelector("#filtrOdCeny").addEventListener("change", () => {
    wyswietlFiltrowaneCeny();
});

d.querySelector("#filtrDoCeny").addEventListener("change", () => {
    wyswietlFiltrowaneCeny();
});

let menu = d.querySelector("#menu");

if(localStorage.getItem("user") == null){
    let logowanie = d.createElement("button");
    let rejestracja = d.createElement("button");

    logowanie.innerHTML="Zaloguj się";
    rejestracja.innerHTML="Zarejestruj się";

    logowanie.addEventListener("click", () => {
        window.location.replace("logowanie.html");
    });

    rejestracja.addEventListener("click", () => {
        window.location.replace("rejestracja.html");
    });

    menu.appendChild(logowanie);
    menu.appendChild(rejestracja);
}
else {
    let wylogowanie = d.createElement("button");
    let haha = d.createElement("h3");
    let zamowienia = d.createElement("a");

    wylogowanie.innerHTML="Wyloguj się";
    zamowienia.innerHTML="Twoje zamówienia";
    haha.innerHTML="Zalogowano jako: " + localStorage.getItem("user");

    wylogowanie.addEventListener("click", () => {
        localStorage.removeItem("user");
        window.location.replace("index.html");
    });

    zamowienia.href="zamowienia.html";

    menu.appendChild(haha);
    menu.appendChild(zamowienia);
    menu.appendChild(wylogowanie);
}
