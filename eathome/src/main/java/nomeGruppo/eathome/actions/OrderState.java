package nomegruppo.eathome.actions;

/*Contiene i possibili stati di un ordinazione.
In ordine:
- in attesa di conferma
- confermato
- rifiutato
- in consegna
- consegnato
 */
public enum OrderState {
    PENDING_CONFIRMATION, CONFIRMED, REJECTED, DELIVERING, DELIVERED
}
