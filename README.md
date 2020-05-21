# Rozwiązania zadań z JNP2:Android

Rozwiązania wszystkich zadań są napisane w Kotlinie. Bardzo możliwe, że do pisania zaliczeniowej aplikacji też użyję tego repozytorium, ale jeszcze nie jestem pewien.

Ponieważ kolejne zadania czasami polegają na modyfikacji poprzednich, to będę się starał dla każdego zadania tworzyć odpowiednio opisane tagi wskazujące na właściwą wersję repozytorium.

## Zadanie 1

Rozwiązanie jest w katalogu `Task-01` w wersji otagowanej `task01`.

## Zadanie 2

Rozwiązanie jest w katalogu `Task-02` w wersji otagowanej `task02`.

## Zadanie 3

Rozwiązanie jest w katalogu `Task-03` w wersji otagowanej `task03`.

W rozwiązaniu nie użyłem `AsyncTask`, bo widziałem w dokumentacji, że w jakiejś (dość odległej) wersji API ma być deprecated. Zamiast tego użyłem wbudowanych w Kotlina coroutines. O ile zrozumiałem dokumentację to w tym momencie kod działa tak, że tworzę bardzo dużo zatrzymywanych funkcji (cały czas mowa o "coroutines", których nie umiem przetłumaczyć na polski). One są uruchamiane przed domyślny dispatcher, który uruchamia je na tylu wątkach, ile jest dostępnych rdzeni, ale nie mniej niż na dwóch (linijki 42 i 53 w `Simulation.kt`). Wątki przekazują informację o zmianach stanu symulacji, wysyłając wiadomości za do Handlerów. Czy to jest poprawne ich użycie?

## Zadanie 4

Już wersja pierwsza trzyma notatki w jednym pliku. Generowanie losowych identyfikatorów i otwieranie wielu plików chyba nie jest aż tak ciekawe ani pouczające, żeby było sens to modyfikować wersję z zadania pierwszego.

## Zadanie 5



