/**
 * 
 */
package mx.randalf.converter.xls2Xml.quartz.scanFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.im4java.core.IM4JavaException;
import org.purl.dc.elements._1.SimpleLiteral;
import org.quartz.JobExecutionException;

import it.sbn.iccu.metaag1.Bib;
import it.sbn.iccu.metaag1.Bib.Holdings;
import it.sbn.iccu.metaag1.Bib.Holdings.Shelfmark;
import it.sbn.iccu.metaag1.Bib.Piece;
import it.sbn.iccu.metaag1.BibliographicLevel;
import it.sbn.iccu.metaag1.Gen;
import it.sbn.iccu.metaag1.Img;
import it.sbn.iccu.metaag1.Img.Altimg;
import it.sbn.iccu.metaag1.Link;
import mx.randalf.converterxls2xml.Dati;
import mx.randalf.converterxls2xml.Dati.Campo;
import mx.randalf.converterxls2xml.Input.Image;
import mx.randalf.converterxls2xml.Output;
import mx.randalf.converterxls2xml.Output.Xml;
import mx.randalf.digital.img.convert.ConvertImg;
import mx.randalf.digital.img.convert.Utility;
import mx.randalf.tools.Utils;
import mx.randalf.tools.exception.UtilException;

/**
 * @author massi
 *
 */
public class DatiScheda {

	private Logger log = LogManager.getLogger(DatiScheda.class);

	private String cartella = null;

	private Hashtable<String, Vector<Object>> campi = null;

	private Vector<Vector<Object>> folderOutput = null;

	private Vector<Object> fileName = null;

	private String extension = null;

	private int numPagina = 0;
	
	private String rectoVerso = null;

	private String pStart = null;
	private String pStop = null;

	private String[] st = null;
	private Output output = null;

	private String pathImageMagick = null;

	/**
	 * @param output 
	 * @param st 
	 * 
	 */
	public DatiScheda(String[] st, Output output, String pStart, String pStop, String pathImageMagick) {

		if (!isEmpty(st, output.getCartella())){
			cartella = readCol(st, output.getCartella());
		}
		this.pStart = pStart;
		this.pStop = pStop;
		this.pathImageMagick = pathImageMagick;

		if (output.getPagInizio() != null && 
				!isEmpty(st, output.getPagInizio())){
			numPagina = new Integer(readCol(st, output.getPagInizio()).replace("[", "").replace("]", ""));
		} else{
			numPagina=1;
		}

		readBib(st, output);
		readXml(st, output.getXml());
		this.st = st;
		this.output = output;
	}

	private void readXml(String[] st, Xml xml){
		Vector<Object> values = null;
		Object value = null;

		folderOutput = new Vector<Vector<Object>>();
		
		for(int x=0; x<xml.getFolder().size(); x++){
			values = new Vector<Object>();
			for (int y=0; y<xml.getFolder().get(x).getDati().size(); y++){
				value = readDati(xml.getFolder().get(x).getDati().get(y), st);
				if (value!= null){
					values.add(value);
				}
			}
			if (values.size()>0){
				folderOutput.add(values);
			}
		}
		
		extension = (xml.getFileName().getExtension()==null?"xml":xml.getFileName().getExtension());
		fileName = new Vector<Object>();
		for (int x=0; x<xml.getFileName().getDati().size(); x++){
			value = readDati(xml.getFileName().getDati().get(x), st);
			if (value!= null){
				fileName.add(value);
			}
			
		}
		
	}

	private void readBib(String[] st, Output output){
		String key = null;
		String value = null;
		Vector<Object> values = null;
		String testo = null;

		campi = new Hashtable<String, Vector<Object>>();
		for (int x=0; x<output.getBib().getCampo().size(); x++){
			key = output.getBib().getCampo().get(x).getTipo();
			value = "";
			for (int y=0; y<output.getBib().getCampo().get(x).getDati().size(); y++){
				testo = (String) readDati(output.getBib().getCampo().get(x).getDati().get(y), st);
				if (testo != null){
					value += testo;
				}
			}
			if (value != null){
				if (campi.get(key)==null){
					values = new Vector<Object>();
				} else {
					values = campi.get(key);
				}
				values .add(value);
				campi.put(key, values);
			}
		}
	}

	public static boolean isEmpty(String[] st, BigInteger numCol){
		String col = null;
		boolean result = false;

		col = readCol(st, numCol);
		if (col==null){
			result = true;
		} else if (col.trim().equals("")){
			result = true;
		}
		return result;
	}

	public static String readCol(String[] st, 
			BigInteger numCol, 
			String decimalFormat, 
			String converter,
			String prefix,
			String suffix){
		String col = null;
		DecimalFormat df = null;

		col = readCol(st, numCol);
		if (col != null && !col.trim().equals("")){
			if (converter != null){
				if (converter.equals("mese")){
					col= Utility.convertMese(new Integer(col.trim()));
				} else if (converter.equals("romano")){
					col=Utility.converti(col)+"";
				} else if (converter.equals("copia")){
					col=(col.trim().equals("0")?"Originale":"Copia");
				}
			}
			if (decimalFormat != null){
				df = new DecimalFormat(decimalFormat);
				col = df.format(new Integer(col));
			}
			col = (prefix==null?"":prefix)+col.trim()+(suffix==null?"":suffix);
		}

		return (col==null?null:col);
	}

	public static String readCol(String[] st, BigInteger numCol){
		String col = null;

		if (numCol.intValue()<st.length){
			col = st[numCol.intValue()];
		}

		return (col==null?null:col.trim());
	}

	public static Object readDati(Dati dati, String[] st){
		Object key = null;
		if (dati.getColonna() != null){
			if (!isEmpty(st, dati.getColonna().getValue())){
				key = readCol(st, 
						dati.getColonna().getValue(), 
						dati.getColonna().getDecimalFormat(), 
						dati.getColonna().getConverter(), 
						dati.getColonna().getPrefix(), 
						dati.getColonna().getSuffix());
			}
		} else if (dati.getCampo() != null){
			key = dati.getCampo();
		} else if (dati.getDefault() != null){
			key = dati.getDefault();
		}
		return key;
	}

	private String convert(Vector<Object> dati){
		String testo = "";
		for (int x=0; x<dati.size(); x++){
			testo += convert(dati.get(x));
		}
		return testo;
	}
	
	private String convert(Object value){
		String testo = "";
		Campo campo = null;
		Vector<String> mCampi = null;
		DecimalFormat df = null;

		if (value.getClass().getName()
				.equals(Campo.class.getName())){
			campo = (Campo) value;
			if (campo.getTipo().equals("numPagina")){
				if (campo.getDecimalFormat() != null){
					df = new DecimalFormat(campo.getDecimalFormat());
					testo+=df.format(numPagina);
				} else {
					testo+=numPagina;
				}
			} else if (campo.getTipo().equals("rectoVerso")){
				if (rectoVerso == null){
					rectoVerso = "r";
				}
				testo += rectoVerso.toLowerCase();
				rectoVerso =(rectoVerso.equalsIgnoreCase("r")?"v":"r");
			} else if (campo.getTipo().equals("numPaginaRomano")){
				testo += Utility.converti(numPagina);
			} else if (campo.getTipo().equals("cronaca")){
				if (this.campi.get(campo.getTipo())!= null){
					mCampi = getCampo(campo.getTipo());
					for (int y=0; y<mCampi.size(); y++){
						testo += (y>0?" ":"")+mCampi.get(y);
					}
				}
			}
		} else {
			testo += (String) value;
		}
		return testo;
	}

	/**
	 * Leggo il contenuto dei campi
	 * @param key Nome del campo da analizzare
	 * @return Lista delle informazioni individuate
	 */
	private Vector<String> getCampo(String key){
		Vector<String> result = new Vector<String>();
		Vector<Object> value = null;
		String testo = null;

		value = campi.get(key);
		if (value != null){
			for (int x=0; x<value.size(); x++){
				testo = convert(value.get(x));
				if (testo != null && ! testo.trim().equals("")){
					result.addElement(testo);
				}
			}
		}
		return result;
	}

	private File getFileImageInput(Image inputImage, int number){
		String pFile = "";
		DecimalFormat df = null;
		
		pFile = inputImage.getPath()+
				File.separator+cartella+
				File.separator;
		if (inputImage.getFormatoImg().getPrefix() != null){
			pFile += inputImage.getFormatoImg().getPrefix();
		}
		if (inputImage.getFormatoImg().getDecimalFormat() != null){
			df = new DecimalFormat(inputImage.getFormatoImg().getDecimalFormat());
			pFile += df.format(number);
		} else {
			pFile += number;
		}
		if (inputImage.getFormatoImg().getSuffix() != null){
			pFile += inputImage.getFormatoImg().getSuffix();
		}
		pFile += "."+inputImage.getFormatoImg().getFormato();
		return new File(pFile);
	}

	public File getPathOutput(File folderOutput, boolean isFile){
		String fOut = null;
		String testo = null;
		
		fOut = folderOutput.getAbsolutePath();
		
		for ( int x=0; x<this.folderOutput.size(); x++){
			testo = convert(this.folderOutput.get(x));
			if (testo != null && ! testo.trim().equals("")){
				fOut += File.separator+testo;
			}
		}
		if (isFile){
			fOut += File.separator+getFileOutput(true);
		}
		return new File(fOut);
	}

	public String getFileOutput(boolean isExtension){
		String fileName = null;
		
		fileName = convert(this.fileName);
		if (fileName != null && !fileName.trim().equals("")){
			if (isExtension){
				fileName += "."+extension;
			}
		}
		return fileName;
	}

	/**
	 * @param numPagina the numPagina to set
	 */
	public void setNumPagina(int numPagina) {
		this.numPagina = numPagina;
	}

	public void addNumPagina(){
		this.numPagina++;
	}

	/**
	 * @param rectoVerso the rectoVerso to set
	 */
	public void setRectoVerso(String rectoVerso) {
		this.rectoVerso = rectoVerso;
	}

	/**
	 * @return the pStart
	 */
	public String getpStart() {
		return pStart;
	}

	/**
	 * @return the pStop
	 */
	public String getpStop() {
		return pStop;
	}

	public int getPagInizio(){
		int result = 0;
		if (output.getPagInizio() != null && 
				!isEmpty(st, output.getPagInizio())){
			result = new Integer(readCol(st, output.getPagInizio()).replace("[", "").replace("]", ""));
		} else{
			result=1;
		}

		return result;
	}
	private String getNomenclatura(){
		String result = "";
		
		for (int x=0;x<output.getImages().getNomenclatura().getDati().size(); x++){
			result +=convert(readDati(output.getImages().getNomenclatura().getDati().get(x), st));
		}
		return result;
	}

	public Gen genGen(){
		Gen gen = null;
		
		gen = new Gen();
		gen.setStprog(output.getGen().getStprog());
		if (output.getGen().getCollection() != null){
			gen.setCollection(output.getGen().getCollection());
		}
		gen.setAgency(output.getGen().getAgency());
		gen.setAccessRights(output.getGen().getAccessRights());
		gen.setCompleteness(output.getGen().getCompleteness());
		return gen;
	}

	public Bib genBib(){
		Bib bib = null;
		Vector<String> biblioteca = null;
		Vector<String> collocazione = null;
		Vector<String> inventario = null;
		Holdings holdings = null;
		Shelfmark shelfmark = null;
		int conta = 0;
		Vector<String> year = null;
		Vector<String> issue = null;
		Vector<String> stpiece_per = null;
		Vector<String> part_number = null;
		Vector<String> part_name = null;
		Vector<String> stpiece_vol = null;
		Piece piece = null;

		bib = new Bib();
		if (getCampo("natura").size()>0){
			bib.setLevel(BibliographicLevel.fromValue(getCampo("natura").get(0)));
		}
		compile(bib.getIdentifier(),"bid");
		compile(bib.getCreator(),"autore");
		compile(bib.getTitle(),"titolo");
		compile(bib.getPublisher(),"pubblicazione");
		compile(bib.getFormat(),"formato");
		compile(bib.getDescription(),"descrizione");
		compile(bib.getDate(),"data");
		compile(bib.getSource(),"source");
		compile(bib.getRelation(),"relation");
		compile(bib.getCoverage(),"coverage");

		biblioteca = getCampo("biblioteca");
		collocazione = getCampo("collocazione");
		inventario = getCampo("inventario");
		if (biblioteca.size()>0 ||
				collocazione.size()>0 ||
				inventario.size()>0){
			if (biblioteca.size()>conta){
				conta = biblioteca.size();
			}
			if (collocazione.size()>conta){
				conta = collocazione.size();
			}
			if (inventario.size()>conta){
				conta = inventario.size();
			}
			for (int x=0; x<conta; x++){
				holdings = new Holdings();
				if (biblioteca.size()>x){
					holdings.setLibrary(biblioteca.get(x));
				}
				if (collocazione.size()>x){
					shelfmark = new Shelfmark();
					shelfmark.setContent(collocazione.get(x));
					holdings.getShelfmark().add(shelfmark);
				}
				if (inventario.size()>x){
					holdings.setInventoryNumber(inventario.get(x));
				}
				bib.getHoldings().add(holdings);
			}
		}
		
		year =  getCampo("year");
		issue =  getCampo("issue");
		stpiece_per =  getCampo("stpiece_per");
		part_number =  getCampo("part_number");
		part_name =  getCampo("part_name");
		stpiece_vol =  getCampo("stpiece_vol");

		if ((year.size()>0 && issue.size()>0) || 
				(part_number.size()>0 && part_name.size()>0)){
			piece = new Piece();
			if ((year.size()>0 && issue.size()>0)){
				piece.setYear(year.get(0));
				piece.setIssue(issue.get(0));
				if (stpiece_per.size()>0){
					piece.setStpiecePer(stpiece_per.get(0));
				}
			} else {
				piece.setPartNumber(new BigInteger(part_number.get(0)));
				piece.setPartName(part_name.get(0));
				if (stpiece_vol.size()>0){
					piece.setStpieceVol(stpiece_vol.get(0));
				}
			}
			bib.setPiece(piece);
		}
		
		return bib;
	}

	private void compile(List<SimpleLiteral> oggetto, String key){
		SimpleLiteral campo = null;
		Vector<String> values = null;

		values = getCampo(key);
		for (int x=0; x<values.size(); x++){
			campo = new SimpleLiteral();
			campo.getContent().add(values.get(x));
			oggetto.add(campo);
		}
	}

	public Img genImg(Image inputImage, int numImgInput, File folderOutput, 
			int numImgOutput) throws JobExecutionException{
		File fImgInput = null;

		fImgInput = getFileImageInput(inputImage, numImgInput);
		return genImg(inputImage, fImgInput, folderOutput, 
				numImgOutput);
	}

	public Img genImg(Image inputImage, String fImg, File folderOutput, 
			int numImgOutput) throws JobExecutionException{
		File fImgInput = null;
		String pFile = null;
		
		pFile = inputImage.getPath()+
				File.separator+fImg;

		fImgInput = new File(pFile);
		return genImg(inputImage, fImgInput, folderOutput, 
				numImgOutput);
	}

	private Img genImg(Image inputImage, File fImgInput, File folderOutput, 
			int numImgOutput) throws JobExecutionException{
		Img img = null;
		Altimg altImg = null;
		String nomenclatura = null;
		Link file = null;
		File pathOutput = null;
		String fileImg = null;
		ConvertImg convertImg = null;

		try {
			convertImg = new ConvertImg(pathImageMagick);
			img = new Img();
			nomenclatura = getNomenclatura();
			img.setNomenclature(nomenclatura.trim());
			img.setSequenceNumber(new BigInteger(numImgOutput+""));
//			System.out.println("\t"+numImgInput);
//			System.out.println("\t\t"+nomenclatura);
//			System.out.println("\t\t"+fImgInput.getAbsolutePath());
			pathOutput = getPathOutput(folderOutput, false);
			for(int z=0; z<output.getImages().getImage().size(); z++){

				file = new Link();
				fileImg = genFileImg(output.getImages().getImage().get(z), 
						numImgOutput);
//				System.out.println(pathOutput.getAbsolutePath()+
//						File.separator+fileImg);
				
				if (inputImage.getFormatoImg().getFormato()
						.equals(output.getImages().getImage().get(z).getFormatoImg().getFormato())){
					Utils.copyFileValidate(fImgInput.getAbsolutePath(), 
							pathOutput.getAbsolutePath()+ File.separator+fileImg);
				} else {
					convertImg.resize(fImgInput, 
							new File(pathOutput.getAbsolutePath()+ File.separator+fileImg), 
							output.getImages().getImage().get(z).getConverter().getPpi(), 
							output.getImages().getImage().get(z).getConverter().getWidth(), 
							output.getImages().getImage().get(z).getConverter().getHeight(), 
							output.getImages().getImage().get(z).getConverter().getQuality(), 
							output.getImages().getImage().get(z).getConverter().getResize());
				}
				file.setHref("./"+fileImg);
				if (z==0){
					img.getUsage().add(output.getImages().getImage().get(z).getUsage());
					img.setFile(file);
				} else {
					altImg = new Altimg();
					altImg.getUsage().add(output.getImages().getImage().get(z).getUsage());
					altImg.setFile(file);
					img.getAltimg().add(altImg);
				}
			}
		} catch (UtilException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(),e);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(),e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(),e);
		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(),e);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(),e);
		} catch (IM4JavaException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(),e);
		}
		return img;
	}
	
	public String genFileImg(mx.randalf.converterxls2xml.Output.Images.Image image, 
			int numImgOutput){
		String fileImg = null;
		DecimalFormat df = null;
		String nomeFile = "";
		
		if (image.getFormatoImg().getPrefix()!= null){
			nomeFile += image.getFormatoImg().getPrefix();
		}
		if (image.getFormatoImg().getDecimalFormat() != null){
			df = new DecimalFormat(image.getFormatoImg().getDecimalFormat());
			nomeFile += df.format(numImgOutput);
		} else {
			nomeFile += numImgOutput;
		}
		if (image.getFormatoImg().getSuffix() != null){
			nomeFile += image.getFormatoImg().getSuffix();
		}
		nomeFile+="."+image.getFormatoImg().getFormato();

		fileImg=getFileOutput(false)+
				File.separator+
				image.getPath()+
				File.separator+
				nomeFile;
		return fileImg;
	}
}
