# Hinweise zum Programmierbeispiel

<Juergen.Zimmermann@HS-Karlsruhe.de>

> Diese Datei ist in Markdown geschrieben und kann z.B. mit IntelliJ IDEA
> oder NetBeans gelesen werden. Näheres zu Markdown gibt es in einem
> [Wiki](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)

## Git initialisieren

Vor der erstmaligen Benutzung von Git ist noch eine Initialisierung mit dem
eigenen Namen und der eigenen Emailadresse notwendig:

```
    git config --global user.name "Max Mustermann"
    git config --global user.email Max.Mustermann@beispiel.dZe
```

## Git-Repository erstellen und konfigurieren

Ein Config-Server kann Properties aus einem Git-Repository bereitstellen.
Diese Properties werden je Microservice in einer Properties-Datei definiert,
z.B. `kunde-dev.properties` für den Microservice _kunde_, wenn er im Profil
_dev_ läuft. YML-Dateien sind leider nicht möglich.

Im  Unterverzeichnis `git` sind die Dateien `kunde-dev.properties` und
`bestellung-dev.properties` für die Beispiele 2 und 3 bereitgestellt.

Jetzt muss man in einer Eingabeaufforderung die nachfolgenden Git-Kommandos
aufrufen, damit ein Git-Repository im Unterverzeichnis `git` initialisiert
wird.

```
    cd git
    git init
    git add .
    git commit -m "Initiale Version der Properties-Dateien"
    git log -p -1
```

Das letzte Kommandos ist nicht notwendig; es zeigt nur das letzte Commit an.
Das Git-Repository kann nun Properties für die Microservices _kunde_ und
_bestellung_ (Beispiel 3) bereitstellen.

## Übersetzung und Start des Config-Servers

In `src\main\resources\application.yml` muss man beim Schlüssel
`spring.cloud.config.server.git.uri` die URI zum Verzeichnis `git` richtig
setzen.

Danach kann man in einer Powershell `config.ps1` aufrufen.

## Überprüfung der bereitgestellten Properties

In einem Webbrowser `http://localhost:8888/kunde/dev` aufrufen, um die
Properties auszugeben, die für den Microservice _kunde_ mit dem Profile _dev_
bereitgestellt werden. Der Benutzername ist `admin` und dass Passwort ist `p`. 

Es können auch folgende URIs verwendet werden:

* `http://localhost:8888/kunde-dev.yml` für die Ausgabe der Properties im Format
   YML oder
* `http://localhost:8888/kunde/dev/master` für die Ausgabe  der Properties aus
  dem Default-Branch _master_ des Git-Repositories. Dass es sich um den Branch
  _master_ handelt, sieht man dadurch, dass bei `label` jetzt der Wert `master`
  steht; beim Request mit `http://localhost:8888/kunde/dev` stand hier `null`.
  Das Git-Repository im Unterverzeichnis `git` hat übrigens nur den Branch
  _master_.

## Microservice _kunde_ starten

Im Server _kunde_ kann man ggf. in der Datei `src\main\resources\bootstrap.yml`
beim Schlüssel `spring.cloud.config.uri` die URI zum Config-Server setzen.

Den Microservice _kunde_ starten: siehe zugehörige Datei `ReadMe.md`.

## Properties beim Microservice _kunde_ überprüfen

Mit der URI `http://localhost:8081/admin/env` kann überprüft werden, ob der
Microservice _kunde_ die Properties vom Config-Server korrekt ausliest. Der
Response wird mit dem MIME-Type `application/vnd.spring-boot.actuator.v1+json`
zurückgegeben, welcher von einem Webbrowser i.a. nicht verstanden wird.

Man kann z.B. den _REST Client_ von _IntelliJ IDEA_ benutzen, der über
`Tools > Test RESTful Web Service` aktiviert werden kann:

* HTTP method: `GET`
* Host/port: `http://localhost:8081`
* Path: `/admin/env`

Die Ausgabe kann mit den beiden Icons _View as JSON_ und _Reformat response_
gut lesbar dargestellt werden. Die vom Config-Server bereitgestellten Properties
sind bei
`"configService:file:///C:/.../IdeaProjects/config/git/kunde-dev.properties"`
zu finden.

## Properties für einen eigenen Microservice

Im Unterverzeichnis `git` eine Properties-Datei, z.B. `buch-dev.properties`
anlegen, falls der eigene Microservice _buch_ heißt. Danach in einer
Powershell die folgenden Git-Kommandos eingeben:

```
    cd git
    git add .
    git commit -m "Properties fuer den eigenen Microservice"
    cd ..
```
