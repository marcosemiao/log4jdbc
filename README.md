# Log4Jdbc

## Fonctionnalités générales
Cet outil est un proxy JDBC qui permet d'intercepter les appels au driver jdbc.
- Il est compatible à partir de la version Java 3.
- L'outil est divisé en 2 parties, la partie interception et la partie traitement des informations, cela permet à quiconque d'implémenter son propre traitement.
- Facile d'utilisation, il suffit juste de changer le driver jdbc par : "fr.ms.log4jdbc.Driver" et de rajouter au début de l'url de connexion "log4jdbc:"
- Il permet de récupérer les requêtes sql, leur résultat, le temps d'exécution de chaque opération jdbc, l'exécution de la requête, des transactions, des batchs...
- Et beaucoup d'autres fonctionnalités...

## En Cours de test - Version expérimentale
Ceci est une première version, elle est utilisée sur plusieurs applications mais il reste du travail :
- Commenter le code
- JavaDoc
- TU

Le reste à faire sera fait au fur et à mesure ;)

Si vous remarquez des bugs, faites moi signe ;)

## Compilation

Pré-Requis :
- Java 7
- Maven 3


Même si l'outil fonctionne à partir de Java 3, il est nécessaire durant la phase de compilation d'avoir un jdk 7, le bytecode généré sera quand meme compatible Java 3.

- récupérer les sources
- se mettre à la racine du projet.
- mvn clean install :)

## Utilisation rapide
L'outil permet d'implémenter sa propre implémentation en fonction de son besoin mais toutefois, il est fourni avec plusieurs implémentations pour une utilisation rapide.

Il y a 3 étapes à effectuer pour l'utiliser :
- Ajouter la dépendance :


	<dependency>
		<groupId>fr.ms.log4jdbc.package</groupId>
        <artifactId>log4jdbc-file-java3</artifactId>
        <version>1.0.0-SNAPSHOT</version>
	</dependency>


- Changer le driver jdbc par "**fr.ms.log4jdbc.Driver**"

Par exemple :

"**com.mysql.jdbc.Driver**" devient "**fr.ms.log4jdbc.Driver**"
"**oracle.jdbc.driver.OracleDriver**" devient "**fr.ms.log4jdbc.Driver**"
"**com.ibm.db2.jcc.DB2Driver**" devient "**fr.ms.log4jdbc.Driver**" etc...

- Changer l'url d'accès à la base de donnée en rajoutant au début "**log4jdbc:**"

_Par exemple :_

- "jdbc:mysql://localhost:3306/test" devient "**log4jdbc:**jdbc:mysql://localhost:3306/test"
- "jdbc:oracle:thin:@myhost:1521:orcl" devient "**log4jdbc:**jdbc:oracle:thin:@myhost:1521:orcl"
- "jdbc:db2://127.0.0.1:50000/SAMPLE" devient "**log4jdbc:**jdbc:db2://127.0.0.1:50000/SAMPLE"

Lancer l'application et log4jdbc tracera sur la sortie standard :
- La date d'exécution de la requête
- Le thread courant
- L'état des connexions jdbc.
- Le nombre de requête.
- La requête sql.


## Paramétrage

Il est possible d'activer ou désactiver certaines fonctionnalités à l'aide d'un fichier de propriétés qui se trouve à la racine du classpath de l'application ou à l'aide d'une propriété système.

Rajouter un fichier vide "**log4jdbc.properties**" à la racine du classpath.
ou
rajouter la propriété système "**-Dlog4jdbc.file=NOMFICHIER**" exemple "**-Dlog4jdbc.file=D:\Perso\log4jdbc-back.properties**"

et rajouter dans ce fichier :

log4jdbc.file=D:/log4jdbc-back.log

log4jdbc.requete.sql.format=true

log4jdbc.requete.sql.all=true
log4jdbc.requete.sql.execute=false
log4jdbc.requete.sql.batch=false
log4jdbc.requete.sql.select=false
log4jdbc.requete.sql.select.resulset=false
log4jdbc.requete.sql.insert=false
log4jdbc.requete.sql.update=false
log4jdbc.requete.sql.delete=false
log4jdbc.requete.sql.create=false



log4jdbc.file								Si vide, sortie standard sinon le fichier de log. Exemple : D:/log4jdbc-back.log

log4jdbc.requete.sql.format					Permet de formater la requête sql.

log4jdbc.requete.sql.all					Traces toutes les requêtes sql, select, insert, update etc... y compris les requêtes dans une transaction et dans ou un batch

log4jdbc.requete.sql.execute				Trace les requêtes sql à l'état exécute donc dans une transaction

log4jdbc.requete.sql.batch					Trace les requêtes sql à l'état batch donc dans un batch.

log4jdbc.requete.sql.select					Trace uniquement les requêtes select

log4jdbc.requete.sql.select.resulset		Trace avec la requête select, le résultat en forme de tableau (attention la volumetrie peut etre importante en fonction des données)

log4jdbc.requete.sql.insert					Trace uniquement les requêtes insert

log4jdbc.requete.sql.update					Trace uniquement les requêtes update

log4jdbc.requete.sql.delete					Trace uniquement les requêtes delete

log4jdbc.requete.sql.create					Trace uniquement les requêtes create

Remarque quand le parametre "log4jdbc.requete.sql.all" est à true, cela est identique que cela :
log4jdbc.requete.sql.execute=true
log4jdbc.requete.sql.batch=true
log4jdbc.requete.sql.select=true
log4jdbc.requete.sql.insert=true
log4jdbc.requete.sql.update=true
log4jdbc.requete.sql.delete=true
log4jdbc.requete.sql.create=true

## Options optionnelles

### Stack Trace
Il est possible d'avoir en plus la stack trace mais cette fonctionnalité est permise uniquement à partir de Java 5.

Au lieu de mettre comme dépendance :

	<dependency>
		<groupId>fr.ms.log4jdbc.package</groupId>
		<artifactId>log4jdbc-file-java3</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency>
	
Rajouter à la place :

	<dependency>
		<groupId>fr.ms.log4jdbc.package</groupId>
		<artifactId>log4jdbc-file-java5</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency>

Ensuite le fonctionnement est similaire sauf que vous avez 2 paramètres en plus dans le fichier de conf :

log4jdbc.stacktrace=true
log4jdbc.stacktrace.start.packages=fr.test,fr.super,fr.app

log4jdbc.stacktrace							Permet de rajouter la stack trace.
log4jdbc.stacktrace.start.packages			Permet d'afficher uniquement les packages qui commence par un argument séparé par un "," exemple : fr.test,fr.super,fr.app


### Stack Trace avec JBoss 5
Il est possible également d'avoir l'intégralité de la stack trace au niveau des appels EJB avec JBOSS 5

Par exemple :

2 instances de JBoss, une avec l'ihm et l'autre avec l'accès avec la base de données, si vous utilisez la fonctionnalité "Stack Trace" vous aurez uniquement la stack trace de l’instance contenant l'accès à la base de données.

Avec cette version, vous aurez également la stack trace du client ejb en plus de la stack trace actuelle, cela permet de visualiser plus facilement toute la chaine.

Au lieu de mettre comme dépendance :

	<dependency>
		<groupId>fr.ms.log4jdbc.package</groupId>
		<artifactId>log4jdbc-file-java3</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency>
	
Rajouter à la place par :

	<dependency>
		<groupId>fr.ms.log4jdbc.package</groupId>
		<artifactId>log4jdbc-file-jboss5</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency> 

Et de rajouter dans votre JBoss 5, la dépendance :

	<dependency>
		<groupId>fr.ms.log4jdbc.logger</groupId>
		<artifactId>log4jdbc-logger-marshaller-jboss5</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency>
	
	ou simplement copier le fichier "log4jdbc-logger-marshaller-jboss5-1.0.1-SNAPSHOT.jar" dans le répertoire lib.

exemple :

jboss-5.0.1.GA\server\default\lib\log4jdbc-logger-marshaller-jboss5-1.0.1-SNAPSHOT.jar

ensuite dans le fichier "remoting-jboss-beans.xml" qui se trouve dans le répertoire "jboss-5.0.1.GA\server\default\deploy"

Remplacer :
            <entry><key>marshaller</key>   <value>org.jboss.invocation.unified.marshall.InvocationMarshaller</value></entry>
            <entry><key>unmarshaller</key> <value>org.jboss.invocation.unified.marshall.InvocationUnMarshaller</value></entry>
			
par :
			<entry><key>marshaller</key>   <value>fr.ms.log4jdbc.jboss5.marshaller.InvocationMarshallerWrapper</value></entry>
            <entry><key>unmarshaller</key> <value>fr.ms.log4jdbc.jboss5.marshaller.InvocationUnMarshallerWrapper</value></entry>



			
			
## Exemple de sortie :
Avec la version Java 3 :

Fri Jan 16 17:23:51 CET 2015 - WorkerThread#1[10.0.2.21:65514]
1. Total 6

Query Number : 20 - State : STATE_EXECUTE
SELECT ID_MOT_CLE, ID_RATTACHEMENT, CD_TYPE_RATTACHEMENT, VA_LIBELLE 
    FROM T_REF_MOT_CLE 
    WHERE ID_RATTACHEMENT = '564a7c7ec0a8281012fda830a6f7826d' AND 
        CD_TYPE_RATTACHEMENT = 'FOR';

|---------------------------------|---------------------------------|---------------------|---------------|
|ID_MOT_CLE                       |ID_RATTACHEMENT                  |CD_TYPE_RATTACHEMENT |VA_LIBELLE     |
|---------------------------------|---------------------------------|---------------------|---------------|
|57a40fdec0a8280e16dfcca34dafffde |564a7c7ec0a8281012fda830a6f7826d |FOR                  |biennale       |
|57a40fdfc0a8280e16dfcca39f580f7f |564a7c7ec0a8281012fda830a6f7826d |FOR                  |internationale |
|57a40fe0c0a8280e16dfcca330316476 |564a7c7ec0a8281012fda830a6f7826d |FOR                  |cirque         |
|57a40fe0c0a8280e16dfcca3b4e16de6 |564a7c7ec0a8281012fda830a6f7826d |FOR                  |arts           |
|---------------------------------|---------------------------------|---------------------|---------------|


Avec la version Java 5 :

Fri Jan 16 17:23:51 CET 2015 - WorkerThread#1[10.0.2.21:65514]
1. Total 6

Query Number : 20 - State : STATE_EXECUTE
SELECT ID_MOT_CLE, ID_RATTACHEMENT, CD_TYPE_RATTACHEMENT, VA_LIBELLE 
    FROM T_REF_MOT_CLE 
    WHERE ID_RATTACHEMENT = '564a7c7ec0a8281012fda830a6f7826d' AND 
        CD_TYPE_RATTACHEMENT = 'FOR';

|---------------------------------|---------------------------------|---------------------|---------------|
|ID_MOT_CLE                       |ID_RATTACHEMENT                  |CD_TYPE_RATTACHEMENT |VA_LIBELLE     |
|---------------------------------|---------------------------------|---------------------|---------------|
|57a40fdec0a8280e16dfcca34dafffde |564a7c7ec0a8281012fda830a6f7826d |FOR                  |biennale       |
|57a40fdfc0a8280e16dfcca39f580f7f |564a7c7ec0a8281012fda830a6f7826d |FOR                  |internationale |
|57a40fe0c0a8280e16dfcca330316476 |564a7c7ec0a8281012fda830a6f7826d |FOR                  |cirque         |
|57a40fe0c0a8280e16dfcca3b4e16de6 |564a7c7ec0a8281012fda830a6f7826d |FOR                  |arts           |
|---------------------------------|---------------------------------|---------------------|---------------|

fr.app.ti.common.dao.AbstractappJdbcDaoSupport.query(AbstractappJdbcDaoSupport.java:316)
fr.app.ti.reference.dao.impl.MotCleDAO.getListMotCle(MotCleDAO.java:223)
fr.app.ti.reference.process.GestionMotCleProcess.getMotClesFromIdRattachement(GestionMotCleProcess.java:233)
fr.app.ti.forfait.business.ForfaitBSBean.getMotsCles(ForfaitBSBean.java:241)

Avec la version JBoss 5 :

Fri Jan 16 17:23:51 CET 2015 - WorkerThread#1[10.0.2.21:65514]
1. Total 6

Query Number : 20 - State : STATE_EXECUTE
SELECT ID_MOT_CLE, ID_RATTACHEMENT, CD_TYPE_RATTACHEMENT, VA_LIBELLE 
    FROM T_REF_MOT_CLE 
    WHERE ID_RATTACHEMENT = '564a7c7ec0a8281012fda830a6f7826d' AND 
        CD_TYPE_RATTACHEMENT = 'FOR';

|---------------------------------|---------------------------------|---------------------|---------------|
|ID_MOT_CLE                       |ID_RATTACHEMENT                  |CD_TYPE_RATTACHEMENT |VA_LIBELLE     |
|---------------------------------|---------------------------------|---------------------|---------------|
|57a40fdec0a8280e16dfcca34dafffde |564a7c7ec0a8281012fda830a6f7826d |FOR                  |biennale       |
|57a40fdfc0a8280e16dfcca39f580f7f |564a7c7ec0a8281012fda830a6f7826d |FOR                  |internationale |
|57a40fe0c0a8280e16dfcca330316476 |564a7c7ec0a8281012fda830a6f7826d |FOR                  |cirque         |
|57a40fe0c0a8280e16dfcca3b4e16de6 |564a7c7ec0a8281012fda830a6f7826d |FOR                  |arts           |
|---------------------------------|---------------------------------|---------------------|---------------|

fr.app.spectacles.SpectaclesManager.getForfaitMotClee(SpectaclesManager.java:910)
fr.app.spectacles.SpectaclesManagerInstance.loadManifestation(SpectaclesManagerInstance.java:334)
fr.app.spectacles.SpectaclesManagerInstance.getManifestationById(SpectaclesManagerInstance.java:101)
fr.app.spectacles.SpectaclesManagerInstance.getManifestationByIdNoCache(SpectaclesManagerInstance.java:95)
fr.app.spectacles.SpectaclesManagerInstance.getManifestationById(SpectaclesManagerInstance.java:82)
fr.app.spectacles.SpectaclesManagerInstance$$FastClassByCGLIB$$dd8593a3.invoke(<generated>)
fr.app.spectacles.ManifestationCallPolicy.handleMethodCall(ManifestationCallPolicy.java:31)
Remote EJB : IndexationForfait
fr.app.ti.common.dao.AbstractappJdbcDaoSupport.query(AbstractappJdbcDaoSupport.java:316)
fr.app.ti.reference.dao.impl.MotCleDAO.getListMotCle(MotCleDAO.java:223)
fr.app.ti.reference.process.GestionMotCleProcess.getMotClesFromIdRattachement(GestionMotCleProcess.java:233)
fr.app.ti.forfait.business.ForfaitBSBean.getMotsCles(ForfaitBSBean.java:241)









A PLUS TARD




## Outil similaire
Ceci n'est pas un fork du projet existant log4jdbc2 : https://code.google.com/p/log4jdbc-log4j2
Mais néanmoins il est fortement inspiré dans l'idée.
log4jdbc2 répondait globalement à mon besoin, à 2 exceptions près.

### Les différentes api utilisées pour le logger :
Log4jdbc2 utilise Log4J2 ou SLF4J. Il est déjà un fork de log4jdbc-remix qui lui utilisait uniquement slf4j. 
Log4jdbc-Remix est lui un fork du projet d'origine Log4jdbc qui utilisait à son tour log4j.
Actuellement à chaque nouveau fork, l'ancienne version n'est plus maintenue.
Cela fait quelques années que j'utilise ces outils en passant de chaque nouvelle version et il répond absolumement à mon besoin.
Le seul soucis c'est qu'actuellement, je suis sur des projets contenant plusieurs applications différents développés dans des contextes différents.
Chaque projet utilisait une implémentation de logger différentes, de commons loggins, en passant par log4j, slf4j et des loggeur maisons !!!!!!!!!!
A un moment il a été assez fastidieux d'utiliser le log4jdbc car on etait obligé de mettre des bridges entres les différents logger.
C'est dans ce but que cet outil a été développé, permettre d'isoler la récupération des données et le traitement de ces données.
Ainsi chacun peut pour un besoin specifique implementer son propre traitement par exemple :
-Une implémentation sur la sortie standard.
-Une implémentation dans un fichier plat
-Une implémentation en utilisant jmx etc etc...

Il suffit juste d'implémenter une interface. (cette partie sera détaillé quand l'outil sera stable.)

### Une version unique pour toutes les versions.
Avec Log4Jdbc2 et les anciennes versions, il y a une version pour chaque version de java :
Java 5 - JDBC 3 donc log4jdbc-log4j2-jdbc3
Java 6 - JDBC 4 donc log4jdbc-log4j2-jdbc4
Java 7 - JDBC 4.1 donc log4jdbc-log4j2-jdbc4.1

Cela n'est pas trop dérangeant... sauf si vous etez sur un projet contenant plusieurs applications fonctionnant avec des versions de Java différentes :(
J'en ai profité pour utiliser des implémentations dynamiques donc il n'y a qu'un seul jar pour toutes les versions de Java et donc de JDBC à partir de Java 1.3.




## Les différentes implémentations du logger :
Actuellement il y a 3 implémentations de logger différentes :
-Pour Java 3
-Pour Java 5
-Pour Java 5 avec JBoss

###






- Il est fortement lié au logger utilisé, log4jdbc2 utilise Log4J2 ou SLF4J, cela était déja un fork de log4jdbc-remix qui utilise uniquement SLF4J qui



