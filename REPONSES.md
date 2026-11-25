# REPONSES AUX QUESTIONS DU TD - ShopFlow

### Q0.1 — Pourquoi interdit-on de pousser directement sur main ? Donnez deux raisons, dont une qui concerne le travail en binôme.
Pour la traçabilité du code : passer par une branche et une Pull Request impose un historique par fonctionnalité/étape comme on a pu le faire dans l'autre travaux de groupe. Et on est obligé de faire une vérification des changements du code pour éviter d'introduire des modifications de code cassées directement sur la branche de référence.

Travail en binôme : cela empêche d'écraser ou de faire entrer en conflit sans contrôle le travail de l'autre membre du binôme avant de fusionner sur main 


### Q0.2 — La collection Bruno est faite de fichiers texte rangés dans le dépôt. Qu'est-ce que cela change pour la relecture d'une Pull Request ?
Le relecteur peut voir exactement quelles requêtes ont été ajoutées ou modifiées et les exécuter localement après avoir récupéré la branche.

## Constater le problème
1. Arrêtez catalog-service (Ctrl+C dans son terminal).
2. Envoyez la requête Bruno de commande valide plusieurs fois. Notez le code HTTP et le temps de réponse affiché par Bruno.
temps de reponse : 10ms - 503 Service Unavailable  
{
  "detail": "Le catalogue est momentanément indisponible, réessayez plus tard",
  "status": 503,
  "title": "Catalogue indisponible",
  "type": "https://shopflow.example/problems/catalog-unavailable"
}

### Q1.1 — Ici la connexion est refusée tout de suite, donc la réponse est rapide. Imaginez maintenant que le catalogue soit allumé mais très lent. Que se passe-t-il pour chaque commande, compte tenu du read-timeout de 2 secondes ? Et si 200 clients commandent en même temps ?
Pour chaque commande, le thread d'order-service attend jusqu'à 2 secondes  avant qu'un délai d'expiration ne se déclenche, ce qui rend chaque création de commande lente.
Si 200 clients commandent en même temps, jusqu'à 200 requêtes peuvent rester bloquées en attente sur le catalogue en même temps.

### Q1.2 — Expliquez avec vos mots l'image du disjoncteur électrique : que protège-t-il, et quand se réarme-t-il ?
C'est un peu comme un disjoncteur electrique : quand il y a trop de courant ou un court-circuit, il coupe tout pour protéger l'installation et éviter qu'un problème n'abîme le reste. Là, c'est pareil : si le catalogue tombe en panne ou rame trop, le circuit breaker coupe les appels pour éviter de fatiguer order-service et de faire planter le reste.

### Q1.3 — Pourquoi placer le circuit breaker sur `CatalogGateway` plutôt que sur `OrderService.place` ?
Parce que `CatalogGateway` sert uniquement à appeler le service catalogue, alors que `OrderService.place` s'occupe de créer la commande.

### Q1.4 — Pourquoi un appel interne direct (`this.getActiveProduct(...)`) ne déclenche-t-il pas `@CircuitBreaker` ni `@Retry` ?
Pour faire fonctionner `@CircuitBreaker` et `@Retry`, Spring crée un intermédiaireautour de la classe. Si une méthode appelle une autre méthode de la même classe avec `this.`, elle passe en direct sans traverser cet intermédiaire, donc Resilience4j n'est pas déclenché.

### Q1.5 — Un produit inconnu (404 / 422) doit-il compter comme un échec du circuit breaker ? Comment l'éviter ?
Non : le serveur catalogue fonctionne bien, c'est juste le client qui a demandé un mauvais numéro de produit. Si on le comptait comme un échec, le circuit se couperait pour tout le monde sans raison. Pour éviter ça, on ajoute `InvalidOrderException` dans `ignore-exceptions` dans le fichier `application.yml`.

### Q1.6 — Pourquoi est-il sûr de retenter un `GET` vers le catalogue, alors qu'un `POST` de paiement demanderait une précaution supplémentaire ?
Un `GET` ne fait que lire des informations. En revanche, un `POST` de paiement sert à débiter de l'argent : si la réponse met trop de temps à revenir mais que le paiement a quand même été validé, retenter automatiquement risquerait de faire payer le client deux fois.

### Q1.7 — Combien d'appels partent réellement vers `catalog-service` lors d'une panne, avant puis après l'ouverture du circuit ?
- **Avant l'ouverture du circuit (`CLOSED`) :** jusqu'à **3 appels** par commande (1 essai normal + 2 retries grâce à `max-attempts: 3`).
- **Après l'ouverture du circuit (`OPEN`) :** **0 appel** vers le catalogue, le circuit bloque tout de suite et renvoie directement l'erreur `503`.

### Q1.8 — Si un problème est détecté pendant la relecture de la Pull Request, faut-il fermer la PR et en ouvrir une nouvelle ?
Non, il ne faut pas la fermer. On corrige simplement le code sur son ordinateur (sur la même branche `feature/1-resilience`), puis on refait `git commit` et `git push` : la Pull Request sur GitHub se met à jour toute seule.