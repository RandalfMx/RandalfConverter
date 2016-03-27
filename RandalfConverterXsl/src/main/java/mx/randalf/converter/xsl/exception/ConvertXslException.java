/**
 * 
 */
package mx.randalf.converter.xsl.exception;

/**
 * Questa classe viene utilizzata per gestire gli errori generati dalla classe di conversione Xsl
 * 
 * @author Massimiliano Randazzo
 *
 */
public class ConvertXslException extends Exception
{

	/**
	 * Questa variabile viene utilizzata per indicare il Serial version UID
	 */
	private static final long serialVersionUID = 8712710100387600025L;

	/**
	 * Costruttore
	 * 
	 * @param arg0 Messaggio di Errore
	 */
	public ConvertXslException(String arg0)
	{
		super(arg0);
	}

}
