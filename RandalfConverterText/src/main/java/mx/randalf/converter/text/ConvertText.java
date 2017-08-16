package mx.randalf.converter.text;

import java.text.DecimalFormat;

import mx.randalf.converter.utility.SpeChar;
import mx.randalf.converter.utility.StopList;

public class ConvertText {

	public ConvertText() {
	}

	/**
	 * Questo metodo viene utilizzato per generare la chiave normalizzata per la
	 * segnatura bibliografica
	 *
	 * @param segnatura
	 *
	 */
	public static String conveSegna(String segnatura) {
		String ris = "";
		String numero = "";
		int nChar = 0;
		Character myChar = null;
		DecimalFormat df6 = new DecimalFormat("000000");

		segnatura = segnatura.toUpperCase();
		for (int x = 0; x < segnatura.length(); x++) {
			myChar = new Character(segnatura.charAt(x));
			nChar = myChar.hashCode();
			if ((nChar > 64 && nChar < 91) || nChar == 95 || nChar == 45) {
				if (!numero.equals(""))
					ris += df6.format(numero);

				if (nChar == 45)
					ris += "_";
				else
					ris += segnatura.charAt(x);
				numero = "";
			} else if (nChar > 47 && nChar < 58)
				numero += segnatura.charAt(x);
			else {
				if (!numero.equals(""))
					ris += df6.format(numero);

				if (!ris.endsWith(" "))
					ris += " ";
				numero = "";
			}
		}
		if (!numero.equals(""))
			ris += df6.format(numero);
		return ris;
	}

	public static String conveVar(String testo) {
		return conveVar(testo, false);
	}

	/**
	 * Queta classe viene utilizzata per la normalizzazione delle stringe
	 * necessarie per gli indici e gli ordinamenti
	 *
	 * @param testo
	 *
	 */
	public static String conveVar(String testo, boolean checkStopList) {
		int pos = 0;
		int pos1 = 0;
		String parola = "";
		SpeChar speChar = new SpeChar();

		pos = testo.indexOf("*");
		if (pos == -1) {
			pos = testo.indexOf(" ");
			if (pos > -1)
				parola = testo.substring(0, pos).trim();

			pos1 = testo.indexOf("'");
			if (pos1 > -1 && (pos == -1 || pos1 < pos))
				parola = testo.substring(0, pos1 + 1);

			if (!parola.equals("") && checkStopList)
				if (StopList.checkStopList(parola))
					testo = testo.substring(parola.length()).trim();
		} else
			testo = testo.substring(pos + 1);
		testo = speChar.usr2key(testo);
		return checkPunteggiatura(testo);
	}

	private static String checkPunteggiatura(String testo) {
		String ris = "";
		int nChar = -1;

		for (int x = 0; x < testo.length(); x++) {
			nChar = testo.substring(x, x + 1).hashCode();
			if ((nChar >= 65 && nChar <= 90) || nChar == 32 || (nChar >= 48 && nChar <= 57)) {
				ris += testo.charAt(x);
			} else if (nChar == 47) {
				ris += " ";
			}
		}
		ris = ris.replace("  ", " ").trim();
		return ris;
	}
}
