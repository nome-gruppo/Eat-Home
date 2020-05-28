package nomeGruppo.eathome.actions;

/*Contiene i possibili stati di un ordinazione.
In ordine:
- in attesa di conferma
- confermato
- rifiutato
- in consegna
- consegnato
 */
//TODO controlla
public enum OrderState {
    PENDING_CONFIRMATION, CONFIRMED, REJECTED, DELIVERING, DELIVERED
}
