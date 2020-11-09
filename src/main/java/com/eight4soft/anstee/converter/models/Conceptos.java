package com.eight4soft.anstee.converter.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="cfdi:Conceptos")
@XmlAccessorType(XmlAccessType.FIELD)
public class Conceptos {
	@XmlElement(name="cfdi:Concepto")
	@Getter
	@Setter
	private List<Concepto> conceptoList;

}
