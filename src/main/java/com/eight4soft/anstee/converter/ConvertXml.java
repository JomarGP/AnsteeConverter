package com.eight4soft.anstee.converter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FilenameFilter;  

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

public class ConvertXml {

	public static void main(String args[]){
		
		try{  
			File folder = new File("./");
			//File folder = new File("D:\\84Soft\\Proyectos\\Daniel\\ANSTEE\\Test\\Testotro");
			File fileToConvert = null;
			
			File[] listOfFiles = folder.listFiles(new FilenameFilter() {
	             
	            @Override
	            public boolean accept(File dir, String name) {
	                if(name.toLowerCase().endsWith(".xml")){
	                    return true;
	                } else {
	                    return false;
	                }
	            }
	        });
			
			if (listOfFiles.length == 1) {
				fileToConvert = listOfFiles[0];
			
			
				//an instance of factory that gives a document builder  
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
				//an instance of builder to parse the specified xml file  
				DocumentBuilder db = dbf.newDocumentBuilder();  
				Document doc = db.parse(fileToConvert);  			
				doc.getDocumentElement().normalize(); 
				
				Element element = doc.getDocumentElement();
				element = (Element) doc.getElementsByTagName("PAC").item(0);
				removeElement(element);
				
				element = doc.getDocumentElement();
				addAttributes(element);
				removeUnusedElements(element);
				
				updateAttributes(doc);
				
				addTotales(doc);
				
				modificarMercanciaNodo(doc);
				
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
		        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
		        DOMSource source = new DOMSource(doc);
	
		        File xmlOficial = new File(fileToConvert.getName().substring(0,fileToConvert.getName().length()-4) + "_Oficial" + fileToConvert.getName().substring(fileToConvert.getName().length()-4));	        
		        if (xmlOficial.exists()) {
		        	xmlOficial.delete();
		        }
		        StreamResult result = new StreamResult(xmlOficial);
		        transformer.transform(source, result);
	        }else if(listOfFiles.length == 0) {
	        	System.out.println("No hay Archivo XML en el folder");
	        }else {
	        	System.out.println("Hay mas de un Archivo XML en el folder");
	        }
			
		}   
		catch (Exception e)   
		{  
			e.printStackTrace();  
		}  
	} 
	
	public static void addAttributes(Element elementCFDI) {
		
		elementCFDI.setAttribute("Certificado","");
		elementCFDI.setAttribute("NoCertificado","");
		elementCFDI.setAttribute("Sello","");
		
	}
	
	public static void removeElement(Element element) {
		
		element.getParentNode().removeChild(element);
		
	}
	
	public static void removeUnusedElements(Element element) {
		if(!element.hasChildNodes() && !element.hasAttributes()) {
			element.getParentNode().removeChild(element);
		}else {
			NodeList list = element.getChildNodes();			
			for(int i=0; i < list.getLength();i++){
				if(list.item(i).getNodeType() == 1) {
					Element currentElement = (Element)list.item(i);
					removeUnusedElements(currentElement);
				}
			}
		}
	}
	
	public static void updateAttributes(Document doc) {
		String auxFraccionArancelaria = null;
		String auxUnidadAduana= null;
		
		NodeList mercanciasNode = doc.getElementsByTagName("cce11:Mercancias");
		
		if(mercanciasNode.getLength() > 0) {
			NodeList mercanciaNodeList = mercanciasNode.item(0).getChildNodes();
			for(int i=0; i < mercanciaNodeList.getLength();i++){
				if(mercanciaNodeList.item(i).getNodeType() == 1) {
					Element eElement = (Element) mercanciaNodeList.item(i);
					
					auxFraccionArancelaria = eElement.getAttribute("Fraccionarancelaria");
					auxUnidadAduana = eElement.getAttribute("Unidadaduana");
					
					eElement.removeAttribute("Fraccionarancelaria");
					eElement.removeAttribute("Unidadaduana");
					
					eElement.setAttribute("FraccionArancelaria", auxFraccionArancelaria);
					eElement.setAttribute("UnidadAduana", auxUnidadAduana);
				}
			}
		}
	}
	
	public static void addTotales(Document doc) {
		String auxTipoMoneda = null;
		String auxComprobanteTotal= null;
		String auxComprobanteTipoCambio = null;
		
		NodeList comercioExteriorNode = doc.getElementsByTagName("cce11:ComercioExterior");
		
		if(comercioExteriorNode.getLength() > 0) {
		
			NodeList comprobanteNode = doc.getElementsByTagName("cfdi:Comprobante");
			Element eComprobante = (Element)comprobanteNode.item(0);
			auxTipoMoneda = eComprobante.getAttribute("Moneda");
			
			if(auxTipoMoneda.equals("USD")) {
				auxComprobanteTotal = eComprobante.getAttribute("Total");
				auxComprobanteTipoCambio = eComprobante.getAttribute("TipoCambio");
			}
			
			
			Element eComercioExterior = (Element)comercioExteriorNode.item(0);
			eComercioExterior.setAttribute("TipoCambioUSD", auxComprobanteTipoCambio);
			eComercioExterior.setAttribute("TotalUSD", auxComprobanteTotal);
		}
	}
	
	public static void modificarMercanciaNodo(Document doc) {
		String conceptoNoIdentificacion = null;
		String conceptoImporte = null;
		String conceptoValorUnitario = null;
		
		NodeList nodoConceptos = doc.getElementsByTagName("cfdi:Conceptos");
		
		if(nodoConceptos.getLength() > 0) {
			NodeList nodoConceptoList = nodoConceptos.item(0).getChildNodes();
			for(int i = 0; i < nodoConceptoList.getLength(); i++) {
				if(nodoConceptoList.item(i).getNodeType() == 1) {
					
					Element eNodoConcepto = (Element)nodoConceptoList.item(i);
					System.out.println("Id Nodo Concepto" + eNodoConcepto.getAttribute("NoIdentificacion"));
					conceptoNoIdentificacion = eNodoConcepto.getAttribute("NoIdentificacion");
					conceptoImporte = eNodoConcepto.getAttribute("Importe");
					conceptoValorUnitario = eNodoConcepto.getAttribute("ValorUnitario");
					
					agregarValoresMercancia(doc,conceptoNoIdentificacion,conceptoImporte,conceptoValorUnitario);
				
				}
			}
		}
	}
	
	public static void agregarValoresMercancia(Document doc, String noIdentificacion,String importe,String valorUnitario) {
		NodeList nodoMercancias = doc.getElementsByTagName("cce11:Mercancias");
		
		if(nodoMercancias.getLength() > 0) {
			NodeList nodoMercanciaList = nodoMercancias.item(0).getChildNodes();
			for(int i = 0; i < nodoMercanciaList.getLength(); i++) {
				if(nodoMercanciaList.item(i).getNodeType() == 1) {
					Element eNodoMercancia = (Element)nodoMercanciaList.item(i);
					System.out.println(eNodoMercancia.getAttribute("NoIdentificacion") + " es igual a " + noIdentificacion);
					if(eNodoMercancia.getAttribute("NoIdentificacion").equals(noIdentificacion)) {
						eNodoMercancia.setAttribute("ValorDolares", importe);
						eNodoMercancia.setAttribute("ValorUnitarioAduana", valorUnitario);
					}	
				}
			}
		}
	}
}  
