package mx.randalf.converter.text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import mx.randalf.interfacException.exception.MagException;
import mx.randalf.interfacException.interfacce.IMagException;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * Questa classe viene utilizzata per poter scrivere i tag XML convertendo il
 * testo
 * 
 * @author Randazzo
 * 
 */
class ConvertTextXml {

	/**
	 * Questa variabile viene utilizzata per loggare l'applicazione
	 */
	private static Logger log = Logger.getLogger(ConvertTextXml.class);

	/**
	 * Questa variabile viene utilizzato per indicare se eseguire la conversione
	 * del Testo oppure no
	 */
	private boolean convertText = true;

	/**
	 * Questa variabile viene utilizzato per indicare se eseguire la conversione
	 * del Testo oppure no
	 */
	private boolean convertTextISO8859 = false;

	/**
	 * Questa variabile viene utilizzata per indicare la lista degli errori
	 * trovati nel Mag
	 */
	protected IMagException iMagException = null;

	/**
	 * Questa variabile viene utilizzata per indicare la sezione del Mag
	 */
	protected String sezione = "";

	/**
	 * Tipo di conversione da adottare
	 */
	protected String charsetName = "";

	/**
	 * Costruttore della classe
	 * 
	 * @param iMagException
	 *            Lista degli errori generati nel Mag
	 * @param sezione
	 *            Nome della sezione del Mag
	 */
	public ConvertTextXml(IMagException iMagException, String sezione) {
		super();
		this.iMagException = iMagException;
		this.sezione = sezione;
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare gli attributi del Tag Xml
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param key
	 *            Nome dell'attributo
	 * @param value
	 *            Valore dell'attributo
	 * @param obbligatorio
	 *            Indica se l'attributo è obbligatorio
	 */
	public void setAttribute(MessageElement tagXml, String key, String value,
			boolean obbligatorio) {
		if (value != null && (!value.equals("")))
			tagXml.setAttribute(key, convert(value));
		else if (obbligatorio) {
			MagException mExc = new MagException("L'attributo [" + key
					+ "] \u00E8 obbligatorio", MagException.FATALERROR,
					MagException.OBBERROR, sezione);
			if (iMagException != null)
				iMagException.add(mExc);
			else
				System.err.println(mExc.toString());
		}
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare gli attributi del Tag Xml
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param key
	 *            Nome dell'attributo
	 * @param value
	 *            Valore dell'attributo
	 * @param obbligatorio
	 *            Indica se l'attributo � obbligatorio
	 */
	public void setAttribute(MessageElement tagXml, String key, int value,
			boolean obbligatorio) {
		setAttribute(tagXml, key, value, obbligatorio, 0);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare gli attributi del Tag Xml
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param key
	 *            Nome dell'attributo
	 * @param value
	 *            Valore dell'attributo
	 * @param obbligatorio
	 *            Indica se l'attributo � obbligatorio
	 */
	public void setAttribute(MessageElement tagXml, String key, int value,
			boolean obbligatorio, int valMin) {
		if (value > valMin)
			setAttribute(tagXml, key, Integer.toString(value), obbligatorio);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml,
			ArrayList<MessageElement> nodes, String key, String value)
			throws SOAPException {
		addChildElement(tagXml, nodes, key, value, "", "", false, false);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml, String key,
			String value, boolean obbligatorio) throws SOAPException {
		addChildElement(tagXml, new ArrayList<MessageElement>(), key, value,
				obbligatorio);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml,
			ArrayList<MessageElement> nodes, String key, String value,
			boolean obbligatorio) throws SOAPException {
		addChildElement(tagXml, nodes, key, value, "", "", obbligatorio, false);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param keyAttr
	 *            Nome dell'attributo
	 * @param valueAttr
	 *            Contenuto dell'attributo
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @param obbAttr
	 *            Indica se l'attributo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml, String key,
			String value, String keyAttr, String valueAttr,
			boolean obbligatorio, boolean obbAttr) throws SOAPException {
		addChildElement(tagXml, new ArrayList<MessageElement>(), key, value,
				keyAttr, valueAttr, obbligatorio, obbAttr);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param keyAttr
	 *            Nome dell'attributo
	 * @param valueAttr
	 *            Contenuto dell'attributo
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @param obbAttr
	 *            Indica se l'attributo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml,
			ArrayList<MessageElement> nodes, String key, String value,
			String keyAttr, String valueAttr, boolean obbligatorio,
			boolean obbAttr) throws SOAPException {
		Vector<String> keyAtt = new Vector<String>();
		Vector<String> valAtt = new Vector<String>();
		Vector<String> obbAtt = new Vector<String>();

		if (valueAttr != null && !valueAttr.equals("")) {
			keyAtt.add(keyAttr);
			valAtt.add(valueAttr);
			obbAtt.add(Boolean.toString(obbAttr));
		}
		addChildElement(tagXml, nodes, key, value, keyAtt, valAtt,
				obbligatorio, obbAtt);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param keyAttr
	 *            Nome degli attributi
	 * @param valueAttr
	 *            Contenuto degli attributi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @param obbAttr
	 *            Indica la lista degli attributi sono obbligatori
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml, String key,
			String value, Vector<String> keyAttr, Vector<String> valueAttr,
			boolean obbligatorio, Vector<String> obbAttr) throws SOAPException {
		this.addChildElement(tagXml, new ArrayList<MessageElement>(), key,
				value, keyAttr, valueAttr, obbligatorio, obbAttr);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param keyAttr
	 *            Nome degli attributi
	 * @param valueAttr
	 *            Contenuto degli attributi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @param obbAttr
	 *            Indica la lista degli attributi sono obbligatori
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml,
			ArrayList<MessageElement> nodes, String key, String value,
			Vector<String> keyAttr, Vector<String> valueAttr,
			boolean obbligatorio, Vector<String> obbAttr) throws SOAPException {

		if ((value != null && (!value.equals("")))
				|| ((valueAttr != null) && (valueAttr.size() > 0))) {
			nodes.add(new MessageElement());
			log.debug("Key : " + key);
			((MessageElement) nodes.get(nodes.size() - 1)).setName(key);
			// ((MessageElement) nodes.get(nodes.size() -
			// 1)).setEncodingStyle("UTF-8");
			// System.out.println("Stato UTF-8: "+((MessageElement)
			// nodes.get(nodes.size() - 1)).getEncodingStyle());
			if (value != null && (!value.equals("")))
				setValue((MessageElement) nodes.get(nodes.size() - 1), value);
			else if (obbligatorio) {
				MagException mExc = new MagException("Il nodo [" + key
						+ "] \u00E8 obbligatorio", MagException.FATALERROR,
						MagException.OBBERROR, sezione);
				if (iMagException != null)
					iMagException.add(mExc);
				else
					System.err.println(mExc.toString());
			}
			if (valueAttr != null)
				for (int x = 0; x < valueAttr.size(); x++)
					setAttribute((MessageElement) nodes.get(nodes.size() - 1),
							(String) keyAttr.get(x), (String) valueAttr.get(x),
							Boolean.getBoolean((String) obbAttr.get(x)));
			tagXml.addChildElement(((MessageElement) nodes.get(nodes.size() - 1)));
		} else if (obbligatorio) {
			MagException mExc = new MagException("Il nodo [" + key
					+ "] \u00E8 obbligatorio", MagException.FATALERROR,
					MagException.OBBERROR, sezione);
			if (iMagException != null)
				iMagException.add(mExc);
			else
				System.err.println(mExc.toString());
		}

	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml, String key, int value,
			boolean obbligatorio) throws SOAPException {
		addChildElement(tagXml, new ArrayList<MessageElement>(), key, value,
				obbligatorio);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml,
			ArrayList<MessageElement> nodes, String key, int value,
			boolean obbligatorio) throws SOAPException {
		addChildElement(tagXml, nodes, key, value, obbligatorio, 0);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml,
			ArrayList<MessageElement> nodes, String key, int value,
			boolean obbligatorio, int valDef) throws SOAPException {
		if (value > valDef)
			addChildElement(tagXml, nodes, key, Integer.toString(value),
					obbligatorio);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un singolo nodo
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Contenuto dei nodi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml,
			ArrayList<MessageElement> nodes, String key, long value,
			boolean obbligatorio) throws SOAPException {
		addChildElement(tagXml, nodes, key, Long.toString(value), obbligatorio);
	}

	/**
	 * Questo metodo viene utilizzato per valorizzare un gruppo di nodi
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param nodes
	 *            Lista dei nodi
	 * @param key
	 *            Nome dei nodi
	 * @param value
	 *            Lista dei nodi
	 * @param obbligatorio
	 *            Indica se il nodo � obbligatorio
	 * @throws SOAPException
	 */
	public void addChildElement(MessageElement tagXml,
			ArrayList<MessageElement> nodes, String key, Vector<String> value,
			boolean obbligatorio) throws SOAPException {
		if (value != null) {
			for (int x = 0; x < value.size(); x++) {
				addChildElement(tagXml, nodes, key, (String) value.get(x),
						obbligatorio);
			}
		} else if (obbligatorio) {
			MagException mExc = new MagException("Il nodo [" + key
					+ "] \u00E8 obbligatorio", MagException.FATALERROR,
					MagException.OBBERROR, sezione);
			if (iMagException != null)
				iMagException.add(mExc);
			else
				System.err.println(mExc.toString());
		}

	}

	/**
	 * Questo metodo viene utilizzato per indicare il valore del singolo tag
	 * 
	 * @param tagXml
	 *            Tag Xml da valorizzare
	 * @param value
	 *            Lista dei nodi
	 */
	public void setValue(MessageElement tagXml, String value) {
		tagXml.setValue(convert(value));
	}

	/**
	 * Questo metodo viene utilizzato per convertire un testo in UTF8
	 * 
	 * @param testo
	 *            Testo da convertire
	 * @return Tersto convertito
	 */
	protected String convert(String testo) {
		String ris = "";
		log.debug("Convert XML: " + testo);
		byte[] utf8;
		log.debug("Convert XML: " + isConvertText());
		if (isConvertText()) {
			try {
				log.debug("Prima convert: " + testo);
				utf8 = testo.getBytes(charsetName);
				ris = new String(utf8);
				log.debug("Dopo convert: " + ris);
			} catch (UnsupportedEncodingException e) {
				log.error(e);
			}
		} else
			ris = testo;
		if (isConvertTextISO8859()) {
			try {
				log.debug("Prima convert: " + testo);
				utf8 = testo.getBytes();
				ris = new String(utf8, charsetName);
				log.debug("Dopo convert: " + ris);
			} catch (UnsupportedEncodingException e) {
				log.error(e);
			}
		} else
			ris = testo;
		log.debug("Convert XML: " + ris);
		return ris;

	}

	/**
	 * Questo metodo viene utilizzato per convertire un testo URI in Stringa
	 * 
	 * @param testo
	 */
	public String toString(String testo) {
		byte[] bUtf8;
		try {
			log.debug("Prima: " + testo);
			bUtf8 = testo.getBytes();
			testo = new String(bUtf8, charsetName);
			log.debug("Dopo: " + testo);
		} catch (UnsupportedEncodingException e) {
			MagException magException = new MagException(
					"Problemi nella conversione del testo UTF-8 [" + testo
							+ "] in Stringa", MagException.WARNING,
					MagException.URIERROR, sezione);
			if (iMagException != null)
				iMagException.add(magException);
			else
				System.err.println(magException.toString());
		} catch (Exception e) {
			log.error(e);
		}
		return testo;
	}

	/**
	 * Questo metodo viene utilizzato per verificare se eseguire la conversione
	 * del Testo oppure no
	 * 
	 */
	public boolean isConvertText() {
		return convertText;
	}

	/**
	 * Questo metodo viene utilizzato per indicare se eseguire la conversione
	 * del Testo oppure no
	 * 
	 * @param convertText
	 */
	public void setConvertText(boolean convertText) {
		this.convertText = convertText;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la sezione presa in
	 * considerazione
	 * 
	 * @param sezione
	 */
	public void setSezione(String sezione) {
		this.sezione = sezione;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il valore di un determinato
	 * nodo
	 * 
	 * @param dati
	 *            Nodo contenente il valore da leggere
	 * @param x
	 *            Posizione rispetto al nodo da leggere
	 */
	public static String readNodeValue(Node dati, int x) {
		return readNodeValue(dati, x, "");
	}

	/**
	 * Questo metodo viene utilizzato per leggere il valore di un determinato
	 * nodo
	 * 
	 * @param dati
	 *            Nodo contenente il valore da leggere
	 * @param x
	 *            Posizione rispetto al nodo da leggere
	 * @param def
	 *            Valore di default da restituire nel caso in cui il valore sia
	 *            nullo
	 */
	public static String readNodeValue(Node dati, int x, String def) {
		String ris = "";
		ris = def;
		if (dati != null)
			if (dati.getChildNodes() != null)
				if (dati.getChildNodes().item(x) != null)
					ris = readNodeValue(dati.getChildNodes().item(x), def);
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per leggere il valore di un determinato
	 * nodo
	 * 
	 * @param dati
	 *            Nodo contenente il valore da leggere
	 */
	public static String readNodeValue(Node dati) {
		return readNodeValue(dati, "");
	}

	/**
	 * Questo metodo viene utilizzato per leggere il valore di un determinato
	 * nodo
	 * 
	 * @param dati
	 *            Nodo contenente il valore da leggere
	 * @param def
	 *            Valore di default da restituire nel caso in cui il valore sia
	 *            nullo
	 */
	public static String readNodeValue(Node dati, String def) {
		String ris = "";
		ris = def;
		if (dati.getFirstChild() != null)
			if (dati.getFirstChild().getNodeValue() != null)
				ris = dati.getFirstChild().getNodeValue();
		return ris;
	}

	/**
	 * Questo metodo viene utilizzato per indicare la lista degli errori trovati
	 * nel Mag
	 * 
	 * @param magException
	 */
	public void setIMagException(IMagException magException) {
		iMagException = magException;
	}

	public boolean isConvertTextISO8859() {
		return convertTextISO8859;
	}

	public void setConvertTextISO8859(boolean convertTextISO8859) {
		this.convertTextISO8859 = convertTextISO8859;
	}
}
