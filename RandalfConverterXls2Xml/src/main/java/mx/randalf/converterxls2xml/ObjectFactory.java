//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.06 at 05:57:43 PM CET 
//


package mx.randalf.converterxls2xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mx.randalf.converterxls2xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Path_QNAME = new QName("http://www.randalf.mx/converterXls2Xml", "path");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mx.randalf.converterxls2xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Dati }
     * 
     */
    public Dati createDati() {
        return new Dati();
    }

    /**
     * Create an instance of {@link Input }
     * 
     */
    public Input createInput() {
        return new Input();
    }

    /**
     * Create an instance of {@link Output }
     * 
     */
    public Output createOutput() {
        return new Output();
    }

    /**
     * Create an instance of {@link Output.Images }
     * 
     */
    public Output.Images createOutputImages() {
        return new Output.Images();
    }

    /**
     * Create an instance of {@link Output.Images.Image }
     * 
     */
    public Output.Images.Image createOutputImagesImage() {
        return new Output.Images.Image();
    }

    /**
     * Create an instance of {@link Output.Xml }
     * 
     */
    public Output.Xml createOutputXml() {
        return new Output.Xml();
    }

    /**
     * Create an instance of {@link Output.Bib }
     * 
     */
    public Output.Bib createOutputBib() {
        return new Output.Bib();
    }

    /**
     * Create an instance of {@link Dati.Colonna }
     * 
     */
    public Dati.Colonna createDatiColonna() {
        return new Dati.Colonna();
    }

    /**
     * Create an instance of {@link Dati.Campo }
     * 
     */
    public Dati.Campo createDatiCampo() {
        return new Dati.Campo();
    }

    /**
     * Create an instance of {@link FormatoImg }
     * 
     */
    public FormatoImg createFormatoImg() {
        return new FormatoImg();
    }

    /**
     * Create an instance of {@link Input.FormatoFile }
     * 
     */
    public Input.FormatoFile createInputFormatoFile() {
        return new Input.FormatoFile();
    }

    /**
     * Create an instance of {@link Input.Image }
     * 
     */
    public Input.Image createInputImage() {
        return new Input.Image();
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

    /**
     * Create an instance of {@link Xls2Xml }
     * 
     */
    public Xls2Xml createXls2Xml() {
        return new Xls2Xml();
    }

    /**
     * Create an instance of {@link Output.Gen }
     * 
     */
    public Output.Gen createOutputGen() {
        return new Output.Gen();
    }

    /**
     * Create an instance of {@link Output.Pagine }
     * 
     */
    public Output.Pagine createOutputPagine() {
        return new Output.Pagine();
    }

    /**
     * Create an instance of {@link Output.Images.Nomenclatura }
     * 
     */
    public Output.Images.Nomenclatura createOutputImagesNomenclatura() {
        return new Output.Images.Nomenclatura();
    }

    /**
     * Create an instance of {@link Output.Images.Image.Converter }
     * 
     */
    public Output.Images.Image.Converter createOutputImagesImageConverter() {
        return new Output.Images.Image.Converter();
    }

    /**
     * Create an instance of {@link Output.Xml.Folder }
     * 
     */
    public Output.Xml.Folder createOutputXmlFolder() {
        return new Output.Xml.Folder();
    }

    /**
     * Create an instance of {@link Output.Xml.FileName }
     * 
     */
    public Output.Xml.FileName createOutputXmlFileName() {
        return new Output.Xml.FileName();
    }

    /**
     * Create an instance of {@link Output.Bib.Key }
     * 
     */
    public Output.Bib.Key createOutputBibKey() {
        return new Output.Bib.Key();
    }

    /**
     * Create an instance of {@link Output.Bib.Campo }
     * 
     */
    public Output.Bib.Campo createOutputBibCampo() {
        return new Output.Bib.Campo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.randalf.mx/converterXls2Xml", name = "path")
    public JAXBElement<String> createPath(String value) {
        return new JAXBElement<String>(_Path_QNAME, String.class, null, value);
    }

}
