package mx.randalf.converter.text;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mx.randalf.interfacException.exception.MagException;
import mx.randalf.interfacException.interfacce.IMagException;

/**
 * Questa classe viene utilizzata per poter scrivere i tag XML convertendo il
 * testo in formato UTF8
 * 
 * @author Randazzo Massimiliano
 *
 */
public class ConvertToURI extends ConvertTextXml {
	private Logger log = LogManager.getLogger(ConvertToURI.class);

	/**
	 * Costruttore
	 *
	 */
	public ConvertToURI(IMagException iMagException, String sezione) {
		super(iMagException, sezione);
		charsetName = "UTF-8";
	}

	/**
	 * Questo metodo viene utilizzato per convertire un testo in UTF8
	 * 
	 * @param testo
	 *            Testo da convertire
	 * @return Testo convertito
	 */
	protected String convert(String testo) {
		URI uri = null;
		if (isConvertText()) {
			try {
				// testo = testo.r eplaceAll(" ","%20");
				testo = URLEncoder.encode(testo, charsetName);
				testo = testo.replaceAll("%2F", "/");
				testo = testo.replaceAll("%3A", ":");
				uri = new URI(testo);
				testo = uri.toString();
			} catch (URISyntaxException e) {
				MagException magException = new MagException(
						"Il testo [" + testo + "] non rispetta la formattazione URI", MagException.WARNING,
						MagException.URIERROR, sezione);
				if (iMagException != null)
					iMagException.add(magException);
				else {
					log.error("\n"+magException.toString());
				}
			} catch (UnsupportedEncodingException e) {
				MagException magException = new MagException(
						"Il testo [" + testo + "] non rispetta la formattazione URI", MagException.WARNING,
						MagException.URIERROR, sezione);
				if (iMagException != null)
					iMagException.add(magException);
				else {
					log.error("\n"+magException.toString());
				}
			}
		}
		return super.convert(testo);
	}

	/**
	 * Questo metodo viene utilizzato per convertire un testo URI in Stringa
	 * 
	 * @param testo
	 *            Testo da convertire
	 * @return Testo convertito
	 */
	public String toString(String testo) {
		try {
			testo = URLDecoder.decode(testo, charsetName);
		} catch (UnsupportedEncodingException e) {
			MagException magException = new MagException(
					"Problemi nella conversione del testo URI [" + testo + "] in Stringa", MagException.WARNING,
					MagException.URIERROR, sezione);
			if (iMagException != null)
				iMagException.add(magException);
			else {
				log.error("\n"+magException.toString());
			}
		} catch (Exception e) {
			log.error("\nEEEEEEEEE: " + e.getMessage());
			log.error(e.getMessage(), e);
		}
		return testo;
	}
}
