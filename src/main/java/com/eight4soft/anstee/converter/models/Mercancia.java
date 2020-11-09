package com.eight4soft.anstee.converter.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.Setter;

//@XmlAccessorType(XmlAccessType.FIELD)

public class Mercancia {
	

	private String noIdentificacion;	
	private String fraccionarancelaria;
	private String fraccionArancelaria;
	private Double cantidadAduana;
	private String unidadaduana;
	private String unidadAduana;
	private Double valorDolares;
	private Double valorUnitarioAduana;
	
	@XmlAttribute(name="NoIdentificacion")
    public String getNoIdentificacion() {
         return noIdentificacion;
    }

    public void setNoIdentificacion(String name) {
         this.noIdentificacion = name;
    }
	
	@XmlAttribute(name="Fraccionarancelaria")
    public String getFraccionarancelaria() {
         return fraccionarancelaria;
    }

    public void setFraccionarancelaria(String name) {
         this.fraccionarancelaria = name;
    }
    
    @XmlAttribute(name="FraccionArancelaria")
    public String getFraccionArancelaria() {
         return fraccionArancelaria;
    }

    public void setFraccionArancelaria(String name) {
         this.fraccionArancelaria = name;
    }
    
    @XmlAttribute(name="CantidadAduana")
    public Double getCantidadAduana() {
		return cantidadAduana;
	}

	public void setCantidadAduana(Double cantidadAduana) {
		this.cantidadAduana = cantidadAduana;
	}

	@XmlAttribute(name="Unidadaduana")
	public String getUnidadaduana() {
		return unidadaduana;
	}

	public void setUnidadaduana(String unidadaduana) {
		this.unidadaduana = unidadaduana;
	}

	@XmlAttribute(name="UnidadAduana")
	public String getUnidadAduana() {
		return unidadAduana;
	}

	public void setUnidadAduana(String unidadAduana) {
		this.unidadAduana = unidadAduana;
	}

	@XmlAttribute(name="ValorDolares")
	public Double getValorDolares() {
		return valorDolares;
	}

	public void setValorDolares(Double valorDolares) {
		this.valorDolares = valorDolares;
	}

	@XmlAttribute(name="ValorUnitarioAduana")
	public Double getValorUnitarioAduana() {
		return valorUnitarioAduana;
	}

	public void setValorUnitarioAduana(Double valorUnitarioAduana) {
		this.valorUnitarioAduana = valorUnitarioAduana;
	}
	
	
	public Mercancia(String noIdentificacion,String fraccionarancelaria,Double cantidadAduana,String unidadaduana,Double valorDolares, Double valorUnitarioAduana) {
		this.noIdentificacion = noIdentificacion;
		this.fraccionarancelaria = fraccionarancelaria;
		this.fraccionArancelaria = fraccionarancelaria;
		this.cantidadAduana = cantidadAduana;
		this.unidadaduana = unidadaduana;
		this.unidadAduana = unidadaduana;
		this.valorDolares = valorDolares;
		this.valorUnitarioAduana = valorUnitarioAduana;
		
	}
	
	public Mercancia() {}

	
}
