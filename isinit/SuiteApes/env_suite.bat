set CLASSPATH=%1;lib/2xmi.jar;lib/JSX.jar;lib/Tidy.jar;lib/jgraph.jar;lib/ipsquad-2.5.2.jar;lib/avalon-framework-4.2.0.jar;lib/fop.jar;lib/batik-1.5.1.jar;lib/JimiProClasses.zip;lib/lucene-1.4.3.jar

:: CLASSPATH : d�crit tous les chemins o� Java doit aller chercher des packages
:: cette variable doit �tre enrichie lorsque de nouvelles librairies sont ajout�es

set JAVA_HOME=C:\JAVA\jre\bin
set PATH=%JAVA_HOME%;%PATH%

:: JAVA_HOME : r�pertoire o� se trouvent les ex�cutables Java
:: cette variable doit �tre mise � jour suivant le poste d'ex�cution

start javaw -Xmx128m -classpath %CLASSPATH% %2