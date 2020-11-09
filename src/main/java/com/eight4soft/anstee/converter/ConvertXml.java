package com.eight4soft.anstee.converter;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.eight4soft.anstee.converter.utils.XmlUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class ConvertXml {
	
	static Document facturaDoc;

	public static void main(String args[]){
		
		
		
		try{  
			File folder = new File("./");
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
			
			if (listOfFiles.length > 0) {
				for(int fileIndex = 0; fileIndex < listOfFiles.length;fileIndex++) {
					fileToConvert = listOfFiles[fileIndex];
					
					//an instance of factory that gives a document builder  
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
					dbf.setIgnoringElementContentWhitespace(true);
					
					//an instance of builder to parse the specified xml file  
					DocumentBuilder db = dbf.newDocumentBuilder();  
					facturaDoc = db.parse(fileToConvert);  			
					facturaDoc.getDocumentElement().normalize(); 
					
					XmlUtils.setDocumentToUpdate(facturaDoc);
					
					
					//Remove PAC element from the Invoice
					//XmlUtils.removeElement("PAC");
					
					//Create attributes to be added into cfdi:Comprobante
					HashMap<String,String> comprobanteAttributes = new HashMap<>();
					
					comprobanteAttributes.put("Certificado", "");
					comprobanteAttributes.put("NoCertificado","");
					comprobanteAttributes.put("Sello","");
					XmlUtils.addAttributes("cfdi:Comprobante",comprobanteAttributes);
									
					XmlUtils.removeUnusedElements("cfdi:Comprobante");
					
					XmlUtils.updateMercanciasNode();
					
					addTotales(facturaDoc);
			
					actualizarHora(facturaDoc);
					actualizarCfdiRelacionado(facturaDoc);
					
					actualizarUUID(facturaDoc);
					
					actualizarNodoPago(facturaDoc);
					
					addComplementoNamspace(facturaDoc);
					
					XPathFactory xfact = XPathFactory.newInstance();
					XPath xpath = xfact.newXPath();
					
					NodeList empty =
						    (NodeList)xpath.evaluate("//text()[normalize-space(.) = '']",
						    		XmlUtils.getDocumentToUpdate(), XPathConstants.NODESET);
					
					for (int i = 0; i < empty.getLength(); i++) {
					    Node node = empty.item(i);
					    node.getParentNode().removeChild(node);
					}
					
					
					Transformer transformer = TransformerFactory.newInstance().newTransformer();
			        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			        
			        
			        DOMSource source = new DOMSource(XmlUtils.getDocumentToUpdate());
			        
		
			        File xmlOficial = new File("Oficial/"+fileToConvert.getName().substring(0,fileToConvert.getName().length()-4) + "_Oficial" + fileToConvert.getName().substring(fileToConvert.getName().length()-4));	        
			        if (xmlOficial.exists()) {
			        	xmlOficial.delete();
			        }
			        StreamResult result = new StreamResult(xmlOficial);
			        transformer.transform(source, result);
				}
	        }else if(listOfFiles.length == 0) {
	        	System.out.println("No hay Archivo XML en el folder");
	        }
			
		}   
		catch (Exception e)   
		{  
			e.printStackTrace();  
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
			
			if(!auxTipoMoneda.equals("MXN")) {
				auxComprobanteTotal = eComprobante.getAttribute("Total");
				auxComprobanteTipoCambio = eComprobante.getAttribute("TipoCambio");
			}
			
			Double dAux = Double.parseDouble(auxComprobanteTotal);
			
			
			Element eComercioExterior = (Element)comercioExteriorNode.item(0);
			eComercioExterior.setAttribute("TipoCambioUSD", auxComprobanteTipoCambio);
			eComercioExterior.setAttribute("TotalUSD",  String.format("%.2f", dAux));
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
