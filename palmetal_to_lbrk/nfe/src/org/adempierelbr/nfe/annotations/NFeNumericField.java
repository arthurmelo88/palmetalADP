package org.adempierelbr.nfe.annotations;

public @interface NFeNumericField {
	boolean isMandatory() default false;
	int numOcorrencias() default 0;
	int tamanho() default 1;
	boolean tamanhoFixo() default false;
}
