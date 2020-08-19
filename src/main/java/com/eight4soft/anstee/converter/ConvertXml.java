package com.eight4soft.anstee.converter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.FilenameFilter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
				
				actualizarHora(doc);
				actualizarCfdiRelacionado(doc);
				
				actualizarUUID(doc);
				
				actualizarNodoPago(doc);
				
				addComplementoNamspace(doc);
				
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
		        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
		        
		        Transformer tf = TransformerFactory.newInstance().newTransformer();
		        tf.setOutputProperty(OutputKeys.INDENT, "yes");
		        tf.setOutputProperty(OutputKeys.METHOD, "xml");
		        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        
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
			
			Double dAux = Double.parseDouble(auxComprobanteTotal);
			
			
			Element eComercioExterior = (Element)comercioExteriorNode.item(0);
			eComercioExterior.setAttribute("TipoCambioUSD", auxComprobanteTipoCambio);
			eComercioExterior.setAttribute("TotalUSD",  String.format("%.2f", dAux));
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
					if(eNodoMercancia.getAttribute("NoIdentificacion").equals(noIdentificacion)) {
						eNodoMercancia.setAttribute("ValorDolares", importe);
						eNodoMercancia.setAttribute("ValorUnitarioAduana", valorUnitario);
					}	
				}
			}
		}
	}
	
	public static void actualizarHora(Document doc) {
		NodeList comprobanteNode = doc.getElementsByTagName("cfdi:Comprobante");
		Element eComprobante = (Element)comprobanteNode.item(0);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		eComprobante.setAttribute("Fecha", now.truncatedTo(ChronoUnit.SECONDS).format(dtf));
	}
	
	public static void actualizarCfdiRelacionado(Document doc) {
		NodeList relacionadosNode = doc.getElementsByTagName("cfdi:CfdiRelacionados");
		if(relacionadosNode.getLength() > 0) {
			Element eNodoRelacionados = (Element)relacionadosNode.item(0);
			if(eNodoRelacionados.getAttribute("TipoRelacion").isEmpty()) {
				eNodoRelacionados.setAttribute("TipoRelacion", "01");
			}
		}
	}	
	
	public static void actualizarUUID(Document doc) {
		NodeList relacionadosNode = doc.getElementsByTagName("cfdi:CfdiRelacionados");
		String auxUUID = null;
		
		if(relacionadosNode.getLength() > 0) {
			NodeList eNodoRelacionadoList = relacionadosNode.item(0).getChildNodes();
			for(int i = 0; i < eNodoRelacionadoList.getLength(); i++) {
				if(eNodoRelacionadoList.item(i).getNodeType() == 1) {
					Element eNodoRelacionado = (Element)eNodoRelacionadoList.item(i);
					auxUUID = eNodoRelacionado.getElementsByTagName("UUID").item(0).getChildNodes().item(0).getNodeValue();
					eNodoRelacionado.setAttribute("UUID",auxUUID);
					eNodoRelacionado.removeChild(eNodoRelacionado.getElementsByTagName("UUID").item(0));
						
				}
			}
		}
	}
	
	public static void actualizarNodoPago(Document doc) {
		NodeList pagoNode = doc.getElementsByTagName("pago10:Pago");
		
		if(pagoNode.getLength() > 0) {
			Element eNodoPago = (Element)pagoNode.item(0);
			eNodoPago.removeAttribute("CtaOrdenante");
			eNodoPago.removeAttribute("RfcEmisorCtaBen");
			eNodoPago.removeAttribute("CtaBeneficiario");
			eNodoPago.removeAttribute("RfcEmisorCtaOrd");
			
			NodeList doctoRelacionadoList = pagoNode.item(0).getChildNodes();
			
			for(int i = 0; i < doctoRelacionadoList.getLength(); i++) {
				if(doctoRelacionadoList.item(i).getNodeType() == 1) {
					Element eDoctoRelacionado = (Element)doctoRelacionadoList.item(i);
					eDoctoRelacionado.removeAttribute("TipoCambioDR");	
				}
			}
		}
	}
	
	public static void addComplementoNamspace(Document doc) {
		
		NodeList comercioExteriorNode = doc.getElementsByTagName("cfdi:Complemento");
		if(comercioExteriorNode.getLength() > 0) {
			Element comercioExteriorElement  = (Element)comercioExteriorNode.item(0);
			
			comercioExteriorElement.removeAttribute("xmlns:cce11");
			comercioExteriorElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cce11", "http://www.sat.gob.mx/ComercioExterior11");
		}
	}
}  
