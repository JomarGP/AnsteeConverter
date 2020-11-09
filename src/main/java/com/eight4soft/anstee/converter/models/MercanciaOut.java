package com.eight4soft.anstee.converter.models;

import javax.xml.bind.annotation.XmlAttribute;

public class MercanciaOut {
	private String noIdentificacion;	
	private String fraccionArancelaria;
	
	@XmlAttribute(name="NoIdentificacion")
    public String getNoIdentificacion() {
         return noIdentificacion;
    }

    public void setNoIdentificacion(String name) {
         this.noIdentificacion = name;
    }
	
	@XmlAttribute(name="FraccionArancelaria")
    public String getFraccionArancelaria() {
         return fraccionArancelaria;
    }

    public void setFraccionArancelaria(String name) {
         this.fraccionArancelaria = name;
    }
    
public MercanciaOut(String noIdentificacion,String fraccionarancelaria) {
		
		System.out.println("FraccionArancelariaIn: " + fraccionarancelaria);
		System.out.println("NoIdentificacion: " + noIdentificacion);
	
		this.noIdentificacion = noIdentificacion;
		//this.setFraccionArancelariaOut(fraccionarancelaria);//fraccionArancelariaOut = fraccionarancelaria
		this.setFraccionArancelaria(fraccionarancelaria);

		
//		System.out.println("FraccionArancelariaOut: " + fraccionArancelariaOut);
//		System.out.println("NoIdentificacion: " + noIdentificacion);
//		System.out.println("CantidadAduana: " + cantidadAduana);
//		System.out.println("UnidadAduanaOut: " + unidadAduanaOut);
	}
	
	public MercanciaOut() {}

}
