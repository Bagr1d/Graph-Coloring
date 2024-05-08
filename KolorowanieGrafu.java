import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class KolorowanieGrafu {

    public static void main(String[] args) {
        try {
            // Podaj ścieżkę do pliku z grafem
            String filePath = "r125.1.col.txt";

            // Wczytaj graf z pliku
            int[][] graf = wczytajGrafZPliku(filePath);

            // Ustaw liczbę kolorów
            int liczbaKolorow = 48;

            // Znajdź ostateczne kolorowanie za pomocą algorytmu lokalnego przeszukiwania
            int[] rozwiazanie = localSearch(graf, liczbaKolorow);

            // Wyświetl wynik
            System.out.println("Ostateczne kolorowanie:");
            for (int i = 0; i < rozwiazanie.length; i++) {
                System.out.println("Wierzchołek " + (i + 1) + ": Kolor " + rozwiazanie[i]);
            }

            // Policzenie liczby konfliktów w ostatecznym rozwiązaniu
            int liczbaKonfliktow = policzKonflikty(graf, rozwiazanie);
            System.out.println("Liczba konfliktów w ostatecznym rozwiązaniu: " + liczbaKonfliktow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda wczytująca graf z pliku
    public static int[][] wczytajGrafZPliku(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String linia;
        int liczbaWierzcholkow = 0;
        int liczbaKrawedzi = 0;
        boolean czytanieKrawedzi = false;

        // Wczytanie informacji o grafie z pliku
        while ((linia = reader.readLine()) != null) {
            if (linia.startsWith("p col")) {
                String[] fragmenty = linia.trim().split("\\s+");
                liczbaWierzcholkow = Integer.parseInt(fragmenty[2]);
                liczbaKrawedzi = Integer.parseInt(fragmenty[3]);
                czytanieKrawedzi = true;
                break;
            }
        }

        int[][] graf = new int[liczbaWierzcholkow][liczbaWierzcholkow];

        // Wczytanie połączeń między wierzchołkami z pliku
        while ((linia = reader.readLine()) != null && czytanieKrawedzi) {
            if (linia.startsWith("e")) {
                String[] fragmenty = linia.trim().split("\\s+");
                int wierzcholek1 = Integer.parseInt(fragmenty[1]) - 1;
                int wierzcholek2 = Integer.parseInt(fragmenty[2]) - 1;

                graf[wierzcholek1][wierzcholek2] = 1;
                graf[wierzcholek2][wierzcholek1] = 1;
            }
        }

        reader.close();
        return graf;
    }

    // Metoda lokalnego przeszukiwania
    public static int[] localSearch(int[][] graf, int liczbaKolorow) {
        int liczbaWierzcholkow = graf.length;

        // Inicjalizacja kolorowania wierzchołków
        int[] rozwiazanie = new int[liczbaWierzcholkow];
        Random losowy = new Random();
        for (int i = 0; i < liczbaWierzcholkow; i++) {
            rozwiazanie[i] = losowy.nextInt(liczbaKolorow); // Losowe przypisanie koloru do każdego wierzchołka
        }

        // Ocena początkowego rozwiązania
        int poczatkowyKoszt = ocenKoszt(graf, rozwiazanie);

        // Iteracje lokalnego przeszukiwania
        int maksymalnaLiczbaIteracji = 1000;
        for (int iteracja = 0; iteracja < maksymalnaLiczbaIteracji; iteracja++) {
            // Przechodzenie przez wszystkie wierzchołki grafu
            for (int wierzcholek = 0; wierzcholek < liczbaWierzcholkow; wierzcholek++) {
                // Losowe wybranie nowego koloru dla aktualnego wierzchołka
                int losowyKolor = losowy.nextInt(liczbaKolorow);
                rozwiazanie[wierzcholek] = losowyKolor;

                // Ocena tymczasowego rozwiązania
                int kosztTymczasowegoRozwiazania = ocenKoszt(graf, rozwiazanie);

                // Jeśli tymczasowe rozwiązanie jest lepsze, zaakceptuj je
                if (kosztTymczasowegoRozwiazania < poczatkowyKoszt) {
                    poczatkowyKoszt = kosztTymczasowegoRozwiazania;
                    // Przerwij iterację i przejdź do kolejnego wierzchołka
                    break;
                } else {
                    // Jeśli tymczasowe rozwiązanie jest gorsze, przywróć poprzedni kolor wierzchołka
                    rozwiazanie[wierzcholek] = rozwiazanie[wierzcholek];
                }
            }
        }

        return rozwiazanie; // Zwróć ostateczne kolorowanie grafu
    }


    // Metoda oceny kosztu
    public static int ocenKoszt(int[][] graf, int[] rozwiazanie) {
        int koszt = 0;
        for (int i = 0; i < graf.length; i++) {
            for (int j = 0; j < graf.length; j++) {
                if (graf[i][j] == 1 && rozwiazanie[i] == rozwiazanie[j]) {
                    koszt++;
                }
            }
        }
        return koszt / 2;
    }

    // Metoda zliczająca konflikty
    public static int policzKonflikty(int[][] graf, int[] rozwiazanie) {
        int liczbaKonfliktow = 0;
        for (int i = 0; i < graf.length; i++) {
            for (int j = 0; j < graf.length; j++) {
                if (graf[i][j] == 1 && rozwiazanie[i] == rozwiazanie[j]) {
                    liczbaKonfliktow++;
                }
            }
        }
        return liczbaKonfliktow / 2;
    }
}