package mx.randalf.converter.IIPImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.Vector;

import mx.randalf.converter.IIPImage.exception.RandalfConverterIIPImageException;

public class RandalfConverterIIPImage {

	private File batchConversione = null;

	private File pathTmp = null;

	public RandalfConverterIIPImage(File batchConversione, File pathTmp) {
		this.batchConversione = batchConversione;
		this.pathTmp = pathTmp;
	}

	/**
	 * Metodo utilizzato per la conversione della singola immagine TIFF in TIFF Piramidale
	 * 
	 * @param imgInput Nome Immagine di Partenza
	 * @param imgOutput Nome Immagine di Destinazione
	 * @return Esito della elaborazione
	 * @throws RandalfConverterIIPImageException
	 */
	public  boolean convertImg(File imgInput, File imgOutput) throws RandalfConverterIIPImageException {
		Runtime runtime = null;
		String[] cmdarray = null;
		Process process = null;
		Integer processResult = null;
		String line = null; 
		BufferedReader stdErr = null;
		Vector<String> err = new Vector<String>();
		BufferedReader stdOut = null;
		Vector<String> out = new Vector<String>();
		boolean result = true;
		String cmd = null;

		try {
			runtime = Runtime.getRuntime();

			if (imgInput.exists()){
				if (!imgOutput.getParentFile().exists()){
					if (!imgOutput.getParentFile().mkdirs()){
						throw new RandalfConverterIIPImageException("Impossibile creare la cartella ["+imgInput.getAbsolutePath()+"]");
					}
				}
				cmdarray = new String[4];
				cmdarray[0] = batchConversione.getAbsolutePath();
				cmdarray[1] = imgInput.getAbsolutePath();
				cmdarray[2] = imgOutput.getAbsolutePath();
				cmdarray[3] = pathTmp.getAbsolutePath()+
						File.separator+
						UUID.randomUUID().toString();

				cmd = "";
				for (int x=0;x<cmdarray.length; x++){
					cmd += (cmd.equals("")?"":" ")+cmdarray[x];
				}
				
//				System.out.println("CMD: "+cmd);
				process = runtime.exec(cmd);
				stdErr = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				stdOut = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
	
				while ((line = stdOut.readLine()) != null) {
					if (line.toLowerCase().indexOf("fatal")>-1 ||
							line.toLowerCase().indexOf("error")>-1 ||
							line.toLowerCase().indexOf("warning")>-1
							){
						result = false;
					}
					out.add(line);
				}
	
				while ((line = stdErr.readLine()) != null) {
//					result = false;
//					System.out.println("ERROR: "+line);
					err.add(line);
				}
				processResult = process.waitFor();
				if (processResult !=0){
					result = false;
				}
				if (!imgOutput.exists() || imgOutput.length()==0){
					result = false;
				}
			} else {
				throw new RandalfConverterIIPImageException("Il file ["+imgInput.getAbsolutePath()+"] non esiste");
			}
		} catch (IOException e) {
			throw new RandalfConverterIIPImageException(e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new RandalfConverterIIPImageException(e.getMessage(), e);
		}
		return result;
	}
//
//	public void convertMag(File fMag){
//		convertMag(fMag, "IIPImage", null);
//	}
//
//	public void convertMag(File fMag, String folderIIPImage){
//		convertMag(fMag, folderIIPImage, null);
//	}

//	public void convertMag(File fMag, String folderIIPImage, File pathOutputAlt){
//		File pathOutput = null;
//		File magOutput = null;
//		MagXsd magXsd = null;
//		Metadigit mag = null;
//		String fileTiff = null;
//		boolean existIIPImage =  false;
//		String[] usages = new String[1];
//
//		usages[0] = "6";
//
//		if (pathOutputAlt == null){
//			pathOutput = fMag.getParentFile();
//		} else {
//			pathOutput = pathOutputAlt;
//		}
//		
//		magOutput =  new File(pathOutput.getAbsolutePath()+
//				File.separator+
//				fMag.getName());
//		
//		magXsd = new MagXsd();
//		mag = magXsd.read(fMag);
//		
//		if (mag.getImg()!= null && mag.getImg().size()>0){
//			for (int x=0; x<mag.getImg().size(); x++){
//				fileTiff = null;
//				existIIPImage = false;
//				if (mag.getImg().get(x).getUsage().get(0).equals("1")){
//					fileTiff = mag.getImg().get(x).getFile().getHref();
//				}
//				if (mag.getImg().get(x).getUsage().get(0).equals("6")){
//					existIIPImage = true;
//				}
//				if (mag.getImg().get(x).getAltimg() != null &&
//						mag.getImg().get(x).getAltimg().size()>0){
//					for (int y=0; y<mag.getImg().get(x).getAltimg().size(); y++){
//						if (mag.getImg().get(x).getAltimg().get(y).getUsage().get(0).equals("1")){
//							fileTiff = mag.getImg().get(x).getAltimg().get(y).getFile().getHref();
//						}
//						if (mag.getImg().get(x).getAltimg().get(y).getUsage().get(0).equals("6")){
//							existIIPImage = true;
//						}
//					}
//				}
//				if (fileTiff!= null && !existIIPImage){
//					
////					magXsd.calcImg(img, pathMag, anaAltImg);
//				}
//			}
//		}
//	}
//
//	private void addIIPImage(File fMag, String fTif, File pathOutput, String folderIIPImage){
//		File fileTif = null;
//		File fIIPImage = null;
//		Altimg altImg = null;
//		Link file = null;
//
//		fileTif = new File(fMag.getParentFile().getAbsolutePath() +
//				File.separator+
//				fTif.replace("./", ""));
//		if (fileTif.exists()){
//			fIIPImage = new File(pathOutput.getAbsolutePath() +
//					File.separator+
//					fileTif.getParentFile().getParentFile().getName()+
//					File.separator+
//					folderIIPImage+
//					fileTif.getName());
//			if (!fIIPImage.exists()){
//				if (Configuration.getValueDefault("aggImg", "true").equals("true")){
//					if (!JFile.convertImg(new File(fileTif), fIIPImage)){
//						throw new JFileException("Problemi nella conversione del file ["+
//								fileTif+"] in ["+fIIPImage.getAbsolutePath()+"]");
//					}
//				}
//			}
//			
//			altImg = new Altimg();
//			altImg.getUsage().add("6");
//			file = new Link();
//			file.setHref(folderImg+"IIPImage/"+ToolsXsl.analizza(riga[0]).trim());
//			altImg.setFile(file);
//			img.getAltimg().add(altImg);
//		} else {
//			throw new RandalfConverterIIPImageException("Il file ["+fileTif.getAbsolutePath()+"] non esiste");
//		}
//
//	}
}
