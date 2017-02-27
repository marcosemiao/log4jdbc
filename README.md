[![Build Status](https://api.travis-ci.org/marcosemiao/log4jdbc.png?branch=refacto)](https://travis-ci.org/marcosemiao/log4jdbc) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.marcosemiao.log4jdbc/log4jdbc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.marcosemiao.log4jdbc/log4jdbc)

# Log4Jdbc

## Fonctionnalités générales
Cet outil est un proxy JDBC qui permet d'intercepter les appels au driver jdbc.
- Compatible à partir de la version Java 3.
- Divisé en 2 parties, la partie interception et la partie traitement des informations, cela permet à quiconque d'implémenter son propre traitement.
- Facile d'utilisation, il suffit juste de changer le driver jdbc par : "**fr.ms.log4jdbc.Driver**" et de rajouter au début de l'url de connexion "**jdbc:log4**"
- Possibilité de l'utiliser en tant que **DataSource**, **XADataSource** ou **ConnectionPoolDataSource**.
- Permet de récupérer les requêtes sql, leur résultat, le temps d'exécution de chaque opération jdbc.
- Permet de visualiser l'intégralité des requêtes pour chaque transaction JDBC et XA.
- Permet de visualiser dans une transation JDBC, la position de chaque **savepoint** et l'état de toutes les requêtes.
- Permet de visualiser la requête même lors d'une levée d'exception sur cette requête.
- Disponible sur le repository central de Maven.
- Et beaucoup d'autres fonctionnalités...

## Utilisation rapide (Driver Jdbc)

Il y a 3 étapes à effectuer pour l'utiliser :
- Ajouter la dépendance :

````xml
<dependency>
	<groupId>com.github.marcosemiao.log4jdbc</groupId>
	<artifactId>log4jdbc-file</artifactId>
	<version>0.2.0</version>
</dependency>
````
ou
https://repo1.maven.org/maven2/com/github/marcosemiao/log4jdbc/log4jdbc-file/0.2.0/log4jdbc-file-0.2.0.jar

- Changer le driver jdbc par "**fr.ms.log4jdbc.Driver**"

_Par exemple :_

"**com.mysql.jdbc.Driver**" devient "**fr.ms.log4jdbc.Driver**"
"**oracle.jdbc.driver.OracleDriver**" devient "**fr.ms.log4jdbc.Driver**"
"**com.ibm.db2.jcc.DB2Driver**" devient "**fr.ms.log4jdbc.Driver**" etc...

- Changer l'url d'accès à la base de donnée en rajoutant au début "**jdbc:log4**"

_Par exemple :_

- "jdbc:mysql://localhost:3306/test" devient "**jdbc:log4**jdbc:mysql://localhost:3306/test"
- "jdbc:oracle:thin:@myhost:1521:orcl" devient "**jdbc:log4**jdbc:oracle:thin:@myhost:1521:orcl"
- "jdbc:db2://127.0.0.1:50000/SAMPLE" devient "**jdbc:log4**jdbc:db2://127.0.0.1:50000/SAMPLE"

Lancer l'application et log4jdbc tracera sur la sortie standard :
- La date d'exécution de la requête
- Le thread courant
- L'état des connexions jdbc.
- Le nombre de requête.
- La requête sql.

### Cas Particulier

Il est possible qu'au démarrage de l'application, log4jdbc ne retrouve pas le vrai driver jdbc, une exception est alors remontée ressemblant à cela :
```
Caused by: java.sql.SQLException: No suitable driver
```

ou bien une erreur indiquant que l'url est incorrecte :

```
Cannot create JDBC driver of class 'fr.ms.log4jdbc.Driver' for connect URL 'jdbc:log4jdbc:mysql://localhost/app'
```

Cela signifie juste que votre driver jdbc n'est pas enregistré automatiquement dans le DriverManager.
Il est donc nécessaire de spécifier à log4jdbc, le driver jdbc que vous utilisez, pour cela il est nécessaire de rajouter une propriété système avec les noms de drivers séparés par une virgule:

**-Dlog4jdbc.drivers=NOM DES DRIVERS**

_Exemple :_

J'utilise le driver **com.mysql.jdbc.Driver** la propriété devient :
```
-Dlog4jdbc.drivers=com.mysql.jdbc.Driver
```
J'utilise aussi en plus le driver **oracle.jdbc.driver.OracleDriver** la propriété devient :
```
-Dlog4jdbc.drivers=com.mysql.jdbc.Driver,oracle.jdbc.driver.OracleDriver
```

## Utilisation rapide (DataSource)

Il y a 3 étapes à effectuer pour l'utiliser :
- Ajouter la dépendance :

````xml
<dependency>
	<groupId>com.github.marcosemiao.log4jdbc</groupId>
	<artifactId>log4jdbc-file</artifactId>
	<version>0.2.0</version>
</dependency>
````
ou
https://repo1.maven.org/maven2/com/github/marcosemiao/log4jdbc/log4jdbc-file/0.2.0/log4jdbc-file-0.2.0.jar

- Changer la DataSource jdbc par :
	- "**fr.ms.log4jdbc.DataSource**" si votre DataSource d'origine utilise "**javax.sql.DataSource**"
	- "**fr.ms.log4jdbc.XADataSource**" si votre DataSource d'origine utilise "**javax.sql.XADataSource**"
	- "**fr.ms.log4jdbc.ConnectionPoolDataSource**" si votre DataSource d'origine utilise "**javax.sql.ConnectionPoolDataSource**"

_Par exemple pour DB2 :_

"com.ibm.db2.jcc.DB2DataSource" devient "fr.ms.log4jdbc.DataSource"

"com.ibm.db2.jcc.DB2XADataSource" devient "fr.ms.log4jdbc.XADataSource"

"com.ibm.db2.jcc.DB2ConnectionPoolDataSource" devient "fr.ms.log4jdbc.ConnectionPoolDataSource" etc...

- Rajouter la variable d'environnement pour définir le DataSource d'origine :
	- "**-Dfr.ms.log4jdbc.DataSource**" pour la DataSource
	- "**-Dfr.ms.log4jdbc.XADataSource**" pour la XADataSource
	- "**-Dfr.ms.log4jdbc.ConnectionPoolDataSource**" pour la ConnectionPoolDataSource

_Par exemple pour DB2 :_

Rajouter "-Dfr.ms.log4jdbc.DataSource=com.ibm.db2.jcc.DB2DataSource" si vous utilisez à l'origine "com.ibm.db2.jcc.DB2DataSource"

Rajouter "-Dfr.ms.log4jdbc.XADataSource=com.ibm.db2.jcc.DB2XADataSource" si vous utilisez à l'origine "com.ibm.db2.jcc.DB2XADataSource"

Rajouter "-Dfr.ms.log4jdbc.ConnectionPoolDataSource=com.ibm.db2.jcc.DB2ConnectionPoolDataSource" si vous utilisez à l'origine "com.ibm.db2.jcc.DB2ConnectionPoolDataSource"
etc...

## Paramétrage

Il est possible d'activer ou désactiver certaines fonctionnalités à l'aide d'un fichier de propriétés qui se trouve à la racine du classpath de l'application ou à l'aide d'une propriété système.

Rajouter un fichier vide "**log4jdbc.properties**" à la racine du classpath.
ou
rajouter la propriété système "**-Dlog4jdbc.file=NOMFICHIER**" exemple "**-Dlog4jdbc.file=D:\Perso\log4jdbc.properties**"

et rajouter dans ce fichier :

```
log4jdbc.file=D:/log4jdbc.log
log4jdbc.request.sql.style=none|oneline|format
log4jdbc.request.sql.all=true
log4jdbc.request.sql.execute=false
log4jdbc.request.sql.batch=false
log4jdbc.request.sql.select=false
log4jdbc.request.sql.select.resultset=false
log4jdbc.request.sql.insert=false
log4jdbc.request.sql.update=false
log4jdbc.request.sql.delete=false
log4jdbc.request.sql.create=false
```


| Option | Description |
| ------ | ------- |
|log4jdbc.file|Si vide, sortie standard sinon le fichier de log. Exemple : D:/log4jdbc.log|
|log4jdbc.request.sql.style|Permet de formater la requête sql|
|log4jdbc.request.sql.all|Traces toutes les requêtes sql, select, insert, update etc... y compris les requêtes dans une transaction et dans ou un batch|
|log4jdbc.request.sql.execute|Trace les requêtes sql à l'état exécute donc dans une transaction|
|log4jdbc.request.sql.batch|Trace les requêtes sql à l'état batch donc dans un batch|
|log4jdbc.request.sql.select|Trace uniquement les requêtes select|
|log4jdbc.request.sql.select.resultset|Trace avec la requête select, le résultat en forme de tableau (attention la volumetrie peut etre importante en fonction des données)|
|log4jdbc.request.sql.insert|Trace uniquement les requêtes insert|
|log4jdbc.request.sql.update|Trace uniquement les requêtes update|
|log4jdbc.request.sql.delete|Trace uniquement les requêtes delete|
|log4jdbc.request.sql.create|Trace uniquement les requêtes create|


**Attention :** "log4jdbc.request.sql.all" est à true, cela est identique à cela :

```
log4jdbc.request.sql.execute=true
log4jdbc.request.sql.batch=true
log4jdbc.request.sql.select=true
log4jdbc.request.sql.insert=true
log4jdbc.request.sql.update=true
log4jdbc.request.sql.delete=true
log4jdbc.request.sql.create=true
```
## Options optionnelles

### Stack Trace
Il est possible d'avoir en plus la stack trace mais cette fonctionnalitée est permise uniquement à partir de **Java 5**.


Ensuite le fonctionnement est similaire sauf que vous avez 2 paramètres en plus dans le fichier de conf :

> log4jdbc.stacktrace=true
log4jdbc.stacktrace.start.packages=fr.test,fr.super,fr.app

| Option | Description |
| ------ | ------- |
|log4jdbc.stacktrace|Permet de rajouter la stack trace|
|log4jdbc.stacktrace.start.packages|Permet d'afficher uniquement les packages qui commence par un argument séparé par un "," exemple : fr.test,fr.super,fr.app|


### Stack Trace avec JBoss 5
Il est possible également d'avoir l'intégralité de la stack trace au niveau des appels **EJB** avec **JBOSS 5**

**Exemple :**

2 instances de JBoss, une avec l'ihm et l'autre avec l'accès avec la base de données, si vous utilisez la fonctionnalité "Stack Trace" vous aurez uniquement la stack trace de l’instance contenant l'accès à la base de données.

Avec cette version, vous aurez également la stack trace du client ejb en plus de la stack trace actuelle, cela permet de visualiser plus facilement toute la chaine.

Au lieu de mettre comme dépendance :

Et de rajouter dans votre JBoss 5, la dépendance :

````xml
<dependency>
	<groupId>com.github.marcosemiao.log4jdbc</groupId>
	<artifactId>log4jdbc-marshaller</artifactId>
	<version>0.2.0</version>
</dependency>
````	
ou
https://repo1.maven.org/maven2/com/github/marcosemiao/log4jdbc/log4jdbc-marshaller/0.2.0/log4jdbc-marshaller-0.2.0.jar

ou simplement copier le fichier "log4jdbc-marshaller-0.2.0.jar" dans le répertoire lib.

**Exemple :**

jboss-5.0.1.GA\server\default\lib\log4jdbc-marshaller-0.2.0.jar

ensuite dans le fichier "remoting-jboss-beans.xml" qui se trouve dans le répertoire "jboss-5.0.1.GA\server\default\deploy"

Remplacer :

````xml
<entry><key>marshaller</key>
<value>org.jboss.invocation.unified.marshall.InvocationMarshaller</value></entry>
<entry><key>unmarshaller</key>
<value>org.jboss.invocation.unified.marshall.InvocationUnMarshaller</value></entry>
````

par :

````xml
<entry><key>marshaller</key>
<value>fr.ms.log4jdbc.jboss5.marshaller.InvocationMarshallerWrapper</value></entry>
<entry><key>unmarshaller</key>
<value>fr.ms.log4jdbc.jboss5.marshaller.InvocationUnMarshallerWrapper</value></entry>
````


			
			
## Exemple de sortie :
**Avec la version Java 3 :**

```
Fri Jan 16 17:23:51 CET 2015 - WorkerThread#1[10.0.2.21:65514]
1. Total 6 - jdbc:db2://DBDEV:50022/BAS_BTP - com.mysql.jdbc.Driver - TRANSACTION_READ_COMMITTED

Query Number : 20 - State : STATE_EXECUTE - Result Count : 4 - ResultSet Exec Time : 15 ms
Transaction Number : 16 - State : STATE_EXECUTE

SELECT ID_MOT_CLE, CD_TYPE_RATTACHEMENT, VA_LIBELLE 
    FROM T_REF_MOT_CLE 
    WHERE ID_RATTACHEMENT = '564a7c7ec0a8281012fda830a6f7826d' AND 
        CD_TYPE_RATTACHEMENT = 'FOR';

|---------------------------------|---------------------|---------------|
|ID_MOT_CLE                       |CD_TYPE_RATTACHEMENT |VA_LIBELLE     |
|---------------------------------|---------------------|---------------|
|57a40fdec0a8280e16dfcca34dafffde |FOR                  |biennale       |
|57a40fdfc0a8280e16dfcca39f580f7f |FOR                  |internationale |
|57a40fe0c0a8280e16dfcca330316476 |FOR                  |cirque         |
|57a40fe0c0a8280e16dfcca3b4e16de6 |FOR                  |arts           |
|---------------------------------|---------------------|---------------|

{executed in 3 ms} 
```

**Avec la version Java 5 :**

```
Fri Jan 16 17:23:51 CET 2015 - WorkerThread#1[10.0.2.21:65514]
1. Total 6 - jdbc:db2://DBDEV:50022/BAS_BTP - com.mysql.jdbc.Driver - TRANSACTION_READ_COMMITTED

Query Number : 20 - State : STATE_EXECUTE - Result Count : 4 - ResultSet Exec Time : 15 ms
Transaction Number : 16 - State : STATE_EXECUTE

SELECT ID_MOT_CLE, CD_TYPE_RATTACHEMENT, VA_LIBELLE 
    FROM T_REF_MOT_CLE 
    WHERE ID_RATTACHEMENT = '564a7c7ec0a8281012fda830a6f7826d' AND 
        CD_TYPE_RATTACHEMENT = 'FOR';

|---------------------------------|---------------------|---------------|
|ID_MOT_CLE                       |CD_TYPE_RATTACHEMENT |VA_LIBELLE     |
|---------------------------------|---------------------|---------------|
|57a40fdec0a8280e16dfcca34dafffde |FOR                  |biennale       |
|57a40fdfc0a8280e16dfcca39f580f7f |FOR                  |internationale |
|57a40fe0c0a8280e16dfcca330316476 |FOR                  |cirque         |
|57a40fe0c0a8280e16dfcca3b4e16de6 |FOR                  |arts           |
|---------------------------------|---------------------|---------------|

fr.app.ti.common.dao.AbstractappJdbcDaoSupport.query(AbstractappJdbcDaoSupport.java:316)
fr.app.ti.reference.dao.impl.MotCleDAO.getListMotCle(MotCleDAO.java:223)
fr.app.ti.reference.process.GestionMotCleProcess.getMotClesFromIdRattachement(GestionMotCleProcess.java:233)
fr.app.ti.forfait.business.ForfaitBSBean.getMotsCles(ForfaitBSBean.java:241)

{executed in 3 ms} 
```

**Avec la version JBoss 5 :**

```
Fri Jan 16 17:23:51 CET 2015 - WorkerThread#1[10.0.2.21:65514]
1. Total 6 - jdbc:db2://DBDEV:50022/BAS_BTP - com.mysql.jdbc.Driver - TRANSACTION_READ_COMMITTED

Query Number : 20 - State : STATE_EXECUTE - Result Count : 4 - ResultSet Exec Time : 15 ms
Transaction Number : 16 - State : STATE_EXECUTE

SELECT ID_MOT_CLE, CD_TYPE_RATTACHEMENT, VA_LIBELLE 
    FROM T_REF_MOT_CLE 
    WHERE ID_RATTACHEMENT = '564a7c7ec0a8281012fda830a6f7826d' AND 
        CD_TYPE_RATTACHEMENT = 'FOR';

|---------------------------------|---------------------|---------------|
|ID_MOT_CLE                       |CD_TYPE_RATTACHEMENT |VA_LIBELLE     |
|---------------------------------|---------------------|---------------|
|57a40fdec0a8280e16dfcca34dafffde |FOR                  |biennale       |
|57a40fdfc0a8280e16dfcca39f580f7f |FOR                  |internationale |
|57a40fe0c0a8280e16dfcca330316476 |FOR                  |cirque         |
|57a40fe0c0a8280e16dfcca3b4e16de6 |FOR                  |arts           |
|---------------------------------|---------------------|---------------|

fr.app.ti.SpectaclesManager.getForfaitMotClee(SpectaclesManager.java:910)
fr.app.ti.SpectaclesManagerInstance.loadManifestation(SpectaclesManagerInstance.java:334)
fr.app.ti.SpectaclesManagerInstance.getManifestationById(SpectaclesManagerInstance.java:101)
fr.app.ti.SpectaclesManagerInstance.getManifestationByIdNoCache(SpectaclesManagerInstance.java:95)
fr.app.ti.SpectaclesManagerInstance.getManifestationById(SpectaclesManagerInstance.java:82)
fr.app.ti.SpectaclesManagerInstance$$FastClassByCGLIB$$dd8593a3.invoke(<generated>)
fr.app.ti.ManifestationCallPolicy.handleMethodCall(ManifestationCallPolicy.java:31)
Remote EJB : IndexationForfait
fr.app.ti.common.dao.AbstractappJdbcDaoSupport.query(AbstractappJdbcDaoSupport.java:316)
fr.app.ti.reference.dao.impl.MotCleDAO.getListMotCle(MotCleDAO.java:223)
fr.app.ti.reference.process.GestionMotCleProcess.getMotClesFromIdRattachement(GestionMotCleProcess.java:233)
fr.app.ti.forfait.business.ForfaitBSBean.getMotsCles(ForfaitBSBean.java:241)

{executed in 3 ms} 
```

**Avec une procédure stockée :**

```
11-05-2016 09:26:50.124 - WebContainer : 0
3. Total 5 - jdbc:db2://DBDEV:50022/BAS_BTP - com.ibm.db2.jcc.DB2ConnectionPoolDataSource - TRANSACTION_READ_COMMITTED

Query Number : 42 - State : QUERY_STATE_COMMIT - Result Count : 15 - ResultSet Exec Time : 94 ms
Transaction Number : 4 - Type : JDBC - State : TRANSACTION_STATE_COMMIT

CALL DBM01.PBP2M_BTP_HISTORIQUE_RA ('M09566',1,2784578)


|--------|-------------------------|
|NUM     |ZI                       |
|--------|-------------------------|
|1114578 |STE DISTRIB              |
|111076  |LA CABANE DE PARIS       |
|1115465 |KIKOOP                   |
|1113819 |ROBERT MATERIELS         |
|11103   |B. DUPOND ET FILS        |
|1119304 |TEST MS                  |
|1115774 |G Z S                    |
|111820  |TEAM                     |
|111210  |LE GALL METHOD           |
|1117974 |TOTO INDUSTRIE           |
|111697  |IKEA                     |
|11157   |LEVERGER ETS             |
|111037  |PORTAIL                  |
|1114998 | MARCEL				   |
|1115036 |COUCOUM                  |
|--------|-------------------------|

example.service.ServiceHistorique.gestionHisto(ServiceHistorique.java:103)
example.service.ServiceHistorique.refreshHistorique(ServiceHistorique.java:162)
example.core.util.UserHelper.refreshHistorique(UserHelper.java:217)
example.ui.web.action.DefautcpAction.getHabilitation(DefautcpAction.java:344)
example.ui.web.action.DefautcpAction.initialisation(DefautcpAction.java:107)
example.ui.web.action.DefautcpHistoriqueAction.execute(DefautcpHistoriqueAction.java:26)
example.ui.web.handler.ErrorManagementFilter.doFilter(ErrorManagementFilter.java:53)
example.framework.filter.HibernateFilter.doFilter(HibernateFilter.java:104)

{executed in 31 ms} 
****************************************************************
```


### Apache Configuration 2 (Java 6)
Il est possible d'utiliser pour les properties la librairie Apache Configuration 2.
Elle permet de spécifier dans le fichier de configuration des variables d'environnement par exemple :

log4jdbc.file=${sys:log_folder}/log4jdbc.log

Pour ce faire il est nécessaire de remplacer la dépendance :

````xml
<dependency>
	<groupId>com.github.marcosemiao.log4jdbc</groupId>
	<artifactId>log4jdbc-file</artifactId>
	<version>0.2.0</version>
</dependency>
````

par

````xml
<dependency>
	<groupId>com.github.marcosemiao.log4jdbc</groupId>
	<artifactId>log4jdbc-file-extra</artifactId>
	<version>0.2.0</version>
</dependency>
````
