# pc-2022 Activity #1
## case0 centralized

Per la realizzazione della Smart Room centralizzata vengono utilizzate due versioni (presenti nel file 'CentralizedSingleBoardSimulator'):

1) SUPER LOOP: Un unico ciclo infinito in cui vengono svolte le logiche della simulazione;

2) EVENT LOOP: La simulazione si registra agli eventi e, quando ne rileva alcuni, agisce in base ad essi.

Il comportamento in entrambi i casi è il seguente:
- se non viene rilevato nessuno nella stanza, allora le luci rimangono spente;
- altrimenti se qualcuno è presente nella stanza, allora le luci si accendono solo quando la luminosità del luogo è inferiore ad un certo valore (T_VALUE).