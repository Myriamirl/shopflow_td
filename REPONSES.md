# REPONSES AUX QUESTIONS DU TD - ShopFlow

## Q0.1 — Pourquoi interdit-on de pousser directement sur main ? Donnez deux raisons, dont une qui concerne le travail en binôme.
Pour la traçabilité du code : passer par une branche et une Pull Request impose un historique par fonctionnalité/étape comme on a pu le faire dans l'autre travaux de groupe. Et on est obligé de faire une vérification des changements du code pour éviter d'introduire des modifications de code cassées directement sur la branche de référence.

Travail en binôme : cela empêche d'écraser ou de faire entrer en conflit sans contrôle le travail de l'autre membre du binôme avant de fusionner sur main 


## Q0.2 — La collection Bruno est faite de fichiers texte rangés dans le dépôt. Qu'est-ce que cela change pour la relecture d'une Pull Request ?
Le relecteur peut voir exactement quelles requêtes ont été ajoutées ou modifiées et les exécuter localement après avoir récupéré la branche.