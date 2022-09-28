# pc-2022 Activity #1 
## case1 distributed

Per la realizzazione della Smart Room distribuita sono stati ideati tre agenti che comunicano tra loro attraverso l'utilizzo di **MQTT**: 'LumSensorDeviceAgent', 'PresDetectDeviceAgent', 'LightDeviceAgent'.

Essi vengono istanziati ciscuno con un rispettivo Verticle di Vertx (nel file 'DistributedSmartRoom').

Per poter comunicare tra loro, gli agenti si connettono allo stesso _host_, alla stessa _porta_ e con lo stesso _topic_.

Il 'LumSensorDeviceAgent' ha il compito di mandare come messaggio il valore della luminosità rilevata nella stanza.

Il 'PresDetectDeviceAgent' spedisce il messaggio nel momento in cui viene rilevato qualcuno nella stanza e quando questo esce (cioè non si rileva più nessuno all'interno del luogo).

Il 'LightDeviceAgent' riceve dagli altri due agenti i rispettivi messaggi e agisce di conseguenza:
- se non viene rilevato nessuno nella stanza, allora le luci rimangono spente;
- altrimenti se qualcuno è presente nella stanza, allora le luci si accendono solo quando la luminosità del luogo è inferiore ad un certo valore (T_VALUE).