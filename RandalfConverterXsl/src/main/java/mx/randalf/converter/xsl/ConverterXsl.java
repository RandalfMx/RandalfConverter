package mx.randalf.converter.xsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import mx.randalf.converter.xsl.exception.ConvertXslException;

/**
 * Questa classe viene utilizzata per eseguire la conversione del file Xml tramite un foglio Xsl
 * 
 * @author Massimiliano Randazzo
 *
 */
public class ConverterXsl
{

	/**
	 * Questa variabile viene utilizzata per loggare le applicazioni
	 */
	private static Logger log = Logger.getLogger(ConverterXsl.class);

	public static void main(String[] args)
	{
		File f = null;
		File[] fl = null;

		try
		{
			if (args.length==3)
			{
				f = new File(args[1]);
				fl  = f.listFiles();
				Arrays.sort(fl);
				for (int x = 0; x<fl.length; x++)
				{
					if (fl[x].isFile() &&
							!fl[x].getName().startsWith("._") &&
							fl[x].getName().endsWith(".xml"))
					{
						ConverterXsl.convertXsl(args[0], fl[x].getAbsolutePath(), args[2]+File.separator+fl[x].getName());
					}
				}
			}
			else
				System.out.println("Nome del foglio di Style da applicare, Nome della cartella da analizzare, Nome della cartella di destinazione");
		}
		catch (ConvertXslException e)
		{
			e.printStackTrace();
		}
	}

	public static void convertXsl(String fileXsl, String fileInput, String fileOutput) throws ConvertXslException
	{
		File fi = null;
		FileInputStream fInput = null;
		File fo = null;
		FileOutputStream fOutput = null;
		
		try
		{
			fi = new File(fileInput);
			fInput = new FileInputStream(fi);
			fo = new File(fileOutput);
			fOutput = new FileOutputStream(fo);
			convertXsl(fileXsl, fInput, fOutput);
		}
		catch (FileNotFoundException e)
		{
			log.error(e.getMessage(), e);
			throw new ConvertXslException(e.getMessage());
		}
		finally
		{
			try
			{
				if (fOutput != null)
				{
					fOutput.flush();
					fOutput.close();
				}
				if (fInput != null)
					fInput.close();
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
				throw new ConvertXslException(e.getMessage());
			}
		}
	}

	
	public static void convertXsl(String fileXsl, InputStream input, OutputStream output) throws ConvertXslException
	{
		StreamSource ssXml = null;
		StreamSource ssXsl = null;
		TransformerFactory transFac = null;
		Transformer trans = null;
		File fXsl = null;

		try
		{
			log.debug("\n"+"convertXsl(String fileXsl, InputStream input, OutputStream output) throws ConvertXslException");
			fXsl = new File(fileXsl);

			log.debug("\n"+"File Xls: "+fileXsl);
			if (fXsl.exists())
			{
				log.debug("\n"+"File Xls trovato");
				ssXml = new StreamSource(input);
				ssXsl = new StreamSource(fXsl);

				transFac = TransformerFactory.newInstance();
//				transFac = new net.sf.saxon.TransformerFactoryImpl();
				trans = transFac.newTransformer(ssXsl);
				trans.transform(ssXml, new StreamResult(output));
			}
			else
				throw new ConvertXslException("Il file "+fXsl.getAbsolutePath()+" non \u00E8 presente.");
			log.debug("\n"+"convertXsl Fine");
		}
		catch (TransformerConfigurationException e)
		{
			log.error(e.getMessage(), e);
			throw new ConvertXslException(e.getMessage());
		}
		catch (TransformerFactoryConfigurationError e)
		{
			log.error(e.getMessage(), e);
			throw new ConvertXslException(e.getMessage());
		}
		catch (TransformerException e)
		{
			log.error(e.getMessage(), e);
			throw new ConvertXslException(e.getMessage());
		}
	}
}
