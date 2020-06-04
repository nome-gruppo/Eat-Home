package nomeGruppo.eathome.utility;

public class City {

    private final String[] listCity;

    public City() {
        String city = "Accadia\n" +
                "Acquaviva delle Fonti\n" +
                "Adelfia\n" +
                "Alberobello\n" +
                "Alberona\n" +
                "Alessano\n" +
                "Alezio\n" +
                "Alliste\n" +
                "Altamura\n" +
                "Andrano\n" +
                "Andria\n" +
                "Anzano di Puglia\n" +
                "Apricena\n" +
                "Aradeo\n" +
                "Arnesano\n" +
                "Ascoli Satriano\n" +
                "Avetrana\n" +
                "Bagnolo del Salento\n" +
                "Bari\n" +
                "Barletta\n" +
                "Biccari\n" +
                "Binetto\n" +
                "Bisceglie\n" +
                "Bitetto\n" +
                "Bitonto\n" +
                "Bitritto\n" +
                "Botrugno\n" +
                "Bovino\n" +
                "Brindisi\n" +
                "Cagnano Varano\n" +
                "Calimera\n" +
                "Campi Salentina\n" +
                "Candela\n" +
                "Cannole\n" +
                "Canosa di Puglia\n" +
                "Caprarica di Lecce\n" +
                "Capurso\n" +
                "Carapelle\n" +
                "Carlantino\n" +
                "Carmiano\n" +
                "Carosino\n" +
                "Carovigno\n" +
                "Carpignano Salentino\n" +
                "Carpino\n" +
                "Casalnuovo Monterotaro\n" +
                "Casalvecchio di Puglia\n" +
                "Casamassima\n" +
                "Casarano\n" +
                "Cassano delle Murge\n" +
                "Castellana Grotte\n" +
                "Castellaneta\n" +
                "Castelluccio dei Sauri\n" +
                "Castelluccio Valmaggiore\n" +
                "Castelnuovo della Daunia\n" +
                "Castri di Lecce\n" +
                "Castrignano de' Greci\n" +
                "Castrignano del Capo\n" +
                "Castro\n" +
                "Cavallino\n" +
                "Ceglie Messapica\n" +
                "Celenza Valfortore\n" +
                "Cellamare\n" +
                "Celle di San Vito\n" +
                "Cellino San Marco\n" +
                "Cerignola\n" +
                "Chieuti\n" +
                "Cisternino\n" +
                "Collepasso\n" +
                "Conversano\n" +
                "Copertino\n" +
                "Corato\n" +
                "Corigliano d'Otranto\n" +
                "Corsano\n" +
                "Crispiano\n" +
                "Cursi\n" +
                "Cutrofiano\n" +
                "Deliceto\n" +
                "Diso\n" +
                "Erchie\n" +
                "Faeto\n" +
                "Faggiano\n" +
                "Fasano\n" +
                "Foggia\n" +
                "Fragagnano\n" +
                "Francavilla Fontana\n" +
                "Gagliano del Capo\n" +
                "Galatina\n" +
                "Galatone\n" +
                "Gallipoli\n" +
                "Ginosa\n" +
                "Gioia del Colle\n" +
                "Giovinazzo\n" +
                "Giuggianello\n" +
                "Giurdignano\n" +
                "Gravina in Puglia\n" +
                "Grottaglie\n" +
                "Grumo Appula\n" +
                "Guagnano\n" +
                "Ischitella\n" +
                "Isole Tremiti\n" +
                "Laterza\n" +
                "Latiano\n" +
                "Lecce\n" +
                "Leporano\n" +
                "Lequile\n" +
                "Lesina\n" +
                "Leverano\n" +
                "Lizzanello\n" +
                "Lizzano\n" +
                "Locorotondo\n" +
                "Lucera\n" +
                "Maglie\n" +
                "Manduria\n" +
                "Manfredonia\n" +
                "Margherita di Savoia\n" +
                "Martano\n" +
                "Martignano\n" +
                "Martina Franca\n" +
                "Maruggio\n" +
                "Massafra\n" +
                "Matino\n" +
                "Mattinata\n" +
                "Melendugno\n" +
                "Melissano\n" +
                "Melpignano\n" +
                "Mesagne\n" +
                "Miggiano\n" +
                "Minervino di Lecce\n" +
                "Minervino Murge\n" +
                "Modugno\n" +
                "Mola di Bari\n" +
                "Molfetta\n" +
                "Monopoli\n" +
                "Monte Sant'Angelo\n" +
                "Monteiasi\n" +
                "Monteleone di Puglia\n" +
                "Montemesola\n" +
                "Monteparano\n" +
                "Monteroni di Lecce\n" +
                "Montesano Salentino\n" +
                "Morciano di Leuca\n" +
                "Motta Montecorvino\n" +
                "Mottola\n" +
                "Muro Leccese\n" +
                "Nardò\n" +
                "Neviano\n" +
                "Noci\n" +
                "Nociglia\n" +
                "Noicattaro\n" +
                "Novoli\n" +
                "Ordona\n" +
                "Oria\n" +
                "Orsara di Puglia\n" +
                "Orta Nova\n" +
                "Ortelle\n" +
                "Ostuni\n" +
                "Otranto\n" +
                "Palagianello\n" +
                "Palagiano\n" +
                "Palmariggi\n" +
                "Palo del Colle\n" +
                "Panni\n" +
                "Parabita\n" +
                "Patù\n" +
                "Peschici\n" +
                "Pietramontecorvino\n" +
                "Poggiardo\n" +
                "Poggio Imperiale\n" +
                "Poggiorsini\n" +
                "Polignano a Mare\n" +
                "Porto Cesareo\n" +
                "Presicce-Acquarica\n" +
                "Pulsano\n" +
                "Putignano\n" +
                "Racale\n" +
                "Rignano Garganico\n" +
                "Roccaforzata\n" +
                "Rocchetta Sant'Antonio\n" +
                "Rodi Garganico\n" +
                "Roseto Valfortore\n" +
                "Ruffano\n" +
                "Rutigliano\n" +
                "Ruvo di Puglia\n" +
                "Salice Salentino\n" +
                "Salve\n" +
                "Sammichele di Bari\n" +
                "San Cassiano\n" +
                "San Cesario di Lecce\n" +
                "San Donaci\n" +
                "San Donato di Lecce\n" +
                "San Ferdinando di Puglia\n" +
                "San Giorgio Ionico\n" +
                "San Giovanni Rotondo\n" +
                "San Marco in Lamis\n" +
                "San Marco la Catola\n" +
                "San Marzano di San Giuseppe\n" +
                "San Michele Salentino\n" +
                "San Nicandro Garganico\n" +
                "San Pancrazio Salentino\n" +
                "San Paolo di Civitate\n" +
                "San Pietro in Lama\n" +
                "San Pietro Vernotico\n" +
                "San Severo\n" +
                "San Vito dei Normanni\n" +
                "Sanarica\n" +
                "Sannicandro di Bari\n" +
                "Sannicola\n" +
                "Sant'Agata di Puglia\n" +
                "Santa Cesarea Terme\n" +
                "Santeramo in Colle\n" +
                "Sava\n" +
                "Scorrano\n" +
                "Seclì\n" +
                "Serracapriola\n" +
                "Sogliano Cavour\n" +
                "Soleto\n" +
                "Specchia\n" +
                "Spinazzola\n" +
                "Spongano\n" +
                "Squinzano\n" +
                "Statte\n" +
                "Sternatia\n" +
                "Stornara\n" +
                "Stornarella\n" +
                "Supersano\n" +
                "Surano\n" +
                "Surbo\n" +
                "Taranto\n" +
                "Taurisano\n" +
                "Taviano\n" +
                "Terlizzi\n" +
                "Tiggiano\n" +
                "Torchiarolo\n" +
                "Toritto\n" +
                "Torre Santa Susanna\n" +
                "Torremaggiore\n" +
                "Torricella\n" +
                "Trani\n" +
                "Trepuzzi\n" +
                "Tricase\n" +
                "Triggiano\n" +
                "Trinitapoli\n" +
                "Troia\n" +
                "Tuglie\n" +
                "Turi\n" +
                "Ugento\n" +
                "Uggiano la Chiesa\n" +
                "Valenzano\n" +
                "Veglie\n" +
                "Vernole\n" +
                "Vico del Gargano\n" +
                "Vieste\n" +
                "Villa Castelli\n" +
                "Volturara Appula\n" +
                "Volturino\n" +
                "Zapponeta\n" +
                "Zollino\n";
        this.listCity = city.split("\n");
    }

    public String[] getListCity() {
        return listCity;
    }
}

