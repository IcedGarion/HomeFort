# HomeFort
UniUpo VC Reti2 Sperimentazioni project:
IOT system for home confort


DETTAGLI
-lightControl guarda solo se accendere le luci in una certa ora della giornata (controlla file)
agisce solo se autoMode � a true; se si cambia light power, lightControl mantiene quello. Se invece la luce � spenta all'inizio, lightControl la accende al max
Se un utente accende o spegne la luce (non lightsPower), allora automode va ad off.

-ComfortControl decide solo temperatura e liminosita' (/colore) delle luci. Quindi se autoMode � off non agisce sulle luci!
Modifica luci solo se sono gi� accese (dall'utente oppure da lightControl (cio� autoMode on))
Se lightControl sta accendendo le luci, Comfort modifica queste impostando potenza e colore (perch� automode � on)