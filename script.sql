-- Trigger pour définir le nom par défaut d'une table si non renseigné
CREATE OR REPLACE TRIGGER trg_nom_defaut
   BEFORE INSERT
   ON TABLES_RESTO
   FOR EACH ROW
BEGIN
   IF :NEW.nom IS NULL THEN
      :NEW.nom := 'Table ' || TO_CHAR(:NEW.numTable);
   END IF;
END;


-- Requête pour afficher les tables disponibles d'un restaurant à une heure donnée
SELECT NUMTABLE, IDRESTO, TABLES_RESTO.NOM
FROM TABLES_RESTO
        INNER JOIN RESTAURANTS ON RESTAURANTS.ID = TABLES_RESTO.IDRESTO
WHERE IDRESTO = 21
  AND TO_DATE('13:00', 'HH24:MI') between TO_DATE(HEUREOUVERTURE, 'HH24:MI') and TO_DATE(HEUREFERMETURE, 'HH24:MI')
MINUS
SELECT TABLES_RESTO.NUMTABLE, IDRESTO, NOM
FROM TABLES_RESTO
        INNER JOIN RESERVATIONS ON RESERVATIONS.NUMTABLE = TABLES_RESTO.NUMTABLE
WHERE TO_TIMESTAMP('2025-06-11 12:30:00', 'YYYY-MM-DD HH24:MI:SS') BETWEEN HEUREDEBUT AND HEUREFIN;


-- Procédure pour réserver une table
CREATE OR REPLACE PROCEDURE reserver_table(
   p_numtable IN NUMBER,
   p_nomclient IN VARCHAR2,
   p_prenomclient IN VARCHAR2,
   p_nbconvives IN NUMBER,
   p_telephone IN VARCHAR2,
   p_heuredebut IN TIMESTAMP,
   p_heurefin IN TIMESTAMP
) AS
   v_count INTEGER;
BEGIN
   -- Vérifie que la table est disponible
   SELECT COUNT(*)
   INTO v_count
   FROM TABLES_RESTO
   WHERE NUMTABLE = p_numtable
     AND NOT EXISTS (SELECT 1
                     FROM RESERVATIONS
                     WHERE NUMTABLE = p_numtable
                       AND p_heuredebut BETWEEN HEUREDEBUT AND HEUREFIN);
   IF v_count = 0 THEN
      RAISE_APPLICATION_ERROR(-20001, 'La table n''est pas disponible pour la réservation aux horaires renseignés.');
   END IF;
   -- Insère la réservation
   INSERT INTO RESERVATIONS (NUMTABLE, NOMCLIENT, PRENOMCLIENT, NBCONVIVES, TELEPHONE, HEUREDEBUT, HEUREFIN)
   VALUES (p_numtable, p_nomclient, p_prenomclient, p_nbconvives, p_telephone, p_heuredebut, p_heurefin);
   COMMIT;
EXCEPTION
   WHEN OTHERS THEN
      ROLLBACK;
      RAISE_APPLICATION_ERROR(-20002, 'Erreur lors de la réservation : ' || SQLERRM);
END reserver_table;


-- Appel de la procédure pour réserver une table
BEGIN
   reserver_table(21, 'Choffat', 'Rémi', 4, '0669696969',
                  TO_TIMESTAMP('2025-06-12 12:00:00', 'YYYY-MM-DD HH24:MI:SS'),
                  TO_TIMESTAMP('2025-06-12 14:00:00', 'YYYY-MM-DD HH24:MI:SS'));
end;