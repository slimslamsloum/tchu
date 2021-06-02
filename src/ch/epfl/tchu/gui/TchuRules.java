package ch.epfl.tchu.gui;

public class TchuRules {

    public final static String TITLE_INTRO_TEXT = "     Comment jouer à tChu? \n \n";

    public final static String INTRO_TEXT = "Une partie de tCHu se déroule sur la carte de la Suisse et ses pays voisins.\n" +
            "Certaines villes suisses sont nommées et visibles sur cette carte.\n " +
            "Elles sont toutes reliées entre elles par un réseau ferroviaire, représenté par des cases rectangulaires colorées placées les unes derrière les autres.\n" +
            "Ce réseau s'étend également aux pays voisins — Allemagne, Autriche, Italie et France — mais les villes des pays voisins \n ne sont pas nommées et sont simplement représentées par un drapeau du pays.\n" +
            "\n" +
            "Le jeu se joue en 1 contre 1. Les joueurs jouent tour à tour, durant lesquels ils doivent choisir entre l'une des trois actions effectuables : piocher une carte, tirer un ticket ou s'emparer d'une nouvelle route.\n" +
            " Chaque route dont le joueur s'empare lui rapporte un certain nombre de points, et il en va de même pour chaque ticket qu'il réussit à valider. \n" +
            "À la fin de la partie, le joueur possédant le chemin le plus long reçoit un bonus de 10 points et celui ayant obtenu le plus grand nombre de points gagne.  \n \n";


    public final static String TITLE_TERMINOLOGY = " Terminologie: \n \n";

    public final static String TERMINOLOGY = "Wagon: chaque joueur en possède 40 en debut de partie, ils sont indispensables pour s’emparer d’une route.\n" +
            " Si un joueur possède moins de trois wagons, son dernier tour commence \n" +
            "\n" +
            "Cartes: Permettent de s’emparer d’une route. Soit elles ont une couleur X(pour s’emparer d’un tunnel ou d’une route classique, de couleur X ou neutre),\n" +
            " soit elles sont de type locomotive (permettent de s’emparer d’un tunnel de n’importe quelle couleur). \n" +
            "\n" +
            "Ticket: Donne au joueur l'objectif de relier les deux stations qui compose le ticket.\n" +
            " Si le joueur arrive avant la fin de la partie à lier ces deux stations en s’emparant des routes nécessaire, il gagne le nombre de points qui s’affiche sur le ticket et les perd dans le cas contraire. \n" +
            "\n" +
            "Route: route entre deux gares dont on peut s’emparer a l’aide de wagons et de cartes. Une route est soit au dessus du sol, soit en dessous (= tunnel).\n" +
            "Pour s’emparer d’une route, il faut un nombre de wagons et de cartes égal a la longueur de la route.\n " +
            "Si la route est au dessus sol, il faut des cartes de la meme couleur que la route (si la route est neutre,  n’importe quelle couleur suffit mais les cartes doivent toutes être de la meme couleur).\n" +
            "Si la route est un tunnel, on peut utiliser des cartes de couleur ainsi que des cartes locomotives.\n \n ";

    public final static String TITLE_BEGINNING_GAME = " Début de partie: \n \n";

    public final static String BEGINNING_GAME = "Chaque joueur choisit parmi 5 tickets, au moins 3 tickets. Il aura deja reçu au préalable des cartes, qu’il peut visionner en bas de la fenêtre de jeu.\n \n";

    public final static String TITLE_TURN_KIND = " Tour d'un joueur:\n \n";

    public final static String TURN_KIND = "Le joueur a le choix entre 3 actions: tirer des tickets, s’emparer d’une route, piocher des cartes.\n" +
            "\n" +
            "S'il choisit de prendre des tickets, il peut choisir autant de billets qu’il veut parmi les 3 qui s’affichent.\n" +
            "\n" +
            "S'il choisit de prendre des cartes, il peut soit prendre une carte de la pile de cartes, soit prendre une carte face visible (a droite de la fenêtre). Il répète ensuite cette meme action.\n" +
            "\n" +
            "S'il choisit de s’emparer d’une route (pas tunnel) et qu’il a les cartes/wagons nécessaires, il s’en empare.\n" +
            "\n" +
            "S'il choisit de s’emparer d’un tunnel et qu’il a les cartes/wagons nécessaires: les 3 premieres cartes de la pile sont montrées.\n" +
            " Pour chaque carte qui est identique a une carte utilisée pour tenter de s’emparer du tunnel, le joueur doit rejouer encore une fois une carte du même type ou de type locomotive.\n " +
            "Si l’une de ces cartes est une locomotive, le joueur doit utiliser une locomotive.\n " +
            "Si le joueur n’a pas les cartes nécessaires, il ne peut pas s’emparer du tunnel et doit passer son tour.\n \n";

    public final static String TITLE_END_GAME = " Fin de partie:\n \n";

    public final static String END_GAME = "Dès que un joueur possède moins de trois wagons, chaque joueur joue une dernière fois. Lorsque la partie se termine, les points des tickets et des routes sont comptés.\n" +
            " Le joueur qui possède le plus long chemin remporte 10 points supplémentaires.\n" +
            " Si les plus long chemins des deux joueurs sont de tailles équivalentes, les deux joueurs remportent 10 points chacun.\n";

}