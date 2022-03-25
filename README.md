# felice_coppola_adc_2021


![image](https://play-lh.googleusercontent.com/9PfXXTijC_98UWTnPlG1xNO1Zs1YyE_samusENzGHzRTHVgGqBWJjuNrlu00N7a6jAk)

| Studente  | Progetto |
| ------------- | ------------- |
| Felice Coppola 0522501020  | Anonymous Chat  |


# Tabella dei contenuti
 1. Descrizione del progetto
 2. Tecnologie e librerie utilizzate
 3. Soluzione proposta e Descrizione della soluzione
 4. Testing con JUnit
 5. Getting Started
 


## Descrizione del progetto
L'idea del progetto è quella di realizzare un "Anonymous Chat" eseguita su una rete P2P, basandosi sull'idea fornita dal [progetto di esempio](https://github.com/spagnuolocarmine/p2ppublishsubscribe.git) del Professor 
[Carmine Spagnuolo](https://github.com/spagnuolocarmine) per l'esame di ADC dell'Università degli studi di Salerno.
Infatti, il progetto mira a ricreare tutte le funzionalità tipiche di una chat anonima, con alcune funzionalità extra, pensate per 
rendere leggermente più accattivante il progetto.
Nello specifico è possibile:
- Creare una stanza
- Accedere ad una stanza
- Inviare messaggi in una stanza
- Lasciare una stanza
Bonus:
- Lasciare la rete
- Distruggere una stanza
- Mostrare gli utenti in una stanza
- Mostrare i messaggi di una stanza

L'obiettivo principale è quello, quindi, di realizzare un progetto che sfrutti una comunicazione asincrona ed anonima. Questi requisiti sono stati raggiunti
grazie all'utilizzo del paradigma **Publish/Subscribe** e al framework/libreria **TomP2P**.

## Tecnologie e librerie utilizzate
- TomP2P: Libreria che permette la gestione di dht all'interno della rete.
- Java: Linguaggio di programmazione utilizzato per lo sviluppo del progetto.
- Maven: Software project management utilizzato per la gestione del progetto.
- JUnit: Framework utilizzato per poter effettuare testing.
- Docker: Software utilizzato per la creazione di container.

## Soluzione proposta
Per la realizzazione e lo sviluppo della soluzione con le tecnologie e librerie precedentemente illustrate si è partiti dalle [API](https://github.com/spagnuolocarmine/distributedsystems-unisa/blob/master/homework/AnonymousChat.java) fornite dal professore

Nel package del progetto, src/main/java diviso per cartelle, troviamo:
- la cartella **Beans** la quale include:
  - **Chatroom.java**: rappresenta la classe per l'istanziazione di oggetti di tipo ChatRoom e che implementa diverse funzionalità per i peer 
  - **Message.java**: rappresenta la classe per l'istanziazione di oggetti di tipo Message, i quali racchiudono banalmente i messaggi inviati nelle stanze
- la cartella **Interfaces** include:
  - **AnonymousChat.java**: rappresenta l'interfaccia che contiene i metodi principali che sono stati poi implementati
  - **MessageListener.java**: rappresenta l'interfaccia per il parsing dei messaggi ricevuti dai peer
- la cartella **Implementation** include
  - **AnonymousChatImpl.java**: rappresenta la classe che implementa l'interfaccia **AnonymousChat**  
  - **MessageListenerImpl.java** rappresenta la classe che implementa l'interfaccia **MessageListener**
- **Tester.java** rappresenta la classe Main per il progetto
- **Dockerfile**file docker che contiene i comandi che l'utente potrà eseguire per creare un'immagine docker
## Descrizione della soluzione
La classe **AnonymousChatImpl** contiene l'implmeentazione dei metodi ereditati dall'interfaccia **AnonymousChat**:
- **createRoom**: permette ad un peer di creare una nuova stanza
- **joinRoom**: permette di effettuare un join ad una stanza già esistente
- **leaveRoom**: permette di uscire da una stanza alla quale si è effettuato il join in precedenza
- **sendMessage**: permette di inviare messaggi in una stanza nella quale si è effettuato il join in precedenza
- **destroyRoom**: permette di distruggere la stanza, solo se si è l'ultimo rimasto
- **showUsers**: permette di visualizzare i messaggi inviati nella stanza
- **leaveNetwork**: permette di lasciare la rete
Oltre questi metodi principali sono stat implementati anche altri metodi di utilizzo generale per riuscire ad effettuare correttamente le funzionalità precedenti, nello specifico:
- **findRoom**: permette di controllare se una stanza esiste e la restituisce
- **createChatRoom**: utilizza il metodo precedente per verificare se una stanza esiste e in caso contrario chiama il metodo principale createRoom per creare la nuova stanza
- **tryToJoinRoom**: verifica se la stanza è presente già nelle mie stanze altrimenti chiama il metodo Join per effettuare o meno il join
- **tryToSendMsg**: verifica se è tutto corretto per l'invio del messaggio (controllo se la stanza esiste e siamo joinati oppure messaggio errato)

## Dipendenze e pom.xml
  ![image](https://user-images.githubusercontent.com/55912466/160074090-dfd25278-f264-4b97-b73a-d3e2204f7d57.png)

## Testing con JUnit
Il testing delle funzionalità è stato effettuato attraverso l'utilizzo della libreria JUnit
Per creare i test cases sono stati:
- creati 4 peers che verranno utilizzati per i vasi test cases
- i test cases sono stati creati sulla base delle funzionalità del progetto, e sono stati esplorati tutti i possibili casi (ad esempio messaggi inviati in una stanza non esistente oppure il join ad una stanza nella quale si è già joinati)
- una volta ultimati tutti i test cases, è stato chiamato il metodo **leaveNetwork()** per tutti i peers cosi da farli uscire dalla rete.  

I test cases creati, per una maggiore leggibilità ed omogeneità presentano i nomi della funzionalità che si sta testando accompagnati anche da un numero (in quanto sono possibili vari casi per un singolo test case) questo anche per evitare conflitti.

Sono stati implementati 17 test cases che è possibile visualizzare qui [Test](https://github.com/Felice-Copp56/felice_coppola_adc_2021/blob/master/src/test/java/AnonymousChatTesting.java)

Viene riportato un esempio di test case per mostrare un esempio di flusso e di struttura:
### Test case per la creazione di una room già esistente o alla quale si è già joinati
![image](https://user-images.githubusercontent.com/55912466/160136863-72967674-a0a2-4834-93fc-b22071dc7a70.png)

## Getting Started
L'applicazione è fornita tramite l'utilizzo di un container Docker, in esecuzione sulla macchina locale.
- Innanzitutto bisogna fare la build del container docker:
```bash
docker build --no-cache -t anonymouschat
```
- Successivamente bisogna lanciare il master peer usando:
```bash
docker run -i --name-master -e MASTERID="127.0.0.1" -e ID=0 anonymouschat
```
**Ricorda di lanciare il master peer usando ID=0.**
**_Note:_** Dopo il primo run puoi lanciare il master usando il seguente comando:

```bash
docker start -i MASTER
```
- Successivamente si possono lanciare i peer generici:
 - Avviato il master peer bisogna controllare l'indirizzo IP del container:
   - Si può verificare l'id del container: ``` docker ps ```
   - Si può verificare l'indirizzo ip: ``` docker inspect <container ID> ```
 - Ultimata questa fase è possibile, finalmente, lanciare il peer generico
   - ``` docker run -i --name peer-1 -e MASTERIP="172.17.0.2" -e ID=1 anonymouschat ```

**Nota** dopo il primo lancio è possibile lanciare il peer usando il seguente comando:
```
docker start -i peer-1
```

