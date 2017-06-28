package mx.randalf.converter.IIPImage.batch;

import java.io.File;
import java.io.FileFilter;

import org.apache.log4j.Logger;
import org.im4java.process.ProcessStarter;

import it.sbn.iccu.metaag1.Img;
import it.sbn.iccu.metaag1.Img.Altimg;
import it.sbn.iccu.metaag1.Link;
import it.sbn.iccu.metaag1.Metadigit;
import mx.randalf.converter.IIPImage.RandalfConverterIIPImage;
import mx.randalf.converter.IIPImage.exception.RandalfConverterIIPImageException;
import mx.randalf.interfacException.exception.PubblicaException;
import mx.randalf.mag.MagXsd;
import mx.randalf.xsd.exception.XsdException;

public class RandalfConverterIIPImageBatch {

	private Logger log = Logger.getLogger(RandalfConverterIIPImageBatch.class);

	private String pathImageMagick = null;
	
	public RandalfConverterIIPImageBatch(String pathImageMagick) {
		this.pathImageMagick = pathImageMagick;
		ProcessStarter.setGlobalSearchPath(pathImageMagick);
	}

	public static void main(String[] args) {
		RandalfConverterIIPImageBatch batch = null;

		if (args.length==2){
			batch = new RandalfConverterIIPImageBatch(args[0]);
			batch.scan(args[1]);
		} else {
			System.out.println("E' necessaio indicare i seguenti parametri:");
			System.out.println("1) Path imagemagick");
			System.out.println("2) Path da analizzare");
		}
	}

	private void scan(String folder) {
		File f = null;

		f = new File(folder);
		if (f.exists()){
			if (f.isDirectory()){
				scan(f);
			} else {
				log.error("["+f.getAbsolutePath()+"] non è una cartella");
			}
		} else {
			log.error("La cartella ["+f.getAbsolutePath()+"] non esiste");
		}
	}

	private void scan(File fi) {
		File[] fl = null;

		fl =fi.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				boolean result = false;
				File fIIP = null;
				if (!pathname.getName().startsWith(".")){
					if (pathname.isDirectory()){
						result = true;
					} else if (pathname.getName().toLowerCase().endsWith(".xml")){
						fIIP = new File(pathname.getAbsolutePath()+".iip");
						if (!fIIP.exists()){
							result = true;
						}
					}
				}
				return result;
			}
		});
		for (File f: fl){
			if (f.isDirectory()){
				scan(f);
			} else {
				elab(f);
			}
		}
	}

	private void elab(File fi) {
		MagXsd magXsd = null;
		Metadigit mag = null;
		File fImgInput = null;
		File fImgOutput = null;
		File fCert = null;
		File fIip = null;
		Link file = null;

		try {
			magXsd = new MagXsd();
			mag = magXsd.read(fi);

			System.out.println("Elaboro il file : "+fi.getAbsolutePath());
			if (mag.getImg() != null && mag.getImg().size()>0){
				for (int x=0; x<mag.getImg().size(); x++){
					if ((x%10)==0){
						System.out.println("img "+x+"/"+mag.getImg().size());
					}
					if (!checkUsage(mag.getImg().get(x), "6")){
						file = getFile(mag.getImg().get(x), "1");
						if (file != null){
							fImgInput = null;
							fImgOutput = null;
							fImgInput = new File(fi.getParentFile().getAbsolutePath()+
									File.separator+
									(file.getHref().startsWith("./")?
											file.getHref().substring(2):
												(file.getHref().startsWith("/")?
														file.getHref().substring(1):
															file.getHref())));
							if (fImgInput.exists()){
								fImgOutput = new File(fImgInput.getParentFile().getParentFile().getAbsolutePath()+
										File.separator+"IIP"+File.separator+
										fImgInput.getName());
								convert(fImgInput, fImgOutput);
								mag.
									getImg().
										get(x).
											getAltimg().
												add(updateImg(fImgOutput.
													getAbsolutePath().
														replace(fi.getParentFile().getAbsolutePath(), 
																".")));
								magXsd.calcImg(
										mag.getImg().get(x), 
										fi.getParentFile().getAbsolutePath(), 
										true, 
										new String[] {"1","6"});
							} else {
								log.error("Il file ["+fImgInput.getAbsolutePath()+"] non esiste");
							}
						}
					}
				}
				System.out.println("Scrivo il file : "+fi.getAbsolutePath());
				if (magXsd.write(mag, fi)){
					fCert = new File(fi.getAbsolutePath()+".cert");
					if (fCert.exists()){
						fIip = new File(fi.getAbsolutePath()+".iip");
						if (!fCert.renameTo(fIip)){
							log.error("Problemi nella rinomina del file ["+
									fCert.getAbsolutePath()+
									"] in ["+
									fIip.getAbsolutePath()+"]");
						}
					}
				}
				
			}
		} catch (XsdException | RandalfConverterIIPImageException e) {
			log.error(e.getMessage(), e);
		} catch (PubblicaException e) {
			log.error(e.getMessage(), e);
		}
	}

	private Altimg updateImg(String hrefFile) {
		Altimg altimg = null;
		Link file = null;
		
		altimg = new Altimg();
		file = new Link();
		file.setHref(hrefFile);
		altimg.setFile(file);
		altimg.getUsage().add("6");
		return altimg;
	}

	private void convert(File fImgInput, File fImgOutput) throws RandalfConverterIIPImageException {
		RandalfConverterIIPImage tecaGenIIPImage = null;
		
		try {
			if (!fImgOutput.exists()){
				tecaGenIIPImage = new RandalfConverterIIPImage(pathImageMagick);
				
				if (!tecaGenIIPImage.convertImg(fImgInput, fImgOutput)){
					throw new RandalfConverterIIPImageException("Problemi nella conversione del file ["+fImgInput.getAbsolutePath()+"] in ["+fImgOutput.getAbsolutePath()+"]");
				}
			}
		} catch (RandalfConverterIIPImageException e) {
			log.error("Problemi nella conversione del file ["+fImgInput.getAbsolutePath()+"] in ["+fImgOutput.getAbsolutePath()+"]");
			throw e;
		}
	}

	private Link getFile(Img img, String usage){
		Link result = null;

		if (img.getUsage().get(0).equals(usage)){
			result = img.getFile();
		} else if (img.getAltimg() != null &&
				img.getAltimg().size()>0){
			for (int x=0; x<img.getAltimg().size(); x++){
				if (img.getAltimg().get(x).getUsage().get(0).equals(usage)){
					result = img.getAltimg().get(x).getFile();
				}
			}
		}
		return result;
	}

	private boolean checkUsage(Img img, String usage){
		boolean result = false;

		if (img.getUsage().get(0).equals(usage)){
			result = true;
		} else if (img.getAltimg() != null &&
				img.getAltimg().size()>0){
			for (int x=0; x<img.getAltimg().size(); x++){
				if (img.getAltimg().get(x).getUsage().get(0).equals(usage)){
					result = true;
				}
			}
		}
		return result;
	}
}
