# felice_coppola_adc_2021
## Felice Coppola: 0522501020
## Anonymous Chat
## MD5 hash project: f1261c15cadd794b0e6be7ba43d4af4d

![image](https://play-lh.googleusercontent.com/9PfXXTijC_98UWTnPlG1xNO1Zs1YyE_samusENzGHzRTHVgGqBWJjuNrlu00N7a6jAk)


# Tabella dei contenuti
 1. Idea del progetto
 2. Soluzione proposta
    - Join
    - Leave
    - Interact
 3. Struttura del progetto
 4. Testing con JUnit
 5. Getting Started
    - Prerequisiti
    - Installazione
 6. Utilizzo


## Idea del progetto
L'idea del progetto è quella di realizzare un "Anonymous Chat" eseguita su una rete P2P, basandosi sull'idea fornita dal [progetto di esempio](https://github.com/spagnuolocarmine/p2ppublishsubscribe.git) del Professor 
[Carmine Spagnuolo](https://github.com/spagnuolocarmine) per l'esame di ADC dell'Università degli studi di Salerno.


L'obiettivo principale è quello, quindi, di realizzare un progetto che sfrutti una comunicazione asincrona ed anonima. Questi requisiti sono stati raggiunti
grazie all'utilizzo del paradigma **Publish/Subscribe** e al framework/libreria **TomP2P**.

## Soluzione Proposta
In breve ogni peer deve essere in grado inviare messaggi su una chat room pubblica in modo anonimo.  Il sistema consente agli utenti di creare una nuova stanza, entrare in una stanza, lasciare una stanza e inviare messaggi.
Inoltre, sono state implementate altre funzionalità che verranno illustrate successivamente.
Ora, vedremo in dettaglio le diverse funzionalità e operazioni garantite per il funzionamento del sistema.
### Join
Tramite quest'operazione un nodo è in grado di collegarsi ad una rete e iniziare a chattare grazie all'instanziazione di un oggetto della classe **AnonymousChat**.



