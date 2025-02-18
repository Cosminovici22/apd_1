# Problema readers-writers - Analiza a performantei

## Solutiile implementate

1. Solutia readers-preferrence

Aceasta solutie prioritizeaza cititorii unei zone de memorie prin constrangerea
accesului scriitorilor la respectiva zona de memorie pana nu mai raman cititori.
Metoda nu este justa din punct de vedere al distributiei accesului la cei doi
operatori. Un scriitor este nevoit sa astepte pana la epuizarea cititorilor
aparuti inaintea crearii scriitorului respectiv si care continua sa apara in
timp ce un alt cititor nu si-a terminat treaba. Acesta abordare cauzeaza efectul
numit writers' starvation si duce la actualizarea inceata a datelor in buffer.

2. Solutia writers-preferrence

Aceasta solutie ofera acces unui scriitor la resursa comuna cat de curand
posibil, deci dupa ce eventualul cititor sau scriitor curent si-a realizat
activitatea. Aceasta meotda poate provoca, la randul ei, o insuficienta a
citirilor din resursa comuna de catre cititori, deoarece cititorii unei zone
de memorie trebuie sa astepta ca toti scriitorii in asteptare sa isi termine
scrierile (readers' starvation). Acesta solutie este implementata in doua
moduri, pe de-o parte folosind semafoare pe post de mutex-uri si pe de alta
parte folosind monitorul obiectelor din Java.

## Performanta

Variatia de performanta dintre cele trei implementari este neglijabila.
Diferenta reala intre abordari este data de ordinea efectuarii operatiilor de
scriere si citire, ceea ce este relevant in cazuri reale. Perfomranta ridicata a
implmentarilor se datoreaza sincronizarii selective a memoriei in functie de
indexul la care se efectueaza operatiile, in schimbul sincronizarii intregului
buffer.

Din teste se poate observa ca timpul de executie depinde de mai multi factori.
Pe de-o parte, un numar mare de thread-uri relativ la dimensiunea buffer-ului
afecteaza in mod negativ timpul de executie. Pe de alta parte, reducerea
numarului de thread-uri si cresterea suprafatei buffer-ului permite executia mai
rapida a operatiilor, intrucat scade probabilitatea concurarii mai multor
thread-uri pentru aceeasi zona de memorie. De mentioant este si ca mecanismele
de sincronizare a implementarilor nu afecteaza neuniform performanta operatiei
de scriere fata de cea a operatiei de citire, fapt vizibil in invarianta
timpilor de executie in functie de raportul dintre numarul de tipuri de operatii
sau durata disproportionala a scrierilor si citirilor in sine.
