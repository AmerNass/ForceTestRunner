
=============================================================================
====================          ForceTestRunner            ====================
=============================================================================

  Qu'est ce que c'est?
  --------------------

  ForceTestRunner est un outil basé sur SOAP API, qui permet de 
  lancer les tests unitaires et générer un résultat dans lequel
  il décrit l'état des test (nombre des tests qui échouent, temps
  des tests, pourcentage de couverture...) en plus des informations 
  détaillés sur la cause des erreurs.


  Installation
  ------------

  Il y a rien à installer pour lancer ForceTestRunner, il suffit
  d'avoir la derniere version de la JRE.


  Utilisation
  ------------

  L'utilisation de ForceTestRunner est facile, il suffit de fournir
  des données valides dans le fichier de configuration.

  * On commence par créer un fichier de config nommé " config.properties".
	voila un template du fichier config.properties :
USERNAME=simpleUser@salesforce.com

PASSWORD=strongPASSWORS1234

TOKEN=JUSjskjskzs2Z2NZ2LK2KJJ

ENDPOINT=https://yourLink.salesforce.com/services/Soap/c/30.0

MAILSLIST=admin1@company.com;admin2@company.com;admin3@company.com

USERNAME : le nom d'utilisateur utilisé pour acceder a salesForce
PASSWORD : votre mot de passe
TOKEN    : votre jeton de sécurité
ENDPOINT : le lien utilisé pour adressé les requete soap (ce qui change
           c'est juste 'yourlink')
MAILS    : la liste des courriels des personnes qu'on souhaite notifié séparé
           par des ' ; '


   * Apres la création du fichier de configuration, on peut lancer le
     programme avec la commande suivante : (il faut etre dans le meme repertoir)

java -jar ForceTestRunner.jar --chemin de notre fichier de config--

   un fichier excel va etre générer dans le meme repertoir ou notre fichier jar
   est situé et un mail sera envoyé a tous les destinataires présent dans la partie
   MAILS.




  Contacts
  --------


