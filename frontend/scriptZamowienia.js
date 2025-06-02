let d = document;

if(localStorage.getItem("user") == null){
    window.location.replace("logowanie.html");
}

async function czytaj(zasob){
    var data = await fetch(`http://localhost:8080/${zasob}`);
    var cos = await data.json();
    
    return cos;
}

let promiseOferty = czytaj("offers");
let promiseKsiazki = czytaj("books");
let promiseZamow = czytaj("orders");

let ksiazki = [];
let oferty = [];
let zamow = [];

let sortowanie = null;
let table = d.createElement("table");
d.querySelector("#wysZamowienia").appendChild(table);

function wyswietlZamowienia(){
    if(zamow.filter(z => z['userLogin'] == localStorage.getItem("user")).length < 1){
        return;
    }
    else{
        d.querySelector("#nic").innerHTML="";
    }
    let newTable = d.createElement("table");
    newTable.appendChild(stworzPierwszyWiersz());

    let counter = 1;

    // sortowanie wedlug nazwy nie dziala nie wiem czemu, wszystkiego probowalem ale te stringi nie chca dzialac
    zamow.sort((a, b) => {
        switch(sortowanie) {
            case 1:
                return compareTwoStrings(a, b);
            case -1:
                return compareTwoStrings(b, a);
            case 2:
                return compareTwoPrices(a, b);
            case -2:
                return compareTwoPrices(b, a);
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

    zamow.forEach(element => {
        if(element['userLogin'] == localStorage.getItem("user")){
            let tr = d.createElement("tr");

            let id = d.createElement("td");
            let nazwa = d.createElement("td");
            let cena = d.createElement("td");
            let ilosc = d.createElement("td");

            let wybranaOff = oferty.filter(oferta => oferta['id'] == element['offerId']).at(0);

            id.innerHTML=counter;
            counter++;
            nazwa.innerHTML=ksiazki.filter(value => value['id'] == wybranaOff['bookId']).at(0)['name'];
            cena.innerHTML=wybranaOff["price"];
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
        }
    });

    d.querySelector("#wysZamowienia").replaceChild(newTable, table);
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
    price.innerHTML=`Zapłacono`;
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
        wyswietlZamowienia();
    });

    name.addEventListener("click", () => {
        sortowanie = sortowanie == 1 ? -1 : 1;
        wyswietlZamowienia();
    });

    price.addEventListener("click", () => {
        sortowanie = sortowanie == 2 ? -2 : 2;
        wyswietlZamowienia();
    });

    amount.addEventListener("click", () => {
        sortowanie = sortowanie == 3 ? -3 : 3;
        wyswietlZamowienia();
    });

    row.appendChild(id);
    row.appendChild(name);
    row.appendChild(price);
    row.appendChild(amount);

    return row;
}

function compareTwoStrings(a, b){
    let aName = ksiazki.filter(k => {
        return k['id'] == oferty.filter(o => {
            return o['id'] == a['offerId'];
        }).at(0)['bookId'];
    }).at(0)['name'];
    let bName = ksiazki.filter(k => {
        return k['id'] == oferty.filter(o => {
            return o['id'] == b['offerId'];
        }).at(0)['bookId'];
    }).at(0)['name'];
    return String(aName).localeCompare(String(bName));
}

function compareTwoPrices(a, b){
    let aPrice = oferty.filter(o => {
        return o['id'] == a['offerId'];
    }).at(0)['price'] * a['amount'];
    let bPrice = oferty.filter(o => {
        return o['id'] == b['offerId'];
    }).at(0)['price'] * b['amount'];

    return aPrice - bPrice;
}


promiseOferty.then(offers => {
    promiseKsiazki.then(books => {
        promiseZamow.then(orders => {
            books.forEach(bElement => {
                ksiazki.push(bElement);
            });
            console.log(ksiazki);

            offers.forEach(element => {
                oferty.push(element);
            });
            console.log(oferty);

            orders.forEach(element => {
                zamow.push(element);
            });
            console.log(orders);

            wyswietlZamowienia();
        });
    });
});