package mx.randalf.converter.text;

import mx.randalf.interfacException.interfacce.IMagException;

/**
 * Questa classe viene utilizzata per poter scrivere i tag XML convertendo il
 * testo in formato UTF8
 * 
 * @author Randazzo Massimiliano
 * 
 */
public class ConvertToUTF8 extends ConvertTextXml {

	/**
	 * Costruttore
	 * 
	 */
	public ConvertToUTF8(IMagException iMagException, String sezione) {
		super(iMagException, sezione);
		// this.setConvertText(false);
		charsetName = "UTF-8";// "UTF-8";
	}
}
