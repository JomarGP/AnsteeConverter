package com.eight4soft.anstee.converter.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="cce11:Mercancias")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mercancias {
	
	@XmlElement(name="cce11:Mercancia")
	@Getter
	@Setter
	private List<Mercancia> mercanciaList;

}
