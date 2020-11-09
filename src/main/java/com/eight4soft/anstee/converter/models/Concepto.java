package com.eight4soft.anstee.converter.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
public class Concepto {
	
	@XmlAttribute(name="ClaveProdServ")
	@Getter
	@Setter
	private String claveProdServ;
	
	@XmlAttribute(name="NoIdentificacion")
	@Getter
	@Setter
	private String noIdentificacion;
	
	@XmlAttribute(name="Cantidad")
	@Getter
	@Setter
	private Double cantidad;
	
	@XmlAttribute(name="ClaveUnidad")
	@Getter
	@Setter
	private String claveUnidad;
	
	@XmlAttribute(name="Unidad")
	@Getter
	@Setter
	private String unidad;
	
	@XmlAttribute(name="Descripcion")
	@Getter
	@Setter 
	private String descripcion;
	
	@XmlAttribute(name="ValorUnitario")
	@Getter
	@Setter
	private Double valorUnitario;
	
	@XmlAttribute(name="Importe")
	@Getter
	@Setter
	private Double importe;

}
