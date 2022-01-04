package mx.randalf.converter.IIPImage.batch;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;


import org.im4java.process.ProcessStarter;

import it.sbn.iccu.metaag1.Img;
import it.sbn.iccu.metaag1.Img.Altimg;
import it.sbn.iccu.metaag1.Link;
import it.sbn.iccu.metaag1.Metadigit;
import mx.randalf.converter.IIPImage.RandalfConverterIIPImage;
import mx.randalf.converter.IIPImage.exception.RandalfConverterIIPImageException;
import mx.randalf.interfacException.exception.PubblicaException;
import mx.randalf.mag.MagXsd;
import mx.randalf.tools.Utils;
import mx.randalf.tools.exception.UtilException;
import mx.randalf.xsd.exception.XsdException;

// 21/12/2021 import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RandalfConverterIIPImageBatch {

//	private Logger log = Logger.getLogger(RandalfConverterIIPImageBatch.class);
	private Logger log = LogManager.getLogger(RandalfConverterIIPImageBatch.class);

	private String pathImageMagick = null;

	protected String ext = null;

	protected String level = null;
	
	public RandalfConverterIIPImageBatch(String pathImageMagick, String tipologiaMateriale) {
		this.pathImageMagick = pathImageMagick;

		if (tipologiaMateriale.equals("IIPSRV")){
			ext = "iip";
			level = "6";
		} else {
			ext = "iiif";
			level = "7";
		}
		ProcessStarter.setGlobalSearchPath(pathImageMagick);
	}

	public static void main(String[] args) {
		RandalfConverterIIPImageBatch batch = null;

		if (args.length==3){
			if (args[2].equals("IIPSRV") || args[2].equals("IIIF")){
				batch = new RandalfConverterIIPImageBatch(args[0], args[2]);
				batch.scan(args[1]);
			} else {
				System.err.println("La titppolgia di materiale non e' corretta deve essere IIPSRV oppure IIIF");
			}
		} else {
			System.out.println("E' necessaio indicare i seguenti parametri:");
			System.out.println("1) Path imagemagick");
			System.out.println("2) Path da analizzare");
			System.out.println("2) Tipologia di materiale (IIPSRV/IIIF)");
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
						fIIP = new File(pathname.getAbsolutePath()+"."+ext);
						if (!fIIP.exists()){
							result = true;
						}
					}
				}
				return result;
			}
		});
		Arrays.sort(fl);
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
		File fSolr = null;
		File fElabOK = null;
		File fElabKO = null;
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
					if (!checkUsage(mag.getImg().get(x), level)){
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
										File.separator+ext.toUpperCase()+File.separator+
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
										new String[] {"1",level});
							} else {
								log.error("Il file ["+fImgInput.getAbsolutePath()+"] non esiste");
							}
						}
					}
				}
				System.out.println("Scrivo il file : "+fi.getAbsolutePath());
				fCert = new File(fi.getAbsolutePath()+".cert");
				if (fCert.exists()){
					if (!fCert.delete()){
						throw new RandalfConverterIIPImageException("Problemi nella cancellazione del file ["+fCert.getAbsolutePath()+"]");
					}
				}
//				if (magXsd.write(mag, fi)){
// Argentino 03/01/2022
if (magXsd.write(mag, fi, true)){
					fCert = new File(fi.getAbsolutePath()+".cert");
					if (fCert.exists()){
						fIip = new File(fi.getAbsolutePath()+"."+ext);
						if (Utils.copyFileValidate(fCert.getAbsolutePath(), fIip.getAbsolutePath())){
							fSolr = new File(fi.getAbsolutePath()+".solr");
							if (fSolr.exists()){
								if (fSolr.delete()){
									fElabOK = new File(fi.getAbsolutePath()+".elabOK");
									if (fElabOK.exists()){
										if (!fElabOK.delete()){
											log.error("Problemi nella cancellazione del file ["+fElabOK.getAbsolutePath()+"]");
										}
									}
									fElabKO = new File(fi.getAbsolutePath()+".elabKO");
									if (fElabKO.exists()){
										if (!fElabKO.delete()){
											log.error("Problemi nella cancellazione del file ["+fElabKO.getAbsolutePath()+"]");
										}
									}
								} else {
									log.error("Problemi nella cancellazione del file ["+fSolr.getAbsolutePath()+"]");
								}
							}
						} else {
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
		} 
		catch (PubblicaException e) {
			log.error(e.getMessage(), e);
		} catch (UtilException e) {
			log.error(e.getMessage(), e);
			} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	protected Altimg updateImg(String hrefFile) {
		Altimg altimg = null;
		Link file = null;
		
		altimg = new Altimg();
		file = new Link();
		file.setHref(hrefFile);
		altimg.setFile(file);
		altimg.getUsage().add(level);
		return altimg;
	}

	protected void convert(File fImgInput, File fImgOutput) throws RandalfConverterIIPImageException {
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

	protected Link getFile(Img img, String usage){
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

	protected boolean checkUsage(Img img, String usage){
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
