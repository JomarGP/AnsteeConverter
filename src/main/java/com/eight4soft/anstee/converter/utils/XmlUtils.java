package com.eight4soft.anstee.converter.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.eight4soft.anstee.converter.models.Concepto;
import com.eight4soft.anstee.converter.models.Conceptos;
import com.eight4soft.anstee.converter.models.Mercancia;
import com.eight4soft.anstee.converter.models.MercanciaOut;
import com.eight4soft.anstee.converter.models.Mercancias;

import lombok.Getter;
import lombok.Setter;

public class XmlUtils {
	
	@Setter
	@Getter
	private static Document documentToUpdate;
	
	public static void addAttributes(String nodeName, Map<String,String> attributes) {
		if(nodeExists(nodeName)) {
			
			Node nodeToUpdate = documentToUpdate.getElementsByTagName(nodeName).item(0);			
			attributes.forEach((key,value) -> ((Element)nodeToUpdate).setAttribute(key, value));
		
		}
	}
	
	private static boolean nodeExists(String nodeName) {
		boolean nodeExists = false;
		if(documentToUpdate.getElementsByTagName(nodeName).getLength() > 0) {
			nodeExists = true;
		}
		return nodeExists;
	}
	
	public static void removeElement(String nodeName) {

		if(nodeExists(nodeName) == true) {
			Node nodeToRemove = documentToUpdate.getElementsByTagName(nodeName).item(0);
			nodeToRemove.getParentNode().removeChild(nodeToRemove);
		}
		
	}
	
	public static void removeUnusedElements(String elementName) {
		if(nodeExists(elementName) && !elementName.equalsIgnoreCase("PAC")) {
			
			NodeList nodosList = documentToUpdate.getElementsByTagName(elementName);	
			for(int i=0; i < nodosList.getLength();i++){
				Node nodeToCheck = nodosList.item(i);
				if(nodeToCheck.getNodeType() == 1) {					
					if(!nodeToCheck.hasChildNodes() && !nodeToCheck.hasAttributes()) {
						//removeElement(nodeToCheck.getNodeName());
						nodeToCheck.getParentNode().removeChild(nodeToCheck);
					}else {
						for(int j = 0;j<nodeToCheck.getChildNodes().getLength();j++) {
							removeUnusedElements(nodeToCheck.getChildNodes().item(j).getNodeName());
						}
					}
				}
			}
		}
	}
	
	public static void updateMercanciasNode() throws JAXBException {
		
		Mercancias mercanciasInput;
		List<Mercancia> mercanciaInList = new ArrayList<>();
		
		Mercancias mercanciasOutput = new Mercancias();
		List<Mercancia> mercanciaOutList = new ArrayList<>();
		
		if(nodeExists("cce11:Mercancias")) {
			Node mercanciasNode = documentToUpdate.getElementsByTagName("cce11:Mercancias").item(0);
			JAXBContext context = JAXBContext.newInstance(Mercancias.class);
			mercanciasInput = (Mercancias)context.createUnmarshaller()
												.unmarshal(mercanciasNode);
			
			mercanciaInList = mercanciasInput.getMercanciaList();
			
			Map<String, Map<String, Mercancia>> result = mercanciaInList.stream()
			        .collect(Collectors.groupingBy(Mercancia::getFraccionarancelaria,
			                Collectors.toMap(Mercancia::getNoIdentificacion,
			                        t -> new Mercancia(t.getNoIdentificacion(),
			                        					t.getFraccionarancelaria(),
			                        					t.getCantidadAduana(),
			                        					t.getUnidadaduana(),
			                        					getValorDolares(t.getNoIdentificacion()),
			                        					getValorUnitarioAduana(t.getNoIdentificacion())),
			                        (a, b) -> new Mercancia(a.getNoIdentificacion(),
			                        						a.getFraccionarancelaria(),
			                        						a.getCantidadAduana() + b.getCantidadAduana(),
			                        						a.getUnidadaduana(),
			                        						getValorDolares(a.getNoIdentificacion()),
			                        						getValorUnitarioAduana(a.getNoIdentificacion())))));
			
			
			mercanciaOutList = 
					result.values().stream()
				    .collect(ArrayList::new, (l,m)->m.values().forEach(l::add), List::addAll);
			
			mercanciaOutList.forEach(x -> {
											x.setFraccionarancelaria(null);
											x.setUnidadaduana(null);
										  });
					
			mercanciasOutput.setMercanciaList(mercanciaOutList);
			
			removeElement("cce11:Mercancias");
			
			Binder<Node> binder = context.createBinder();
			binder.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			binder.marshal(mercanciasOutput, documentToUpdate.getElementsByTagName("cce11:ComercioExterior").item(0));
				    
		}
	}
	
	
	private static Double getValorDolares(String noIdentificacion){
		
		Double totalValorDolares = 0.0;
		
		Conceptos conceptosInput;
		List<Concepto> conceptoInList = new ArrayList<>();
		
		try {
		
			if(nodeExists("cfdi:Conceptos")) {

				Node mercanciasNode = documentToUpdate.getElementsByTagName("cfdi:Conceptos").item(0);
				JAXBContext context = JAXBContext.newInstance(Conceptos.class);
				conceptosInput = (Conceptos)context.createUnmarshaller()
													.unmarshal(mercanciasNode);
				
				conceptoInList = conceptosInput.getConceptoList();
				
				totalValorDolares = conceptoInList
									.stream()
									.filter(con -> con.getNoIdentificacion().equalsIgnoreCase(noIdentificacion))
									.collect(Collectors.summingDouble(Concepto::getImporte));	
				
			}
		}catch(Exception ex) {
			System.out.printf("algun error" + ex);
			totalValorDolares = 0.0;
		}
		
		return totalValorDolares;
	}
	
	private static Double getValorUnitarioAduana(String noIdentificacion){
		
		Double valorUnitarioAduana = 0.0;
		
		Conceptos conceptosInput;
		List<Concepto> conceptoInList = new ArrayList<>();
		
		try {
		
			if(nodeExists("cfdi:Conceptos")) {

				Node mercanciasNode = documentToUpdate.getElementsByTagName("cfdi:Conceptos").item(0);
				JAXBContext context = JAXBContext.newInstance(Conceptos.class);
				conceptosInput = (Conceptos)context.createUnmarshaller()
													.unmarshal(mercanciasNode);
				
				conceptoInList = conceptosInput.getConceptoList();
				
				valorUnitarioAduana = conceptoInList
									.stream()
									.filter(con -> con.getNoIdentificacion().equalsIgnoreCase(noIdentificacion))
									.findFirst()
									.get().getValorUnitario();
				
			}
		}catch(Exception ex) {
			System.out.printf("algun error" + ex);
			valorUnitarioAduana = 0.0;
		}
		
		return valorUnitarioAduana;
	}
	
	
}
