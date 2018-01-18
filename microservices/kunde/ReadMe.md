# Hinweise zum Programmierbeispiel

<Juergen.Zimmermann@HS-Karlsruhe.de>

> Diese Datei ist in Markdown geschrieben und kann z.B. mit IntelliJ IDEA
> oder NetBeans gelesen werden. Näheres zu Markdown gibt es in einem
> [Wiki](http://bit.ly/Markdown-Cheatsheet)

## Powershell

Überprüfung, ob sich Skripte starten lassen:

```CMD
    Get-Executionpolicy -list
    Set-ExecutionPolicy RemoteSigned CurrentUser
    Get-Executionpolicy -list
```

Ausführung des Powershell-Skripts `kunde.ps1` in einer Eingabeaufforderung ("cmd"):

```CMD
    powershell -file kunde.ps1
```

## Falls die Speichereinstellung für Gradle zu großzügig ist

In `gradle.properties` bei `org.gradle.jvmargs` den voreingestellten Wert
(1,5 GB) ggf. reduzieren.

## Vorbereitungen im Quellcode der Microservices

### Eigener *aufgerufener* Microservice ("Server")

In `config\AppConfig.kt` sind folgende Annotationen zusätzlich
erforderlich:

* `@EnableDiscoveryClient`
* `@EnableCircuitBreaker`

In `Microservice.kt` sind folgende Properties zusätzlich erforderlich
(siehe Beispiel _kunde_):

* `eureka.`...
* `feign.`...

In `src\main\resources` ist zusätzlich die Konfigrationsdatei `bootstrap.yml`
für _Netflix Eureka_ und _Spring Config_ erforderlich.

### Eigener *aufrufender* Microservice ("Client")

In `config\AppConfig.kt` sind folgende Annotationen zusätzlich
erforderlich:

* `@EnableDiscoveryClient`
* `@EnableCircuitBreaker`

In `Microservice.kt` sind folgende Properties zusätzlich erforderlich
(siehe Beispiel _bestellung_):

* `eureka.`...
* `feign.`...

In `src\main\resources` ist zusätzlich die Konfigrationsdatei `bootstrap.yml`
für _Netflix Eureka_ und _Spring Config_ erforderlich.

## Vorbereitung für den Start der Server

### Internet-Verbindung

Eine _Internet-Verbindung_ muss vorhanden sein, damit die eigenen Microservices
über die IP-Adresse des Rechners aufgerufen werden können. Ansonsten würden die
Rechnernamen verwendet werden, wozu ein DNS-Server benötigt wird.

### IP-Adresse und hosts

Die IP-Adresse wird über das Kommando `ipconfig` ermittelt und liefert z.B.
folgende Ausgabe:

```TXT
    C:\>ipconfig

    Windows-IP-Konfiguration

    Ethernet-Adapter Ethernet:

       ...
       IPv4-Adresse  . . . . . . . . . . : 193.196.84.110
       ...
```

Die IP-Adresse muss dann in `C:\Windows\System32\drivers\etc\hosts` am
Dateiende eingetragen und abgespeichert werden. Dazu muss man
Administrator-Berechtigung haben.

```TXT
    193.196.84.110 localhost
```

### VirtualBox ggf. deaktivieren

Falls VirtualBox installiert ist, darf es nicht aktiviert sein, weil sonst
intern die IP-Adresse `192.168.56.1` verwendet wird.

VirtualBox wird folgendermaßen deaktiviert:

* Netzwerk- und Freigabecenter öffnen, z.B. Kontextmenü beim WLAN-Icon
* _"Adaptereinstellungen ändern"_ anklicken
* _"VirtualBox Host-only Network"_ anklicken
* Deaktivieren


### Proxy-Einstellung für Gradle

Die Proxy-Einstellung in gradle.properties muss richtig gesetzt sein. Dabei
muss die eigene IP-Adresse bei den Ausnahmen ("nonProxyHosts") eingetragen
sein, wozu man typischerweise Wildcards benutzt.

## Überblick: Start der Server

* MongoDB
* Registry
* Config
* Zookeeper
* Kafka
* Mailserver
* API-Gateway
* Circuit Breaker Dashboard (_optional_)
* kunde
* bestellung

Die Server (außer MongoDB, Zookeeper und Kafka) sind jeweils in einem eigenen Gradle-Projekt.

## MongoDB starten und beenden

Durch Aufruf der .ps1-Datei:

````CMD
    .\mongodb
````

bzw.

````CMD
    .\mongodb stop
````

## IntelliJ IDEA evtl. statt Studio 3T als Datenbankbrowser

Das Teilfenster _Mongo Explorer_ aktivieren und einen Doppelklick auf den
Eintrag `localhost` machen. Jetzt sieht man die Datenbank `hska` und kann
zu den Collections dieser Datenbank navigieren.

Eine Collection kann man wiederum durch einen Doppelklick inspizieren und
kann dabei die Ansicht zwischen _Tree_ und _Table_ variieren.

## Mailserver

_FakeSMTP_ wird durch die .ps1-Datei `mailserver` gestartet und läuft auf Port 25000.

## Config-Server starten

Siehe `ReadMe.md` im Beispiel `config`.

## Übersetzung und Ausführung

### Start des Servers

In einer Powershell wird der Server mit der Möglichkeit für einen
_Restart_ gestartet, falls es geänderte Dateien gibt:

```CMD
    .\kunde
```

### Kontinuierliches Monitoring von Dateiänderungen

In einer zweiten Powershell überwachen, ob es Änderungen gibt, so dass
die Dateien für den Server neu bereitgestellt werden müssen; dazu gehören die
übersetzten .class-Dateien und auch Konfigurationsdateien. Damit nicht bei jeder
Änderung der Server neu gestartet wird und man ständig warten muss, gibt es eine
"Trigger-Datei". Wenn die Datei `restart.txt` im Verzeichnis
`src\main\resources` geändert wird, dann wird ein _Neustart des Servers_
ausgelöst und nur dann.

Die Powershell, um kontinuierlich geänderte Dateien für den Server
bereitzustellen, kann auch innerhalb der IDE geöffnet werden (z.B. als
_Terminal_ bei IntelliJ).

```CMD
    .\gradlew classes -t --build-cache
```

### Eventuelle Probleme mit Windows

_Nur_ falls es mit Windows Probleme gibt, weil der CLASSPATH zu lang ist und
deshalb `java.exe` nicht gestartet werden kann, dann kann man auf die beiden
folgenden Kommandos absetzen:

```CMD
    .\gradlew assemble --build-cache
    java -jar build/libs/kunde.jar --spring.profiles.active=dev
```

### Properties beim gestarteten Microservice _kunde_ überprüfen

*Funktioniert noch nicht mit Spring WebFlux*

Mit der URI `https://localhost:8444/admin/env` kann überprüft werden, ob der
Microservice _kunde_ die Properties vom Config-Server korrekt ausliest. Der
Response wird mit dem MIME-Type `application/vnd.spring-boot.actuator.v1+json`
zurückgegeben, welcher von einem Webbrowser i.a. nicht verstanden wird.

Man kann z.B. den _REST Client_ von _IntelliJ IDEA_ benutzen,
der über `Tools > Test RESTful Web Service` aktiviert werden kann:

* HTTP method: `GET`
* Host/port: `https://localhost:8444`
* Path: `/admin/env`

Die Ausgabe kann mit den beiden Icons _View as JSON_ und _Reformat response_
gut lesbar dargestellt werden. Die vom Config-Server bereitgestellten Properties
sind bei
`"configService:file:///C:/Users/.../IdeaProjects/config/git-repo/kunde-dev.properties"`
zu finden.

Analog können bei Microservice `bestellung` die Properties überprüft werden:

* Der Port ist von `8444` auf `8445` zu ändern.
* Bei `"configService:file:///C:/Users/...` steht `bestellung-dev.properties`

### Registrierung bei _Service Registry_ überprüfen

````URI
    http://localhost:8761/eureka/apps/kunde
````

### Herunterfahren in einer eigenen Powershell

*Funktioniert noch nicht mit Spring WebFlux*

```CMD
    .\kunde stop
```

### Tests

Folgende Server müssen gestartet sein:

* MongoDB
* Registry
* Config
* Zookeeper
* Kafka
* Kafka-Mailer
* Mailserver

```CMD
    .\gradlew test --build-cache
```

### Codeanalyse durch detekt und kotlinter

```CMD
    .\gradlew lintKotlin detektCheck --build-cache
```

Um *detekt* bzw. `detektCheck` aufzurufen, muss man online sein.

## Beispiel für einen _PUT_-Request mit einer multimedialen Datei

Man kann z.B. den _REST Client_ von _IntelliJ IDEA_ benutzen,
der über `Tools > Test RESTful Web Service` aktiviert werden kann.

* HTTP method: `PUT`
* Host/port: `https://localhost:8444`
* Path: `/multimedia/000000000000000000000001`

Die hochzuladende Datei wird über `File to send` ausgewählt.

Bei `Headers` einen Eintrag mit dem Schlüssel `Content-Type` und dem
MIME-Type, z.B. `image/png`, erstellen.

In `src\config\rest` gibt es PNG- und JPG-Dateien.

Nun kann man mit _3T Studio_ in der Datenbank `hska` in der Collection
`fs.files` die hochgeladene Datei mit dem Default-Viewer inspizieren.

Außerdem kann man z.B. in einem Webbrowser die hochgeladene Datei über
die URL `https://localhost:8444/multimedia/000000000000000000000001`
als GET-Request herunterladen.

## Dashboard für Service Registry (Eureka)

```URI
    http://localhost:8761
```

## Anzeige im Circuit Breaker Dashboard (FEHLT)

```URI
    http://localhost:8762
```

Im Dashboard die URI für den zu beobachtenden Microservice eingeben, z.B.:

```URI
    http://admin:p@localhost:8081/admin/hystrix.stream
```

Hier wird BASIC-Authentifizierung mit dem Benutzernamen 'admin' und mit dem
Passwort 'p' verwendet.

### Beachte

* Erst **nach dem ersten Request** des zu beobachtenden Microservice ist eine
  Anzeige zu sehen.
* Mit dem Microservice wird über _HTTP_, und nicht über _HTTPS_ kommuniziert,
  weil man sonst für _Hystrix_ noch einen _Truststore_ konfigurieren müsste.
  Das würde den Umfang der Übungen sprengen und gehört in Vorlesungen mit den
  Schwerpunkten "IT-Sicherheit" und "Automatisierung von Geschäftsprozessen".

## Vorhandene Mappings auflisten

*Funktioniert noch nicht mit Spring WebFlux*

D.h. welche Zuordnung gibt es zwischen URIs bzw. Pfaden, HTTP-Methoden und
Java-Methoden?

```URI
    https://localhost:8444/admin/mappings
```

## Vorhandene Spring-Beans auflisten

*Funktioniert noch nicht mit Spring WebFlux*

```URI
    https://localhost:8444/admin/beans
```

## Ausführen der JAR-Datei in einer Powershell

```CMD
    java -jar build/libs/kunde.jar --spring.profiles.active=dev
```

Die [Dokumentation](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#executable-jar)
enthält weitere Details zu einer ausführbaren JAR-Datei bei Spring Boot
