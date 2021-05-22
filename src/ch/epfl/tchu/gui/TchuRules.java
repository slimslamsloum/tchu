package ch.epfl.tchu.gui;

public class TchuRules {

    public final static String TITLE_INTRO_TEXT = "Alors comment on joue? \n \n";

    public final static String INTRO_TEXT = "Une partie de tCHu se joue sur une carte de la Suisse et des pays voisins. Un certain nombre de villes, toutes suisses, sont nommées sur cette carte. Elles sont reliées entre elles par un réseau ferroviaire représenté par des cases rectangulaires colorées placées les unes derrière les autres. Ce réseau s'étend également aux pays voisins — Allemagne, Autriche, Italie et France — mais les villes des pays voisins ne sont pas nommées et sont simplement représentées par un drapeau du pays.\n" +
            "\n" +
            "Le jeu se joue en 1V1 au tour par tour, et le but est a la fin de la partie d’avoir plus de points que son adversaire. \n" +
            "Pour avoir des points, on peut s’emparer d’une route, valider un ticket que l’on possède au préalable (s’emparer des routes nécessaires pour relier les deux gares sur mentionnées sur le ticket), et à la fin de la partie, posséder le chemin le plus long. \n \n ";


    public final static String TITLE_TERMINOLOGY = "Terminologie: \n \n";

    public final static String TERMINOLOGY = "Route: route entre deux gares dont on peut s’emparer a l’aide de wagons et de cartes. Une route est soit au dessus du sol, soit en dessous (= tunnel). Pour s’emparer d’une route, il faut un nombre de wagons et de cartes égal a la longueur de la route. Si la route est au dessus sol, il faut des cartes de la meme couleur que la route (si la route est neutre, n’importe quelle couleur suffit mais les cartes doivent toutes être de la meme couleur). Si la route est un tunnel, on peut utiliser des cartes de couleur ainsi que des cartes locomotives.\n" +
            "\n" +
            "Cartes: Permettent de s’emparer d’une route. Elles ont soit une couleur X(pour s’emparer d’un tunnel ou d’une route classique, de couleur X ou neutre), soit elles sont de type locomotive (permettent de s’emparer d’un tunnel de n’importe quelle couleur). \n" +
            "\n" +
            "Ticket: montre deux stations a lier. Si le joueur arrive avant la fin de la partie a lier ces deux stations en s’emparant des routes nécessaire, il gagne le nombre de points qui s’affiche sur le ticket et les perd dans le cas contraire. \n" +
            "\n" +
            "Wagon: chaque joueur en possède 40 en debut de partie, ils sont indispensable pour s’emparer d’une route. \n \n ";

    public final static String TITLE_BEGINNING_GAME = "Début de partie: \n \n";

    public final static String BEGINNING_GAME = "Chaque joueur choisit parmi 5 tickets, au moins 3 tickets. Il aura deja reçu au préalable des cartes, qu’il peut visionner en bas de la fenêtre de jeu.\n \n";

    public final static String TITLE_TURN_KIND = "Tour d'un joueur:\n \n";

    public final static String TURN_KIND = "Le joueur a le choix entre 3 actions: piocher des tickets, s’emparer d’une route, piocher des cartes.\n" +
            "\n" +
            "Si il choisit de prendre des tickets, il peut choisir autant de billets qu’il veut parmi les 3 qui s’affichent.\n" +
            "\n" +
            "Si il choisit de prendre des cartes, il peut soit prendre une carte de la pile de cartes, soit prendre une carte face visible (a droite de la fenêtre). Il répète ensuite cette meme action.\n" +
            "\n" +
            "Si il choisit de s’emparer d’une route (pas tunnel) et qu’il a les cartes/wagons nécessaires, il s’en empare.\n" +
            "\n" +
            "Si il choisit de s’emparer d’un tunnel et qu’il a les cartes/wagons nécessaires:\n" +
            "-les 3 premieres cartes de la pile sont montrées. Pour chaque carte qui est identique a une carte utilisée pour tenter de s’emparer du tunnel, le joueur doit bruler encore une fois cette meme carte. Si l’une de ces cartes est une locomotive. Le joueur doit bruler une locomotive. Si le joueur n’a pas les cartes nécessaires, il ne peut pas s’emparer du tunnel et son tour est passe.\n \n";

    public final static String TITLE_END_GAME = "Fin de partie:\n \n";

    public final static String END_GAME = "Des que un joueur a deux wagons ou moins, chaque joueur joue une dernière fois. Lorsque la partie se termine, les points des tickets et des routes sont comptes. Le joueur qui possède le chemin le plus emporte également 10 points en plus. Si les deux joueurs ont tous les deux des chemins aussi longs, les deux joueurs emportent 10 points chacun.";

}
